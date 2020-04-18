package de.softknk;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class Cell extends Circle {

    static double area = Main.GRID_SIZE / (double) Main.AMOUNT;
    public static double RADIUS = area / 2.3;
    public static double space = area / 2 - RADIUS;

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
        super(column * area + space + RADIUS,row * area + space + RADIUS, RADIUS, Color.BLACK);
        this.row = row;
        this.column = column;

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

    public static void updateSizeSettings() {
        area = Main.GRID_SIZE / (double) Main.AMOUNT;
        RADIUS = area / 2.3;
    }

    public void reset() {
        draw(defaultColor);
        if (obstacle)
            setObstacle(false);
        prev = null;
    }

    public int costs() {
        if (Main.current != null)
            return Main.current.costs(this);
        else
            return -1;
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