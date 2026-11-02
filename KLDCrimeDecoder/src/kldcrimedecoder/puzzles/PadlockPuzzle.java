package kldcrimedecoder.puzzles;

import kldcrimedecoder.data.Story;

/**
 * Level 9: the four-digit padlock.
 *
 * Inherits from the abstract Puzzle and fills in its three methods, like
 * WordScramblePuzzle does. It hides the secret code and the hint counter
 * (encapsulation). Like every puzzle, it never touches the score or the timer.
 */
public class PadlockPuzzle extends Puzzle {

    private final String code;
    private int hintsGiven;

    /** The normal lock: the code comes from Story, nothing is hard coded here. */
    public PadlockPuzzle() {
        this(Story.LOCK_CODE);
    }

    public PadlockPuzzle(String code) {
        this.code = code;
    }

    /** How many digits the lock has. */
    public int length() {
        return code.length();
    }

    @Override
    public String getPrompt() {
        return Story.L9_PROMPT;
    }

    /** True if the four digits (for example "1403") open the lock. */
    @Override
    public boolean check(String answer) {
        return code.equals(lettersOnly(answer));   // lettersOnly() comes from Puzzle
    }

    /** First call gives hint 1, the next gives hint 2, and it stays on the last one. */
    @Override
    public String getHint() {
        int index = Math.min(hintsGiven, Story.L9_HINTS.length - 1);
        hintsGiven++;
        return Story.L9_HINTS[index][1];
    }

    /** How many hints have been shown so far. */
    public int getHintsGiven() {
        return hintsGiven;
    }
}