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
    public AngleControlsPanel(final Dimension dimension) {
        super("Angle", dimension);
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

        final List<AngleControlPanel> otherPanels = new ArrayList<>();

        for (int i = 0; i < numAngles; i++) {
            final AngleControlPanel angleControlPanel = new AngleControlPanel(i, angleNames.get(i));

            _scrollPanel.add(angleControlPanel);

            shape.addAngleChangeListener(changeEvent -> {
                final Object source = changeEvent.getSource();
                if (source instanceof DrawableShape) {
                    angleControlPanel.setAngleValue(shape.getAngle(angleControlPanel.getAngleIndex()));
                }
            });
            otherPanels.add(angleControlPanel);
        }

        for (final AngleControlPanel controlPanel : otherPanels) {
            controlPanel.setList(otherPanels);
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

            _syncBox = new JComboBox<>();
            _syncBox.setEnabled(false);
            _syncBox.setVisible(false);

            topPanel.add(label);
            topPanel.add(_slider);
            topPanel.add(_valueDisplay);
            middlePanel.add(_progress);
            middlePanel.add(_progressStep);
            middlePanel.add(new JLabel("deg/s"));
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

            _valueDisplay.addChangeListener(changeEvent -> _slider.setValue((int) _valueDisplay.getValue()));

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
                    syncAngle(_syncBox.getSelectedIndex());
                    setAngleValue(DimensionalAnalysis.getAngle(_angleIndex));
                    _syncBox.setSelectedIndex(0);
                }
            });
        }

        private void syncAngle(int selectedItem) {
            int value = _otherPanels.get(selectedItem - 1)._slider.getValue();
            DimensionalAnalysis.setAngle(_angleIndex, Math.toRadians(value));
            _slider.setValue(value);
//            if (selectedItem >= _angleIndex) selectedItem += 1;
//            selectedItem--;
//            _progress.setSelected(DimensionalAnalysis.getAngleProgress(selectedItem));
//            DimensionalAnalysis.setAngle(_angleIndex, DimensionalAnalysis.getAngle(selectedItem));
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
                }
            }
        }
    }
}
