package com.higgs.da.canvas;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class AttributeControlsPanel extends JPanel {
    private final String controlName;

    protected JPanel headerPanel;

    protected JPanel scrollablePanel;

    public AttributeControlsPanel(final String controlName, final Dimension dimension) {
        this.controlName = controlName;
        this.setSize(dimension);
        this.setPreferredSize(dimension);

        this.initSelf();
    }

    private void initSelf() {
        this.setLayout(new BorderLayout());

        this.add(this.headerPanel = this.getLabelPanel(), BorderLayout.NORTH);
        this.add(this.getScrollPanel(), BorderLayout.CENTER);

        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    private JPanel getLabelPanel() {
        final JPanel panel = new JPanel(new BorderLayout());
        final JLabel slidersLabel = new JLabel(this.controlName + " Controls");
        final JSeparator separator = new JSeparator();

        panel.add(slidersLabel, BorderLayout.WEST);
        panel.add(separator, BorderLayout.SOUTH);

        panel.setSize(this.getWidth(), 50);

        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        return panel;
    }

    private JComponent getScrollPanel() {
        this.scrollablePanel = new JPanel();
        this.scrollablePanel.setLayout(new BoxLayout(this.scrollablePanel, BoxLayout.Y_AXIS));

        final JScrollPane scrollPanel = new JScrollPane(this.scrollablePanel);

        scrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        scrollPanel.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPanel;
    }
}
