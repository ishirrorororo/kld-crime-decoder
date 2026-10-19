/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package kldcrimedecoder.levels;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import kldcrimedecoder.data.Story;
import kldcrimedecoder.engine.GameManager;
import kldcrimedecoder.engine.GameState;
import kldcrimedecoder.engine.LevelManager;
import kldcrimedecoder.engine.ScoreManager;
import kldcrimedecoder.engine.TimerManager;
import kldcrimedecoder.ui.DialogueBox;
import kldcrimedecoder.ui.DragDropHelper;
import kldcrimedecoder.ui.Theme;

/**
 *
 * @author Joel
 */
public class Level4 extends javax.swing.JPanel {
    private static final Color BAD        = new Color(0xFF6B6B);
    private static final Color PAPER      = new Color(0xE9DFC8);   // journal page
    private static final Color CARD_FRONT = new Color(0xF3EBD6);
    private static final Color CARD_BACK  = new Color(0xD7E3EE);   // pale blue = back of the card
    private static final Color INK        = new Color(0x1F3A93);   // blue ink
    private static final Color BROWN      = new Color(0x5A4632);
    /** true = every selected card shows the silhouette, so the picture does not give the answer away. */
    private static final boolean SILHOUETTE_ONLY = true;

    private int placedCount;        // cards sorted correctly so far
    private int hintsUsed;
    private boolean puzzleActive;   // false while the intro and the choice are on screen
    private JLabel selected;        // the card the player tapped last

    /**
     * Creates new form Level4
     */
    public Level4() {
    initComponents();

    // ---- theme ----
    Theme.background(this);
    Theme.heading(lblTitle);
    Theme.label(lblFeedback);
    // dark boxes so the text is readable on top of the picture
lblFeedback.setOpaque(true);
lblFeedback.setBackground(new Color(0x0B1220));
lblFeedback.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Theme.AMBER, 1),
        BorderFactory.createEmptyBorder(4, 8, 4, 8)));
lblTitle.setOpaque(true);
lblTitle.setBackground(new Color(0x0B1220));
lblTitle.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
    Theme.button(btnHint, btnContinue);

    // ---- background picture (a colored placeholder if the file is missing) ----
    lblBg.setIcon(loadImage("room_library.png", 1000, 580, "room_library.png (missing)"));

    // ---- the journal: the open-book picture is the page ----
    pnlJournal.setOpaque(false);
    pnlJournal.setBorder(null);
    Icon book = loadBook("journal_book.png", 720);
    if (book != null) {
        lblBook.setIcon(book);
    } else {                                   // no picture: a plain paper page, like before
        pnlJournal.setOpaque(true);
        pnlJournal.setBackground(PAPER);
        pnlJournal.setBorder(BorderFactory.createLineBorder(BROWN, 3));
    }
    lblNamesTitle.setText("Tap a card to flip it.");
    lblNamesTitle.setFont(new Font("Consolas", Font.PLAIN, 11));
    lblNamesTitle.setForeground(BROWN);
    for (JLabel t : new JLabel[] { lblClearedTitle, lblSuspectTitle }) {
        t.setFont(new Font("Consolas", Font.BOLD, 13));
        t.setForeground(INK);
        t.setHorizontalAlignment(SwingConstants.CENTER);
    }
    lblPortrait.setBorder(BorderFactory.createLineBorder(BROWN, 2));
    lblPortraitName.setFont(new Font("Georgia", Font.BOLD, 14));
    lblPortraitName.setForeground(INK);
    lblFact.setFont(new Font("Georgia", Font.PLAIN, 13));
    lblFact.setForeground(INK);
    lblFact.setVerticalAlignment(SwingConstants.TOP);

    // ---- the three empty panels get their layouts here ----
    pnlCards.setOpaque(false);
    pnlCards.setLayout(new GridLayout(3, 2, 6, 6));        // 3 rows x 2 columns on the left page
    for (JPanel box : new JPanel[] { pnlCleared, pnlSuspect }) {
        box.setOpaque(false);                              // drawn on the page
        box.setLayout(new GridLayout(3, 1, 0, 6));
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createDashedBorder(BROWN, 2, 5, 3, true),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));
    }

    // ---- the two boxes accept cards ----
    DragDropHelper.makeDropTarget(pnlCleared, (label, target) -> handleDrop(label, true));
    DragDropHelper.makeDropTarget(pnlSuspect, (label, target) -> handleDrop(label, false));

    // Start fresh every time this card is shown
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
        pnlJournal = new javax.swing.JPanel();
        lblNamesTitle = new javax.swing.JLabel();
        pnlCards = new javax.swing.JPanel();
        lblPortrait = new javax.swing.JLabel();
        lblPortraitName = new javax.swing.JLabel();
        lblFact = new javax.swing.JLabel();
        lblClearedTitle = new javax.swing.JLabel();
        lblSuspectTitle = new javax.swing.JLabel();
        pnlCleared = new javax.swing.JPanel();
        pnlSuspect = new javax.swing.JPanel();
        lblBook = new javax.swing.JLabel();
        lblFeedback = new javax.swing.JLabel();
        btnHint = new javax.swing.JButton();
        btnContinue = new javax.swing.JButton();
        lblBg = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("The Journal");
        add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 524, 150, 30));

        pnlJournal.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblNamesTitle.setText("Names in the journal. Tap a card to flip it.");
        pnlJournal.add(lblNamesTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 40, 240, 18));

        javax.swing.GroupLayout pnlCardsLayout = new javax.swing.GroupLayout(pnlCards);
        pnlCards.setLayout(pnlCardsLayout);
        pnlCardsLayout.setHorizontalGroup(
            pnlCardsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 284, Short.MAX_VALUE)
        );
        pnlCardsLayout.setVerticalGroup(
            pnlCardsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 218, Short.MAX_VALUE)
        );

        pnlJournal.add(pnlCards, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, 284, 218));
        pnlJournal.add(lblPortrait, new org.netbeans.lib.awtextra.AbsoluteConstraints(42, 286, 76, 88));
        pnlJournal.add(lblPortraitName, new org.netbeans.lib.awtextra.AbsoluteConstraints(126, 286, 200, 20));
        pnlJournal.add(lblFact, new org.netbeans.lib.awtextra.AbsoluteConstraints(126, 308, 200, 80));

        lblClearedTitle.setText("CLEARED");
        pnlJournal.add(lblClearedTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 60, 136, 20));

        lblSuspectTitle.setText("SUSPECT");
        pnlJournal.add(lblSuspectTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 60, 136, 20));

        javax.swing.GroupLayout pnlClearedLayout = new javax.swing.GroupLayout(pnlCleared);
        pnlCleared.setLayout(pnlClearedLayout);
        pnlClearedLayout.setHorizontalGroup(
            pnlClearedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 136, Short.MAX_VALUE)
        );
        pnlClearedLayout.setVerticalGroup(
            pnlClearedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 292, Short.MAX_VALUE)
        );

        pnlJournal.add(pnlCleared, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 80, 136, 292));

        javax.swing.GroupLayout pnlSuspectLayout = new javax.swing.GroupLayout(pnlSuspect);
        pnlSuspect.setLayout(pnlSuspectLayout);
        pnlSuspectLayout.setHorizontalGroup(
            pnlSuspectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 136, Short.MAX_VALUE)
        );
        pnlSuspectLayout.setVerticalGroup(
            pnlSuspectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 292, Short.MAX_VALUE)
        );

        pnlJournal.add(pnlSuspect, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 80, 136, 292));

        lblBook.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/journal_book.png"))); // NOI18N
        pnlJournal.add(lblBook, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 720, 496));

        add(pnlJournal, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 14, 720, 496));
        add(lblFeedback, new org.netbeans.lib.awtextra.AbsoluteConstraints(172, 516, 586, 56));

        btnHint.setText("Hint");
        btnHint.addActionListener(this::btnHintActionPerformed);
        add(btnHint, new org.netbeans.lib.awtextra.AbsoluteConstraints(766, 524, 100, 34));

        btnContinue.setText("Continue");
        btnContinue.addActionListener(this::btnContinueActionPerformed);
        add(btnContinue, new org.netbeans.lib.awtextra.AbsoluteConstraints(874, 524, 100, 34));

        lblBg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/room_library.png"))); // NOI18N
        add(lblBg, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 580));
    }// </editor-fold>//GEN-END:initComponents

    private void btnHintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHintActionPerformed
         if (!puzzleActive) {
        return;
        }
        ScoreManager.onHint();
        int i = Math.min(hintsUsed, Story.L4_HINTS.length - 1);   // hint 1, then hint 2
        hintsUsed++;
        setFeedback("Hint " + (i + 1) + ": " + Story.L4_HINTS[i], true);
    }//GEN-LAST:event_btnHintActionPerformed

    private void btnContinueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContinueActionPerformed
        GameManager.showCard("suspectBoard");      // the board comes before Level 5
    }//GEN-LAST:event_btnContinueActionPerformed
/** Resets the level, plays the intro, then asks the choice. */
private void startLevel() {
    placedCount = 0;
    hintsUsed = 0;
    puzzleActive = false;
    selected = null;

    lblTitle.setText(Story.LEVEL_TITLES[4]);
    setFeedback("", true);
    clearSelection();
    btnContinue.setEnabled(false);
    btnHint.setEnabled(false);          // hints start after the choice
    buildCards();

    speak(Story.L4_INTRO, this::askChoice);
}

/** Choice moment: study the handwriting, or skip it. */
private void askChoice() {
    DialogueBox box = GameManager.getDialogue();
    if (box == null) {                  // no dialogue box (testing the form alone)
        beginPuzzle();
        return;
    }
    TimerManager.pause();               // the clock waits while the player chooses
    box.ask("Arnie (thinking)", Story.L4_CHOICE_PROMPT,
            new String[] { Story.L4_CHOICE_A, Story.L4_CHOICE_B },
            pick -> {
                GameState gs = GameState.getInstance();
                if (!gs.isGameOver()) {
                    TimerManager.resume();
                }
                if (pick == 0) {        // A: study the handwriting
                    gs.addDenial(Story.DENIAL_READ);
                    gs.putChoice("level4", "read");
                    TimerManager.drain(Story.CHOICE_READ_SECONDS);
                    if (gs.isGameOver()) {
                        return;         // time ran out, the ending is showing
                    }
                    speak(Story.L4_AFTER_A, this::beginPuzzle);
                } else {                // B: skip it
                    gs.addDenial(Story.DENIAL_SKIP);
                    gs.putChoice("level4", "skip");
                    speak(Story.L4_AFTER_B, this::beginPuzzle);
                }
            });
}

private void beginPuzzle() {
    puzzleActive = true;
    btnHint.setEnabled(true);
    setFeedback(Story.L4_PROMPT, true);
}

/** Makes the six cards. */
private void buildCards() {
    pnlCards.removeAll();
    pnlCleared.removeAll();
    pnlSuspect.removeAll();

    int[] order = { 0, 3, 1, 4, 2, 5 };           // mixes cleared and suspect names
    for (int i : order) {
        pnlCards.add(makeCard(Story.L4_CARDS[i]));
    }
    for (JPanel p : new JPanel[] { pnlCards, pnlCleared, pnlSuspect }) {
        p.revalidate();
        p.repaint();
    }
}

/** One card: the name on the front, the fact on the back. */
private JLabel makeCard(String[] row) {
    final JLabel card = new JLabel("", SwingConstants.CENTER);
    card.setOpaque(true);
    card.putClientProperty("row", row);           // {name, fact, "cleared"/"suspect"}
    card.putClientProperty("flipped", Boolean.FALSE);
    showFront(card);

    DragDropHelper.makeDraggable(card);
    card.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            onCardClicked(card);
        }
    });
    return card;
}

private void showFront(JLabel card) {
    String[] row = (String[]) card.getClientProperty("row");
    card.setBackground(CARD_FRONT);
    card.setForeground(INK);
    card.setFont(new Font("Georgia", Font.BOLD, 14));
    card.setBorder(BorderFactory.createLineBorder(BROWN, 2));
    card.setText("<html><body style='width:100px'><div align='center'>" + row[0] + "</div></body></html>");
}

private void showBack(JLabel card) {
    String[] row = (String[]) card.getClientProperty("row");
    card.setBackground(CARD_BACK);
    card.setForeground(INK);
    card.setFont(new Font("Georgia", Font.PLAIN, 10));
    card.setBorder(BorderFactory.createLineBorder(BROWN, 2));
    card.setText("<html><body style='width:108px'><div align='center'>" + row[1] + "</div></body></html>");
}

/** Tap = flip the card, select it, and show its portrait. */
private void onCardClicked(JLabel card) {
      if (!puzzleActive || Boolean.TRUE.equals(card.getClientProperty("dd.locked"))) {
      return;
  }
    boolean flippedNow = !Boolean.TRUE.equals(card.getClientProperty("flipped"));
    card.putClientProperty("flipped", flippedNow);
    if (flippedNow) {
        showBack(card);
    } else {
        showFront(card);
    }

    // highlight the selected card
    if (selected != null && selected != card) {
        selected.setBorder(BorderFactory.createLineBorder(BROWN, 2));
    }
    selected = card;
    card.setBorder(BorderFactory.createLineBorder(Theme.AMBER, 3));

    // small portrait + the fact (the fact shows only while the card is flipped)
    String[] row = (String[]) card.getClientProperty("row");
      Icon pic = SILHOUETTE_ONLY ? Theme.silhouette(72, 84) : Theme.portrait(row[0], 72, 84);
if (pic == null) {                                       // no pictures in the project yet
    pic = loadImage(portraitFile(row[0]), 72, 84, row[0]);
}
lblPortrait.setIcon(pic);
    lblPortrait.setVisible(true);
    lblPortraitName.setText(row[0]);
    lblFact.setText(flippedNow ? "<html>" + row[1] + "</html>" : "");
}

private void clearSelection() {
    lblPortrait.setIcon(null);
    lblPortrait.setVisible(false);
    lblPortraitName.setText("");
    lblFact.setText("");
}

/** "Amy Mercado" becomes "portrait_amy_mercado.png". */
private static String portraitFile(String name) {
    return "portrait_" + name.toLowerCase().replace(' ', '_') + ".png";
}

/** A card was dropped into a box. Return true to keep it there. */
private boolean handleDrop(JLabel card, boolean droppedOnCleared) {
    if (!puzzleActive) {
        return false;                    // the intro and the choice are still going
    }
    String[] row = (String[]) card.getClientProperty("row");
    boolean isCleared = row[2].equals("cleared");

    if (isCleared != droppedOnCleared) { // wrong box: the card jumps back
        ScoreManager.onWrong();
        setFeedback(row[0] + (droppedOnCleared ? Story.L4_WRONG_TO_CLEARED : Story.L4_WRONG_TO_SUSPECT), false);
        return false;
    }

    // right box: the card stays and cannot be moved again
    DragDropHelper.lock(card);
      card.putClientProperty("flipped", Boolean.FALSE);
  showFront(card);
    card.setBorder(BorderFactory.createLineBorder(BROWN, 2));
    if (card == selected) {
    selected = null;
    clearSelection();                // the portrait goes away with the card
}
    placedCount++;
    setFeedback(row[0] + (isCleared ? Story.L4_RIGHT_CLEARED : Story.L4_RIGHT_SUSPECT), true);

    if (placedCount == Story.L4_CARDS.length) {
        SwingUtilities.invokeLater(this::onSolved);   // after the card has moved in
    }
    return true;
}

private void onSolved() {
    ScoreManager.onCorrect();
    puzzleActive = false;
    btnHint.setEnabled(false);
    speak(Story.L4_SOLVED, () -> btnContinue.setEnabled(true));
}

/** Plays lines in the dialogue box. "after" runs when the last line ends. */
private void speak(String[][] lines, Runnable after) {
    DialogueBox box = GameManager.getDialogue();
    if (box == null) {
        if (after != null) {
            after.run();
        }
        return;
    }
    box.play(lines, after);
}

/** Loads /kldcrimedecoder/images/<file>. If missing, draws a colored placeholder with a label. */
private static Icon loadImage(String file, int w, int h, String label) {
    java.net.URL url = Level4.class.getResource("/kldcrimedecoder/images/" + file);
    if (url != null) {
        Image img = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
    BufferedImage ph = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = ph.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(new Color(0x2B3A55));                 // placeholder color
    g.fillRect(0, 0, w, h);
    g.setColor(Theme.AMBER);
    g.drawRect(0, 0, w - 1, h - 1);
    g.setFont(new Font("Consolas", Font.PLAIN, 11));
    FontMetrics fm = g.getFontMetrics();
    g.drawString(label, Math.max(2, (w - fm.stringWidth(label)) / 2), h / 2);
    g.dispose();
    return new ImageIcon(ph);
}

private void setFeedback(String text, boolean good) {
    lblFeedback.setForeground(good ? Theme.AMBER : BAD);
    lblFeedback.setText("<html>" + text.replace("\n", "<br>") + "</html>");
    lblFeedback.setVisible(!text.isEmpty());
}

/** Loads the open-book picture, cuts off the empty edges, and scales it with sharp pixels. Null if missing. */
private static Icon loadBook(String file, int width) {
    java.net.URL url = Level4.class.getResource("/kldcrimedecoder/images/" + file);
    if (url == null) {
        return null;
    }
    try {
        BufferedImage src = javax.imageio.ImageIO.read(url);

        // find the box that holds the visible pixels (the file has empty space around the book)
        int minX = src.getWidth();
        int minY = src.getHeight();
        int maxX = -1;
        int maxY = -1;
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                if ((src.getRGB(x, y) >>> 24) > 10) {      // this pixel is not transparent
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }
        if (maxX < 0) {
            return null;                                   // the picture is empty
        }
        BufferedImage trimmed = src.getSubimage(minX, minY, maxX - minX + 1, maxY - minY + 1);

        // scale it: nearest neighbor keeps the pixel art sharp
        int height = trimmed.getHeight() * width / trimmed.getWidth();
        BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(trimmed, 0, 0, width, height, null);
        g.dispose();
        return new ImageIcon(out);
    } catch (java.io.IOException e) {
        return null;
    }
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnContinue;
    private javax.swing.JButton btnHint;
    private javax.swing.JLabel lblBg;
    private javax.swing.JLabel lblBook;
    private javax.swing.JLabel lblClearedTitle;
    private javax.swing.JLabel lblFact;
    private javax.swing.JLabel lblFeedback;
    private javax.swing.JLabel lblNamesTitle;
    private javax.swing.JLabel lblPortrait;
    private javax.swing.JLabel lblPortraitName;
    private javax.swing.JLabel lblSuspectTitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlCards;
    private javax.swing.JPanel pnlCleared;
    private javax.swing.JPanel pnlJournal;
    private javax.swing.JPanel pnlSuspect;
    // End of variables declaration//GEN-END:variables
}
