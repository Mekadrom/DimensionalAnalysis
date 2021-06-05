package com.higgs.da;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.higgs.da.canvas.DimensionalCanvasFrame;

public final class DimensionalAnalysis {
    public static final String ORTHOGRAPHIC = "Orthographic";
    public static final String PERSPECTIVE = "Perspective";

    private static DimensionalCanvasFrame frame;

    private static int lineThickness = 1;

    private static int pointSize = 1;

    private static int drawScale = 100;

    private static final int MAX_DIMENSIONS = DimensionalMatrixHelper.AXES_ORDER.length();

    public static void main(final String[] args) {
        FlatDarculaLaf.setup();
        DimensionalAnalysis.frame = new DimensionalCanvasFrame();
        DimensionalAnalysis.frame.resetControls();
        DimensionalAnalysis.start();
    }

    public static void start() {
        DimensionalAnalysis.frame.start();
    }

    public static void stop() {
        DimensionalAnalysis.frame.stop();
    }

    public static void setLineThickness(final int value) {
        DimensionalAnalysis.lineThickness = value;
    }

    public static int getLineThickness() {
        return DimensionalAnalysis.lineThickness;
    }

    public static void setPointSize(final int value) {
        DimensionalAnalysis.pointSize = value;
    }

    public static int getPointSize() {
        return DimensionalAnalysis.pointSize;
    }

    public static void setDrawScale(final int drawScale) {
        DimensionalAnalysis.drawScale = drawScale;
    }

    public static int getDrawScale() {
        return DimensionalAnalysis.drawScale;
    }

    public static void setShape(final DrawableShape shape) {
        DimensionalAnalysis.frame.setDrawableShape(shape);
        DimensionalAnalysis.frame.resetControls();
    }

    public static void setAngle(final int angleIndex, final double value) {
        DimensionalAnalysis.frame.getDrawableShape().setAngle(angleIndex, value);
    }

    public static double getAngle(final int angleIndex) {
        return DimensionalAnalysis.frame.getDrawableShape().getAngle(angleIndex);
    }

    public static void setAngleProgress(final int angleIndex, final boolean selected) {
        DimensionalAnalysis.frame.getDrawableShape().setAngleProgress(angleIndex, selected);
    }

    public static void setAngleProgressSpeed(final int angleIndex, final double speed) {
        DimensionalAnalysis.frame.getDrawableShape().setAngleProgressSpeed(angleIndex, speed);
    }

    public static DrawableShape getShape() {
        return DimensionalAnalysis.frame.getDrawableShape();
    }

    public static void setLength(final int axisIndex, final double value) {
        DimensionalAnalysis.frame.getDrawableShape().setLength(axisIndex, value);
    }

    public static void setProjection(final int projectionIndex, final String value) {
        DimensionalAnalysis.frame.getDrawableShape().setProjection(projectionIndex, value);
    }

    public static void setPerspectiveLength(final int projectionIndex, final Double value) {
        DimensionalAnalysis.frame.getDrawableShape().setPerspectiveLength(projectionIndex, value);
    }

    public static boolean getAngleProgress(final int angleIndex) {
        return DimensionalAnalysis.frame.getDrawableShape().getAngleProgress(angleIndex);
    }
}
