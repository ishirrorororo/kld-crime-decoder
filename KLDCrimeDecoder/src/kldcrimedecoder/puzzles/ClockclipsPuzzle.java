package kldcrimedecoder.puzzles;

import kldcrimedecoder.data.Story;

/**
 * Clock clips puzzle (Level 8). Four camera clips (A, B, C, D) must be dragged into four time slots
 * (7:45 PM, 8:10 PM, 8:30 PM, 9:00 PM). The wall clock in clips A to C shows the time; clip D is the static one.
 * This class only holds the data and checks the order. Level8 draws the screen and the dragging.
 *
 * The answer is a string with one digit per clip, in order A, B, C, D:
 * 0 = not placed yet, 1 to 4 = the slot the clip was dropped in. The right answer is "1234".
 */
public class ClockclipsPuzzle extends Puzzle {

    public int getClipCount() {
        return Story.L8_CLIPS.length;
    }

    /** Caption on the clip (what the player sees), index 0 = clip A. */
    public String getClip(int index) {
        return Story.L8_CLIPS[index];
    }

    /** Label of a time slot, index 0 = first slot. */
    public String getSlot(int index) {
        return Story.L8_SLOTS[index];
    }

    /** The slot (1 to 4) a clip belongs in. */
    public int getCorrectSlot(int clip) {
        return clip + 1;
    }

    @Override
    public String getPrompt() {
        return Story.L8_PROMPT;
    }

    /** True when every clip is in its own slot. */
    @Override
    public boolean check(String answer) {
        if (answer == null || answer.length() != getClipCount()) {
            return false;
        }
        for (int i = 0; i < getClipCount(); i++) {
            if (answer.charAt(i) - '0' != getCorrectSlot(i)) {
                return false;
            }
        }
        return true;
    }

    /** True when all four clips have been placed in some slot. */
    public boolean isFullyPlaced(String answer) {
        return answer != null && answer.length() == getClipCount() && answer.indexOf('0') < 0;
    }

    @Override
    public String getHint() {
        return getHint(1);
    }

    /** Hint number 1, 2, ... (stays on the last one if asked for more). */
    public String getHint(int number) {
        int n = Math.max(1, Math.min(number, Story.L8_HINTS.length));
        return Story.L8_HINTS[n - 1];
    }
}