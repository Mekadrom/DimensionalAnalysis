package com.higgs.da.canvas;

import com.higgs.da.DimensionalAnalysis;
import com.higgs.da.DrawableShape;

import javax.swing.*;
import java.awt.*;

public class DimensionalControlPanel extends JPanel {
    private JSpinner _numDim;
    private JSlider _lineThickness;
    private JSlider _pointSize;
    private JSlider _drawScale;

    public DimensionalControlPanel(final Dimension size) {
        setSize(size);
        setPreferredSize(size);

        initSelf();

        initComponents();
        initActionListeners();
    }

    private void initSelf() {
        setLayout(new FlowLayout(FlowLayout.CENTER));
//        setLayout(new GridBagLayout()); // fuck grid bags
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    private void initComponents() {
        final JLabel dimLabel = new JLabel("# of dimensions:");
        _numDim = new JSpinner(new SpinnerNumberModel(2, 2, 26, 1));

        final JLabel lineThickLabel = new JLabel("Line Thickness:");
        _lineThickness = new JSlider(SwingConstants.HORIZONTAL, 1, 20, 1);

        final JLabel pointSizeLabel = new JLabel("Point Size:");
        _pointSize = new JSlider(SwingConstants.HORIZONTAL, 1, 20, 1);

        final JLabel drawScaleLabel = new JLabel("Draw Scale:");
        _drawScale = new JSlider(SwingConstants.HORIZONTAL, 1, 500, 100);

        add(dimLabel);
        add(_numDim);
        add(lineThickLabel);
        add(_lineThickness);
        add(pointSizeLabel);
        add(_pointSize);
        add(drawScaleLabel);
        add(_drawScale);
    }

    private void initActionListeners() {
        _numDim.addChangeListener(changeEvent -> {
            DimensionalAnalysis.stop();
            DimensionalAnalysis.setShape(new DrawableShape((int) _numDim.getValue()));
            DimensionalAnalysis.start();
        });

        _lineThickness.addChangeListener(changeEvent -> DimensionalAnalysis.setLineThickness(_lineThickness.getValue()));
        _pointSize.addChangeListener(changeEvent -> DimensionalAnalysis.setPointSize(_pointSize.getValue()));
        _drawScale.addChangeListener(changeEvent -> DimensionalAnalysis.setDrawScale(_drawScale.getValue()));
    }
}
