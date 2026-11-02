/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kldcrimedecoder.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Makes buttons that sit on top of a picture (hotspots) invisible, and draws a golden glow
 * around them only while the mouse is over them.
 * Use:  HoverGlow.install(btnPhone, btnPhoto, btnBag);
 * The buttons are still placed in the Design tab. This only changes how they look.
 */
public final class HoverGlow {

    private static final Color GLOW = new Color(255, 215, 1);   // same gold as the Level 3 shelves
    private static final int SIZE = 8;

    private HoverGlow() {
    }

    public static void install(AbstractButton... buttons) {
        for (AbstractButton b : buttons) {
            b.setUI(new InvisibleButtonUI());       // paints nothing, whatever the Look and Feel is
            b.setText("");
            b.setOpaque(false);
            b.setContentAreaFilled(false);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setBorder(new GlowBorder());
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    b.setBorderPainted(true);
                    b.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    b.setBorderPainted(false);
                    b.repaint();
                }
            });
        }
    }

    /** A button look that draws nothing at all. */
    private static class InvisibleButtonUI extends BasicButtonUI {
        @Override
        public void paint(Graphics g, JComponent c) {
            // nothing: the picture behind the button shows through
        }
    }

    /** Gold glow that is bright at the edge and fades inward. */
    private static class GlowBorder implements Border {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(1.5f));
            for (int i = 0; i < SIZE; i++) {
                int alpha = 220 * (SIZE - i) / SIZE;
                g2.setColor(new Color(GLOW.getRed(), GLOW.getGreen(), GLOW.getBlue(), alpha));
                g2.drawRoundRect(x + i, y + i, w - 1 - 2 * i, h - 1 - 2 * i, 14, 14);
            }
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(0, 0, 0, 0);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}