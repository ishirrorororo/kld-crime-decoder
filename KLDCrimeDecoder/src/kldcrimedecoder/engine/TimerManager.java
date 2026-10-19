package kldcrimedecoder.engine;

import javax.swing.Timer;

/**
 * Countdown clock. A javax.swing.Timer ticks once per second on the EDT,
 * so it is safe to update labels from it.
 */
public class TimerManager {

    private static final Timer timer = new Timer(1000, e -> drain(1));
    
    /** Starts the clock (GameManager resets the time first). */
    public static void start() {
        timer.restart();
    }

    /** Stops the clock completely. */
    public static void stop() {
        timer.stop();
    }

    /** Pause while a dialog is open. Does nothing if already running. */
    public static void resume() {
        timer.start();
    }

    public static void pause() {
        timer.stop();
    }

    /** Removes seconds from the clock. Also used for penalties. */
    public static void drain(int seconds) {
       
        GameState gs = GameState.getInstance();
        if (gs.isGameOver()) {
            return;
        }
        gs.setTimeLeftSeconds(gs.getTimeLeftSeconds() - seconds);
        GameManager.refreshTopBar();

        if (gs.getTimeLeftSeconds() <= 0) {
            GameManager.finishGame();   // time is up, so this becomes the bad ending
        }
    }
}