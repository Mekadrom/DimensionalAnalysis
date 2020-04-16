package com.higgs.da.canvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public class DimensionalCanvasFrame extends JFrame {
    private DimensionalCanvas _panel = null;

    private List<Drawable> _drawables = new ArrayList<>();

    public DimensionalCanvasFrame(final Dimension size) {
        super("Dimensional Analysis");

        setSize(size);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setContentPane(_panel = new DimensionalCanvas());

        setResizable(false);
        setEnabled(true);
        setVisible(true);
    }

    /**
     * Shortcut to start the panel's update loop
     */
    public void start() {
        _panel.start();
    }

    public void addDrawable(final Drawable drawable) {
        if (!_drawables.contains(drawable)) _drawables.add(drawable);
    }

    public void addDrawables(final Collection<? extends Drawable> drawables) {
        drawables.parallelStream().forEach(this::addDrawable);
    }

    class DimensionalCanvas extends JPanel {
        private boolean started = false; // state variable for if this canvas has started its update loop yet

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
            if (started) return;

            final Thread logicThread = new Thread(() -> {
                final int TARGET_FPS = 60;
                final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;

                while(DimensionalCanvas.this.isVisible()) {
                    long start = System.nanoTime(), delta;

                    Arrays.stream(everyFrameRun).forEach(Runnable::run);

                    delta = OPTIMAL_TIME - (System.nanoTime() - start);
                    if(delta >= 0) {
                        try {
                            Thread.sleep(delta / 1000000);
                        } catch(final InterruptedException e) {
                            System.out.println("Error keeping fps at 60");
                            e.printStackTrace();
                        }
                    } else {
                        Thread.yield();
                    }
                }
            });
            started = true;
            logicThread.start();
        }

        /**
         * Updates every drawable every frame; like ticks
         */
        private void logic() {
            for (final Drawable drawable : _drawables) {
                drawable.update();
            }
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

            for (final Drawable drawable : _drawables) {
                drawable.draw(g2d);
            }
            g2d.dispose();

            return image;
        }
    }
}
