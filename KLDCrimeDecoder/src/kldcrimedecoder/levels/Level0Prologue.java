package kldcrimedecoder.levels;
 
import java.awt.BasicStroke;
import kldcrimedecoder.ui.DialogueBox;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import kldcrimedecoder.data.Story;
import kldcrimedecoder.engine.*;
 
/**
 * Prologue: 0A bedroom (click to collect), 0B classroom, 0C name prompt.
 * Buttons, labels and the text field are made in the Design tab
 * (see Level0Prologue.form). The BACKGROUND is drawn by ScenePanel, so there
 * is NO background label in the form.
 *
 * Background changes (script order):
 *   room_bedroom -> room_classroom -> alli_phone (password) -> alli_lockscreen (opened)
 */
public class Level0Prologue extends ScenePanel {
 
    private static final Color BEDROOM_BG = new Color(0x2A2438);
    private static final Color CLASSROOM_BG = new Color(0x2E3B2E);
    private static final Color PHONE_BG = new Color(0x10151F);
    private static final int DANEL_HEIGHT = 450;           // medium size (was 450)
    private static final int DANEL_X = 60;                 // left side
    private static final int DANEL_OVERLAP = 4;            // how far she sinks behind the text box
    private static final int MAX_NAME = GameState.MAX_NICKNAME;
    private static final int TOTAL_ITEMS = 3;                  // phone, photo, bag
 
    private final JButton[] hotspots;           // phone, photo, bag (made in the Design tab)
    private int collectedCount;
    private boolean busy;                       // true while any dialogue plays
 
    public Level0Prologue() {
        initComponents();
        hotspots = new JButton[]{btnPhone, btnPhoto, btnBag};
        styleComponents();
 
        btnPhone.addActionListener(e -> collectPhone());
        btnPhoto.addActionListener(e -> collect(btnPhoto, Story.PROLOGUE_PHOTO, Story.ITEM_TORN_PHOTO, null));
        btnBag.addActionListener(e -> collect(btnBag, null, null, null));   // collected silently
 
        limitNickname();
        txtNickname.addActionListener(e -> unlockPhone());   // Enter also unlocks
        btnUnlock.addActionListener(e -> unlockPhone());
 
        showNamePrompt(false);
        setScene("room_bedroom", BEDROOM_BG);               // visible even before the first show event
 
        // Restart the prologue every time this screen is shown
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                startScene();
            }
        });
    }
 
    /** Fonts and colors only. Positions and sizes come from the Design tab. */
    private void styleComponents() {
        lblTutorial.setText(Story.PROLOGUE_TUTORIAL);
        lblTutorial.setFont(new Font("Consolas", Font.BOLD, 15));
        lblTutorial.setForeground(new Color(255, 230, 120));
        lblTutorial.setBackground(new Color(0x0A0F1A));         // dark strip, readable on any picture
        lblTutorial.setOpaque(true);
        lblTutorial.setBorder(BorderFactory.createLineBorder(new Color(255, 200, 60), 2));
        lblTutorial.setHorizontalAlignment(SwingConstants.CENTER);
 
        lblNamePrompt.setText("");
        lblNamePrompt.setFont(new Font("Consolas", Font.BOLD, 18));
        lblNamePrompt.setForeground(Color.WHITE);
        lblNamePrompt.setBackground(PHONE_BG);
        lblNamePrompt.setOpaque(true);
        lblNamePrompt.setHorizontalAlignment(SwingConstants.CENTER);
 
        txtNickname.setText("");
        txtNickname.setFont(new Font("Consolas", Font.BOLD, 20));
        txtNickname.setHorizontalAlignment(SwingConstants.CENTER);
 
        makeInvisibleHotspot(btnPhone, -12, 86, 32);   // tilted outline for the phone
        makeInvisibleHotspot(btnPhoto);
        makeInvisibleHotspot(btnBag);
    }
 
    // =====================================================
    // SCENE 0A: Arnie's bedroom (click to collect)
    // =====================================================
    @Override
    public void startScene() {                      // overrides ScenePanel.startScene()
        collectedCount = 0;
        hideCharacter();
        hideOverlay();
        GameManager.pauseTimer();                                              // no timer in the bedroom
        GameState.getInstance().setTimeLeftSeconds(GameState.START_SECONDS);   // back to 15:00 if replayed
        GameManager.refreshTopBar();                                           // top bar shows 15:00
        setScene("room_bedroom", BEDROOM_BG);       // BACKGROUND 1: bedroom
        for (JButton b : hotspots) {
            b.setVisible(true);
        }
        lblTutorial.setVisible(true);
        showNamePrompt(false);
        busy = true;                                // block clicks while the intro plays
        play(Story.PROLOGUE_0A_INTRO, () -> busy = false);
    }
 
    /** Collects one item. lines can be null (silent). overlayName can be null. */
    private void collect(JButton button, String[][] lines, String item, String overlayName) {
        if (busy) {
            return;                                 // wait for the dialogue to finish
        }
        busy = true;
        button.setVisible(false);                   // collected, so it disappears
        if (item != null) {
            GameState.getInstance().addItem(item);
        }
        collectedCount++;
        if (overlayName != null) {
            showOverlay(overlayName);
        }
 
        if (lines == null) {
            afterCollect();
        } else {
            play(lines, this::afterCollect);
        }
    }
 
    /**
     * Phone sequence:
     * 1) lock screen picture (arnie_phone), name tag "Arnie", text "Alli's Mom?";
     * 2) after a click, the opened thread (arnie_msgs) shows mom's message drawn
     *    on the phone, and Arnie's thought plays in the text bar.
     */
    private void collectPhone() {
        if (busy) {
            return;
        }
        busy = true;
        btnPhone.setVisible(false);
        GameState.getInstance().addItem(Story.ITEM_ARNIE_PHONE);
        collectedCount++;
        showOverlay("arnie_phone");                                   // lock screen
        play(Story.PROLOGUE_PHONE_NOTIF, () -> {                      // click to continue
            showOverlay("arnie_msgs");                                // opened thread
            showOverlayText(Story.PROLOGUE_PHONE[0][1]);              // mom's message on the phone
            play(new String[][]{Story.PROLOGUE_PHONE[1]}, this::afterCollect);
        });
    }
 
    /** Runs after any item's dialogue; starts Scene 0B when all three are collected. */
    private void afterCollect() {
        hideOverlay();
        busy = false;
        if (collectedCount == TOTAL_ITEMS) {
            busy = true;                            // no clicks during the ending lines
            for (JButton b : hotspots) {
                b.setVisible(false);
            }
            play(Story.PROLOGUE_0A_END, this::startScene0B);
        }
    }
 
    // =====================================================
    // SCENE 0B: Classroom (Prof. Danel: neutral, confused, neutral)
    // She is shown on the left, only on her own lines.
    // =====================================================
    private void startScene0B() {
        lblTutorial.setVisible(false);
        GameManager.startTimer();                   // the timer starts in the classroom
        setScene("room_classroom", CLASSROOM_BG);   // BACKGROUND 2: classroom
        setCharHeight(DANEL_HEIGHT);                // uses the ScenePanel setter (encapsulation)
        hideCharacter();                            // she appears only when she speaks
        playDanelScene(Story.PROLOGUE_0B_A, "neutral", () ->
            playDanelScene(Story.PROLOGUE_0B_B, "confused", () ->
                playDanelScene(Story.PROLOGUE_0B_C, "neutral", () -> {
                    hideCharacter();
                    GameState.getInstance().addItem(Story.ITEM_ALLI_PHONE, Story.ITEM_BRASS_KEY);
                    startScene0C();
                })));
    }
 
    /**
     * Plays a dialogue one line at a time. Prof. Danel is shown (with this
     * emotion) only on her own lines; on Arnie's and the narrator's lines she is hidden.
     */
    private void playDanelScene(String[][] lines, String emotion, Runnable after) {
        playLine(lines, 0, emotion, after);
    }
 
    private void playLine(String[][] lines, int i, String emotion, Runnable after) {
        if (i >= lines.length) {
            after.run();
            return;
        }
        String[] line = lines[i];
        if (line[0].startsWith("Prof. Danel")) {
            showDanel(emotion);
        } else {
            hideCharacter();
        }
        play(new String[][]{line}, () -> playLine(lines, i + 1, emotion, after));
    }
 
    /** Uses danel_<emotion>.png (danel_neutral, danel_confused). Falls back to danel_neutral. */
        /** Uses danel_<emotion>.png (danel_neutral, danel_confused). Falls back to danel_neutral. */
    private void showDanel(String emotion) {
        String file = "danel_" + emotion;
        if (getClass().getResource("/kldcrimedecoder/images/" + file + ".png") == null) {
            file = "danel_neutral";
        }
        int boxTop = getHeight() - DialogueBox.BOX_HEIGHT;       // top edge of the text box
        int y = boxTop - DANEL_HEIGHT + DANEL_OVERLAP;           // her bottom sits on the box
        showCharacter(file, DANEL_X, y);
    }
 
    // =====================================================
    // SCENE 0C: Name prompt (Alli's phone)
    // =====================================================
    private void startScene0C() {
        busy = false;
        hideCharacter();
        setScene("alli_lockscreen", PHONE_BG, true);          // BACKGROUND 3: password screen (keeps picture ratio)
        lblNamePrompt.setText(Story.PROLOGUE_0C_PROMPT);
        txtNickname.setText("");
        showNamePrompt(true);
        txtNickname.requestFocusInWindow();
    }
 
    private void unlockPhone() {
        if (busy) {
            return;                                          // ignore double Enter / double click
        }
        busy = true;
        GameState.getInstance().setNickname(txtNickname.getText().trim()); // GameState cleans it; blank -> ARNIE
        showNamePrompt(false);
        setScene("alli_phone", PHONE_BG, true);         // the phone opens (9:02 + notification)
        play(Story.PROLOGUE_0C_OPEN, LevelManager::nextLevel);   // go to Level 1
    }
 
    // =====================================================
    // Helpers
    // =====================================================
    private void showNamePrompt(boolean show) {
        lblNamePrompt.setVisible(show);
        txtNickname.setVisible(show);
        btnUnlock.setVisible(show);
    }
 
    /** Straight hotspot (outline fills the button). */
    private void makeInvisibleHotspot(JButton b) {
        makeInvisibleHotspot(b, 0, 0, 0);
    }
 
    /** Hotspot with a tilted outline, e.g. for the phone lying on the desk. */
    private void makeInvisibleHotspot(JButton b, double degrees, int boxW, int boxH) {
        b.setText("");
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new HotspotBorder(degrees, boxW, boxH));
        b.setBorderPainted(false);
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setBorderPainted(true);
            }
 
            @Override
            public void mouseExited(MouseEvent e) {
                b.setBorderPainted(false);
            }
        });
    }
 
    /** Yellow outline that can be rotated (INHERITANCE: extends AbstractBorder). */
    private static class HotspotBorder extends AbstractBorder {
        private final double angle;
        private final int boxW, boxH;
 
        HotspotBorder(double degrees, int boxW, int boxH) {
            this.angle = Math.toRadians(degrees);
            this.boxW = boxW;
            this.boxH = boxH;
        }
 
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 230, 120));
            g2.setStroke(new BasicStroke(2));
            int bw = (boxW > 0) ? boxW : w - 2;
            int bh = (boxH > 0) ? boxH : h - 2;
            g2.rotate(angle, x + w / 2.0, y + h / 2.0);
            g2.drawRect(x + (w - bw) / 2, y + (h - bh) / 2, bw, bh);
            g2.dispose();
        }
    }
 
    /** Nickname: letters only, up to 8 (script). */
    private void limitNickname() {
        ((AbstractDocument) txtNickname.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                    throws BadLocationException {
                replace(fb, offset, 0, text, attr);
            }
 
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String t = (text == null) ? "" : text;
                int newLength = fb.getDocument().getLength() - length + t.length();
                if (t.matches("[A-Za-z]*") && newLength <= MAX_NAME) {
                    super.replace(fb, offset, length, t, attrs);
                }
            }
        });
    }


    // >>> KEEP the NetBeans-generated initComponents() and the
    // >>> "Variables declaration" block that stay at the bottom of your file.
    // >>> Do not type them yourself; the Design tab writes them.
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTutorial = new javax.swing.JLabel();
        btnPhone = new javax.swing.JButton();
        btnPhoto = new javax.swing.JButton();
        btnBag = new javax.swing.JButton();
        txtNickname = new javax.swing.JTextField();
        lblNamePrompt = new javax.swing.JLabel();
        btnUnlock = new javax.swing.JButton();

        setLayout(null);

        lblTutorial.setText("jLabel1");
        add(lblTutorial);
        lblTutorial.setBounds(170, 10, 660, 30);

        btnPhone.setText("jButton1");
        btnPhone.setAlignmentX(755.0F);
        btnPhone.setAlignmentY(325.0F);
        add(btnPhone);
        btnPhone.setBounds(734, 297, 100, 56);

        btnPhoto.setText("jButton1");
        btnPhoto.setAlignmentX(872.0F);
        btnPhoto.setAlignmentY(305.0F);
        add(btnPhoto);
        btnPhoto.setBounds(802, 207, 22, 40);

        btnBag.setText("jButton1");
        btnBag.setAlignmentX(805.0F);
        btnBag.setAlignmentY(410.0F);
        add(btnBag);
        btnBag.setBounds(807, 390, 126, 149);

        txtNickname.setText("jTextField1");
        add(txtNickname);
        txtNickname.setBounds(320, 250, 262, 40);

        lblNamePrompt.setText("jLabel1");
        add(lblNamePrompt);
        lblNamePrompt.setBounds(250, 20, 500, 36);

        btnUnlock.setText("Unlock");
        add(btnUnlock);
        btnUnlock.setBounds(380, 305, 160, 40);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBag;
    private javax.swing.JButton btnPhone;
    private javax.swing.JButton btnPhoto;
    private javax.swing.JButton btnUnlock;
    private javax.swing.JLabel lblNamePrompt;
    private javax.swing.JLabel lblTutorial;
    private javax.swing.JTextField txtNickname;
    // End of variables declaration//GEN-END:variables
}