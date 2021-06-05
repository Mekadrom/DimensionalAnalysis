package com.higgs.da.canvas;

import com.higgs.da.DimensionalAnalysis;
import com.higgs.da.DimensionalMatrixHelper;
import com.higgs.da.DrawableShape;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class DimensionalCanvasFrame extends JFrame {
    private static final Dimension SIZE = new Dimension(1366, 768);

    private DimensionalCanvas panel = null;

    private static DrawableShape shape = new DrawableShape(2);

    private AngleControlsPanel angleControlsPanel;
    private LengthControlsPanel lengthControlsPanel;
    private ProjectionControlsPanel projectionControlsPanel;

    private DimensionalControlPanel _globalControlPanel;

    public DimensionalCanvasFrame() {
        super("Dimensional Analysis");

        DimensionalMatrixHelper.preload(2);
        this.init();
    }

    private void init() {
        this.setSize(DimensionalCanvasFrame.SIZE.width + 100, DimensionalCanvasFrame.SIZE.height + 100);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.resetPanel(new JPanel(new BorderLayout()));

        this.setEnabled(true);
        this.setVisible(true);
    }

    private JPanel getGlobalControlPanel() {
        if (this._globalControlPanel == null) {
            this._globalControlPanel = new DimensionalControlPanel(new Dimension(this.getWidth(), 100));
        }
        return this._globalControlPanel;
    }

    private JPanel getAngleControlPanel() {
        if (this.angleControlsPanel == null) {
            this.angleControlsPanel = new AngleControlsPanel(new Dimension(400, this.getHeight()));
        }
        return this.angleControlsPanel;
    }

    private JPanel getLengthControlPanel() {
        if (this.lengthControlsPanel == null) {
            this.lengthControlsPanel = new LengthControlsPanel(new Dimension(240, this.getHeight()));
        }
        return this.lengthControlsPanel;
    }

    private JPanel getProjectionControlsPanel() {
        if (this.projectionControlsPanel == null) {
            this.projectionControlsPanel = new ProjectionControlsPanel(new Dimension(350, this.getHeight()));
        }
        return this.projectionControlsPanel;
    }

    /**
     * Shortcut to start the panel's update loop
     */
    public void start() {
        this.panel.start();
    }

    public void stop() {
        this.panel.stop();
    }

    public void setDrawableShape(final DrawableShape shape) {
        DimensionalCanvasFrame.shape = shape;
    }

    public DrawableShape getDrawableShape() {
        return DimensionalCanvasFrame.shape;
    }

    public void resetControls() {
        this.resetAngleControls();
        this.resetLengthControls();

        if (this.getDrawableShape() != null) {
            this.resetProjectionControls();
        } else {
            this.getContentPane().remove(this.getProjectionControlsPanel());
        }

        this.resetPanel(this.getContentPane());
    }

    private void resetPanel(final Container contentPane) {
        contentPane.removeAll();

        contentPane.add(this.getGlobalControlPanel(), BorderLayout.NORTH);
        contentPane.add(this.getAngleControlPanel(), BorderLayout.WEST);
        contentPane.add(this.getRightControlPanel(), BorderLayout.EAST);

        contentPane.add(this.panel = new DimensionalCanvas(), BorderLayout.CENTER);

        this.setContentPane(contentPane);
    }

    private JPanel getRightControlPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(this.getLengthControlPanel());
        panel.add(this.getProjectionControlsPanel());

        return panel;
    }

    public void resetAngleControls() {
        this.angleControlsPanel = new AngleControlsPanel(new Dimension(400, this.getHeight()));
        this.angleControlsPanel.setShape(DimensionalCanvasFrame.shape);
    }

    private void resetLengthControls() {
        this.lengthControlsPanel = new LengthControlsPanel(new Dimension(240, this.getHeight()));
        this.lengthControlsPanel.setShape(DimensionalCanvasFrame.shape);
    }

    private void resetProjectionControls() {
        this.projectionControlsPanel = new ProjectionControlsPanel(new Dimension(350, this.getHeight()));
        this.projectionControlsPanel.setShape(DimensionalCanvasFrame.shape);
    }

    static class DimensionalCanvas extends JPanel {
        private boolean stopped = false;

        private Thread logicThread;

//        private String fps = "";

        public void stop() {
            this.stopped = true;
        }

        /**
         * Call once to create a thread that updates the canvas' background and does logic
         */
        public void start() {
            this.start(this::logic, this::repaint);
        }

        /**
         * Internal start method that takes a list of functions to run every frame and runs them in sequential order
         *
         * @param everyFrameRun a list of runnables to run every frame
         */
        private void start(final Runnable... everyFrameRun) {
            final double TARGET_FPS = 60.0;
            final double OPTIMAL_TIME = 1000000000.0 / TARGET_FPS;

            this.stopped = false;
            if (this.logicThread == null) {
                this.logicThread = new Thread(() -> {
                    long before;
                    long timer = System.currentTimeMillis();
                    double delta = 0;
                    int frames = 0;

                    while (DimensionalCanvas.this.isVisible() && !this.stopped) {
                        before = System.nanoTime();
                        if (delta >= 1) {
                            Arrays.stream(everyFrameRun).forEach(Runnable::run);
                            frames++;
                            delta--;
                        }
                        delta += (System.nanoTime() - before) / OPTIMAL_TIME;

                        if (System.currentTimeMillis() - timer > 1000) {
//                            this.fps = String.valueOf(frames);
                            frames = 0;
                            timer += 1000;
                        }
                    }
                }, "DCP Action Thread");
                this.logicThread.start();
            }
        }

        /**
         * Updates the shape every frame; like ticks
         */
        private void logic() {
            DimensionalCanvasFrame.shape.update();
        }

        /**
         * Overridden to draw the image obtained from draw() onto this panel
         *
         * @param g the graphics component to draw on
         */
        @Override
        public void paint(final Graphics g) {
            super.paint(g);
            g.drawImage(this.draw(), 0, 0, null);
        }

        /**
         * Clears the image by filling with a background and then draws every drawable
         *
         * @return the current frame
         */
        public BufferedImage draw() {
            final BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

            final Graphics2D g2d = image.createGraphics();

            g2d.setColor(UIManager.getColor("Panel.background"));
            g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

            g2d.setColor(Color.WHITE);

//            g2d.drawString(this.fps, 2, 12);

            g2d.translate(this.getSize().width / 2, this.getSize().height / 2);
            g2d.setStroke(new BasicStroke((float) DimensionalAnalysis.getLineThickness(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            DimensionalCanvasFrame.shape.draw(g2d);
            g2d.translate(-this.getSize().width / 2, -this.getSize().height / 2);

            g2d.dispose();

            return image;
        }
    }
}
