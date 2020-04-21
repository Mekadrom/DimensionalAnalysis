package com.higgs.da.canvas;

import com.higgs.da.DimensionalAnalysis;
import com.higgs.da.DrawableShape;

import javax.swing.*;
import java.awt.*;

public class DimensionalControlPanel extends JPanel {
    private JSlider _dimSlider;
    private JSlider _lineThickSlider;
    private JSlider _pointSizeSlider;
    private JSlider _drawScaleSlider;

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
        _dimSlider = new JSlider(SwingConstants.HORIZONTAL, 2, 4, 2);

        final JLabel lineThickLabel = new JLabel("Line Thickness:");
        _lineThickSlider = new JSlider(SwingConstants.HORIZONTAL, 1, 20, 1);

        final JLabel pointSizeLabel = new JLabel("Point Size:");
        _pointSizeSlider = new JSlider(SwingConstants.HORIZONTAL, 1, 20, 8);

        final JLabel drawScaleLabel = new JLabel("Draw Scale:");
        _drawScaleSlider = new JSlider(SwingConstants.HORIZONTAL, 1, 500, 100);

        add(dimLabel);
        add(_dimSlider);
        add(lineThickLabel);
        add(_lineThickSlider);
        add(pointSizeLabel);
        add(_pointSizeSlider);
        add(drawScaleLabel);
        add(_drawScaleSlider);
    }

    private void initActionListeners() {
        _dimSlider.addChangeListener(changeEvent -> {
            final JSlider source = (JSlider)changeEvent.getSource();
            if (!source.getValueIsAdjusting()) {
                DimensionalAnalysis.stop();
                DimensionalAnalysis.setShape(new DrawableShape(_dimSlider.getValue()));
                DimensionalAnalysis.start();
            }
        });

        _lineThickSlider.addChangeListener(changeEvent -> DimensionalAnalysis.setLineThickness(_lineThickSlider.getValue()));
        _pointSizeSlider.addChangeListener(changeEvent -> DimensionalAnalysis.setPointSize(_pointSizeSlider.getValue()));
        _drawScaleSlider.addChangeListener(changeEvent -> DimensionalAnalysis.setDrawScale(_drawScaleSlider.getValue()));
    }
}
