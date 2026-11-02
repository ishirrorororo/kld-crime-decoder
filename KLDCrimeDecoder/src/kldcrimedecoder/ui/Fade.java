package kldcrimedecoder.ui;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

/**
 * Fades a label's picture in. Place the label in the designer with its icon
 * set as usual. new Fade(label).start(600, null) makes it visible and brings
 * the picture from invisible to fully shown in 600 ms.
 */
public class Fade {

    private static final int FRAME_MS = 30;

    private final JLabel target;
    private final Icon original;        // the picture from the designer
    private BufferedImage source;

    private Timer timer;

    public Fade(JLabel target) {
        this.target = target;
        this.original = target.getIcon();
    }

    /** Makes the label visible and fades it in. onDone may be null. */
    public void start(int durationMs, Runnable onDone) {
        stop();

        if (original == null || durationMs <= 0) {
            target.setVisible(true);
            if (onDone != null) {
                onDone.run();
            }
            return;
        }

        if (source == null) {
            source = new BufferedImage(
                    original.getIconWidth(), original.getIconHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = source.createGraphics();
            original.paintIcon(target, g, 0, 0);
            g.dispose();
        }

        target.setIcon(frame(0f));
        target.setVisible(true);

        final long begin = System.currentTimeMillis();

        timer = new Timer(FRAME_MS, e -> {
            double t = Math.min(1.0,
                    (System.currentTimeMillis() - begin) / (double) durationMs);

            if (t >= 1.0) {
                stop();                          // puts the full picture back
                if (onDone != null) {
                    onDone.run();
                }
            } else {
                target.setIcon(frame((float) t));
            }
        });
        timer.start();
    }

    /** Stops a running fade and shows the full picture. Safe at any time. */
    public void stop() {
        if (timer != null) {
            timer.stop();
            timer = null;
            target.setIcon(original);
        }
    }

    /** The picture at this opacity, 0 = invisible, 1 = fully shown. */
    private Icon frame(float alpha) {
        BufferedImage img = new BufferedImage(
                source.getWidth(), source.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setComposite(AlphaComposite.SrcOver.derive(alpha));
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return new ImageIcon(img);
    }
}