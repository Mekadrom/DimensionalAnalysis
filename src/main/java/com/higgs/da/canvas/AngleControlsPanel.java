package com.higgs.da.canvas;

import com.higgs.da.DimensionalAnalysis;
import com.higgs.da.DimensionalMatrixHelper;
import com.higgs.da.DrawableShape;
import com.higgs.da.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Arrays;
import java.util.List;

public class AngleControlsPanel extends AttributeControlsPanel {
    public AngleControlsPanel(final Dimension dimension) {
        super("Angle", dimension);
    }

    public void setShape(final DrawableShape shape) {
        if (shape == null) return;

        int numAngles = shape.getNumAngles();
        int numDim = shape.getNumDimensions(); // DimensionalMatrixHelper.dimCountFromAngleCount(numAngles);

        final char[] possibleChars = Arrays.copyOfRange(DimensionalMatrixHelper.AXES_ORDER, 0, numDim);

        // minus two because number of dimensions required to define angle is always two less than the number of spatial dimensions
        int dimCount = numDim - 2;

        final List<String> angleNames = Utils.permute(possibleChars, dimCount, false, false);

        for (int i = 0; i < numAngles; i++) {
            final AngleControlPanel angleControlPanel = new AngleControlPanel(i, angleNames.get(i));

            _scrollPanel.add(angleControlPanel);

            shape.addAngleChangeListener(changeEvent -> {
                final Object source = changeEvent.getSource();
                if (source instanceof DrawableShape) {
                    angleControlPanel.setAngleValue(shape.getAngle(angleControlPanel.getAngleIndex()));
                }
            });
        }
    }

    static class AngleControlPanel extends AttributeControlPanel {
        private int _angleIndex;
        private String _suffix;

        private JSlider _slider;
        private JCheckBox _progress;
        private JSpinner _valueDisplay;

        public AngleControlPanel(final int angleIndex, final String suffix) {
            _angleIndex = angleIndex;
            _suffix = suffix;

            init();
        }

        private void init() {
            setLayout(new BorderLayout());

            final JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            final JPanel middlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

            final JLabel label = new JLabel("Angle " + _suffix.toUpperCase());

            _slider = new JSlider(0, 360, 0);

            final SpinnerNumberModel model = new SpinnerNumberModel(0, 0, 360, 1);
            _valueDisplay = new JSpinner(model);

            final JLabel progressCheckBoxLabel = new JLabel("Progress");
            _progress = new JCheckBox();
            _progress.setSelected(true);

            topPanel.add(label);
            topPanel.add(_slider);
            topPanel.add(_valueDisplay);
            middlePanel.add(progressCheckBoxLabel);
            middlePanel.add(_progress);

            setSize(100, 50);

            add(topPanel, BorderLayout.NORTH);
            add(middlePanel, BorderLayout.CENTER);

            SwingUtilities.invokeLater(this::addListeners);
        }

        private void addListeners() {
            _slider.addChangeListener(changeEvent -> {
                DimensionalAnalysis.setAngle(_angleIndex, Math.toRadians(_slider.getValue()));

                _valueDisplay.setValue(_slider.getValue());
                revalidate();
                repaint();
            });

            _slider.addFocusListener(new FocusAdapter() {
                public void focusGained(final FocusEvent focusEvent) {
                    _progress.setSelected(false);
                }
            });

            _progress.addChangeListener(changeEvent -> DimensionalAnalysis.setAngleProgress(_angleIndex, _progress.isSelected()));

            _valueDisplay.addChangeListener(changeEvent -> _slider.setValue((int) _valueDisplay.getValue()));
        }

        public void setAngleValue(final double value) {
            _slider.setValue((int) Math.toDegrees(value));
        }

        public int getAngleIndex() {
            return _angleIndex;
        }
    }
}
