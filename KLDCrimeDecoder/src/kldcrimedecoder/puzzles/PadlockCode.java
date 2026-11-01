package kldcrimedecoder.puzzles;

/**
 * The padlock's secret code. Plain Java, no Swing, so it only knows the
 * rules of the puzzle and nothing about how it looks on screen.
 */
public class PadlockCode {

    private final String code;

    public PadlockCode(String code) {
        this.code = code;
    }

    /** How many digits the lock has. */
    public int length() {
        return code.length();
    }

    /** True if the attempt (for example "1403") opens the lock. */
    public boolean check(String attempt) {
        return code.equals(attempt);
    }
}
