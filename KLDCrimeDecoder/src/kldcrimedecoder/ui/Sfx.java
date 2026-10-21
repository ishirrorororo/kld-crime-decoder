/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kldcrimedecoder.ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
/**
 *
 * @author Joel
 */

/**
 * All the game's sound. Files live in /kldcrimedecoder/sfx/name.wav.
 *   Sfx.play("chime")         a short sound, once
 *   Sfx.ambience("hum")       the looping sound of this screen (null = silence)
 *   Sfx.clock(0.5)            the ticking clock, 0 = off, 1 = loud
 * A missing file is silent. It never stops the game.
 */
public class Sfx {

    private static final Map<String, byte[]> cache = new HashMap<>();   // files, read once

    private static String ambienceName;      // the loop we WANT to hear
    private static Clip ambienceClip;
    private static Clip clockClip;
    private static double clockVolume = 0;
    private static boolean muted = false;

    /** Plays a short sound once. */
    public static void play(String name) {
        play(name, 1.0);
    }

    public static void play(String name, double volume) {
        if (muted) {
            return;
        }
        final Clip clip = open(name);
        if (clip == null) {
            return;
        }
        setVolume(clip, volume);
        clip.addLineListener(ev -> {
            if (ev.getType() == LineEvent.Type.STOP) {
                clip.close();                          // free the sound when it ends
            }
        });
        clip.start();
    }

    /** The looping sound of the current screen. Pass null for silence. */
    public static void ambience(String name) {
        boolean same = name != null && name.equals(ambienceName)
                && ambienceClip != null && ambienceClip.isRunning();
        if (same) {
            return;                                    // already playing, no restart
        }
        ambienceName = name;
        restartAmbience();
    }

    private static void restartAmbience() {
        stopClip(ambienceClip);
        ambienceClip = null;
        if (ambienceName == null || muted) {
            return;
        }
        ambienceClip = open(ambienceName);
        if (ambienceClip != null) {
            setVolume(ambienceClip, 0.5);              // loops sit below the one-shots
            ambienceClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /** The ticking clock (file clock_tick). 0 = off, 1 = loud. */
    public static void clock(double volume) {
        clockVolume = volume;
        if (volume <= 0 || muted) {
            stopClip(clockClip);
            clockClip = null;
            return;
        }
        if (clockClip == null) {
            clockClip = open("clock_tick");
            if (clockClip == null) {
                return;
            }
            clockClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
        setVolume(clockClip, volume);
    }

    public static void stopAll() {
        ambience(null);
        clock(0);
    }

    /** For a mute button later. */
    public static void setMuted(boolean value) {
        muted = value;
        if (muted) {
            stopClip(ambienceClip);
            ambienceClip = null;
            stopClip(clockClip);
            clockClip = null;
        } else {
            restartAmbience();
            clock(clockVolume);
        }
    }

    public static boolean isMuted() {
        return muted;
    }

    // ------------------------------------------------------------ helpers

   private static final boolean DEBUG = true;    // prints why a sound is silent. Set false when it works

private static Clip open(String name) {
    try {
        byte[] data = cache.get(name);
        if (data == null) {
            try (InputStream in = Sfx.class.getResourceAsStream("/kldcrimedecoder/sfx/" + name + ".wav")) {
                if (in == null) {
                    if (DEBUG) {
                        System.err.println("Sfx: file not found: /kldcrimedecoder/sfx/" + name + ".wav");
                    }
                    return null;
                }
                data = in.readAllBytes();
            }
            cache.put(name, data);
        }
        
        // 1. Get a temporary stream to read the base audio format
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        AudioInputStream baseAudio = AudioSystem.getAudioInputStream(bais);
        javax.sound.sampled.AudioFormat format = baseAudio.getFormat();
        
        // 2. Explicitly calculate the total frame length to prevent negative/overflow bugs
        long frameLength = data.length / format.getFrameSize();
        
        // 3. Reconstruct a safe stream with the precise frame length
        AudioInputStream audio = new AudioInputStream(new ByteArrayInputStream(data), format, frameLength);
        
        Clip clip = AudioSystem.getClip();
        clip.open(audio);
        return clip;
    } catch (Exception e) {
        if (DEBUG) {
            System.err.println("Sfx: cannot play " + name + ": " + e);
            e.printStackTrace(); // Added to see the exact stack trace if it still fails
        }
        return null;
    }
}
    private static void setVolume(Clip clip, double volume) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            double v = Math.max(0.0001, Math.min(1.0, volume));
            float db = (float) (20.0 * Math.log10(v));
            gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), db)));
        }
    }

    private static void stopClip(Clip clip) {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }
}
