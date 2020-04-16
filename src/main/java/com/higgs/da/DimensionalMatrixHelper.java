package com.higgs.da;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class DimensionalMatrixHelper {
    public static final float FRUSTUM_LENGTH_3D = 2.2f;
    public static final float FRUSTUM_LENGTH_4D = 2f;

    /**
     * TODO: use this to create rotation matrices dynamically
     * Use to obtain identity rotation matrix and perform transformations
     * to result in higher dimensional rotation matrices.
     * @param theta angle involved about any rotator (axis, plane, etc.)
     * @return the identity rotation matrix, where a positive angle results in counter clockwise rotation transformation
     */
    public static INDArray getIdentityRotationMatrix(final double theta) {
        return Nd4j.create(new double[] {
                cos(theta), -sin(theta),
                sin(theta),  cos(theta)
        }, new int[] { 2, 2 });
    }

    /**
     * Rotate a given point in any dimension using the given angles
     * @param angles any number of angles that corresponds to a number of dimensions
     * @param point
     * @return
     */
    public static INDArray rotate(final INDArray angles, final INDArray point) {
        final int dim = dimCountFromAngleCount(angles.columns());

        // TODO: automate decisions to nth dimension
        if (dim == 2) {
            double theta = angles.getColumn(0).getDouble(0);

            final INDArray rotation = Nd4j.create(new float[] {
                    (float) cos(theta), (float) -sin(theta),
                    (float) sin(theta), (float) cos(theta)
            }, new int[] { dim, dim });

            INDArray rotated = Nd4j.matmul(rotation, point);

            return rotated;
        } else if (dim == 3) {
            double x = angles.getColumn(0).getDouble(0);
            double y = angles.getColumn(1).getDouble(0);
            double z = angles.getColumn(2).getDouble(0);

            final INDArray rotationX = Nd4j.create(new float[] {
                    1,              0,               0,
                    0, (float) cos(x), (float) -sin(x),
                    0, (float) sin(x), (float)  cos(x)
            }, new int[] { dim, dim });

            final INDArray rotationY = Nd4j.create(new float[] {
                    (float)  cos(y), 0, (float) sin(y),
                                  0, 1,              0,
                    (float) -sin(y), 0, (float) cos(y)
            }, new int[] { dim, dim });

            final INDArray rotationZ = Nd4j.create(new float[] {
                    (float) cos(z), (float) -sin(z), 0,
                    (float) sin(z), (float)  cos(z), 0,
                                 0,               0, 1
            }, new int[] { dim, dim });

            INDArray rotated = Nd4j.matmul(rotationX, point);
            rotated = Nd4j.matmul(rotationY, rotated);
            rotated = Nd4j.matmul(rotationZ, rotated);

            return rotated;
        } else if (dim == 4) {
            double xy = angles.getColumn(0).getDouble(0);
            double xz = angles.getColumn(1).getDouble(0);
            double xw = angles.getColumn(2).getDouble(0);
            double yz = angles.getColumn(3).getDouble(0);
            double yw = angles.getColumn(4).getDouble(0);
            double zw = angles.getColumn(5).getDouble(0);

            final INDArray rotationXY = Nd4j.create(new float[] {
                    (float)  cos(xy), (float) sin(xy), 0, 0,
                    (float) -sin(xy), (float) cos(xy), 0, 0,
                                   0,               0, 1, 0,
                                   0,               0, 0, 1
            }, new int[] { dim, dim });

            final INDArray rotationXZ = Nd4j.create(new float[] {
                    (float) cos(xz), 0, (float) -sin(xz), 0,
                                  0, 1,                0, 0,
                    (float) sin(xz), 0, (float)  cos(xz), 0,
                                  0, 0,                0, 1
            }, new int[] { dim, dim });

            final INDArray rotationXW = Nd4j.create(new float[] {
                    (float)  cos(xw), 0, 0, (float) sin(xw),
                                   0, 1, 0,               0,
                                   0, 0, 1,               0,
                    (float) -sin(xw), 0, 0, (float) cos(xw)
            }, new int[] { dim, dim });

            final INDArray rotationYZ = Nd4j.create(new float[] {
                    1,                0,               0, 0,
                    0, (float)  cos(yz), (float) sin(yz), 0,
                    0, (float) -sin(yz), (float) cos(yz), 0,
                    0,                0,               0, 1
            }, new int[] { dim, dim });

            final INDArray rotationYW = Nd4j.create(new float[] {
                    1,               0, 0,                0,
                    0, (float) cos(yw), 0, (float) -sin(zw),
                    0,               0, 1,                0,
                    0, (float) sin(yw), 0, (float)  cos(yw)
            }, new int[] { dim, dim });

            final INDArray rotationZW = Nd4j.create(new float[] {
                    1, 0,               0,                0,
                    0, 1,               0,                0,
                    0, 0, (float) cos(zw), (float) -sin(zw),
                    0, 0, (float) sin(zw), (float)  cos(zw)
            }, new int[] { dim, dim });

            INDArray rotated = Nd4j.matmul(rotationXY, point);
            rotated = Nd4j.matmul(rotationXZ, rotated);
            rotated = Nd4j.matmul(rotationXW, rotated);
            rotated = Nd4j.matmul(rotationYZ, rotated);
            rotated = Nd4j.matmul(rotationYW, rotated);
            rotated = Nd4j.matmul(rotationZW, rotated);

            return rotated;
        }
        return point; // return unrotated point if something goes wrong
    }

    /**
     * Utility method for determining the number of dimensions involved in a rotation calculation based on the number of
     * angles required to fully define the rotation.
     * @param nangles the number of angles
     * @return the number of dimensions
     */
    public static int dimCountFromAngleCount(final int nangles) {
        int angleCount = 1;
        int progress = 2;
        int dim = 2;
        while (angleCount != nangles) {
            angleCount += progress++;

            // in case the number of angles given doesn't actually correspond to a dimension, use last good dimension
            // which essentially ignores any more angle numbers
            if (angleCount > nangles) {
                break;
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

    public static INDArray getPerspectiveProjection(final int ndimensions, final INDArray rotated) {
        // TODO: automate creation of these
        if (ndimensions == 3) {
            float w = 1 / (FRUSTUM_LENGTH_3D - rotated.getColumn(0).getFloat(ndimensions - 1));
            final INDArray projection2d = Nd4j.create(new float[] {
                    w, 0, 0,
                    0, w, 0
            }, new int[] { 2, 3 });
            return projection2d;
        } else if (ndimensions == 4) {
            float w = 1 / (FRUSTUM_LENGTH_4D - rotated.getColumn(0).getFloat(ndimensions - 1));
            final INDArray projection3d = Nd4j.create(new float[] {
                    w, 0, 0, 0,
                    0, w, 0, 0,
                    0, 0, w, 0
            }, new int[] { 3, 4 });
            return projection3d;
        }
        return getOrthographicPerspectiveProjection(ndimensions, rotated);
    }

    public static INDArray getOrthographicPerspectiveProjection(final int ndimensions, final INDArray rotated) {
        // TODO: automate creation of these
        if (ndimensions == 3) {
            final INDArray projection2d = Nd4j.create(new float[] {
                    1, 0, 0,
                    0, 1, 0
            }, new int[] { 2, 3 });
            return projection2d;
        } else if (ndimensions == 4) {
            final INDArray projection3d = Nd4j.create(new float[] {
                    1, 0, 0, 0,
                    0, 1, 0, 0,
                    0, 0, 1, 0
            }, new int[] { 3, 4 });
            return projection3d;
        }
        return null;
    }
}
