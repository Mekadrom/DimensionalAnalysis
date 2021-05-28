package com.higgs.da.canvas;

import com.higgs.da.DimensionalAnalysis;
import com.higgs.da.DimensionalMatrixHelper;
import com.higgs.da.DrawableShape;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Arrays;
import java.util.List;

public class ProjectionControlsPanel extends AttributeControlsPanel {
    public ProjectionControlsPanel(final Dimension dimension) {
        super("Projection", dimension);
    }

    public void setShape(final DrawableShape shape) {
        if (shape == null) {
            return;
        }

        final int numDim = shape.getNumDimensions();

        int index = 0;
        for (int i = numDim; i > 2; i--) {
            this.scrollablePanel.add(new ProjectionControlPanel(index++, i, i - 1));
        }
    }

    private static class ProjectionControlPanel extends AttributeControlPanel {
        private final int _projectionIndex;

        private final int _higherDim;
        private final int _lowerDim;

        private JComboBox<String> _projectionType;
        private JSpinner _frustumLength;

        private static final List<String> options = Arrays.asList(
                DimensionalAnalysis.PERSPECTIVE,
                DimensionalAnalysis.ORTHOGRAPHIC
        );

        public ProjectionControlPanel(final int projectionIndex, final int higherDim, final int lowerDim) {
            this._projectionIndex = projectionIndex;

            this._higherDim = higherDim;
            this._lowerDim = lowerDim;

            this.init();
        }

        private void init() {
            this.setLayout(new BorderLayout());

            final JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            final JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

            final JLabel label = new JLabel("Projection from " + this._higherDim + "D to " + this._lowerDim + "D :");
            this._projectionType = new JComboBox<>();

            for (final String projOpt : ProjectionControlPanel.options) {
                this._projectionType.addItem(projOpt);
            }

            final JLabel frustumLabel = new JLabel("Projection Distance:");
            this._frustumLength = new JSpinner(new SpinnerNumberModel(DimensionalMatrixHelper.DEFAULT_FRUSTUM_LENGTH, 0.1, 10.0, 0.1));

            topPanel.add(label);
            topPanel.add(this._projectionType);

            bottomPanel.add(frustumLabel);
            bottomPanel.add(this._frustumLength);

            this.setSize(200, 50);

            this.add(topPanel, BorderLayout.NORTH);
            this.add(bottomPanel, BorderLayout.CENTER);

            SwingUtilities.invokeLater(this::addListeners);
        }

        private void addListeners() {
            this._projectionType.addActionListener(actionEvent -> {
                if (this._projectionType.getSelectedObjects().length > 1) {
                    this._projectionType.setSelectedItem(this._projectionType.getSelectedObjects()[0]);
                }

                DimensionalAnalysis.setProjection(this._projectionIndex, (String) this._projectionType.getSelectedItem());
                this.revalidate();
                this.repaint();
            });

            this._frustumLength.addChangeListener(changeEvent -> {
                DimensionalAnalysis.setPerspectiveLength(this._projectionIndex, (Double) this._frustumLength.getValue());
                this.revalidate();
                this.repaint();
            });
        }
    }
}
