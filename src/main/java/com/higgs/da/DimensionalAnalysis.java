package com.higgs.da;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DimensionalAnalysis {
    private List<Shape> _shapes = new ArrayList<>();

    public DimensionalAnalysis(final int ndimensions, final Dimension sizeCanvas) {
        _shapes.add(Shape.getShapeForDim(ndimensions, sizeCanvas));
    }

    public List<Shape> getShapes() {
        return _shapes;
    }
}
