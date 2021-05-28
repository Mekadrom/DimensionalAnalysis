package com.higgs.da.canvas;

import com.higgs.da.DimensionalAnalysis;
import com.higgs.da.DimensionalMatrixHelper;
import com.higgs.da.DrawableShape;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class LengthControlsPanel extends AttributeControlsPanel {
    public LengthControlsPanel(final Dimension dimension) {
        super("Length", dimension);
    }

    public void setShape(final DrawableShape shape) {
        if (shape == null) return;

        final int numDim = shape.getNumDimensions();

        final char[] dimChars = DimensionalMatrixHelper.getAxes(numDim);

        for (int i = 0; i < numDim; i++) {
            this.scrollablePanel.add(new LengthControlPanel(i, dimChars[i]));
        }
    }

    private static class LengthControlPanel extends AttributeControlPanel {
        private final int axisIndex;
        private final char dimChar;

        private JSpinner _length;

        public LengthControlPanel(final int axisIndex, final char dimChar) {
            this.axisIndex = axisIndex;
            this.dimChar = Character.toUpperCase(dimChar);
            this.init();
        }

        private void init() {
            this.setLayout(new BorderLayout());

            final JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

            final JLabel label = new JLabel("Length along '" + this.dimChar + "' axis:");

            this._length = new JSpinner(new SpinnerNumberModel(1, 0.05, 20, 0.05));

            topPanel.add(label);
            topPanel.add(this._length);

            this.setSize(100, 50);

            this.add(topPanel, BorderLayout.CENTER);

            SwingUtilities.invokeLater(this::addListeners);
        }

        private void addListeners() {
            this._length.addChangeListener(changeEvent -> DimensionalAnalysis.setLength(this.axisIndex, (Double) this._length.getValue()));
        }
    }
}
