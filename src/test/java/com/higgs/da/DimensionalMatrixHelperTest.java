package com.higgs.da;

import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import static org.junit.Assert.*;

public class DimensionalMatrixHelperTest {
    @Test
    public void getPP() {
        final INDArray points = Nd4j.create(new float[] {
                -1,  1,  1, -1, -1,  1, 1, -1, // x
                -1, -1,  1,  1, -1, -1, 1,  1, // y
                -1, -1, -1, -1,  1,  1, 1,  1  // z
        }, new int[] { 3, 8 });

        DimensionalMatrixHelper.initFrustumLengths(3);

        System.out.println(DimensionalMatrixHelper.getPerspectiveProjection(3, points));
        System.out.println(DimensionalMatrixHelper.getPP(3, points));
    }
}