package kldcrimedecoder.puzzles;

public interface Puzzle {

    String getTitle();

    boolean isSolved();

    /** Returns the hint for the given number (1 = first hint). */
    String getHint(int number);
}