package com.higgs.da.canvas;

import com.higgs.da.DimensionalAnalysis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class AngleControlPanel extends AttributeControlPanel {
    private int _angleIndex;

    private JSlider _slider;
    private JLabel _display;

    private JCheckBox _progress;

    private JSpinner _valueDisplay;

    public AngleControlPanel(final int angleIndex, final String suffix) {
        _angleIndex = angleIndex;

        setLayout(new BorderLayout());

        final JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        final JPanel middlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        final JLabel label = new JLabel("Angle " + suffix.toUpperCase());

        _slider = new JSlider(0, 360, 0);
        _display = new JLabel();
        _display.setText("0");

        final JLabel progressCheckBoxLabel = new JLabel("Progress");
        _progress = new JCheckBox();
        _progress.setSelected(true);

        topPanel.add(label);
        topPanel.add(_slider);
        topPanel.add(_display);
        middlePanel.add(progressCheckBoxLabel);
        middlePanel.add(_progress);

        setSize(100, 50);

        add(topPanel, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER);

        init();
    }

    private void init() {
        SwingUtilities.invokeLater(this::addListeners);
    }

    private void addListeners() {
        _slider.addChangeListener(changeEvent -> {
            DimensionalAnalysis.setAngle(_angleIndex, Math.toRadians(_slider.getValue()));

            _display.setText(Integer.toString(_slider.getValue()));
        });

        _slider.addFocusListener(new FocusAdapter() {
            public void focusGained(final FocusEvent focusEvent) {
                _progress.setSelected(false);
            }
        });

        _progress.addChangeListener(changeEvent -> {
            DimensionalAnalysis.setAngleProgress(_angleIndex, _progress.isSelected());
        });
    }

    public void setAngleValue(final double value) {
        _slider.setValue((int) Math.toDegrees(value));
    }

    public int getAngleIndex() {
        return _angleIndex;
    }
}
