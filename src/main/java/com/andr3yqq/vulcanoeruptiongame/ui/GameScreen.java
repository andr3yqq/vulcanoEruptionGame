package com.andr3yqq.vulcanoeruptiongame.ui;

import com.andr3yqq.vulcanoeruptiongame.controller.GameController;
import com.andr3yqq.vulcanoeruptiongame.model.DifficultyLevel;
import com.andr3yqq.vulcanoeruptiongame.model.Position;
import com.andr3yqq.vulcanoeruptiongame.model.SimulationOutcome;
import com.andr3yqq.vulcanoeruptiongame.simulation.SimulationEngine;
import com.andr3yqq.vulcanoeruptiongame.simulation.TickReport;
import com.andr3yqq.vulcanoeruptiongame.simulation.SimulationStateView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import lombok.Getter;

public class GameScreen {
    private static final double TICK_MS = 600;
    @Getter
    private final BorderPane root;
    private final Canvas mapCanvas;
    private final StackPane mapContainer;
    private final Rectangle flashOverlay;
    private final MapRenderer renderer;
    private final GameController controller = new GameController();
    private final Timeline timeline;

    private SimulationEngine engine;

    // UI controls
    private final Label tickLabel = new Label();
    private final Label savedLabel = new Label();
    private final Label lostLabel = new Label();
    private final Label barricadeLabel = new Label();
    private final Label openRoadLabel = new Label();
    private final Label statusLabel = new Label();
    private final Label actionHintLabel = new Label("Pasirinkite veiksmą ir spauskite ant langelio.");
    private final ToggleButton barricadeButton = new ToggleButton("Barikada");
    private final ToggleButton openRoadButton = new ToggleButton("Naujas kelias");
    private final Button startButton = new Button("Pradėti");
    private final Button pauseButton = new Button("Pauzė");
    private final Button resetButton = new Button("Perkrauti");
    private final ToggleGroup actionToggleGroup = new ToggleGroup();
    private final ComboBox<DifficultyLevel> difficultyCombo = new ComboBox<>(FXCollections.observableArrayList(DifficultyLevel.values()));
    private final ObservableList<String> eventLogItems = FXCollections.observableArrayList();
    private final ListView<String> eventLogView = new ListView<>(eventLogItems);

    private final FeedbackService feedback;

    private enum ActionMode { NONE, BARRICADE, OPEN_ROAD }
    private ActionMode actionMode = ActionMode.NONE;

    public GameScreen(double canvasWidth, double canvasHeight, double sidebarWidth) {
        mapCanvas = new Canvas(canvasWidth, canvasHeight);
        flashOverlay = new Rectangle(canvasWidth, canvasHeight);
        flashOverlay.setMouseTransparent(true);
        flashOverlay.setOpacity(0);
        mapContainer = new StackPane(mapCanvas, flashOverlay);
        renderer = new MapRenderer(mapCanvas, null);

        root = new BorderPane();
        root.setCenter(mapContainer);
        root.setRight(buildSidebar(sidebarWidth));

        feedback = new FeedbackService(flashOverlay, loadClip("/sounds/lava.wav"), loadClip("/sounds/save.wav"), loadClip("/sounds/death.wav"), this::logEvent);
        timeline = new Timeline(new KeyFrame(Duration.millis(TICK_MS), e -> onTick()));
        timeline.setCycleCount(Timeline.INDEFINITE);

        initControllerCallbacks();
        setupSimulation();
        hookInteractions();
    }

    private void initControllerCallbacks() {
        controller.setTickConsumer(this::handleTick);
        controller.setStateConsumer(this::handleState);
    }

    private VBox buildSidebar(double width) {
        difficultyCombo.setValue(DifficultyLevel.NORMAL);
        difficultyCombo.valueProperty().addListener((obs, oldVal, newVal) -> onDifficultyChanged(newVal));
        statusLabel.getStyleClass().add("status-label");
        actionHintLabel.setWrapText(true);
        pauseButton.setDisable(true);
        eventLogView.setPrefHeight(160);
        eventLogView.setPlaceholder(new Label("Kol kas jokių įvykių."));

        barricadeButton.setToggleGroup(actionToggleGroup);
        openRoadButton.setToggleGroup(actionToggleGroup);

        VBox sidebar = new VBox(10,
                label("Evakuacijos centras", "sidebar-title"), new Separator(),
                label("Sunkumas", null), difficultyCombo, new Separator(),
                tickLabel, savedLabel, lostLabel, barricadeLabel, openRoadLabel, statusLabel,
                new Separator(), label("Veiksmai", null),
                barricadeButton, openRoadButton, actionHintLabel,
                new Separator(), startButton, pauseButton, resetButton,
                new Separator(), label("Įvykių žurnalas", null), eventLogView
        );
        sidebar.setPadding(new Insets(16));
        sidebar.setPrefWidth(width);
        sidebar.getStyleClass().add("sidebar");
        return sidebar;
    }

    private Label label(String text, String style) {
        Label l = new Label(text);
        if (style != null) l.getStyleClass().add(style);
        return l;
    }

    private void setupSimulation() {
        controller.setDifficulty(difficultyCombo.getValue());
        engine = controller.newSimulation();
        renderer.setEngine(engine);
        renderer.draw();
        eventLogItems.clear();
        updateSidebarTexts();
        statusLabel.setText("Pasiruošę startui (" + difficultyCombo.getValue().getDisplayName() + ").");
        logEvent(seedMessage());
        actionToggleGroup.selectToggle(null);
        timeline.stop();
        startButton.setDisable(false);
        pauseButton.setDisable(true);
        pauseButton.setText("Pauzė");
        enableDifficulty(true);
    }

    private String seedMessage() {
        if (difficultyCombo.getValue().isProcedural()) {
            return "Procedūrinis seed=" + Long.toHexString(controller.getLastProceduralSeed());
        }
        return "Naudojamas paruoštas žemėlapis";
    }

    private void hookInteractions() {
        startButton.setOnAction(e -> {
            timeline.play();
            startButton.setDisable(true);
            pauseButton.setDisable(false);
            statusLabel.setText("Simuliacija vyksta...");
            enableDifficulty(false);
            logEvent("Simuliacija paleista.");
        });
        pauseButton.setOnAction(e -> togglePause());
        resetButton.setOnAction(e -> { pauseButton.setText("Pauzė"); setupSimulation(); logEvent("Simuliacija perkrauta."); });

        actionToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == null) {
                actionMode = ActionMode.NONE;
                actionHintLabel.setText("Pasirinkite veiksmą ir spauskite ant langelio.");
            } else {
                actionMode = newToggle == barricadeButton ? ActionMode.BARRICADE : ActionMode.OPEN_ROAD;
                actionHintLabel.setText(actionMode == ActionMode.BARRICADE
                        ? "Pasirinkta barikada. Užblokuokite kelią lavos kryptimi." : "Pasirinkta kelio atidarymas. Kurti alternatyvą gyventojams.");
            }
        });

        mapCanvas.setOnMouseClicked(event -> {
            if (actionMode == ActionMode.NONE) {
                statusLabel.setText("Pirma pasirinkite veiksmą.");
                return;
            }
            Position target = renderer.pickCell(event.getX(), event.getY());
            boolean success = actionMode == ActionMode.BARRICADE ? controller.barricade(target) : controller.openRoad(target);
            if (success) {
                statusLabel.setText("Veiksmas pritaikytas " + target);
                renderer.draw();
                updateSidebarTexts();
                logEvent((actionMode == ActionMode.BARRICADE ? "Barikada" : "Kelias") + " ties " + target);
            } else {
                statusLabel.setText("Nepavyko: netinkamas langelis arba pasibaigė veiksmai.");
            }
        });
    }

    private void onDifficultyChanged(DifficultyLevel newVal) {
        if (timeline.getStatus() == Timeline.Status.RUNNING) {
            statusLabel.setText("Sustabdykite simuliaciją prieš keisdami sunkumą.");
            return;
        }
        setupSimulation();
        logEvent("Sunkumas pakeistas į " + (newVal != null ? newVal.getDisplayName() : ""));
    }

    private void togglePause() {
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
    }

    private void onTick() {
        TickReport report = engine.tick();
        renderer.draw();
        handleTick(report);
    }

    private void handleTick(TickReport report) {
        updateSidebarTexts();
        if (!report.getNewLavaTiles().isEmpty()) {
            feedback.flash(Color.ORANGERED);
            feedback.playLava();
            logEvent("Lava užėmė " + report.getNewLavaTiles().size() + " lang.");
        }
        if (!report.getSavedCitizens().isEmpty()) {
            feedback.flash(Color.LIGHTGREEN);
            feedback.playSave();
            report.getSavedCitizens().forEach(id -> logEvent("Pilietis #" + id + " pasiekė saugią zoną."));
        }
        if (!report.getLostCitizens().isEmpty()) {
            feedback.flash(Color.CRIMSON);
            feedback.playDeath();
            report.getLostCitizens().forEach(id -> logEvent("Pilietis #" + id + " žuvo."));
        }
        if (report.getOutcome() != SimulationOutcome.RUNNING) {
            timeline.stop();
            startButton.setDisable(true);
            pauseButton.setDisable(true);
            pauseButton.setText("Pauzė");
            statusLabel.setText(report.getOutcome() == SimulationOutcome.VICTORY
                    ? "Pergalė! Išgelbėti: " + engine.getState().getSavedCount()
                    : "Pralaimėjimas. Lava buvo greitesnė.");
            barricadeButton.setDisable(true);
            openRoadButton.setDisable(true);
            logEvent(report.getOutcome() == SimulationOutcome.VICTORY
                    ? "Pergalė! " + engine.getState().getSavedCount() + " gyventojai išgelbėti."
                    : "Pralaimėjimas. Visi gyventojai žuvo arba lava pasiekė saugias zonas.");
            enableDifficulty(true);
        }
    }

    private void handleState(SimulationStateView state) {
        tickLabel.setText("Žingsnis: " + state.getTick());
        savedLabel.setText("Išgelbėti: " + state.getSavedCount());
        lostLabel.setText("Žuvo: " + state.getLostCount());
        barricadeLabel.setText("Barikados: " + state.getBarricadeActionsLeft());
        openRoadLabel.setText("Nauji keliai: " + state.getOpenRoadActionsLeft());
    }

    private void updateSidebarTexts() {
        handleState(new SimulationStateView(engine.getState()));
        boolean hasBarricades = engine.getState().getBarricadeActionsLeft() > 0;
        boolean hasRoads = engine.getState().getOpenRoadActionsLeft() > 0;
        barricadeButton.setDisable(!hasBarricades);
        openRoadButton.setDisable(!hasRoads);
        if ((!hasBarricades && actionMode == ActionMode.BARRICADE) || (!hasRoads && actionMode == ActionMode.OPEN_ROAD)) {
            actionToggleGroup.selectToggle(null);
            actionMode = ActionMode.NONE;
            actionHintLabel.setText("Pasirinkite veiksmą ir spauskite ant langelio.");
        }
    }

    private void logEvent(String message) {
        eventLogItems.addFirst("T" + (engine != null ? engine.getState().getTick() : 0) + ": " + message);
        while (eventLogItems.size() > FeedbackService.maxLogItems()) {
            eventLogItems.removeLast();
        }
    }

    private void enableDifficulty(boolean enabled) {
        difficultyCombo.setDisable(!enabled);
    }

    private AudioClip loadClip(String path) {
        var url = getClass().getResource(path);
        return url == null ? null : new AudioClip(url.toExternalForm());
    }
}
