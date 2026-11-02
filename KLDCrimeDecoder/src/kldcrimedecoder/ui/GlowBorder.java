/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kldcrimedecoder.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractButton;
import javax.swing.border.Border;

/**
 * The soft gold glow Level 3 puts around a shelf when the mouse is over it,
 * now as a shared class any level can use.
 *
 * It implements Swing's Border interface, so a button can use it anywhere a
 * normal border goes. The easy way to use it:
 *     GlowBorder.onHover(btnPadlock, btnNote);
 * The glow shows while the mouse is over an enabled button and goes away
 * when the mouse leaves. Call GlowBorder.clear(...) when you hide a button
 * that the mouse might still be over.
 */
public class GlowBorder implements Border {

    /** The gold Level 3 uses. */
    public static final Color GOLD = new Color(255, 215, 1);

    private static final int DEFAULT_SIZE = 10;

    private final Color color;
    private final int size;

    public GlowBorder(Color color, int size) {
        this.color = color;
        this.size = size;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(1.5f));
        for (int i = 0; i < size; i++) {
            int alpha = 220 * (size - i) / size;   // bright at the edge, fading inward
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
            g2.drawRoundRect(x + i, y + i, w - 1 - 2 * i, h - 1 - 2 * i, 14, 14);
        }
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(0, 0, 0, 0);             // no insets, so nothing shifts
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    /** Gives each button the gold hover glow. Disabled buttons do not glow. */
    public static void onHover(AbstractButton... buttons) {
        for (AbstractButton button : buttons) {
            final GlowBorder glow = new GlowBorder(GOLD, DEFAULT_SIZE);

            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (button.isEnabled()) {
                        button.setBorder(glow);
                        button.setBorderPainted(true);
                        button.repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    clear(button);
                }
            });
        }
    }

    /** Turns the glow off right now (for example when the button is hidden or clicked). */
    public static void clear(AbstractButton... buttons) {
        for (AbstractButton button : buttons) {
            button.setBorderPainted(false);
            button.repaint();
        }
    }
}
