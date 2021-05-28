package com.higgs.da;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DrawableShape {
    private final int numDim;
    private INDArray points;
    private INDArray angles;

    private boolean[] anglesProgress;
    private double[] anglesProgressSpeed;

    private static float[] frustumLengths;

    private String[] projectionType;

    private final List<ChangeListener> angleListeners = new ArrayList<>();

    public DrawableShape(final int numDim) {
        this.numDim = numDim;
        this.initShape();
        this.initAngles();
        this.initProjections();
        this.initFrustumLengths();
    }

    private void initShape() {
        this.points = DrawableShape.getNDimensionalUnitShape(this.numDim);
    }

    private static INDArray getNDimensionalUnitShape(final int numDim) {
        final INDArray points = Nd4j.ones(numDim, (int) Math.round(Math.pow(2, numDim)));
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

    private void initAngles() {
        final int numAngles = DimensionalMatrixHelper.angleCountFromDimCount(this.numDim);
        this.angles = Nd4j.zeros(1, numAngles);

        this.anglesProgress = new boolean[numAngles];
        Arrays.fill(this.anglesProgress, true);

        this.anglesProgressSpeed = new double[numAngles];
        Arrays.fill(this.anglesProgressSpeed, Math.toRadians(45.0) / 60.0);
    }

    private void initProjections() {
        if (this.numDim < 3) {
            this.projectionType = new String[0];
        } else {
            this.projectionType = new String[this.numDim - 2];
            Arrays.fill(this.projectionType, DimensionalAnalysis.PERSPECTIVE);
        }
    }

    public void initFrustumLengths() {
        if (this.numDim < 3) {
            DrawableShape.frustumLengths = new float[0];
        } else {
            DrawableShape.frustumLengths = new float[this.numDim - 2];
            Arrays.fill(DrawableShape.frustumLengths, DimensionalMatrixHelper.DEFAULT_FRUSTUM_LENGTH);
        }
    }

    public void setAngles(final INDArray angles) {
        if (angles.rows() == this.angles.rows() && angles.columns() == this.angles.columns()) {
            this.angles = angles;
        }
    }

    public INDArray getAngles() {
        return this.angles;
    }

    public void setAngle(final int angleIndex, final double value) {
        if (angleIndex < this.angles.columns()) {
            this.angles.put(0, angleIndex, value);
        }
    }

    public double getAngle(final int angleIndex) {
        if (angleIndex < this.angles.columns()) {
            return this.angles.getDouble(new int[] { 0, angleIndex });
        }
        return 0.0;
    }

    public void setLength(final int axisIndex, final double value) {
        for (int i = 0; i < this.points.columns(); i++) {
            final double prevValue = this.points.getDouble(axisIndex, i);
            final int neg = (int) (prevValue / Math.abs(prevValue));

            this.points.put(axisIndex, i, (value / 2) * neg);
        }
    }

    public double getLength(final int axisIndex) {
        return this.points.getDouble(axisIndex, 0) * 2.0;
    }

    public void setPerspectiveLength(final int projectionIndex, final Double value) {
        if (projectionIndex >= DrawableShape.frustumLengths.length) return;
        DrawableShape.frustumLengths[projectionIndex] = (float) (double) value;
    }

    public void transformAngles(final INDArray transform) {
        Nd4j.matmul(this.angles, transform, this.angles);
    }

    public int getNumDimensions() {
        return this.numDim;
    }

    public int getNumAngles() {
        return this.angles.columns();
    }

    public void draw(final Graphics2D g2d) {
        final INDArray transformed = Nd4j.ones(2, this.points.columns());
        for (int column = 0; column < this.points.columns(); column++) {
            transformed.putColumn(column, DimensionalMatrixHelper.transform(this.angles,
                    this.points.getColumn(column, true),
                    this.projectionType,
                    DrawableShape.frustumLengths));
        }
        this.drawShape(g2d, transformed);
    }

    private void drawShape(final Graphics2D g2d, final INDArray transformed) {
        this.drawPoints(g2d, transformed);

        final INDArray firstHalf = Nd4j.create(transformed.rows(), transformed.columns() / 2);
        final INDArray secondHalf = Nd4j.create(transformed.rows(), transformed.columns() / 2);

        DimensionalMatrixHelper.bisectMatrixColumnWise(firstHalf, secondHalf, transformed);
        this.drawNDimShape(g2d, this.numDim, new INDArray[] { firstHalf, secondHalf });
    }

    private void drawPoints(final Graphics2D g2d, final INDArray transformed) {
        for (int i = 0; i < transformed.columns(); i++) {
            this.drawPoint(g2d, transformed.getColumn(i).getInt(0), transformed.getColumn(i).getInt(1));
        }
    }

    private void drawPoint(final Graphics2D g2d, final int x, final int y) {
        g2d.fillOval(x - (DimensionalAnalysis.getPointSize() / 2),
                y - (DimensionalAnalysis.getPointSize() / 2),
                DimensionalAnalysis.getPointSize(),
                DimensionalAnalysis.getPointSize());
    }

    private void drawNDimShape(final Graphics2D g2d, final int numDim, final INDArray[] shapes) {
        // draw connecting lines
        for (int i = 0; i < shapes[0].columns(); i++) {
            this.drawNDimLine(g2d, shapes[0].getColumn(i), shapes[1].getColumn(i));
        }

        for (final INDArray shape : shapes) {
            if (numDim == 1) {
                this.drawNDimLine(g2d, shapes[0].getColumn(0), shapes[1].getColumn(0));
            } else {
                final INDArray firstHalf = Nd4j.create(shape.rows(), shape.columns() / 2);
                final INDArray secondHalf = Nd4j.create(shape.rows(), shape.columns() / 2);
                DimensionalMatrixHelper.bisectMatrixColumnWise(firstHalf, secondHalf, shape);

                // draw component shapes
                this.drawNDimShape(g2d, numDim - 1, new INDArray[] { firstHalf, secondHalf });
            }
        }
    }

    private void drawNDimLine(final Graphics2D g2d, final INDArray p1, final INDArray p2) {
        final int x1 = (int) Math.round(p1.getDouble(0, 0));
        final int y1 = (int) Math.round(p1.getDouble(1, 0));
        final int x2 = (int) Math.round(p2.getDouble(0, 0));
        final int y2 = (int) Math.round(p2.getDouble(1, 0));
        g2d.drawLine(x1, y1, x2, y2);
    }

    /**
     * Called in DimensionalCanvas' logic loop.
     */
    public void update() {
        if (this.angleListeners.size() != this.angles.columns()) return;

        for (int i = 0; i < this.angles.columns(); i++) {
            if (this.anglesProgress[i]) {
                double angle = this.angles.getColumn(i).getDouble(0) + this.anglesProgressSpeed[i];

                if (angle > 2 * Math.PI) angle -= 2 * Math.PI;
                if (angle < 0) angle += 2 * Math.PI;

                this.angleListeners.get(i).stateChanged(new ChangeEvent(this));
                this.angles.put(0, i, angle);
            }
        }
    }

    public void setAngleProgress(final int angleIndex, final boolean selected) {
        this.anglesProgress[angleIndex] = selected;
    }

    public boolean getAngleProgress(final int angleIndex) {
        return this.anglesProgress[angleIndex];
    }

    public void setAngleProgressSpeed(final int angleIndex, final double speed) {
        this.anglesProgressSpeed[angleIndex] = speed;
    }

    public void addAngleChangeListener(final ChangeListener listener) {
        this.angleListeners.add(listener);
    }

    public void setProjection(final int projectionIndex, final String value) {
        this.projectionType[projectionIndex] = value;
    }
}