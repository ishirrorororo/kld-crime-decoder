/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package kldcrimedecoder.levels;

import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import kldcrimedecoder.data.Story;
import kldcrimedecoder.engine.GameManager;
import kldcrimedecoder.engine.LevelManager;
import kldcrimedecoder.engine.ScoreManager;
import kldcrimedecoder.puzzles.PadlockPuzzle;
import kldcrimedecoder.ui.CombinationWheel;
import kldcrimedecoder.ui.DialogueBox;
import kldcrimedecoder.ui.Theme;

public class Level9 extends JPanel {

    private static final int DESIGN_WIDTH = 1000;
    private static final int DESIGN_HEIGHT = 580;

    private static final Color BAD =
            new Color(0xFF6B6B);

    private static final String SECRET = "1403";

    private final PadlockPuzzle lock =
            new PadlockPuzzle(SECRET);

    private final CombinationWheel[] wheels =
            new CombinationWheel[4];

    private final JLabel lblTitle =
            new JLabel("The Hallway Closet");

    private final JButton btnPhone =
            new JButton("Phone");

    private final JPanel pnlPhone =
            new JPanel(null);

    private final JLabel lblPhoneBanner =
            new JLabel();

    private final JButton btnPhoneClose =
            new JButton("Close");

    private final JLabel lblFeedback =
            new JLabel();

    private final JButton btnUnlock =
            new JButton("Try Lock");

    private final JButton btnHint =
            new JButton("Hint");

    private final JButton btnContinue =
            new JButton("Continue");

    private int hintsUsed;
    private boolean puzzleActive;
    private boolean padlockClickable = false;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Level9() {

        initComponents();
        
        pnlLockArtDesign.setVisible(false);
        
            // CHARACTER SPRITE SETUP
        lblCharacterSprite.setText("");
        lblCharacterSprite.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCharacterSprite.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        lblCharacterSprite.setOpaque(false);
        lblCharacterSprite.setVisible(false);
        
        
        // BACKGROUND LABEL FROM GUI BUILDER
        setComponentZOrder(
                jLabel1,
                getComponentCount() - 1);

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

        lblTitle.setBounds(14, 524, 170, 30);

        Theme.heading(lblTitle);

        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(0x0B1220));

        lblTitle.setBorder(
                BorderFactory.createEmptyBorder(2, 8, 2, 8));

        add(lblTitle,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(
                        14, 524, 170, 30));

        // =====================================================
        // FOUR NUMBER WHEELS
        // =====================================================

        int[] wheelX = {
            168, 200, 232, 264
        };

        for (int i = 0; i < wheels.length; i++) {

            wheels[i] = new CombinationWheel(
                    () -> updateCodePreview());

            wheels[i].setEnabled(false);

            pnlLockArtDesign.add(wheels[i],
                new org.netbeans.lib.awtextra.AbsoluteConstraints(
                    wheelX[i], 280, 32, 82
                )
            );

        }


        // =====================================================
        // PHONE BUTTON
        // =====================================================

        btnPhone.setBounds(
                14, 14, 110, 34);

        btnPhone.addActionListener(
                e -> togglePhone());

        add(btnPhone,
            new org.netbeans.lib.awtextra.AbsoluteConstraints(
                14, 14, 110, 34));


        // =====================================================
        // PHONE WALLPAPER
        // =====================================================

        pnlPhone.setBounds(
                300, 20, 400, 110);

        pnlPhone.setBackground(
                new Color(0x10151F));

        pnlPhone.setBorder(
                BorderFactory.createLineBorder(
                        Theme.AMBER, 2));

        lblPhoneBanner.setBounds(
                10, 10, 380, 60);

        lblPhoneBanner.setText(
                "<html>MAR 14: ARNIE'S BIRTHDAY.<br>"
                + "Ube cake!</html>");

        lblPhoneBanner.setFont(
                Theme.MONO_BOLD);

        lblPhoneBanner.setForeground(
                Theme.AMBER);

        pnlPhone.add(lblPhoneBanner);

        btnPhoneClose.setBounds(
                290, 72, 90, 28);

        btnPhoneClose.addActionListener(
                e -> pnlPhone.setVisible(false));

        pnlPhone.add(btnPhoneClose);

        pnlPhone.setVisible(false);

        add(pnlPhone,
        new org.netbeans.lib.awtextra.AbsoluteConstraints(
                300, 20, 400, 110));


        // =====================================================
        // FEEDBACK
        // =====================================================

        lblFeedback.setBounds(
                172, 516, 460, 56);

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

        btnUnlock.setBounds(
                642, 524, 105, 34);

        btnUnlock.addActionListener(
                e -> checkCode());

        add(btnUnlock,
            new org.netbeans.lib.awtextra.AbsoluteConstraints(
                642, 524, 105, 34));


        // =====================================================
        // HINT BUTTON
        // =====================================================

        btnHint.setBounds(
                756, 524, 100, 34);

        btnHint.addActionListener(
                e -> showHint());

        add(btnHint,
        new org.netbeans.lib.awtextra.AbsoluteConstraints(
                756, 524, 100, 34));


        // =====================================================
        // CONTINUE BUTTON
        // =====================================================

        btnContinue.setBounds(
                866, 524, 118, 34);

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

        // Keep the padlock enabled so it can detect mouse hovering.
        btnPadlock.setEnabled(true);
        padlockClickable = false;


        // =====================================================
        // START LEVEL WHEN DISPLAYED
        // =====================================================

        addComponentListener(
                new ComponentAdapter() {

            @Override
            public void componentShown(
                    ComponentEvent e) {

                startLevel();
            }
        });

        // =====================================================
        // COMPONENT LAYERING
        // =====================================================

        // KEEP PADLOCK BUTTON IN FRONT
        setComponentZOrder(
                btnPadlock, 0);
    }
    
    private void showCharacter(String imageName) {

        if (imageName == null || imageName.isEmpty()) {
            lblCharacterSprite.setVisible(false);
            return;
        }

        java.net.URL imageURL = getClass().getResource(
                "/kldcrimedecoder/images/" + imageName
        );

        if (imageURL == null) {
            System.out.println("Character image not found: " + imageName);
            lblCharacterSprite.setVisible(false);
            return;
        }

        javax.swing.ImageIcon icon = new javax.swing.ImageIcon(imageURL);

        java.awt.Image image = icon.getImage().getScaledInstance(
                lblCharacterSprite.getWidth(),
                lblCharacterSprite.getHeight(),
                java.awt.Image.SCALE_SMOOTH
        );

        lblCharacterSprite.setIcon(new javax.swing.ImageIcon(image));
        lblCharacterSprite.setVisible(true);
        lblCharacterSprite.repaint();

        // CONNECT DIALOGUE SPEAKERS TO CHARACTER SPRITES
        if (GameManager.getDialogue() != null) {

            GameManager.getDialogue().setSpeakerChangeListener(speaker -> {

                if ("Jenna".equals(speaker)) {
                    showCharacter("jenna_neutral.png");

                } else if ("Arnie".equals(speaker)
                        || "Arnie (thinking)".equals(speaker)) {
                    showCharacter("arnie_neutral.png");

                } else {
                    showCharacter(null);
                }

            });
        }
    }

    // =========================================================
    // START LEVEL
    // =========================================================

        private void startLevel() {

        hintsUsed = 0;
        puzzleActive = false;
        padlockClickable = false;
        pnlLockArtDesign.setVisible(false);
        btnPadlock.setVisible(true);

        // Keep enabled for hover detection.
        btnPadlock.setEnabled(true);

        pnlPhone.setVisible(false);

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

                    setFeedback(
                            "Click the padlock to inspect it.",
                            true);
                });
    }


    // =========================================================
    // BEGIN PUZZLE
    // =========================================================

    private void beginPuzzle() {

        puzzleActive = true;

        // SHOW THE COMBINATION PUZZLE
        pnlLockArtDesign.setVisible(true);
        setComponentZOrder(pnlLockArtDesign, 0);

        // HIDE THE INVISIBLE PADLOCK CLICK AREA
        btnPadlock.setVisible(false);

        for (CombinationWheel wheel : wheels) {
            wheel.setEnabled(true);
        }

        btnUnlock.setEnabled(true);
        btnHint.setEnabled(true);
        btnPhone.setEnabled(true);

        setFeedback(
                Story.L9_PROMPT, true);

        repaint();
    }


    // =========================================================
    // UPDATE CODE PREVIEW
    // =========================================================

    private void updateCodePreview() {

        if (puzzleActive) {

            StringBuilder current =
                    new StringBuilder();

            for (CombinationWheel wheel : wheels) {
                current.append(wheel.getDigit());
            }

            setFeedback(
                    "Set the four wheels, then select "
                    + "Try Lock. Code: " + current,
                    true);
        }
    }


    // =========================================================
    // CHECK CODE
    // =========================================================

    private void checkCode() {

        if (!puzzleActive) return;

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

        pnlPhone.setVisible(false);

        setFeedback(
                Story.L9_RIGHT, true);

        speak(
                Story.L9_SOLVED,
                () -> btnContinue.setEnabled(true));
    }


    // =========================================================
    // SHOW HINT
    // =========================================================

    private void showHint() {

        if (!puzzleActive) return;

        ScoreManager.onHint();

        int index = Math.min(
                hintsUsed,
                Story.L9_HINTS.length - 1);

        hintsUsed++;

        setFeedback(
                "Hint " + (index + 1) + ": "
                + Story.L9_HINTS[index],
                true);
    }


    // =========================================================
    // PHONE
    // =========================================================

    private void togglePhone() {

        pnlPhone.setVisible(
                !pnlPhone.isVisible());

        if (pnlPhone.isVisible()) {

            setComponentZOrder(
                    pnlPhone, 0);

            pnlPhone.repaint();
        }
    }


    // =========================================================
    // DIALOGUE
    // =========================================================

    private void speak(
            String[][] lines,
            Runnable after) {

        DialogueBox box =
                GameManager.getDialogue();

        if (box == null) {

            if (after != null) {
                after.run();
            }

            return;
        }

        box.play(lines, after);
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

        lblFeedback.setVisible(
                !text.isEmpty());
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
        lblLockArt = new javax.swing.JLabel();
        lblCharacterSprite = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1000, 600));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnPadlock.setContentAreaFilled(false);
        btnPadlock.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPadlock.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        btnPadlock.setNextFocusableComponent(btnPadlock);
        btnPadlock.addActionListener(this::btnPadlockActionPerformed);
        add(btnPadlock, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 290, 50, 80));

        pnlLockArtDesign.setOpaque(false);
        pnlLockArtDesign.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblLockArt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLockArt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/padlock_puzzle.png"))); // NOI18N
        lblLockArt.setAutoscrolls(true);
        lblLockArt.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        pnlLockArtDesign.add(lblLockArt, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -10, 330, -1));

        add(pnlLockArtDesign, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 40, 330, 450));
        add(lblCharacterSprite, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 130, 360, 350));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/room_hallway.png"))); // NOI18N
        jLabel1.setLabelFor(jLabel1);
        jLabel1.setText("jLabel1");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 600));
    }// </editor-fold>//GEN-END:initComponents

    private void btnPadlockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPadlockActionPerformed
        if (!padlockClickable) {
        return;
        }

        padlockClickable = false;

        beginPuzzle();
    }//GEN-LAST:event_btnPadlockActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPadlock;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblCharacterSprite;
    private javax.swing.JLabel lblLockArt;
    private javax.swing.JPanel pnlLockArtDesign;
    // End of variables declaration//GEN-END:variables
}
