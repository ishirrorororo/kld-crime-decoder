package kldcrimedecoder.puzzles;

import kldcrimedecoder.data.Story;

/**
 * Log sort puzzle (Level 7). Six login entries must be sorted into two columns:
 * 1 = AimeeF (Computer 4, always logs out), 2 = AMIE (Computer 7, never logs out).
 * This class only holds the data and checks the sorting. Level7 draws the screen and the dragging.
 *
 * The answer is a string with one digit per entry, in order: 0 = not sorted yet,
 * 1 = AimeeF column, 2 = AMIE column. For example "121212".
 */
public class LogSortPuzzle extends Puzzle {

    public static final int UNSORTED = 0, AIMEE = 1, AMIE = 2;

    public int getEntryCount() {
        return Story.L7_ENTRIES.length;
    }

    public String getEntry(int index) {
        return Story.L7_ENTRIES[index];
    }

    /** The column an entry belongs in (AIMEE or AMIE). */
    public int getCorrectColumn(int index) {
        return Story.L7_CORRECT[index];
    }

    @Override
    public String getPrompt() {
        return Story.L7_PROMPT;
    }

    /** True when every entry is in the right column. */
    @Override
    public boolean check(String answer) {
        if (answer == null || answer.length() != getEntryCount()) {
            return false;
        }
        for (int i = 0; i < getEntryCount(); i++) {
            if (answer.charAt(i) - '0' != getCorrectColumn(i)) {
                return false;
            }
        }
        return true;
    }

    /** True when every entry has been put in some column (none left in the tray). */
    public boolean isFullySorted(String answer) {
        return answer != null && answer.length() == getEntryCount() && answer.indexOf('0') < 0;
    }

    @Override
    public String getHint() {
        return getHint(1);
    }

    /** Hint number 1, 2, ... (stays on the last one if asked for more). */
    public String getHint(int number) {
        int n = Math.max(1, Math.min(number, Story.L7_HINTS.length));
        return Story.L7_HINTS[n - 1];
    }
}