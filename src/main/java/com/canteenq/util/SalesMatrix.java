package com.canteenq.util;

public final class SalesMatrix {

    private SalesMatrix() {
    }

    public static int[][] add(int[][] a, int[][] b) {
        sameSize(a, b);
        int[][] result = new int[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                result[i][j] = a[i][j] + b[i][j];
            }
        }
        return result;
    }

    public static int[][] subtract(int[][] a, int[][] b) {
        sameSize(a, b);
        int[][] result = new int[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                result[i][j] = a[i][j] - b[i][j];
            }
        }
        return result;
    }

    public static double[][] multiply(double[][] a, int[][] b) {
        if (a.length == 0 || b.length == 0 || a[0].length != b.length) {
            throw new IllegalArgumentException("Columns of first matrix must match rows of second");
        }
        double[][] result = new double[a.length][b[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                double sum = 0;
                for (int k = 0; k < b.length; k++) {
                    sum += a[i][k] * b[k][j];
                }
                result[i][j] = sum;
            }
        }
        return result;
    }

    private static void sameSize(int[][] a, int[][] b) {
        if (a.length != b.length || (a.length > 0 && a[0].length != b[0].length)) {
            throw new IllegalArgumentException("Both matrices must be of same size");
        }
    }
}
