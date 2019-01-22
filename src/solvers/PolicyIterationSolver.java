package solvers;

import models.Cell;
import models.Maze;

import java.util.HashSet;
import java.util.Set;

public class PolicyIterationSolver implements MazeSolver {
    private final static double EPSILON = 0.0000001;
    private int directions[][] = {{-1, 0}, {0, 1}, {0, -1}, {0, 1}};
    private double PICurrent = 0.0;
    private double PINext = 0.0;

    @Override
    public void solve(Maze maze, double gamma) {
        initializeRandomPI(maze);
        do {
            // policy evaluation
            PICurrent = performPolicyEvaluation(maze, gamma);

            // update values
            updateValues(maze);

            // greedy choose new pi
            PINext = greedyPolicyImprovement(maze, gamma);
        } while (Math.abs(PINext - PICurrent) > EPSILON);
    }

    private void updateValues(Maze maze) {
        Cell[][] cells = maze.getCells();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                if (!cells[i][j].getBarrier()) {
                    cells[i][j].setOldValue(cells[i][j].getNewValue());
                }
            }
        }
    }

    private double greedyPolicyImprovement(Maze maze, double gamma) {
        Cell[][] cells = maze.getCells();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                if (!cells[i][j].getBarrier()) {
                    double actionValues[] = getActionValues(cells, i, j, gamma);
                    Set<Integer> newActions = getNewActionsGreedy(actionValues);
                    cells[i][j].updateProbabilities(newActions);
                }
            }
        }
        return performPolicyEvaluation(maze, gamma);
    }

    private Set<Integer> getNewActionsGreedy(double[] actionValues) {
        Set<Integer> newActions = new HashSet<>();
        double max = Double.MIN_VALUE;
        for (double actionValue : actionValues) {
            max = Math.max(max, actionValue);
        }
        for (int i = 0; i < actionValues.length; i++) {
            if (Math.abs(max - actionValues[i]) < EPSILON) {
                newActions.add(i);
            }
        }
        return newActions;
    }

    private double[] getActionValues(Cell[][] cells, int x, int y, double gamma) {
        double actionValues[] = new double[4];
        int n = cells.length;
        for (int i = 0; i < 4; i++) {
            int nextX = x + directions[i][0];
            int nextY = y + directions[i][1];
            if (!isValid(nextX, nextY, cells)) {
                int reward = (nextX == n - 1 && nextY == n - 1) ? n * n : 0;
                actionValues[i] = (reward + (gamma * cells[nextX][nextY].getOldValue()));
            }
        }
        return actionValues;
    }

    private double performPolicyEvaluation(Maze maze, double gamma) {
        double policyValue = 0.0;
        Cell[][] cells = maze.getCells();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                if (!cells[i][j].getBarrier()) {
                    double newValue = calculateNewValue(cells, i, j, gamma);
                    cells[i][j].setNewValue(newValue);
                    policyValue += newValue;
                }
            }
        }
        return policyValue;
    }

    private double calculateNewValue(Cell[][] cells, int x, int y, double gamma) {
        double newValue = 0;
        int n = cells.length;
        Cell cell = cells[x][y];
        double probabilities[] = cell.getProbabilities();
        for (int i = 0; i < 4; i++) { // 4 actions
            int nextX = x + directions[i][0];
            int nextY = y + directions[i][1];
            if (!isValid(nextX, nextY, cells)) {
                int reward = (nextX == n - 1 && nextY == n - 1) ? n * n : 0;
                newValue += probabilities[i]
                        * (reward + (gamma * cells[nextX][nextY].getOldValue()));
            }
        }
        return newValue;
    }

    private boolean isValid(int nextX, int nextY, Cell[][] cells) {
        return nextX > -1 && nextX < cells.length
                && nextY > -1 && nextY < cells.length
                && !cells[nextX][nextY].getBarrier();
    }

    private void initializeRandomPI(Maze maze) {
        Cell[][] cells = maze.getCells();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                if (!cells[i][j].getBarrier()) {
                    cells[i][j].initializeProbabilities();
                }
            }
        }
    }
}
