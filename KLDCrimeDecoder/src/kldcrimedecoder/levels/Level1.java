package kldcrimedecoder.levels;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import kldcrimedecoder.data.Story;
import kldcrimedecoder.engine.GameManager;
import kldcrimedecoder.engine.GameState;
import kldcrimedecoder.engine.LevelManager;
import kldcrimedecoder.ui.HoverGlow;
import kldcrimedecoder.ui.ScenePanel;
import kldcrimedecoder.ui.Theme;
import kldcrimedecoder.engine.ScoreManager;
/**
 *
 * @author Alli Ocampo
 */
public class Level1 extends kldcrimedecoder.ui.ScenePanel {

    private static final int MAX_HINTS = 2;
    private static final Color SCREEN_BG = new Color(0x0F1626);
 
    private int turn;                    // how many clicks the wheel was turned
    private int hintsUsed;
    private boolean busy;                // true while a dialogue plays
    private boolean solved;
    private WheelView wheel;
    private PhoneGlow glow;
 
    public Level1() {
        initComponents();
        buildGlow();
        fit580(lblPhone);                // shrink the pictures to 580 x 580 whatever file is set
        fit580(lblPhoneWifi);
        Theme.background(this);
        buildScreens();
        styleControls();
    }
    
    // ------------------------------------------------------------ setup
    private void fit580(JLabel lbl) {
        Icon ic = lbl.getIcon();
        if (ic instanceof ImageIcon && ic.getIconWidth() != 580) {
            lbl.setIcon(new ImageIcon(((ImageIcon) ic).getImage()
                    .getScaledInstance(580, 580, Image.SCALE_SMOOTH)));
        }
    }
    
    private void buildScreens() {
        // chat: Amie's two texts
        pnlChat.setLayout(new javax.swing.BoxLayout(pnlChat, javax.swing.BoxLayout.Y_AXIS));
        pnlChat.setOpaque(true);
        pnlChat.setBackground(SCREEN_BG);
        pnlChat.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel head = new JLabel("Amie");
        head.setFont(Theme.HEADING);
        head.setForeground(Theme.AMBER);
        head.setAlignmentX(LEFT_ALIGNMENT);
        pnlChat.add(head);
        pnlChat.add(javax.swing.Box.createVerticalStrut(12));
        for (String[] line : Story.L1_AMIE) {
            pnlChat.add(bubble(line[1]));
            pnlChat.add(javax.swing.Box.createVerticalStrut(10));
        }
        
        // cipher wheel
        wheel = new WheelView();
        pnlWheel.setLayout(new java.awt.BorderLayout());
        pnlWheel.setOpaque(true);
        pnlWheel.setBackground(SCREEN_BG);
        pnlWheel.add(wheel);
    }
    
    private JLabel bubble(String text) {
        JLabel b = new JLabel("<html><div style='width:230px'>" + text + "</div></html>");
        b.setOpaque(true);
        b.setBackground(new Color(0x2B3A5C));
        b.setForeground(Color.WHITE);
        b.setFont(Theme.MONO);
        b.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        b.setAlignmentX(LEFT_ALIGNMENT);
        return b;
    }
    
    private void styleControls() {
        Theme.button(btnTurnLeft, btnTurnRight, btnUnlock, btnHint);
        for (JButton b : new JButton[] { btnTurnLeft, btnTurnRight, btnUnlock, btnHint }) {
            b.setOpaque(true);
            b.setContentAreaFilled(true);
            b.setBorderPainted(true);
        }
        btnTurnLeft.setText("\u25C0");
        btnTurnRight.setText("\u25B6");
        btnTurnLeft.setFont(new Font("Dialog", Font.BOLD, 14));
        btnTurnRight.setFont(new Font("Dialog", Font.BOLD, 14));
        btnUnlock.setText("Unlock");
        btnHint.setText("Hint");

        lblInstruction.setOpaque(true);
        lblInstruction.setBackground(new Color(0x0A0F1A));
        lblInstruction.setForeground(Color.WHITE);
        lblInstruction.setFont(Theme.MONO);
        lblInstruction.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInstruction.setBorder(BorderFactory.createLineBorder(new Color(0xE0A100), 1));

        lblCoded.setOpaque(true);
        lblCoded.setBackground(SCREEN_BG);
        lblCoded.setForeground(Theme.AMBER);
        lblCoded.setFont(new Font("Consolas", Font.BOLD, 16));
        lblCoded.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        lblFeedback.setOpaque(true);
        lblFeedback.setBackground(SCREEN_BG);
        lblFeedback.setFont(Theme.MONO.deriveFont(12f));
        lblFeedback.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        // the app buttons and the arrow sit on top of the picture: invisible
        for (JButton b : new JButton[] { btnAppMessages, btnAppWifi, btnAppVoicemail,
                btnAppFacebook, btnWifiBack }) {
            b.setText("");
            b.setOpaque(false);
            b.setContentAreaFilled(false);
            b.setBorderPainted(false);
            b.setFocusPainted(false);
            b.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        }
        HoverGlow.install(btnAppMessages, btnWifiBack);
    }
   
    private void setInstruction(String text) {
        lblInstruction.setText(text);
        lblInstruction.setVisible(text != null && !text.isEmpty());
    }
        // ------------------------------------------------------------ flow
    /** Called every time this card is shown (see ScenePanel). */
    @Override
    protected void startScene() {
        glow.stop();
        turn = 0;
        hintsUsed = 0;
        solved = false;
        btnHint.setEnabled(true);
        busy = true;
        wheel.snap(0);

        lblPhone.setVisible(true);
        lblPhoneWifi.setVisible(false);
        pnlChat.setVisible(false);
        pnlWheel.setVisible(false);
        lblCoded.setVisible(false);
        lblFeedback.setVisible(false);
        btnTurnLeft.setVisible(false);
        btnTurnRight.setVisible(false);
        btnUnlock.setVisible(false);
        btnHint.setVisible(false);
        btnWifiBack.setVisible(false);
        for (JButton b : new JButton[] { btnAppMessages, btnAppWifi, btnAppVoicemail, btnAppFacebook }) {
            b.setVisible(true);
            b.setBorderPainted(false);
        }
        setInstruction(Story.L1_TAP_MESSAGES);

        GameManager.setTimerVisible(true);
        scene(Story.L1_INTRO, () -> busy = false);
    }

    private void startPuzzle() {
        pnlChat.setVisible(false);
        pnlWheel.setVisible(true);
        lblCoded.setVisible(true);
        btnTurnLeft.setVisible(true);
        btnTurnRight.setVisible(true);
        btnUnlock.setVisible(true);
        btnHint.setVisible(true);
        refreshCoded();
        setInstruction(Story.L1_PUZZLE);
        busy = false;
    }

    /** Every click moves each letter back one place: 14 clicks turn ASSH into MEET. */
    private void refreshCoded() {
        StringBuilder sb = new StringBuilder();
        for (char ch : Story.L1_CODED.toCharArray()) {
            if (ch >= 'A' && ch <= 'Z') {
                sb.append((char) ('A' + Math.floorMod(ch - 'A' - turn, 26)));
            } else {
                sb.append(ch);
            }
        }
        lblCoded.setText(sb.toString());
    }
    
    private void setTurn(int t) {
        if (busy || solved) {
            return;
        }
        turn = t;
        wheel.turnTo(turn);
        refreshCoded();
        lblFeedback.setVisible(false);
    }

    private void openWifi() {
        pnlWheel.setVisible(false);
        lblCoded.setVisible(false);
        lblFeedback.setVisible(false);
        btnTurnLeft.setVisible(false);
        btnTurnRight.setVisible(false);
        btnUnlock.setVisible(false);
        lblPhone.setVisible(false);               // the home screen is in front of the Wi-Fi screen
        lblPhoneWifi.setVisible(true);
        setInstruction("");

        int n = Story.L1_WIFI.length;
        String[][] before = java.util.Arrays.copyOfRange(Story.L1_WIFI, 0, n - 1);
        String[][] last = java.util.Arrays.copyOfRange(Story.L1_WIFI, n - 1, n);
        scene(before, () -> {
            glow.play();                          // the glow starts with the inventory line
            scene(last, () -> {
                GameState.getInstance().addItem(Story.ITEM_WIFI_LOG);
                btnWifiBack.setVisible(true);
                setInstruction(Story.L1_TAP_BACK);
                busy = false;
            });
        });
    }
        
    // ------------------------------------------------------------ the cipher wheel
    /** Two rings: the outer one is fixed, the inner one turns. */
    private class WheelView extends JComponent {
        private double anim;
        private int target;
        private final javax.swing.Timer timer = new javax.swing.Timer(16, e -> step());
        private double lastAngle;     // angle of the mouse at the last event
        private double dragged;       // how far this drag has turned, in radians
        private int startTurn;        // the turn when the drag started
        WheelView() {
            setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
            java.awt.event.MouseAdapter m = new java.awt.event.MouseAdapter() {
                @Override
                public void mousePressed(java.awt.event.MouseEvent e) {
                    lastAngle = angleOf(e);
                    dragged = 0;
                    startTurn = turn;
                }

                @Override
                public void mouseDragged(java.awt.event.MouseEvent e) {
                    double a = angleOf(e);
                    double d = a - lastAngle;
                    if (d > Math.PI) {
                        d -= 2 * Math.PI;                 // crossed the left edge going clockwise
                    }
                    if (d < -Math.PI) {
                        d += 2 * Math.PI;
                    }
                    dragged += d;
                    lastAngle = a;
                    setTurn(startTurn + (int) Math.round(dragged / (2 * Math.PI / 26)));
                }

                @Override
                public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
                    setTurn(turn + (e.getWheelRotation() > 0 ? 1 : -1));
                }
            };
            addMouseListener(m);
            addMouseMotionListener(m);
            addMouseWheelListener(m);
        }

        private double angleOf(java.awt.event.MouseEvent e) {
            return Math.atan2(e.getY() - getHeight() / 2.0, e.getX() - getWidth() / 2.0);
        }
        
        void turnTo(int t) {
            target = t;
            if (!timer.isRunning()) {
                timer.start();
            }
        }
 
        void snap(int t) {
            timer.stop();
            target = t;
            anim = t;
            repaint();
        }
 
        private void step() {
            double d = target - anim;
            if (Math.abs(d) < 0.02) {
                anim = target;
                timer.stop();
            } else {
                anim += d * 0.25;
            }
            repaint();
        }
 
        private void centered(Graphics2D g, String s, double x, double y) {
            FontMetrics fm = g.getFontMetrics();
            g.drawString(s, (int) (x - fm.stringWidth(s) / 2.0),
                    (int) (y + (fm.getAscent() - fm.getDescent()) / 2.0));
        }
        
         @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            int ro = Math.min(getWidth(), getHeight()) / 2 - 6;
            int ri = (int) (ro * 0.70);
            int rh = (int) (ri * 0.35);
 
            g.setColor(new Color(0x1B2A47));
            g.fillOval(cx - ro, cy - ro, 2 * ro, 2 * ro);
            g.setColor(Theme.AMBER);
            g.setStroke(new java.awt.BasicStroke(2f));
            g.drawOval(cx - ro, cy - ro, 2 * ro, 2 * ro);
 
            g.setColor(new Color(0x0B1220));
            g.fillOval(cx - ri, cy - ri, 2 * ri, 2 * ri);
            g.setColor(Theme.AMBER);
            g.drawOval(cx - ri, cy - ri, 2 * ri, 2 * ri);
 
            // outer ring: fixed letters
            g.setFont(new Font("Consolas", Font.BOLD, 13));
            g.setColor(Theme.TEXT);
            double rOuter = (ro + ri) / 2.0;
            for (int i = 0; i < 26; i++) {
                double a = -Math.PI / 2 + i * 2 * Math.PI / 26;
                centered(g, String.valueOf((char) ('A' + i)),
                        cx + Math.cos(a) * rOuter, cy + Math.sin(a) * rOuter);
            }
 
            // inner ring: turns with the clicks
            g.setFont(new Font("Consolas", Font.BOLD, 10));
            g.setColor(Theme.AMBER);
            double rInner = ri * 0.72;
            for (int p = 0; p < 26; p++) {
                double a = -Math.PI / 2 + (p + anim) * 2 * Math.PI / 26;
                centered(g, String.valueOf((char) ('A' + p)),
                        cx + Math.cos(a) * rInner, cy + Math.sin(a) * rInner);
            }
 
            // hub: how far the wheel is turned
            g.setColor(new Color(0x1B2A47));
            g.fillOval(cx - rh, cy - rh, 2 * rh, 2 * rh);
            g.setColor(Theme.AMBER);
            g.drawOval(cx - rh, cy - rh, 2 * rh, 2 * rh);
            g.setFont(new Font("Consolas", Font.BOLD, 16));
            centered(g, String.valueOf(Math.floorMod(target, 26)), cx, cy);
            g.dispose();
        }
    }
    
    private void buildGlow() {
        glow = new PhoneGlow();
        add(glow, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 580));
        setComponentZOrder(glow, getComponentZOrder(lblPhoneWifi));   // right in front of the Wi-Fi screen
    }

    /** A pulsing yellow glow around the edges of the phone. */
    private class PhoneGlow extends JComponent {
        // the phone body inside the 580 x 580 picture: change these if the glow sits off
        private static final int X = 366;
        private static final int Y = -12;
        private static final int W = 352;
        private static final int H = 590;
        private static final int ARC = 34;

        private final javax.swing.Timer timer = new javax.swing.Timer(16, e -> repaint());
        private long t0;
        private boolean on;

        PhoneGlow() {
            setVisible(false);
        }

        void play() {
            t0 = System.nanoTime();
            on = true;
            setVisible(true);
            timer.start();
        }

        void stop() {
            timer.stop();
            on = false;
            setVisible(false);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            if (!on) {
                return;
            }
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            double ms = (System.nanoTime() - t0) / 1e6;
            double fade = Math.min(1.0, ms / 500.0);                 // fades in
            double pulse = 0.65 + 0.35 * Math.sin(ms * 0.005);       // gentle pulsing

            g.setStroke(new java.awt.BasicStroke(2f));
            for (int i = 0; i < 16; i++) {                           // soft glow going outward
                double k = 1.0 - i / 16.0;
                int alpha = (int) (180 * fade * pulse * k * k);
                g.setColor(new Color(255, 215, 0, alpha));
                g.drawRoundRect(X - i, Y - i, W + 2 * i, H + 2 * i, ARC + 2 * i, ARC + 2 * i);
            }
            for (int i = 1; i <= 5; i++) {                           // a little glow going inward
                int alpha = (int) (110 * fade * pulse * (6 - i) / 6.0);
                g.setColor(new Color(255, 215, 0, alpha));
                g.drawRoundRect(X + i, Y + i, W - 2 * i, H - 2 * i, ARC - i, ARC - i);
            }
            g.setStroke(new java.awt.BasicStroke(3f));               // bright edge
            g.setColor(new Color(255, 236, 140, (int) (255 * fade)));
            g.drawRoundRect(X, Y, W, H, ARC, ARC);
            g.dispose();
        }
    }
     
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnHint = new javax.swing.JButton();
        lblInstruction = new javax.swing.JLabel();
        btnWifiBack = new javax.swing.JButton();
        btnTurnLeft = new javax.swing.JButton();
        btnUnlock = new javax.swing.JButton();
        btnTurnRight = new javax.swing.JButton();
        btnAppMessages = new javax.swing.JButton();
        btnAppWifi = new javax.swing.JButton();
        btnAppVoicemail = new javax.swing.JButton();
        btnAppFacebook = new javax.swing.JButton();
        lblFeedback = new javax.swing.JLabel();
        lblCoded = new javax.swing.JLabel();
        pnlWheel = new javax.swing.JPanel();
        pnlChat = new javax.swing.JPanel();
        lblPhone = new javax.swing.JLabel();
        lblPhoneWifi = new javax.swing.JLabel();
        lblBg = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1000, 580));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnHint.setText("Hint");
        btnHint.addActionListener(this::btnHintActionPerformed);
        add(btnHint, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 15, 120, 36));
        add(lblInstruction, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 12, 700, 30));

        btnWifiBack.setBorderPainted(false);
        btnWifiBack.setContentAreaFilled(false);
        btnWifiBack.addActionListener(this::btnWifiBackActionPerformed);
        add(btnWifiBack, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 50, 50, 40));

        btnTurnLeft.setText("◀");
        btnTurnLeft.addActionListener(this::btnTurnLeftActionPerformed);
        add(btnTurnLeft, new org.netbeans.lib.awtextra.AbsoluteConstraints(392, 318, 50, 32));

        btnUnlock.setText("Unlock");
        btnUnlock.addActionListener(this::btnUnlockActionPerformed);
        add(btnUnlock, new org.netbeans.lib.awtextra.AbsoluteConstraints(448, 318, 184, 32));

        btnTurnRight.setText("▶");
        btnTurnRight.addActionListener(this::btnTurnRightActionPerformed);
        add(btnTurnRight, new org.netbeans.lib.awtextra.AbsoluteConstraints(638, 318, 50, 32));

        btnAppMessages.setBorderPainted(false);
        btnAppMessages.setContentAreaFilled(false);
        btnAppMessages.addActionListener(this::btnAppMessagesActionPerformed);
        add(btnAppMessages, new org.netbeans.lib.awtextra.AbsoluteConstraints(396, 380, 66, 74));

        btnAppWifi.setBorderPainted(false);
        btnAppWifi.setContentAreaFilled(false);
        add(btnAppWifi, new org.netbeans.lib.awtextra.AbsoluteConstraints(471, 380, 58, 74));

        btnAppVoicemail.setBorderPainted(false);
        btnAppVoicemail.setContentAreaFilled(false);
        add(btnAppVoicemail, new org.netbeans.lib.awtextra.AbsoluteConstraints(543, 380, 63, 74));

        btnAppFacebook.setBorderPainted(false);
        btnAppFacebook.setContentAreaFilled(false);
        add(btnAppFacebook, new org.netbeans.lib.awtextra.AbsoluteConstraints(615, 380, 61, 74));
        add(lblFeedback, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 292, 300, 22));
        add(lblCoded, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 264, 300, 26));

        javax.swing.GroupLayout pnlWheelLayout = new javax.swing.GroupLayout(pnlWheel);
        pnlWheel.setLayout(pnlWheelLayout);
        pnlWheelLayout.setHorizontalGroup(
            pnlWheelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        pnlWheelLayout.setVerticalGroup(
            pnlWheelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 230, Short.MAX_VALUE)
        );

        add(pnlWheel, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 30, 300, 230));

        pnlChat.setOpaque(false);

        javax.swing.GroupLayout pnlChatLayout = new javax.swing.GroupLayout(pnlChat);
        pnlChat.setLayout(pnlChatLayout);
        pnlChatLayout.setHorizontalGroup(
            pnlChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        pnlChatLayout.setVerticalGroup(
            pnlChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 345, Short.MAX_VALUE)
        );

        add(pnlChat, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 50, 300, 345));

        lblPhone.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/alli_homescreen_580.png"))); // NOI18N
        add(lblPhone, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 0, 580, 580));

        lblPhoneWifi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/alli_wifi_580.png"))); // NOI18N
        add(lblPhoneWifi, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 0, 580, 580));

        lblBg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/room_classroom.png"))); // NOI18N
        add(lblBg, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 600));
    }// </editor-fold>//GEN-END:initComponents

    private void btnAppMessagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAppMessagesActionPerformed
        if (busy) {
            return;
        }
        busy = true;
        for (JButton b : new JButton[] { btnAppMessages, btnAppWifi, btnAppVoicemail, btnAppFacebook }) {
            b.setVisible(false);
        }
        pnlChat.setVisible(true);
        setInstruction("");
        scene(Story.L1_AMIE, this::startPuzzle);
    }//GEN-LAST:event_btnAppMessagesActionPerformed

    private void btnTurnLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTurnLeftActionPerformed
        setTurn(turn - 1);
    }//GEN-LAST:event_btnTurnLeftActionPerformed

    private void btnTurnRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTurnRightActionPerformed
        setTurn(turn + 1);
    }//GEN-LAST:event_btnTurnRightActionPerformed

    private void btnUnlockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUnlockActionPerformed
        if (busy) { return; }
        if (busy || solved) {
            return;
        }
        if (Math.floorMod(turn, 26) == Story.CAESAR_SHIFT) {
            solved = true;
            busy = true;
            ScoreManager.onCorrect();                 // +100 and the speed bonus
            showFeedback(lblFeedback, Story.L1_RIGHT, true);
            btnHint.setVisible(false);
            scene(Story.L1_SOLVED, this::openWifi);
        } else {
            ScoreManager.onWrong();                   // -10 points and time
            showFeedback(lblFeedback, Story.L1_WRONG, false);
        }
    }//GEN-LAST:event_btnUnlockActionPerformed

    private void btnHintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHintActionPerformed
        if (busy || solved || hintsUsed >= MAX_HINTS) {
            return;
        }
        ScoreManager.onHint();                        // -5 points and time
        int idx = hintsUsed++;
        if (hintsUsed >= MAX_HINTS) {
            btnHint.setEnabled(false);
        }
        busy = true;
        scene(new String[][] {
            { "Arnie (thinking)", Story.L1_HINT_INTRO },
            { "Arnie (thinking)", Story.L1_HINTS[idx] }
        }, () -> busy = false);
    }//GEN-LAST:event_btnHintActionPerformed

    private void btnWifiBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWifiBackActionPerformed
        if (busy) {
            return;
        }
        busy = true;
        glow.stop();
        btnWifiBack.setVisible(false);
        scene(Story.L1_OUT, LevelManager::nextLevel);
    }//GEN-LAST:event_btnWifiBackActionPerformed
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAppFacebook;
    private javax.swing.JButton btnAppMessages;
    private javax.swing.JButton btnAppVoicemail;
    private javax.swing.JButton btnAppWifi;
    private javax.swing.JButton btnHint;
    private javax.swing.JButton btnTurnLeft;
    private javax.swing.JButton btnTurnRight;
    private javax.swing.JButton btnUnlock;
    private javax.swing.JButton btnWifiBack;
    private javax.swing.JLabel lblBg;
    private javax.swing.JLabel lblCoded;
    private javax.swing.JLabel lblFeedback;
    private javax.swing.JLabel lblInstruction;
    private javax.swing.JLabel lblPhone;
    private javax.swing.JLabel lblPhoneWifi;
    private javax.swing.JPanel pnlChat;
    private javax.swing.JPanel pnlWheel;
    // End of variables declaration//GEN-END:variables
}
