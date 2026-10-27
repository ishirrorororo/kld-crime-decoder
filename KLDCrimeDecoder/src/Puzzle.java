package kldcrimedecoder.puzzle;

/**
 * Base class for every puzzle. A level form only needs these three methods:
 *   getPrompt()  -> text for lblPrompt
 *   check(text)  -> true if the answer in txtAnswer is correct
 *   getHint()    -> text for the hint (the level also calls ScoreManager.onHint())
 *
 * Puzzles never touch the score or the timer. The level form does that.
 */
public abstract class Puzzle {

    /** The text shown to the player (goes in lblPrompt). */
    public abstract String getPrompt();

    /** Returns true if the player's answer is correct. */
    public abstract boolean check(String answer);

    /** A hint for this puzzle. */
    public abstract String getHint();

    // ---- small helpers shared by the puzzle classes ----

    /** Lower case, trimmed, and extra spaces collapsed into one. Null becomes "". */
    protected static String clean(String s) {
        if (s == null) {
            return "";
        }
        return s.trim().toLowerCase().replaceAll("\\s+", " ");
    }

    /** Keeps only letters and digits, in lower case. "Meet me, here!" -> "meetmehere". */
    protected static String lettersOnly(String s) {
        if (s == null) {
            return "";
        }
        return s.toLowerCase().replaceAll("[^a-z0-9]", "");
    }
}