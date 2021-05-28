package com.higgs.da;

import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class DimensionalMatrixHelperTest {
    @Test
    public void bisectMatrixColumnWise() {
        final INDArray array = Nd4j.create(new float[] {
                2, 3,
                4, 5
        }, new int[] { 2, 2 });

        final INDArray half1 = Nd4j.create(array.rows(), array.columns() / 2);
        final INDArray half2 = Nd4j.create(array.rows(), array.columns() / 2);
        DimensionalMatrixHelper.bisectMatrixColumnWise(half1, half2, array);
        System.out.println("first half: \n" + half1);
        System.out.println("second half: \n" + half2);
    }
}