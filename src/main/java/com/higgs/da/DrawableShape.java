package com.higgs.da;

import com.higgs.da.canvas.AngleChangeListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DrawableShape {
    private int _ndimensions;
    private INDArray _points;
    private INDArray _angles;

    private boolean[] _anglesProgress;
    private double[] _anglesProgressSpeed;
    private List<ChangeListener> _angleListeners = new ArrayList<>();

    public DrawableShape(final int ndimensions) {
        _ndimensions = ndimensions;
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
        _points.mul(0.5); // create shape with side lengths equal to 1 rather than 2
    }

    private void initAngles() {
        final int numAngles = DimensionalMatrixHelper.angleCountFromDimCount(_ndimensions);
        _angles = Nd4j.zeros(new int[] { 1, numAngles });

        _anglesProgress = new boolean[numAngles];
        Arrays.fill(_anglesProgress, true);

        _anglesProgressSpeed = new double[numAngles];
        Arrays.fill(_anglesProgressSpeed, 0.01);
    }

    public void setAngles(final INDArray angles) {
        if (angles.rows() == _angles.rows() && angles.columns() == _angles.columns()) {
            _angles = angles;
        }
    }

    public INDArray getAngles() {
        return _angles;
    }

    public void setAngle(final int angleIndex, final double value) {
        if (angleIndex < _angles.columns()) {
            _angles.put(0, angleIndex, value);
        }
    }

    public double getAngle(final int angleIndex) {
        if (angleIndex < _angles.columns()) {
            return _angles.getDouble(new int[] { 0, angleIndex });
        }
        return 0.0;
    }

    public void transformAngles(final INDArray transform) {
        Nd4j.matmul(_angles, transform, _angles);
    }

    public int getNumAngles() {
        return _angles.columns();
    }

    /**
     * Draw method called by the DimensionalCanvas' draw loop
     * @param g2d the graphics object to draw on
     */
    public void draw(final Graphics2D g2d) {
        if (_ndimensions == 2) {
            final INDArray transformed = Nd4j.ones(new int[] { 2, _points.columns() });
            for (int column = 0; column < _points.columns(); column++) {
                final INDArray point = _points.getColumn(column, true); // the current point
                INDArray rotated = DimensionalMatrixHelper.rotate(_angles, point); // the point rotated about the angles

                // don't need to project into 2d space

                rotated = rotated.mul(DimensionalAnalysis.getDrawScale()); // scale drawing by draw scale

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
                projected2d = projected2d.mul(DimensionalAnalysis.getDrawScale()); // scale drawing by draw scale

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

                projected2d = projected2d.mul(DimensionalAnalysis.getDrawScale()); // scale drawing by draw scale

                transformed.putColumn(column, projected2d);
            }

            // draw points
            for (int i = 0; i < transformed.columns(); i++) {
                drawPoint2d(g2d, transformed.getColumn(i).getInt(0), transformed.getColumn(i).getInt(1));
            }


            for (int j = 0; j < 2; j++) {
                for (int i = 0; i < transformed.columns() / 4; i++) {
                    drawLine(g2d, j * 8, i, (i + 1) % 4, transformed); // draw back square lines
                    drawLine(g2d, j * 8, i + 4, ((i + 1) % 4) + 4, transformed); // draw front square lines
                    drawLine(g2d, j * 8, i, i + 4, transformed); // draw connecting lines
                }
            }

            for (int i = 0; i < transformed.columns() / 2; i++) {
                drawLine(g2d, 0, i, i + 8, transformed);
            }
        }
    }

    private void drawPoint2d(final Graphics2D g2d, final int x, final int y) {
        g2d.fillOval(x - (DimensionalAnalysis.getPointSize() / 2),
                    y - (DimensionalAnalysis.getPointSize() / 2),
                        DimensionalAnalysis.getPointSize(),
                        DimensionalAnalysis.getPointSize());
    }

    void drawLine(final Graphics2D g2d, final int offset, int p1, int p2, final INDArray points) {
        g2d.drawLine(points.getColumn(p1 + offset).getInt(0), points.getColumn(p1 + offset).getInt(1),
                     points.getColumn(p2 + offset).getInt(0), points.getColumn(p2 + offset).getInt(1));
    }

    /**
     * Called in DimensionalCanvas' logic loop.
     */
    public void update() {
        double[] angles = new double[_angles.columns()];
        for (int column = 0; column < _angles.columns(); column++) {
            angles[column] = _angles.getColumn(column).getDouble(0);
        }

        for (int angle = 0; angle < angles.length; angle++) {
            if (_anglesProgress[angle]) {
                angles[angle] += _anglesProgressSpeed[angle];

                if (angles[angle] > 2 * Math.PI) angles[angle] -= 2 * Math.PI;
                if (angles[angle] < 0) angles[angle] += 2 * Math.PI;

                _angleListeners.get(angle).stateChanged(new ChangeEvent(this));
            }
            _angles.put(0, angle, angles[angle]);
        }
    }

    public void setAngleProgress(final int angleIndex, final boolean selected) {
        _anglesProgress[angleIndex] = selected;
    }

    public void setAngleProgressSpeed(final int angleIndex, final double speed) {
        _anglesProgressSpeed[angleIndex] = speed;
    }

    public void addAngleChangeListener(final ChangeListener listener) {
        _angleListeners.add(listener);
    }
}