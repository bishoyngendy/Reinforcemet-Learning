import com.sun.javaws.exceptions.InvalidArgumentException;
import models.Cell;
import models.Maze;

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
            Cell[][] cells = maze.getCells();
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    if (cells[i][j].getBarrier()) {
                        System.out.print("B ");
                    } else {
                        System.out.print("S ");
                    }
                }
                System.out.println();
            }
        } catch (InvalidArgumentException e) {
            System.out.println("Invalid: ");
        }
    }
}
