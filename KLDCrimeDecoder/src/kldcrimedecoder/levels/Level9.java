/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package kldcrimedecoder.levels;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import kldcrimedecoder.data.Story;
import kldcrimedecoder.engine.GameManager;
import kldcrimedecoder.engine.LevelManager;
import kldcrimedecoder.engine.ScoreManager;
import kldcrimedecoder.puzzles.PadlockPuzzle;
import kldcrimedecoder.ui.CombinationWheel;
import kldcrimedecoder.ui.DialogueBox;
import kldcrimedecoder.ui.PopIn;
import kldcrimedecoder.ui.Theme;
import kldcrimedecoder.ui.SpeakerSprites;
import kldcrimedecoder.ui.Shake;

import java.util.HashMap;
import java.util.Map;
import kldcrimedecoder.ui.Fade;
import kldcrimedecoder.ui.GlowBorder;

public class Level9 extends JPanel {
    
    private SpeakerSprites sprites;

    private static final int DESIGN_WIDTH = 1000;
    private static final int DESIGN_HEIGHT = 600;

    private static final Color BAD =
            new Color(0xFF6B6B);

    // the secret code comes from Story.LOCK_CODE, nothing is typed here
    private PadlockPuzzle lock =
            new PadlockPuzzle();

    // wheel1 to wheel4 are placed in the designer
    private CombinationWheel[] wheels;
    
    private enum PhoneScreen { HOME, WIFI, VOICEMAIL, FACEBOOK }
    
    private static final int POP_FROM_DY = -160;
    private static final int POP_MS = 1000;
    
    private static final int PHONE_FROM_DY = 220;
    private static final int PHONE_POP_MS = 400;
    
    private static final int SHAKE_PX = 12;      // how far it swings each way
    private static final int SHAKE_MS = 450;     // how long the shake lasts
    private static final int SHAKE_COUNT = 4;    // how many left-right swings
    
    private static final int SCENE_SHAKE_PX = 10;
    private static final int SCENE_SHAKE_MS = 500;
    private static final int SCENE_SHAKE_COUNT = 5;

    private Shake sceneShake;
    
    private static final int UNLOCK_HOP_PX = -24;     // how far the open lock snaps from
    private static final int UNLOCK_HOP_MS = 350;
    private static final int UNLOCK_HOLD_MS = 1200;   // how long the open lock stays on screen
    
    // dark and light spells in milliseconds, starting dark
        private static final int[] FLICKER_MS = {
        70, 90, 50, 140, 60, 70, 40, 160,
        80, 120, 50, 90, 60, 110, 70, 260
    };
        
    private static final int SCENE_FADE_MS = 600;

    private final Map<JLabel, Fade> fades = new HashMap<>();
    private JLabel currentScene;
        
    private static final int NOTE_FADE_MS = 400;

    private Fade noteFade;
    private PopIn notePop;

    private Timer flickerTimer;
    private int flickerStep;

    private PopIn unlockHop;
    private Timer unlockTimer;

private Shake padlockShake;

    private PopIn padlockPopIn;
    
    private PopIn phonePopIn;

    private final JLabel lblTitle =
            new JLabel("The Hallway Closet");

    private final JButton btnPhone =
            new JButton("Phone");

    private final JLabel lblFeedback =
            new JLabel();

    private final JButton btnUnlock =
            new JButton("Try Lock");

    private final JButton btnHint =
            new JButton("Hint");

    private final JButton btnContinue =
            new JButton("Continue");

    private boolean puzzleActive;
    private boolean padlockClickable;
    private boolean barVisible = true;     // is the bottom bar allowed to show?
    private boolean hasFeedback;           // does the feedback box have text?

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Level9() {

        initComponents();
        
        noteFade = new Fade(lblNote);
        notePop = new PopIn(lblNote, 0, 40, 400);      // the note rises 40 pixels into place
        makeHotspot(btnNote);
        GlowBorder.onHover(btnPadlock, btnNote);
        
        fades.put(lblSceneDoor, new Fade(lblSceneDoor));
        fades.put(lblSceneCloset, new Fade(lblSceneCloset));
        
        makeHotspot(btnAppWifi, btnAppVoicemail, btnAppFacebook, btnPhoneNav);
        showPhoneScreen(PhoneScreen.HOME);
        padlockPopIn = new PopIn(pnlLockArtDesign, 0, POP_FROM_DY, POP_MS);
        padlockShake = new Shake(pnlLockArtDesign, SHAKE_PX, SHAKE_MS, SHAKE_COUNT);
        
        sceneShake = new Shake(
            SCENE_SHAKE_PX, SCENE_SHAKE_MS, SCENE_SHAKE_COUNT,
            jLabel1, lblSceneDoor, lblSceneCloset,
            lblJennaScared, lblJennaSuspicious, lblJennaNeutral, lblJennaConfused);
        
        unlockHop = new PopIn(lblUnLockArt, 0, UNLOCK_HOP_PX, UNLOCK_HOP_MS);
        phonePopIn = new PopIn(pnlPhone, 0, PHONE_FROM_DY, PHONE_POP_MS);
        pnlLockArtDesign.setVisible(false);
        lblBgPuzzle.setVisible(false);
        
        lblFlicker.setVisible(false);
        lblNote.setVisible(false);
        btnNote.setVisible(false);
        lblSceneDoor.setVisible(false);
        lblSceneCloset.setVisible(false);
        lblUnLockArt.setVisible(false);
        
        fades.put(lblSceneSecondDoor, new Fade(lblSceneSecondDoor));
        lblSceneSecondDoor.setVisible(false);
        
        sprites = new SpeakerSprites()
            .add("jenna", "scared", lblJennaScared)
            .add("jenna", "suspicious", lblJennaSuspicious)
            .add("jenna", "neutral", lblJennaNeutral)
            .add("jenna", "confused", lblJennaConfused)

            .effect("flicker", this::flicker)
            .effect("shake", () -> sceneShake.start())
            .effect("door", () -> showScene(lblSceneDoor))
            .effect("closet", () -> showScene(lblSceneCloset))
            .effect("hidenote", this::hideNote)
            .effect("seconddoor", () -> showScene(lblSceneSecondDoor))
            .effect("hallway", () -> showScene(null));

        // PADLOCK BUTTON
        btnPadlock.setText("");
        btnPadlock.setContentAreaFilled(false);
        btnPadlock.setOpaque(false);
        btnPadlock.setFocusPainted(false);
        btnPadlock.setBorderPainted(false);
        btnPadlock.setRolloverEnabled(true);

        btnPadlock.setCursor(
                new java.awt.Cursor(
                        java.awt.Cursor.HAND_CURSOR));

        setPreferredSize(
                new java.awt.Dimension(
                        DESIGN_WIDTH, DESIGN_HEIGHT));

        setBackground(Theme.BG);

        // =====================================================
        // LEVEL TITLE
        // =====================================================

        Theme.heading(lblTitle);

        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(0x0B1220));

        lblTitle.setBorder(
                BorderFactory.createEmptyBorder(2, 8, 2, 8));

        add(lblTitle,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(
                        14, 524, 170, 30));

        // =====================================================
        // WHEELS (placed in the designer)
        // =====================================================

        wheels = new CombinationWheel[] {
            wheel1, wheel2, wheel3, wheel4
        };

        for (CombinationWheel wheel : wheels) {
            wheel.setEnabled(false);
        }

        // =====================================================
        // PHONE BUTTON
        // =====================================================

        btnPhone.addActionListener(
                e -> togglePhone());

        add(btnPhone,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(
                        14, 14, 110, 34));

        pnlPhone.setVisible(false);

        // =====================================================
        // FEEDBACK
        // =====================================================

        Theme.label(lblFeedback);

        lblFeedback.setOpaque(true);

        lblFeedback.setBackground(
                new Color(0x0B1220));

        lblFeedback.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Theme.AMBER, 1),
                        BorderFactory.createEmptyBorder(
                                4, 8, 4, 8)));

        add(lblFeedback,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(
                        172, 516, 460, 56));

        // =====================================================
        // TRY LOCK BUTTON
        // =====================================================

        btnUnlock.addActionListener(
                e -> checkCode());

        add(btnUnlock,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(
                        642, 524, 105, 34));

        // =====================================================
        // HINT BUTTON
        // =====================================================

        btnHint.addActionListener(
                e -> showHint());

        add(btnHint,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(
                        756, 524, 100, 34));

        // =====================================================
        // CONTINUE BUTTON
        // =====================================================

        btnContinue.addActionListener(
                e -> LevelManager.nextLevel());

        add(btnContinue,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(
                        866, 524, 118, 34));

        // =====================================================
        // BUTTON STYLING
        // =====================================================

        Theme.button(
                btnHint,
                btnContinue,
                btnPhone,
                btnPhoneClose,
                btnUnlock);

        // =====================================================
        // INITIAL BUTTON STATES
        // =====================================================

        btnContinue.setEnabled(false);
        btnUnlock.setEnabled(false);
        btnHint.setEnabled(false);
        btnPhone.setEnabled(false);

        btnPadlock.setEnabled(false);
        padlockClickable = false;

        // =====================================================
        // START LEVEL WHEN DISPLAYED
        // =====================================================

        addComponentListener(
                new ComponentAdapter() {

            @Override
            public void componentShown(ComponentEvent e) {
                startLevel();
            }
        });

        // =====================================================
        // COMPONENT LAYERING (keep these as the LAST lines)
        // Components added after the background would hide behind it.
        // =====================================================

        int n = getComponentCount();

        setComponentZOrder(btnPadlock, 0);
        setComponentZOrder(jLabel1, n - 1);          // hallway: the very back
        setComponentZOrder(lblBgPuzzle, n - 2);      // padlock close-up
        setComponentZOrder(lblSceneDoor, n - 3);     // open door
        setComponentZOrder(lblSceneCloset, n - 4);   // closet: in front of the door
        setComponentZOrder(lblSceneSecondDoor, n - 5);
    }

    // =========================================================
    // START LEVEL
    // =========================================================

    private void startLevel() {
        
        setBottomBar(true);

        lock = new PadlockPuzzle();      // a fresh lock and fresh hints

        puzzleActive = false;
        
        padlockClickable = false;

        pnlLockArtDesign.setVisible(false);
        
        padlockPopIn.stop();
        
        padlockShake.stop();
        
        stopFlicker();
        
        hideNote();
        btnNote.setVisible(false);
        
        showScene(null);
        
        sceneShake.stop();
        
        unlockHop.stop();

        if (unlockTimer != null) {
            unlockTimer.stop();
        }

        lblUnLockArt.setVisible(false);
        lblLockArt.setVisible(true);
        
        lblBgPuzzle.setVisible(false);

        btnPadlock.setVisible(true);

        btnPadlock.setEnabled(false);

        pnlPhone.setVisible(false);

        sprites.hideAll();
        
        GlowBorder.clear(btnPadlock, btnNote);

        lblTitle.setText(
                Story.LEVEL_TITLES[9]);

        setFeedback("", true);

        btnContinue.setEnabled(false);
        btnUnlock.setEnabled(false);
        btnHint.setEnabled(false);
        btnPhone.setEnabled(false);

        for (CombinationWheel wheel : wheels) {
            wheel.setDigit(0);
            wheel.setEnabled(false);
        }

        speak(
                Story.L9_INTRO,
                () -> {

                    padlockClickable = true;
                    btnPadlock.setEnabled(true);

                    setFeedback(
                            Story.L9_CLICK_PROMPT,
                            true);
                });
    }

    // =========================================================
    // BEGIN PUZZLE
    // =========================================================

    private void beginPuzzle() {

        puzzleActive = true;

        lblBgPuzzle.setVisible(true);
        GlowBorder.clear(btnPadlock);
        btnPadlock.setVisible(false);

        setComponentZOrder(pnlLockArtDesign, 0);

        setFeedback(Story.L9_PROMPT, true);

        padlockPopIn.start(() -> {
            for (CombinationWheel wheel : wheels) {
                wheel.setEnabled(true);
            }
            btnUnlock.setEnabled(true);
            btnHint.setEnabled(true);
            btnPhone.setEnabled(true);
        });
    }

    // =========================================================
    // CHECK CODE
    // =========================================================

    private void checkCode() {

        if (!puzzleActive) {
            return;
        }

        StringBuilder attempt =
                new StringBuilder();

        for (CombinationWheel wheel : wheels) {
            attempt.append(wheel.getDigit());
        }

        if (lock.check(attempt.toString())) {
            onSolved();
        } else {
            onWrong();
        }
    }

    // =========================================================
    // WRONG ANSWER
    // =========================================================

    private void onWrong() {

        ScoreManager.onWrong();

        setFeedback(
                Story.L9_WRONG, false);

        padlockShake.start();
    }

    // =========================================================
    // PUZZLE SOLVED
    // =========================================================

    private void onSolved() {

        puzzleActive = false;

        for (CombinationWheel wheel : wheels) {
            wheel.setEnabled(false);
        }

        ScoreManager.onCorrect();

        btnUnlock.setEnabled(false);
        btnHint.setEnabled(false);
        btnPhone.setEnabled(false);

        closePhone();

        setFeedback(
                Story.L9_RIGHT, true);

        playUnlock(() -> {
            pnlLockArtDesign.setVisible(false);

            speak(Story.L9_SOLVED, this::waitForNote);
        });
    }

    // =========================================================
    // SHOW HINT
    // =========================================================

    private void showHint() {

        if (!puzzleActive) {
            return;
        }

        ScoreManager.onHint();

        // the puzzle keeps its own hint counter
        setFeedback(
                lock.getHint(),
                true);
    }

    // =========================================================
    // PHONE
    // =========================================================

    private void togglePhone() {

        if (pnlPhone.isVisible()) {
            closePhone();
            return;
        }

        showPhoneScreen(PhoneScreen.HOME);
        setComponentZOrder(pnlPhone, 0);
        phonePopIn.start(null);
    }
    
    /** Makes a button invisible but clickable, like the padlock button. */
    private void makeHotspot(JButton... buttons) {

        for (JButton b : buttons) {
            b.setText("");
            b.setContentAreaFilled(false);
            b.setBorderPainted(false);
            b.setFocusPainted(false);
            b.setOpaque(false);
            b.setCursor(java.awt.Cursor.getPredefinedCursor(
                    java.awt.Cursor.HAND_CURSOR));
        }
    }

    /** Shows one phone screen and hides the rest. */
    private void showPhoneScreen(PhoneScreen screen) {

        lblPhoneHome.setVisible(screen == PhoneScreen.HOME);
        lblPhoneWifi.setVisible(screen == PhoneScreen.WIFI);
        lblPhoneVoicemail.setVisible(screen == PhoneScreen.VOICEMAIL);
        lblPhoneFacebook.setVisible(screen == PhoneScreen.FACEBOOK);

        // the app icons only work on the home screen, the nav button everywhere else
        boolean home = (screen == PhoneScreen.HOME);
        btnAppWifi.setVisible(home);
        btnAppVoicemail.setVisible(home);
        btnAppFacebook.setVisible(home);
        btnPhoneNav.setVisible(!home);
    }

        private void closePhone() {

            phonePopIn.stop();
            pnlPhone.setVisible(false);
        }
        
    // =========================================================
    // UNLOCK ANIMATION
    // =========================================================

    private void playUnlock(Runnable after) {

        lblLockArt.setVisible(false);      // the closed padlock goes away
        unlockHop.start(null);             // the open padlock appears with a snap

        unlockTimer = new Timer(UNLOCK_HOLD_MS, e -> after.run());
        unlockTimer.setRepeats(false);
        unlockTimer.start();
    }
    
    // =========================================================
    // FLICKER
    // =========================================================

    private void flicker() {

        stopFlicker();

        flickerStep = 0;
        setComponentZOrder(lblFlicker, 0);     // above the hallway and the sprites
        lblFlicker.setVisible(true);           // step 0 is dark

        flickerTimer = new Timer(FLICKER_MS[0], e -> {

            flickerStep++;

            if (flickerStep >= FLICKER_MS.length) {
                stopFlicker();
                return;
            }

            lblFlicker.setVisible(flickerStep % 2 == 0);   // even = dark, odd = light
            flickerTimer.setDelay(FLICKER_MS[flickerStep]);
        });
        flickerTimer.start();
    }
    
        // =========================================================
    // THE NOTE
    // =========================================================

    /** The closet scene has been read: the hint bar asks for the note, which becomes clickable. */
    private void waitForNote() {

        setFeedback(Story.L9_NOTE_PROMPT, true);

        btnNote.setVisible(true);
        setComponentZOrder(btnNote, 0);       // above the scene and the sprites
    }

    /** The note was clicked and the closet line is over: show the note, play the rest. */
    private void startNoteScene() {

        showNote();

        speak(
                Story.L9_NOTE,
                () -> btnContinue.setEnabled(true));
    }

    private void showNote() {

        setComponentZOrder(lblNote, 0);
        noteFade.start(NOTE_FADE_MS, null);
        notePop.start(null);
    }

    private void hideNote() {

        noteFade.stop();
        notePop.stop();
        lblNote.setVisible(false);
    }
    
    /** Shows one scene picture, or none if you pass null. */
    /** Fades in a scene picture over the old one, or clears them if you pass null. */
    private void showScene(JLabel next) {

        for (Fade f : fades.values()) {
            f.stop();                              // finish any fade in progress
        }

        final JLabel previous = currentScene;
        currentScene = next;

        for (JLabel s : fades.keySet()) {
            if (s != previous && s != next) {
                s.setVisible(false);
            }
        }

        if (next == null) {
            if (previous != null) {
                previous.setVisible(false);
            }
            return;
        }

        if (next == previous) {
            next.setVisible(true);
            return;
        }

        fades.get(next).start(SCENE_FADE_MS, () -> {
            if (previous != null) {
                previous.setVisible(false);
            }
            lblBgPuzzle.setVisible(false);     // the close-up goes away only now
        });
    }

    private void stopFlicker() {

        if (flickerTimer != null) {
            flickerTimer.stop();
            flickerTimer = null;
        }
        lblFlicker.setVisible(false);
    }

    // =========================================================
    // DIALOGUE
    // =========================================================
    
    /** Shows or hides the bottom bar (title, feedback box, Try Lock, Hint, Continue). */
    private void setBottomBar(boolean show) {

        barVisible = show;

        lblTitle.setVisible(show);
        btnUnlock.setVisible(show);
        btnHint.setVisible(show);
        btnContinue.setVisible(show);
        lblFeedback.setVisible(show && hasFeedback);
    }

    private void speak(String[][] lines, Runnable after) {

        setBottomBar(false);                 // nothing peeks out under the dialogue box

        sprites.play(lines, () -> {
            setBottomBar(true);
            if (after != null) {
                after.run();
            }
        });
    }

    // =========================================================
    // FEEDBACK
    // =========================================================

    private void setFeedback(
            String text,
            boolean good) {

        lblFeedback.setForeground(
                good ? Theme.AMBER : BAD);

        lblFeedback.setText(
                "<html>"
                + text.replace("\n", "<br>")
                + "</html>");

        hasFeedback = !text.isEmpty();
        lblFeedback.setVisible(barVisible && hasFeedback);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnPadlock = new javax.swing.JButton();
        pnlLockArtDesign = new javax.swing.JPanel();
        wheel1 = new kldcrimedecoder.ui.CombinationWheel();
        wheel2 = new kldcrimedecoder.ui.CombinationWheel();
        wheel3 = new kldcrimedecoder.ui.CombinationWheel();
        wheel4 = new kldcrimedecoder.ui.CombinationWheel();
        lblLockArt = new javax.swing.JLabel();
        lblUnLockArt = new javax.swing.JLabel();
        lblJennaScared = new javax.swing.JLabel();
        lblJennaSuspicious = new javax.swing.JLabel();
        lblJennaNeutral = new javax.swing.JLabel();
        lblJennaConfused = new javax.swing.JLabel();
        pnlPhone = new javax.swing.JPanel();
        btnPhoneClose = new javax.swing.JButton();
        btnAppWifi = new javax.swing.JButton();
        btnAppVoicemail = new javax.swing.JButton();
        btnAppFacebook = new javax.swing.JButton();
        btnPhoneNav = new javax.swing.JButton();
        lblPhoneHome = new javax.swing.JLabel();
        lblPhoneWifi = new javax.swing.JLabel();
        lblPhoneVoicemail = new javax.swing.JLabel();
        lblPhoneFacebook = new javax.swing.JLabel();
        lblFlicker = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lblBgPuzzle = new javax.swing.JLabel();
        lblSceneDoor = new javax.swing.JLabel();
        lblSceneCloset = new javax.swing.JLabel();
        lblNote = new javax.swing.JLabel();
        btnNote = new javax.swing.JButton();
        lblSceneSecondDoor = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1000, 600));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnPadlock.setContentAreaFilled(false);
        btnPadlock.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPadlock.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        btnPadlock.addActionListener(this::btnPadlockActionPerformed);
        add(btnPadlock, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 290, 50, 80));

        pnlLockArtDesign.setOpaque(false);
        pnlLockArtDesign.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        pnlLockArtDesign.add(wheel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 268, 24, 80));
        pnlLockArtDesign.add(wheel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(135, 268, 24, 80));
        pnlLockArtDesign.add(wheel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 268, 24, 80));

        wheel4.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        pnlLockArtDesign.add(wheel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(202, 268, 24, 80));

        lblLockArt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLockArt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/padlock_puzzle.png"))); // NOI18N
        lblLockArt.setAutoscrolls(true);
        lblLockArt.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        pnlLockArtDesign.add(lblLockArt, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -10, 330, -1));

        lblUnLockArt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblUnLockArt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/unlock.png"))); // NOI18N
        lblUnLockArt.setAutoscrolls(true);
        lblUnLockArt.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        pnlLockArtDesign.add(lblUnLockArt, new org.netbeans.lib.awtextra.AbsoluteConstraints(-60, 0, 450, 450));

        add(pnlLockArtDesign, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 40, -1, -1));

        lblJennaScared.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/jenna_scared.png"))); // NOI18N
        add(lblJennaScared, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 340, 430));

        lblJennaSuspicious.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/jenna_suspicious.png"))); // NOI18N
        add(lblJennaSuspicious, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 468, 455));

        lblJennaNeutral.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/jenna_neutral.png"))); // NOI18N
        add(lblJennaNeutral, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 468, 455));

        lblJennaConfused.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/jenna_confused.png"))); // NOI18N
        add(lblJennaConfused, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 468, 455));

        pnlPhone.setOpaque(false);
        pnlPhone.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnPhoneClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/power_off.png"))); // NOI18N
        btnPhoneClose.setContentAreaFilled(false);
        btnPhoneClose.addActionListener(this::btnPhoneCloseActionPerformed);
        pnlPhone.add(btnPhoneClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 140, 25, 25));

        btnAppWifi.setContentAreaFilled(false);
        btnAppWifi.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAppWifi.addActionListener(this::btnAppWifiActionPerformed);
        pnlPhone.add(btnAppWifi, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 290, 40, 40));

        btnAppVoicemail.setContentAreaFilled(false);
        btnAppVoicemail.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAppVoicemail.addActionListener(this::btnAppVoicemailActionPerformed);
        pnlPhone.add(btnAppVoicemail, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 290, 40, 40));

        btnAppFacebook.setContentAreaFilled(false);
        btnAppFacebook.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAppFacebook.addActionListener(this::btnAppFacebookActionPerformed);
        pnlPhone.add(btnAppFacebook, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 290, 40, 40));

        btnPhoneNav.setContentAreaFilled(false);
        btnPhoneNav.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPhoneNav.addActionListener(this::btnPhoneNavActionPerformed);
        pnlPhone.add(btnPhoneNav, new org.netbeans.lib.awtextra.AbsoluteConstraints(163, 400, 60, 28));

        lblPhoneHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/alli_homescreen.png"))); // NOI18N
        pnlPhone.add(lblPhoneHome, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 440, 440));

        lblPhoneWifi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/wifi.png"))); // NOI18N
        pnlPhone.add(lblPhoneWifi, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 440, 440));

        lblPhoneVoicemail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/voicemail.png"))); // NOI18N
        pnlPhone.add(lblPhoneVoicemail, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 440, 440));

        lblPhoneFacebook.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/fb_post.png"))); // NOI18N
        pnlPhone.add(lblPhoneFacebook, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 440, 440));

        add(pnlPhone, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 50, 440, 440));

        lblFlicker.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/flicker_dark.png"))); // NOI18N
        add(lblFlicker, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 600));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/room_hallway.png"))); // NOI18N
        jLabel1.setLabelFor(jLabel1);
        jLabel1.setText("jLabel1");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 600));

        lblBgPuzzle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/room_closet.png"))); // NOI18N
        add(lblBgPuzzle, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 600));

        lblSceneDoor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/closet_door_open.png"))); // NOI18N
        add(lblSceneDoor, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 600));

        lblSceneCloset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/room_closet_in.png"))); // NOI18N
        add(lblSceneCloset, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 600));

        lblNote.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/the_note.png"))); // NOI18N
        add(lblNote, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 60, 380, 336));

        btnNote.setText("jButton1");
        btnNote.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNote.addActionListener(this::btnNoteActionPerformed);
        add(btnNote, new org.netbeans.lib.awtextra.AbsoluteConstraints(432, 260, 64, 48));

        lblSceneSecondDoor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/second_door.png"))); // NOI18N
        add(lblSceneSecondDoor, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 600));
    }// </editor-fold>//GEN-END:initComponents

    private void btnPadlockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPadlockActionPerformed
        if (!padlockClickable) {
        return;
        }

        padlockClickable = false;

        beginPuzzle();
    }//GEN-LAST:event_btnPadlockActionPerformed

    private void btnPhoneCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPhoneCloseActionPerformed
        closePhone();
    }//GEN-LAST:event_btnPhoneCloseActionPerformed

    private void btnAppWifiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAppWifiActionPerformed
        showPhoneScreen(PhoneScreen.WIFI);
    }//GEN-LAST:event_btnAppWifiActionPerformed

    private void btnAppVoicemailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAppVoicemailActionPerformed
        showPhoneScreen(PhoneScreen.VOICEMAIL);
    }//GEN-LAST:event_btnAppVoicemailActionPerformed

    private void btnAppFacebookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAppFacebookActionPerformed
        showPhoneScreen(PhoneScreen.FACEBOOK);
    }//GEN-LAST:event_btnAppFacebookActionPerformed

    private void btnPhoneNavActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPhoneNavActionPerformed
        showPhoneScreen(PhoneScreen.HOME);
    }//GEN-LAST:event_btnPhoneNavActionPerformed

    private void btnNoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNoteActionPerformed
        GlowBorder.clear(btnNote);
        btnNote.setVisible(false);
        setFeedback("", true);

        startNoteScene();
    }//GEN-LAST:event_btnNoteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAppFacebook;
    private javax.swing.JButton btnAppVoicemail;
    private javax.swing.JButton btnAppWifi;
    private javax.swing.JButton btnNote;
    private javax.swing.JButton btnPadlock;
    private javax.swing.JButton btnPhoneClose;
    private javax.swing.JButton btnPhoneNav;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblBgPuzzle;
    private javax.swing.JLabel lblFlicker;
    private javax.swing.JLabel lblJennaConfused;
    private javax.swing.JLabel lblJennaNeutral;
    private javax.swing.JLabel lblJennaScared;
    private javax.swing.JLabel lblJennaSuspicious;
    private javax.swing.JLabel lblLockArt;
    private javax.swing.JLabel lblNote;
    private javax.swing.JLabel lblPhoneFacebook;
    private javax.swing.JLabel lblPhoneHome;
    private javax.swing.JLabel lblPhoneVoicemail;
    private javax.swing.JLabel lblPhoneWifi;
    private javax.swing.JLabel lblSceneCloset;
    private javax.swing.JLabel lblSceneDoor;
    private javax.swing.JLabel lblSceneSecondDoor;
    private javax.swing.JLabel lblUnLockArt;
    private javax.swing.JPanel pnlLockArtDesign;
    private javax.swing.JPanel pnlPhone;
    private kldcrimedecoder.ui.CombinationWheel wheel1;
    private kldcrimedecoder.ui.CombinationWheel wheel2;
    private kldcrimedecoder.ui.CombinationWheel wheel3;
    private kldcrimedecoder.ui.CombinationWheel wheel4;
    // End of variables declaration//GEN-END:variables
}
