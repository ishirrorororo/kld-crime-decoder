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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
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
import kldcrimedecoder.engine.TimerManager;
import kldcrimedecoder.ui.DialogueBox;
import kldcrimedecoder.puzzles.ClockclipsPuzzle;

/**
 * Level 8: The Front Desk (Joel). Four camera clips sit on the four screens of the monitor.
 * Read the wall clock in each clip, then drag clips onto each other to swap them until every clip is
 * on the screen with its time (7:45, 8:10, 8:30, 9:00 PM). Then the choice: rewind clip D or skip it.
 * The puzzle's data and answer check live in the puzzles package (ClockClipsPuzzle).
 * All the text lives in Story.java (L8_...).
 *
 * @author Joel
 */
public class Level8 extends javax.swing.JPanel {

    private static final String KEY_SOLVED = "level8.solved";
    private static final String KEY_CHOICE = "level8.choice";

    // same palette as KLDCrimeDecoder.java
    private static final Color PANEL = new Color(0x1B2A47);
    private static final Color DEEP  = new Color(0x111A2E);
    private static final Color AMBER = new Color(0xFFB000);
    private static final Color INK   = new Color(0xE8E8E8);
    private static final Color BAD   = new Color(0xFF6B6B);
    private static final Color GOOD  = new Color(0x6BFFB0);

    private enum Phase { INTRO, PUZZLE, CHOICE, SOLVED }
    private Phase phase = Phase.INTRO;

    // Joel's height on screen (his picture is scaled to this, keeping its proportions)
    private static final int SPRITE_H = 390;

    // The four monitor screens, measured for room_monitor.png (760 x 468) placed at x=20, y=24.
    // Each clip is 344 x 181 and sits on a screen, just under the monitor frame.
    private static final int CLIP_W = 344, CLIP_H = 181;
    private static final int[] SCREEN_X = {52, 404, 52, 404};     // screen 0 = 7:45, 1 = 8:10, 2 = 8:30, 3 = 9:00 PM
    private static final int[] SCREEN_Y = {73, 73, 261, 261};

    private final ClockclipsPuzzle puzzle = new ClockclipsPuzzle();

    private JLabel[] clips;             // filled in setupLevel8() from lblClipA..D
    private JLabel[] times;             // lblTime1..4, the time tags on the screens
    private int[] screenOf = new int[puzzle.getClipCount()];   // which screen (0..3) each clip is on
    private int hintsUsed = 0;

    private final Map<String, ImageIcon> joelIcons = new HashMap<>();

    // ----- the wide front-office room, the front desk you click, and the sticky notes -----
    private static final String KEY_BOARD = "level8.boardSeen";
    // where the front desk is on the wide room picture (X, Y, Width, Height in panel pixels). Move/resize the glow here.
    private static final int DESK_X = 515, DESK_Y = 210, DESK_W = 470, DESK_H = 285;
    // the two sticky notes on the right of the monitor (prompt on top, feedback under it), and the buttons
    private static final int NOTE_W = 225, NOTE_H = 150;
    private static final int NOTE_X = 775, PROMPT_Y = 15, FEEDBACK_Y = 170;
    private ImageIcon wideRoomIcon, deskRoomIcon;      // expanded_room.png (before the click) and room_desk.png (after)
    private ImageIcon noteYellow, notePink;            // sticky_prompt.png and sticky_feedback.png
    private javax.swing.JButton btnDesk;               // invisible button over the front desk (glows on hover)
    private boolean waitingForClick = false;           // true while the player must click the front desk
    private Runnable afterClick;
    private kldcrimedecoder.ui.BoardOverlay board;     // the corkboard scene shown before the level

    /**
     * Creates new form Level8
     */
    public Level8() {
        initComponents();
        setupLevel8();

        // invisible button over the front desk: gold glow on hover (HoverGlow), click goes to the desk scene
        btnDesk = new javax.swing.JButton();
        add(btnDesk, new org.netbeans.lib.awtextra.AbsoluteConstraints(DESK_X, DESK_Y, DESK_W, DESK_H), 0);
        kldcrimedecoder.ui.HoverGlow.install(btnDesk);
        btnDesk.addActionListener(e -> deskClicked());
        btnDesk.setVisible(false);

        board = new kldcrimedecoder.ui.BoardOverlay(3);          // 3 names cleared (Amy, Amariah, Aimee)
        add(board, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 580), 0);   // index 0 = drawn on top
        board.setVisible(false);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                onShown();
            }
        });
    }

    /** Things overlap on the desk picture, so repaint overlapping children in order (same as Level3). */
    @Override
    public boolean isOptimizedDrawingEnabled() {
        return false;
    }

    /** Styling that is easier in code than in the Properties window. */
    private void setupLevel8() {
        clips = new JLabel[] { lblClipA, lblClipB, lblClipC, lblClipD };
        times = new JLabel[] { lblTime1, lblTime2, lblTime3, lblTime4 };

        // The designer's AbsoluteLayout would snap every label back to its design position each time
        // the panel is laid out. Let it place everything once, then switch it off so dragging sticks.
        deskPanel.doLayout();
        deskPanel.setLayout(null);
        deskPanel.setOpaque(false);                // the room_desk picture behind is the background
        deskPanel.setBorder(null);

        // ----- the two room pictures: the wide office first, the close-up desk after the click -----
        deskRoomIcon = iconFor(lblBackground, "/kldcrimedecoder/images/room_desk.png");
        wideRoomIcon = loadWideRoom();
        noteYellow = loadNote("sticky_prompt.png");
        notePink = loadNote("sticky_feedback.png");

        // ----- where the sticky notes and the buttons sit (set here, so the numbers are all in one place) -----
        lblPrompt.setBounds(NOTE_X, PROMPT_Y, NOTE_W, NOTE_H);
        lblFeedback.setBounds(NOTE_X, FEEDBACK_Y, NOTE_W, NOTE_H);
        btnCheck.setBounds(NOTE_X + 10, 328, 100, 30);
        btnHint.setBounds(NOTE_X + 118, 328, 100, 30);
        btnRewind.setBounds(NOTE_X + 10, 178, 205, 40);        // the choice buttons take the feedback note's place
        btnSkip.setBounds(NOTE_X + 10, 228, 205, 40);

        // ----- the monitor picture (frame on top, the four screens are see-through) -----
        lblMonitor.setText("");
        lblMonitor.setIcon(iconFor(lblMonitor, "/kldcrimedecoder/images/room_monitor.png"));

        // ----- time tags on the screens -----
        for (int i = 0; i < times.length; i++) {
            JLabel t = times[i];
            t.setText(puzzle.getSlot(i));
            t.setFont(new Font("Consolas", Font.BOLD, 12));
            t.setForeground(AMBER);
            t.setHorizontalAlignment(SwingConstants.CENTER);
            t.setOpaque(true);
            t.setBackground(DEEP);
            t.setBorder(BorderFactory.createLineBorder(AMBER, 1));
            t.setBounds(SCREEN_X[i] + CLIP_W - 70 - 8, SCREEN_Y[i] + CLIP_H - 20 - 8, 70, 20);   // bottom-right corner of its screen
        }

        // ----- the four clips -----
        for (int i = 0; i < clips.length; i++) {
            final int idx = i;
            final JLabel c = clips[i];
            ImageIcon icon = iconFor(c, "/kldcrimedecoder/images/clip_" + (char) ('a' + i) + ".png");
            c.setIcon(icon);
            c.setText(icon == null ? puzzle.getClip(i) : "");        // no picture yet: show the caption
            c.setHorizontalAlignment(SwingConstants.CENTER);
            c.setFont(new Font("Consolas", Font.PLAIN, 12));
            c.setForeground(INK);
            if (icon == null) {
                c.setOpaque(true);
                c.setBackground(PANEL);
            }
            c.setBorder(null);
            c.setSize(CLIP_W, CLIP_H);
            c.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));

            MouseAdapter drag = new MouseAdapter() {
                private Point grab;
                @Override
                public void mousePressed(MouseEvent ev) {
                    if (phase != Phase.PUZZLE) {
                        return;
                    }
                    grab = ev.getPoint();
                    deskPanel.setComponentZOrder(c, 0);          // draw the dragged clip on top of everything
                    c.setBorder(new GlowBorder(AMBER, 5));
                }
                @Override
                public void mouseDragged(MouseEvent ev) {
                    if (phase != Phase.PUZZLE || grab == null) {
                        return;
                    }
                    Point p = SwingUtilities.convertPoint(c, ev.getPoint(), deskPanel);
                    c.setLocation(p.x - grab.x, p.y - grab.y);
                }
                @Override
                public void mouseReleased(MouseEvent ev) {
                    if (phase != Phase.PUZZLE || grab == null) {
                        return;
                    }
                    grab = null;
                    c.setBorder(null);
                    deskPanel.setComponentZOrder(c, Math.max(0, deskPanel.getComponentZOrder(lblGray) - 1));   // just in front of the gray backing
                    Point mid = new Point(c.getX() + CLIP_W / 2, c.getY() + CLIP_H / 2);
                    for (int s = 0; s < SCREEN_X.length; s++) {
                        if (screenRect(s).contains(mid)) {
                            swapOnto(idx, s);
                            break;
                        }
                    }
                    setBox(lblFeedback, "", INK);
                    arrange();
                }
            };
            c.addMouseListener(drag);
            c.addMouseMotionListener(drag);
        }

        btnCheck.setText("Check");
        btnHint.setText("Hint");
        btnRewind.setText(Story.L8_CHOICE_REWIND);
        btnSkip.setText(Story.L8_CHOICE_SKIP);
        btnContinue.setText("Continue");
        lblBackground.setText("");
        setBox(lblFeedback, "", INK);

        // ----- Joel: one label, the picture changes with his mood -----
        lblJoel.setText("");
        lblJoel.setHorizontalAlignment(SwingConstants.CENTER);
        lblJoel.setVerticalAlignment(SwingConstants.BOTTOM);
        if (lblJoel.getIcon() instanceof ImageIcon) {
            joelIcons.put("designer", fit((ImageIcon) lblJoel.getIcon(), SPRITE_H));   // stand-in if a mood picture is missing
        }
        showJoel("neutral");

        arrange();
        deskPanel.setVisible(false);
        lblJoel.setVisible(false);
        btnContinue.setVisible(false);
    }

    /** The picture set on the label in the Design tab, or else the picture file, or null if neither exists. */
    private ImageIcon iconFor(JLabel designerLabel, String resource) {
        if (designerLabel.getIcon() instanceof ImageIcon) {
            return (ImageIcon) designerLabel.getIcon();
        }
        java.net.URL u = getClass().getResource(resource);
        if (u == null) {
            System.err.println("Level8: " + resource + " NOT FOUND (and no icon set in the Design tab)");
            return null;
        }
        return new ImageIcon(u);
    }

    /** Puts Joel's picture for a mood on his label: "happy", "sad", "confused" or "neutral" (joel_<mood>.png). */
    private void showJoel(String mood) {
        ImageIcon icon = joelIcons.get(mood);
        if (icon == null) {
            java.net.URL u = getClass().getResource("/kldcrimedecoder/images/joel_" + mood + ".png");
            if (u != null) {
                icon = fit(new ImageIcon(u), SPRITE_H);
                joelIcons.put(mood, icon);
            } else {
                System.err.println("Level8: joel_" + mood + ".png NOT FOUND");
                icon = joelIcons.get("designer");
            }
        }
        lblJoel.setIcon(icon);
        lblJoel.setText(icon == null ? "Joel" : "");
    }

    /** The wide front-office picture, scaled to the panel width (1000) and cropped to the panel height (600). */
    private ImageIcon loadWideRoom() {
        java.net.URL u = getClass().getResource("/kldcrimedecoder/images/expanded_room.png");
        if (u == null) {
            System.err.println("Level8: expanded_room.png NOT FOUND, the desk picture is used instead");
            return null;
        }
        ImageIcon src = new ImageIcon(u);
        int iw = src.getIconWidth(), ih = src.getIconHeight();
        if (iw <= 0 || ih <= 0) {
            return null;
        }
        double sc = 1000.0 / iw;
        int nh = (int) Math.round(ih * sc);
        int top = (int) Math.round(Math.max(0, nh - 600) * 0.68);        // crop more of the ceiling than of the floor
        java.awt.image.BufferedImage out = new java.awt.image.BufferedImage(1000, 600, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(src.getImage(), 0, -top, 1000, nh, null);
        g.dispose();
        return new ImageIcon(out);
    }

    /** A sticky note picture, scaled to NOTE_W x NOTE_H. Null if the file is missing (then the old dark box is used). */
    private ImageIcon loadNote(String file) {
        java.net.URL u = getClass().getResource("/kldcrimedecoder/images/" + file);
        if (u == null) {
            System.err.println("Level8: " + file + " NOT FOUND, using the dark box instead");
            return null;
        }
        java.awt.image.BufferedImage out = new java.awt.image.BufferedImage(NOTE_W, NOTE_H, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(new ImageIcon(u).getImage(), 0, 0, NOTE_W, NOTE_H, null);
        g.dispose();
        return new ImageIcon(out);
    }

    /** Which room picture is behind everything: the wide office (true) or the close-up desk (false). */
    private void setRoomView(boolean wide) {
        lblBackground.setIcon(wide && wideRoomIcon != null ? wideRoomIcon : deskRoomIcon);
    }

    /** The darker version of a colour, for text that sits on the paper of a sticky note. */
    private static Color onPaper(Color fg) {
        if (fg == BAD)   return new Color(0xB02A2A);
        if (fg == GOOD)  return new Color(0x1E7A44);
        if (fg == AMBER) return new Color(0x8A5200);
        return new Color(0x3A2F20);
    }

    /**
     * Text on a sticky note: the prompt on the yellow one, everything else on the pink one.
     * Empty text hides the note. If a note picture is missing, it falls back to the old dark box.
     */
    private void setBox(JLabel l, String text, Color fg) {
        boolean has = text != null && !text.trim().isEmpty();
        ImageIcon note = (l == lblPrompt) ? noteYellow : notePink;
        l.setFont(new Font("Consolas", Font.PLAIN, 11));
        if (note != null) {
            l.setIcon(has ? note : null);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            l.setVerticalAlignment(SwingConstants.CENTER);
            l.setHorizontalTextPosition(SwingConstants.CENTER);
            l.setVerticalTextPosition(SwingConstants.CENTER);
            l.setForeground(onPaper(fg));
            l.setOpaque(false);
            l.setBorder(null);
            // the <br> pushes the text below the tape; the width keeps it inside the paper
            l.setText(has ? "<html><body style='width:150px; text-align:center'><br>" + text + "</body></html>" : "");
        } else {
            l.setForeground(fg);
            l.setVerticalAlignment(SwingConstants.TOP);
            l.setOpaque(has);
            l.setBackground(DEEP);
            l.setBorder(has ? BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(fg, 1, true),
                    BorderFactory.createEmptyBorder(5, 7, 5, 7)) : null);
            l.setText(has ? "<html><body style='width:138px'>" + text + "</body></html>" : "");
        }
        l.repaint();
    }

    // ----- screen geometry -----

    private Rectangle screenRect(int s) {
        return new Rectangle(SCREEN_X[s], SCREEN_Y[s], CLIP_W, CLIP_H);
    }

    /** The clip currently on a screen. */
    private int clipOn(int screen) {
        for (int i = 0; i < screenOf.length; i++) {
            if (screenOf[i] == screen) {
                return i;
            }
        }
        return -1;
    }

    /** Puts a clip on a screen; the clip that was there takes its old screen. */
    private void swapOnto(int clip, int screen) {
        int from = screenOf[clip];
        if (from == screen) {
            return;
        }
        int other = clipOn(screen);
        if (other >= 0) {
            screenOf[other] = from;
        }
        screenOf[clip] = screen;
    }

    /** Puts the clips on random screens. At most one starts in the right place, so every game is different. */
    private void shuffleStart() {
        java.util.Random rnd = new java.util.Random();
        int n = screenOf.length;
        int fixed;
        do {
            java.util.List<Integer> order = new java.util.ArrayList<>();
            for (int s = 0; s < n; s++) {
                order.add(s);
            }
            java.util.Collections.shuffle(order, rnd);
            fixed = 0;
            for (int i = 0; i < n; i++) {
                screenOf[i] = order.get(i);
                if (screenOf[i] == puzzle.getCorrectSlot(i) - 1) {
                    fixed++;
                }
            }
        } while (fixed > 1);
    }

    /** Snaps every clip to its screen. */
    private void arrange() {
        for (int i = 0; i < clips.length; i++) {
            clips[i].setLocation(SCREEN_X[screenOf[i]], SCREEN_Y[screenOf[i]]);
        }
        deskPanel.repaint();
    }

    // ===== Scene flow =====

    /** Called every time the card is shown. */
    private void onShown() {
        GameState gs = GameState.getInstance();
        hintsUsed = 0;
        setBox(lblFeedback, "", INK);
        endWaiting();
        board.setVisible(false);

        boolean solved = gs.getChoice(KEY_SOLVED, "no").equals("yes");
        boolean chosen = !gs.getChoice(KEY_CHOICE, "none").equals("none");
        if (solved) {
            for (int i = 0; i < screenOf.length; i++) {
                screenOf[i] = puzzle.getCorrectSlot(i) - 1;
            }
            arrange();
            if (chosen) {                                         // all done: Joel and Continue
                enterPhase(Phase.SOLVED);
                showJoel("neutral");
                lblJoel.setVisible(false);
                btnContinue.setVisible(true);
            } else {                                              // solved but never made the choice
                enterPhase(Phase.CHOICE);
            }
            return;
        }
        shuffleStart();                                           // start mixed up, differently every time
        arrange();
        setRoomView(true);                                        // the wide office, until the front desk is clicked
        enterPhase(Phase.INTRO);
        // 1) the corkboard (once)  2) the wide office until you click the front desk  3) Joel at the desk  4) the puzzle
        Runnable startScene = () -> {
            // every line of L8_INTRO_OPEN except the last is played normally; the last one ("Click the front desk...")
            // is shown and the dialogue box STAYS OPEN until the player clicks the desk
            String[][] open = Story.L8_INTRO_OPEN;
            playScene(java.util.Arrays.copyOf(open, open.length - 1), () -> {
                if (GameManager.getDialogue() != null) {
                    GameManager.getDialogue().say("", open[open.length - 1][1]);
                }
                waitForClick(() -> {
                    setRoomView(false);                           // now the close-up desk scene appears
                    playScene(Story.L8_INTRO, () -> enterPhase(Phase.PUZZLE));
                });
            });
        };
        if (gs.getChoice(KEY_BOARD, "no").equals("no")) {
            gs.putChoice(KEY_BOARD, "yes");
            board.play(Story.BOARD_UPDATE[3], startScene);
        } else {
            startScene.run();
        }
    }

    // ----- clickable front desk -----

    /** Waits until the player clicks the front desk (it glows gold on hover), then runs "next". */
    private void waitForClick(Runnable next) {
        waitingForClick = true;
        afterClick = next;
        btnDesk.setToolTipText(Story.L8_CLICK_TIP);
        btnDesk.setVisible(true);
    }

    private void endWaiting() {
        waitingForClick = false;
        afterClick = null;
        if (btnDesk != null) {
            btnDesk.setVisible(false);
            btnDesk.setBorderPainted(false);
            btnDesk.setToolTipText(null);
        }
    }

    private void deskClicked() {
        if (!waitingForClick) {
            return;
        }
        Runnable next = afterClick;
        endWaiting();
        if (next != null) {
            next.run();
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

    /**
     * Plays a scene line by line. A line may have a third column with Joel's mood for that line
     * ("happy", "sad", "confused", "neutral"); Joel is on screen while someone is speaking (not for narration).
     */
    private void playScene(String[][] lines, Runnable done) {
        playFrom(lines, 0, done);
    }

    private void playFrom(String[][] lines, int i, Runnable done) {
        if (i >= lines.length) {
            lblJoel.setVisible(false);                           // Joel is on screen only while someone speaks
            if (done != null) {
                done.run();
            }
            return;
        }
        String[] row = lines[i];
        if (row.length > 2 && !row[2].isEmpty()) {
            showJoel(row[2]);
        }
        boolean atMonitor = (phase == Phase.PUZZLE || phase == Phase.CHOICE);
        lblJoel.setVisible(!atMonitor && !row[0].isEmpty());
        playDialogue(new String[][] { { row[0], row[1] } }, () -> playFrom(lines, i + 1, done));
    }

    private void enterPhase(Phase p) {
        phase = p;
        if (p != Phase.INTRO) {
            setRoomView(false);                        // the close-up desk scene
        }
        boolean monitor = (p == Phase.PUZZLE || p == Phase.CHOICE);
        deskPanel.setVisible(monitor);
        btnCheck.setVisible(p == Phase.PUZZLE);
        btnHint.setVisible(p == Phase.PUZZLE);
        boolean useBox = (GameManager.getDialogue() != null);     // the choice is asked in the dialogue box, like the earlier levels
        btnRewind.setVisible(p == Phase.CHOICE && !useBox);       // the two buttons are only a stand-in when the level runs on its own
        btnSkip.setVisible(p == Phase.CHOICE && !useBox);
        btnRewind.setEnabled(true);
        btnSkip.setEnabled(true);
        btnContinue.setVisible(false);
        if (monitor) {
            lblJoel.setVisible(false);
        }
        if (p == Phase.PUZZLE) {
            setBox(lblPrompt, puzzle.getPrompt(), INK);
        } else if (p == Phase.CHOICE && !useBox) {
            setBox(lblPrompt, Story.L8_CHOICE_PROMPT, AMBER);
        } else {
            setBox(lblPrompt, "", INK);
        }
        repaint();
        if (p == Phase.CHOICE && useBox) {
            askChoice();
        }
    }

    /** "Rewind clip D" or "Skip it", asked in the dialogue box (the options sit beside it, like Level 4). */
    private void askChoice() {
        DialogueBox box = GameManager.getDialogue();
        TimerManager.pause();                          // the clock waits while the player chooses
        box.ask("", Story.L8_CHOICE_PROMPT, new String[] { Story.L8_CHOICE_REWIND, Story.L8_CHOICE_SKIP }, pick -> {
            if (!GameState.getInstance().isGameOver()) {
                TimerManager.resume();
            }
            if (pick == 0) {
                rewind();
            } else {
                skipIt();
            }
        });
    }

    private void check() {
        if (phase != Phase.PUZZLE) {
            return;
        }
        if (puzzle.check(answerString())) {
            onCorrect();
        } else {
            onWrongAnswer();
        }
    }

    private void onCorrect() {
        GameState gs = GameState.getInstance();
        gs.putChoice(KEY_SOLVED, "yes");
        setBox(lblFeedback, Story.L8_RIGHT, GOOD);
        ScoreManager.onCorrect();                      // +100 plus speed bonus
        phase = Phase.SOLVED;                          // locks the clips while the choice appears
        for (JLabel c : clips) {
            c.setBorder(new GlowBorder(GOOD, 6));
        }
        btnCheck.setEnabled(false);
        btnHint.setEnabled(false);
        Timer toChoice = new Timer(1200, ev -> {
            for (JLabel c : clips) {
                c.setBorder(null);
            }
            setBox(lblFeedback, "", INK);
            enterPhase(Phase.CHOICE);
        });
        toChoice.setRepeats(false);
        toChoice.start();
    }

    private void onWrongAnswer() {
        ScoreManager.onWrong();                        // -10 points, -15 s
        setBox(lblFeedback, Story.L8_WRONG, BAD);
        shake(lblFeedback);
    }

    private void giveHint() {
        if (phase != Phase.PUZZLE) {
            return;
        }
        hintsUsed++;
        ScoreManager.onHint();                         // -5 points, -20 s
        setBox(lblFeedback, puzzle.getHint(hintsUsed), AMBER);
    }

    // ===== The choice: rewind clip D, or skip it =====

    private void rewind() {
        if (phase != Phase.CHOICE) {
            return;
        }
        GameState.getInstance().putChoice(KEY_CHOICE, "rewind");
        ScoreManager.onChoiceRead();                   // -30 s, Denial down
        finishChoice(Story.L8_REWIND);
    }

    private void skipIt() {
        if (phase != Phase.CHOICE) {
            return;
        }
        GameState.getInstance().putChoice(KEY_CHOICE, "skip");
        ScoreManager.onChoiceSkip();                   // Denial up
        finishChoice(Story.L8_SKIP);
    }

    /** Arnie's thought over the monitor, then back to Joel at the desk for the after-scene. */
    private void finishChoice(String[][] thought) {
        btnRewind.setEnabled(false);
        btnSkip.setEnabled(false);
        GameState.getInstance().addItem(Story.ITEM_CLOSET_CAMERA);
        playScene(thought, () -> {
            enterPhase(Phase.SOLVED);
            showJoel("neutral");
            playScene(Story.L8_AFTER, () -> {
                lblJoel.setVisible(false);
                btnContinue.setVisible(true);
                repaint();
            });
        });
    }

    // ===== Helpers =====

    /** One digit per clip (A, B, C, D): the screen it is on, 1 to 4. The format ClockClipsPuzzle checks. */
    private String answerString() {
        StringBuilder sb = new StringBuilder();
        for (int s : screenOf) {
            sb.append(s + 1);
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

    /** Moves on to Level 9. LevelManager knows the order. */
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
        deskPanel = new javax.swing.JPanel();
        lblTime1 = new javax.swing.JLabel();
        lblTime2 = new javax.swing.JLabel();
        lblTime3 = new javax.swing.JLabel();
        lblTime4 = new javax.swing.JLabel();
        lblPrompt = new javax.swing.JLabel();
        lblFeedback = new javax.swing.JLabel();
        btnCheck = new javax.swing.JButton();
        btnHint = new javax.swing.JButton();
        btnRewind = new javax.swing.JButton();
        btnSkip = new javax.swing.JButton();
        lblMonitor = new javax.swing.JLabel();
        lblClipA = new javax.swing.JLabel();
        lblClipB = new javax.swing.JLabel();
        lblClipC = new javax.swing.JLabel();
        lblClipD = new javax.swing.JLabel();
        lblGray = new javax.swing.JLabel();
        lblJoel = new javax.swing.JLabel();
        lblBackground = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnContinue.setText("continue");
        btnContinue.addActionListener(this::btnContinueActionPerformed);
        add(btnContinue, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 500, 160, 40));

        deskPanel.setOpaque(false);
        deskPanel.setPreferredSize(new java.awt.Dimension(1000, 580));
        deskPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTime1.setText("Time1");
        deskPanel.add(lblTime1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 81, 70, 20));

        lblTime2.setText("Time2");
        deskPanel.add(lblTime2, new org.netbeans.lib.awtextra.AbsoluteConstraints(412, 81, 70, 20));

        lblTime3.setText("Time3");
        deskPanel.add(lblTime3, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 269, 70, 20));

        lblTime4.setText("Time4");
        deskPanel.add(lblTime4, new org.netbeans.lib.awtextra.AbsoluteConstraints(412, 269, 70, 20));

        lblPrompt.setText("prompt");
        deskPanel.add(lblPrompt, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 40, 180, 110));

        lblFeedback.setText("feedback");
        deskPanel.add(lblFeedback, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 162, 180, 90));

        btnCheck.setText("check");
        btnCheck.addActionListener(this::btnCheckActionPerformed);
        deskPanel.add(btnCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 262, 85, 30));

        btnHint.setText("hint");
        btnHint.addActionListener(this::btnHintActionPerformed);
        deskPanel.add(btnHint, new org.netbeans.lib.awtextra.AbsoluteConstraints(895, 262, 85, 30));

        btnRewind.setText("rewind");
        btnRewind.addActionListener(this::btnRewindActionPerformed);
        deskPanel.add(btnRewind, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 162, 180, 40));

        btnSkip.setText("skip");
        btnSkip.addActionListener(this::btnSkipActionPerformed);
        deskPanel.add(btnSkip, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 212, 180, 40));

        lblMonitor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/room_monitor.png"))); // NOI18N
        lblMonitor.setText("monitor");
        deskPanel.add(lblMonitor, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 24, 760, 539));

        lblClipA.setText("clipA");
        deskPanel.add(lblClipA, new org.netbeans.lib.awtextra.AbsoluteConstraints(53, 73, 344, 181));

        lblClipB.setText("clipB");
        deskPanel.add(lblClipB, new org.netbeans.lib.awtextra.AbsoluteConstraints(404, 73, 344, 181));

        lblClipC.setText("clipC");
        deskPanel.add(lblClipC, new org.netbeans.lib.awtextra.AbsoluteConstraints(52, 261, 344, 181));

        lblClipD.setText("clipD");
        deskPanel.add(lblClipD, new org.netbeans.lib.awtextra.AbsoluteConstraints(404, 261, 344, 181));

        lblGray.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/gray.png"))); // NOI18N
        deskPanel.add(lblGray, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 70, 700, 370));

        add(deskPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, 0, -1, -1));

        lblJoel.setText("joel");
        add(lblJoel, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 80, 300, 390));

        lblBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/room_desk.png"))); // NOI18N
        lblBackground.setText("bg");
        add(lblBackground, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -10, 1000, 600));
    }// </editor-fold>//GEN-END:initComponents

    private void btnCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckActionPerformed
        check();
    }//GEN-LAST:event_btnCheckActionPerformed

    private void btnContinueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContinueActionPerformed
        goToNextLevel();
    }//GEN-LAST:event_btnContinueActionPerformed

    private void btnHintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHintActionPerformed
        giveHint();
    }//GEN-LAST:event_btnHintActionPerformed

    private void btnRewindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRewindActionPerformed
        rewind();
    }//GEN-LAST:event_btnRewindActionPerformed

    private void btnSkipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSkipActionPerformed
        skipIt();
    }//GEN-LAST:event_btnSkipActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCheck;
    private javax.swing.JButton btnContinue;
    private javax.swing.JButton btnHint;
    private javax.swing.JButton btnRewind;
    private javax.swing.JButton btnSkip;
    private javax.swing.JPanel deskPanel;
    private javax.swing.JLabel lblBackground;
    private javax.swing.JLabel lblClipA;
    private javax.swing.JLabel lblClipB;
    private javax.swing.JLabel lblClipC;
    private javax.swing.JLabel lblClipD;
    private javax.swing.JLabel lblFeedback;
    private javax.swing.JLabel lblGray;
    private javax.swing.JLabel lblJoel;
    private javax.swing.JLabel lblMonitor;
    private javax.swing.JLabel lblPrompt;
    private javax.swing.JLabel lblTime1;
    private javax.swing.JLabel lblTime2;
    private javax.swing.JLabel lblTime3;
    private javax.swing.JLabel lblTime4;
    // End of variables declaration//GEN-END:variables
}
