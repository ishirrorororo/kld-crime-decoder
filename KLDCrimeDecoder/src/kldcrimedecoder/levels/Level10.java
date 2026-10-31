/**
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */

package kldcrimedecoder.levels;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Icon;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.AbstractBorder;
import kldcrimedecoder.data.Story;
import kldcrimedecoder.engine.GameManager;
import kldcrimedecoder.engine.GameState;
import kldcrimedecoder.engine.LevelManager;
import kldcrimedecoder.engine.ScoreManager;
import kldcrimedecoder.ui.DialogueBox;
import kldcrimedecoder.ui.Theme;

/**
 *
 * @author Rosaldo
 */
public class Level10 extends javax.swing.JPanel {
    // lets us write "Arnie [guilty]" and split it into the name and the mood
    private static final Pattern MOOD = Pattern.compile("^(.*?)\\s*\\[(\\w+)\\]$");
 
    private static final String SECRET = "I AM YOU";   // the hidden message in the steam
    private static final int ACCEPT_DENIAL = -10;       // accepting the truth lowers the denial meter
    private static final int DENY_DENIAL = 50;          // denying it raises the meter to the bad ending limit
 
    // background pictures, the files are inside the images folder
    private static final String BG_START = "bathroom.png";
    private static final String BG_FACE  = "bathroomScene1.png";
    private static final String BG_MIRROR = "bathroomMirror.png";
    private static final String BG_AMIE  = "bathroomAmie.png";
    private static final String BG_FOG   = "bathroomFog.png";
 
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
        "You chose the key at the very beginning.",
        "Count the letters in the nickname Alli's lock screen asked you for."
    };
 
    private int shift;              // the correct number, same as the length of the nickname
    private String coded;           // the secret message after it was scrambled
    private boolean glitchNext;     // true = the next background change plays the glitch effect
    private boolean puzzleActive;
    private boolean solved;
    private int hintsUsed;
 
    public Level10() {
        initComponents();

        // use the same dark theme as the other levels
        Theme.background(this);
        Theme.heading(lblTitle);
        Theme.label(lblFeedback, lblCoded, lblDecoded);
        Theme.button(btnSubmit, btnHint);
        pnlCipher.setOpaque(false);
        pnlCipher.setBorder(new FogBorder());
 
        // big letters for the steam message, amber for the answer preview
        lblCoded.setFont(new Font("Consolas", Font.BOLD, 34));
        lblCoded.setForeground(new Color(0xCFEFEA));
        lblCoded.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDecoded.setFont(new Font("Consolas", Font.BOLD, 26));
        lblDecoded.setForeground(Theme.AMBER);
        lblDecoded.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        spnShift.setFont(new Font("Consolas", Font.BOLD, 20));
        lblFeedback.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText(Story.LEVEL_TITLES[10]);
 
        // spinner goes from 0 to 25, the player picks the number
        spnShift.setModel(new SpinnerNumberModel(0, 0, 25, 1));
        spnShift.addChangeListener(e -> showDecoded());
 
        // restart the level every time this screen is shown
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                startLevel();
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        pnlCipher = new javax.swing.JPanel();
        lblCoded = new javax.swing.JLabel();
        spnShift = new javax.swing.JSpinner();
        lblDecoded = new javax.swing.JLabel();
        btnSubmit = new javax.swing.JButton();
        btnHint = new javax.swing.JButton();
        lblFeedback = new javax.swing.JLabel();
        lblBg = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setText("jLabel1");
        add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 960, 30));

        pnlCipher.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblCoded.setText("jLabel1");
        pnlCipher.add(lblCoded, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 15, 500, 45));
        pnlCipher.add(spnShift, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 75, 80, 30));
        spnShift.getAccessibleContext().setAccessibleName("");

        lblDecoded.setText("jLabel2");
        pnlCipher.add(lblDecoded, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 115, 500, 40));
        lblDecoded.getAccessibleContext().setAccessibleDescription("");

        add(pnlCipher, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 50, 500, 170));
        pnlCipher.getAccessibleContext().setAccessibleName("");

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
        lblBg.getAccessibleContext().setAccessibleName("");
    }// </editor-fold>//GEN-END:initComponents

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        if (!puzzleActive || solved) {
            return;
        }
        int turn = (Integer) spnShift.getValue();
        if (turn == shift) {
            onSolved();
        } else {
            ScoreManager.onWrong();
            setFeedback("Those letters still don't make a sentence. Turn it again.", false);
        }
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void btnHintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHintActionPerformed
        if (!puzzleActive || solved) {
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
    private javax.swing.JLabel lblDecoded;
    private javax.swing.JLabel lblFeedback;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlCipher;
    private javax.swing.JSpinner spnShift;
    // End of variables declaration//GEN-END:variables
 
    // ------------------------------------------------------------------
    //  Starting the level
    // ------------------------------------------------------------------
 
    private void startLevel() {
        solved = false;
        puzzleActive = false;
        hintsUsed = 0;
 
        // the nickname from the Prologue decides the number (uses ARNIE if nothing was saved)
        String nickname = GameState.getInstance()
                .getChoice(GameState.KEY_FINAL_WORD, "ARNIE").trim();
        shift = Math.max(1, Math.min(25, nickname.length()));
        coded = caesar(SECRET, shift);
 
        showBackground(BG_START);
        pnlCipher.setVisible(false);
        btnHint.setVisible(false);       // the buttons only show with the puzzle
        btnSubmit.setVisible(false);
        spnShift.setValue(0);
        setFeedback("", true);
 
        scene(INTRO, this::beginPuzzle);
    }
 
    // dialogue is done, so show the puzzle
    private void beginPuzzle() {
        lblCoded.setText(spaced(coded));
        spnShift.setValue(0);
        showDecoded();
        pnlCipher.setVisible(true);
        btnHint.setVisible(true);
        btnSubmit.setVisible(true);
        puzzleActive = true;
        btnHint.setEnabled(true);
        btnSubmit.setEnabled(true);
        setFeedback("Turn the number until the steam makes sense, then press Unlock.", true);
    }
 
    // ------------------------------------------------------------------
    //  Puzzle
    // ------------------------------------------------------------------
 
    // shows what the message looks like with the number the player picked
    private void showDecoded() {
        int turn = (Integer) spnShift.getValue();
        lblDecoded.setText(spaced(caesar(coded, -turn)));
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
 
    // shifts every letter by "amount" (a negative amount shifts backwards)
    private static String caesar(String text, int amount) {
        StringBuilder out = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                out.append((char) ('A' + ((c - 'A' + amount) % 26 + 26) % 26));
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }
 
    // ------------------------------------------------------------------
    //  The final choice (accept or deny)
    // ------------------------------------------------------------------
 
    private void askFinalChoice() {
        DialogueBox box = GameManager.getDialogue();
        if (box == null) {                // no dialogue box, so skip it (for testing only)
            LevelManager.nextLevel();
            return;
        }
        box.ask("Arnie", "What do you do?", CHOICES, pick -> {
            GameState gs = GameState.getInstance();
            if (pick == 0) {
                gs.putChoice("level10", "accept");
                gs.addDenial(ACCEPT_DENIAL);
                box.play(new String[][] {
                    {"Arnie [guilty]", "You're right. I did this. I'm sorry."}
                }, LevelManager::nextLevel);
            } else {
                gs.putChoice("level10", "deny");
                gs.addDenial(DENY_DENIAL);
                box.play(new String[][] {
                    {"Arnie [guilty]", "No. You're not me."}
                }, LevelManager::nextLevel);
            }
        });
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
            lblBg.setIcon(null);
            lblBg.setText("[background missing: " + file + "]");
            return;
        }
        java.awt.Image src = new javax.swing.ImageIcon(url).getImage();
        int w = 1000, h = 580;
        double s = Math.max((double) w / src.getWidth(null), (double) h / src.getHeight(null));
        int dw = (int) Math.ceil(src.getWidth(null) * s);
        int dh = (int) Math.ceil(src.getHeight(null) * s);
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(src, (w - dw) / 2, (h - dh) / 2, dw, dh, null);
        g.dispose();
        lblBg.setText("");
        if (glitchNext) {
            glitchNext = false;
            playGlitch(out);
        } else {
            lblBg.setIcon(new javax.swing.ImageIcon(out));
        }
    }
 
    // glitch transition: about half a second of broken frames, then the real picture
    private void playGlitch(final BufferedImage target) {
        final Random rnd = new Random();
        final int totalFrames = 10;
        final int[] frame = {0};
        javax.swing.Timer timer = new javax.swing.Timer(50, null);
        timer.addActionListener(e -> {
            frame[0]++;
            if (frame[0] >= totalFrames) {
                lblBg.setIcon(new javax.swing.ImageIcon(target));   // done, show it clean
                ((javax.swing.Timer) e.getSource()).stop();
                return;
            }
            int power = 90 - frame[0] * 8;        // the glitch gets weaker every frame
            lblBg.setIcon(new javax.swing.ImageIcon(glitchFrame(target, rnd, power)));
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
        lblFeedback.setForeground(good ? Theme.TEXT : new Color(0xFF6B6B));
        lblFeedback.setText(text);
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