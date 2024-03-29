package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Maze {
    private Cell[][] cells;
    private int dimension;

    public Maze(int dimension, int numberOfBarriers) throws Exception {
        if (numberOfBarriers > dimension * dimension - 1 || numberOfBarriers < 0) {
            throw new RuntimeException("Invalid Barriers Number");
        }
        this.dimension = dimension;
        this.cells = new Cell[dimension][dimension];
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][j] = new Cell();
            }
        }
        List<Pair> pairs = getBarrierIndices(dimension, numberOfBarriers);
        for (Pair pair : pairs) {
            cells[pair.x][pair.y].setBarrier(true);
        }
    }

    public int getDimension() {
        return dimension;
    }

    private List<Pair> getBarrierIndices(int dimension, int numberOfBarriers) {
        List<Pair> all = new ArrayList<>();
        List<Pair> ret = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (!(i == dimension - 1 && j == dimension - 1)) {
                    all.add(new Pair(i, j));
                }
            }
        }
        for (int i = 0; i < numberOfBarriers; i++) {
            int randomIndex = random.nextInt(all.size());
            Pair pair = all.remove(randomIndex);
            ret.add(pair);
        }
        return ret;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }

    class Pair {
        private int x;
        private int y;

        public Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}