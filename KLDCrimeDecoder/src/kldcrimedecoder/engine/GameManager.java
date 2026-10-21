package kldcrimedecoder.engine;

import kldcrimedecoder.ui.MainFrame;
import kldcrimedecoder.ui.DialogueBox;
import java.util.HashMap;
import java.util.Map;
import kldcrimedecoder.ui.Sfx;

/**
 * The "boss" class. Starts and ends the game, switches screens,
 * and refreshes the top bar. Everything here is static, so any
 * form can call it, for example GameManager.showCard("level2").
 */
public class GameManager {
    /** Looping sound for each screen: card name -> file name. */
private static final Map<String, String> AMBIENCE = new HashMap<>();
static {
    AMBIENCE.put("menu", "menu_ticking");
    AMBIENCE.put("level2", "keyboard");
    AMBIENCE.put("level7", "keyboard_clock");
    AMBIENCE.put("suspectBoard", "chatter");
    AMBIENCE.put("level8", "hum");
    AMBIENCE.put("level9", "drip");
    AMBIENCE.put("level10", "tap");
}

    private static MainFrame mainFrame;

    /** MainFrame calls this once in its constructor. */
    public static void init(MainFrame frame) {
        mainFrame = frame;
    }
    private static DialogueBox dialogue;

/** MainFrame gives us the dialogue box once. */
public static void setDialogue(DialogueBox box) {
    dialogue = box;
}

/** Levels call GameManager.getDialogue().play(...). Can be null in a test. */
public static DialogueBox getDialogue() {
    return dialogue;
}

    /** Switches the CardLayout to the screen with this name. */
    public static void showCard(String name) {
    if (dialogue != null) {
        dialogue.hideBox();
    }
    Sfx.ambience(AMBIENCE.get(name));        // no entry = silence
    if (mainFrame != null) {
        mainFrame.showCard(name);
    }
}


    /** Called by the Start button. Resets everything and begins level 0. */
    public static void startNewGame() {
        Sfx.clock(0);
        GameState.getInstance().reset();
        LevelManager.reset();
        refreshTopBar();
        TimerManager.start();
        showCard(LevelManager.currentCard());
    }

    /** Updates the timer and score labels in the top bar. */
    public static void refreshTopBar() {
        if (mainFrame == null) {
            return;
        }
        GameState gs = GameState.getInstance();
        mainFrame.setTimerText(gs.getTimeLeftSeconds());
        mainFrame.setScoreText(gs.getScore());
    }

    /**
     * Ends the game and shows the right ending.
     * Called when time runs out or after the last level.
     */
    public static void finishGame() {
        GameState gs = GameState.getInstance();
        if (gs.isGameOver()) {
            return;                       // only finish once
        }
        gs.setGameOver(true);
        TimerManager.stop();
        Sfx.clock(0);
        if (isBadEnding()) {
            showCard("badEnding");
            Sfx.play("bell");                        // the bell ends the break
        } else {
            showCard("goodEnding");
        }
    }

    /** The ending rule from the project spec. */
    public static boolean isBadEnding() {
        GameState gs = GameState.getInstance();
        return gs.getTimeLeftSeconds() <= 0 || gs.getDenialMeter() >= 50;
    }

    /** Used by the ending screens to return to the menu. */
    public static void backToMenu() {
    TimerManager.stop();
    Sfx.clock(0);                      // no clock tick on the menu
    showCard("menu");
    }   
}