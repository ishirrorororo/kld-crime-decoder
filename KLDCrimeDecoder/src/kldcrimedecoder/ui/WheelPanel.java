package kldcrimedecoder.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * Draws the two rings of the cipher wheel. It only DRAWS; the puzzle logic is in CipherWheelPuzzle.
 * In the Design tab: add a JPanel, then Properties > Code > Custom Creation Code:
 *     new kldcrimedecoder.ui.WheelPanel()
 */
public class WheelPanel extends JPanel {

    private int offset;

    public WheelPanel() {
        setOpaque(false);
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = Math.floorMod(offset, 26);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int r = Math.min(cx, cy) - 4;
        int rIn = (int) (r * 0.68);

        g2.setColor(new Color(0x1B2A47));                 // outer ring
        g2.fillOval(cx - r, cy - r, 2 * r, 2 * r);
        g2.setColor(new Color(0x2E4470));                 // inner ring (the one that turns)
        g2.fillOval(cx - rIn, cy - rIn, 2 * rIn, 2 * rIn);
        g2.setColor(new Color(0xFFB000));
        g2.drawOval(cx - r, cy - r, 2 * r, 2 * r);
        g2.drawOval(cx - rIn, cy - rIn, 2 * rIn, 2 * rIn);

        g2.setFont(new Font("Consolas", Font.BOLD, Math.max(10, r / 10)));
        for (int i = 0; i < 26; i++) {
            Graphics2D gl = (Graphics2D) g2.create();
            gl.rotate(2 * Math.PI * i / 26, cx, cy);      // letters stand along the ring
            gl.setColor(new Color(0xE8DCC0));
            drawCentered(gl, String.valueOf((char) ('A' + i)), cx, cy - (int) (r * 0.84));
            gl.setColor(new Color(0xFFB000));
            drawCentered(gl, String.valueOf((char) ('A' + Math.floorMod(i - offset, 26))), cx, cy - (int) (r * 0.52));
            gl.dispose();
        }
        g2.setColor(Color.WHITE);                         // little marker at the top
        g2.fillPolygon(new int[] { cx - 5, cx + 5, cx }, new int[] { cy - r - 2, cy - r - 2, cy - r + 8 }, 3);
        g2.dispose();
    }

    private static void drawCentered(Graphics2D g, String s, int x, int y) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(s, x - fm.stringWidth(s) / 2, y + (fm.getAscent() - fm.getDescent()) / 2);
    }
}