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

/**
 * Helper that plays a short story scene on a screen (used by the two endings).
 * shows the background, the character pictures and the dialogue box.
 *
 * A line is {speaker, text}. The speaker can have a mood, like "Alli [angry]".
 * The text can start with {bg:file.png} to change the background.
 */
public class ScenePlayer {

    private static final Pattern MOOD = Pattern.compile("^(.*?)\\s*\\[(\\w+)\\]$");
    private static final int SPRITE_W = 220;
    private static final int SPRITE_H = 300;

    private final JLabel bg;      // full screen background
    private final JLabel left;    // Arnie stands on the left
    private final JLabel right;   // everybody else stands on the right

    public ScenePlayer(JLabel bg, JLabel left, JLabel right) {
        this.bg = bg;
        this.left = left;
        this.right = right;
    }

    /** Plays the lines one by one, then runs "after" (can be null). */
    public void play(String[][] lines, Runnable after) {
        playFrom(lines, 0, after);
    }

    /** Same as above but with nothing to do at the end (method overloading). */
    public void play(String[][] lines) {
        play(lines, null);
    }

    /** Hides both character pictures. */
    public void hideSprites() {
        left.setVisible(false);
        right.setVisible(false);
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

        // split "Alli [angry]" into the name and the mood
        Matcher m = MOOD.matcher(speaker);
        if (m.matches()) {
            speaker = m.group(1);
            showSprite(speaker, m.group(2));
        }

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

    // Looks for a picture like alli_angry.png, then alli.png. If none exists, the label is hidden.
    private void showSprite(String name, String mood) {
        if (name.isEmpty() || name.startsWith("Amie")) {
            return;                               // Amie only lives in the backgrounds
        }
        JLabel target = name.startsWith("Arnie") ? left : right;
        String key = name.toLowerCase().replace(".", "").replace(' ', '_');
        Icon pic = Theme.image(key + "_" + mood + ".png", SPRITE_W, SPRITE_H);
        if (pic == null) {
            pic = Theme.image(key + ".png", SPRITE_W, SPRITE_H);
        }
        if (pic == null) {
            target.setVisible(false);
            return;
        }
        target.setText("");
        target.setIcon(pic);
        target.setForeground(Color.WHITE);
        target.setVisible(true);
    }
}