package kldcrimedecoder.engine;

import kldcrimedecoder.data.Story;

/**
 * Handles points and the hidden Denial meter. Wrong answers and hints also drain
 * time through TimerManager. Change the numbers below to tune the game.
 *
 * Levels call:
 *   ScoreManager.onCorrect()      when a puzzle is solved
 *   ScoreManager.onWrong()        on a wrong answer
 *   ScoreManager.onHint()         when the player uses a hint
 *   ScoreManager.onChoiceRead()   choice moment (Levels 4, 8, 10): "look closer"
 *   ScoreManager.onChoiceSkip()   choice moment: "skip it"
 * The ending screens call getRank() and getSelfAwarenessPercent().
 */
public class ScoreManager {

    // ---- correct answers ----
    private static final int POINTS_CORRECT   = 100;
    private static final int MAX_SPEED_BONUS  = 50;  // for solving fast
    private static final int BONUS_WINDOW_SEC = 60;  // bonus shrinks to 0 over this time

    // ---- wrong answers ----
    private static final int WRONG_POINTS   = -10;
    private static final int WRONG_SECONDS  = 15;

    // ---- hints (script: Hint 1 costs 15 s, Hint 2 costs 30 s and nearly gives the answer) ----
    private static final int   HINT_POINTS  = -5;
    private static final int[] HINT_SECONDS = {15, 30};   // 1st hint on a level, then 2nd and every one after

    // ---- hidden Denial meter ----
    private static final int DENIAL_WARN_ABOVE = 30;      // one-time warning line (script)
    private static final int DENIAL_BAD_AT     = 50;      // same number as GameManager.isBadEnding()
    private static final String KEY_DENIAL_WARNED = "denialWarned";

    // ---- ranks (final screen). Names are in Story.RANK_NAMES, same order ----
    private static final int[] RANK_MIN_SCORE = {0, 300, 600, 900};

    // which level the hint counter belongs to
    private static int hintLevel = -1;
    private static int hintsOnLevel = 0;

    /** Call when the player answers a puzzle correctly. */
    public static void onCorrect() {
        int seconds = LevelManager.getSecondsOnLevel();
        int bonus = MAX_SPEED_BONUS * Math.max(0, BONUS_WINDOW_SEC - seconds) / BONUS_WINDOW_SEC;
        GameState.getInstance().addScore(POINTS_CORRECT + bonus);
        GameManager.refreshTopBar();
    }

    /** Call when the player answers wrong. */
    public static void onWrong() {
        GameState.getInstance().addScore(WRONG_POINTS);
        TimerManager.drain(WRONG_SECONDS);   // also refreshes the top bar
    }

    /** Call when the player uses a hint. The first hint on a level costs less than the second. */
    public static void onHint() {
        int level = LevelManager.currentLevel();
        if (level != hintLevel) {            // new level, count from the start
            hintLevel = level;
            hintsOnLevel = 0;
        }
        int seconds = HINT_SECONDS[Math.min(hintsOnLevel, HINT_SECONDS.length - 1)];
        hintsOnLevel++;
        GameState.getInstance().addScore(HINT_POINTS);
        TimerManager.drain(seconds);         // last, because it can end the game
    }

    /** Choice moment: the player chose to look closer. Costs time, lowers denial. */
    public static void onChoiceRead() {
        addDenial(Story.DENIAL_READ);
        TimerManager.drain(Story.CHOICE_READ_SECONDS);
    }

    /** Choice moment: the player skipped it. Free, but raises denial. */
    public static void onChoiceSkip() {
        addDenial(Story.DENIAL_SKIP);
    }

    /**
     * Raises (positive) or lowers (negative) the hidden Denial meter.
     * The first time it goes above 30, a strange line shows up (once only).
     */
    public static void addDenial(int amount) {
        GameState gs = GameState.getInstance();
        gs.addDenial(amount);
        if (gs.getDenialMeter() > DENIAL_WARN_ABOVE
                && gs.getChoice(KEY_DENIAL_WARNED, "no").equals("no")) {
            gs.putChoice(KEY_DENIAL_WARNED, "yes");          // cleared again by GameState.reset()
            TimerManager.queueMessage("", Story.DENIAL_WARNING);
        }
    }

    /** For the final screen: 100% = never skipped a clue about herself, 0% = denial reached the bad-ending limit. */
    public static int getSelfAwarenessPercent() {
        int denial = GameState.getInstance().getDenialMeter();
        return Math.max(0, 100 - denial * 100 / DENIAL_BAD_AT);
    }

    /** For the final screen: the rank that goes with the current score. */
    public static String getRank() {
        int score = GameState.getInstance().getScore();
        int rank = 0;
        for (int i = 0; i < RANK_MIN_SCORE.length; i++) {
            if (score >= RANK_MIN_SCORE[i]) {
                rank = i;
            }
        }
        return Story.RANK_NAMES[rank];
    }

    /** Forget the hint counter. Call from GameManager.startNewGame(). */
    public static void reset() {
        hintLevel = -1;
        hintsOnLevel = 0;
    }
}