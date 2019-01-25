import models.Cell;
import models.Maze;

import java.awt.*;
import java.util.ArrayList;

public class ValueFunction {

	private Maze maze;
	private double gamma;
	private double[][] value;
	private int dimension;
	private int[][] policy;
	private int dx[] = { 0, 1, 0, -1 };
	private int dy[] = { 1, 0, -1, 0 };
	private Cell[][] cells;

	public ValueFunction(Maze maze, double gamma) {
		this.maze = maze;
		this.cells = maze.getCells();
		this.gamma = gamma;
		this.dimension = maze.getDimension();
		this.value = new double[dimension][dimension];
		this.policy = new int[dimension][dimension];
		for (int i = 0; i < 100000; i++) {
			value = performIteration();
		}
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				int dir = -1;
				double max = Integer.MIN_VALUE;
				if (value[i][j] != 0)
					for (int k = 0; k < 4; k++) {
						int x = i + dx[k], y = j + dy[k];
						if (canGo(x, y) && (value[x][y] != 0 || (x == y && x == dimension - 1)) && value[x][y] > max) {
							max = value[x][y];
							dir = k;
						}
					}
				policy[i][j] = dir;
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Maze maze = new Maze(7, 5);
		ValueFunction vf = new ValueFunction(maze, 1);
		Cell[][] cells = maze.getCells();
		double[][] x = vf.getValues();
		int[][] y = vf.getPolicy();
		PrintTable pt = new PrintTable();
		pt.print(cells);
		pt.print(x);
		pt.print(y);
	}

	public double[][] getValues() {
		return value;
	}

	public int[][] getPolicy() {
		return policy;
	}

	private boolean canGo(int x, int y) {
		if (x < 0 || y < 0)
			return false;
		if (x >= dimension || y >= dimension)
			return false;
		if (cells[x][y].isBarrier())
			return false;
		return true;
	}

	private double getReward(int x, int y) {
		if (x == dimension - 1 && x == y)
			return dimension * dimension;
		return -1;
	}

	private double[][] performIteration() {
		double[][] curV = new double[dimension][dimension];
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				if (!cells[i][j].isBarrier())
					curV[i][j] = computeStateValue(i, j);
			}
		}
		return curV;
	}

	private double computeStateValue(int xx, int yy) {
		if (xx == dimension - 1 && xx == yy)
			return 0.0;
		double v = 0.0, cnt = 0.0;
		for (int dir = 0; dir < 4; dir++) {
			int x = xx + dx[dir], y = yy + dy[dir];
			if (canGo(x, y)) {
				++cnt;
				v += getReward(x, y) + value[x][y];
			}
		}
		v *= (1 / cnt);
		return v;
	}

	public ArrayList<Point> evalPath(int x, int y) {
		--x;
		--y;
		ArrayList<Point> pts = new ArrayList<Point>();
		pts.add(new Point(x + 1, y + 1));
		while (policy[x][y] != -1) {
			int newx = x + dx[policy[x][y]], newy = y + dy[policy[x][y]];
			pts.add(new Point(newx + 1, newy + 1));
			x = newx;
			y = newy;
		}
		return pts;
	}
}
