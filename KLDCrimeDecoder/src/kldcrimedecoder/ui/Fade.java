package kldcrimedecoder.ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/** Fade to black, (optional caption), switch, fade back in. Blocks clicks while running. */
public final class Fade {
    private static final int FADE_MS = 350;
    private static final int HOLD_MS = 1500;     // how long the caption stays
    private static final Overlay OVERLAY = new Overlay();

    private Fade() { }

    /** caption may be null. action is what switches the scene / level. */
    public static void through(Component anchor, String caption, Runnable action) {
        JRootPane root = SwingUtilities.getRootPane(anchor);
        if (root == null) {
            action.run();
            return;
        }
        if (OVERLAY.busy) {
            return;                                   // ignore double triggers
        }
        OVERLAY.busy = true;
        if (root.getGlassPane() != OVERLAY) {
            root.setGlassPane(OVERLAY);
        }
        OVERLAY.caption = null;
        OVERLAY.alpha = 0f;
        OVERLAY.setVisible(true);

        OVERLAY.animate(0f, 1f, FADE_MS, () -> {
            OVERLAY.caption = caption;
            OVERLAY.repaint();
            Timer hold = new Timer(caption == null ? 0 : HOLD_MS, e -> {
                OVERLAY.caption = null;
                action.run();                         // new scene starts under the black
                OVERLAY.animate(1f, 0f, FADE_MS + 100, () -> {
                    OVERLAY.setVisible(false);
                    OVERLAY.busy = false;
                });
            });
            hold.setRepeats(false);
            hold.start();
        });
    }

    private static class Overlay extends JComponent {
        float alpha;
        String caption;
        boolean busy;
        private Timer timer;

        Overlay() {
            setOpaque(false);
            MouseAdapter block = new MouseAdapter() { };   // swallows clicks
            addMouseListener(block);
            addMouseMotionListener(block);
            addMouseWheelListener(block);
        }

        void animate(float from, float to, int ms, Runnable done) {
            if (timer != null) {
                timer.stop();
            }
            long t0 = System.nanoTime();
            timer = new Timer(16, null);
            timer.addActionListener(e -> {
                float t = Math.min(1f, (System.nanoTime() - t0) / 1e6f / ms);
                alpha = from + (to - from) * t;
                repaint();
                if (t >= 1f) {
                    timer.stop();
                    if (done != null) {
                        done.run();
                    }
                }
            });
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setColor(new Color(0, 0, 0, Math.round(255 * Math.max(0f, Math.min(1f, alpha)))));
            g.fillRect(0, 0, getWidth(), getHeight());
            if (caption != null) {
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.setFont(new Font("Consolas", Font.BOLD, 18));
                g.setColor(Color.WHITE);
                FontMetrics fm = g.getFontMetrics();
                List<String> lines = new ArrayList<>();
                StringBuilder line = new StringBuilder();
                for (String w : caption.split(" ")) {
                    if (line.length() > 0 && fm.stringWidth(line + " " + w) > 620) {
                        lines.add(line.toString());
                        line = new StringBuilder();
                    }
                    line.append(line.length() > 0 ? " " : "").append(w);
                }
                lines.add(line.toString());
                int y = (getHeight() - lines.size() * fm.getHeight()) / 2 + fm.getAscent();
                for (String s : lines) {
                    g.drawString(s, (getWidth() - fm.stringWidth(s)) / 2, y);
                    y += fm.getHeight();
                }
            }
            g.dispose();
        }
    }
}