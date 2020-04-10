package de.softknk.algorithms;

import de.softknk.Cell;
import de.softknk.Main;
import de.softknk.Pathfinding;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.*;

public class AStar extends Pathfinding {

    private List<Cell> openList;
    private Set<Cell> closedList;
    private Timeline timeline;

    public AStar(Cell start, Cell target) {
        this.start = start;
        this.target = target;
        this.start.setObstacle(false);
        this.target.setObstacle(false);

        openList = new ArrayList<>();
        closedList = new HashSet<>();

        lines = new ArrayList<>();
        circles = new ArrayList<>();
    }

    @Override
    public void findPath() {
        if (start != null && target != null)
            findPath(start, target);
    }

    @Override
    public int costs(Cell source) {
        return source.getG() + heuristic(source, target);
    }

    private int heuristic(Cell source, Cell target) {
        return Manhattan.manhattan_distance(1, source, target);
    }

    private void findPath(Cell start, Cell target) {
        openList.clear();
        closedList.clear();

        initCells(target);
        start.setDistance(0);
        openList.add(start);

        timeline = new Timeline(new KeyFrame(Duration.millis(20), event -> {
            if (!paused) {
                if (step()) {
                    openList.forEach(c -> c.draw(Color.rgb(255, 255, 0)));
                    closedList.forEach(c -> {
                        if (c != Main.start)
                            c.animate(Color.rgb(138, 222, 255), Color.rgb(220, 150, 255));
                    });
                } else {
                    drawPath();
                    timeline.stop();
                }
            }

            if (stopped) {
                clean();
                timeline.stop();
            }

        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @Override
    public void clean() {
        stopped = true;

        openList.forEach(cell -> {
            if (cell.isObstacle())
                cell.setObstacle(true);
            else
                cell.setObstacle(false);

            cell.animationRunning = false;
        });
        closedList.forEach(cell -> {
            if (cell.isObstacle())
                cell.setObstacle(true);
            else
                cell.setObstacle(false);

            cell.animationRunning = false;
        });

        Main.pane.getChildren().removeAll(lines);
        Main.pane.getChildren().removeAll(circles);
    }

    private boolean step() {
        if (!openList.isEmpty()) {
            Cell current = getCellWithLowestCosts();

            if (current == target)
                return false;

            List<Cell> adjacentCells = getAdjacentCells(current);
            Iterator<Cell> iterator = adjacentCells.iterator();
            while (iterator.hasNext()) {
                Cell adjacentCell = iterator.next();
                if (!closedList.contains(adjacentCell)) {
                    computeShortestCellDistance(current, adjacentCell);
                    if (!openList.contains(adjacentCell))
                        openList.add(adjacentCell);
                }
            }

            openList.remove(current);
            closedList.add(current);
            return true;
        }
        return false;
    }

    private void computeShortestCellDistance(Cell source, Cell destination) {
        if (destination.getDistance() > source.getDistance() + 1) {
            destination.setDistance(source.getDistance() + 1);
            destination.setPrevious(source);
        }
    }

    private Cell getCellWithLowestCosts() {
        Cell lowest = openList.get(0);
        Iterator<Cell> iterator = openList.iterator();
        while (iterator.hasNext()) {
            Cell current = iterator.next();
            if (current.costs() < lowest.costs())
                lowest = current;
        }
        return lowest;
    }

    private void initCells(Cell target) {
        for (int i = 0; i < Main.SIZE; i++) {
            for (int j = 0; j < Main.SIZE; j++) {
                Main.cells[i][j].setDistance(Integer.MAX_VALUE / 2);
            }
        }
    }

    public List<Cell> getAdjacentCells(Cell cell) {
        Cell[] adjacentCells = new Cell[4];

        if (cell.getRow() - 1 >= 0)
            adjacentCells[0] = Main.cells[cell.getRow() - 1][cell.getColumn()];
        if (cell.getColumn() + 1 < Main.cells.length)
            adjacentCells[1] = Main.cells[cell.getRow()][cell.getColumn() + 1];
        if (cell.getRow() + 1 < Main.cells.length)
            adjacentCells[2] = Main.cells[cell.getRow() + 1][cell.getColumn()];
        if (cell.getColumn() - 1 >= 0)
            adjacentCells[3] = Main.cells[cell.getRow()][cell.getColumn() - 1];

     /*  for (int i = cell.getRow() - 1; i < cell.getRow() + 2; i++) {
            for (int j = cell.getColumn() - 1; j < cell.getColumn() + 2; j++) {
                if (i >= 0 && i < Main.SIZE && j >= 0 && j < Main.SIZE) {
                    adjacentCells.add(Main.cells[i][j]);
                }
            }
        } */

        List<Cell> result = new ArrayList<>();

        for (int i = 0; i < adjacentCells.length; i++) {
            if (adjacentCells[i] != null && !adjacentCells[i].isObstacle())
                result.add(adjacentCells[i]);
        }

        return result;
    }
}
