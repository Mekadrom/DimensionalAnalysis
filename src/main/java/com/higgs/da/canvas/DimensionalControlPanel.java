package com.higgs.da.canvas;

import com.higgs.da.DimensionalAnalysis;
import com.higgs.da.DimensionalMatrixHelper;
import com.higgs.da.DrawableShape;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class DimensionalControlPanel extends JPanel {
    private JSpinner numDim;
    private JSlider lineThickness;
    private JSlider pointSize;
    private JSlider drawScale;

    public DimensionalControlPanel(final Dimension size) {
        this.setSize(size);
        this.setPreferredSize(size);

        this.initSelf();

        this.initComponents();
        this.initActionListeners();
    }

    private void initSelf() {
        this.setLayout(new FlowLayout(FlowLayout.CENTER));
//        setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    private void initComponents() {
        final JLabel dimLabel = new JLabel("# of dimensions:");
        this.numDim = new JSpinner(new SpinnerNumberModel(2, 2, 26, 1));

        final JLabel lineThickLabel = new JLabel("Line Thickness:");
        this.lineThickness = new JSlider(SwingConstants.HORIZONTAL, 1, 20, 1);

        final JLabel pointSizeLabel = new JLabel("Point Size:");
        this.pointSize = new JSlider(SwingConstants.HORIZONTAL, 1, 20, 1);

        final JLabel drawScaleLabel = new JLabel("Draw Scale:");
        this.drawScale = new JSlider(SwingConstants.HORIZONTAL, 1, 500, 100);

        this.add(dimLabel);
        this.add(this.numDim);
        this.add(lineThickLabel);
        this.add(this.lineThickness);
        this.add(pointSizeLabel);
        this.add(this.pointSize);
        this.add(drawScaleLabel);
        this.add(this.drawScale);
    }

    private void initActionListeners() {
        this.numDim.addChangeListener(changeEvent -> {
            DimensionalAnalysis.stop();
            DimensionalMatrixHelper.preload((int) this.numDim.getValue());
            DimensionalAnalysis.setShape(new DrawableShape((int) this.numDim.getValue()));
            DimensionalAnalysis.start();
        });
        this.lineThickness.addChangeListener(changeEvent -> DimensionalAnalysis.setLineThickness(this.lineThickness.getValue()));
        this.pointSize.addChangeListener(changeEvent -> DimensionalAnalysis.setPointSize(this.pointSize.getValue()));
        this.drawScale.addChangeListener(changeEvent -> DimensionalAnalysis.setDrawScale(this.drawScale.getValue()));
    }
}
