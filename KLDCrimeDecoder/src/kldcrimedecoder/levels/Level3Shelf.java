/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package kldcrimedecoder.levels;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import kldcrimedecoder.data.Story;
import kldcrimedecoder.engine.GameManager;
import kldcrimedecoder.engine.GameState;
import kldcrimedecoder.engine.LevelManager;
import kldcrimedecoder.engine.ScoreManager;
import kldcrimedecoder.puzzles.WordScramblePuzzle;
import kldcrimedecoder.ui.DialogueBox;
import kldcrimedecoder.ui.DragDropHelper;
import kldcrimedecoder.ui.Theme;
import java.awt.Graphics;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import javax.swing.JComponent;

/**
 *
 * @author Joel
 */
public class Level3Shelf extends javax.swing.JPanel {
private static final Color BAD = new Color(0xFF6B6B);        // wrong answer (dark bars)
private static final Color PAPER_BAD = new Color(0xB00020);  // wrong answer (on the paper)
private static final Color INK = new Color(0x2B2B2B);        // dark text on the paper
private static final String KEY_SOLVED = "level3.tilesSolved";
private static final String KEY_INTRO  = "level3.introSeen";
private static final int POP_MS = 900;       // how long the pop-out takes
private static final double POP_X = 500;     // where the journal ends up (its center)
private static final double POP_Y = 175;     // above the dialogue band, which starts at y 340

private JournalPop journalPop;               // the see-through layer that draws the journal
private Timer popTimer;
private long popStart;

private WordScramblePuzzle puzzle;
private boolean solved;
private int hintsUsed;
private JPanel[] slots;          // one slot for each letter

    /**
     * Creates new form Level3Shelf
     */
   public Level3Shelf() {
    initComponents();

    // ---- theme for the dark side bars ----
    Theme.background(this);
    Theme.label(lblFeedback);
    Theme.button(btnBack, btnContinue, btnHint, btnClosePaper);

    // ---- the paper is light, so it uses dark ink ----
    Font small = new Font("Consolas", Font.PLAIN, 13);
    pnlPaper.setOpaque(false);
    lblPaperPrompt.setFont(small);
    lblPaperPrompt.setForeground(INK);
    lblPaperFeedback.setFont(small);
    pnlSlots.setOpaque(false);
    pnlTiles.setOpaque(false);

    // ---- slots (the two panels are empty in the Design tab) ----
    int n = Story.L3_TILES.length();
    pnlSlots.setLayout(new GridLayout(1, n, 3, 0));
    pnlTiles.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));
    slots = new JPanel[n];
    for (int i = 0; i < n; i++) {
        JPanel slot = new JPanel(new BorderLayout());
        slot.setBackground(new Color(0xEDE7D6));
        slot.setBorder(BorderFactory.createLineBorder(INK, 1));
        slots[i] = slot;
        pnlSlots.add(slot);
        // a slot takes a tile only when it is empty
        DragDropHelper.makeDropTarget(slot, (label, target) -> slotAccepts(target));
    }
    // the tray takes tiles back, and clears the old message
    DragDropHelper.makeDropTarget(pnlTiles, (label, target) -> {
    setPaperFeedback("", true);
    return true;
    });

    // ---- the book that sticks out ----
    btnSpine.setText("");
    btnSpine.setIcon(makeSpineIcon(26, 70));
    btnSpine.setMargin(new java.awt.Insets(0, 0, 0, 0));
    btnSpine.setOpaque(false);
    btnSpine.setContentAreaFilled(false);
    btnSpine.setBorderPainted(false);
    btnSpine.setFocusPainted(false);
    btnSpine.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btnSpine.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            btnSpine.setBorder(BorderFactory.createLineBorder(Theme.AMBER, 2));
            btnSpine.setBorderPainted(true);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            btnSpine.setBorderPainted(false);
        }
    });

    // Every time this card is shown, rebuild it from the saved progress
    addComponentListener(new ComponentAdapter() {
    @Override
    public void componentShown(ComponentEvent e) {
        startLevel();
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        stopJournalPop();
    }
});
    // ---- the journal that pops out: a see-through layer on top of everything ----
BufferedImage raw = Theme.trimmedImage("journal.png");
journalPop = new JournalPop(raw == null ? null : scaleSharp(raw, 243));
add(journalPop, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 580), 0);
journalPop.setVisible(false);
    
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPaper = new javax.swing.JPanel();
        lblPaperPrompt = new javax.swing.JLabel();
        btnHint = new javax.swing.JButton();
        lblPaperFeedback = new javax.swing.JLabel();
        btnClosePaper = new javax.swing.JButton();
        pnlSlots = new javax.swing.JPanel();
        pnlTiles = new javax.swing.JPanel();
        lblPaperBg = new javax.swing.JLabel();
        lblFeedback = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        btnContinue = new javax.swing.JButton();
        btnSpine = new javax.swing.JButton();
        lblShelfBg = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1000, 580));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlPaper.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblPaperPrompt.setText("jLabel1");
        pnlPaper.add(lblPaperPrompt, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 108, 285, 78));

        btnHint.setText("Hint");
        btnHint.addActionListener(this::btnHintActionPerformed);
        pnlPaper.add(btnHint, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 374, 115, 30));
        pnlPaper.add(lblPaperFeedback, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 328, 270, 40));

        btnClosePaper.setText("Close");
        btnClosePaper.addActionListener(this::btnClosePaperActionPerformed);
        pnlPaper.add(btnClosePaper, new org.netbeans.lib.awtextra.AbsoluteConstraints(195, 374, 115, 30));

        javax.swing.GroupLayout pnlSlotsLayout = new javax.swing.GroupLayout(pnlSlots);
        pnlSlots.setLayout(pnlSlotsLayout);
        pnlSlotsLayout.setHorizontalGroup(
            pnlSlotsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlSlotsLayout.setVerticalGroup(
            pnlSlotsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        pnlPaper.add(pnlSlots, new org.netbeans.lib.awtextra.AbsoluteConstraints(57, 190, 261, 40));
        pnlPaper.add(pnlTiles, new org.netbeans.lib.awtextra.AbsoluteConstraints(53, 240, 270, 82));

        lblPaperBg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/paper.png"))); // NOI18N
        pnlPaper.add(lblPaperBg, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -1, 376, 440));

        add(pnlPaper, new org.netbeans.lib.awtextra.AbsoluteConstraints(312, 70, 376, 439));
        add(lblFeedback, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 216, 160));

        btnBack.setText("Back to the aisle");
        btnBack.addActionListener(this::btnBackActionPerformed);
        add(btnBack, new org.netbeans.lib.awtextra.AbsoluteConstraints(784, 20, 196, 36));

        btnContinue.setText("Continue");
        btnContinue.addActionListener(this::btnContinueActionPerformed);
        add(btnContinue, new org.netbeans.lib.awtextra.AbsoluteConstraints(784, 520, 196, 40));

        btnSpine.addActionListener(this::btnSpineActionPerformed);
        add(btnSpine, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 230, 26, 70));

        lblShelfBg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/shelf_005.png"))); // NOI18N
        add(lblShelfBg, new org.netbeans.lib.awtextra.AbsoluteConstraints(236, 0, 527, 580));
    }// </editor-fold>//GEN-END:initComponents

    private void btnHintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHintActionPerformed
        if (solved) {
        return;
    }
    ScoreManager.onHint();
    int i = Math.min(hintsUsed, Story.L3_HINTS.length - 1);   // hint 1, then hint 2
    hintsUsed++;
    setPaperFeedback("Hint " + (i + 1) + ": " + Story.L3_HINTS[i], true);
    }//GEN-LAST:event_btnHintActionPerformed

    private void btnClosePaperActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClosePaperActionPerformed
         pnlPaper.setVisible(false);
    }//GEN-LAST:event_btnClosePaperActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
       GameManager.showCard("level3");  
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnContinueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContinueActionPerformed
         LevelManager.nextLevel();
    }//GEN-LAST:event_btnContinueActionPerformed

    private void btnSpineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSpineActionPerformed
        hideDialogue();                            // so the box does not cover the paper
    pnlPaper.setVisible(true);
    }//GEN-LAST:event_btnSpineActionPerformed
   /** Plays lines in the dialogue box at the bottom of the screen. */
   /** Rebuilds the screen from the saved progress. Called every time the card is shown. */
private void startLevel() {
    stopJournalPop();
    GameState gs = GameState.getInstance();
    solved = gs.getChoice(KEY_SOLVED, "no").equals("yes");
    puzzle = new WordScramblePuzzle(Story.SCRAMBLE_WORD);
    hintsUsed = 0;

    setFeedback("", true);
    pnlPaper.setVisible(false);
    lblPaperPrompt.setText("<html>" + Story.L3_CATALOG + "</html>");
    setPaperFeedback("", true);
    btnHint.setEnabled(!solved);
    btnSpine.setVisible(!solved);              // after the puzzle the book is gone
    btnContinue.setEnabled(solved);
    buildTiles();

    // intro: only the first time the player enters this screen
    if (!solved && gs.getChoice(KEY_INTRO, "no").equals("no")) {
        gs.putChoice(KEY_INTRO, "yes");
        speak(Story.L3_SHELF_INTRO, null);
    }
    if (solved) {
    playJournalPop(false);          // already solved: the journal just rests there
}
}

/** Fresh tiles in the tray. If the puzzle is already solved, the word sits locked in the slots. */
private void buildTiles() {
    for (JPanel slot : slots) {
        slot.removeAll();
    }
    pnlTiles.removeAll();

    if (solved) {
        for (int i = 0; i < slots.length; i++) {
            JLabel tile = makeTile(Story.SCRAMBLE_WORD.charAt(i));
            DragDropHelper.lock(tile);
            slots[i].add(tile, BorderLayout.CENTER);
        }
    } else {
        for (char c : Story.L3_TILES.toCharArray()) {
            pnlTiles.add(makeTile(c));
        }
    }
    pnlSlots.revalidate();
    pnlSlots.repaint();
    pnlTiles.revalidate();
    pnlTiles.repaint();
}

private JLabel makeTile(char letter) {
    final JLabel tile = new JLabel(String.valueOf(letter), SwingConstants.CENTER);
    tile.setFont(new Font("Consolas", Font.BOLD, 20));
    tile.setForeground(INK);
    tile.setOpaque(true);
    tile.setBackground(new Color(0xD8C79B));
    tile.setBorder(BorderFactory.createLineBorder(INK, 1));
    tile.setPreferredSize(new Dimension(30, 36));
    DragDropHelper.makeDraggable(tile);

    // a plain click (no dragging) moves the tile
    tile.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            moveTileByClick(tile);
        }
    });
    return tile;
}

/** Called by a slot when a tile is dropped on it. */
private boolean slotAccepts(JPanel slot) {
    if (solved || slot.getComponentCount() > 0) {
        return false;                          // already has a tile
    }
    SwingUtilities.invokeLater(this::checkWord);   // check AFTER the tile has moved in
    return true;
}
/** Tray tile: jumps to the first empty slot. Slot tile: goes back to the tray. */
private void moveTileByClick(JLabel tile) {
    if (solved) {
        return;                                  // solved tiles stay locked
    }
    if (tile.getParent() == pnlTiles) {          // tray -> first empty slot
        for (JPanel slot : slots) {
            if (slot.getComponentCount() == 0) {
                slot.add(tile, BorderLayout.CENTER);
                pnlSlots.revalidate();
                pnlSlots.repaint();
                pnlTiles.revalidate();
                pnlTiles.repaint();
                SwingUtilities.invokeLater(this::checkWord);
                return;
            }
        }
    } else {                                     // slot -> tray
        pnlTiles.add(tile);
        setPaperFeedback("", true);              // clear the old "wrong" message
        pnlSlots.revalidate();
        pnlSlots.repaint();
        pnlTiles.revalidate();
        pnlTiles.repaint();
    }
}

/** When every slot has a tile, check the word. */
private void checkWord() {
    if (solved) {
        return;
    }
    StringBuilder word = new StringBuilder();
    for (JPanel slot : slots) {
        if (slot.getComponentCount() == 0) {
            return;                            // not full yet
        }
        word.append(((JLabel) slot.getComponent(0)).getText());
    }
    if (puzzle.check(word.toString())) {
        onSolved();
    } else {
        ScoreManager.onWrong();
        setPaperFeedback(Story.L3_TILES_WRONG, false);
    }
}

private void onSolved() {
    solved = true;
    ScoreManager.onCorrect();
    GameState gs = GameState.getInstance();
    gs.putChoice(KEY_SOLVED, "yes");
    gs.addItem(Story.ITEM_JOURNAL);
    gs.addItem(Story.ITEM_EVIDENCE_AR);

    for (JPanel slot : slots) {                // the tiles can no longer be moved
        DragDropHelper.lock((JLabel) slot.getComponent(0));
    }
    btnHint.setEnabled(false);
    setPaperFeedback(Story.L3_TILES_RIGHT, true);

    // a short pause so the player sees the word, then the journal pops out
Timer pause = new Timer(1200, e -> {
    pnlPaper.setVisible(false);
    playJournalPop(true);                     // the journal pops out of the spine
    btnSpine.setVisible(false);               // the spine is empty now

    // the lines start when the journal has landed
    Timer talk = new Timer(POP_MS, e2 -> speak(Story.L3_FOUND, () -> btnContinue.setEnabled(true)));
    talk.setRepeats(false);
    talk.start();
});
pause.setRepeats(false);
pause.start();
}

/** Plays lines in the dialogue box. "after" runs when the last line ends. */
private void speak(String[][] lines, Runnable after) {
    DialogueBox box = GameManager.getDialogue();
    if (box == null) {                         // no box (testing a form alone)
        if (after != null) {
            after.run();
        }
        return;
    }
    box.play(lines, after);
}

private void hideDialogue() {
    if (GameManager.getDialogue() != null) {
        GameManager.getDialogue().hideBox();
    }
}

/** A book spine drawn in code, so you need no image for it. */
private static ImageIcon makeSpineIcon(int w, int h) {
    BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = img.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(new Color(0, 0, 0, 90));        // shadow
    g.fillRect(4, 4, w - 4, h - 4);
    g.setColor(new Color(0x7A2E2E));           // the spine
    g.fillRect(0, 0, w - 4, h - 4);
    g.setColor(new Color(0xE8DCC0));           // the peeling label
    g.fillRect(3, 8, w - 10, h / 3);
    g.setColor(new Color(0x5A1F1F));
    g.drawRect(0, 0, w - 5, h - 5);
    g.dispose();
    return new ImageIcon(img);
}

private void setFeedback(String text, boolean good) {
    lblFeedback.setForeground(good ? Theme.AMBER : BAD);
    lblFeedback.setText("<html>" + text.replace("\n", "<br>") + "</html>");
}

private void setPaperFeedback(String text, boolean good) {
    lblPaperFeedback.setForeground(good ? INK : PAPER_BAD);
    lblPaperFeedback.setText("<html>" + text.replace("\n", "<br>") + "</html>");
}
/** Swing normally assumes children never overlap. Ours do, so repaint them in the right order. */
@Override
public boolean isOptimizedDrawingEnabled() {
    return false;
}

/** The journal flies out of the spine to the middle of the screen and floats there. */
private void playJournalPop(boolean animate) {
    stopJournalPop();
    Rectangle spine = btnSpine.getBounds();
    final double x0 = spine.getCenterX();               // start at the spine
    final double y0 = spine.getCenterY();
    journalPop.setVisible(true);
    popStart = System.currentTimeMillis() - (animate ? 0 : POP_MS);   // no animation = already landed
    // Sfx.play("thud");                                 // optional sound, see the Sfx step from Level 3

    popTimer = new Timer(16, e -> {
        long ms = System.currentTimeMillis() - popStart;
        double t = ms / (double) POP_MS;                 // 0 at the start, 1 when landed
        if (t < 1) {
            double move = easeOutCubic(t);               // flies to the middle
            double size = 0.12 + 0.88 * easeOutBack(t);  // grows, a little too big, then settles
            journalPop.place(x0 + (POP_X - x0) * move, y0 + (POP_Y - y0) * move,
                    size, Math.min(1, t * 2) * 0.8);
        } else {
            double bob = Math.sin(ms / 350.0) * 3;       // gentle floating up and down
            journalPop.place(POP_X, POP_Y + bob, 1.0, 0.8);
        }
    });
    popTimer.start();
}

private void stopJournalPop() {
    if (popTimer != null) {
        popTimer.stop();
    }
    if (journalPop != null) {
        journalPop.setVisible(false);
    }
}

/** Fast at the start, slow at the end. */
private static double easeOutCubic(double t) {
    double u = 1 - t;
    return 1 - u * u * u;
}

/** Goes a little past 1 and comes back: the "pop". */
private static double easeOutBack(double t) {
    double c1 = 1.70158;
    double c3 = c1 + 1;
    double u = t - 1;
    return 1 + c3 * u * u * u + c1 * u * u;
}

/** Scales a picture to this width with sharp pixels (no blur). */
private static BufferedImage scaleSharp(BufferedImage src, int width) {
    int height = src.getHeight() * width / src.getWidth();
    BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = out.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    g.drawImage(src, 0, 0, width, height, null);
    g.dispose();
    return out;
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnClosePaper;
    private javax.swing.JButton btnContinue;
    private javax.swing.JButton btnHint;
    private javax.swing.JButton btnSpine;
    private javax.swing.JLabel lblFeedback;
    private javax.swing.JLabel lblPaperBg;
    private javax.swing.JLabel lblPaperFeedback;
    private javax.swing.JLabel lblPaperPrompt;
    private javax.swing.JLabel lblShelfBg;
    private javax.swing.JPanel pnlPaper;
    private javax.swing.JPanel pnlSlots;
    private javax.swing.JPanel pnlTiles;
    // End of variables declaration//GEN-END:variables
/** A see-through layer over the whole screen. It only draws the journal. */
private static class JournalPop extends JComponent {

    private final BufferedImage img;     // the journal, already at its final size (null = missing file)
    private double cx;                   // center of the book
    private double cy;
    private double scale;                // 1.0 = final size
    private double glow;                 // 0 to 1: how strong the amber glow is

    JournalPop(BufferedImage img) {
        this.img = img;
        setOpaque(false);                // see-through, clicks go to what is behind
        setFocusable(false);
    }

    private int finalWidth() {
        return img != null ? img.getWidth() : 243;
    }

    private int finalHeight() {
        return img != null ? img.getHeight() : 213;
    }

    void place(double cx, double cy, double scale, double glow) {
        this.cx = cx;
        this.cy = cy;
        this.scale = scale;
        this.glow = glow;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        int w = (int) Math.round(finalWidth() * scale);
        int h = (int) Math.round(finalHeight() * scale);
        if (w < 2 || h < 2) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();

        // amber glow behind the book
        float radius = (float) (finalWidth() * 0.75 * Math.max(scale, 0.2));
        g2.setPaint(new RadialGradientPaint(new Point2D.Double(cx, cy), radius,
                new float[] { 0f, 1f },
                new Color[] { new Color(255, 176, 0, (int) (140 * glow)), new Color(255, 176, 0, 0) }));
        g2.fillOval((int) (cx - radius), (int) (cy - radius), (int) (radius * 2), (int) (radius * 2));

        // the book itself, with sharp pixels
        int x = (int) Math.round(cx - w / 2.0);
        int y = (int) Math.round(cy - h / 2.0);
        if (img != null) {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(img, x, y, w, h, null);
        } else {                         // no picture: a purple box, so the game never crashes
            g2.setColor(new Color(0x7E64A2));
            g2.fillRect(x, y, w, h);
            g2.setColor(Theme.AMBER);
            g2.drawRect(x, y, w - 1, h - 1);
        }
        g2.dispose();
    }
}
}
