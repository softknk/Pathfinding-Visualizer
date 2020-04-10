package de.softknk.algorithms;

import de.softknk.Cell;

import static java.lang.Math.abs;

public interface Manhattan {

    static int manhattan_distance(int factor, Cell source, Cell target) {
        return factor * (abs(target.getRow() - source.getRow()) +
                abs(target.getColumn() - source.getColumn()));
    }
}
