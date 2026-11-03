/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package kldcrimedecoder.levels;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.Timer;
import javax.swing.border.AbstractBorder;
import kldcrimedecoder.data.Story;
import kldcrimedecoder.engine.GameManager;
import kldcrimedecoder.engine.GameState;
import kldcrimedecoder.engine.LevelManager;
import kldcrimedecoder.engine.ScoreManager;
import kldcrimedecoder.ui.DialogueBox;
import kldcrimedecoder.ui.Theme;

/**
 * Level 10: the mirror room.
 * The player wipes the steam off the mirror with the mouse, then decodes a
 * message that was scrambled with their own nickname as the key (a Vigenere
 * cipher). Solving it leads to the final choice and a fade to black.
 *
 * @author Rosaldo
 */
public class Level10 extends javax.swing.JPanel {

    // lets us write "Arnie [guilty]" and split it into the name and the mood
    private static final Pattern MOOD = Pattern.compile("^(.*?)\\s*\\[(\\w+)\\]$");

    private static final String MESSAGE = "I AM YOU";   // the hidden message in the steam
    private static final int ACCEPT_DENIAL = -10;       // accepting the truth lowers the denial meter
    private static final int DENY_DENIAL = 50;          // denying it raises the meter to the bad ending limit

    // the steam on the mirror
    private static final int FOG_W = 500;               // same size as lblFog in the Design tab
    private static final int FOG_H = 130;
    private static final int CELL = 16;                 // the fog is split into squares to measure how much was wiped
    private static final int BRUSH = 24;                // radius of the wipe
    private static final double WIPE_GOAL = 0.55;       // 55 percent wiped = clear enough

    // table shown under the letters so the player can do the math
    private static final String REFERENCE = "<html><center>"
            + "A=0 B=1 C=2 D=3 E=4 F=5 G=6 H=7 I=8 J=9 K=10 L=11 M=12<br>"
            + "N=13 O=14 P=15 Q=16 R=17 S=18 T=19 U=20 V=21 W=22 X=23 Y=24 Z=25"
            + "</center></html>";

    // background pictures, the files are inside the images folder
    private static final String BG_START = "bathroom.png";
    private static final String BG_FACE = "bathroomScene1.png";
    private static final String BG_MIRROR = "bathroomMirror.png";
    private static final String BG_AMIE = "bathroomAmie.png";
    private static final String BG_FOG = "bathroomFog.png";

    // story lines before the puzzle. {bg:file.png} at the start of a line changes the background
    private static final String[][] INTRO = {
        {"", "{bg:" + BG_START + "}You lock yourself in the bathroom and splash water on your face. The tap keeps running. You look up."},
        {"", "{bg:" + BG_FACE + "}For the first time, you see your own face."},
        {"", "The reflection doesn't blink."},
        {"....", "{glitch}{bg:" + BG_AMIE + "}Took you long enough. I did leave a trail."},
        {"Arnie [guilty]", "Who are you?"},
        {"....", "You know exactly who I am. You've been wearing me for years."},
        {"", "{bg:" + BG_MIRROR + "}The mirror fogs over. Letters appear in the steam."}
    };

    // story lines after the puzzle is solved (the fog picture is already showing)
    private static final String[][] AFTER = {
        {"Arnie [sad]", "I was always second. I just wanted to be first once."},
        {"Amie [glitch]", "I know. That's why I'm here."},
        {"Amie [glitch]", "I am you. I'm what you never said out loud."},
        {"", "The brass key in your pocket is suddenly heavy. The tag now shows a number: 2."}
    };

    private static final String[] CHOICES = {
        "Accept it: You're right. I did this. I'm sorry.",
        "Deny it: No. You're not me."
    };

    private static final String[] HINTS = {
        "Every letter in the steam was pushed forward by the letter written under it.",
        "Use the table: A=0, B=1, C=2... "
        + "To undo it, go backwards that many letters. If N sits over E (4), N goes back 4 to J.",
        "It is three words and six letters in total. The first word is just one letter."
    };

    private String keyWord;         // the nickname from the Prologue, letters only (ARNIE if none was saved)
    private String codedLine;       // the message after it was scrambled, with spaces for the display
    private String keyLine;         // the key letters that sit under each coded letter
    private BufferedImage bgBase;   // the current background, kept so it can fade to black at the end
    private BufferedImage fogImg;   // the steam. wiping makes holes in it
    private boolean[][] wiped;      // which squares of the fog were already wiped
    private int wipedCount;
    private boolean glitchNext;     // true = the next background change plays the glitch effect
    private boolean fogActive;      // true while the player can still wipe the mirror
    private boolean puzzleActive;
    private boolean solved;
    private int hintsUsed;

    public Level10() {
        initComponents();

        // use the same dark theme as the other levels
        Theme.background(this);
        Theme.heading(lblTitle);
        Theme.label(lblFeedback, lblCoded, lblKey, lblRef);
        Theme.button(btnSubmit, btnHint);
        pnlCipher.setOpaque(false);
        pnlCipher.setBorder(new FogBorder());
        lblTitle.setText(Story.LEVEL_TITLES[10]);

        // coded letters on top, the key letters right under them (same font so they line up)
        Font mono = new Font("Consolas", Font.BOLD, 30);
        lblCoded.setFont(mono);
        lblCoded.setForeground(new Color(0xCFEFEA));
        lblCoded.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblKey.setFont(mono);
        lblKey.setForeground(Theme.AMBER);
        lblKey.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRef.setFont(new Font("Consolas", Font.PLAIN, 11));
        lblRef.setForeground(new Color(0xCFEFEA));
        lblRef.setText(REFERENCE);
        lblRef.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        // the answer box
        txtAnswer.setFont(new Font("Consolas", Font.BOLD, 18));
        txtAnswer.setForeground(Theme.AMBER);
        txtAnswer.setBackground(new Color(0x111A2E));
        txtAnswer.setCaretColor(Theme.AMBER);
        txtAnswer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtAnswer.setBorder(BorderFactory.createLineBorder(Theme.AMBER));
        txtAnswer.addActionListener(e -> btnSubmit.doClick());   // Enter works like the Unlock button

        // dark bar behind the instructions, because the mirror picture is too light for plain text
        lblFeedback.setOpaque(true);
        lblFeedback.setBackground(new Color(0x0B1220));
        lblFeedback.setForeground(Theme.AMBER);
        lblFeedback.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        // dragging the mouse over the fog wipes it away
        MouseAdapter wiper = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                wipe(e.getX(), e.getY());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                wipe(e.getX(), e.getY());
            }
        };
        lblFog.addMouseListener(wiper);
        lblFog.addMouseMotionListener(wiper);
        lblFog.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));

        // restart the level every time this screen is shown
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                startLevel();
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        pnlCipher = new javax.swing.JPanel();
        lblFog = new javax.swing.JLabel();
        lblCoded = new javax.swing.JLabel();
        lblKey = new javax.swing.JLabel();
        lblRef = new javax.swing.JLabel();
        txtAnswer = new javax.swing.JTextField();
        btnSubmit = new javax.swing.JButton();
        btnHint = new javax.swing.JButton();
        lblFeedback = new javax.swing.JLabel();
        lblBg = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setText("jLabel1");
        add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 960, 30));

        pnlCipher.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        pnlCipher.add(lblFog, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 130));

        lblCoded.setText("jLabel1");
        pnlCipher.add(lblCoded, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 8, 500, 42));

        lblKey.setText("jLabel2");
        pnlCipher.add(lblKey, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 500, 36));

        lblRef.setText("jLabel3");
        pnlCipher.add(lblRef, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 480, 36));
        pnlCipher.add(txtAnswer, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 135, 200, 30));

        add(pnlCipher, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 50, 500, 175));

        btnSubmit.setText("Unlock");
        btnSubmit.addActionListener(this::btnSubmitActionPerformed);
        add(btnSubmit, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 235, 110, 30));

        btnHint.setText("Hint");
        btnHint.addActionListener(this::btnHintActionPerformed);
        add(btnHint, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 235, 110, 30));

        lblFeedback.setText("jLabel1");
        add(lblFeedback, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 300, 800, 28));

        lblBg.setMaximumSize(new java.awt.Dimension(985, 575));
        lblBg.setMinimumSize(new java.awt.Dimension(985, 575));
        lblBg.setPreferredSize(new java.awt.Dimension(985, 575));
        add(lblBg, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 600));
    }// </editor-fold>//GEN-END:initComponents

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        if (!puzzleActive || fogActive || solved) {
            return;
        }
        String answer = lettersOnly(txtAnswer.getText());
        String wanted = lettersOnly(MESSAGE);
        if (answer.equals(wanted)) {
            onSolved();
            return;
        }
        // tell the player how close they were, so they can fix it letter by letter
        int right = 0;
        for (int i = 0; i < Math.min(answer.length(), wanted.length()); i++) {
            if (answer.charAt(i) == wanted.charAt(i)) {
                right++;
            }
        }
        ScoreManager.onWrong();
        setFeedback(right + " of " + wanted.length() + " letters are right. Check the letters that don't fit.", false);
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void btnHintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHintActionPerformed
        if (!puzzleActive || fogActive || solved) {
            return;
        }
        ScoreManager.onHint();
        int i = Math.min(hintsUsed, HINTS.length - 1);
        setFeedback("Hint " + (i + 1) + ": " + HINTS[i], true);
        hintsUsed++;
    }//GEN-LAST:event_btnHintActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHint;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JLabel lblBg;
    private javax.swing.JLabel lblCoded;
    private javax.swing.JLabel lblFeedback;
    private javax.swing.JLabel lblFog;
    private javax.swing.JLabel lblKey;
    private javax.swing.JLabel lblRef;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlCipher;
    private javax.swing.JTextField txtAnswer;
    // End of variables declaration//GEN-END:variables

    // ------------------------------------------------------------------
    //  Starting the level
    // ------------------------------------------------------------------

    private void startLevel() {
        solved = false;
        puzzleActive = false;
        fogActive = false;
        hintsUsed = 0;

        // the nickname from the Prologue is the key (ARNIE if nothing was saved)
        String nickname = GameState.getInstance()
                .getChoice(GameState.KEY_FINAL_WORD, "ARNIE");
        keyWord = lettersOnly(nickname);
        if (keyWord.isEmpty()) {
            keyWord = "ARNIE";
        }
        buildLines();

        showBackground(BG_START);
        lblTitle.setVisible(true);       // the fade at the end hides these, so bring them back
        pnlCipher.setVisible(false);
        btnHint.setVisible(false);       // the buttons only show with the puzzle
        btnSubmit.setVisible(false);
        txtAnswer.setText("");
        setFeedback("", true);

        scene(INTRO, this::beginPuzzle);
    }

    // dialogue is done, so show the fogged mirror
    private void beginPuzzle() {
        lblCoded.setText(codedLine);
        lblKey.setText(keyLine);
        resetFog();
        pnlCipher.setVisible(true);
        btnHint.setVisible(true);
        btnSubmit.setVisible(true);
        btnHint.setEnabled(false);       // locked until the mirror is clear
        btnSubmit.setEnabled(false);
        txtAnswer.setEnabled(false);
        puzzleActive = true;
        fogActive = true;
        setFeedback("The mirror is fogged up. Drag the mouse over the glass to wipe it clear.", true);
    }

    // ------------------------------------------------------------------
    //  Puzzle: wipe the fog, then decode
    // ------------------------------------------------------------------

    // scrambles MESSAGE with the nickname as the key. Each letter moves forward by the key letter's number
    private void buildLines() {
        StringBuilder coded = new StringBuilder();
        StringBuilder key = new StringBuilder();
        int used = 0;                                    // how many real letters we passed so far
        for (char c : MESSAGE.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                char k = keyWord.charAt(used % keyWord.length());
                used++;
                coded.append((char) ('A' + (c - 'A' + (k - 'A')) % 26));
                key.append(k);
            } else {
                coded.append(' ');
                key.append(' ');
            }
        }
        codedLine = spaced(coded.toString());
        keyLine = spaced(key.toString());
    }

    // makes a fresh layer of steam that covers the letters
    private void resetFog() {
        fogImg = new BufferedImage(FOG_W, FOG_H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = fogImg.createGraphics();
        g.setColor(new Color(205, 225, 228, 240));
        g.fillRect(0, 0, FOG_W, FOG_H);
        Random r = new Random(7);                        // soft blotches so it does not look flat
        for (int i = 0; i < 90; i++) {
            int size = 20 + r.nextInt(60);
            g.setColor(new Color(255, 255, 255, 18 + r.nextInt(30)));
            g.fillOval(r.nextInt(FOG_W), r.nextInt(FOG_H), size, size / 2 + 10);
        }
        g.dispose();
        wiped = new boolean[FOG_W / CELL + 1][FOG_H / CELL + 1];
        wipedCount = 0;
        lblFog.setText("");
        lblFog.setIcon(new ImageIcon(fogImg));
        lblFog.setVisible(true);
    }

    // clears a round hole in the fog where the mouse is
    private void wipe(int x, int y) {
        if (!fogActive || fogImg == null) {
            return;
        }
        Graphics2D g = fogImg.createGraphics();
        g.setComposite(AlphaComposite.Clear);
        g.fillOval(x - BRUSH, y - BRUSH, BRUSH * 2, BRUSH * 2);
        g.dispose();

        // remember which squares were touched so we know how much is clear
        for (int cx = Math.max(0, (x - BRUSH) / CELL); cx <= Math.min(wiped.length - 1, (x + BRUSH) / CELL); cx++) {
            for (int cy = Math.max(0, (y - BRUSH) / CELL); cy <= Math.min(wiped[0].length - 1, (y + BRUSH) / CELL); cy++) {
                if (!wiped[cx][cy]) {
                    wiped[cx][cy] = true;
                    wipedCount++;
                }
            }
        }
        lblFog.repaint();

        double done = wipedCount / (double) (wiped.length * wiped[0].length);
        if (done >= WIPE_GOAL) {
            fogActive = false;
            fadeFogAway();
        }
    }

    // the leftover fog melts away, then the answer box unlocks
    private void fadeFogAway() {
        final int[] step = {0};
        Timer t = new Timer(40, null);
        t.addActionListener(e -> {
            Graphics2D g = fogImg.createGraphics();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT, 0.3f));
            g.fillRect(0, 0, FOG_W, FOG_H);
            g.dispose();
            lblFog.repaint();
            step[0]++;
            if (step[0] >= 10) {
                t.stop();
                lblFog.setVisible(false);
                btnHint.setEnabled(true);
                btnSubmit.setEnabled(true);
                txtAnswer.setEnabled(true);
                txtAnswer.requestFocusInWindow();
                setFeedback("Each letter was pushed forward by the letter under it. Type the real message, then press Unlock.", true);
            }
        });
        t.start();
    }

    private void onSolved() {
        solved = true;
        puzzleActive = false;
        ScoreManager.onCorrect();
        btnSubmit.setVisible(false);
        btnHint.setVisible(false);
        showBackground(BG_FOG);
        pnlCipher.setVisible(false);
        setFeedback("", true);
        scene(AFTER, this::askFinalChoice);
    }

    // keeps only the letters A to Z, in capitals
    private static String lettersOnly(String text) {
        StringBuilder out = new StringBuilder();
        for (char c : text.toUpperCase().toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                out.append(c);
            }
        }
        return out.toString();
    }

    // ------------------------------------------------------------------
    //  The final choice (accept or deny), then a fade to black
    // ------------------------------------------------------------------

    private void askFinalChoice() {
        DialogueBox box = GameManager.getDialogue();
        if (box == null) {                // no dialogue box, so skip it (for testing only)
            fadeOut(LevelManager::nextLevel);
            return;
        }
        box.ask("Arnie", "What do you do?", CHOICES, pick -> {
            GameState gs = GameState.getInstance();
            if (pick == 0) {
                gs.putChoice("level10", "accept");
                gs.addDenial(ACCEPT_DENIAL);
                box.play(new String[][] {
                    {"Arnie [guilty]", "You're right. I did this. I'm sorry."}
                }, () -> fadeOut(LevelManager::nextLevel));
            } else {
                gs.putChoice("level10", "deny");
                gs.addDenial(DENY_DENIAL);
                box.play(new String[][] {
                    {"Arnie [guilty]", "No. You're not me."}
                }, () -> fadeOut(LevelManager::nextLevel));
            }
        });
    }

    // slowly turns the whole screen black, then runs "after" (the next screen)
    private void fadeOut(final Runnable after) {
        lblTitle.setVisible(false);              // only the background fades, the rest goes right away
        pnlCipher.setVisible(false);
        btnHint.setVisible(false);
        btnSubmit.setVisible(false);
        lblFeedback.setVisible(false);
        if (bgBase == null) {
            after.run();
            return;
        }
        final long start = System.currentTimeMillis();
        final int duration = 1400;               // milliseconds
        Timer t = new Timer(30, null);
        t.addActionListener(e -> {
            double p = Math.min(1.0, (System.currentTimeMillis() - start) / (double) duration);
            lblBg.setIcon(new ImageIcon(darker(bgBase, (int) (p * 100))));
            if (p >= 1.0) {
                t.stop();
                after.run();
            }
        });
        t.start();
    }

    // a copy of the picture made darker (0 = normal, 100 = black)
    private BufferedImage darker(BufferedImage src, int percent) {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.setColor(new Color(0, 0, 0, Math.max(0, Math.min(100, percent)) * 255 / 100));
        g.fillRect(0, 0, out.getWidth(), out.getHeight());
        g.dispose();
        return out;
    }

    // ------------------------------------------------------------------
    //  Dialogue and picture helpers
    // ------------------------------------------------------------------

    private void scene(String[][] lines, Runnable after) {
        playFrom(lines, 0, after);
    }

    // plays one line, then goes to the next one. runs "after" when there are no more lines
    private void playFrom(final String[][] lines, final int i, final Runnable after) {
        if (i >= lines.length) {
            if (after != null) {
                after.run();
            }
            return;
        }
        String speaker = lines[i][0];
        String text = lines[i][1];

        Matcher m = MOOD.matcher(speaker);        // takes the [mood] out of the name, "Arnie [guilty]" becomes "Arnie"
        if (m.matches()) {
            speaker = m.group(1);
        }

        // check for tags at the start of the text, {bg:file.png} changes the background
        while (text.startsWith("{")) {
            int end = text.indexOf('}');
            if (end < 0) {
                break;
            }
            String tag = text.substring(1, end);
            text = text.substring(end + 1).trim();
            if (tag.equals("glitch")) {
                glitchNext = true;                // the next {bg:...} will glitch in
            } else if (tag.startsWith("bg:")) {
                showBackground(tag.substring(3));
            }
        }

        DialogueBox box = GameManager.getDialogue();
        if (box == null) {                        // no dialogue box, so skip it (for testing only)
            playFrom(lines, i + 1, after);
            return;
        }
        box.play(new String[][] { { speaker, text } }, () -> playFrom(lines, i + 1, after));
    }

    // changes the background picture, it fills 1000 x 580 and cuts off what does not fit
    private void showBackground(String file) {
        java.net.URL url = getClass().getResource("/kldcrimedecoder/images/" + file);
        if (url == null) {                        // file not found, so show a message instead
            bgBase = null;
            lblBg.setIcon(null);
            lblBg.setText("[background missing: " + file + "]");
            return;
        }
        java.awt.Image src = new ImageIcon(url).getImage();
        int w = 1000, h = 580;
        double s = Math.max((double) w / src.getWidth(null), (double) h / src.getHeight(null));
        int dw = (int) Math.ceil(src.getWidth(null) * s);
        int dh = (int) Math.ceil(src.getHeight(null) * s);
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(src, (w - dw) / 2, (h - dh) / 2, dw, dh, null);
        g.dispose();
        bgBase = out;                             // remembered for the fade at the end
        lblBg.setText("");
        if (glitchNext) {
            glitchNext = false;
            playGlitch(out);
        } else {
            lblBg.setIcon(new ImageIcon(out));
        }
    }

    // glitch transition: about half a second of broken frames, then the real picture
    private void playGlitch(final BufferedImage target) {
        final Random rnd = new Random();
        final int totalFrames = 10;
        final int[] frame = {0};
        Timer timer = new Timer(50, null);
        timer.addActionListener(e -> {
            frame[0]++;
            if (frame[0] >= totalFrames) {
                lblBg.setIcon(new ImageIcon(target));   // done, show it clean
                ((Timer) e.getSource()).stop();
                return;
            }
            int power = 90 - frame[0] * 8;        // the glitch gets weaker every frame
            lblBg.setIcon(new ImageIcon(glitchFrame(target, rnd, power)));
        });
        timer.start();
    }

    // one broken frame: cuts the picture into strips and slides each strip sideways
    private BufferedImage glitchFrame(BufferedImage src, Random rnd, int power) {
        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        int y = 0;
        while (y < h) {
            int stripH = Math.min(6 + rnd.nextInt(40), h - y);
            int dx = rnd.nextInt(3) == 0 ? 0 : rnd.nextInt(power * 2 + 1) - power;
            BufferedImage strip = src.getSubimage(0, y, w, stripH);
            g.drawImage(strip, dx, y, null);
            g.drawImage(strip, dx > 0 ? dx - w : dx + w, y, null);   // wraps around the edge
            if (rnd.nextInt(6) == 0) {                                 // random colored bar
                g.setColor(new Color(0, 255, 200, 70));
                g.fillRect(0, y, w, stripH);
            }
            y += stripH;
        }
        g.dispose();
        return out;
    }

    private void setFeedback(String text, boolean good) {
        lblFeedback.setForeground(good ? Theme.AMBER : new Color(0xFF8A8A));
        lblFeedback.setText(text);
        lblFeedback.setVisible(!text.isEmpty());  // no empty dark bar when there is nothing to say
    }

    // puts a space between the letters so they look like writing in the steam
    private static String spaced(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c == ' ') {
                sb.append("   ");
            } else {
                sb.append(c).append(' ');
            }
        }
        return sb.toString().trim();
    }

    // a border that paints a see-through dark box with an amber line around the puzzle
    private static class FogBorder extends AbstractBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(11, 18, 32, 190));
            g2.fillRect(x, y, w, h);
            g2.setColor(Theme.AMBER);
            g2.drawRect(x, y, w - 1, h - 1);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(6, 6, 6, 6);
        }
    }
}