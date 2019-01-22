package solvers;

import models.Maze;

public interface MazeSolver {
    void solve(Maze maze, double gamma);
}
