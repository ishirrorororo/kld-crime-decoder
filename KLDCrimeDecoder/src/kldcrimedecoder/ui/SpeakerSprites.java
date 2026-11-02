package kldcrimedecoder.ui;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import kldcrimedecoder.engine.GameManager;

/**
 * Shows the right character picture for every line of dialogue.
 *
 * Each sprite is a JLabel you place in the designer, with its picture set in
 * the Properties window. The level registers them once:
 *     add("jenna", "worried", lblJennaWorried)
 *
 * A Story line looks like {"Jenna [worried]", "text"}: the name, then the mood
 * in square brackets. Before each line this class shows the matching label
 * (or the character's "neutral" label if that mood has none) and hides the
 * others, then plays the line in the DialogueBox, one line at a time like
 * Level 5 does.
 *
 * Special cases:
 *   - A line with no name ("") is narration: no name box, no picture.
 *   - A name starting with "@" shows the picture but NO name box, so the line
 *     reads as narration with a character on screen:
 *         {"@Jenna [scared]", "Behind you, quick footsteps."}
 *
 * Any level can reuse it: register the labels, then call play().
 */
public class SpeakerSprites {

    /** Splits "Jenna [worried]" into the name and the mood. */
    private static final Pattern MOOD = Pattern.compile("^(.*?)\\s*\\[(\\w+)\\]$");
    private static final String DEFAULT_MOOD = "neutral";
    private static final String NARRATION_MARK = "@";

    // "jenna:worried" -> the label that shows Jenna looking worried
    private final Map<String, JLabel> labels = new LinkedHashMap<>();

    /** Registers one sprite label. Returns this, so calls can be chained. */
    public SpeakerSprites add(String character, String mood, JLabel label) {
        label.setText("");
        label.setVisible(false);
        labels.put(key(character, mood), label);
        return this;
    }

    private static String key(String character, String mood) {
        return character.toLowerCase() + ":" + mood.toLowerCase();
    }

    public void hideAll() {
        for (JLabel label : labels.values()) {
            label.setVisible(false);
        }
    }

    /** Plays the lines one by one, changing the picture before each line. */
    public void play(String[][] lines, Runnable after) {
        playFrom(lines, 0, after);
    }

    private void playFrom(String[][] lines, int i, Runnable after) {
        DialogueBox box = GameManager.getDialogue();

        if (box == null || lines == null || i >= lines.length) {
            hideAll();
            if (after != null) {
                after.run();
            }
            return;
        }

        String speaker = lines[i][0] == null ? "" : lines[i][0];
        String mood = DEFAULT_MOOD;

        Matcher m = MOOD.matcher(speaker);          // "Jenna [worried]" -> "Jenna" + "worried"
        if (m.matches()) {
            speaker = m.group(1);
            mood = m.group(2);
        }

        // "@Jenna [scared]": show Jenna's picture, but leave the name box empty
        String nameInBox = speaker;
        if (speaker.startsWith(NARRATION_MARK)) {
            speaker = speaker.substring(NARRATION_MARK.length()).trim();
            nameInBox = "";
        }

        show(speaker, mood);

        box.play(new String[][] { { nameInBox, lines[i][1] } },
                () -> playFrom(lines, i + 1, after));
    }

    /** Shows the label for this speaker and mood, and hides the others. */
    private void show(String speaker, String mood) {
        hideAll();

        // "Arnie (thinking)" -> "arnie"
        String character = speaker.split("[\\s(]")[0];

        JLabel label = labels.get(key(character, mood));
        if (label == null) {
            label = labels.get(key(character, DEFAULT_MOOD));   // no label for this mood
        }
        if (label != null) {
            label.setVisible(true);
        }
    }
}