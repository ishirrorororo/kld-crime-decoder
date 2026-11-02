package kldcrimedecoder.ui;

import java.awt.Container;
import java.awt.Point;
import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * A short "pop in" for any component.
 *
 * The component ends up exactly where you placed it in the designer. It
 * starts offset by (fromDx, fromDy), moves in with a small overshoot, then
 * settles. Example: new PopIn(pnlLockArtDesign, 0, -160, 450) drops the panel
 * in from 160 pixels above its place in 450 ms.
 *
 * Tip: do not show or hide other components while it runs. A layout pass
 * would put the component back at its designer position mid-animation.
 */
public class PopIn {

    private static final int FRAME_MS = 15;

    private final JComponent target;
    private final int fromDx;
    private final int fromDy;
    private final int durationMs;

    private Timer timer;

    public PopIn(JComponent target, int fromDx, int fromDy, int durationMs) {
        this.target = target;
        this.fromDx = fromDx;
        this.fromDy = fromDy;
        this.durationMs = durationMs;
    }

    /** Makes the target visible and plays the animation. onDone may be null. */
    public void start(Runnable onDone) {
        stop();

        target.setVisible(true);

        // lay the parent out first, so getLocation() is the designer position
        Container parent = target.getParent();
        if (parent != null) {
            parent.validate();
        }
        final Point end = target.getLocation();

        target.setLocation(end.x + fromDx, end.y + fromDy);

        final long begin = System.currentTimeMillis();

        timer = new Timer(FRAME_MS, e -> {
            double t = durationMs <= 0
                    ? 1.0
                    : Math.min(1.0, (System.currentTimeMillis() - begin) / (double) durationMs);

            double left = 1.0 - easeOutBack(t);          // 1 = at the start, 0 = home

            target.setLocation(
                    end.x + (int) Math.round(fromDx * left),
                    end.y + (int) Math.round(fromDy * left));

            if (t >= 1.0) {
                stop();
                target.setLocation(end);                  // exactly where the designer put it
                if (onDone != null) {
                    onDone.run();
                }
            }
        });
        timer.start();
    }

    /** Stops a running animation. Safe to call at any time. */
    public void stop() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    /** Goes a little past 1 and comes back, which gives the "pop". */
    private static double easeOutBack(double t) {
        final double c1 = 1.70158;
        final double c3 = c1 + 1.0;
        double u = t - 1.0;
        return 1.0 + c3 * u * u * u + c1 * u * u;
    }
}