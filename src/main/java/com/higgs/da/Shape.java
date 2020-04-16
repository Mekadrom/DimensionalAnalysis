package com.higgs.da;

import com.higgs.da.canvas.Drawable;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.awt.*;

public class Shape implements Drawable {
    private static final int POINT_DIAMETER =   8;
    private static final int DRAW_SCALE     = 100;

    private int _xTranslate;
    private int _yTranslate;

    public static Shape getShapeForDim(final int ndimensions, final Dimension sizeCanvas) {
        return new Shape(ndimensions, sizeCanvas);
    }

    private int _ndimensions;
    private Dimension _size;
    private INDArray _points;
    private INDArray _angles;

    public Shape(final int ndimensions, final Dimension sizeCanvas) {
        _ndimensions = ndimensions;
        _size = sizeCanvas;
        _xTranslate = (int) ((_size.getWidth() / 2) - (DRAW_SCALE / (DimensionalMatrixHelper.FRUSTUM_LENGTH_3D * 2)));
        _yTranslate = (int) ((_size.getHeight() / 2) - (DRAW_SCALE / (DimensionalMatrixHelper.FRUSTUM_LENGTH_3D * 2)));
        initPoints();
        initAngles();
    }

    /**
     * Initializes the p
     */
    private void initPoints() {
        // TODO: automate this
        if (_ndimensions == 1) {
            // line
            _points = Nd4j.create(new float[] {
                    -1, 1,
                    -1, -1
            }, new int[] { 2, 2 });
        } else if (_ndimensions == 2) {
            // square (two lines connected)
            _points = Nd4j.create(new float[] {
                    -1,  1, 1, -1, // x
                    -1, -1, 1,  1  // y
            }, new int[] { 2, 4 });
        } else if (_ndimensions == 3) {
            // cube (two squares connected)
            _points = Nd4j.create(new float[] {
                    -1,  1,  1, -1, -1,  1, 1, -1, // x
                    -1, -1,  1,  1, -1, -1, 1,  1, // y
                    -1, -1, -1, -1,  1,  1, 1,  1  // z
            }, new int[] { 3, 8 });
        } else if (_ndimensions == 4) {
            // tesseract (two cubes connected)
            _points = Nd4j.create(new float[] {
                    -1,  1,  1, -1, -1,  1,  1, -1, -1,  1,  1, -1, -1,  1, 1, -1, // x
                    -1, -1,  1,  1, -1, -1,  1,  1, -1, -1,  1,  1, -1, -1, 1,  1, // y
                    -1, -1, -1, -1,  1,  1,  1,  1, -1, -1, -1, -1,  1,  1, 1,  1, // z
                    -1, -1, -1, -1, -1, -1, -1, -1,  1,  1,  1,  1,  1,  1, 1,  1  // w
            }, new int[] { 4, 16 });
        }
    }

    private void initAngles() {
        final int numAngles = DimensionalMatrixHelper.angleCountFromDimCount(_ndimensions);
        _angles = Nd4j.zeros(new int[] { 1, numAngles });
    }

    /**
     * Draw method called by the DimensionalCanvas' draw loop
     * @param g2d the graphics object to draw on
     */
    @Override
    public void draw(final Graphics2D g2d) {
        if (_ndimensions == 2) {
            final INDArray transformed = Nd4j.ones(new int[] { 2, _points.columns() });
            for (int column = 0; column < _points.columns(); column++) {
                final INDArray point = _points.getColumn(column, true); // the current point
                INDArray rotated = DimensionalMatrixHelper.rotate(_angles, point); // the point rotated about the angles

                // don't need to project into 2d space

                rotated = rotated.mul(DRAW_SCALE); // scale drawing by draw scale

                transformed.putColumn(column, rotated);
            }

            // draw points
            for (int i = 0; i < transformed.columns(); i++) {
                drawPoint2d(g2d, transformed.getColumn(i).getInt(0), transformed.getColumn(i).getInt(1));
            }

            // draw lines
            for (int i = 0; i < transformed.columns(); i++) {
                drawLine(g2d, 0, i, (i + 1) % 4, transformed);
            }
        } else if (_ndimensions == 3) {
            final INDArray transformed = Nd4j.ones(new int[] { 2, _points.columns() });
            for (int column = 0; column < _points.columns(); column++) {
                final INDArray point = _points.getColumn(column, true); // the current point
                final INDArray rotated = DimensionalMatrixHelper.rotate(_angles, point); // the point rotated about the angles

                INDArray projected2d = Nd4j.matmul(DimensionalMatrixHelper.getPerspectiveProjection(_ndimensions, rotated), rotated); // orthographic projection from 3d to 2d
                projected2d = projected2d.mul(DRAW_SCALE); // scale drawing by draw scale

                transformed.putColumn(column, projected2d);
            }

            // draw points
            for (int i = 0; i < transformed.columns(); i++) {
                drawPoint2d(g2d, transformed.getColumn(i).getInt(0), transformed.getColumn(i).getInt(1));
            }

            // draw lines
            for (int i = 0; i < transformed.columns() / 2; i++) {
                drawLine(g2d, 0, i, (i + 1) % 4, transformed); // draw back square lines
                drawLine(g2d, 0, i + 4, ((i + 1) % 4) + 4, transformed); // draw front square lines
                drawLine(g2d, 0, i, i + 4, transformed); // draw connecting lines
            }
        } else if (_ndimensions == 4) {
            final INDArray transformed = Nd4j.ones(new int[] { 2, _points.columns() });
            for (int column = 0; column < _points.columns(); column++) {
                final INDArray point = _points.getColumn(column, true); // the current point
                final INDArray rotated = DimensionalMatrixHelper.rotate(_angles, point); // the point rotated about the angles

//                INDArray projected3d = Nd4j.matmul(PROJECTION_3D, rotated); // "orthographic" projection from 4d to 3d

                INDArray projected3d = Nd4j.matmul(DimensionalMatrixHelper.getOrthographicPerspectiveProjection(_ndimensions, rotated), rotated);
                INDArray projected2d = Nd4j.matmul(DimensionalMatrixHelper.getOrthographicPerspectiveProjection(_ndimensions - 1, projected3d), projected3d); // orthographic projection from 3d to 2d

                projected2d = projected2d.mul(DRAW_SCALE); // scale drawing by draw scale

                transformed.putColumn(column, projected2d);
            }

            // draw points
            for (int i = 0; i < transformed.columns(); i++) {
                drawPoint2d(g2d, transformed.getColumn(i).getInt(0), transformed.getColumn(i).getInt(1));
            }

            for (int i = 0; i < transformed.columns() / 4; i++) {
                drawLine(g2d, 0, i, (i + 1) % 4, transformed); // draw back square lines
                drawLine(g2d, 0, i + 4, ((i + 1) % 4) + 4, transformed); // draw front square lines
                drawLine(g2d, 0, i, i + 4, transformed); // draw connecting lines
            }

            for (int i = 0; i < transformed.columns() / 4; i++) {
                drawLine(g2d, 8, i, (i + 1) % 4, transformed); // draw back square lines
                drawLine(g2d, 8, i + 4, ((i + 1) % 4) + 4, transformed); // draw front square lines
                drawLine(g2d, 8, i, i + 4, transformed); // draw connecting lines
            }

            for (int i = 0; i < transformed.columns() / 2; i++) {
                drawLine(g2d, 0, i, i + 8, transformed);
            }
        }
    }

    private void drawPoint2d(final Graphics2D g2d, final int x, final int y) {
        g2d.translate(_xTranslate, _yTranslate);
        g2d.fillOval(x - (POINT_DIAMETER / 2), y - (POINT_DIAMETER / 2), POINT_DIAMETER, POINT_DIAMETER);
        g2d.translate(-_xTranslate, -_yTranslate);
    }

    void drawLine(final Graphics2D g2d, final int offset, int p1, int p2, final INDArray points) {
        g2d.translate(_xTranslate, _yTranslate); // move to center of canvas
        g2d.drawLine(points.getColumn(p1 + offset).getInt(0), points.getColumn(p1 + offset).getInt(1),
                     points.getColumn(p2 + offset).getInt(0), points.getColumn(p2 + offset).getInt(1));
        g2d.translate(-_xTranslate, -_yTranslate); // move back to origin
    }

    /**
     * Called in DimensionalCanvas' logic loop.
     */
    public void update() {
        double[] angles = new double[_angles.columns()];
        for (int column = 0; column < _angles.columns(); column++) {
            angles[column] = _angles.getColumn(column).getDouble(0);
        }

//        angles[0] += 0.01;
        angles[1] = Math.PI / 4;
        angles[2] = Math.PI / 4;
        angles[3] = Math.PI / 4;
        angles[4] = Math.PI / 4;
        angles[5] += 0.01;

        for (int angle = 0; angle < angles.length; angle++) {
//            angles[angle] += 0.01;
            _angles.put(0, angle, angles[angle]);
        }
    }
}