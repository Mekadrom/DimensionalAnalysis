package com.higgs.da;

import com.bulenkov.darcula.DarculaLaf;
import com.higgs.da.canvas.DimensionalCanvasFrame;

import javax.swing.*;

public class DimensionalAnalysis {
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
    }
}
