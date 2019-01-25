package utils;

import models.Cell;

public class TablePrinter {
    public void printCells(Cell[][] cells) {
        String str[][] = new String[cells.length][cells.length];
        for (int i = 0; i < str.length; i++) {
            for (int j = 0; j < str.length; j++) {
                if (cells[i][j].isBarrier())
                    str[i][j] = "B";
                else
                    str[i][j] = ".";
            }
        }
        str[str.length - 1][str.length - 1] = "@";
        print(str);
    }

    public void printValues(Cell[][] cells) {
        String str[][] = new String[cells.length][cells.length];
        for (int i = 0; i < str.length; i++) {
            for (int j = 0; j < str.length; j++) {
                str[i][j] = String.valueOf(cells[i][j].getNewValue());
            }
        }
        print(str);
    }

    public void printPolicy(Cell[][] cells) {
        String str[][] = new String[cells.length][cells.length];
        for (int i = 0; i < str.length; i++) {
            for (int j = 0; j < str.length; j++) {
                String s;
                int direction = cells[i][j].isBarrier() ? -1 : getPolicy(cells[i][j]);
                switch (direction) {
                    case 0:
                        s = "→";
                        break;
                    case 1:
                        s = "↓";
                        break;
                    case 2:
                        s = "←";
                        break;
                    case 3:
                        s = "↑";
                        break;
                    default:
                        s = "#";
                        break;
                }
                str[i][j] = s;
            }
        }
        str[str.length - 1][str.length - 1] = "@";
        print(str);
    }

    private int getPolicy(Cell cell) {
        int dir = -1;
        double max = Double.MIN_VALUE;
        double[] probabilities = cell.getProbabilities();
        for (int i = 0; i < probabilities.length; i++) {
            if (probabilities[i] > max) {
                max = probabilities[i];
                dir = i;
            }
        }
        return dir;
    }
//
//    public void print(double[][] arr) {
//        String str[][] = new String[arr.length][arr.length];
//        for (int i = 0; i < str.length; i++) {
//            for (int j = 0; j < str.length; j++) {
//                str[i][j] = String.valueOf(arr[i][j]);
//            }
//        }
//        print(str);
//    }

    public void print(int[][] arr) {
        String str[][] = new String[arr.length][arr.length];
        for (int i = 0; i < str.length; i++) {
            for (int j = 0; j < str.length; j++) {
                String s = "";
                switch (arr[i][j]) {
                    case 0:
                        s = "→";
                        break;
                    case 1:
                        s = "↓";
                        break;
                    case 2:
                        s = "←";
                        break;
                    case 3:
                        s = "↑";
                        break;
                    default:
                        s = "#";
                        break;
                }
                str[i][j] = s;
            }
        }
        str[str.length - 1][str.length - 1] = "@";
        print(str);
    }

//    public void print(Cell[][] arr) {
//        String str[][] = new String[arr.length][arr.length];
//        for (int i = 0; i < str.length; i++) {
//            for (int j = 0; j < str.length; j++) {
//                if (arr[i][j].isBarrier())
//                    str[i][j] = "B";
//                else
//                    str[i][j] = ".";
//            }
//        }
//        str[str.length - 1][str.length - 1] = "@";
//        print(str);
//    }

    private void print(String[][] arr) {
        int mx = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr.length; j++) {
                mx = Math.max(mx, arr[i][j].length());
            }
        }
        mx += 2;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr.length; j++) {
                arr[i][j] = formatString(mx, arr[i][j]);
            }
        }
        int len = arr.length * (mx + 1) + 1;
        printHorizontal(len, mx);
        for (int i = 0; i < arr.length; i++) {
            System.out.print("|");
            for (int j = 0; j < arr.length; j++) {
                System.out.print(arr[i][j] + "|");
            }
            System.out.println();
            printHorizontal(len, mx);
        }
    }

    private void printHorizontal(int length, int off) {
        int cur = 0;
        for (int i = 0; i < length; i++) {
            if (cur == 0)
                System.out.print("+");
            else
                System.out.print("-");
            ++cur;
            if (cur == off + 1)
                cur = 0;
        }
        System.out.println();
    }

    private String formatString(int len, String str) {
        boolean ok = true;
        while (str.length() != len) {
            if (ok)
                str += " ";
            else
                str = " " + str;
            ok = !ok;
        }
        return str;
    }

}
