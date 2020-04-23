package com.higgs.da.canvas;

import javax.swing.*;
import java.awt.*;

public class AttributeControlsPanel extends JPanel {
    private String _controlName;

    protected JPanel _headerPanel;

    protected JPanel _scrollablePanel;

    public AttributeControlsPanel(final String controlName, final Dimension dimension) {
        _controlName = controlName;
        setSize(dimension);
        setPreferredSize(dimension);

        initSelf();
    }

    private void initSelf() {
        setLayout(new BorderLayout());

        add(_headerPanel = getLabelPanel(), BorderLayout.NORTH);
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
        _scrollablePanel = new JPanel();
        _scrollablePanel.setLayout(new BoxLayout(_scrollablePanel, BoxLayout.Y_AXIS));

        final JScrollPane scrollPanel = new JScrollPane(_scrollablePanel);

        scrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        return scrollPanel;
    }
}
