package de.softknk.algorithms;

import de.softknk.Cell;
import de.softknk.Main;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public abstract class Pathfinding {

    protected Cell start, target;
    protected boolean paused, stopped;

    public List<Line> lines;
    public List<Circle> circles;

    public abstract void clean();

    public abstract void findPath();

    public abstract int costs(Cell source);

    public void stop() {
        stopped = true;
    }

    public void drawPath() {
        Color color = Color.rgb(70, 70, 70);
        lines = new ArrayList<>();
        circles = new ArrayList<>();
        final Cell[] tmp = {target};

        Timeline[] timelines = new Timeline[]{new Timeline()};
        timelines[0] = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            if (!paused) {
                if (tmp[0].getPrev() != null) {
                    Line line = new Line(tmp[0].getCenterX(), tmp[0].getCenterY(), tmp[0].getPrev().getCenterX(), tmp[0].getPrev().getCenterY());
                    line.setStroke(color);
                    line.setStrokeWidth(2);
                    Main.pane.getChildren().add(line);
                    Circle circle = new Circle(tmp[0].getCenterX(), tmp[0].getCenterY(), Cell.RADIUS * 0.65, color);
                    lines.add(line);
                    circles.add(circle);
                    Main.pane.getChildren().add(circle);
                    tmp[0] = tmp[0].getPrev();
                } else {
                    Circle circle = new Circle(tmp[0].getCenterX(), tmp[0].getCenterY(), Cell.RADIUS * 0.65, color);
                    Main.pane.getChildren().add(circle);
                    circles.add(circle);
                    timelines[0].stop();
                }
            }
        }));


        timelines[0].setCycleCount(Timeline.INDEFINITE);
        timelines[0].play();
    }

    public void pause() {
        paused = true;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean getPaused() {
        return paused;
    }
}
