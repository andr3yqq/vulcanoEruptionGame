package com.andr3yqq.vulcanoeruptiongame;

import com.andr3yqq.vulcanoeruptiongame.model.Position;
import com.andr3yqq.vulcanoeruptiongame.model.SimulationConfig;
import com.andr3yqq.vulcanoeruptiongame.model.SimulationOutcome;
import com.andr3yqq.vulcanoeruptiongame.simulation.SimulationEngine;
import com.andr3yqq.vulcanoeruptiongame.simulation.TickReport;
import com.andr3yqq.vulcanoeruptiongame.ui.MapRenderer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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

    @Override
    public void start(Stage stage) {
        mapCanvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        mapCanvas.getStyleClass().add("map-canvas");

        BorderPane root = new BorderPane();
        root.setCenter(new StackPane(mapCanvas));
        root.setRight(buildSidebar());

        Scene scene = new Scene(root, CANVAS_WIDTH + 280, CANVAS_HEIGHT);
        java.net.URL stylesheet = HelloApplication.class.getResource("/styles.css");
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }

        stage.setTitle("Ugnikalnio evakuacija");
        stage.setScene(scene);
        stage.show();

        setupSimulation();
        hookupInteractions();
    }

    private VBox buildSidebar() {
        Label header = new Label("Evakuacijos centras");
        header.getStyleClass().add("sidebar-title");

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

        ToggleButton barricadeButton = new ToggleButton("Barikada");
        ToggleButton openRoadButton = new ToggleButton("Naujas kelias");
        actionToggleGroup = new ToggleGroup();
        barricadeButton.setToggleGroup(actionToggleGroup);
        openRoadButton.setToggleGroup(actionToggleGroup);

        VBox sidebar = new VBox(10,
                header,
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
                resetButton
        );
        sidebar.setPadding(new Insets(16));
        sidebar.setPrefWidth(260);
        return sidebar;
    }

    private void setupSimulation() {
        engine = new SimulationEngine(SimulationConfig.defaultConfig());
        renderer = new MapRenderer(mapCanvas, engine);
        renderer.draw();
        updateSidebarTexts();
        statusLabel.setText("Pasiruošę startui.");
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
    }

    private void hookupInteractions() {
        startButton.setOnAction(e -> {
            timeline.play();
            startButton.setDisable(true);
            pauseButton.setDisable(false);
            statusLabel.setText("Simuliacija vyksta...");
        });
        pauseButton.setOnAction(e -> {
            if (timeline.getStatus() == Timeline.Status.RUNNING) {
                timeline.pause();
                pauseButton.setText("Tęsti");
                statusLabel.setText("Pauzė");
            } else {
                timeline.play();
                pauseButton.setText("Pauzė");
                statusLabel.setText("Simuliacija vyksta...");
            }
        });
        resetButton.setOnAction(e -> {
            pauseButton.setText("Pauzė");
            setupSimulation();
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
            } else {
                statusLabel.setText("Nepavyko: netinkamas langelis arba pasibaigė veiksmai.");
            }
        });
    }

    private void advanceSimulation() {
        TickReport report = engine.tick();
        renderer.draw();
        updateSidebarTexts();
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
        }
    }

    private void updateSidebarTexts() {
        tickLabel.setText("Žingsnis: " + engine.getState().getTick());
        savedLabel.setText("Išgelbėti: " + engine.getState().getSavedCount());
        lostLabel.setText("Žuvo: " + engine.getState().getLostCount());
        barricadeLabel.setText("Barikados: " + engine.getState().getBarricadeActionsLeft());
        openRoadLabel.setText("Nauji keliai: " + engine.getState().getOpenRoadActionsLeft());
    }

    public static void main(String[] args) {
        launch();
    }
}
