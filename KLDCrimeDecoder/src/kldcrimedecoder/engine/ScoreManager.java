package kldcrimedecoder.engine;

/**
 * Handles points. Wrong answers and hints also drain time
 * through TimerManager. Change the numbers below to tune the game.
 */
public class ScoreManager {

    private static final int POINTS_CORRECT   = 100;
    private static final int MAX_SPEED_BONUS  = 50;  // for solving fast
    private static final int BONUS_WINDOW_SEC = 60;  // bonus shrinks to 0 over this time

    private static final int WRONG_POINTS   = -10;
    private static final int WRONG_SECONDS  = 15;
    private static final int HINT_POINTS    = -5;
    private static final int HINT_SECONDS   = 20;

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

    /** Call when the player uses a hint. */
    public static void onHint() {
        GameState.getInstance().addScore(HINT_POINTS);
        TimerManager.drain(HINT_SECONDS);
    }
}