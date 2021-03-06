package de.softknk.algorithms;

import de.softknk.Cell;
import de.softknk.GridOperation;
import de.softknk.Main;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.*;

public class Dijkstra extends Pathfinding {

    private List<Cell> openList;
    private Set<Cell> closedList;
    private Timeline timeline;

    public Dijkstra(Cell start, Cell target) {
        this.start = start;
        this.target = target;
        this.start.setObstacle(false);
        this.target.setObstacle(false);

        openList = new ArrayList<>();
        closedList = new HashSet<>();
    }

    @Override
    public void findPath() {
        if (start != null && target != null)
            _findPath();
    }

    @Override
    public int costs(Cell source) {
        return source.getG();
    }

    private void _findPath() {
        openList.clear();
        closedList.clear();

        initCells();
        start.setDistance(0);
        openList.add(start);

        timeline = new Timeline(new KeyFrame(Duration.millis(20), event -> {
            if (!paused) {
                if (step()) {
                    openList.forEach(c -> c.draw(Color.rgb(255, 255, 0)));
                    closedList.forEach(c -> {
                        if (c != Main.start)
                            c.animate(Color.rgb(140, 220, 255), Color.rgb(220, 150, 255));
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

    private void initCells() {
        GridOperation.grid_operation((i, j) -> Main.grid[i][j].setDistance(Integer.MAX_VALUE / 2));
    }

    public List<Cell> getAdjacentCells(Cell cell) {
        Cell[] adjacentCells = new Cell[4];

        if (cell.getRow() - 1 >= 0)
            adjacentCells[0] = Main.grid[cell.getRow() - 1][cell.getColumn()];
        if (cell.getColumn() + 1 < Main.grid.length)
            adjacentCells[1] = Main.grid[cell.getRow()][cell.getColumn() + 1];
        if (cell.getRow() + 1 < Main.grid.length)
            adjacentCells[2] = Main.grid[cell.getRow() + 1][cell.getColumn()];
        if (cell.getColumn() - 1 >= 0)
            adjacentCells[3] = Main.grid[cell.getRow()][cell.getColumn() - 1];

        List<Cell> result = new ArrayList<>();

        for (int i = 0; i < adjacentCells.length; i++) {
            if (adjacentCells[i] != null && !adjacentCells[i].isObstacle())
                result.add(adjacentCells[i]);
        }

        return result;
    }
}

