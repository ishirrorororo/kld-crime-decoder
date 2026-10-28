package kldcrimedecoder.levels;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import kldcrimedecoder.engine.GameManager;

/**
 * INHERITANCE: base class for story screens (Level0Prologue extends this).
 * It paints the scene picture, one character and an optional overlay
 * (like a phone screen). Levels only choose WHAT to show.
 */
public class ScenePanel extends JPanel implements Scene {

    private static final String IMG_PATH = "/kldcrimedecoder/images/";

    // ENCAPSULATION: all state is private, reached through getters/setters
    private Image background;
    private Color fallbackColor = Color.BLACK;
    private boolean fitHeight;                  // true = keep picture ratio (phone screens)

    private Image charBody, charHair;
    private int charX, charY;
    private int charHeight = 450;

    private Image overlay;
    private String overlayText;                 // message bubble drawn on the overlay phone screen
    private int overlayY = 50;
    private int overlayHeight = 420;

    // ---------- getters / setters ----------
    public Color getFallbackColor() { return fallbackColor; }
    public void setFallbackColor(Color c) { this.fallbackColor = c; repaint(); }

    public int getCharHeight() { return charHeight; }
    public void setCharHeight(int h) { this.charHeight = h; repaint(); }

    public int getOverlayY() { return overlayY; }
    public void setOverlayY(int y) { this.overlayY = y; repaint(); }

    public int getOverlayHeight() { return overlayHeight; }
    public void setOverlayHeight(int h) { this.overlayHeight = h; repaint(); }

    // ---------- POLYMORPHISM: subclasses override this ----------
    @Override
    public void startScene() {
        // default: nothing. Each level overrides it.
    }

    // ---------- images ----------
    protected Image loadImage(String name) {
        java.net.URL url = getClass().getResource(IMG_PATH + name + ".png");
        if (url == null) {
            System.out.println("IMAGE NOT FOUND: " + IMG_PATH + name + ".png");
            return null;
        }
        return new ImageIcon(url).getImage();
    }

    // POLYMORPHISM (overloading): same name, different parameters
    protected void setScene(String imageName, Color fallback) {
        setScene(imageName, fallback, false);
    }

    protected void setScene(String imageName, Color fallback, boolean keepRatio) {
        background = loadImage(imageName);
        fallbackColor = fallback;
        fitHeight = keepRatio;
        repaint();
    }

    protected void showCharacter(String body, int x, int y) {
        showCharacter(body, null, x, y);
    }

    protected void showCharacter(String body, String hair, int x, int y) {
        charBody = loadImage(body);
        charHair = (hair == null) ? null : loadImage(hair);
        charX = x;
        charY = y;
        repaint();
    }

    protected void hideCharacter() {
        charBody = null;
        charHair = null;
        repaint();
    }

    protected void showOverlay(String imageName) {
        overlay = loadImage(imageName);
        repaint();
    }

    /** Draws a text message (like a chat bubble) on the phone screen of the overlay. */
    protected void showOverlayText(String text) {
        overlayText = text;
        repaint();
    }

    protected void hideOverlay() {
        overlay = null;
        overlayText = null;                       // the message goes away with the phone
        repaint();
    }

    /** Plays dialogue. If there is no DialogueBox (test), just continues. */
    protected void play(String[][] lines, Runnable after) {
        if (GameManager.getDialogue() != null) {
            GameManager.getDialogue().play(lines, after);
        } else if (after != null) {
            after.run();
        }
    }

    // ---------- painting ----------
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(fallbackColor);
        g.fillRect(0, 0, getWidth(), getHeight());
        if (background != null) {
            if (fitHeight) {
                drawCentered(g, background, 0, getHeight());
            } else {
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
        }
        drawCharacter(g, charBody);               // body/emotion first
        drawCharacter(g, charHair);               // hair on top
        if (overlay != null) {
            drawCentered(g, overlay, overlayY, overlayHeight);
            if (overlayText != null) {
                drawMessageBubble(g);
            }
        }
    }

    private void drawCharacter(Graphics g, Image img) {
        if (img == null || img.getHeight(this) <= 0) {
            return;
        }
        int w = img.getWidth(this) * charHeight / img.getHeight(this);
        g.drawImage(img, charX, charY, w, charHeight, this);
    }

    private void drawCentered(Graphics g, Image img, int y, int h) {
        if (img.getHeight(this) <= 0) {
            return;
        }
        int w = img.getWidth(this) * h / img.getHeight(this);
        g.drawImage(img, (getWidth() - w) / 2, y, w, h, this);
    }

    /**
     * Draws overlayText as a pixel-art chat bubble inside the phone screen.
     * The bubble is drawn small (no smoothing) and then enlarged, so every
     * letter is made of chunky pixels but stays readable.
     */
    private void drawMessageBubble(Graphics g) {
        int h = overlayHeight;
        int w = overlay.getWidth(this) * h / overlay.getHeight(this);
        int left = (getWidth() - w) / 2;
        int px = Math.max(2, Math.round(h / 210f));            // size of one "pixel" on screen
        int screenX = left + (int) (w * 0.20);
        int screenW = (int) (w * 0.48);
        int top = overlayY + (int) (h * 0.19);

        int bw = screenW / px;                                  // bubble width in small pixels
        Font font = new Font(Font.MONOSPACED, Font.BOLD, 11);
        BufferedImage probe = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D pg = probe.createGraphics();
        pg.setFont(font);
        FontMetrics fm = pg.getFontMetrics();
        pg.dispose();

        int pad = 5;
        java.util.List<String> rows = wrap(overlayText, fm, bw - 2 * pad);
        int lineH = fm.getHeight() - 1;
        int bh = rows.size() * lineH + 2 * pad;

        BufferedImage buf = new BufferedImage(bw, bh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D b = buf.createGraphics();
        b.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        b.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        b.setFont(font);
        // bubble with notched corners (pixel look)
        b.setColor(new Color(0x2B2F3A));
        b.fillRect(1, 0, bw - 2, bh);
        b.fillRect(0, 1, bw, bh - 2);
        b.setColor(Color.WHITE);
        b.fillRect(2, 1, bw - 4, bh - 2);
        b.fillRect(1, 2, bw - 2, bh - 4);
        // text
        b.setColor(new Color(0x2B2F3A));
        int y = pad + fm.getAscent() - 1;
        for (String row : rows) {
            b.drawString(row, pad, y);
            y += lineH;
        }
        b.dispose();

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.drawImage(buf, screenX, top, bw * px, bh * px, null);
        g2.dispose();
    }

    /** Splits text into rows that fit maxWidth. */
    private java.util.List<String> wrap(String text, FontMetrics fm, int maxWidth) {
        java.util.List<String> rows = new java.util.ArrayList<>();
        StringBuilder row = new StringBuilder();
        for (String word : text.split(" ")) {
            String test = row.length() == 0 ? word : row + " " + word;
            if (fm.stringWidth(test) > maxWidth && row.length() > 0) {
                rows.add(row.toString());
                row = new StringBuilder(word);
            } else {
                row = new StringBuilder(test);
            }
        }
        if (row.length() > 0) {
            rows.add(row.toString());
        }
        return rows;
    }
}