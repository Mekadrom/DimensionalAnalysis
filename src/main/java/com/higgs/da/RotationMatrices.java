package com.higgs.da;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import static java.lang.Math.*;

public class RotationMatrices {

    public static INDArray build(int axis, float[] angles) {
        if(angles.length == 1) {
            if(axis == 0) {
                return Nd4j.create(new double[] {
                        cos(angles[axis]), -sin(angles[axis]), 0,
                        sin(angles[axis]), cos(angles[axis]),  0,
                        0,                 0,                  1
                }, new int[] { 3, 3 });
            }
        }
        return null;
    }

//    float[][] rotationZ = {
//            { cos(angle), -sin(angle), 0},
//            { sin(angle), cos(angle), 0},
//            { 0, 0, 1}
//    };
//
//    float[][] rotationX = {
//            { 1, 0, 0},
//            { 0, cos(angle), -sin(angle)},
//            { 0, sin(angle), cos(angle)}
//    };
//
//    float[][] rotationY = {
//            { cos(angle), 0, sin(angle)},
//            { 0, 1, 0},
//            { -sin(angle), 0, cos(angle)}
//    };
}
