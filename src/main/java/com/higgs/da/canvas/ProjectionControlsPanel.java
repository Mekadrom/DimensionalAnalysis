package com.higgs.da.canvas;

import com.higgs.da.DimensionalAnalysis;
import com.higgs.da.DimensionalMatrixHelper;
import com.higgs.da.DrawableShape;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class ProjectionControlsPanel extends AttributeControlsPanel {
    public ProjectionControlsPanel(final Dimension dimension) {
        super("Projection", dimension);
    }

    public void setShape(final DrawableShape shape) {
        if (shape == null) return;

        int numDim = shape.getNumDimensions();

        int index = 0;

        for (int i = numDim; i > 2; i--) {
            _scrollablePanel.add(new ProjectionControlPanel(index++, i, i - 1));
        }
    }

    private static class ProjectionControlPanel extends AttributeControlPanel {
        private int _projectionIndex;

        private int _higherDim;
        private int _lowerDim;

        private JComboBox<String> _projectionType;
        private JSpinner _frustumLength;

        private static final List<String> _options = Arrays.asList(
            DimensionalAnalysis.PERSPECTIVE,
            DimensionalAnalysis.ORTHOGRAPHIC
        );

        public ProjectionControlPanel(final int projectionIndex, final int higherDim, final int lowerDim) {
            _projectionIndex = projectionIndex;

            _higherDim = higherDim;
            _lowerDim = lowerDim;

            init();
        }

        private void init() {
            setLayout(new BorderLayout());

            final JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            final JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

            final JLabel label = new JLabel("Projection from " + _higherDim + "D to " + _lowerDim + "D :");
            _projectionType = new JComboBox<>();

            for (final String projOpt : _options) {
                _projectionType.addItem(projOpt);
            }

            final JLabel frustumLabel = new JLabel("Projection Distance:");
            _frustumLength = new JSpinner(new SpinnerNumberModel(DimensionalMatrixHelper.DEFAULT_FRUSTUM_LENGTH, 0.1, 10.0, 0.1));

            topPanel.add(label);
            topPanel.add(_projectionType);

            bottomPanel.add(frustumLabel);
            bottomPanel.add(_frustumLength);

            setSize(100, 50);

            add(topPanel, BorderLayout.NORTH);
            add(bottomPanel, BorderLayout.CENTER);

            SwingUtilities.invokeLater(this::addListeners);
        }

        private void addListeners() {
            _projectionType.addActionListener(actionEvent -> {
                if (_projectionType.getSelectedObjects().length > 1) {
                    _projectionType.setSelectedItem(_projectionType.getSelectedObjects()[0]);
                }

                DimensionalAnalysis.setProjection(_projectionIndex, (String) _projectionType.getSelectedItem());
                revalidate();
                repaint();
            });

            _frustumLength.addChangeListener(changeEvent -> {
                DimensionalAnalysis.setPerspectiveLength(_projectionIndex, (Double) _frustumLength.getValue());
                revalidate();
                repaint();
            });
        }
    }
}
