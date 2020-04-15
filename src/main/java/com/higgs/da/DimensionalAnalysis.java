package com.higgs.da;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class DimensionalAnalysis {
    public static final Shape SQUARE;

    static {
        final INDArray squarePoints = Nd4j.zeros(new int[] { 4, 2 });

        System.out.println(squarePoints);

        SQUARE = new Shape(2, squarePoints);
    }

    private int _ndimensions;

    public DimensionalAnalysis(final int ndimensions) {
        _ndimensions = ndimensions;

    }

    public static class Shape {
        public Shape(int ndimensions, final INDArray points) {

        }
    }
}
