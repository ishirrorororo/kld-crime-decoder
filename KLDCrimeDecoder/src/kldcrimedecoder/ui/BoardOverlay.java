/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kldcrimedecoder.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import kldcrimedecoder.data.Story;
import kldcrimedecoder.engine.GameManager;

/**
 * The Suspect Board as a short scene on top of a level (Levels 6, 7 and 8).
 * Same look as the SuspectBoard form: the corkboard over the hallway, one card per suspect.
 * Cleared suspects get a CLEARED stamp, the next lead glows amber, then Arnie's lines play.
 *
 * Use in a level (after initComponents()):
 *   board = new BoardOverlay(1);     // 1 = Amy cleared, 2 = Amy and Amariah, 3 = all three
 *   add(board, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 580), 0);   // index 0 = on top
 *   board.play(Story.BOARD_UPDATE[1], () -> startTheLevel());
 */
public class BoardOverlay extends JPanel {

    private static final Color CORK  = new Color(0xB5895A);
    private static final Color PAPER = new Color(0xF3EBD6);
    private static final Color INK   = new Color(0x1F3A93);
    private static final Color BROWN = new Color(0x5A4632);
    private static final Color DARK  = new Color(0x0B1220);
    private static final Color STAMP = new Color(0xC0392B);

    private final int cleared;

    /** @param cleared how many suspects are cleared so far (the cards run Amy, Amariah, Aimee). */
    public BoardOverlay(int cleared) {
        this.cleared = cleared;
        setLayout(null);
        setOpaque(true);
        setBackground(DARK);
        addMouseListener(new MouseAdapter() { });          // swallow clicks so the level underneath can't be clicked

        JLabel title = new JLabel(Story.BOARD_TITLE);
        Theme.heading(title);
        title.setOpaque(true);
        title.setBackground(DARK);
        title.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        title.setBounds(30, 10, 236, 28);
        add(title);

        JLabel feedback = new JLabel(cleared >= Story.BOARD_CARDS.length
                ? Story.BOARD_ALL_CLEARED
                : Story.BOARD_NEXT_LEAD + Story.BOARD_CARDS[cleared][0]);
        Theme.label(feedback);
        feedback.setOpaque(true);
        feedback.setBackground(DARK);
        feedback.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.AMBER, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        feedback.setBounds(276, 8, 694, 32);
        add(feedback);

        JPanel board = new JPanel(new GridLayout(1, 3, 14, 0));
        board.setBackground(CORK);
        board.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x6B4A2B), 6),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        board.setBounds(30, 50, 570, 274);
        for (int i = 0; i < Story.BOARD_CARDS.length; i++) {
            board.add(makeCard(i, Story.BOARD_CARDS[i]));
        }
        add(board);

        JLabel bg = new JLabel(loadImage("room_hallway.png", 1000, 580));
        bg.setBounds(0, 0, 1000, 580);
        add(bg);
    }

    /** Shows the board, plays the lines, hides the board, then runs "done". */
    public void play(String[][] lines, Runnable done) {
        setVisible(true);
        Runnable finish = () -> {
            setVisible(false);
            if (done != null) {
                done.run();
            }
        };
        if (GameManager.getDialogue() == null) {
            finish.run();                                   // no dialogue box (testing the form alone)
        } else {
            GameManager.getDialogue().play(lines, finish);
        }
    }

    /** One pinned card, like SuspectBoard's, plus the CLEARED stamp or the amber "next lead" edge. */
    private JLabel makeCard(final int index, String[] row) {
        Icon pic = Theme.portrait(row[0], 100, 112);
        final boolean isCleared = index < cleared;
        final boolean isNext = index == cleared;
        final JLabel card = new JLabel(
                "<html><div align='center'><b>" + row[0] + "</b><br><small>" + wrap(row[1], 20) + "</small></div></html>",
                pic, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isCleared) {                                   // dim the card, then stamp it
                    g2.setColor(new Color(30, 30, 30, 95));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.rotate(Math.toRadians(-16), getWidth() / 2.0, getHeight() / 2.0);
                    g2.setFont(new Font("Consolas", Font.BOLD, 24));
                    String s = "CLEARED";
                    int w = g2.getFontMetrics().stringWidth(s);
                    int x = (getWidth() - w) / 2, y = getHeight() / 2 + 8;
                    g2.setColor(new Color(STAMP.getRed(), STAMP.getGreen(), STAMP.getBlue(), 215));
                    g2.setStroke(new BasicStroke(3f));
                    g2.drawRoundRect(x - 8, y - 26, w + 16, 36, 8, 8);
                    g2.drawString(s, x, y);
                    g2.rotate(Math.toRadians(16), getWidth() / 2.0, getHeight() / 2.0);
                }
                g2.setColor(new Color(0xC0392B));                  // the red pin
                g2.fillOval(getWidth() / 2 - 8, 5, 16, 16);
                g2.setColor(new Color(255, 255, 255, 150));
                g2.fillOval(getWidth() / 2 - 4, 8, 5, 5);
                g2.dispose();
            }
        };
        card.setOpaque(true);
        card.setBackground(PAPER);
        card.setForeground(INK);
        card.setFont(new Font("Georgia", Font.PLAIN, 13));
        card.setVerticalTextPosition(SwingConstants.BOTTOM);
        card.setHorizontalTextPosition(SwingConstants.CENTER);
        card.setIconTextGap(8);
        int thick = isNext ? 4 : 2;
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isNext ? Theme.AMBER : BROWN, thick),
                BorderFactory.createEmptyBorder(26 - thick, 8 - thick, 8 - thick, 8 - thick)));
        return card;
    }

    private static Icon loadImage(String file, int w, int h) {
        java.net.URL url = BoardOverlay.class.getResource("/kldcrimedecoder/images/" + file);
        if (url == null) {
            return null;
        }
        return new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    /** Breaks a sentence into short lines, so it never runs past the edge of the card. */
    private static String wrap(String text, int maxChars) {
        StringBuilder out = new StringBuilder();
        int line = 0;
        for (String word : text.split(" ")) {
            if (line > 0 && line + 1 + word.length() > maxChars) {
                out.append("<br>");
                line = 0;
            } else if (line > 0) {
                out.append(' ');
                line++;
            }
            out.append(word);
            line += word.length();
        }
        return out.toString();
    }
}