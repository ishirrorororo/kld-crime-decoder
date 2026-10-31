package kldcrimedecoder.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import kldcrimedecoder.engine.GameManager;
import java.awt.AlphaComposite;
import java.awt.RenderingHints;
import javax.swing.Timer;
/**
 * Helper that plays a short story scene on a screen (used by the two endings).
 * It shows the background, the character pictures and the dialogue box.
 *
 * A line is {speaker, text}. The speaker can have a mood, like "Alli [angry]".
 * The text can start with tags:
 *   {bg:file.png}    changes the background
 *   {item:file.png}  pops up an item picture (like the key) for that line only
 *
 * Only the person who is talking is shown. Arnie is on the right,
 * everybody else (Alli, Prof. Danel) is on the left.
 */
public class ScenePlayer {

    private static final Pattern MOOD = Pattern.compile("^(.*?)\\s*\\[(\\w+)\\]$");
    private static final int SPRITE_W = 220;
    private static final int SPRITE_H = 300;

    private final JLabel bg;      // full screen background
    private final JLabel left;    // everybody else (the NPCs) stands on the left
    private final JLabel right;   // Arnie stands on the right
    private final JLabel item;    // picture that pops up in the middle (can be null)
    private Timer itemTimer;      // runs the pop-up animation of the item picture
    public ScenePlayer(JLabel bg, JLabel left, JLabel right, JLabel item) {
        this.bg = bg;
        this.left = left;
        this.right = right;
        this.item = item;
    }

    /** Same thing for a screen that has no item picture (constructor overloading). */
    public ScenePlayer(JLabel bg, JLabel left, JLabel right) {
        this(bg, left, right, null);
    }

    /** Plays the lines one by one, then runs "after" (can be null). */
    public void play(String[][] lines, Runnable after) {
        playFrom(lines, 0, after);
    }

    /** Same as above but with nothing to do at the end (method overloading). */
    public void play(String[][] lines) {
        play(lines, null);
    }

    /** Hides both character pictures and the item picture. */
    public void hideSprites() {
        left.setVisible(false);
        right.setVisible(false);
                if (itemTimer != null) {
            itemTimer.stop();                     // stop the pop-up if it is still running
        }
        if (item != null) {
            item.setVisible(false);
        }
    }

    private void playFrom(final String[][] lines, final int i, final Runnable after) {
        if (i >= lines.length) {
            if (after != null) {
                after.run();
            }
            return;
        }
        String speaker = lines[i][0];
        String text = lines[i][1];

        // split "Alli [angry]" into the name and the mood (the mood can be left out)
        String mood = "";
        Matcher m = MOOD.matcher(speaker);
        if (m.matches()) {
            speaker = m.group(1);
            mood = m.group(2);
        }
        hideSprites();                            // every line starts with a clean screen
        showSpeaker(speaker, mood);               // then only the speaker appears

        // tags at the start of the text, {bg:file.png} changes the background
        while (text.startsWith("{")) {
            int end = text.indexOf('}');
            if (end < 0) {
                break;
            }
            String tag = text.substring(1, end);
            text = text.substring(end + 1).trim();
            if (tag.startsWith("bg:")) {
                showBackground(tag.substring(3));
            } else if (tag.startsWith("item:")) {
                showItem(tag.substring(5));
            }
        }

        DialogueBox box = GameManager.getDialogue();
        if (box == null) {                        // no dialogue box, so skip it (testing only)
            playFrom(lines, i + 1, after);
            return;
        }
        box.play(new String[][] { { speaker, text } }, () -> playFrom(lines, i + 1, after));
    }

    /** Changes the background picture. It fills 1000 x 580 and cuts off what does not fit. */
    public void showBackground(String file) {
        java.net.URL url = ScenePlayer.class.getResource("/kldcrimedecoder/images/" + file);
        if (url == null) {                        // file not found, so show a message instead
            bg.setIcon(null);
            bg.setText("[background missing: " + file + "]");
            return;
        }
        Image src = new ImageIcon(url).getImage();
        int w = 1000, h = 580;
        double s = Math.max((double) w / src.getWidth(null), (double) h / src.getHeight(null));
        int dw = (int) Math.ceil(src.getWidth(null) * s);
        int dh = (int) Math.ceil(src.getHeight(null) * s);
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(src, (w - dw) / 2, (h - dh) / 2, dw, dh, null);
        g.dispose();
        bg.setText("");
        bg.setIcon(new ImageIcon(out));
    }

    // Shows the speaker's picture: Arnie on the right, everybody else on the left.
    // It looks for alli_angry.png first, then alli.png. If none exists, nothing is shown.
    private void showSpeaker(String name, String mood) {
        if (name.isEmpty() || name.startsWith("Amie")) {
            return;                               // narration and Amie have no picture
        }
        JLabel target = name.startsWith("Arnie") ? right : left;
        String key = name.toLowerCase().replace(".", "").replace(' ', '_');
        Icon pic = null;
        if (!mood.isEmpty()) {
            pic = Theme.image(key + "_" + mood + ".png", SPRITE_W, SPRITE_H);
        }
        if (pic == null) {
            pic = Theme.image(key + ".png", SPRITE_W, SPRITE_H);
        }
        if (pic == null) {
            return;
        }
        target.setText("");
        target.setIcon(pic);
        target.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        target.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);   // feet stay on the bottom edge
        target.setForeground(Color.WHITE);
        target.setVisible(true);
    }

    // pops up a picture (like the brass key) in the middle of the screen
    // it grows a little too big, then settles back, while fading in
    private void showItem(String file) {
        if (item == null) {
            return;
        }
        Icon pic = Theme.image(file, 170, 170);   // a bit smaller so it has room to grow
        if (pic == null) {
            return;
        }
        // draw the picture on a 200 x 200 transparent image so it can be scaled
        final int size = 200;
        final BufferedImage base = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D bg2 = base.createGraphics();
        pic.paintIcon(null, bg2, (size - pic.getIconWidth()) / 2, (size - pic.getIconHeight()) / 2);
        bg2.dispose();

        item.setText("");
        item.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        item.setVisible(true);

        if (itemTimer != null) {
            itemTimer.stop();
        }
        final long start = System.currentTimeMillis();
        final int duration = 450;                 // milliseconds
        itemTimer = new Timer(15, e -> {
            double t = Math.min(1.0, (System.currentTimeMillis() - start) / (double) duration);
            double c1 = 1.70158;
            double c3 = c1 + 1;
            double scale = 1 + c3 * Math.pow(t - 1, 3) + c1 * Math.pow(t - 1, 2);
            float alpha = (float) Math.min(1.0, t * 3);

            BufferedImage frame = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = frame.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            int w = Math.max(1, (int) (size * scale));
            g.drawImage(base, (size - w) / 2, (size - w) / 2, w, w, null);
            g.dispose();
            item.setIcon(new ImageIcon(frame));

            if (t >= 1.0) {
                ((Timer) e.getSource()).stop();
            }
        });
        itemTimer.start();
    }
}