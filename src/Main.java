import com.sun.javaws.exceptions.InvalidArgumentException;
import models.Cell;
import models.Maze;
import solvers.MazeSolver;
import solvers.PolicyIterationSolver;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter N: ");
        int n = scanner.nextInt();
        System.out.println("Enter Number Of Barriers: ");
        int numberOfBarriers = scanner.nextInt();
        System.out.println("Enter Gamma: ");
        float gamma = scanner.nextFloat();
        try {
            Maze maze = new Maze(n, numberOfBarriers);
            MazeSolver mazeSolver = new PolicyIterationSolver(maze, gamma);
            mazeSolver.solve();
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println(e.getMessage());
//            System.out.println("Invalid: ");
        }
    }
}
