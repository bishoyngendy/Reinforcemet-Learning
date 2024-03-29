package solvers;

import models.Cell;
import models.Maze;
import utils.TablePrinter;

import java.util.HashSet;
import java.util.Set;

public class PolicyIterationSolver implements MazeSolver {
    private final static double EPSILON = 0.0000001;
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
        long startTime = System.currentTimeMillis();
        tablePrinter.printCells(cells);
        initializeRandomPI();
        double PICurrent;
        double PINext;
        int iterations = 0;
        do {
            tablePrinter.printPolicy(cells);
            tablePrinter.printValues(cells);

            // policy evaluation
            PICurrent = performPolicyEvaluation();

            // update old values with new values
            updateValues();

            // greedy choose new pi
            PINext = greedyPolicyImprovement();

            iterations++;
        } while (Math.abs(PINext - PICurrent) > EPSILON);
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("Time Taken To Solve in ms: " + estimatedTime);
        System.out.println("Number of Iterations to Solve: " + iterations);
        double[][] pathCost = getPathCost();
        System.out.println("Path Cost from Each Cell: ");
        tablePrinter.print(pathCost, dimension);
    }

    private double[][] getPathCost() {
        double[][] pathCost = new double[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (i == dimension - 1 && j == dimension - 1) {
                    pathCost[i][j] = 0;
                } else if (cells[i][j].isBarrier()) {
                    pathCost[i][j] = -1;
                } else if (cells[i][j].getOldValue() < 0) {
                    pathCost[i][j] = dimension * dimension;
                } else {
                    int cost = getPathCost(i, j);
                    pathCost[i][j] = cost;
                }
            }
        }
        return pathCost;
    }

    private int getPathCost(int row, int column) {
        if (row == dimension - 1 && column == dimension - 1) {
            return 0;
        }
        if (!isValid(row, column)) {
            return dimension * dimension;
        }
        int policy = cells[row][column].getPolicy();
        int nextX = row + directions[policy][0];
        int nextY = column + directions[policy][1];
        return 1 + getPathCost(nextX, nextY);
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
                if (!cells[i][j].isBarrier() && !(i == dimension - 1 && j == dimension - 1)) {
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
        return (row == dimension - 1 && column == dimension - 1) ? 100000 - dimension * dimension : -1;
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
                    int newAction = getNewActionGreedy(actionValues);
                    cells[i][j].updatePolicy(newAction);
                }
            }
        }
        return performPolicyEvaluation();
    }

    private int getNewActionGreedy(double[] actionValues) {
        int newAction = -1;
        double max = -Double.MAX_VALUE;
        for (int i = 0; i < 4; i++) {
            if (actionValues[i] > max) {
                max = actionValues[i];
                newAction = i;
            }
        }
        return newAction;
    }

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
