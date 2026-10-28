package kldcrimedecoder.puzzles;

import java.util.Random;
import kldcrimedecoder.data.Story;

/**
 * Word scramble. The player unscrambles a word, and that word
 * tells them which book to pick from the JList.
 */
public class WordScramblePuzzle implements Puzzle {

    private final String word;        // the answer, in capitals
    private final String scrambled;   // the mixed-up letters
    private boolean solved = false;   // becomes true after a correct answer

    public WordScramblePuzzle(String word) {
        this.word = word.toUpperCase();
        this.scrambled = scramble(this.word);
    }

    /** Mixes the letters. The seed is fixed, so the result is always the same. */
    private static String scramble(String w) {
        char[] letters = w.toCharArray();
        Random rnd = new Random(w.hashCode());
        // Try a few times so the result is different from the original word.
        for (int attempt = 0; attempt < 20; attempt++) {
            for (int i = letters.length - 1; i > 0; i--) {   // Fisher-Yates shuffle
                int j = rnd.nextInt(i + 1);
                char tmp = letters[i];
                letters[i] = letters[j];
                letters[j] = tmp;
            }
            if (!new String(letters).equals(w)) {
                break;
            }
        }
        return new String(letters);
    }

    /** Removes everything except letters and lowercases the result. */
    private static String lettersOnly(String s) {
        return s.replaceAll("[^A-Za-z]", "").toLowerCase();
    }

    public String getScrambled() {
        return scrambled;
    }

    public String getWord() {
        return word;
    }

    // ---- Methods required by the Puzzle interface ----

    @Override
    public String getTitle() {
        return "Word Scramble";
    }

    @Override
    public boolean isSolved() {
        return solved;
    }

    @Override
    public String getHint(int number) {
        return Story.HINT_SCRAMBLE;
    }

    // ---- Extra methods (not in Puzzle, so no @Override) ----

    public String getPrompt() {
        return "A note on shelf 005 is smudged:  " + scrambled
             + "\nUnscramble it to find the right book.";
    }

    public boolean check(String answer) {
        boolean correct = lettersOnly(answer).equals(word.toLowerCase());
        if (correct) {
            solved = true;
        }
        return correct;
    }
}