package com.higgs.da.canvas;

import javax.swing.*;
import java.awt.*;

public class AttributeControlPanel extends JPanel {
    private String _controlName;

    private JPanel _scrollPanel;

    public AttributeControlPanel(final String controlName, final Dimension dimension) {
        _controlName = controlName;
        setSize(dimension);
        setPreferredSize(dimension);

        initSelf();
        initComponents();
        initActionListeners();
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

    private JPanel getScrollPanel() {
        final JPanel panel = new JPanel();
        final JScrollPane scrollPane = new JScrollPane();

        return panel;
    }

    private void initComponents() {

    }

    private void initActionListeners() {

    }
}
