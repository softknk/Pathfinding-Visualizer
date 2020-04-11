package de.softknk;

public class VisualizationNotPossibleException extends Exception {

    enum Problem {
        START_OR_TARGET,
        ALGORITHM_NOT_SELECTED
    }

    public VisualizationNotPossibleException(Problem problem) {
        System.out.println("problem: " + problem);
    }
}
