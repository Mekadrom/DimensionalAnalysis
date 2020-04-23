package com.higgs.da.canvas;

import com.higgs.da.DimensionalAnalysis;
import com.higgs.da.DimensionalMatrixHelper;
import com.higgs.da.DrawableShape;
import com.higgs.da.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class AngleControlsPanel extends AttributeControlsPanel {

    private static List<AngleControlPanel> _controls;

    public AngleControlsPanel(final Dimension dimension) {
        super("Angle", dimension);
        init();
    }

    private void init() {
        final JButton _stopProgressing = new JButton("Toggle Progress All");

        _stopProgressing.addActionListener(actionEvent -> _controls.forEach(AngleControlPanel::toggleProgress));

        _headerPanel.add(_stopProgressing, BorderLayout.EAST);
    }

    public void setShape(final DrawableShape shape) {
        SwingUtilities.invokeLater(() -> reset(shape));
    }

    private void reset(final DrawableShape shape) {
        if (shape == null) return;

        int numAngles = shape.getNumAngles();
        int numDim = shape.getNumDimensions(); // DimensionalMatrixHelper.dimCountFromAngleCount(numAngles);

        final char[] possibleChars = DimensionalMatrixHelper.getAxes(numDim);

        // minus two because number of dimensions required to define angle is always two less than the number of spatial dimensions
        int dimCount = numDim - 2;

        final List<String> angleNames = Utils.permute(possibleChars, dimCount, false, false);

        _controls = new ArrayList<>();

        for (int i = 0; i < numAngles; i++) {
            final AngleControlPanel angleControlPanel = new AngleControlPanel(i, angleNames.get(i));

            _scrollablePanel.add(angleControlPanel);

            shape.addAngleChangeListener(changeEvent -> {
                final Object source = changeEvent.getSource();
                if (source instanceof DrawableShape) {
                    angleControlPanel.setAngleValue(shape.getAngle(angleControlPanel.getAngleIndex()));
                }
            });
            _controls.add(angleControlPanel);
        }

        for (final AngleControlPanel controlPanel : _controls) {
            controlPanel.setList(_controls);
        }
    }

    static class AngleControlPanel extends AttributeControlPanel {
        private int _angleIndex;
        private String _suffix;

        private JSlider _slider;
        private JCheckBox _progress;
        private JSpinner _valueDisplay;
        private JSpinner _progressStep;

        private final List<AngleControlPanel> _otherPanels = new ArrayList<>();

        private JComboBox<String> _syncBox;
        private JLabel _syncLabel;

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
            label.setFont(new Font("Droid Sans Mono", Font.BOLD, 12));

            _slider = new JSlider(0, 360, 0);

            _valueDisplay = new JSpinner(new SpinnerNumberModel(0, 0, 360, 1));

            _progress = new JCheckBox("Progress", true);

            _progressStep = new JSpinner(new SpinnerNumberModel(45, 0, 360, 1));

            _syncLabel = new JLabel("Sync with:");
            _syncLabel.setEnabled(false);
            _syncLabel.setVisible(false);

            _syncBox = new JComboBox<>();
            _syncBox.setEnabled(false);
            _syncBox.setVisible(false);

            topPanel.add(label);
            topPanel.add(_slider);
            topPanel.add(_valueDisplay);
            middlePanel.add(_progress);
            middlePanel.add(_progressStep);
            middlePanel.add(new JLabel("deg/s"));
            middlePanel.add(_syncLabel);
            middlePanel.add(_syncBox);

            setSize(100, 50);

            add(topPanel, BorderLayout.NORTH);
            add(middlePanel, BorderLayout.CENTER);

            this.addListeners();
        }

        private void addListeners() {
            _slider.addChangeListener(changeEvent -> {
                DimensionalAnalysis.setAngle(_angleIndex, Math.toRadians(_slider.getValue()));

                _valueDisplay.setValue(_slider.getValue());
            });

            _slider.addFocusListener(new FocusAdapter() {
                public void focusGained(final FocusEvent e) {
                    _progress.setSelected(false);
                }
            });

            _valueDisplay.addChangeListener(changeEvent -> {
                DimensionalAnalysis.setAngle(_angleIndex, Math.toRadians((int) _valueDisplay.getValue()));

                _slider.setValue((int) _valueDisplay.getValue());
            });

            _progress.addChangeListener(changeEvent -> DimensionalAnalysis.setAngleProgress(_angleIndex, _progress.isSelected()));

            _progressStep.addChangeListener(changeEvent -> {
                if ((int) _progressStep.getValue() < 1) {
                    _progressStep.setValue(1);
                    _progress.setSelected(false);
                }
                DimensionalAnalysis.setAngleProgressSpeed(_angleIndex, Math.toRadians((int) _progressStep.getValue()) / 60.0);
            });

            _progressStep.addFocusListener(new FocusAdapter() {
                public void focusGained(final FocusEvent e) {
                    _progress.setSelected(true);
                }
            });

            _syncBox.addActionListener(actionEvent -> {
                if (_syncBox.getSelectedIndex() != 0) {
                    syncAngle(_syncBox.getSelectedIndex() - 1);
                    _syncBox.setSelectedIndex(0);
                }
            });
        }

        private void syncAngle(int index) {
            int value = (int) _otherPanels.get(index)._valueDisplay.getValue();
            DimensionalAnalysis.setAngle(_angleIndex, Math.toRadians(value));
            _slider.setValue(value);
        }

        public void setAngleValue(final double radians) {
            _slider.setValue((int) Math.toDegrees(radians));
        }

        public int getAngleIndex() {
            return _angleIndex;
        }

        public String getSuffix() {
            return _suffix;
        }

        public void setList(final List<AngleControlPanel> otherPanels) {
            if (otherPanels.size() > 1) {
                _otherPanels.addAll(otherPanels);
                _otherPanels.remove(this);
                _syncBox.addItem("");
                for (final AngleControlPanel otherPanel : _otherPanels) {
                    _syncBox.addItem(otherPanel.getSuffix());
                }
                _syncBox.revalidate();
                _syncBox.repaint();

                if (_otherPanels.size() > 0) {
                    _syncBox.setVisible(true);
                    _syncBox.setEnabled(true);
                    _syncLabel.setVisible(true);
                    _syncLabel.setEnabled(true);
                }
            }
        }

        public void toggleProgress() {
            _progress.doClick();
        }
    }
}
