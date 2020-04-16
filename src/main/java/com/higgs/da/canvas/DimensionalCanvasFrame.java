package com.higgs.da.canvas;

import com.higgs.da.DimensionalAnalysis;
import com.higgs.da.DrawableShape;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class DimensionalCanvasFrame extends JFrame {
    private static final Dimension SIZE = new Dimension(800, 800);

    private DimensionalCanvas _panel = null;

    private static DrawableShape _shape = new DrawableShape(2);

    public DimensionalCanvasFrame() {
        super("Dimensional Analysis");

        setSize(SIZE.width + 100, SIZE.height + 100);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JPanel panel = new JPanel(new BorderLayout());

        panel.add(getGlobalControlPanel(), BorderLayout.NORTH);
        panel.add(getAngleControlPanel(), BorderLayout.WEST);
        panel.add(_panel = new DimensionalCanvas(), BorderLayout.CENTER);

        setContentPane(panel);

        setResizable(false);
        setEnabled(true);
        setVisible(true);
    }

    private JPanel getGlobalControlPanel() {
        return new DimensionalControlPanel(new Dimension(getWidth(), 100));
    }

    private JPanel getAngleControlPanel() {
        return new AttributeControlPanel("Angle", new Dimension(120, getHeight()));
    }

    private JPanel getLengthControlPanel() {
        return new AttributeControlPanel("Side Length", new Dimension(120, getHeight()));
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

    static class DimensionalCanvas extends JPanel {
        private boolean _stopped = false;

        private  Thread _logicThread;

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
         * Internal start method that takes a list of functions to run every frame, does them in sequential order
         * @param everyFrameRun a list of runnables to run every frame
         */
        private void start(final Runnable... everyFrameRun) {
            _stopped = false;
            if (_logicThread == null) {
                _logicThread = new Thread(() -> {
                    final int TARGET_FPS = 60;
                    final long OPTIMAL_TIME = 1000000000L / TARGET_FPS;

                    while (DimensionalCanvas.this.isVisible()) {
                        if (!_stopped) {
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
                    }
                });
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
            final BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);

            final Graphics2D g2d = image.createGraphics();

            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

            g2d.setColor(Color.WHITE);
            g2d.translate(getSize().width / 2, getSize().height / 2);
            g2d.setStroke(new BasicStroke(DimensionalAnalysis.getLineThickness()));
            _shape.draw(g2d);
            g2d.translate(-getSize().width / 2, -getSize().height / 2);

            g2d.dispose();

            return image;
        }
    }
}
