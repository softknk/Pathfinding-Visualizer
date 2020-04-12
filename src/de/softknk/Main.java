package de.softknk;

import de.softknk.algorithms.*;
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

import static de.softknk.VisualizationNotPossibleException.Problem;

public class Main extends Application {

    public static final int GRID_SIZE = 720;

    public static Pane pane;
    public static int SIZE = 40;
    public static Cell[][] grid;
    public static Cell start, target;
    public static Pathfinding current;

    public static boolean selectStart, selectEnd;
    public static boolean showsCells = true;
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
        scene.getStylesheets().add(Main.class.getResource("/de/softknk/style/box.css").toExternalForm());
        scene.getStylesheets().add(Main.class.getResource("/de/softknk/style/button.css").toExternalForm());

        PrefGUI p = new PrefGUI();

        TextField sizeInput = new TextField();
        sizeInput.getStyleClass().add("text-field");
        sizeInput.setPromptText("Define grid size" + " (" + SIZE + ")");
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
        visualize.setOnAction(event -> {
            try {
                startVisualization();
            } catch (VisualizationNotPossibleException e) {
                e.printStackTrace();
            }
        });
        visualize.getStyleClass().add("button");
        visualize.setPrefWidth(p.getContainer().getPrefWidth());
        p.getContainer().getChildren().add(visualize);

        initGrid();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            if (start != null)
                start.draw(Color.rgb(252, 177, 3));
            if (target != null)
                target.draw(Color.rgb(99, 149, 255));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        pane.requestFocus();
        pane.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.S) {
                try {
                    startVisualization();
                } catch (VisualizationNotPossibleException e) {
                    e.printStackTrace();
                }
            } else if (key.getCode() == KeyCode.R)
                reset();
            else if (key.getCode() == KeyCode.C)
                createRandomObstacles();
            else if (key.getCode() == KeyCode.P) {
                if (current != null)
                    current.setPaused(!current.getPaused());
            } else if (key.getCode() == KeyCode.F) {
                selectEnd = false;
                selectStart = !selectStart;
            } else if (key.getCode() == KeyCode.E) {
                selectStart = false;
                selectEnd = !selectEnd;
            } else if (key.getCode() == KeyCode.D)
                showOrHideCellBorders();
            else if (key.getCode() == KeyCode.Z)
                creativeMode = !creativeMode;
        });

        stage.setResizable(false);
        stage.setTitle("Pathfinding Visualizer");
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("/res/icon.png")));
        stage.show();
    }

    private void startVisualization() throws VisualizationNotPossibleException {
        if (start != null && target != null) {
            if (current != null)
                current.clean();
            current = getSelectedAlgorithm();
            if (current != null)
                current.findPath();
        } else {
            throw new VisualizationNotPossibleException(Problem.START_OR_TARGET);
        }
    }

    private Pathfinding getSelectedAlgorithm() throws VisualizationNotPossibleException {
        if (PrefGUI.selected == null) {
            throw new VisualizationNotPossibleException(Problem.ALGORITHM_NOT_SELECTED);
        } else {
            if (PrefGUI.selected == Algorithms.ASTAR)
                return new AStar(start, target);
            else if (PrefGUI.selected == Algorithms.DIJKSTRA)
                return new Dijkstra(start, target);
            else if (PrefGUI.selected == Algorithms.GREEDY)
                return new GreedyBestFirstSearch(start, target);
            else
                return null;
        }
    }

    private void reset() {
        if (current != null) {
            current.stop();
            current.clean();
        }

        start = null;
        target = null;

        GridOperation.grid_operation((i, j) -> grid[i][j].reset());
    }

    private void createRandomObstacles() {
        if (current != null)
            current.clean();

        GridOperation.grid_operation((i, j) -> {
            grid[i][j].setObstacle(false);
            if (grid[i][j] != start && grid[i][j] != target) {
                if (Math.random() > 0.75)
                    grid[i][j].setObstacle(true);
            }
        });
    }

    private void showOrHideCellBorders() {
        showsCells = !showsCells;

        final int[] a = {0, 0}; //i, j
        final Timeline[] timeline = {null};
        timeline[0] = new Timeline(new KeyFrame(Duration.millis(1), event -> {
            if (showsCells)
                grid[a[0]][a[1]].setStroke(Color.rgb(180, 180, 180));
            else
                grid[a[0]][a[1]].setStroke(null);

            if (a[0] == grid.length - 1 && a[1] == grid[0].length - 1)
                timeline[0].stop();

            if (a[1] == grid[0].length - 1) {
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
        if (grid != null) {
            Cell.updateSizeSettings();
            GridOperation.grid_operation(((i, j) -> pane.getChildren().remove(grid[i][j])));
        }

        grid = new Cell[SIZE][SIZE];

        GridOperation.grid_operation((i, j) -> {
            grid[i][j] = new Cell(i, j);
            pane.getChildren().add(grid[i][j]);
        });
    }
}