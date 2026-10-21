package kldcrimedecoder.levels;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import kldcrimedecoder.data.Story;
import kldcrimedecoder.engine.*;

/**
 * Prologue: 0A bedroom (click to collect), 0B classroom, 0C name prompt.
 * The components are made in the Design tab (see Level0Prologue.form).
 */
public class Level0Prologue extends ScenePanel {

    private static final Color BEDROOM_BG = new Color(0x2A2438);
    private static final Color CLASSROOM_BG = new Color(0x2E3B2E);
    private static final Color PHONE_BG = new Color(0x10151F);
    private static final int DANEL_X = 275, DANEL_Y = 130;     // center, bottom of screen
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

    /** Fonts and colors (positions and texts come from the Design tab). */
    private void styleComponents() {
        lblTutorial.setText(Story.PROLOGUE_TUTORIAL);
        lblTutorial.setFont(new Font("Consolas", Font.BOLD, 16));
        lblTutorial.setForeground(new Color(255, 230, 120));
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

        for (JButton b : hotspots) {
            makeInvisibleHotspot(b);
        }
    }

    // =====================================================
    // SCENE 0A: Arnie's bedroom (click to collect)
    // =====================================================
    @Override
    public void startScene() {                      // overrides ScenePanel.startScene()
        collectedCount = 0;
        hideCharacter();
        hideOverlay();
        setScene("room_bedroom", BEDROOM_BG);
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
            showOverlay(overlayName);               // e.g. the text from Alli's mom
        }

        Runnable afterLines = this::afterCollect;
        if (lines == null) {
            afterLines.run();
        } else {
            play(lines, afterLines);
        }
    }

    /** Phone: lock screen shows Alli's mom's text, then the opened thread shows Arnie's thought. */
    private void collectPhone() {
        if (busy) {
            return;
        }
        busy = true;
        btnPhone.setVisible(false);
        GameState.getInstance().addItem(Story.ITEM_ARNIE_PHONE);
        collectedCount++;
        showOverlay("arnie_phone");                                   // lock screen: text from Alli's mom
        play(new String[][]{Story.PROLOGUE_PHONE[0]}, () -> {
            showOverlay("arnie_msgs");                                // thread opened: Arnie thinks
            play(new String[][]{Story.PROLOGUE_PHONE[1]}, this::afterCollect);
        });
    }

    /** Runs after any item's dialogue; starts Scene 0B when all three are collected. */
    private void afterCollect() {
        hideOverlay();
        busy = false;
        if (collectedCount == TOTAL_ITEMS) {
            busy = true;                            // no clicks during the ending lines
            play(Story.PROLOGUE_0A_END, this::startScene0B);
        }
    }

    // =====================================================
    // SCENE 0B: Classroom (Prof. Danel: neutral, confused, neutral)
    // =====================================================
    private void startScene0B() {
        lblTutorial.setVisible(false);
        setScene("room_classroom", CLASSROOM_BG);
        showDanel("neutral");
        play(Story.PROLOGUE_0B_A, () -> {
            showDanel("confused");
            play(Story.PROLOGUE_0B_B, () -> {
                showDanel("neutral");
                play(Story.PROLOGUE_0B_C, () -> {
                    hideCharacter();
                    GameState.getInstance().addItem(Story.ITEM_ALLI_PHONE, Story.ITEM_BRASS_KEY);
                    startScene0C();
                });
            });
        });
    }

    /** Uses sir_danel_<emotion>.png if it exists, otherwise the single sir_danel.png. */
    private void showDanel(String emotion) {
        String file = "sir_danel_" + emotion;
        if (getClass().getResource("/kldcrimedecoder/images/" + file + ".png") == null) {
            file = "sir_danel";
        }
        showCharacter(file, DANEL_X, DANEL_Y);
    }

    // =====================================================
    // SCENE 0C: Name prompt (lock screen)
    // =====================================================
    private void startScene0C() {
        busy = false;
        hideCharacter();
        setScene("alli_lockscreen", PHONE_BG, true);
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
        setScene("alli_phone", PHONE_BG, true);              // the phone opens
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

    /** Makes a button see-through, with a yellow outline when the mouse is over it. */
    private void makeInvisibleHotspot(JButton b) {
        b.setText("");
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createLineBorder(new Color(255, 230, 120), 2));
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

    // ===== TEMPORARY TEST CODE (Joel's): DELETE IN PHASE 5 =====
   
    // ===== END TEMPORARY TEST CODE =====

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTutorial = new javax.swing.JLabel();
        btnPhone = new javax.swing.JButton();
        btnPhoto = new javax.swing.JButton();
        btnBag = new javax.swing.JButton();
        lblNamePrompt = new javax.swing.JLabel();
        txtNickname = new javax.swing.JTextField();
        btnUnlock = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(1000, 580));
        setLayout(null);

        lblTutorial.setText("Tutorial");
        add(lblTutorial);
        lblTutorial.setBounds(150, 50, 700, 30);

        btnPhone.setText("Phone");
        add(btnPhone);
        btnPhone.setBounds(745, 303, 100, 48);

        btnPhoto.setText("Photo");
        add(btnPhoto);
        btnPhoto.setBounds(808, 203, 36, 55);

        btnBag.setText("Bag");
        add(btnBag);
        btnBag.setBounds(805, 394, 135, 150);

        lblNamePrompt.setText("Prompt");
        add(lblNamePrompt);
        lblNamePrompt.setBounds(210, 48, 580, 36);

        txtNickname.setText("Nickname");
        add(txtNickname);
        txtNickname.setBounds(332, 258, 258, 32);

        btnUnlock.setText("Unlock");
        add(btnUnlock);
        btnUnlock.setBounds(420, 296, 160, 28);
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