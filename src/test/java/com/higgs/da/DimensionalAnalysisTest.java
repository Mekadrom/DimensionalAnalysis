package com.higgs.da;

import com.higgs.da.canvas.DimensionalCanvasFrame;

import java.awt.*;

public class DimensionalAnalysisTest {
    public static void main(final String[] args) {
        final Dimension size = new Dimension(640, 640);

        final DimensionalCanvasFrame canvas = new DimensionalCanvasFrame(size);

        final DimensionalAnalysis analysis = new DimensionalAnalysis(4, size);

        canvas.addDrawables(analysis.getShapes());
        canvas.start();
    }
}