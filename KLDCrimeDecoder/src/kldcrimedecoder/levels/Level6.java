/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package kldcrimedecoder.levels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import kldcrimedecoder.data.Story;
import kldcrimedecoder.engine.GameManager;
import kldcrimedecoder.engine.GameState;
import kldcrimedecoder.engine.LevelManager;
import kldcrimedecoder.engine.ScoreManager;
import kldcrimedecoder.puzzles.AcrosticPuzzle;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Level 6: Amariah Navarro. The scene and the phone screen. The puzzle's data and answer
 * check live in the puzzles package (AcrosticPuzzle); this class draws the phone feed.
 * All the text lives in Story.java (L6_...).
 *
 * @author Joel
 */
public class Level6 extends javax.swing.JPanel {

    private static final String KEY_INTRO  = "level6.introSeen";
    private static final String KEY_SOLVED = "level6.solved";

    private static final Color INK = new Color(0xE8E8E8);

    private enum Phase { INTRO, PUZZLE, SOLVED }
    private Phase phase = Phase.INTRO;

    // these sit on the white phone screen, so they are the darker versions of the usual palette
    private static final Color DARK  = new Color(0x222222);
    private static final Color AMBER = new Color(0xB36B00);
    private static final Color BAD   = new Color(0xC0392B);
    private static final Color GOOD  = new Color(0x1E8A4C);

    private final AcrosticPuzzle puzzle = new AcrosticPuzzle();
    private boolean[] seen = new boolean[puzzle.getPostCount()];
    private int feedIdx = 0;
    private int hintsUsed = 0;
    private boolean glowFirst = false;
    private boolean updatingBar = false;     // true while the code (not the player) moves the scroll bar

    private ImageIcon standingIcon, leaningIcon;      // Amariah sprites
    private Timer buzzTimer;
    private int buzzX, buzzY;
    // same as lblAmariah's Width/Height in the Design tab (Properties → Layout)
    private static final int SPRITE_W = 300, SPRITE_H = 390;

    /** Scales an icon to fit inside w x h, keeping its proportions. */
    private static ImageIcon fit(ImageIcon src, int w, int h) {
        if (src == null) return null;
        int iw = src.getIconWidth(), ih = src.getIconHeight();
        if (iw <= 0 || ih <= 0) return src;
        double s = (double) h / ih;       
        int nw = Math.max(1, (int) Math.round(iw * s)), nh = Math.max(1, (int) Math.round(ih * s));
        java.awt.image.BufferedImage out =
                new java.awt.image.BufferedImage(nw, nh, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = out.createGraphics();
        g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                s < 1 ? java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC
                      : java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(src.getImage(), 0, 0, nw, nh, null);
        g.dispose();
        return new ImageIcon(out);
    }

    /**
     * Creates new form Level6
     */
    public Level6() {
        initComponents();
        setupLevel6();

        // When the hallway appears, (re)start the level
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                onShown();
            }
        });
    }

    /** The puzzle and sprites sit over the hallway picture, so repaint overlapping children in order (same as Level3). */
    @Override
    public boolean isOptimizedDrawingEnabled() {
        return false;
    }

    /** Styling that is easier in code than in the Properties window. */
    private void setupLevel6() {
        btnContinue.setText("Continue");
        lblBackground.setText("");

        // Amariah: both sprites are scaled to fit her label
    lblAmariah.setFont(new Font("Consolas", Font.BOLD, 18));
    lblAmariah.setForeground(INK);
    lblAmariah.setHorizontalAlignment(SwingConstants.CENTER);
    lblAmariah.setVerticalAlignment(SwingConstants.BOTTOM);       // feet stay in the same place in both poses
    standingIcon = fit((ImageIcon) lblAmariah.getIcon(), SPRITE_W, SPRITE_H);
    java.net.URL u = getClass().getResource("/kldcrimedecoder/images/amariah_leaning_full.png");
    if (u != null) {
        leaningIcon = fit(new ImageIcon(u), SPRITE_W, SPRITE_H);
    } else {
        System.err.println("Level6: amariah_leaning_full.png NOT FOUND in /kldcrimedecoder/images/");
    }
    lblAmariah.setIcon(standingIcon);
    lblAmariah.setText(standingIcon == null ? "Amariah" : "");
    lblAvatar.setText("");

        // phone feed
        lblPost.setVerticalAlignment(SwingConstants.TOP);
        lblPost.setText("");
        lblFeed.setText("");
        lblReactions.setText("Like        Comment        Share");
        lblReactions.setFont(new Font("Consolas", Font.BOLD, 12));
        lblReactions.setForeground(new Color(0x777777));
        lblReactions.setHorizontalAlignment(SwingConstants.CENTER);
        lblCounter.setFont(new Font("Consolas", Font.BOLD, 12));
        lblCounter.setForeground(DARK);
        lblCounter.setHorizontalAlignment(SwingConstants.CENTER);
        lblPrompt.setText(puzzle.getPrompt());
        lblPrompt.setFont(new Font("Consolas", Font.PLAIN, 13));
        lblPrompt.setForeground(DARK);
        txtAnswer.setFont(new Font("Consolas", Font.BOLD, 20));
        txtAnswer.setHorizontalAlignment(SwingConstants.CENTER);
        txtAnswer.setDocument(new javax.swing.text.PlainDocument() {   // only as many letters as the answer, uppercase
            @Override
            public void insertString(int off, String s, javax.swing.text.AttributeSet a)
                    throws javax.swing.text.BadLocationException {
                if (s != null && getLength() + s.length() <= puzzle.getAnswerLength()) {
                    super.insertString(off, s.toUpperCase(), a);
                }
            }
        });
        // scroll bar: one step per post
        sbFeed.setOrientation(javax.swing.JScrollBar.VERTICAL);
        sbFeed.setValues(0, 1, 0, puzzle.getPostCount());          // value, extent, min, max -> 0..(posts - 1)
        sbFeed.setUnitIncrement(1);
        sbFeed.setBlockIncrement(1);
        sbFeed.addAdjustmentListener(e -> {
            if (!updatingBar && phase == Phase.PUZZLE) {
                showPost(e.getValue());
            }
        });
        sbFeed.setUI(new FeedScrollUI());
        btnSubmit.setText("Submit");
        btnHint.setText("Hint");
        lblFeedback.setText(" ");
        lblFeedback.setFont(new Font("Consolas", Font.ITALIC, 13));
        lblFeedback.setHorizontalAlignment(SwingConstants.CENTER);
        phonePanel.setOpaque(false);
        phonePanel.addMouseWheelListener(e -> scrollFeed(e.getWheelRotation() > 0 ? 1 : -1));   // mouse wheel scrolls too

        phonePanel.setVisible(false);
        lblPhoneBuzz.setVisible(false);
        lblAmariah.setVisible(false);
        btnContinue.setVisible(false);
    }

    // ===== Scene flow =====

    /** Called every time the card is shown. */
    private void onShown() {
        GameState gs = GameState.getInstance();
        stopBuzz();
        lblPhoneBuzz.setVisible(false);
        lblAmariah.setVisible(false);

        if (gs.getChoice(KEY_SOLVED, "no").equals("yes")) {      // already solved: go straight to Continue
            enterPhase(Phase.SOLVED);
            phonePanel.setVisible(false);
            btnContinue.setVisible(true);
            return;
        }
        enterPhase(Phase.INTRO);
        gs.putChoice(KEY_INTRO, "yes");
        playScene(Story.L6_INTRO, () -> enterPhase(Phase.PUZZLE));
    }

    private void enterPhase(Phase p) {
        phase = p;
        phonePanel.setVisible(p != Phase.INTRO);
        setControlsEnabled(p == Phase.PUZZLE);
        if (p == Phase.PUZZLE) {
            seen = new boolean[puzzle.getPostCount()];
            hintsUsed = 0;
            glowFirst = false;
            txtAnswer.setText("");
            lblFeedback.setText(" ");
            showPost(0);
            txtAnswer.requestFocusInWindow();
        }
        btnContinue.setVisible(false);
        repaint();
    }

    private void onCorrect() {
        GameState gs = GameState.getInstance();
        lblFeedback.setForeground(GOOD);
        lblFeedback.setText(Story.L6_RIGHT);
        ScoreManager.onCorrect();                      // +100 plus speed bonus
        setControlsEnabled(false);
        gs.putChoice(KEY_SOLVED, "yes");
        gs.addItem(Story.ITEM_AMARIAH_CLEARED);
        gs.addItem(Story.ITEM_CLUE_CLOSET);
        phase = Phase.SOLVED;
        Timer closePhone = new Timer(1200, e -> {                // let the player see the green message first
            phonePanel.setVisible(false);
            playScene(Story.L6_AFTER, () -> {
                btnContinue.setVisible(true);
                repaint();
            });
        });
        closePhone.setRepeats(false);
        closePhone.start();
    }

    /** Plays lines in the shared DialogueBox (the same one Level 3 uses). */
    private void playDialogue(String[][] lines, Runnable done) {
        if (GameManager.getDialogue() != null) {
            GameManager.getDialogue().play(lines, done);
        } else if (done != null) {
            done.run();                                          // no dialogue box (panel run on its own): carry on
        }
    }

    /** Plays a scene line by line; Amariah is on screen only while she or Arnie speaks. */
    private void playScene(String[][] lines, Runnable done) {
        playFrom(lines, 0, done);
    }

    private void playFrom(String[][] lines, int i, Runnable done) {
        if (i >= lines.length) {
            lblAmariah.setVisible(false);
            stopBuzz();
            lblPhoneBuzz.setVisible(false);
            if (done != null) {
                done.run();
            }
            return;
        }
        String speaker = lines[i][0];
        boolean opening = (lines == Story.L6_INTRO && i == 0);   // the "phone buzzes" narration line

        lblPhoneBuzz.setVisible(opening);
        if (opening) {
            startBuzz();
        } else {
            stopBuzz();
        }

        ImageIcon sprite = (opening && leaningIcon != null) ? leaningIcon : standingIcon;
        lblAmariah.setIcon(sprite);
        lblAmariah.setText(sprite == null ? "Amariah" : "");      // text placeholder only while there is no sprite
        lblAmariah.setVisible(opening || speaker.equals("Amariah") || speaker.equals("Arnie"));

        playDialogue(new String[][] { lines[i] }, () -> playFrom(lines, i + 1, done));
    }

    private void startBuzz() {
        stopBuzz();
        buzzX = lblPhoneBuzz.getX();
        buzzY = lblPhoneBuzz.getY();
        int[] t = {0};
        buzzTimer = new Timer(60, e -> {
            int k = t[0]++;
            boolean on = (k % 20) < 8;                  // shake for about half a second, then pause
            lblPhoneBuzz.setLocation(buzzX + (on ? (k % 2 == 0 ? -4 : 4) : 0), buzzY);
        });
        buzzTimer.start();
    }

    private void stopBuzz() {
        if (buzzTimer != null) {
            buzzTimer.stop();
            lblPhoneBuzz.setLocation(buzzX, buzzY);
            buzzTimer = null;
        }
    }

    // ===== Phone feed (the puzzle's data and answer check are in AcrosticPuzzle) =====

    private void setControlsEnabled(boolean on) {
        txtAnswer.setEnabled(on);
        btnSubmit.setEnabled(on);
        btnHint.setEnabled(on);
        sbFeed.setEnabled(on);
    }

    private void scrollFeed(int dir) {
        if (phase != Phase.PUZZLE) {
            return;
        }
        int n = Math.max(0, Math.min(puzzle.getPostCount() - 1, feedIdx + dir));
        sbFeed.setValue(n);                              // the scroll bar's listener shows the post
    }

    private void showPost(int n) {
        feedIdx = n;
        seen[n] = true;
        lblPost.setText(postHtml(n));
        lblCounter.setText("Post " + (n + 1) + " / " + puzzle.getPostCount());
        if (sbFeed.getValue() != n) {
            updatingBar = true;
            sbFeed.setValue(n);
            updatingBar = false;
        }
    }

    private String postHtml(int n) {
        String t = puzzle.getPost(n);
        String first = glowFirst
                ? "<font color='#E08A00'><b>" + t.charAt(0) + "</b></font>"
                : "<b>" + t.charAt(0) + "</b>";
        return "<html><body style='width:240px; font-family:Consolas; color:#111111'>"
             + "<table cellpadding='0' cellspacing='0'><tr><td width='56'>&nbsp;</td><td>"      // room for the avatar
             + "<div style='font-size:12px; color:#1C3F8C'><b>Amariah</b></div>"
             + "<div style='font-size:11px; color:#777777'>Tagged: Alli</div>"
             + "</td></tr></table><br>"
             + "<div style='font-size:21px'>" + first + t.substring(1) + "</div><br>"
             + "<div style='font-size:11px; color:#777777'>Posted by <font color='#1C3F8C'>Amariah</font><br>"
             + puzzle.getPostTime(n) + "</div></body></html>";
    }

    private void submit() {
        if (phase != Phase.PUZZLE) {
            return;
        }
        for (boolean s : seen) {
            if (!s) {                                           // no penalty: just a nudge
                lblFeedback.setForeground(AMBER);
                lblFeedback.setText(Story.L6_SCROLL_ALL);
                return;
            }
        }
        if (puzzle.check(txtAnswer.getText())) {
            onCorrect();
        } else {
            onWrongAnswer();
        }
    }

    private void onWrongAnswer() {
        ScoreManager.onWrong();                        // -10 points, -15 s
        lblFeedback.setForeground(BAD);
        lblFeedback.setText(Story.L6_WRONG);
        txtAnswer.selectAll();
        shake(txtAnswer);
    }

    private void giveHint() {
        if (phase != Phase.PUZZLE) {
            return;
        }
        hintsUsed++;
        ScoreManager.onHint();                         // -5 points, -20 s
        lblFeedback.setForeground(AMBER);
        lblFeedback.setText(puzzle.getHint(hintsUsed));
        if (hintsUsed == 1) {
            glowFirst = true;                          // light up the first letters
            showPost(feedIdx);
        }
    }

    /** Quick sideways wobble for a wrong answer. */
    private void shake(java.awt.Component c) {
        final int x0 = c.getX();
        final int[] offs = {-6, 6, -4, 4, -2, 2, 0};
        final int[] i = {0};
        Timer t = new Timer(35, null);
        t.addActionListener(e -> {
            c.setLocation(x0 + offs[i[0]], c.getY());
            if (++i[0] >= offs.length) {
                t.stop();
            }
        });
        t.start();
    }
    
    /** Slim rounded scroll bar in the Facebook blue: no arrow buttons, light track, grey outline all around. */
    private static class FeedScrollUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        private static final Color THUMB   = new Color(0x3B5998);
        private static final Color TRACK   = new Color(0xE9EBEE);
        private static final Color OUTLINE = new Color(0x6B6B6B);   // dark grey

        @Override
        protected void configureScrollBarColors() {
            thumbColor = THUMB;
            trackColor = TRACK;
        }

        @Override
        protected javax.swing.JButton createDecreaseButton(int orientation) {
            return noButton();
        }

        @Override
        protected javax.swing.JButton createIncreaseButton(int orientation) {
            return noButton();
        }

        private javax.swing.JButton noButton() {
            javax.swing.JButton b = new javax.swing.JButton();
            java.awt.Dimension zero = new java.awt.Dimension(0, 0);
            b.setPreferredSize(zero);
            b.setMinimumSize(zero);
            b.setMaximumSize(zero);
            return b;
        }

        /** The whole bar is painted here: white base, light track, thumb, then the outline on top. */
        @Override
        public void paint(Graphics g, javax.swing.JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = c.getWidth(), h = c.getHeight();
            g2.setColor(Color.WHITE);                               // cover the dark default background
            g2.fillRect(0, 0, w, h);
            g2.setColor(TRACK);
            g2.fillRoundRect(2, 1, w - 5, h - 3, 6, 6);             // track, 1 px away from the top and bottom edge
            java.awt.Rectangle t = getThumbBounds();
            if (!t.isEmpty() && scrollbar.isEnabled()) {
                g2.setColor(THUMB);
                g2.fillRoundRect(4, t.y + 2, w - 9, t.height - 4, 6, 6);
            }
            g2.setColor(OUTLINE);                                   // outline: top, bottom, left and right
            g2.setStroke(new java.awt.BasicStroke(3f));
            g2.drawRoundRect(2, 1, w - 5, h - 3, 6, 6);
            g2.dispose();
        }
    }

    /** Moves on to Level 7. LevelManager knows the order. */
    private void goToNextLevel() {
        LevelManager.nextLevel();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        phonePanel = new javax.swing.JPanel();
        lblPost = new javax.swing.JLabel();
        lblAvatar = new javax.swing.JLabel();
        lblReactions = new javax.swing.JLabel();
        lblPrompt = new javax.swing.JLabel();
        txtAnswer = new javax.swing.JTextField();
        btnSubmit = new javax.swing.JButton();
        btnHint = new javax.swing.JButton();
        lblFeedback = new javax.swing.JLabel();
        lblCounter = new javax.swing.JLabel();
        sbFeed = new javax.swing.JScrollBar();
        lblFeed = new javax.swing.JLabel();
        btnContinue = new javax.swing.JButton();
        lblAmariah = new javax.swing.JLabel();
        lblPhoneBuzz = new javax.swing.JLabel();
        lblBackground = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1000, 580));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        phonePanel.setBackground(new java.awt.Color(153, 153, 153));
        phonePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblPost.setText("post");
        phonePanel.add(lblPost, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 140, 260, 345));

        lblAvatar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/avatar_amariah.png"))); // NOI18N
        phonePanel.add(lblAvatar, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 130, 50, 60));

        lblReactions.setText("reactions");
        phonePanel.add(lblReactions, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 310, 280, 30));

        lblPrompt.setText("prompt");
        phonePanel.add(lblPrompt, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 390, 374, 20));

        txtAnswer.setText("jTextField1");
        txtAnswer.addActionListener(this::txtAnswerActionPerformed);
        phonePanel.add(txtAnswer, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 420, 280, 34));

        btnSubmit.setText("jButton1");
        btnSubmit.addActionListener(this::btnSubmitActionPerformed);
        phonePanel.add(btnSubmit, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 470, 90, 34));

        btnHint.setText("jButton1");
        btnHint.addActionListener(this::btnHintActionPerformed);
        phonePanel.add(btnHint, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 470, 88, 34));

        lblFeedback.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFeedback.setText("feedback");
        phonePanel.add(lblFeedback, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 520, 220, -1));

        lblCounter.setText("counter");
        phonePanel.add(lblCounter, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 346, -1, 20));

        sbFeed.setForeground(new java.awt.Color(204, 204, 204));
        phonePanel.add(sbFeed, new org.netbeans.lib.awtextra.AbsoluteConstraints(385, 140, 15, 180));

        lblFeed.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/phone_feed.png"))); // NOI18N
        lblFeed.setText("feed");
        phonePanel.add(lblFeed, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, -70, 590, 700));

        add(phonePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 20, 560, 570));

        btnContinue.setText("jButton1");
        btnContinue.addActionListener(this::btnContinueActionPerformed);
        add(btnContinue, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 500, 160, 40));

        lblAmariah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/portrait_amariah_navarro.png"))); // NOI18N
        add(lblAmariah, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 80, 300, 390));

        lblPhoneBuzz.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/phone_buzz_small.png"))); // NOI18N
        add(lblPhoneBuzz, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 160, 314, 314));

        lblBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/room_hallway.png"))); // NOI18N
        lblBackground.setText("bg");
        add(lblBackground, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -10, 1000, 600));
    }// </editor-fold>//GEN-END:initComponents

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        submit();
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void txtAnswerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAnswerActionPerformed
        submit();
    }//GEN-LAST:event_txtAnswerActionPerformed

    private void btnHintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHintActionPerformed
        giveHint();
    }//GEN-LAST:event_btnHintActionPerformed

    private void btnContinueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContinueActionPerformed
        goToNextLevel();
    }//GEN-LAST:event_btnContinueActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnContinue;
    private javax.swing.JButton btnHint;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JLabel lblAmariah;
    private javax.swing.JLabel lblAvatar;
    private javax.swing.JLabel lblBackground;
    private javax.swing.JLabel lblCounter;
    private javax.swing.JLabel lblFeed;
    private javax.swing.JLabel lblFeedback;
    private javax.swing.JLabel lblPhoneBuzz;
    private javax.swing.JLabel lblPost;
    private javax.swing.JLabel lblPrompt;
    private javax.swing.JLabel lblReactions;
    private javax.swing.JPanel phonePanel;
    private javax.swing.JScrollBar sbFeed;
    private javax.swing.JTextField txtAnswer;
    // End of variables declaration//GEN-END:variables
}
