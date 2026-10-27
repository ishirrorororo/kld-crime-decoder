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

    public String getScrambled() {
        return scrambled;
    }

    public String getWord() {
        return word;
    }

    public String getPrompt() {
        return "A note on shelf 005 is smudged:  " + scrambled
             + "\nUnscramble it to find the right book.";
    }

    public boolean check(String answer) {
        return lettersOnly(answer).equals(word.toLowerCase());
    }

    public String getHint() {
        return Story.HINT_SCRAMBLE;
    }

    @Override
    public String getTitle() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean isSolved() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getHint(int number) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private Object lettersOnly(String answer) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}