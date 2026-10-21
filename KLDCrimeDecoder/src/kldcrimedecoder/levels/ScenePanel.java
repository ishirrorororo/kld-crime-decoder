package kldcrimedecoder.levels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
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

    protected void hideOverlay() {
        overlay = null;
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
}