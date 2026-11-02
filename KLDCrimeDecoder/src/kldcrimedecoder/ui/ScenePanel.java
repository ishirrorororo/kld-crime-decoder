package kldcrimedecoder.ui;

import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import javax.swing.JPanel;
import kldcrimedecoder.engine.GameManager;

/**
 * Base class for every story scene (Prologue 0A/0B/0C, Level 1).
 * It does what Level 3, 4 and 5 each do by hand:
 *   - calls startScene() every time the card is shown
 *   - plays dialogue lines in the DialogueBox
 *   - reads "Name [mood]" and "{tag}" in a line (same format as Level 5)
 *
 * ABSTRACTION: startScene() is abstract, so each scene decides its own start.
 * POLYMORPHISM: scenes override onMood() and onTag() to react.
 * In a scene's form, change
 *     "extends javax.swing.JPanel"  to  "extends kldcrimedecoder.ui.ScenePanel"
 */
public abstract class ScenePanel extends JPanel {

    protected static final Color BAD = new Color(0xFF6B6B);
    private static final Pattern MOOD = Pattern.compile("^(.*?)\\s*\\[(\\w+)\\]$");   // "Amy [angry]"
    private static final Pattern TAG = Pattern.compile("^\\{(\\w+)\\}\\s*(.*)$");     // "{slow} text"

    protected ScenePanel() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                startScene();
            }
        });
    }

    /** Called every time this card is shown. Reset your scene here. */
    protected abstract void startScene();

    /** Children can react when a line says "Name [mood]". Default: nothing. */
    protected void onMood(String mood) {      
    }

    protected void onSpeaker(String speaker) {
    }
    
    /** Children can react to {slow}, {flicker}, {shake}... Default: nothing. */
    protected void onTag(String tag) {
    }

    /** Our panels overlap (buttons on top of a background label), so repaint them in order. */
    @Override
    public boolean isOptimizedDrawingEnabled() {
        return false;
    }

    /** Plays lines in the dialogue box. "after" runs when the last line ends. */
    protected void speak(String[][] lines, Runnable after) {
        DialogueBox box = GameManager.getDialogue();
        if (box == null) {                       // testing a form alone
            if (after != null) {
                after.run();
            }
            return;
        }
        box.play(lines, after);
    }

    /** Like speak(), but reads moods and tags line by line. */
    protected void scene(String[][] lines, Runnable after) {
        playFrom(lines, 0, after);
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

        String mood = null;
        Matcher m = MOOD.matcher(speaker);
        if (m.matches()) {
            speaker = m.group(1);
            mood = m.group(2);
        }
        onSpeaker(speaker);                      // first: who is talking
        if (mood != null) {
            onMood(mood);                        // then: which face
        }

        Matcher t = TAG.matcher(text);
        if (t.matches()) {
            onTag(t.group(1));
            text = t.group(2);
        }

        DialogueBox box = GameManager.getDialogue();
        if (box == null) {
            playFrom(lines, i + 1, after);
            return;
        }
        String shown = speaker.equals("Narrator") ? "" : speaker;   // narration has no name box
        box.play(new String[][] { { shown, text } }, () -> playFrom(lines, i + 1, after));
    }

    /** Amber text = good, red = wrong. Empty text hides the label. */
    protected void showFeedback(JLabel label, String text, boolean good) {
        label.setForeground(good ? Theme.AMBER : BAD);
        label.setText("<html>" + text.replace("\n", "<br>") + "</html>");
        label.setVisible(!text.isEmpty());
    }
}