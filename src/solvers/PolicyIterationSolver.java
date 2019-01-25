package solvers;

import models.Cell;
import models.Maze;
import utils.TablePrinter;

public class PolicyIterationSolver implements MazeSolver {
    private final static double EPSILON = 0.001;
    private final int dimension;
    private final Cell[][] cells;
    private final double gamma;
    private final TablePrinter tablePrinter;
    private int directions[][] = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

    public PolicyIterationSolver(Maze maze, double gamma) {
        this.dimension = maze.getDimension();
        this.cells = new Cell[dimension][dimension];
        this.gamma = gamma;
        this.tablePrinter = new TablePrinter();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][j] = maze.getCells()[i][j].clone();
            }
        }
    }

    @Override
    public void solve() {
        tablePrinter.printCells(cells);
        initializeRandomPI();
        double PICurrent;
        double PINext;
//        do {
        for (int i = 0; i < 4; i++) {
            tablePrinter.printPolicy(cells);
            tablePrinter.printValues(cells);

            // policy evaluation
            PICurrent = performPolicyEvaluation();
//            System.out.println("Old: " + PICurrent);

            // update old values with new values
            updateValues();

            // greedy choose new pi
            PINext = greedyPolicyImprovement();
//            System.out.println("New: " + PINext);

            System.out.println(Math.abs(PINext - PICurrent));

//            tablePrinter.printValues(cells);
//        } while (Math.abs(PINext - PICurrent) > EPSILON);
        }
    }

    private void initializeRandomPI() {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                if (!cells[i][j].isBarrier()) {
                    cells[i][j].initializeProbabilities();
                }
            }
        }
    }

    private double performPolicyEvaluation() {
        double policyValue = 0.0;
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                if (!cells[i][j].isBarrier()) {
                    double newValue = calculateNewValue(i, j);
                    cells[i][j].setNewValue(newValue);
                    policyValue += newValue;
                }
            }
        }
        return policyValue;
    }

    private double calculateNewValue(int row, int column) {
        double newValue = 0;
        Cell cell = cells[row][column];
        double probabilities[] = cell.getProbabilities();
        for (int i = 0; i < 4; i++) { // 4 actions
            int nextX = row + directions[i][0];
            int nextY = column + directions[i][1];
            if (isValid(nextX, nextY)) {
                int reward = getImmediateReward(nextX, nextY);
                newValue += probabilities[i]
                        * (reward + (gamma * cells[nextX][nextY].getOldValue()));
            }
        }
        return newValue;
    }

    private int getImmediateReward(int row, int column) {
        return (row == dimension - 1 && column == dimension - 1) ? dimension : 0;
    }

    private boolean isValid(int row, int column) {
        return (row > -1 && row < dimension && column > -1 && column < dimension
                && !cells[row][column].isBarrier());
    }

    private void updateValues() {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                if (!cells[i][j].isBarrier()) {
                    cells[i][j].setOldValue(cells[i][j].getNewValue());
                }
            }
        }
    }

    private double greedyPolicyImprovement() {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                if (!cells[i][j].isBarrier()) {
                    double actionValues[] = getActionValues(i, j);
//                    Set<Integer> newActions = getNewActionsGreedy(actionValues);
//                    cells[i][j].updateProbabilities(newActions);
                    int newAction = getNewActionGreedy(actionValues);
                    cells[i][j].updatePolicy(newAction);
                }
            }
        }
        return performPolicyEvaluation();
    }

    private int getNewActionGreedy(double[] actionValues) {
        int newAction = -1;
        double max = Double.MIN_VALUE;
        for (int i = 0; i < 4; i++) {
            if (actionValues[i] > max) {
                max = actionValues[i];
                newAction = i;
            }
        }
        return newAction;
    }
//
//    private Set<Integer> getNewActionsGreedy(double[] actionValues) {
//        Set<Integer> newActions = new HashSet<>();
//        double max = Double.MIN_VALUE;
//        for (double actionValue : actionValues) {
//            max = Math.max(max, actionValue);
//        }
//        for (int i = 0; i < actionValues.length; i++) {
//            if (Math.abs(max - actionValues[i]) < EPSILON) {
//                newActions.add(i);
//            }
//        }
//        return newActions;
//    }

    private double[] getActionValues(int row, int column) {
        double actionValues[] = new double[4];
        for (int i = 0; i < 4; i++) {
            int nextX = row + directions[i][0];
            int nextY = column + directions[i][1];
            if (isValid(nextX, nextY)) {
                int reward = getImmediateReward(nextX, nextY);
                actionValues[i] = (reward + (gamma * cells[nextX][nextY].getOldValue()));
            }
        }
        return actionValues;
    }
}
