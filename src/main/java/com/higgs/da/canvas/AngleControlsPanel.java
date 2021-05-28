package com.higgs.da.canvas;

import com.higgs.da.DimensionalAnalysis;
import com.higgs.da.DimensionalMatrixHelper;
import com.higgs.da.DrawableShape;
import com.higgs.da.Utils;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

public class AngleControlsPanel extends AttributeControlsPanel {

    private static List<AngleControlPanel> controls;

    public AngleControlsPanel(final Dimension dimension) {
        super("Angle", dimension);
        this.init();
    }

    private void init() {
        final JButton _stopProgressing = new JButton("Toggle Progress All");

        _stopProgressing.addActionListener(actionEvent -> AngleControlsPanel.controls.forEach(AngleControlPanel::toggleProgress));

        this.headerPanel.add(_stopProgressing, BorderLayout.EAST);
    }

    public void setShape(final DrawableShape shape) {
        SwingUtilities.invokeLater(() -> this.reset(shape));
    }

    private void reset(final DrawableShape shape) {
        if (shape == null) return;

        final int numAngles = shape.getNumAngles();
        final int numDim = shape.getNumDimensions(); // DimensionalMatrixHelper.dimCountFromAngleCount(numAngles);

        final char[] possibleChars = DimensionalMatrixHelper.getAxes(numDim);

        // minus two because number of dimensions required to define angle is always two less than the number of spatial dimensions
        final int dimCount = numDim - 2;

        final List<String> angleNames = Utils.permute(possibleChars, dimCount, false, false);

        AngleControlsPanel.controls = new ArrayList<>();

        for (int i = 0; i < numAngles; i++) {
            final AngleControlPanel angleControlPanel = new AngleControlPanel(i, angleNames.get(i));

            this.scrollablePanel.add(angleControlPanel);

            shape.addAngleChangeListener(changeEvent -> {
                final Object source = changeEvent.getSource();
                if (source instanceof DrawableShape) {
                    angleControlPanel.setAngleValue(shape.getAngle(angleControlPanel.getAngleIndex()));
                }
            });
            AngleControlsPanel.controls.add(angleControlPanel);
        }

        for (final AngleControlPanel controlPanel : AngleControlsPanel.controls) {
            controlPanel.setList(AngleControlsPanel.controls);
        }
    }

    static class AngleControlPanel extends AttributeControlPanel {
        private final int angleIndex;
        private final String suffix;

        private JSlider slider;
        private JCheckBox progress;
        private JSpinner valueDisplay;
        private JSpinner progressStep;

        private final List<AngleControlPanel> _otherPanels = new ArrayList<>();

        private JComboBox<String> _syncBox;
        private JLabel _syncLabel;

        public AngleControlPanel(final int angleIndex, final String suffix) {
            this.angleIndex = angleIndex;
            this.suffix = suffix;

            this.init();
        }

        private void init() {
            this.setLayout(new BorderLayout());

            final JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            final JPanel middlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

            final JLabel label = new JLabel("Angle " + this.suffix.toUpperCase());
            label.setFont(new Font("Droid Sans Mono", Font.BOLD, 12));

            this.slider = new JSlider(0, 360, 0);

            this.valueDisplay = new JSpinner(new SpinnerNumberModel(0, 0, 360, 1));

            this.progress = new JCheckBox("Progress", true);

            this.progressStep = new JSpinner(new SpinnerNumberModel(45, 0, 360, 1));

            this._syncLabel = new JLabel("Sync with:");
            this._syncLabel.setEnabled(false);
            this._syncLabel.setVisible(false);

            this._syncBox = new JComboBox<>();
            this._syncBox.setEnabled(false);
            this._syncBox.setVisible(false);

            topPanel.add(label);
            topPanel.add(this.slider);
            topPanel.add(this.valueDisplay);
            middlePanel.add(this.progress);
            middlePanel.add(this.progressStep);
            middlePanel.add(new JLabel("deg/s"));
            middlePanel.add(this._syncLabel);
            middlePanel.add(this._syncBox);

            this.setSize(100, 50);

            this.add(topPanel, BorderLayout.NORTH);
            this.add(middlePanel, BorderLayout.CENTER);

            this.addListeners();
        }

        private void addListeners() {
            this.slider.addChangeListener(changeEvent -> {
                DimensionalAnalysis.setAngle(this.angleIndex, Math.toRadians(this.slider.getValue()));

                this.valueDisplay.setValue(this.slider.getValue());
            });

            this.slider.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(final FocusEvent e) {
                    AngleControlPanel.this.progress.setSelected(false);
                }
            });

            this.valueDisplay.addChangeListener(changeEvent -> {
                DimensionalAnalysis.setAngle(this.angleIndex, Math.toRadians((int) this.valueDisplay.getValue()));

                this.slider.setValue((int) this.valueDisplay.getValue());
            });

            this.progress.addChangeListener(changeEvent -> DimensionalAnalysis.setAngleProgress(this.angleIndex, this.progress.isSelected()));

            this.progressStep.addChangeListener(changeEvent -> {
                if ((int) this.progressStep.getValue() < 1) {
                    this.progressStep.setValue(1);
                    this.progress.setSelected(false);
                }
                DimensionalAnalysis.setAngleProgressSpeed(this.angleIndex, Math.toRadians((int) this.progressStep.getValue()) / 60.0);
            });

            this.progressStep.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(final FocusEvent e) {
                    AngleControlPanel.this.progress.setSelected(true);
                }
            });

            this._syncBox.addActionListener(actionEvent -> {
                if (this._syncBox.getSelectedIndex() != 0) {
                    this.syncAngle(this._syncBox.getSelectedIndex() - 1);
                    this._syncBox.setSelectedIndex(0);
                }
            });
        }

        private void syncAngle(final int index) {
            final int value = (int) this._otherPanels.get(index).valueDisplay.getValue();
            DimensionalAnalysis.setAngle(this.angleIndex, Math.toRadians(value));
            this.slider.setValue(value);
        }

        public void setAngleValue(final double radians) {
            this.slider.setValue((int) Math.toDegrees(radians));
        }

        public int getAngleIndex() {
            return this.angleIndex;
        }

        public String getSuffix() {
            return this.suffix;
        }

        public void setList(final List<AngleControlPanel> otherPanels) {
            if (otherPanels.size() > 1) {
                this._otherPanels.addAll(otherPanels);
                this._otherPanels.remove(this);
                this._syncBox.addItem("");
                for (final AngleControlPanel otherPanel : this._otherPanels) {
                    this._syncBox.addItem(otherPanel.getSuffix());
                }
                this._syncBox.revalidate();
                this._syncBox.repaint();

                if (this._otherPanels.size() > 0) {
                    this._syncBox.setVisible(true);
                    this._syncBox.setEnabled(true);
                    this._syncLabel.setVisible(true);
                    this._syncLabel.setEnabled(true);
                }
            }
        }

        public void toggleProgress() {
            this.progress.doClick();
        }
    }
}
