package kldcrimedecoder.puzzles;

import kldcrimedecoder.data.Story;

/**
 * Acrostic puzzle (Level 6). Five posts on Alli's feed; the first letter of each
 * post spells the answer. This class only holds the puzzle's data and checks the
 * answer. Level6 draws the phone and the scrolling.
 */
public class AcrosticPuzzle extends Puzzle {

    public int getPostCount() {
        return Story.L6_POSTS.length;
    }

    public String getPost(int index) {
        return Story.L6_POSTS[index];
    }

    public String getPostTime(int index) {
        return Story.L6_TIMES[index];
    }

    /** How many letters the answer box should accept. */
    public int getAnswerLength() {
        return Story.L6_ANSWER.length();
    }

    @Override
    public String getPrompt() {
        return Story.L6_PROMPT;
    }

    @Override
    public boolean check(String answer) {
        return lettersOnly(answer).equals(Story.L6_ANSWER.toLowerCase());
    }

    /** First hint (the Puzzle base class asks for a hint with no number). */
    @Override
    public String getHint() {
        return getHint(1);
    }

    /** Hint number 1, 2, ... (stays on the last one if asked for more). */
    public String getHint(int number) {
        int n = Math.max(1, Math.min(number, Story.L6_HINTS.length));
        return Story.L6_HINTS[n - 1];
    }
}