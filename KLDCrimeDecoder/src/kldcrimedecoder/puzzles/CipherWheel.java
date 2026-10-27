package kldcrimedecoder.puzzles;

/**
 * Logic of the cipher wheel (no GUI here).
 * ENCAPSULATION: private fields, access only through methods.
 * ABSTRACTION: implements Puzzle.
 * POLYMORPHISM: decode() is overloaded; getHint() is overloaded;
 * toString() is overridden.
 */
public class CipherWheel implements Puzzle {

    public static final int ALPHABET = 26;
    /** -1 = each click moves letters BACK one place (ASSH -> MEET after 14). */
    private static final int DIRECTION = -1;
    private static final String[] HINTS = {
        "Amie says the day you were born. Check the wallpaper.",
        "The reminder says March 14. Turn the wheel 14."
    };

    private final String coded;
    private final String plain;
    private int shift;
    private int hintsUsed;

    public CipherWheel(String coded, String plain) {
        this.coded = coded.toUpperCase();
        this.plain = plain.toUpperCase();
    }

    // ---------- wheel ----------
    public void rotate(int clicks) {
        shift = Math.floorMod(shift + clicks, ALPHABET);
    }

    public int getShift() {
        return shift;
    }

    public String getCoded() {
        return coded;
    }

    /** Decodes the coded text with the CURRENT shift. */
    public String decode() {
        return decode(coded, shift);
    }

    /** Decodes any text with any shift (overload). */
    public String decode(String text, int amount) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                int moved = Math.floorMod((c - 'A') + DIRECTION * amount, ALPHABET);
                sb.append((char) ('A' + moved));
            } else {
                sb.append(c);                       // spaces stay
            }
        }
        return sb.toString();
    }

    /** Letter shown on the inner ring under outer letter index i. */
    public char innerLetterAt(int i) {
        return (char) ('A' + Math.floorMod(i + DIRECTION * shift, ALPHABET));
    }

    // ---------- Puzzle ----------
    @Override
    public String getTitle() {
        return "Cipher wheel";
    }

    @Override
    public boolean isSolved() {
        return decode().equals(plain);
    }

    @Override
    public String getHint(int number) {
        int i = Math.max(1, Math.min(number, HINTS.length));
        return HINTS[i - 1];
    }

    /** Next unused hint (overload without a number). */
    public String getHint() {
        if (hintsUsed < HINTS.length) {
            hintsUsed++;
        }
        return getHint(hintsUsed);
    }

    @Override
    public String toString() {
        return getTitle() + " [shift=" + shift + "]";
    }
}