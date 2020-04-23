package com.higgs.da;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DrawableShape {
    private final int _numDim;
    private INDArray _points;
    private INDArray _angles;

    private boolean[] _anglesProgress;
    private double[] _anglesProgressSpeed;

    private static float[] _frustumLengths;

    private String[] _projectionType;

    private final List<ChangeListener> _angleListeners = new ArrayList<>();

    public DrawableShape(final int numDim) {
        _numDim = numDim;
        initShape();
        initAngles();
        initProjections();
        initFrustumLengths();
    }

    private void initShape() {
        _points = getNDimensionalUnitShape(_numDim);
    }

    private static INDArray getNDimensionalUnitShape(final int numDim) {
        final INDArray points = Nd4j.ones(new int [] { numDim, (int) Math.round(Math.pow(2, numDim)) });

        for (int i = 0; i < points.rows(); i++) {
            boolean neg = true;
            for (int j = 0; j < points.columns(); j++) {
                points.put(i, j, neg ? -0.5 : 0.5);
                if ((j + 1) % (Math.pow(2, i)) == 0) {
                    neg = !neg;
                }
            }
        }
        return points;
    }

    /**
     *             _points = Nd4j.create(new float[] {
     *                     -1, 1,
     *                     -1, -1
     *             }, new int[] { 2, 2 });
     *             _points = Nd4j.create(new float[] {
     *                     -1,  1, 1, -1, // x
     *                     -1, -1, 1,  1  // y
     *             }, new int[] { 2, 4 });
     *             _points = Nd4j.create(new float[] {
     *                     -1,  1,  1, -1, -1,  1, 1, -1, // x
     *                     -1, -1,  1,  1, -1, -1, 1,  1, // y
     *                     -1, -1, -1, -1,  1,  1, 1,  1  // z
     *             }, new int[] { 3, 8 });
     *             _points = Nd4j.create(new float[] {
     *                     -1,  1,  1, -1, -1,  1,  1, -1, -1,  1,  1, -1, -1,  1, 1, -1, // x
     *                     -1, -1,  1,  1, -1, -1,  1,  1, -1, -1,  1,  1, -1, -1, 1,  1, // y
     *                     -1, -1, -1, -1,  1,  1,  1,  1, -1, -1, -1, -1,  1,  1, 1,  1, // z
     *                     -1, -1, -1, -1, -1, -1, -1, -1,  1,  1,  1,  1,  1,  1, 1,  1  // w
     *             }, new int[] { 4, 16 });
     */

    private void initAngles() {
        final int numAngles = DimensionalMatrixHelper.angleCountFromDimCount(_numDim);
        _angles = Nd4j.zeros(new int[] { 1, numAngles });

        _anglesProgress = new boolean[numAngles];
        Arrays.fill(_anglesProgress, true);

        _anglesProgressSpeed = new double[numAngles];
        Arrays.fill(_anglesProgressSpeed, Math.toRadians(45.0) / 60.0);
    }

    private void initProjections() {
        if (_numDim < 3) {
            _projectionType = new String[0];
        } else {
            _projectionType = new String[_numDim - 2];
            Arrays.fill(_projectionType, DimensionalAnalysis.PERSPECTIVE);
        }
    }

    public void initFrustumLengths() {
        if (_numDim < 3) {
            _frustumLengths = new float[0];
        } else {
            _frustumLengths = new float[_numDim - 2];
            Arrays.fill(_frustumLengths, DimensionalMatrixHelper.DEFAULT_FRUSTUM_LENGTH);
        }
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

    public void setLength(final int axisIndex, final double value) {
        for (int i = 0; i < _points.columns(); i++) {
            final double prevValue = _points.getDouble(axisIndex, i);
            final int neg = (int) (prevValue / Math.abs(prevValue));

            _points.put(axisIndex, i, (value / 2) * neg);
        }
    }

    public double getLength(final int axisIndex) {
        return _points.getDouble(axisIndex, 0) * 2.0;
    }

    public void setPerspectiveLength(final int projectionIndex, final Double value) {
        if (projectionIndex >= _frustumLengths.length) return;
        _frustumLengths[projectionIndex] = (float)(double) value;
    }

    public void transformAngles(final INDArray transform) {
        Nd4j.matmul(_angles, transform, _angles);
    }

    public int getNumDimensions() {
        return _numDim;
    }

    public int getNumAngles() {
        return _angles.columns();
    }

    public void draw(final Graphics2D g2d) {
        final INDArray transformed = Nd4j.ones(new int[] { 2, _points.columns() });
        for (int column = 0; column < _points.columns(); column++) {
            transformed.putColumn(column, DimensionalMatrixHelper.transform(_angles,
                    _points.getColumn(column, true),
                    _projectionType,
                    _frustumLengths));
        }
        drawShape(g2d, transformed);
    }

    private void drawShape(final Graphics2D g2d, final INDArray transformed) {
        drawPoints(g2d, transformed);
        drawNDimShape(g2d, _numDim, transformed);
    }

    private void drawPoints(final Graphics2D g2d, final INDArray transformed) {
        for (int i = 0; i < transformed.columns(); i++) {
            drawPoint(g2d, transformed.getColumn(i).getInt(0), transformed.getColumn(i).getInt(1));
        }
    }

    private void drawPoint(final Graphics2D g2d, final int x, final int y) {
        g2d.fillOval(x - (DimensionalAnalysis.getPointSize() / 2),
                    y - (DimensionalAnalysis.getPointSize() / 2),
                        DimensionalAnalysis.getPointSize(),
                        DimensionalAnalysis.getPointSize());
    }

    private void drawNDimShape(final Graphics2D g2d, final int numDim, final INDArray points) {
        if (numDim == 1) {
            drawNDimLine(g2d, points.getColumn(0), points.getColumn(1));
        } else {
            final INDArray firstHalf = Nd4j.create(new int[] { points.rows(), points.columns() / 2 });
            final INDArray secondHalf = Nd4j.create(new int[] { points.rows(), points.columns() / 2 });

            DimensionalMatrixHelper.bisectMatrixColumnWise(firstHalf, secondHalf, points);

            drawNDimShape(g2d, numDim - 1, firstHalf);
            drawNDimShape(g2d, numDim - 1, secondHalf);

            // draw connecting lines
            for (int i = 0; i < points.columns() / 2; i++) {
                drawNDimLine(g2d, points.getColumn(i), points.getColumn((int) Math.round(i + Math.pow(2, numDim - 1))));
            }
        }
    }

    private void drawNDimLine(final Graphics2D g2d, final INDArray p1, final INDArray p2) {
        int x1 = (int) Math.round(p1.getDouble(0, 0));
        int y1 = (int) Math.round(p1.getDouble(1, 0));
        int x2 = (int) Math.round(p2.getDouble(0, 0));
        int y2 = (int) Math.round(p2.getDouble(1, 0));

        g2d.drawLine(x1, y1, x2, y2);
    }

    /**
     * Called in DimensionalCanvas' logic loop.
     */
    public void update() {
        if (_angleListeners.size() != _angles.columns()) return;

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

    public boolean getAngleProgress(final int angleIndex) {
        return _anglesProgress[angleIndex];
    }

    public void setAngleProgressSpeed(final int angleIndex, final double speed) {
        _anglesProgressSpeed[angleIndex] = speed;
    }

    public void addAngleChangeListener(final ChangeListener listener) {
        _angleListeners.add(listener);
    }

    public void setProjection(final int projectionIndex, final String value) {
        _projectionType[projectionIndex] = value;
    }
}