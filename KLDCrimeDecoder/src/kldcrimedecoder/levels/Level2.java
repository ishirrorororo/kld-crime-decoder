package kldcrimedecoder.levels;
 
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;
import kldcrimedecoder.data.Story;
import kldcrimedecoder.engine.GameManager;
import kldcrimedecoder.engine.LevelManager;
import kldcrimedecoder.ui.ScenePanel;
import kldcrimedecoder.ui.Theme;
import kldcrimedecoder.engine.ScoreManager;
/**
 * 
 */
public class Level2 extends kldcrimedecoder.ui.ScenePanel {   
    private static final int[] VALUES = { 128, 64, 32, 16, 8, 4, 2, 1 };
    private static final int ANSWER = 5;
    private static final int MAX_HINTS = 2;
 
    private JButton[] bulbs;
    private final boolean[] lit = new boolean[8];         // which bulbs are lit
    private final boolean[] counted = new boolean[8];     // which lit bulbs the player added
    private boolean busy;                                 // true while a dialogue is playing
    private boolean solved;
    private int hintsUsed;
    private boolean reishaConfused;
    private boolean reishaSpeaking;

    /**
     * Creates new form Level1
     */
    public Level2() {
        initComponents();
        bulbs = new JButton[] { btnBulb128, btnBulb64, btnBulb32, btnBulb16,
                                btnBulb8, btnBulb4, btnBulb2, btnBulb1 };
        lit[5] = true;                                    // the 4
        lit[7] = true;                                    // the 1

        Theme.background(this);
        Theme.label(lblInstruction, lblTotal, lblFeedback);
        Theme.button(btnSearch, btnHint);
        styleNoteAndLogins();

        for (int i = 0; i < bulbs.length; i++) {
            final int idx = i;
            JButton b = bulbs[i];
            for (java.awt.event.ActionListener old : b.getActionListeners()) {
                b.removeActionListener(old);              // remove every listener from the Design tab
            }
            b.setUI(new BasicButtonUI());
            b.setFocusPainted(false);
            b.setFont(b.getFont().deriveFont(Font.BOLD, 13f));
            b.addActionListener(e -> clickBulb(idx));     // exactly one listener per bulb
        }
    }

    @Override
    protected void startScene() {
        GameManager.setTimerVisible(true);
        solved = false;
        hintsUsed = 0;
        reishaConfused = false;
        reishaSpeaking = false;
        java.util.Arrays.fill(counted, false);
        txtShelf.setText("");
        btnHint.setEnabled(true);
        lblInstruction.setText(Story.L2_PROMPT);
        showFeedback(lblFeedback, "", true);              // hides the label
        refreshBulbs();
        updateTotal();
        refreshReisha();
        lblLogins.setVisible(false);                      // hidden until Reisha opens the log
        showPuzzle(false);                                // nothing of the puzzle yet

        String[][] before = java.util.Arrays.copyOfRange(Story.L2_INTRO, 0, 3);
        String[][] after = java.util.Arrays.copyOfRange(Story.L2_INTRO, 3, Story.L2_INTRO.length);

        busy = true;                                      // clicks wait until the intro ends
        scene(before, () -> {
            lblLogins.setVisible(true);                   // after "Staff only. But it's you, so."
            scene(after, () -> {
                reishaSpeaking = false;                   // Reisha leaves
                refreshReisha();
                showPuzzle(true);                         // now the bulbs, note and search appear
                busy = false;
            });
        });
    }
    
        private void showPuzzle(boolean on) {
        lblInstruction.setVisible(on);
        lblTotal.setVisible(on);
        lblNote.setVisible(on);
        txtShelf.setVisible(on);
        btnSearch.setVisible(on);
        btnHint.setVisible(on);
        for (JButton b : bulbs) {
            b.setVisible(on);
        }
    }
        
    // ------------------------------------------------------------ puzzle
    private void clickBulb(int i) {
        if (busy || solved) {
            return;
        }
        if (!lit[i]) {
            showFeedback(lblFeedback, Story.L2_DARK, false);
            return;
        }
        counted[i] = !counted[i];                         // click again to remove it
        refreshBulbs();
        updateTotal();
    }
       
    private void updateTotal() {
        int total = 0;
        for (int i = 0; i < VALUES.length; i++) {
            if (counted[i]) {
                total += VALUES[i];
            }
        }
        lblTotal.setText("Total: " + total);
    }

    private void checkAnswer() {
        if (busy || solved) {
            return;
        }
        String digits = txtShelf.getText().replaceAll("\\D", "");   // "005" also works
        if (digits.isEmpty()) {
            return;
        }
        boolean right = digits.length() <= 6 && Integer.parseInt(digits) == ANSWER;
        if (right) {
            solved = true;
            busy = true;
            ScoreManager.onCorrect();                 // +100 and the speed bonus
            showFeedback(lblFeedback, Story.L2_RIGHT, true);
            scene(Story.L2_SOLVED, LevelManager::nextLevel);
        } else {
            ScoreManager.onWrong();                   // -10 points and time
            showFeedback(lblFeedback, Story.L2_WRONG, false);
            txtShelf.selectAll();
        }
    }

    private void useHint() {
        if (busy || solved || hintsUsed >= MAX_HINTS) {
            return;
        }
        ScoreManager.onHint();                        // -5 points and time
        int n = hintsUsed++;
        if (hintsUsed >= MAX_HINTS) {
            btnHint.setEnabled(false);
        }
        busy = true;
        scene(new String[][] {
            { "Arnie (thinking)", Story.L1_HINT_INTRO },
            { "Reisha [happy]", Story.L2_HINTS[n] }
        }, () -> {
            reishaSpeaking = false;                   // she goes away after the hint
            refreshReisha();
            busy = false;
        });
    }
    /** TODO: replace the body with the SAME line Level1 uses to take seconds off the timer. */

    
    // ---------------------------------------------------------------- Reisha
    @Override
    protected void onMood(String mood) {
        if (reishaSpeaking) {
            reishaConfused = mood.equals("confused");
            refreshReisha();
        }
    }

    private void refreshReisha() {
        lblReisha.setVisible(reishaSpeaking && !reishaConfused);
        lblReishaConfused.setVisible(reishaSpeaking && reishaConfused);
    }
    
    @Override
    protected void onSpeaker(String speaker) {
        reishaSpeaking = speaker.startsWith("Reisha");
        reishaConfused = false;                           // onMood runs right after and sets the face
        refreshReisha();
    }

    
    // ---------------------------------------------------------------- looks
    private void refreshBulbs() {
        for (int i = 0; i < bulbs.length; i++) {
            JButton b = bulbs[i];
            b.setOpaque(true);
            b.setContentAreaFilled(true);
            if (!lit[i]) {                                // dark bulb
                b.setBackground(new Color(0x3A3F4B));
                b.setForeground(new Color(0x7A8090));
                b.setBorder(BorderFactory.createLineBorder(new Color(0x2A2E38), 2));
            } else if (counted[i]) {                      // lit and added
                b.setBackground(new Color(0xE0A100));
                b.setForeground(new Color(0x2B2B3B));
                b.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            } else {                                      // lit, not added yet
                b.setBackground(new Color(0xFFE98A));
                b.setForeground(new Color(0x2B2B3B));
                b.setBorder(BorderFactory.createLineBorder(new Color(0xE0A100), 2));
            }
        }
    }

    private void styleNoteAndLogins() {
        lblInstruction.setOpaque(true);
        lblInstruction.setBackground(new Color(0x0A0F1A));
        lblInstruction.setForeground(Color.WHITE);
        lblInstruction.setHorizontalAlignment(SwingConstants.CENTER);
        lblInstruction.setBorder(BorderFactory.createLineBorder(new Color(0xE0A100), 1));
        lblNote.setOpaque(true);
        lblNote.setBackground(new Color(0xFFE98A));
        lblNote.setForeground(new Color(0x2B2B3B));
        lblNote.setFont(lblNote.getFont().deriveFont(Font.BOLD, 11f));
        lblNote.setHorizontalAlignment(SwingConstants.CENTER);
        lblNote.setText("<html><center>" + Story.L2_NOTE + "</center></html>");
 
        lblLogins.setOpaque(true);
        lblLogins.setBackground(new Color(0x0A0F1A));
        lblLogins.setForeground(new Color(0x6CFF9A));
        lblLogins.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        lblLogins.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogins.setText("<html><center>ALLI&nbsp; 9:38 PM<br>AMIE&nbsp; 9:40 PM</center></html>");
    }
    
    
// ===== END TEMPORARY TEST CODE =====
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnHint = new javax.swing.JButton();
        lblFeedback = new javax.swing.JLabel();
        lblInstruction = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();
        txtShelf = new javax.swing.JTextField();
        lblTotal = new javax.swing.JLabel();
        btnBulb128 = new javax.swing.JButton();
        btnBulb64 = new javax.swing.JButton();
        btnBulb32 = new javax.swing.JButton();
        btnBulb16 = new javax.swing.JButton();
        btnBulb8 = new javax.swing.JButton();
        btnBulb4 = new javax.swing.JButton();
        btnBulb2 = new javax.swing.JButton();
        btnBulb1 = new javax.swing.JButton();
        lblNote = new javax.swing.JLabel();
        lblLogins = new javax.swing.JLabel();
        lblReishaConfused = new javax.swing.JLabel();
        lblReisha = new javax.swing.JLabel();
        lblBg = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1000, 600));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnHint.setText("Hint");
        btnHint.addActionListener(this::btnHintActionPerformed);
        add(btnHint, new org.netbeans.lib.awtextra.AbsoluteConstraints(878, 392, 90, 32));
        add(lblFeedback, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 392, 160, 60));
        add(lblInstruction, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 12, 680, 30));

        btnSearch.setText("Search");
        btnSearch.addActionListener(this::btnSearchActionPerformed);
        add(btnSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(878, 350, 90, 32));

        txtShelf.addActionListener(this::txtShelfActionPerformed);
        add(txtShelf, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 350, 160, 32));
        add(lblTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 310, 258, 28));

        btnBulb128.setText("128");
        btnBulb128.addActionListener(this::btnBulb128ActionPerformed);
        add(btnBulb128, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 228, 60, 34));

        btnBulb64.setText("64");
        btnBulb64.addActionListener(this::btnBulb64ActionPerformed);
        add(btnBulb64, new org.netbeans.lib.awtextra.AbsoluteConstraints(776, 228, 60, 34));

        btnBulb32.setText("32");
        add(btnBulb32, new org.netbeans.lib.awtextra.AbsoluteConstraints(842, 228, 60, 34));

        btnBulb16.setText("16");
        btnBulb16.addActionListener(this::btnBulb16ActionPerformed);
        add(btnBulb16, new org.netbeans.lib.awtextra.AbsoluteConstraints(908, 228, 60, 34));

        btnBulb8.setText("8");
        btnBulb8.addActionListener(this::btnBulb8ActionPerformed);
        add(btnBulb8, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 268, 60, 34));

        btnBulb4.setText("4");
        btnBulb4.addActionListener(this::btnBulb4ActionPerformed);
        add(btnBulb4, new org.netbeans.lib.awtextra.AbsoluteConstraints(776, 268, 60, 34));

        btnBulb2.setText("2");
        btnBulb2.addActionListener(this::btnBulb2ActionPerformed);
        add(btnBulb2, new org.netbeans.lib.awtextra.AbsoluteConstraints(842, 268, 60, 34));

        btnBulb1.setText("1");
        btnBulb1.addActionListener(this::btnBulb1ActionPerformed);
        add(btnBulb1, new org.netbeans.lib.awtextra.AbsoluteConstraints(908, 268, 60, 34));
        add(lblNote, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 90, 260, 70));
        add(lblLogins, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 90, 260, 70));

        lblReishaConfused.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/reisha_confused_small.png"))); // NOI18N
        add(lblReishaConfused, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 343, 406));

        lblReisha.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/reisha_happy_small.png"))); // NOI18N
        add(lblReisha, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 343, 406));

        lblBg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/room_library.png"))); // NOI18N
        add(lblBg, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 600));
    }// </editor-fold>//GEN-END:initComponents

    private void btnBulb128ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBulb128ActionPerformed
        clickBulb(0);
    }//GEN-LAST:event_btnBulb128ActionPerformed

    private void btnBulb64ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBulb64ActionPerformed
        clickBulb(1);
    }//GEN-LAST:event_btnBulb64ActionPerformed

    private void btnBulb16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBulb16ActionPerformed
        clickBulb(3);
    }//GEN-LAST:event_btnBulb16ActionPerformed

    private void btnBulb8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBulb8ActionPerformed
        clickBulb(4);
    }//GEN-LAST:event_btnBulb8ActionPerformed

    private void btnBulb4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBulb4ActionPerformed
        clickBulb(5);
    }//GEN-LAST:event_btnBulb4ActionPerformed

    private void btnBulb2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBulb2ActionPerformed
        clickBulb(6);
    }//GEN-LAST:event_btnBulb2ActionPerformed

    private void btnBulb1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBulb1ActionPerformed
        clickBulb(7);
    }//GEN-LAST:event_btnBulb1ActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        checkAnswer();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void txtShelfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtShelfActionPerformed
        checkAnswer();
    }//GEN-LAST:event_txtShelfActionPerformed

    private void btnHintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHintActionPerformed
        useHint();
    }//GEN-LAST:event_btnHintActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBulb1;
    private javax.swing.JButton btnBulb128;
    private javax.swing.JButton btnBulb16;
    private javax.swing.JButton btnBulb2;
    private javax.swing.JButton btnBulb32;
    private javax.swing.JButton btnBulb4;
    private javax.swing.JButton btnBulb64;
    private javax.swing.JButton btnBulb8;
    private javax.swing.JButton btnHint;
    private javax.swing.JButton btnSearch;
    private javax.swing.JLabel lblBg;
    private javax.swing.JLabel lblFeedback;
    private javax.swing.JLabel lblInstruction;
    private javax.swing.JLabel lblLogins;
    private javax.swing.JLabel lblNote;
    private javax.swing.JLabel lblReisha;
    private javax.swing.JLabel lblReishaConfused;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTextField txtShelf;
    // End of variables declaration//GEN-END:variables
}
