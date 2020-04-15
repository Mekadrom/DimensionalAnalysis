package com.higgs.da;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class DimensionalAnalysis {
    public static final Shape SQUARE;
    public static final Shape CUBE;

    static {
        final INDArray squarePoints = Nd4j.zeros(new int[] { 4, 2 });
        final INDArray cubePoints = Nd4j.zeros(new int[] { 8, 3 });

        // TODO: automate this

        squarePoints.put(0, 0, -1);
        squarePoints.put(0, 1, -1);

        squarePoints.put(1, 0,  1);
        squarePoints.put(1, 1, -1);

        squarePoints.put(2, 0, -1);
        squarePoints.put(2, 1,  1);

        squarePoints.put(3, 0,  1);
        squarePoints.put(3, 1,  1);


        cubePoints.put(0, 0, -1);
        cubePoints.put(0, 1, -1);
        cubePoints.put(0, 2, -1);

        cubePoints.put(1, 0,  1);
        cubePoints.put(1, 1, -1);
        cubePoints.put(1, 2, -1);

        cubePoints.put(2, 0, -1);
        cubePoints.put(2, 1,  1);
        cubePoints.put(2, 2, -1);

        cubePoints.put(3, 0, -1);
        cubePoints.put(3, 1, -1);
        cubePoints.put(3, 2, 1);

        cubePoints.put(4, 0,  1);
        cubePoints.put(4, 1,  1);
        cubePoints.put(4, 2, -1);

        cubePoints.put(5, 0,  -1);
        cubePoints.put(5, 1,  1);
        cubePoints.put(5, 2,  1);

        cubePoints.put(6, 0,  1);
        cubePoints.put(6, 1, -1);
        cubePoints.put(6, 2,  1);

        cubePoints.put(7, 0,  1);
        cubePoints.put(7, 1,  1);
        cubePoints.put(7, 2,  1);

//        boolean negative = true;
//
//        for(int r = 0; r < squarePoints.rows(); r++) {
//            for(int c = 0; c < squarePoints.columns(); c++) {
//                int value = 1;
//                if(r == 0 || (r == 1 && c == 0) || (r == 2 && c == 1)) {
//                    value *= -1;
//                }
//                squarePoints.put(r, c, value);
//            }
//        }

        System.out.println(squarePoints);

        SQUARE = new Shape(2, squarePoints);
        CUBE = new Shape(3, cubePoints);
    }

    private int _ndimensions;
    private INDArray _angles;

    public DimensionalAnalysis(final int ndimensions) {
        _ndimensions = ndimensions;
//        CUBE.
    }

    public static class Shape {
        public Shape(int ndimensions, final INDArray points) {

        }
    }
}
