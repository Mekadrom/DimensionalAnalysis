package com.higgs.da.canvas;

import com.higgs.da.DimensionalAnalysis;
import com.higgs.da.DrawableShape;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class DimensionalCanvasFrame extends JFrame {
    private static final Dimension SIZE = new Dimension(1600, 900);

    private DimensionalCanvas _panel = null;

    private static DrawableShape _shape = new DrawableShape(2);

    private AngleControlsPanel _angleControlsPanel;
    private LengthControlsPanel _lengthControlsPanel;
    private ProjectionControlsPanel _projectionControlsPanel;

    private DimensionalControlPanel _globalControlPanel;

    public DimensionalCanvasFrame() {
        super("Dimensional Analysis");

        setSize(SIZE.width + 100, SIZE.height + 100);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JPanel panel = new JPanel(new BorderLayout());

        resetPanel(panel);

        setEnabled(true);
        setVisible(true);
    }

    private JPanel getGlobalControlPanel() {
        if (_globalControlPanel == null) {
            _globalControlPanel = new DimensionalControlPanel(new Dimension(getWidth(), 100));
        }
        return _globalControlPanel;
    }

    private JPanel getAngleControlPanel() {
        if (_angleControlsPanel == null) {
            _angleControlsPanel = new AngleControlsPanel(new Dimension(400, getHeight()));
        }
        return _angleControlsPanel;
    }

    private JPanel getLengthControlPanel() {
        if (_lengthControlsPanel == null) {
            _lengthControlsPanel = new LengthControlsPanel(new Dimension(240, getHeight()));
        }
        return _lengthControlsPanel;
    }

    private JPanel getProjectionControlsPanel() {
        if (_projectionControlsPanel == null) {
            _projectionControlsPanel = new ProjectionControlsPanel(new Dimension(300, getHeight()));
        }
        return _projectionControlsPanel;
    }

    /**
     * Shortcut to start the panel's update loop
     */
    public void start() {
        _panel.start();
    }

    public void stop() {
        _panel.stop();
    }

    public void setDrawableShape(final DrawableShape shape) {
        _shape = shape;
    }

    public DrawableShape getDrawableShape() {
        return _shape;
    }

    public void resetControls() {
        resetAngleControls();
        resetLengthControls();

        if (getDrawableShape() != null && getDrawableShape().getNumDimensions() > 2) {
            resetProjectionControls();
        } else {
            getContentPane().remove(getProjectionControlsPanel());
        }

        resetPanel(getContentPane());
    }

    private void resetPanel(final Container contentPane) {
        contentPane.removeAll();

        contentPane.add(getGlobalControlPanel(), BorderLayout.NORTH);
        contentPane.add(getAngleControlPanel(), BorderLayout.WEST);
        contentPane.add(getRightControlPanel(), BorderLayout.EAST);

        contentPane.add(_panel = new DimensionalCanvas(), BorderLayout.CENTER);

        setContentPane(contentPane);
    }

    private JPanel getRightControlPanel() {
        final JPanel panel = new JPanel(new BorderLayout());

        panel.add(getLengthControlPanel(), BorderLayout.WEST);
        panel.add(getProjectionControlsPanel(), BorderLayout.EAST);

        return panel;
    }

    public void resetAngleControls() {
        _angleControlsPanel = new AngleControlsPanel(new Dimension(400, getHeight()));
        _angleControlsPanel.setShape(_shape);
    }

    private void resetLengthControls() {
        _lengthControlsPanel = new LengthControlsPanel(new Dimension(240, getHeight()));
        _lengthControlsPanel.setShape(_shape);
    }

    private void resetProjectionControls() {
        _projectionControlsPanel = new ProjectionControlsPanel(new Dimension(300, getHeight()));
        _projectionControlsPanel.setShape(_shape);
    }

    static class DimensionalCanvas extends JPanel {
        private boolean _stopped = false;

        private Thread _logicThread;

        public void stop() {
            _stopped = true;
        }

        /**
         * Call once to create a thread that updates the canvas' background and does logic
         */
        public void start() {
            start(this::logic, this::repaint);
        }

        /**
         * Internal start method that takes a list of functions to run every frame and runs them in sequential order
         * @param everyFrameRun a list of runnables to run every frame
         */
        private void start(final Runnable... everyFrameRun) {
            _stopped = false;
            if (_logicThread == null) {
                _logicThread = new Thread(() -> {
                    final int TARGET_FPS = 60;
                    final long OPTIMAL_TIME = 1000000000L / TARGET_FPS;

                    while (DimensionalCanvas.this.isVisible() && !_stopped) {
                        long start = System.nanoTime(), delta;

                        Arrays.stream(everyFrameRun).forEach(Runnable::run);

                        delta = OPTIMAL_TIME - (System.nanoTime() - start);
                        if (delta >= 0) {
                            try {
                                Thread.sleep(delta / 1000000L);
                            } catch (final InterruptedException e) {
                                System.out.println("Error keeping fps at 60");
                                e.printStackTrace();
                            }
                        } else {
                            Thread.yield();
                        }
                    }
                }, "DCP Action Thread");
                _logicThread.start();
            }
        }

        /**
         * Updates the shape every frame; like ticks
         */
        private void logic() {
            _shape.update();
        }

        /**
         * Overridden to draw the image obtained from draw() onto this panel
         * @param g the graphics component to draw on
         */
        @Override
        public void paint(final Graphics g) {
            super.paint(g);
            g.drawImage(draw(), 0, 0, null);
        }

        /**
         * Clears the image by filling with a background and then draws every drawable
         * @return the current frame
         */
        public BufferedImage draw() {
            final BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

            final Graphics2D g2d = image.createGraphics();

            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

            g2d.setColor(Color.WHITE);
            g2d.translate(getSize().width / 2, getSize().height / 2);
            g2d.setStroke(new BasicStroke((float) DimensionalAnalysis.getLineThickness(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            _shape.draw(g2d);
            g2d.translate(-getSize().width / 2, -getSize().height / 2);

            g2d.dispose();

            return image;
        }
    }
}
