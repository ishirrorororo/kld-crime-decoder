package kldcrimedecoder.puzzles;

/**
 * ABSTRACTION: every puzzle in the game has hints and can check an answer.
 * The level panels only talk to this type, not to the details of each puzzle.
 */
public abstract class Puzzle {

    private final String[] hints;   // ENCAPSULATION: private, read through methods
    private int hintsUsed;

    protected Puzzle(String[] hints) {
        this.hints = (hints == null) ? new String[0] : hints.clone();
    }

    /** Each puzzle decides what "correct" means. */
    public abstract boolean check(String answer);

    /** Hint 1, then hint 2, then the last hint again. */
    public String nextHint() {
        if (hints.length == 0) {
            return "";
        }
        int i = Math.min(hintsUsed, hints.length - 1);
        hintsUsed++;
        return "Hint " + (i + 1) + ": " + hints[i];
    }

    public int getHintsUsed() {
        return hintsUsed;
    }

    public void reset() {
        hintsUsed = 0;
    }
}