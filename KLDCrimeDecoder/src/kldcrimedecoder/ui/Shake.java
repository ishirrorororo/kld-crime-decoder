package kldcrimedecoder.ui;

import java.awt.Point;
import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * A short left-right shake for any component. It fades out and always
 * ends exactly where the component started.
 * Example: new Shake(pnlLockArtDesign, 12, 450, 4) shakes the panel
 * 12 pixels each way, 4 times, over 450 ms.
 */
public class Shake {

    private static final int FRAME_MS = 15;

    private final JComponent target;
    private final int amplitude;
    private final int durationMs;
    private final int shakes;

    private Timer timer;
    private Point home;

    public Shake(JComponent target, int amplitude, int durationMs, int shakes) {
        this.target = target;
        this.amplitude = amplitude;
        this.durationMs = durationMs;
        this.shakes = shakes;
    }

    /** Plays the shake. Calling it again restarts it cleanly. */
    public void start() {
        stop();                                   // puts the target back home first

        home = target.getLocation();
        final long begin = System.currentTimeMillis();

        timer = new Timer(FRAME_MS, e -> {
            double t = durationMs <= 0
                    ? 1.0
                    : Math.min(1.0, (System.currentTimeMillis() - begin) / (double) durationMs);

            // swings left and right, getting smaller until it stops
            double offset = amplitude * (1.0 - t) * Math.sin(t * shakes * 2.0 * Math.PI);

            target.setLocation(home.x + (int) Math.round(offset), home.y);

            if (t >= 1.0) {
                stop();
            }
        });
        timer.start();
    }

    /** Stops a running shake and puts the target back. Safe at any time. */
    public void stop() {
        if (timer != null) {
            timer.stop();
            timer = null;
            if (home != null) {
                target.setLocation(home);
            }
        }
    }
}