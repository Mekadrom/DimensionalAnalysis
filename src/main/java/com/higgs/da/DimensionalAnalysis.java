package com.higgs.da;

import com.bulenkov.darcula.DarculaLaf;
import com.higgs.da.canvas.DimensionalCanvasFrame;

import javax.swing.*;

public class DimensionalAnalysis {
    public static final String ORTHOGRAPHIC = "Orthogonal";
    public static final String PERSPECTIVE = "Perspective";
    public static final String STEREOGRAPHIC = "Stereographic";

    private static DimensionalCanvasFrame _frame;

    private static int _lineThickness = 1;

    private static int _pointSize = 8;

    private static int _drawScale = 100;

    public static void main(final String[] args) {
        try {
            UIManager.setLookAndFeel(new DarculaLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        _frame = new DimensionalCanvasFrame();
        _frame.resetControls();
        start();
    }

    public static void start() {
        _frame.start();
    }

    public static void stop() {
        _frame.stop();
    }

    public static void setLineThickness(final int value) {
        _lineThickness = value;
    }

    public static int getLineThickness() {
        return _lineThickness;
    }

    public static void setPointSize(final int value) {
        _pointSize = value;
    }

    public static int getPointSize() {
        return _pointSize;
    }

    public static void setDrawScale(final int drawScale) {
        _drawScale = drawScale;
    }

    public static int getDrawScale() {
        return _drawScale;
    }

    public static void setShape(final DrawableShape shape) {
        _frame.setDrawableShape(shape);
        _frame.resetControls();

        DimensionalMatrixHelper.initFrustumLengths(shape.getNumDimensions());
    }

    public static void setAngle(final int angleIndex, final double value) {
        _frame.getDrawableShape().setAngle(angleIndex, value);
    }

    public static double getAngle(final int angleIndex) {
        return _frame.getDrawableShape().getAngle(angleIndex);
    }

    public static void setAngleProgress(final int angleIndex, final boolean selected) {
        _frame.getDrawableShape().setAngleProgress(angleIndex, selected);
    }

    public static void setAngleProgressSpeed(final int angleIndex, final double speed) {
        _frame.getDrawableShape().setAngleProgressSpeed(angleIndex, speed);
    }

    public static DrawableShape getShape() {
        return _frame.getDrawableShape();
    }

    public static void setLength(final int axisIndex, final double value) {
        _frame.getDrawableShape().setLength(axisIndex, value);
    }

    public static void setProjection(final int projectionIndex, final String value) {
        _frame.getDrawableShape().setProjection(projectionIndex, value);
    }
}
