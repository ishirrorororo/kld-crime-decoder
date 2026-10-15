package kldcrimedecoder.engine;

/**
 * Knows the order of the levels. Levels call LevelManager.nextLevel()
 * when the player finishes them.
 */
public class LevelManager {
    
    
    /** TESTING ONLY: jump straight to a level (0 to 10). */
public static void jumpTo(int level) {
    index = Math.max(0, Math.min(level, CARDS.length - 1));
    levelStartMillis = System.currentTimeMillis();
    GameManager.showCard(CARDS[index]);
}


    /** Card names, in order. They must match the names used in MainFrame. */
    private static final String[] CARDS = {
        "level0", "level1", "level2", "level3", "level4", "level5",
        "level6", "level7", "level8", "level9", "level10"
    };

    private static int index = 0;
    private static long levelStartMillis = System.currentTimeMillis();

    /** Back to level 0. */
    public static void reset() {
        index = 0;
        levelStartMillis = System.currentTimeMillis();
    }

    public static String currentCard() { return CARDS[index]; }

    public static int currentLevel() { return index; }

    /** Moves to the next level. After level 10, the game ends. */
    public static void nextLevel() {
        if (GameState.getInstance().isGameOver()) {
            return;
        }
        if (index < CARDS.length - 1) {
            index++;
            levelStartMillis = System.currentTimeMillis();
            GameManager.showCard(CARDS[index]);
        } else {
            GameManager.finishGame();
        }
    }

    /** Seconds spent on the current level (used for the speed bonus). */
    public static int getSecondsOnLevel() {
        return (int) ((System.currentTimeMillis() - levelStartMillis) / 1000);
    }
}