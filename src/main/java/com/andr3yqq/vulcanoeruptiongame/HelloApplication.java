package com.andr3yqq.vulcanoeruptiongame;

import com.andr3yqq.vulcanoeruptiongame.model.DifficultyLevel;
import com.andr3yqq.vulcanoeruptiongame.model.Position;
import com.andr3yqq.vulcanoeruptiongame.model.SimulationConfig;
import com.andr3yqq.vulcanoeruptiongame.model.SimulationOutcome;
import com.andr3yqq.vulcanoeruptiongame.simulation.SimulationEngine;
import com.andr3yqq.vulcanoeruptiongame.simulation.TickReport;
import com.andr3yqq.vulcanoeruptiongame.ui.MapRenderer;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * JavaFX front-end: draws the map, runs the simulation clock, and exposes player actions.
 */
public class HelloApplication extends Application {

    private static final double CANVAS_WIDTH = 720;
    private static final double CANVAS_HEIGHT = 520;

    private enum ActionMode { NONE, BARRICADE, OPEN_ROAD }

    private Canvas mapCanvas;
    private SimulationEngine engine;
    private MapRenderer renderer;
    private Timeline timeline;
    private ActionMode actionMode = ActionMode.NONE;

    private Label tickLabel;
    private Label savedLabel;
    private Label lostLabel;
    private Label barricadeLabel;
    private Label openRoadLabel;
    private Label statusLabel;
    private Label actionHintLabel;
    private Button startButton;
    private Button pauseButton;
    private Button resetButton;
    private ToggleGroup actionToggleGroup;
    private ToggleButton barricadeButton;
    private ToggleButton openRoadButton;
    private ComboBox<DifficultyLevel> difficultyCombo;
    private ObservableList<String> eventLogItems;
    private ListView<String> eventLogView;
    private DifficultyLevel currentDifficulty = DifficultyLevel.NORMAL;
    private StackPane mapContainer;
    private Rectangle flashOverlay;
    private FadeTransition flashAnimation;
    private AudioClip lavaClip;
    private AudioClip saveClip;
    private AudioClip deathClip;
    private boolean suppressDifficultyListener;

    @Override
    public void start(Stage stage) {
        mapCanvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        mapCanvas.getStyleClass().add("map-canvas");

        flashOverlay = new Rectangle(CANVAS_WIDTH, CANVAS_HEIGHT);
        flashOverlay.setFill(Color.TRANSPARENT);
        flashOverlay.setOpacity(0);
        flashOverlay.setMouseTransparent(true);

        mapContainer = new StackPane(mapCanvas, flashOverlay);
        flashOverlay.widthProperty().bind(mapContainer.widthProperty());
        flashOverlay.heightProperty().bind(mapContainer.heightProperty());

        BorderPane root = new BorderPane();
        root.setCenter(mapContainer);
        root.setRight(buildSidebar());

        Scene scene = new Scene(root, CANVAS_WIDTH + 280, CANVAS_HEIGHT);
        java.net.URL stylesheet = HelloApplication.class.getResource("/styles.css");
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }

        stage.setTitle("Ugnikalnio evakuacija");
        stage.setScene(scene);
        stage.show();

        loadAudioClips();
        setupSimulation();
        hookupInteractions();
    }

    private VBox buildSidebar() {
        Label header = new Label("Evakuacijos centras");
        header.getStyleClass().add("sidebar-title");

        Label difficultyLabel = new Label("Sunkumas");
        difficultyCombo = new ComboBox<>(FXCollections.observableArrayList(DifficultyLevel.values()));
        suppressDifficultyListener = true;
        difficultyCombo.setValue(currentDifficulty);
        suppressDifficultyListener = false;
        difficultyCombo.valueProperty().addListener((obs, oldVal, newVal) -> onDifficultyChanged(newVal));

        tickLabel = new Label();
        savedLabel = new Label();
        lostLabel = new Label();
        barricadeLabel = new Label();
        openRoadLabel = new Label();
        statusLabel = new Label();
        statusLabel.getStyleClass().add("status-label");
        actionHintLabel = new Label("Pasirinkite veiksmą ir spauskite ant langelio.");
        actionHintLabel.setWrapText(true);

        startButton = new Button("Pradėti");
        pauseButton = new Button("Pauzė");
        pauseButton.setDisable(true);
        resetButton = new Button("Perkrauti");

        barricadeButton = new ToggleButton("Barikada");
        openRoadButton = new ToggleButton("Naujas kelias");
        actionToggleGroup = new ToggleGroup();
        barricadeButton.setToggleGroup(actionToggleGroup);
        openRoadButton.setToggleGroup(actionToggleGroup);

        eventLogItems = FXCollections.observableArrayList();
        eventLogView = new ListView<>(eventLogItems);
        eventLogView.setPrefHeight(180);
        eventLogView.setPlaceholder(new Label("Kol kas jokių įvykių."));
        eventLogView.setFocusTraversable(false);
        eventLogView.getStyleClass().add("event-log");

        VBox sidebar = new VBox(10,
                header,
                new Separator(),
                difficultyLabel,
                difficultyCombo,
                new Separator(),
                tickLabel,
                savedLabel,
                lostLabel,
                barricadeLabel,
                openRoadLabel,
                statusLabel,
                new Separator(),
                new Label("Veiksmai"),
                barricadeButton,
                openRoadButton,
                actionHintLabel,
                new Separator(),
                startButton,
                pauseButton,
                resetButton,
                new Separator(),
                new Label("Įvykių žurnalas"),
                eventLogView
        );
        sidebar.setPadding(new Insets(16));
        sidebar.setPrefWidth(260);
        sidebar.getStyleClass().add("sidebar");
        return sidebar;
    }

    private void setupSimulation() {
        DifficultyLevel selected = (difficultyCombo != null && difficultyCombo.getValue() != null)
                ? difficultyCombo.getValue()
                : DifficultyLevel.NORMAL;
        currentDifficulty = selected;
        SimulationConfig config = SimulationConfig.forDifficulty(selected);
        engine = new SimulationEngine(config);
        renderer = new MapRenderer(mapCanvas, engine);
        renderer.draw();
        if (eventLogItems != null) {
            eventLogItems.clear();
        }
        updateSidebarTexts();
        updateActionButtons();
        statusLabel.setText("Pasiruošę startui (" + selected.getDisplayName() + ").");
        logEvent("Sukurta nauja simuliacija (" + selected.getDisplayName() + "). Lava startuoja taške "
                + engine.getState().getConfig().getMap().getVolcanoSource());
        actionMode = ActionMode.NONE;
        if (actionToggleGroup != null) {
            actionToggleGroup.selectToggle(null);
        }
        if (timeline != null) {
            timeline.stop();
        }
        timeline = new Timeline(new KeyFrame(Duration.millis(600), e -> advanceSimulation()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        startButton.setDisable(false);
        pauseButton.setDisable(true);
        pauseButton.setText("Pauzė");
        setDifficultyControlEnabled(true);
    }

    private void onDifficultyChanged(DifficultyLevel newValue) {
        if (suppressDifficultyListener || newValue == null || newValue == currentDifficulty) {
            return;
        }
        if (timeline != null && timeline.getStatus() == Timeline.Status.RUNNING) {
            statusLabel.setText("Sustabdykite simuliaciją prieš keičiant sunkumą.");
            suppressDifficultyListener = true;
            difficultyCombo.setValue(currentDifficulty);
            suppressDifficultyListener = false;
            return;
        }
        setupSimulation();
    }

    private void hookupInteractions() {
        startButton.setOnAction(e -> {
            timeline.play();
            startButton.setDisable(true);
            pauseButton.setDisable(false);
            statusLabel.setText("Simuliacija vyksta...");
            setDifficultyControlEnabled(false);
            logEvent("Simuliacija paleista.");
        });
        pauseButton.setOnAction(e -> {
            if (timeline.getStatus() == Timeline.Status.RUNNING) {
                timeline.pause();
                pauseButton.setText("Tęsti");
                statusLabel.setText("Pauzė");
                logEvent("Simuliacija pristabdyta.");
            } else {
                timeline.play();
                pauseButton.setText("Pauzė");
                statusLabel.setText("Simuliacija vyksta...");
                logEvent("Simuliacija tęsiama.");
            }
        });
        resetButton.setOnAction(e -> {
            pauseButton.setText("Pauzė");
            setupSimulation();
            logEvent("Simuliacija perkrauta.");
        });

        actionToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == null) {
                actionMode = ActionMode.NONE;
                actionHintLabel.setText("Pasirinkite veiksmą ir spauskite ant langelio.");
            } else if (newToggle instanceof ToggleButton button) {
                if ("Barikada".equals(button.getText())) {
                    actionMode = ActionMode.BARRICADE;
                    actionHintLabel.setText("Pasirinkta barikada. Užblokuokite kelią lavos kryptimi.");
                } else {
                    actionMode = ActionMode.OPEN_ROAD;
                    actionHintLabel.setText("Pasirinkta kelio atidarymas. Kurti alternatyvą gyventojams.");
                }
            }
        });

        mapCanvas.setOnMouseClicked(event -> {
            if (actionMode == ActionMode.NONE) {
                statusLabel.setText("Pirma pasirinkite veiksmą.");
                return;
            }
            Position target = renderer.pickCell(event.getX(), event.getY());
            boolean success = switch (actionMode) {
                case BARRICADE -> engine.buildBarricade(target);
                case OPEN_ROAD -> engine.openRoad(target);
                case NONE -> false;
            };
            if (success) {
                statusLabel.setText("Veiksmas pritaikytas langeliui " + target);
                renderer.draw();
                updateSidebarTexts();
                String verb = actionMode == ActionMode.BARRICADE ? "Barikada pastatyta" : "Kelias atvertas";
                logEvent(verb + " ties " + target);
            } else {
                statusLabel.setText("Nepavyko: netinkamas langelis arba pasibaigė veiksmai.");
                logEvent("Veiksmas nepavyko ties " + target);
            }
        });
    }

    private void advanceSimulation() {
        TickReport report = engine.tick();
        renderer.draw();
        updateSidebarTexts();
        boolean lavaSpread = !report.getNewLavaTiles().isEmpty();
        if (lavaSpread) {
            logEvent("Lava užėmė " + report.getNewLavaTiles().size() + " lang.");
            flashOverlay(Color.ORANGERED);
            playClip(lavaClip);
        }
        boolean anySaved = !report.getSavedCitizens().isEmpty();
        report.getSavedCitizens().forEach(id -> logEvent("Pilietis #" + id + " pasiekė saugią zoną."));
        if (anySaved) {
            flashOverlay(Color.LIGHTGREEN);
            playClip(saveClip);
        }
        boolean anyLost = !report.getLostCitizens().isEmpty();
        report.getLostCitizens().forEach(id -> logEvent("Pilietis #" + id + " žuvo."));
        if (anyLost) {
            flashOverlay(Color.CRIMSON);
            playClip(deathClip);
        }
        if (report.getOutcome() != SimulationOutcome.RUNNING) {
            timeline.stop();
            startButton.setDisable(true);
            pauseButton.setDisable(true);
            pauseButton.setText("Pauzė");
            statusLabel.setText(switch (report.getOutcome()) {
                case VICTORY -> "Pergalė! Išgelbėti gyventojai: " + engine.getState().getSavedCount();
                case FAILURE -> "Pralaimėjimas. Lava buvo greitesnė.";
                case RUNNING -> "";
            });
            barricadeButton.setDisable(true);
            openRoadButton.setDisable(true);
            logEvent(report.getOutcome() == SimulationOutcome.VICTORY
                    ? "Pergalė! " + engine.getState().getSavedCount() + " gyventojai išgelbėti."
                    : "Pralaimėjimas. Visi gyventojai žuvo arba lava pasiekė saugias zonas.");
            setDifficultyControlEnabled(true);
        }
    }

    private void updateSidebarTexts() {
        tickLabel.setText("Žingsnis: " + engine.getState().getTick());
        savedLabel.setText("Išgelbėti: " + engine.getState().getSavedCount());
        lostLabel.setText("Žuvo: " + engine.getState().getLostCount());
        barricadeLabel.setText("Barikados: " + engine.getState().getBarricadeActionsLeft());
        openRoadLabel.setText("Nauji keliai: " + engine.getState().getOpenRoadActionsLeft());
        updateActionButtons();
    }

    private void updateActionButtons() {
        if (engine == null || barricadeButton == null || openRoadButton == null) {
            return;
        }
        boolean hasBarricades = engine.getState().getBarricadeActionsLeft() > 0;
        boolean hasRoads = engine.getState().getOpenRoadActionsLeft() > 0;
        barricadeButton.setDisable(!hasBarricades);
        openRoadButton.setDisable(!hasRoads);
        if ((!hasBarricades && actionMode == ActionMode.BARRICADE) ||
                (!hasRoads && actionMode == ActionMode.OPEN_ROAD)) {
            actionToggleGroup.selectToggle(null);
            actionMode = ActionMode.NONE;
            actionHintLabel.setText("Pasirinkite veiksmą ir spauskite ant langelio.");
        }
    }

    private void setDifficultyControlEnabled(boolean enabled) {
        if (difficultyCombo != null) {
            difficultyCombo.setDisable(!enabled);
        }
    }

    private void logEvent(String message) {
        if (eventLogItems == null) {
            return;
        }
        int tick = engine != null ? engine.getState().getTick() : 0;
        eventLogItems.add(0, "T" + tick + ": " + message);
        while (eventLogItems.size() > 120) {
            eventLogItems.remove(eventLogItems.size() - 1);
        }
    }

    private void loadAudioClips() {
        lavaClip = loadClip("/sounds/lava.wav");
        saveClip = loadClip("/sounds/save.wav");
        deathClip = loadClip("/sounds/death.wav");
    }

    private AudioClip loadClip(String resourcePath) {
        java.net.URL url = HelloApplication.class.getResource(resourcePath);
        if (url == null) {
            System.err.println("Nerastas garsas: " + resourcePath);
            return null;
        }
        try {
            return new AudioClip(url.toExternalForm());
        } catch (Exception ex) {
            System.err.println("Nepavyko įkelti garso " + resourcePath + ": " + ex.getMessage());
            return null;
        }
    }

    private void playClip(AudioClip clip) {
        if (clip != null) {
            clip.play();
        }
    }

    private void flashOverlay(Color color) {
        if (flashOverlay == null) {
            return;
        }
        if (flashAnimation != null) {
            flashAnimation.stop();
        }
        flashOverlay.setFill(new Color(color.getRed(), color.getGreen(), color.getBlue(), 0.45));
        flashAnimation = new FadeTransition(Duration.millis(450), flashOverlay);
        flashAnimation.setFromValue(0.45);
        flashAnimation.setToValue(0);
        flashAnimation.play();
    }

    public static void main(String[] args) {
        launch();
    }
}
