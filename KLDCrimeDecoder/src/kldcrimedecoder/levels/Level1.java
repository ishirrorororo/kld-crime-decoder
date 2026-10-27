package kldcrimedecoder.levels;
 
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import kldcrimedecoder.data.Story;
import kldcrimedecoder.engine.*;
import kldcrimedecoder.puzzles.CipherWheel;
import kldcrimedecoder.puzzles.Puzzle;
import kldcrimedecoder.ui.CipherWheelPanel;
 
/**
 * Level 1: Alli's Phone (cipher wheel).
 * Labels and buttons are made in the Design tab (Level1Phone.form).
 * Background alli_phone is drawn by ScenePanel (no background label).
 * INHERITANCE: extends ScenePanel. ABSTRACTION: the puzzle is held as a Puzzle.
 */
public class Level1 extends ScenePanel {
 
    private static final Color PHONE_BG = new Color(0x10151F);
 
    private final CipherWheel wheel = new CipherWheel(Story.LEVEL1_CODED, Story.LEVEL1_PLAIN);
    private final Puzzle puzzle = wheel;                 // same object, seen as the interface
    private final CipherWheelPanel wheelPanel;
    private boolean busy;
 
    public Level1() {
        initComponents();
        wheelPanel = new CipherWheelPanel(wheel);       // the only hand-made component
        WheelPanel.setBounds(344, 80, 240, 240);   // inside the phone screen
        add(WheelPanel);
        styleComponents();
 
        btnLeft.addActionListener(e -> turn(-1));
        btnRight.addActionListener(e -> turn(1));
        btnHint.addActionListener(e -> lblHint.setText(wrapText(wheel.getHint())));
        btnUnlock.addActionListener(e -> tryUnlock());
 
        showPuzzle(false);
        setScene("alli_phone", PHONE_BG, true);
 
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                startScene();
            }
        });
    }
 
    private void styleComponents() {
        Font mono = new Font("Consolas", Font.BOLD, 20);
        for (javax.swing.JLabel l : new javax.swing.JLabel[]{lblCoded, lblDecoded, lblShift, lblHint, lblWifi}) {
            l.setFont(mono);
            l.setForeground(Color.WHITE);
            l.setBackground(PHONE_BG);
            l.setOpaque(true);
            l.setHorizontalAlignment(SwingConstants.CENTER);
        }
        lblShift.setFont(new Font("Consolas", Font.BOLD, 14));
        lblWifi.setFont(new Font("Consolas", Font.BOLD, 15));
        lblHint.setFont(new Font("Consolas", Font.PLAIN, 12));
        lblHint.setForeground(new Color(255, 230, 120));
        lblWifi.setBorder(BorderFactory.createLineBorder(new Color(150, 200, 255), 2));
        lblCoded.setText(wheel.getCoded());
    }
 
    @Override
    public void startScene() {
        busy = true;
        hideCharacter();
        hideOverlay();
        wheel.rotate(-wheel.getShift());                // back to 0 if replayed
        lblHint.setText("");
        lblWifi.setVisible(false);
        showPuzzle(false);
        setScene("alli_phone", PHONE_BG, true);
        // TODO: play notification chime here with your sound helper
        play(Story.LEVEL1_INTRO, () -> {
            busy = false;
            showPuzzle(true);
            refresh();
        });
    }
 
    /** Lets a long label text wrap inside the label (centered). */
    private String wrapText(String text) {
        return "<html><div style='text-align:center'>" + text + "</div></html>";
    }
 
    private void turn(int clicks) {
        if (busy) {
            return;
        }
        wheel.rotate(clicks);
        refresh();
    }
 
    private void refresh() {
        lblShift.setText("Shift: " + wheel.getShift());
        lblDecoded.setText(wheel.decode());
        wheelPanel.repaint();
    }
 
    private void tryUnlock() {
        if (busy) {
            return;
        }
        if (!puzzle.isSolved()) {
            lblHint.setText(wrapText("The lock does not move. The text does not make sense yet."));
            return;
        }
        busy = true;
        showPuzzle(false);
        play(Story.LEVEL1_AFTER_A, () -> {
            lblWifi.setText(wrapText(Story.LEVEL1_WIFI));          // Wi-Fi tab opens by itself
            lblWifi.setVisible(true);
            GameState.getInstance().addItem(Story.ITEM_WIFI_LOG);
            play(Story.LEVEL1_AFTER_B, () ->
                play(Story.LEVEL1_OUT, LevelManager::nextLevel));
        });
    }
 
    private void showPuzzle(boolean show) {
        WheelPanel.setVisible(show);
        lblCoded.setVisible(show);
        lblDecoded.setVisible(show);
        lblShift.setVisible(show);
        lblHint.setVisible(show);
        btnLeft.setVisible(show);
        btnRight.setVisible(show);
        btnHint.setVisible(show);
        btnUnlock.setVisible(show);
    }
 
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblCoded = new javax.swing.JLabel();
        lblDecoded = new javax.swing.JLabel();
        btnLeft = new javax.swing.JButton();
        lblShift = new javax.swing.JLabel();
        btnRight = new javax.swing.JButton();
        btnHint = new javax.swing.JButton();
        btnUnlock = new javax.swing.JButton();
        lblHint = new javax.swing.JLabel();
        lblWifi = new javax.swing.JLabel();
        WheelPanel = new kldcrimedecoder.ui.CipherWheelPanel(wheel);

        setLayout(null);

        lblCoded.setText("Coded");
        add(lblCoded);
        lblCoded.setBounds(350, 50, 300, 34);

        lblDecoded.setText("Decoded");
        add(lblDecoded);
        lblDecoded.setBounds(350, 405, 300, 34);

        btnLeft.setText("< Left");
        btnLeft.addActionListener(this::btnLeftActionPerformed);
        add(btnLeft);
        btnLeft.setBounds(300, 445, 120, 30);

        lblShift.setText("Shift");
        add(lblShift);
        lblShift.setBounds(430, 445, 140, 30);

        btnRight.setText("> Right");
        btnRight.addActionListener(this::btnRightActionPerformed);
        add(btnRight);
        btnRight.setBounds(580, 445, 120, 30);

        btnHint.setText("Hint");
        add(btnHint);
        btnHint.setBounds(300, 485, 120, 30);

        btnUnlock.setText("Unlock");
        add(btnUnlock);
        btnUnlock.setBounds(580, 485, 120, 30);

        lblHint.setText("Hint: ");
        add(lblHint);
        lblHint.setBounds(300, 520, 400, 24);

        lblWifi.setText("Wifi");
        add(lblWifi);
        lblWifi.setBounds(300, 200, 400, 60);

        javax.swing.GroupLayout WheelPanelLayout = new javax.swing.GroupLayout(WheelPanel);
        WheelPanel.setLayout(WheelPanelLayout);
        WheelPanelLayout.setHorizontalGroup(
            WheelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 240, Short.MAX_VALUE)
        );
        WheelPanelLayout.setVerticalGroup(
            WheelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 240, Short.MAX_VALUE)
        );

        add(WheelPanel);
        WheelPanel.setBounds(344, 80, 240, 240);
    }// </editor-fold>//GEN-END:initComponents

    private void btnRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRightActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRightActionPerformed

    private void btnLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeftActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnLeftActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel WheelPanel;
    private javax.swing.JButton btnHint;
    private javax.swing.JButton btnLeft;
    private javax.swing.JButton btnRight;
    private javax.swing.JButton btnUnlock;
    private javax.swing.JLabel lblCoded;
    private javax.swing.JLabel lblDecoded;
    private javax.swing.JLabel lblHint;
    private javax.swing.JLabel lblShift;
    private javax.swing.JLabel lblWifi;
    // End of variables declaration//GEN-END:variables
}
