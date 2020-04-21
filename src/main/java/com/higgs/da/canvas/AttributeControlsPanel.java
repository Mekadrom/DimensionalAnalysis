package com.higgs.da.canvas;

import com.higgs.da.DimensionalAnalysis;

import javax.swing.*;
import java.awt.*;

public class AttributeControlsPanel extends JPanel {
    private String _controlName;

    private JPanel _scrollPanel;

    public AttributeControlsPanel(final String controlName, final Dimension dimension) {
        _controlName = controlName;
        setSize(dimension);
        setPreferredSize(dimension);

        initSelf();
    }

    private void initSelf() {
        setLayout(new BorderLayout());

        add(getLabelPanel(), BorderLayout.NORTH);
        add(getScrollPanel(), BorderLayout.CENTER);

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    private JPanel getLabelPanel() {
        final JPanel panel = new JPanel(new BorderLayout());
        final JLabel slidersLabel = new JLabel(_controlName + " Controls");
        final JSeparator separator = new JSeparator();

        panel.add(slidersLabel, BorderLayout.CENTER);
        panel.add(separator, BorderLayout.SOUTH);

        panel.setSize(getWidth(), 50);

        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        return panel;
    }

    private JComponent getScrollPanel() {
        _scrollPanel = new JPanel();
        _scrollPanel.setLayout(new BoxLayout(_scrollPanel, BoxLayout.Y_AXIS));

        final JScrollPane scrollPanel = new JScrollPane(_scrollPanel);

        scrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        return scrollPanel;
    }

    public void populate(final AttributeControlPanel component) {
        _scrollPanel.add(component);
    }

    public void clearControls() {
        for (final Component c : _scrollPanel.getComponents()) {
            if (c instanceof AttributeControlPanel) {
                _scrollPanel.remove(c);
            }
        }
    }
}
