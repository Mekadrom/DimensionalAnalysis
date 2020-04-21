package com.higgs.da.canvas;

import com.higgs.da.DimensionalAnalysis;
import com.higgs.da.DimensionalMatrixHelper;
import com.higgs.da.DrawableShape;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class LengthControlsPanel extends AttributeControlsPanel {
    public LengthControlsPanel(final Dimension dimension) {
        super("Length", dimension);
    }

    public void setShape(final DrawableShape shape) {
        if (shape == null) return;

        int numDim = shape.getNumDimensions();

        final char[] dimChars = Arrays.copyOfRange(DimensionalMatrixHelper.AXES_ORDER, 0, numDim);

        for (int i = 0; i < numDim; i++) {
            _scrollPanel.add(new LengthControlPanel(i, dimChars[i]));
        }
    }

    private static class LengthControlPanel extends AttributeControlPanel {
        private int _axisIndex;
        private char _dimChar;

        private JSpinner _length;

        public LengthControlPanel(final int axisIndex, final char dimChar) {
            _axisIndex = axisIndex;
            _dimChar = Character.toUpperCase(dimChar);

            init();
        }

        private void init() {
            setLayout(new BorderLayout());

            final JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

            final JLabel label = new JLabel("Length along '" + _dimChar + "' axis:");

            _length = new JSpinner(new SpinnerNumberModel(1, 0.05, 20, 0.05));

            topPanel.add(label);
            topPanel.add(_length);

            setSize(100, 50);

            add(topPanel, BorderLayout.CENTER);

            SwingUtilities.invokeLater(this::addListeners);
        }

        private void addListeners() {
            _length.addChangeListener(changeEvent -> DimensionalAnalysis.setLength(_axisIndex, (Double) _length.getValue()));
        }
    }
}
