package de.softknk;

/**
 * used to loop over the whole grid
 */
public class GridOperation {

    public interface Operation {
        void consume(int i, int j);
    }

    public static void grid_operation(Operation operation) {
        for (int i = 0; i < Main.grid.length; i++) {
            for (int j = 0; j < Main.grid[0].length; j++)
                operation.consume(i, j);
        }
    }
}
