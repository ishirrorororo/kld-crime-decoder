/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */


package kldcrimedecoder.levels;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import kldcrimedecoder.data.Story;
import kldcrimedecoder.engine.GameManager;
import kldcrimedecoder.engine.LevelManager;
import kldcrimedecoder.engine.ScoreManager;
import kldcrimedecoder.puzzles.PadlockCode;
import kldcrimedecoder.ui.CombinationWheel;
import kldcrimedecoder.ui.DialogueBox;
import kldcrimedecoder.ui.Theme;

public class Level9 extends JPanel {

    private static final int DESIGN_WIDTH = 1000;
    private static final int DESIGN_HEIGHT = 580;

    private static final Color BAD =
            new Color(0xFF6B6B);

    private static final String SECRET = "1403";

    private static final int LOCK_ART_SIZE = 460;

    private final PadlockCode lock =
            new PadlockCode(SECRET);

    private final CombinationWheel[] wheels =
            new CombinationWheel[4];

    private final JLabel lblTitle =
            new JLabel("The Hallway Closet");

    private final JPanel pnlLockArt =
            new JPanel(null);

    private final JLabel lblLockArt =
            new JLabel();

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

    private final JLabel lblBg =
            new JLabel();

    private int hintsUsed;
    private boolean puzzleActive;

    public Level9() {

        setLayout(null);

        setPreferredSize(
                new java.awt.Dimension(
                        DESIGN_WIDTH, DESIGN_HEIGHT));

        setBackground(Theme.BG);

        // BACKGROUND
        lblBg.setBounds(
                0, 0, DESIGN_WIDTH, DESIGN_HEIGHT);

        lblBg.setIcon(loadImage(
                "room_hallway.png",
                DESIGN_WIDTH,
                DESIGN_HEIGHT,
                "room_hallway.png (missing)"));

        add(lblBg);

        // LEVEL TITLE
        lblTitle.setBounds(14, 524, 170, 30);

        Theme.heading(lblTitle);

        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(0x0B1220));

        lblTitle.setBorder(
                BorderFactory.createEmptyBorder(
                        2, 8, 2, 8));

        add(lblTitle);

        // PADLOCK SPRITE
        pnlLockArt.setBounds(
                270, 34, LOCK_ART_SIZE, LOCK_ART_SIZE);

        pnlLockArt.setOpaque(false);

        lblLockArt.setBounds(
                0, 0, LOCK_ART_SIZE, LOCK_ART_SIZE);

        lblLockArt.setIcon(loadImage(
                "padlock_combination.png",
                LOCK_ART_SIZE,
                LOCK_ART_SIZE,
                "padlock_combination.png (missing)"));

        pnlLockArt.add(lblLockArt);

        // FOUR NUMBER WHEELS
        int[] wheelX = {
            168, 200, 232, 264
        };

        for (int i = 0; i < wheels.length; i++) {

            wheels[i] = new CombinationWheel(
                    () -> updateCodePreview());

            wheels[i].setBounds(
                    wheelX[i], 280, 32, 82);

            wheels[i].setEnabled(false);

            pnlLockArt.add(wheels[i]);
        }

        add(pnlLockArt);

        // PHONE BUTTON
        btnPhone.setBounds(14, 14, 110, 34);

        btnPhone.addActionListener(
                e -> togglePhone());

        add(btnPhone);

        // PHONE WALLPAPER
        pnlPhone.setBounds(300, 20, 400, 110);

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

        lblPhoneBanner.setFont(Theme.MONO_BOLD);
        lblPhoneBanner.setForeground(Theme.AMBER);

        pnlPhone.add(lblPhoneBanner);

        btnPhoneClose.setBounds(
                290, 72, 90, 28);

        btnPhoneClose.addActionListener(
                e -> pnlPhone.setVisible(false));

        pnlPhone.add(btnPhoneClose);

        pnlPhone.setVisible(false);

        add(pnlPhone);

        // FEEDBACK
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

        add(lblFeedback);

        // TRY LOCK BUTTON
        btnUnlock.setBounds(
                642, 524, 105, 34);

        btnUnlock.addActionListener(
                e -> checkCode());

        add(btnUnlock);

        // HINT BUTTON
        btnHint.setBounds(
                756, 524, 100, 34);

        btnHint.addActionListener(
                e -> showHint());

        add(btnHint);

        // CONTINUE BUTTON
        btnContinue.setBounds(
                866, 524, 118, 34);

        btnContinue.addActionListener(
                e -> LevelManager.nextLevel());

        add(btnContinue);

        Theme.button(
                btnHint,
                btnContinue,
                btnPhone,
                btnPhoneClose,
                btnUnlock);

        btnContinue.setEnabled(false);
        btnUnlock.setEnabled(false);
        btnHint.setEnabled(false);
        btnPhone.setEnabled(false);

        // START LEVEL WHEN DISPLAYED
        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentShown(ComponentEvent e) {
                startLevel();
            }
        });

        // KEEP BACKGROUND BEHIND OTHER COMPONENTS
        setComponentZOrder(
                lblBg, getComponentCount() - 1);
    }

    private void startLevel() {

        hintsUsed = 0;
        puzzleActive = false;

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
                this::beginPuzzle);
    }

    private void beginPuzzle() {

        puzzleActive = true;

        for (CombinationWheel wheel : wheels) {
            wheel.setEnabled(true);
        }

        btnUnlock.setEnabled(true);
        btnHint.setEnabled(true);
        btnPhone.setEnabled(true);

        setFeedback(
                Story.L9_PROMPT, true);
    }

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

    private void onWrong() {

        ScoreManager.onWrong();

        setFeedback(
                Story.L9_WRONG, false);
    }

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

    private void togglePhone() {

        pnlPhone.setVisible(
                !pnlPhone.isVisible());

        if (pnlPhone.isVisible()) {

            setComponentZOrder(
                    pnlPhone, 0);

            pnlPhone.repaint();
        }
    }

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

    private void setFeedback(
            String text,
            boolean good) {

        lblFeedback.setForeground(
                good ? Theme.AMBER : BAD);

        lblFeedback.setText(
                "<html>" +
                text.replace("\n", "<br>") +
                "</html>");

        lblFeedback.setVisible(
                !text.isEmpty());
    }

    private static Icon loadImage(
            String file,
            int width,
            int height,
            String label) {

        java.net.URL url =
                Level9.class.getResource(
                        "/kldcrimedecoder/images/" + file);

        if (url != null) {

            Image image =
                    new ImageIcon(url)
                            .getImage()
                            .getScaledInstance(
                                    width,
                                    height,
                                    Image.SCALE_SMOOTH);

            return new ImageIcon(image);
        }

        BufferedImage placeholder =
                new BufferedImage(
                        width,
                        height,
                        BufferedImage.TYPE_INT_ARGB);

        Graphics2D g =
                placeholder.createGraphics();

        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(0x2B3A55));
        g.fillRect(0, 0, width, height);

        g.setColor(Theme.AMBER);
        g.drawRect(0, 0, width - 1, height - 1);

        g.setFont(
                new Font("Consolas", Font.PLAIN, 14));

        FontMetrics metrics =
                g.getFontMetrics();

        g.drawString(
                label,
                Math.max(
                        2,
                        (width - metrics.stringWidth(label)) / 2),
                height / 2);

        g.dispose();

        return new ImageIcon(placeholder);
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1000, 600));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/room_hallway.png"))); // NOI18N
        jLabel1.setText("jLabel1");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 600));

        jLabel2.setText("jLabel2");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 330, -1, -1));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}
