package kldcrimedecoder.levels;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import kldcrimedecoder.data.Story;
import kldcrimedecoder.engine.*;

/**
 * Prologue: 0A bedroom (click to collect), 0B classroom, 0C name prompt.
 * Follows the group script, version 3.
 */
public class Level0Prologue extends javax.swing.JPanel {

    private static final String IMG_PATH = "/kldcrimedecoder/images/";

    private Image background;
    private Color fallbackColor = Color.BLACK;
    private int collectedCount;
    private boolean busy;                       // true while a collect dialogue plays

    // character: emotion picture on the bottom, hair on top
    private Image charBody, charHair;
    private int charX, charY;

    // Scene 0A
    private JLabel lblTutorial;
    private JButton btnPhone, btnPhoto, btnBag;

    // Scene 0C
    private JLabel lblNamePrompt;
    private JTextField txtNickname;
    private JButton btnUnlock;

    public Level0Prologue() {
        setLayout(null);                                   // exact positions
        setPreferredSize(new Dimension(1000, 580));

        buildHotspots();
        buildNamePrompt();
        buildTestBar();

        // Restart the prologue every time this screen is shown
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                startScene0A();
            }
        });
    }

    // =====================================================
    // SCENE 0A: Arnie's bedroom (click to collect)
    // =====================================================
    private void startScene0A() {
        collectedCount = 0;
        busy = false;
        hideCharacter();
        setScene("room_bedroom", new Color(0x2A2438));
        for (JButton b : new JButton[]{btnPhone, btnPhoto, btnBag}) {
            b.setVisible(true);
        }
        lblTutorial.setVisible(true);
        showNamePrompt(false);
        play(Story.PROLOGUE_0A_INTRO, null);
    }

    /** Collects one item. lines can be null (collected silently). */
    private void collect(JButton button, String[][] lines, String item) {
        if (busy) {
            return;                              // wait for the dialogue to finish
        }
        busy = true;
        button.setVisible(false);                // collected, so it disappears
        if (item != null) {
            GameState.getInstance().addItem(item);
        }
        collectedCount++;

        Runnable afterLines = () -> {
            busy = false;
            if (collectedCount == 3) {
                play(Story.PROLOGUE_0A_END, this::startScene0B);
            }
        };
        if (lines == null) {
            afterLines.run();
        } else {
            play(lines, afterLines);
        }
    }

    // =====================================================
    // SCENE 0B: Classroom (Prof. Danel: neutral, confused, neutral)
    // =====================================================
    private void startScene0B() {
        lblTutorial.setVisible(false);
        setScene("room_classroom", new Color(0x2E3B2E));
        showCharacter("neutral", "hair_guy", 330, 90);
        play(Story.PROLOGUE_0B_A, () -> {
            showCharacter("confused", "hair_guy", 330, 90);
            play(Story.PROLOGUE_0B_B, () -> {
                showCharacter("neutral", "hair_guy", 330, 90);
                play(Story.PROLOGUE_0B_C, () -> {
                    hideCharacter();
                    GameState.getInstance().addItem(Story.ITEM_ALLI_PHONE);
                    GameState.getInstance().addItem(Story.ITEM_BRASS_KEY);
                    startScene0C();
                });
            });
        });
    }

    // =====================================================
    // SCENE 0C: Name prompt (lock screen)
    // =====================================================
    private void startScene0C() {
        hideCharacter();
        setScene("ui_phone", new Color(0x10151F));
        lblNamePrompt.setText(Story.PROLOGUE_0C_PROMPT);
        txtNickname.setText("");
        showNamePrompt(true);
        txtNickname.requestFocusInWindow();
    }

    private void unlockPhone() {
        String name = txtNickname.getText().trim().toUpperCase();
        if (name.isEmpty()) {
            name = "ARNIE";                                  // default
        }
        GameState.getInstance().setNickname(name);           // stored for Level 10
        showNamePrompt(false);
        play(Story.PROLOGUE_0C_OPEN, LevelManager::nextLevel);   // go to Level 1
    }

    // =====================================================
    // Helpers
    // =====================================================
    /** Plays dialogue. If there is no DialogueBox (test), just continues. */
    private void play(String[][] lines, Runnable after) {
        if (GameManager.getDialogue() != null) {
            GameManager.getDialogue().play(lines, after);
        } else if (after != null) {
            after.run();
        }
    }

    private Image loadImage(String name) {
        java.net.URL url = getClass().getResource(IMG_PATH + name + ".png");
        return (url == null) ? null : new ImageIcon(url).getImage();
    }

    private void setScene(String imageName, Color fallback) {
        background = loadImage(imageName);
        fallbackColor = fallback;
        repaint();
    }

    private void showCharacter(String emotion, String hair, int x, int y) {
        charBody = loadImage(emotion);
        charHair = loadImage(hair);
        charX = x;
        charY = y;
        repaint();
    }

    private void hideCharacter() {
        charBody = null;
        charHair = null;
        repaint();
    }

    /** Draws a character image scaled to a fixed height. */
    private void drawChar(Graphics g, Image img) {
        if (img == null || img.getHeight(this) <= 0) {
            return;
        }
        int h = 450;                                         // change to resize
        int w = img.getWidth(this) * h / img.getHeight(this);
        g.drawImage(img, charX, charY, w, h, this);
    }

    private void showNamePrompt(boolean show) {
        lblNamePrompt.setVisible(show);
        txtNickname.setVisible(show);
        btnUnlock.setVisible(show);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(fallbackColor);          // shown until the real image exists
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        drawChar(g, charBody);                   // emotion first (bottom)
        drawChar(g, charHair);                   // hair on top
    }

    // =====================================================
    // Buttons and widgets
    // =====================================================
    private void buildHotspots() {
        lblTutorial = new JLabel(Story.PROLOGUE_TUTORIAL, SwingConstants.CENTER);
        lblTutorial.setFont(new Font("Consolas", Font.BOLD, 16));
        lblTutorial.setForeground(new Color(255, 230, 120));
        lblTutorial.setBounds(150, 50, 700, 30);
        add(lblTutorial);

        btnPhone = makeHotspot("Phone", 120, 160);
        btnPhoto = makeHotspot("Torn photo", 430, 160);
        btnBag   = makeHotspot("Bag", 760, 160);

        // phone: Alli's mom's text. photo: the torn photo. bag: collected silently.
        btnPhone.addActionListener(e -> collect(btnPhone, Story.PROLOGUE_PHONE, Story.ITEM_ARNIE_PHONE));
        btnPhoto.addActionListener(e -> collect(btnPhoto, Story.PROLOGUE_PHOTO, Story.ITEM_TORN_PHOTO));
        btnBag.addActionListener(e -> collect(btnBag, null, null));

        // Use pictures if the files exist (change the names to match yours)
        setImageButton(btnPhone, "item_phone", 120, 120);
        setImageButton(btnPhoto, "item_photo", 120, 120);
        setImageButton(btnBag,   "item_bag",   160, 160);
    }

    private JButton makeHotspot(String text, int x, int y) {
        JButton b = new JButton(text);
        b.setBounds(x, y, 150, 70);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(b);
        return b;
    }

    /** Swaps the plain button for a picture, if the image file exists. */
    private void setImageButton(JButton b, String imageName, int w, int h) {
        Image img = loadImage(imageName);
        if (img == null) {
            return;                              // keeps the text if the image is missing
        }
        b.setIcon(new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH)));
        b.setText("");
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setSize(w, h);
    }

    private void buildNamePrompt() {
        lblNamePrompt = new JLabel("", SwingConstants.CENTER);
        lblNamePrompt.setFont(new Font("Consolas", Font.BOLD, 20));
        lblNamePrompt.setForeground(Color.WHITE);
        lblNamePrompt.setBounds(250, 140, 500, 36);
        add(lblNamePrompt);

        txtNickname = new JTextField();                      // empty: the player types
        txtNickname.setFont(new Font("Consolas", Font.BOLD, 22));
        txtNickname.setHorizontalAlignment(SwingConstants.CENTER);
        txtNickname.setBounds(350, 190, 300, 42);
        txtNickname.addActionListener(e -> unlockPhone());   // Enter key also unlocks

        // up to 8 characters
        ((AbstractDocument) txtNickname.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                    throws BadLocationException {
                replace(fb, offset, 0, text, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                int newLength = fb.getDocument().getLength() - length + (text == null ? 0 : text.length());
                if (newLength <= 8) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        add(txtNickname);

        btnUnlock = new JButton("Unlock");
        btnUnlock.setBounds(420, 245, 160, 40);
        btnUnlock.addActionListener(e -> unlockPhone());
        add(btnUnlock);
    }

    // ===== TEMPORARY TEST CODE (Joel's): DELETE IN PHASE 5 =====
    private void buildTestBar() {
        JPanel bar = new JPanel(new FlowLayout());
        bar.setOpaque(false);
        bar.setBounds(0, 0, 1000, 40);
        JButton bOk    = new JButton("Correct");
        JButton bWrong = new JButton("Wrong");
        JButton bHint  = new JButton("Hint");
        JButton bDeny  = new JButton("Denial +60");
        JButton bNext  = new JButton("Next level");
        JButton bEnd   = new JButton("Finish game");
        JButton bDrain = new JButton("Drain 880s");
        bOk.addActionListener(e -> ScoreManager.onCorrect());
        bWrong.addActionListener(e -> ScoreManager.onWrong());
        bHint.addActionListener(e -> ScoreManager.onHint());
        bDeny.addActionListener(e -> GameState.getInstance().addDenial(60));
        bNext.addActionListener(e -> LevelManager.nextLevel());
        bEnd.addActionListener(e -> GameManager.finishGame());
        bDrain.addActionListener(e -> TimerManager.drain(880));
        bar.add(bOk);
        bar.add(bWrong);
        bar.add(bHint);
        bar.add(bDeny);
        bar.add(bNext);
        bar.add(bEnd);
        bar.add(bDrain);
        add(bar);
    }
    // ===== END TEMPORARY TEST CODE =====
}