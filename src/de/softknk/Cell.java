package de.softknk;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class Cell extends Circle {

    static double rectangle_size = Main.GRID_SIZE / (double) Main.SIZE;
    static double radius_factor = 2.3;
    public static double RADIUS = rectangle_size / radius_factor;
    public static double place = (rectangle_size - RADIUS * 2) / 2;

    private static Color defaultColor, obstacleColor;

    private int row, column;
    private int g;
    private Cell prev;
    private boolean obstacle;

    private Timeline animation;
    public boolean animationRunning = false;

    static {
        defaultColor = Color.rgb(191, 255, 128);
        obstacleColor = Color.rgb(180, 180, 180);
    }

    public Cell(int row, int column) {
        super(column * rectangle_size + place + RADIUS,row * rectangle_size + place + RADIUS, RADIUS, Color.BLACK);
        this.row = row;
        this.column = column;

        setId("cell");

        draw(getDefaultColor());
        setStroke(obstacleColor);

        setOnMousePressed(event -> {
            Main.pane.requestFocus();
            changeObstacle();
            if (Main.selectStart) {
                if (Main.start != null)
                    Main.start.setObstacle(false);
                Main.start = this;
                Main.selectStart = false;
            } else if (Main.selectEnd) {
                if (Main.target != null)
                    Main.target.setObstacle(false);
                Main.target = this;
                Main.selectEnd = false;
            }
        });

        setOnMouseEntered(event -> {
            if (Main.creativeMode) {
                setObstacle(!obstacle);
            }
        });
    }

    public void draw(Paint color) {
        setFill(color);
    }

    public void animate(Paint first, Paint finalColor) {
        if (!animationRunning) {
            draw(first);
            setRadius(RADIUS / 4);
            animation = new Timeline(new KeyFrame(Duration.millis(35), event -> {
                if (!Main.current.isPaused()) {
                    if (getRadius() <= RADIUS)
                        setRadius(getRadius() + 0.2);
                    else {
                        draw(finalColor);
                        animation.stop();
                    }
                }
            }));
            animation.setCycleCount(Timeline.INDEFINITE);
            animation.play();
            animationRunning = true;
        }
    }

    public static void updateSettings() {
        rectangle_size = Main.GRID_SIZE / (double) Main.SIZE;
        RADIUS = rectangle_size / radius_factor;
    }

    public void reset() {
        if (obstacle)
            setObstacle(false);
        prev = null;
    }

    public int costs() {
        return Main.current.costs(this);
    }

    public void setObstacle(boolean obstacle) {
        this.obstacle = obstacle;
        setObstacleColor();
    }

    public void changeObstacle() {
        obstacle = !obstacle;
        setObstacleColor();
    }

    private void setObstacleColor() {
        if (obstacle)
            draw(obstacleColor);
        else
            draw(defaultColor);
    }

    /*
    setter and getter
     */

    public void setDistance(int distance) {
        g = distance;
    }

    public int getDistance() {
        return g;
    }

    public void setPrevious(Cell prev) {
        this.prev = prev;
    }

    public Cell getPrev() {
        return prev;
    }

    public boolean isObstacle() {
        return obstacle;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public static Color getDefaultColor() {
        return defaultColor;
    }

    public static Color getObstacleColor() {
        return obstacleColor;
    }

    public int getG() {
        return g;
    }
}
