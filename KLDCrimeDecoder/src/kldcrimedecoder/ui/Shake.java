package kldcrimedecoder.ui;

import java.awt.Point;
import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * A short left-right shake for one or more components. It fades out and
 * always ends exactly where each component started.
 *
 * One component:   new Shake(pnlLockArtDesign, 12, 450, 4)
 * Several at once: new Shake(10, 500, 5, lblBackground, lblSprite)
 * (amplitude in pixels, duration in ms, number of left-right swings)
 */
public class Shake {

    private static final int FRAME_MS = 15;

    private final JComponent[] targets;
    private final int amplitude;
    private final int durationMs;
    private final int shakes;

    private Timer timer;
    private Point[] homes;

    public Shake(JComponent target, int amplitude, int durationMs, int shakes) {
        this(amplitude, durationMs, shakes, target);
    }

    public Shake(int amplitude, int durationMs, int shakes, JComponent... targets) {
        this.targets = targets;
        this.amplitude = amplitude;
        this.durationMs = durationMs;
        this.shakes = shakes;
    }

    /** Plays the shake. Calling it again restarts it cleanly. */
    public void start() {
        stop();                                   // puts everything back home first

        homes = new Point[targets.length];
        for (int i = 0; i < targets.length; i++) {
            homes[i] = targets[i].getLocation();
        }

        final long begin = System.currentTimeMillis();

        timer = new Timer(FRAME_MS, e -> {
            double t = durationMs <= 0
                    ? 1.0
                    : Math.min(1.0, (System.currentTimeMillis() - begin) / (double) durationMs);

            // swings left and right, getting smaller until it stops
            int offset = (int) Math.round(
                    amplitude * (1.0 - t) * Math.sin(t * shakes * 2.0 * Math.PI));

            for (int i = 0; i < targets.length; i++) {
                targets[i].setLocation(homes[i].x + offset, homes[i].y);
            }

            if (t >= 1.0) {
                stop();
            }
        });
        timer.start();
    }

    /** Stops a running shake and puts everything back. Safe at any time. */
    public void stop() {
        if (timer != null) {
            timer.stop();
            timer = null;
            for (int i = 0; i < targets.length; i++) {
                targets[i].setLocation(homes[i]);
            }
        }
    }
}