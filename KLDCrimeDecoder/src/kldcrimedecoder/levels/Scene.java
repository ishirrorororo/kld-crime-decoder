package kldcrimedecoder.levels;

/**
 * ABSTRACTION: every story screen promises to know how to start itself.
 * The game engine only needs this one method, not the details of each level.
 */
public interface Scene {

    /** Resets the screen and plays it from the beginning. */
    void startScene();
}