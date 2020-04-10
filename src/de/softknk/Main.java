package de.softknk;

import de.softknk.algorithms.AStar;
import de.softknk.algorithms.Dijkstra;
import de.softknk.algorithms.GreedyBestFirstSearch;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;


public class Main extends Application {

    public static Pane pane;
    public static int SIZE = 40;
    public static final int GRID_SIZE = 720;
    public static Cell[][] cells;
    public static Cell start, target;
    public static Pathfinding current;

    public static boolean selectStart, selectEnd;
    static boolean showsCells = true;

    public static boolean creativeMode = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        pane = new Pane();
        pane.setStyle("-fx-background-color: #BFFF80");
        Scene scene = new Scene(pane, GRID_SIZE + 190 - 10, GRID_SIZE - 10, Cell.getDefaultColor());
        stage.setScene(scene);
        scene.getStylesheets().add(Main.class.getResource("/de/softknk/box.css").toExternalForm());
        scene.getStylesheets().add(Main.class.getResource("/de/softknk/button.css").toExternalForm());

        PrefGUI p = new PrefGUI();

        TextField sizeInput = new TextField();
        sizeInput.getStyleClass().add("text-field");
        sizeInput.setPromptText("Define grid size");
        sizeInput.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.ENTER) {
                if (!sizeInput.getText().equals("")) {
                    reset();
                    SIZE = Integer.parseInt(sizeInput.getText());
                    initGrid();
                    pane.requestFocus();
                }
            }
        });
        sizeInput.setPrefWidth(p.getContainer().getPrefWidth());
        p.getContainer().getChildren().add(sizeInput);

        Button visualize = new Button("VISUALIZE");
        visualize.setPrefSize(125, 40);
        visualize.setOnAction(event -> startVisualization());
        visualize.getStyleClass().add("button");
        visualize.setPrefWidth(p.getContainer().getPrefWidth());
        p.getContainer().getChildren().add(visualize);

  /*      Line seperator = new Line(GRID_SIZE, 0, GRID_SIZE, GRID_SIZE);
        seperator.setStroke(Cell.getObstacleColor());
        seperator.setStrokeWidth(1.5);
        pane.getChildren().add(seperator); */

        initGrid();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            if (start != null)
                start.draw(Color.rgb(252, 186, 3));
            if (target != null)
                target.draw(Color.rgb(94, 199, 255));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        pane.requestFocus();
        pane.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.S)
                startVisualization();
            if (key.getCode() == KeyCode.R)
                reset();
            else if (key.getCode() == KeyCode.C)
                createRandomField();
            else if (key.getCode() == KeyCode.P) {
                if (current != null)
                    current.paused = !current.paused;
            } else if (key.getCode() == KeyCode.F) {
                selectEnd = false;
                selectStart = !selectStart;
            } else if (key.getCode() == KeyCode.E) {
                selectStart = false;
                selectEnd = !selectEnd;
            } else if (key.getCode() == KeyCode.D)
                showCellBorders();
            else if (key.getCode() == KeyCode.Z)
                creativeMode = !creativeMode;
        });

        stage.setResizable(false);
        stage.setTitle("Pathfinding Visualizer");
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("/res/icon.png")));
        stage.show();
    }

    private void startVisualization() {
        if (start != null && target != null) {
            if (current != null)
                current.clean();
            current = getSelectedAlgorithm(start, target);
            if (current != null)
                current.findPath();
        } else {
            //TODO throw Start or End not defined Exception
        }
    }

    private Pathfinding getSelectedAlgorithm(Cell start, Cell target) {
        if (PrefGUI.selected == null) {
            System.out.println("No Algorithm selected.");
            //TODO throw no algorithm selected exception
        } else {
            if (PrefGUI.selected == PrefGUI.Algorithms.ASTAR)
                return new AStar(start, target);
            else if (PrefGUI.selected == PrefGUI.Algorithms.DIJKSTRA)
                return new Dijkstra(start, target);
            else if (PrefGUI.selected == PrefGUI.Algorithms.GREEDY)
                return new GreedyBestFirstSearch(start, target);
        }
        return null;
    }

    private void reset() {
        if (current != null) {
            current.stop();
            current.clean();
        }

        start = null;
        target = null;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Main.cells[i][j].reset();
            }
        }
    }

    private void createRandomField() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j].setObstacle(false);
                if (cells[i][j] != start && cells[i][j] != target) {
                    if (Math.random() > 0.75)
                        cells[i][j].setObstacle(true);
                }
            }
        }
    }

    private void showCellBorders() {
        showsCells = !showsCells;

        final int[] a = {0, 0}; //i, j
        final Timeline[] timeline = {null};
        timeline[0] = new Timeline(new KeyFrame(Duration.millis(1), event -> {
            if (showsCells)
                cells[a[0]][a[1]].setStroke(Color.rgb(180, 180, 180));
            else
                cells[a[0]][a[1]].setStroke(null);

            if (a[0] == cells.length - 1 && a[1] == cells[0].length - 1)
                timeline[0].stop();

            if (a[1] == cells[0].length - 1) {
                a[1] = 0;
                a[0] += 1;
            } else {
                a[1] += 1;
            }
        }));
        timeline[0].setCycleCount(Timeline.INDEFINITE);
        timeline[0].play();
    }

    private void initGrid() {
        if (cells != null) {
            Cell.updateSettings();
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells.length; j++) {
                    pane.getChildren().remove(cells[i][j]);
                }
            }
        }

        cells = new Cell[SIZE][SIZE];

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j] = new Cell(i, j);
                pane.getChildren().add(cells[i][j]);
            }
        }
    }
}