/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package kldcrimedecoder.levels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import kldcrimedecoder.data.Story;
import kldcrimedecoder.engine.GameManager;
import kldcrimedecoder.engine.GameState;
import kldcrimedecoder.engine.LevelManager;
import kldcrimedecoder.engine.ScoreManager;
import kldcrimedecoder.puzzles.LogSortPuzzle;
import kldcrimedecoder.ui.Theme;

/**
 * Level 7: Aimee Ferrer. The scene and the log screen: drag six login entries into the AimeeF or AMIE column.
 * The puzzle's data and answer check live in the puzzles package (LogSortPuzzle).
 * All the text lives in Story.java (L7_...).
 *
 * @author Joel
 */
public class Level7 extends javax.swing.JPanel {

    private static final String KEY_SOLVED = "level7.solved";

    // same palette as KLDCrimeDecoder.java
    private static final Color PANEL = new Color(0x1B2A47);
    private static final Color AMBER = new Color(0xFFB000);
    private static final Color INK   = new Color(0xE8E8E8);
    private static final Color BAD   = new Color(0xFF6B6B);
    private static final Color GOOD  = new Color(0x6BFFB0);

    private enum Phase { INTRO, PUZZLE, SOLVED }
    private Phase phase = Phase.INTRO;

    private static final int TRAY = LogSortPuzzle.UNSORTED, COL_AIMEE = LogSortPuzzle.AIMEE, COL_AMIE = LogSortPuzzle.AMIE;
    // same as lblAimee's Width/Height in the Design tab (Properties -> Layout)
    private static final int SPRITE_W = 340, SPRITE_H = 430;
    private static final int SEATED_H = 330;     // the seated Aimee at the computer keeps her own size

    // darker versions of the palette for text that sits on the cream paper
    private static final Color INK_DARK = new Color(0x3A2F20);
    private static final Color FB_AMBER = new Color(0xB36B00);
    private static final Color FB_BAD   = new Color(0xC0392B);
    private static final Color FB_GOOD  = new Color(0x1E8A4C);

    private final LogSortPuzzle puzzle = new LogSortPuzzle();

    // Tags (log_tag.png is 256 x 50) wait in a tray on the left, then are dragged into the slots on the sheet.
    private static final int ENTRY_W = 256, ENTRY_H = 50;
    private static final int TRAY_X = 40, TRAY_Y = 96, TRAY_PITCH = 62;

    // Where the six slots are on the clipboard picture, measured on the 1171 x 946 original drawing.
    // The code scales these to however big lblSheet is in the Design tab.
    private static final int SRC_W = 1171;
    private static final int[] SLOT_COL_X = {77, 622};       // left column = AimeeF, right column = AMIE
    private static final int[] SLOT_ROW_Y = {331, 490, 650};  // three rows
    private static final int SLOT_W = 468, SLOT_H = 121;

    private JLabel[] entries;            // filled in setupLevel7() from lblEntry1..6
    private int[] where = new int[puzzle.getEntryCount()];    // TRAY, COL_AIMEE or COL_AMIE for each entry
    private int hintsUsed = 0;

    private ImageIcon standingIcon, seatedIcon;      // Aimee sprites: her normal portrait, and her sitting at Computer 4

    // Aimee's expressions: aimee_<mood>.png (confused, sad, scared, suspicious, neutral...), picked by the tag in Story.java
    private static final java.util.regex.Pattern MOOD = java.util.regex.Pattern.compile("^(.*?)\\s*\\[(\\w+)\\]$");
    private static final String KEY_BOARD = "level7.boardSeen";
    // same as lblAimeeSeated's X, Y, Width, Height in the Design tab (the glow button is placed over her picture)
    private static final int SEAT_X = -20, SEAT_Y = 110, SEAT_W = 300, SEAT_H = 380;
    private String[][] openingLines;                   // the narration lines played while she sits at the computer
    private final java.util.Map<String, ImageIcon> moodIcons = new java.util.HashMap<>();
    private String currentMood = "neutral";
    private boolean waitingForClick = false;           // true while the player must click Aimee to start the talk
    private Runnable afterClick;
    private javax.swing.JButton btnTalk;               // invisible button over the seated Aimee (glows on hover)
    private kldcrimedecoder.ui.BoardOverlay board;     // the corkboard scene shown before the level

    /**
     * Creates new form Level7
     */
    public Level7() {
        initComponents();
        setupLevel7();

        // invisible button over the seated Aimee: gold glow on hover (HoverGlow), click starts the talk
        btnTalk = new javax.swing.JButton();
        int tw = (seatedIcon == null) ? SEAT_W : seatedIcon.getIconWidth();
        int th = (seatedIcon == null) ? SEAT_H : seatedIcon.getIconHeight();
        add(btnTalk, new org.netbeans.lib.awtextra.AbsoluteConstraints(
                SEAT_X + (SEAT_W - tw) / 2, SEAT_Y + (SEAT_H - th), tw, th), 0);
        kldcrimedecoder.ui.HoverGlow.install(btnTalk);
        btnTalk.addActionListener(e -> talkClicked());
        btnTalk.setVisible(false);

        board = new kldcrimedecoder.ui.BoardOverlay(2);          // 2 names cleared (Amy, Amariah)
        add(board, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 580), 0);   // index 0 = drawn on top
        board.setVisible(false);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                onShown();
            }
        });
    }

    /** Things overlap on the library picture, so repaint overlapping children in order (same as Level3). */
    @Override
    public boolean isOptimizedDrawingEnabled() {
        return false;
    }

    /** Styling that is easier in code than in the Properties window. */
    private void setupLevel7() {
        entries = new JLabel[] { lblEntry1, lblEntry2, lblEntry3, lblEntry4, lblEntry5, lblEntry6 };

        // The designer's AbsoluteLayout would snap every label back to its design position each time
        // the panel is laid out. Let it place everything once, then switch it off so dragging sticks.
        logPanel.doLayout();
        logPanel.setLayout(null);
        logPanel.setOpaque(false);                 // the clipboard picture is the panel now
        logPanel.setBorder(null);

        // ----- clipboard sheet, headings over its green bar, sticky note -----
        lblSheet.setText("");
        styleHeading(lblColAimee, Story.L7_COL_AIMEE);
        styleHeading(lblColAmie, Story.L7_COL_AMIE);

        styleSticky(lblSticky,  Story.L7_STICKY_AIMEE);      // rule for AimeeF, above her column
        styleSticky(lblSticky2, Story.L7_STICKY_AMIE);       // rule for AMIE, above hers

        // ----- prompt (plain text on the sheet, just under the slots) -----
        lblPrompt.setText(puzzle.getPrompt());
        lblPrompt.setFont(new Font("Consolas", Font.BOLD, 11));
        lblPrompt.setForeground(INK_DARK);
        lblPrompt.setHorizontalAlignment(SwingConstants.CENTER);
        lblPrompt.setOpaque(false);
        lblPrompt.setBorder(null);

        // ----- the six tags -----
        ImageIcon tagIcon = iconFor(lblEntry1, "/kldcrimedecoder/images/log_tag.png");
        for (int i = 0; i < entries.length; i++) {
            final int idx = i;
            final JLabel e = entries[i];
            e.setIcon(tagIcon);
            e.setText(tagHtml(puzzle.getEntry(i), false));      // computer number stays hidden until solved
            e.setHorizontalAlignment(SwingConstants.CENTER);
            e.setHorizontalTextPosition(SwingConstants.CENTER);          // text sits on top of the tag picture
            e.setVerticalTextPosition(SwingConstants.CENTER);
            e.setForeground(INK_DARK);
            e.setFont(new Font("Consolas", Font.BOLD, 11));              // Font.BOLD / Font.PLAIN, and the 11 is the size
            if (tagIcon == null) {                                        // no picture: plain dark card
                e.setOpaque(true);
                e.setBackground(PANEL);
                e.setForeground(INK);
            } else {
                e.setOpaque(false);
            }
            e.setBorder(null);
            e.setSize(ENTRY_W, ENTRY_H);
            e.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));

            MouseAdapter drag = new MouseAdapter() {
                private Point grab;
                @Override
                public void mousePressed(MouseEvent ev) {
                    if (phase != Phase.PUZZLE) {
                        return;
                    }
                    grab = ev.getPoint();
                    logPanel.setComponentZOrder(e, 0);          // draw the dragged tag on top
                    e.setBorder(new GlowBorder(AMBER, 5));
                }
                @Override
                public void mouseDragged(MouseEvent ev) {
                    if (phase != Phase.PUZZLE || grab == null) {
                        return;
                    }
                    Point p = SwingUtilities.convertPoint(e, ev.getPoint(), logPanel);
                    e.setLocation(p.x - grab.x, p.y - grab.y);
                }
                @Override
                public void mouseReleased(MouseEvent ev) {
                    if (phase != Phase.PUZZLE || grab == null) {
                        return;
                    }
                    grab = null;
                    e.setBorder(null);
                    Point c = new Point(e.getX() + ENTRY_W / 2, e.getY() + ENTRY_H / 2);
                    int target = TRAY;
                    if (columnArea(0).contains(c)) {
                        target = COL_AIMEE;
                    } else if (columnArea(1).contains(c)) {
                        target = COL_AMIE;
                    }
                    lblFeedback.setText(" ");
                    if (target != TRAY && countIn(target, idx) >= SLOT_ROW_Y.length) {   // that column is full
                        lblFeedback.setForeground(FB_AMBER);
                        lblFeedback.setText(Story.L7_COLUMN_FULL);
                        target = TRAY;
                    }
                    where[idx] = target;
                    arrange();
                }
            };
            e.addMouseListener(drag);
            e.addMouseMotionListener(drag);
        }

        btnCheck.setText("Check");
        btnHint.setText("Hint");
        btnContinue.setText("Continue");
        lblBackground.setText("");
        lblFeedback.setText(" ");
        lblFeedback.setFont(new Font("Consolas", Font.ITALIC, 13));
        lblFeedback.setHorizontalAlignment(SwingConstants.CENTER);

        // ----- Aimee -----
        lblAimee.setFont(new Font("Consolas", Font.BOLD, 18));
        lblAimee.setForeground(INK);
        lblAimee.setHorizontalAlignment(SwingConstants.CENTER);
        lblAimee.setVerticalAlignment(SwingConstants.BOTTOM);
        standingIcon = (lblAimee.getIcon() instanceof ImageIcon) ? fit((ImageIcon) lblAimee.getIcon(), SPRITE_H) : null;

        // seated pose: use the icon set on lblAimeeSeated in the Design tab; else fall back to the file name
        ImageIcon seatedRaw = (lblAimeeSeated.getIcon() instanceof ImageIcon) ? (ImageIcon) lblAimeeSeated.getIcon() : null;
        if (seatedRaw == null) {
            java.net.URL u = getClass().getResource("/kldcrimedecoder/images/aimeeF_computer.png");
            if (u != null) {
                seatedRaw = new ImageIcon(u);
            } else {
                System.err.println("Level7: no seated icon on lblAimeeSeated and aimeeF_computer.png NOT FOUND");
            }
        }
        seatedIcon = (seatedRaw == null) ? null : fit(seatedRaw, SEATED_H);

        lblAimee.setIcon(standingIcon);                         // her dialogue sprite stays where the Design tab puts it
        lblAimee.setText(standingIcon == null && seatedIcon == null ? "Aimee" : "");
        if (standingIcon == null && seatedIcon == null) {
            lblAimee.setBorder(BorderFactory.createDashedBorder(new Color(255, 255, 255, 90)));
        }
        // the seated pose has its own label, so it can sit somewhere else
        lblAimeeSeated.setText("");
        lblAimeeSeated.setIcon(seatedIcon);
        lblAimeeSeated.setHorizontalAlignment(SwingConstants.CENTER);
        lblAimeeSeated.setVerticalAlignment(SwingConstants.BOTTOM);

        arrange();
        logPanel.setVisible(false);
        lblAimee.setVisible(false);
        lblAimeeSeated.setVisible(false);
        btnContinue.setVisible(false);
        
        kldcrimedecoder.ui.Theme.button(btnCheck, btnHint, btnContinue);
    }

    /** White-cream text on the green bar of the sheet. */
    /** Yellow sticky note with the rule printed on top of the picture. */
    private void styleSticky(JLabel note, String text) {
        ImageIcon icon = iconFor(note, "/kldcrimedecoder/images/log_sticky.png");
        note.setIcon(icon);
        note.setText(text);
        note.setHorizontalAlignment(SwingConstants.CENTER);
        note.setHorizontalTextPosition(SwingConstants.CENTER);
        note.setVerticalTextPosition(SwingConstants.CENTER);
        note.setForeground(new Color(0x2B2B2B));
        note.setFont(new Font("Consolas", Font.PLAIN, 11));
        note.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));   // keep the text off the note edges
        if (icon == null) {                                  // no picture: plain yellow note
            note.setOpaque(true);
            note.setBackground(new Color(0xFFF3A8));
        }
    }

    private void styleHeading(JLabel h, String text) {
        h.setText(text);
        h.setFont(new Font("Consolas", Font.BOLD, 15));
        h.setForeground(new Color(0xF4EEDD));
        h.setHorizontalAlignment(SwingConstants.CENTER);
        h.setOpaque(false);
    }

    /** The picture set on the label in the Design tab, or else the picture file, or null if neither exists. */
    private ImageIcon iconFor(JLabel designerLabel, String resource) {
        if (designerLabel.getIcon() instanceof ImageIcon) {
            return (ImageIcon) designerLabel.getIcon();
        }
        java.net.URL u = getClass().getResource(resource);
        if (u == null) {
            System.err.println("Level7: " + resource + " NOT FOUND (and no icon set in the Design tab)");
            return null;
        }
        return new ImageIcon(u);
    }

    /**
     * Two lines on the ruled tag. While solving, line 1 is only the place ("by the window");
     * once solved, the computer number is added ("Computer 4 | by the window").
     */
    private static String tagHtml(String entry, boolean showComputer) {
        String[] f = entry.split(" \\| ");
        String line1 = (f.length >= 4) ? (showComputer ? f[0] + " | " + f[1] : f[1]) : entry;
        String line2 = (f.length >= 4) ? f[2] + " | " + f[3] : " ";
        return "<html><table cellspacing='0' cellpadding='0'>"
             + "<tr><td width='22'></td><td height='17' valign='bottom' align='center'>" + line1 + "</td></tr>"
             + "<tr><td></td><td height='17' valign='bottom' align='center'>" + line2 + "</td></tr></table></html>";
    }

    // ----- sheet geometry -----

    /** Scale between the original clipboard drawing and lblSheet as placed in the Design tab. */
    private double sheetScale() {
        return lblSheet.getWidth() / (double) SRC_W;
    }

    /** One slot on the sheet: column 0 = AimeeF, 1 = AMIE; row 0..2. */
    private java.awt.Rectangle slotRect(int col, int row) {
        double s = sheetScale();
        return new java.awt.Rectangle(
                lblSheet.getX() + (int) Math.round(SLOT_COL_X[col] * s),
                lblSheet.getY() + (int) Math.round(SLOT_ROW_Y[row] * s),
                (int) Math.round(SLOT_W * s),
                (int) Math.round(SLOT_H * s));
    }

    /** The whole stack of slots in one column, a little bigger so the drop is forgiving. */
    private java.awt.Rectangle columnArea(int col) {
        java.awt.Rectangle first = slotRect(col, 0);
        java.awt.Rectangle last = slotRect(col, SLOT_ROW_Y.length - 1);
        java.awt.Rectangle area = first.union(last);
        area.grow(10, 10);
        return area;
    }

    /** How many entries are in a column, not counting the one being dropped. */
    private int countIn(int column, int except) {
        int n = 0;
        for (int i = 0; i < where.length; i++) {
            if (i != except && where[i] == column) {
                n++;
            }
        }
        return n;
    }

    /** Snaps every tag to its place: its own spot in the tray, or the next free slot of its column. */
    private void arrange() {
        int nA = 0, nB = 0;
        for (int i = 0; i < entries.length; i++) {
            int x, y;
            if (where[i] == COL_AIMEE || where[i] == COL_AMIE) {
                int col = (where[i] == COL_AIMEE) ? 0 : 1;
                int row = Math.min((col == 0 ? nA++ : nB++), SLOT_ROW_Y.length - 1);
                java.awt.Rectangle r = slotRect(col, row);
                x = r.x + (r.width - ENTRY_W) / 2;
                y = r.y + (r.height - ENTRY_H) / 2;
            } else {
                x = TRAY_X;
                y = TRAY_Y + i * TRAY_PITCH;
            }
            entries[i].setBounds(x, y, ENTRY_W, ENTRY_H);
        }
        logPanel.repaint();
    }

    // ===== Scene flow =====

    /** Called every time the card is shown. */
    private void onShown() {
        GameState gs = GameState.getInstance();
        hintsUsed = 0;
        lblFeedback.setText(" ");
        endWaiting();
        board.setVisible(false);
        lblAimee.setVisible(false);
        lblAimeeSeated.setVisible(false);

        if (gs.getChoice(KEY_SOLVED, "no").equals("yes")) {      // already solved: show the sorted log and Continue
            for (int i = 0; i < where.length; i++) {
                where[i] = puzzle.getCorrectColumn(i);
                entries[i].setText(tagHtml(puzzle.getEntry(i), true));
            }
            arrange();
            enterPhase(Phase.SOLVED);
            logPanel.setVisible(false);
            btnContinue.setVisible(true);
            return;
        }
        for (int i = 0; i < where.length; i++) {
            where[i] = TRAY;
            entries[i].setText(tagHtml(puzzle.getEntry(i), false));
        }
        arrange();
        enterPhase(Phase.INTRO);
        // 1) the corkboard (once)  2) Aimee sits at Computer 4 until you click her  3) the talk  4) the puzzle
        Runnable startScene = () -> {
            // every line of L7_INTRO_OPEN except the last is played normally; the last one ("Click Aimee...")
            // is shown and the dialogue box STAYS OPEN until the player clicks her
            String[][] open = Story.L7_INTRO_OPEN;
            openingLines = java.util.Arrays.copyOf(open, open.length - 1);
            playScene(openingLines, () -> {
                if (GameManager.getDialogue() != null) {
                    GameManager.getDialogue().say("", open[open.length - 1][1]);
                }
                waitForClick(() -> playScene(Story.L7_INTRO, () -> enterPhase(Phase.PUZZLE)));
            });
        };
        if (gs.getChoice(KEY_BOARD, "no").equals("no")) {
            gs.putChoice(KEY_BOARD, "yes");
            board.play(Story.BOARD_UPDATE[2], startScene);
        } else {
            startScene.run();
        }
    }

    /** Plays lines in the shared DialogueBox (the same one Level 3 uses). */
    private void playDialogue(String[][] lines, Runnable done) {
        if (GameManager.getDialogue() != null) {
            GameManager.getDialogue().play(lines, done);
        } else if (done != null) {
            done.run();                                          // no dialogue box (panel run on its own): carry on
        }
    }

    /** Plays a scene line by line; Aimee is on screen only while someone is speaking (not for narration). */
    private void playScene(String[][] lines, Runnable done) {
        currentMood = "neutral";
        playFrom(lines, 0, done);
    }

    private void playFrom(String[][] lines, int i, Runnable done) {
        boolean opening = (lines == openingLines);                 // she sits at Computer 4, waiting to be clicked
        if (i >= lines.length) {
            if (!opening) {
                lblAimee.setVisible(false);
                lblAimeeSeated.setVisible(false);
            }
            if (done != null) {
                done.run();
            }
            return;
        }
        String speaker = lines[i][0];
        java.util.regex.Matcher m = MOOD.matcher(speaker);       // "Aimee [scared]" -> "Aimee" + "scared"
        if (m.matches()) {
            speaker = m.group(1);
            currentMood = m.group(2).toLowerCase();
        }
        boolean speaking = !speaker.isEmpty();

        if (opening) {
            lblAimee.setVisible(false);
            lblAimeeSeated.setVisible(seatedIcon != null);
        } else {
            ImageIcon sprite = moodIcon(currentMood);
            boolean useSeated = (sprite == null && seatedIcon != null);   // no portrait at all: she stays seated
            lblAimee.setIcon(sprite);
            lblAimee.setText(sprite == null && !useSeated ? "Aimee" : "");
            lblAimee.setVisible(speaking && !useSeated);
            lblAimeeSeated.setVisible(speaking && useSeated);
        }
        playDialogue(new String[][] { { speaker, lines[i][1] } }, () -> playFrom(lines, i + 1, done));
    }

    // ----- clickable Aimee -----

    /** Waits until the player clicks Aimee (she glows gold on hover), then runs "next". */
    private void waitForClick(Runnable next) {
        waitingForClick = true;
        afterClick = next;
        lblAimeeSeated.setVisible(seatedIcon != null);
        btnTalk.setToolTipText(Story.L7_CLICK_TIP);
        btnTalk.setVisible(true);
    }

    private void endWaiting() {
        waitingForClick = false;
        afterClick = null;
        if (btnTalk != null) {
            btnTalk.setVisible(false);
            btnTalk.setBorderPainted(false);
            btnTalk.setToolTipText(null);
        }
    }

    private void talkClicked() {
        if (!waitingForClick) {
            return;
        }
        Runnable next = afterClick;
        endWaiting();
        if (next != null) {
            next.run();
        }
    }

    /** Her picture for a mood (aimee_<mood>.png), or the Design tab portrait if that file doesn't exist. */
    private ImageIcon moodIcon(String mood) {
        if (!moodIcons.containsKey(mood)) {
            ImageIcon ic;
            java.net.URL u = getClass().getResource("/kldcrimedecoder/images/aimee_" + mood + ".png");
            if (u != null) {
                ic = fit(new ImageIcon(u), SPRITE_H);
            } else {
                System.err.println("Level7: aimee_" + mood + ".png NOT FOUND, using the Design tab portrait instead");
                ic = standingIcon;
            }
            moodIcons.put(mood, ic);
        }
        return moodIcons.get(mood);
    }

    private void enterPhase(Phase p) {
        phase = p;
        logPanel.setVisible(p != Phase.INTRO);
        btnCheck.setEnabled(p == Phase.PUZZLE);
        btnHint.setEnabled(p == Phase.PUZZLE);
        btnContinue.setVisible(false);
        repaint();
    }

    private void check() {
        if (phase != Phase.PUZZLE) {
            return;
        }
        String answer = answerString();
        if (!puzzle.isFullySorted(answer)) {
            lblFeedback.setForeground(FB_AMBER);
            lblFeedback.setText(Story.L7_NOT_ALL);              // no penalty for pressing Check too early
            return;
        }
        if (puzzle.check(answer)) {
            onCorrect();
        } else {
            onWrongAnswer();
        }
    }

    private void onCorrect() {
        GameState gs = GameState.getInstance();
        gs.putChoice(KEY_SOLVED, "yes");
        lblFeedback.setForeground(FB_GOOD);
        lblFeedback.setText(Story.L7_RIGHT);
        ScoreManager.onCorrect();                      // +100 plus speed bonus
        gs.addItem(Story.ITEM_AIMEE_CLEARED);
        gs.addItem(Story.ITEM_COMP7_LOG);
        enterPhase(Phase.SOLVED);
        for (int i = 0; i < entries.length; i++) {
            entries[i].setText(tagHtml(puzzle.getEntry(i), true));      // now show which computer each one was
            entries[i].setBorder(new GlowBorder(GOOD, 6));
        }
        Timer closeLog = new Timer(2500, ev -> {
            logPanel.setVisible(false);
            playScene(Story.L7_AFTER, () -> {
                btnContinue.setVisible(true);
                repaint();
            });
        });
        closeLog.setRepeats(false);
        closeLog.start();
    }

    private void onWrongAnswer() {
        ScoreManager.onWrong();                        // -10 points, -15 s
        lblFeedback.setForeground(FB_BAD);
        lblFeedback.setText(Story.L7_WRONG);
        for (int i = 0; i < where.length; i++) {       // only the wrong ones go back to the tray
            if (where[i] != puzzle.getCorrectColumn(i)) {
                where[i] = TRAY;
            }
        }
        arrange();
        shake(lblFeedback);
    }

    private void giveHint() {
        hintsUsed++;
        ScoreManager.onHint();                         // -5 points, -20 s
        lblFeedback.setForeground(FB_AMBER);
        lblFeedback.setText(puzzle.getHint(hintsUsed));
    }

    // ===== Helpers =====

    /** One digit per entry (0 = still in the tray, 1 = AimeeF, 2 = AMIE), the format LogSortPuzzle checks. */
    private String answerString() {
        StringBuilder sb = new StringBuilder();
        for (int w : where) {
            sb.append(w);
        }
        return sb.toString();
    }

    /** Scales an icon to the given height, keeping its proportions. */
    private static ImageIcon fit(ImageIcon src, int h) {
        int iw = src.getIconWidth(), ih = src.getIconHeight();
        if (iw <= 0 || ih <= 0) {
            return src;
        }
        double s = (double) h / ih;
        int nw = Math.max(1, (int) Math.round(iw * s)), nh = Math.max(1, (int) Math.round(ih * s));
        java.awt.image.BufferedImage out = new java.awt.image.BufferedImage(nw, nh, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                s < 1 ? RenderingHints.VALUE_INTERPOLATION_BICUBIC : RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(src.getImage(), 0, 0, nw, nh, null);
        g.dispose();
        return new ImageIcon(out);
    }

    /** Quick sideways wobble for a wrong answer. */
    private void shake(java.awt.Component c) {
        final int x0 = c.getX();
        final int[] offs = {-6, 6, -4, 4, -2, 2, 0};
        final int[] i = {0};
        Timer t = new Timer(35, null);
        t.addActionListener(e -> {
            c.setLocation(x0 + offs[i[0]], c.getY());
            if (++i[0] >= offs.length) {
                t.stop();
            }
        });
        t.start();
    }

    /** Same glow Level 3 uses on the shelves. */
    private static class GlowBorder implements javax.swing.border.Border {
        private final Color color;
        private final int size;

        GlowBorder(Color color, int size) {
            this.color = color;
            this.size = size;
        }

        @Override
        public void paintBorder(java.awt.Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(1.5f));
            for (int i = 0; i < size; i++) {
                int alpha = 220 * (size - i) / size;
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
                g2.drawRoundRect(x + i, y + i, w - 1 - 2 * i, h - 1 - 2 * i, 14, 14);
            }
            g2.dispose();
        }

        @Override
        public java.awt.Insets getBorderInsets(java.awt.Component c) {
            return new java.awt.Insets(0, 0, 0, 0);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    /** Moves on to Level 8. LevelManager knows the order. */
    private void goToNextLevel() {
        LevelManager.nextLevel();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnContinue = new javax.swing.JButton();
        logPanel = new javax.swing.JPanel();
        lblEntry1 = new javax.swing.JLabel();
        lblEntry2 = new javax.swing.JLabel();
        lblEntry3 = new javax.swing.JLabel();
        lblEntry4 = new javax.swing.JLabel();
        lblEntry5 = new javax.swing.JLabel();
        lblEntry6 = new javax.swing.JLabel();
        lblSticky = new javax.swing.JLabel();
        lblSticky2 = new javax.swing.JLabel();
        lblColAimee = new javax.swing.JLabel();
        lblColAmie = new javax.swing.JLabel();
        lblFeedback = new javax.swing.JLabel();
        lblPrompt = new javax.swing.JLabel();
        btnCheck = new javax.swing.JButton();
        btnHint = new javax.swing.JButton();
        lblSheet = new javax.swing.JLabel();
        lblAimeeSeated = new javax.swing.JLabel();
        lblAimee = new javax.swing.JLabel();
        lblBackground = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnContinue.setText("Continue");
        btnContinue.addActionListener(this::btnContinueActionPerformed);
        add(btnContinue, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 500, 160, 40));

        logPanel.setOpaque(false);
        logPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblEntry1.setText("entry1");
        logPanel.add(lblEntry1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 96, 256, 50));

        lblEntry2.setText("entry2");
        logPanel.add(lblEntry2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 158, 256, 50));

        lblEntry3.setText("entry3");
        logPanel.add(lblEntry3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 220, 256, 50));

        lblEntry4.setText("entry4");
        logPanel.add(lblEntry4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 282, 256, 50));

        lblEntry5.setText("entry5");
        logPanel.add(lblEntry5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 344, 256, 50));

        lblEntry6.setText("entry6");
        logPanel.add(lblEntry6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 406, 256, 50));

        lblSticky.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/log_sticky.png"))); // NOI18N
        lblSticky.setText("sticky");
        logPanel.add(lblSticky, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 140, 270, 48));

        lblSticky2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/log_sticky.png"))); // NOI18N
        lblSticky2.setText("sticky2");
        logPanel.add(lblSticky2, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 140, 270, 48));

        lblColAimee.setText("colAimee");
        logPanel.add(lblColAimee, new org.netbeans.lib.awtextra.AbsoluteConstraints(392, 99, 256, 34));

        lblColAmie.setText("colAmie");
        logPanel.add(lblColAmie, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 99, 256, 34));

        lblFeedback.setText("feedback");
        logPanel.add(lblFeedback, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 454, 580, -1));

        lblPrompt.setText("prompt");
        logPanel.add(lblPrompt, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 442, 580, -1));

        btnCheck.setText("check");
        btnCheck.addActionListener(this::btnCheckActionPerformed);
        logPanel.add(btnCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 470, 100, 26));

        btnHint.setText("hint");
        btnHint.addActionListener(this::btnHintActionPerformed);
        logPanel.add(btnHint, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 470, 100, 26));

        lblSheet.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/log_sheet.png"))); // NOI18N
        logPanel.add(lblSheet, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 15, 640, 517));

        add(logPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 580));
        add(lblAimeeSeated, new org.netbeans.lib.awtextra.AbsoluteConstraints(-20, 110, 300, 380));

        lblAimee.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/portrait_aimee_ferrer.png"))); // NOI18N
        lblAimee.setText("Aimee");
        add(lblAimee, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 340, 430));

        lblBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/library_comp.png"))); // NOI18N
        add(lblBackground, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -10, 1000, 600));
    }// </editor-fold>//GEN-END:initComponents

    private void btnContinueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContinueActionPerformed
    goToNextLevel();        // TODO add your handling code here:
    }//GEN-LAST:event_btnContinueActionPerformed

    private void btnCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckActionPerformed
    check();        // TODO add your handling code here:
    }//GEN-LAST:event_btnCheckActionPerformed

    private void btnHintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHintActionPerformed
    giveHint();        // TODO add your handling code here:
    }//GEN-LAST:event_btnHintActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCheck;
    private javax.swing.JButton btnContinue;
    private javax.swing.JButton btnHint;
    private javax.swing.JLabel lblAimee;
    private javax.swing.JLabel lblAimeeSeated;
    private javax.swing.JLabel lblBackground;
    private javax.swing.JLabel lblColAimee;
    private javax.swing.JLabel lblColAmie;
    private javax.swing.JLabel lblEntry1;
    private javax.swing.JLabel lblEntry2;
    private javax.swing.JLabel lblEntry3;
    private javax.swing.JLabel lblEntry4;
    private javax.swing.JLabel lblEntry5;
    private javax.swing.JLabel lblEntry6;
    private javax.swing.JLabel lblFeedback;
    private javax.swing.JLabel lblPrompt;
    private javax.swing.JLabel lblSheet;
    private javax.swing.JLabel lblSticky;
    private javax.swing.JLabel lblSticky2;
    private javax.swing.JPanel logPanel;
    // End of variables declaration//GEN-END:variables
}
