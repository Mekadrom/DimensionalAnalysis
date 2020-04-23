package com.higgs.da;

import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DimensionalMatrixHelper {

    // the order of axes labels. yes, the application is touted to be able to visualize n-dimensions but we run out
    // of characters eventually (after 26 dimensions) also the visualization loses perceptibility after 5 dimensions
    public static final String AXES_ORDER = "xyzwuvijklmnopqrstabcdefgh";

    // starting frustum length for every possible perspective projection
    public static final float DEFAULT_FRUSTUM_LENGTH = 1.8f;


    private static List<String> _dimPerm = new ArrayList<>();

    public static void preload(int numDim) {
        // retrieve a list of all permutations of each dimension in the possible axes (up to 26) in combinations of 2
        _dimPerm = Utils.permute(getAxes(numDim), 2, false, false);
    }

    public static INDArray transform(final INDArray angles, final INDArray point, final String[] projections, final float[] fLengths) {
        final int dimNum = dimCountFromAngleCount(angles.columns());
        return project(rotate(angles, point), projections, fLengths).mul(DimensionalAnalysis.getDrawScale() * (2 * Math.pow(DEFAULT_FRUSTUM_LENGTH, dimNum) / dimNum));
    }

    private static INDArray project(final INDArray point, final String[] projections, final float[] fLengths) {
        INDArray projected = copy(point);
        for (int dim = point.rows(); dim > 2; dim--) {
            final float fLength = fLengths[dim - 3];
            final String pType = projections[dim - 3];
            if (DimensionalAnalysis.PERSPECTIVE.equalsIgnoreCase(pType)) {
                projected = Nd4j.matmul(getPerspectiveProjection(projected, fLength), projected);
            } else {
                projected = Nd4j.matmul(getOrthographicProjection(dim), projected);
            }
        }
        return projected;
    }

    /**
     * Rotate a given point in any dimension using the given angles
     *
     * @param angles any number of angles that corresponds to a number of dimensions
     * @param point a point to rotate-transform
     * @return the rotate-transformed point
     */
    public static INDArray rotate(final INDArray angles, final INDArray point) {
        INDArray rotated = copy(point);
        for (final INDArray rotationMatrix : getRotationMatrices(angles)) {
            rotated = Nd4j.matmul(rotationMatrix, rotated);
        }
        return rotated;
    }

    /**
     * Computes the rotation matrices for any given number of angles.
     *
     * @param angles angles in a format where there is only one row and as many columns as there are angles needed
     *               to define rotation in numDim-dimensional space
     * @return an array of rotation matrices to be applied sequentially to obtain a completely rotate-transformed point
     */
    private static INDArray[] getRotationMatrices(final INDArray angles) {
        int numDim = dimCountFromAngleCount(angles.columns()); // get numDim from number of angles

        // for every angle value there are two dimensions that are rotated about; this 2d int array stores that info
        final int[][] dimRotateAbout = new int[angles.columns()][2];
        for (int i = 0; i < _dimPerm.size(); i++) {
            final String permutation = _dimPerm.get(i);
            for (int j = 0; j < permutation.length(); j++) {
                dimRotateAbout[i][j] = AXES_ORDER.indexOf(permutation.charAt(j));
            }
        }

        final INDArray[] rotationMatrices = new INDArray[angles.columns()];
        for (int i = 0; i < angles.columns(); i++) {
            rotationMatrices[i] = getRotationMatrix(angles.getColumn(i).getDouble(0), numDim, dimRotateAbout[i]);
        }
        return rotationMatrices;
    }

    /**
     * Given an angle value (radians), the number of dimensions, and a set of two dimensions to rotate
     * about, computes a single rotation matrix in n-dimensional space.
     *
     * Most of what I know about this particular topic comes from this paper:
     * http://citeseerx.ist.psu.edu/viewdoc/download;jsessionid=3621C7A9C67B2FA7223C87151D9AAD07?doi=10.1.1.4.8662&rep=rep1&type=pdf
     *
     * @param angle the angle value to use in cosine and sine calculations
     * @param numDim the number of dimensions
     * @param rotateAbout an array of size [2] that contains the two dimensions to rotate about
     * @return the rotation matrix for the given angle, number of dimensions, and dimensions to rotate with
     */
    private static INDArray getRotationMatrix(final double angle, final int numDim, final int[] rotateAbout) {
        final INDArray rotMat = Nd4j.zeros(new int[] { numDim, numDim });
        for (int i = 0; i < numDim; i++) {
            for (int j = 0; j < numDim; j++) {
                if ((i == rotateAbout[0] && j == rotateAbout[0]) || (i == rotateAbout[1] && j == rotateAbout[1])) {
                    rotMat.put(i, j,  Math.cos(angle));
                } else if (i == rotateAbout[0] && j == rotateAbout[1]) {
                    rotMat.put(i, j, -Math.sin(angle));
                } else if (i == rotateAbout[1] && j == rotateAbout[0]) {
                    rotMat.put(i, j,  Math.sin(angle));
                } else if (i == j) {
                    rotMat.put(i, j, 1.0);
                }
            }
        }
        return rotMat;
    }

    /**
     * Utility method for determining the number of dimensions involved in a rotation calculation based on the number of
     * angles required to fully define the rotation.
     *
     * @param nangles the number of angles
     * @return the number of dimensions
     */
    public static int dimCountFromAngleCount(final int nangles) {
        int angleCount = 1;
        int progress = 2;
        int dim = 2;
        while (angleCount != nangles) {
            angleCount += progress++;

            // in case the number of angles given doesn't actually correspond to a dimension,
            // use last good dimension which essentially ignores any more angle numbers
            if (angleCount > nangles) {
                return -1;
            } else {
                dim++;
            }
        }
        return dim;
    }

    /**
     * Utility method for determining the number of dimensions involved in a rotation calculation based on the number of
     * angles required to fully define the rotation.
     * @param ndimensions the number of dimensions
     * @return the number of angles
     */
    public static int angleCountFromDimCount(final int ndimensions) {
        int numAngles = 1;
        int progress = 2;
        int dim = 2;
        while (dim != ndimensions) {
            numAngles += progress++;
            dim++;
        }
        return numAngles;
    }

    /**
     * Given a point and the frustum length for a perspective projection, returns the projection matrix that
     * corresponds to the given values
     *
     * @param point a point to project
     * @param frustumLength a distance to the observer
     * @return a projection matrix for this point
     */
    public static INDArray getPerspectiveProjection(final INDArray point, final float frustumLength) {
        float w = 1 / (frustumLength - point.getColumn(0).getFloat(point.rows() - 1));
        final INDArray persp = Nd4j.zeros(new int[] { point.rows() - 1, point.rows() });
        fillArrayAxially(persp, w);
        return persp;
    }

    /**
     * Given only a number of dimensions, returns an orthographic projection matrix
     *
     * @param numDim the number of dimensions you start with
     * @return a matrix with 1 less row than the input, an orthographic projection simply
     * truncates this extra point data
     */
    public static INDArray getOrthographicProjection(final int numDim) {
        final INDArray ortho = Nd4j.zeros(new int[] { numDim - 1, numDim });
        fillArrayAxially(ortho, 1);
        return ortho;
    }

    public static INDArray fillArrayAxially(final double value, final long... shape) {
        final INDArray array = Nd4j.create(DataType.FLOAT, shape);
        fillArrayAxially(array, value);
        return array;
    }

    /**
     * Fill an array with the given value but only in the positions where the row index == the column index. Otherwise
     * puts zero
     *
     * @param array the array to fill diagonally (affected in place)
     * @param value the value to fill the array with
     */
    public static void fillArrayAxially(final INDArray array, final double value) {
        fillArrayAxially(array, value, 0);
    }

    /**
     * Fill an array with the given value but only in the positions where the row index == the column index. Otherwise
     * puts the other value given
     *
     * @param array the array to fill diagonally (affected in place)
     * @param value the value to fill the array with
     * @param other the other value to fill array with
     */
    public static void fillArrayAxially(final INDArray array, final double value, final double other) {
        for (int i = 0; i < array.rows(); i++) {
            for (int j = 0; j < array.columns(); j++) {
                if (i == j) {
                    array.put(i, j, value);
                } else {
                    array.put(i, j, other);
                }
            }
        }
    }

    /**
     * Retrieves the ordered axes labels truncated for the number of dimensions specified
     *
     * @param numDim the number of axes labels needed
     * @return the axes labels as a char[]
     */
    public static char[] getAxes(final int numDim) {
        return Arrays.copyOfRange(AXES_ORDER.toCharArray(), 0, numDim);
    }

    /**
     * Shorthand for creating a duplicate of an INDArray
     *
     * @param input the array to copy
     * @return the copy of the array (input is unaffected)
     */
    public static INDArray copy(final INDArray input) {
        final INDArray copy = Nd4j.ones(input.shape());
        Nd4j.copy(input, copy);
        return copy;
    }

    public static void bisectMatrixColumnWise(final INDArray firstHalf, final INDArray secondHalf, final INDArray points) {
        for (int i = 0; i < points.columns(); i++) {
            if (i < points.columns() / 2) {
                firstHalf.putColumn(i, points.getColumn(i));
            } else {
                secondHalf.putColumn(i - (points.columns() / 2), points.getColumn(i));
            }
        }
    }
}
