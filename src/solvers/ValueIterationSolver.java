package solvers;

import models.Cell;
import models.Maze;
import utils.TablePrinter;

public class ValueIterationSolver implements MazeSolver {

    private final int dimension;
    private final Cell[][] cells;
    private final double gamma;
    private final TablePrinter tablePrinter;
    private int directions[][] = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

    public ValueIterationSolver(Maze maze, double gamma) {
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
        int iterations;
        for (iterations = 0; iterations < 100000; iterations++) {
            tablePrinter.printPolicy(cells);
            tablePrinter.printValues(cells);

            // select maximum action and update policy
            performIteration();

            // update old values with new values
            boolean converged = updateValues();
            if (converged) {
                tablePrinter.printPolicy(cells);
                tablePrinter.printValues(cells);
                break;
            }
        }
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

    private void performIteration() {
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (!cells[i][j].isBarrier() && !(i == dimension - 1 && j == dimension - 1)) {
                    updateValueAndPolicy(i, j);
                }
            }
        }
    }

    private void updateValueAndPolicy(int row, int column) {
        int policy = -1;
        double value = -Double.MAX_VALUE;
        for (int i = 0; i < 4; i++) {
            int nextX = row + directions[i][0];
            int nextY = column + directions[i][1];
            if (isValid(nextX, nextY)) {
                double reward = getImmediateReward(nextX, nextY);
                double currValue = reward + gamma * cells[nextX][nextY].getOldValue();
                if (currValue > value) {
                    value = currValue;
                    policy = i;
                }
            }
        }
        cells[row][column].setNewValue(value);
        cells[row][column].updatePolicy(policy);
    }

    private int getImmediateReward(int row, int column) {
        return (row == dimension - 1 && column == dimension - 1) ? 100000 - dimension * dimension : -1;
    }

    private boolean isValid(int row, int column) {
        return (row > -1 && row < dimension && column > -1 && column < dimension
                && !cells[row][column].isBarrier());
    }

    private boolean updateValues() {
        boolean converged = true;
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                if (!cells[i][j].isBarrier()) {
                    if (cells[i][j].getOldValue() != cells[i][j].getNewValue()) {
                        converged = false;
                    }
                    cells[i][j].setOldValue(cells[i][j].getNewValue());
                }
            }
        }
        return converged;
    }
}
