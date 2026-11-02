package kldcrimedecoder.puzzles;

import java.util.Random;

/**
 * Same calls your teammates already use in Level 3:
 *   new WordScramblePuzzle(Story.SCRAMBLE_WORD)   and   puzzle.check(word)
 * If you already have this file, keep yours and only compare it with this one.
 */
public class WordScramblePuzzle extends Puzzle {

    private final String word;
    private final String scrambled;

    public WordScramblePuzzle(String word) {
        super(null);
        this.word = word.toUpperCase();
        this.scrambled = shuffle(this.word);
    }

    private static String shuffle(String w) {
        char[] c = w.toCharArray();
        Random rnd = new Random();
        for (int attempt = 0; attempt < 20; attempt++) {      // try not to return the same word
            for (int i = c.length - 1; i > 0; i--) {
                int j = rnd.nextInt(i + 1);
                char t = c[i];
                c[i] = c[j];
                c[j] = t;
            }
            if (!new String(c).equals(w)) {
                break;
            }
        }
        return new String(c);
    }

    public String getScrambled() {
        return scrambled;
    }

    @Override
    public boolean check(String answer) {
        return answer != null && answer.trim().equalsIgnoreCase(word);
    }

    /** POLYMORPHISM (overloading): the same check, but from tiles/characters. */
    public boolean check(char[] letters) {
        return check(new String(letters));
    }
}