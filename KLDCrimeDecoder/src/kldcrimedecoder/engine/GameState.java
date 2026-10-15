package kldcrimedecoder.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton that holds everything about the current playthrough.
 * Get it anywhere with: GameState.getInstance()
 */
public class GameState {

    /** Starting time: 15 minutes. */
    public static final int START_SECONDS = 15 * 60;

    /** Key used to store the word the player types for the final cipher. */
    public static final String KEY_FINAL_WORD = "finalKeyWord";

    private static GameState instance;

    private int score;
    private int timeLeftSeconds;
    private int denialMeter;          // hidden until the final screen
    private boolean gameOver;

    private final List<String> inventory = new ArrayList<>();
    private final Map<String, String> playerChoices = new HashMap<>();

    private GameState() {
        reset();
    }

    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    /** Puts everything back to the start of a new game. */
    public void reset() {
        score = 0;
        timeLeftSeconds = START_SECONDS;
        denialMeter = 0;
        gameOver = false;
        inventory.clear();
        playerChoices.clear();
    }

    // ---- score ----
    public int getScore() { return score; }

    public void addScore(int points) {
        score = Math.max(0, score + points);   // never below 0
    }

    // ---- time ----
    public int getTimeLeftSeconds() { return timeLeftSeconds; }

    public void setTimeLeftSeconds(int seconds) {
        timeLeftSeconds = Math.max(0, seconds);
    }

    // ---- denial meter ----
    public int getDenialMeter() { return denialMeter; }

    /** Use a positive number to raise denial, a negative number to lower it. */
    public void addDenial(int amount) {
        denialMeter = Math.max(0, denialMeter + amount);
    }

    // ---- game over flag ----
    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean over) { gameOver = over; }

    // ---- inventory ----
    public void addItem(String item) {
        if (!inventory.contains(item)) {
            inventory.add(item);
        }
    }

    public boolean hasItem(String item) { return inventory.contains(item); }

    public List<String> getInventory() {
        return Collections.unmodifiableList(inventory);
    }

    // ---- player choices ----
    public void putChoice(String key, String value) { playerChoices.put(key, value); }

    public String getChoice(String key, String defaultValue) {
        return playerChoices.getOrDefault(key, defaultValue);
    }
}