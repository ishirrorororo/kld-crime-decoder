package kldcrimedecoder.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import kldcrimedecoder.puzzles.CipherWheel;

/**
 * The only hand-drawn part of Level 1: two rings of letters.
 * INHERITANCE: extends JPanel. POLYMORPHISM: overrides paintComponent.
 * Outer ring = fixed coded letters. Inner ring = turns with the wheel.
 */
public class CipherWheelPanel extends JPanel {

    private final CipherWheel wheel;

    public CipherWheelPanel(CipherWheel wheel) {
        this.wheel = wheel;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        double cx = getWidth() / 2.0, cy = getHeight() / 2.0;
        int r = Math.min(getWidth(), getHeight()) / 2 - 4;
        int rInner = (int) (r * 0.66);
        int rHub = (int) (r * 0.42);

        g2.setColor(new Color(16, 21, 31, 235));
        g2.fillOval((int) cx - r, (int) cy - r, r * 2, r * 2);
        g2.setColor(new Color(255, 230, 120));
        g2.drawOval((int) cx - r, (int) cy - r, r * 2, r * 2);
        g2.setColor(new Color(40, 52, 76, 240));
        g2.fillOval((int) cx - rInner, (int) cy - rInner, rInner * 2, rInner * 2);
        g2.setColor(new Color(150, 200, 255));
        g2.drawOval((int) cx - rInner, (int) cy - rInner, rInner * 2, rInner * 2);
        g2.setColor(new Color(16, 21, 31));
        g2.fillOval((int) cx - rHub, (int) cy - rHub, rHub * 2, rHub * 2);

        g2.setFont(new Font("Consolas", Font.BOLD, Math.max(11, r / 9)));
        for (int i = 0; i < CipherWheel.ALPHABET; i++) {
            double angle = Math.toRadians(-90 + i * 360.0 / CipherWheel.ALPHABET);
            g2.setColor(Color.WHITE);
            drawLetter(g2, (char) ('A' + i), cx + Math.cos(angle) * (r - 14), cy + Math.sin(angle) * (r - 14));
            g2.setColor(new Color(150, 200, 255));
            drawLetter(g2, wheel.innerLetterAt(i), cx + Math.cos(angle) * (rInner - 14), cy + Math.sin(angle) * (rInner - 14));
        }
        g2.dispose();
    }

    private void drawLetter(Graphics2D g2, char c, double x, double y) {
        FontMetrics fm = g2.getFontMetrics();
        String s = String.valueOf(c);
        g2.drawString(s, (float) (x - fm.stringWidth(s) / 2.0), (float) (y + fm.getAscent() / 2.0 - 2));
    }
}

