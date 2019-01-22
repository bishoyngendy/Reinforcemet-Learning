package models;

import com.sun.javaws.exceptions.InvalidArgumentException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Maze {
    private Cell[][] cells;

    public Maze(int dimension, int numberOfBarriers) throws InvalidArgumentException {
        if (numberOfBarriers > dimension * dimension || numberOfBarriers < 0) {
            throw new InvalidArgumentException(new String[]{"Invalid Barriers Number"});
        }
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

    private List<Pair> getBarrierIndices(int dimension, int numberOfBarriers) {
        List<Pair> all = new ArrayList<>();
        List<Pair> ret = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                all.add(new Pair(i, j));
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
