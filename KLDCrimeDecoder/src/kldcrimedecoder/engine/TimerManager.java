package kldcrimedecoder.engine;

import java.util.ArrayDeque;
import java.util.Deque;
import javax.swing.Timer;
import kldcrimedecoder.data.Story;
import kldcrimedecoder.ui.DialogueBox;

/**
 * Countdown clock. A javax.swing.Timer ticks once per second on the EDT,
 * so it is safe to update labels from it.
 *
 * It also shows the "time is running out" lines from the script:
 * under 5:00 and under 2:00. A line never interrupts a dialogue that is
 * already open. It waits and appears as soon as the box is free.
 */
public class TimerManager {

    private static final int WARN_FIRST_SEC  = 5 * 60;   // "The hallway clock is louder..."
    private static final int WARN_SECOND_SEC = 2 * 60;   // "Stop reading. Move."

    private static final Timer timer = new Timer(1000, e -> drain(1));

    private static boolean warnedFirst;
    private static boolean warnedSecond;
    private static final Deque<String[]> waiting = new ArrayDeque<>();   // {speaker, text}

    /** Starts the clock (GameManager resets the time first). */
    public static void start() {
        warnedFirst = false;
        warnedSecond = false;
        waiting.clear();
        timer.restart();
    }

    /** Stops the clock completely. */
    public static void stop() {
        timer.stop();
        waiting.clear();
    }

    /** Pause while a dialog is open. Does nothing if already running. */
    public static void resume() {
        if (!GameState.getInstance().isGameOver()) {
            timer.start();
        }
    }

    public static void pause() {
        timer.stop();
    }

    public static boolean isRunning() {
        return timer.isRunning();
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
            return;
        }
        checkWarnings(gs.getTimeLeftSeconds());
        showNextMessage();
    }

    /** A line to show as soon as no dialogue is open. Used here and by ScoreManager. */
    public static void queueMessage(String speaker, String text) {
        waiting.add(new String[] { speaker, text });
        showNextMessage();
    }

    private static void checkWarnings(int timeLeft) {
        if (timeLeft <= WARN_SECOND_SEC && !warnedSecond) {
            warnedSecond = true;
            warnedFirst = true;                       // if one big penalty skipped past 5:00, don't show both
            waiting.clear();
            queueMessage(Story.WARN_SPEAKER, Story.TIMER_2MIN);
        } else if (timeLeft <= WARN_FIRST_SEC && !warnedFirst) {
            warnedFirst = true;
            queueMessage(Story.WARN_SPEAKER, Story.TIMER_5MIN);
        }
    }

    /** Shows one waiting line, but only if the dialogue box is free. */
    private static void showNextMessage() {
        if (waiting.isEmpty()) {
            return;
        }
        DialogueBox box = GameManager.getDialogue();
        if (box == null || box.isOpen()) {
            return;                                   // try again on the next tick
        }
        String[] m = waiting.poll();
        box.say(m[0], m[1]);
    }
}