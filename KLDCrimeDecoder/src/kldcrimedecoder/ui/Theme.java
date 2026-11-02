package kldcrimedecoder.ui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * One place for colors and fonts. Forms call these in their
 * constructors, after initComponents(), so we never touch the blue code.
 */
public class Theme {
    /** The picture used for any character that has no portrait file. */
public static final String SILHOUETTE_FILE = "cleared_characters.png";

/**
 * Loads /kldcrimedecoder/images/<file>, cuts off the empty edges,
 * and fits the picture inside w x h. Returns null if the file is missing.
 */
public static Icon image(String file, int w, int h) {
    java.net.URL url = Theme.class.getResource("/kldcrimedecoder/images/" + file);
    if (url == null) {
        return null;
    }
    try {
        BufferedImage trimmed = trim(javax.imageio.ImageIO.read(url));
        double scale = Math.min((double) w / trimmed.getWidth(), (double) h / trimmed.getHeight());
        int tw = Math.max(1, (int) Math.round(trimmed.getWidth() * scale));
        int th = Math.max(1, (int) Math.round(trimmed.getHeight() * scale));
        return new ImageIcon(trimmed.getScaledInstance(tw, th, Image.SCALE_AREA_AVERAGING));
    } catch (java.io.IOException e) {
        return null;
    }
}

/** "Amy Mercado" becomes portrait_amy_mercado.png. A name with no file gets the silhouette. */
public static Icon portrait(String name, int w, int h) {
    String file = "portrait_" + name.toLowerCase().replace(' ', '_') + ".png";
    Icon pic = image(file, w, h);
    return pic != null ? pic : image(SILHOUETTE_FILE, w, h);
}

public static Icon silhouette(int w, int h) {
    return image(SILHOUETTE_FILE, w, h);
}

/** Cuts the transparent edges off a picture. */
private static BufferedImage trim(BufferedImage src) {
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
        return src;                                    // empty picture
    }
    return src.getSubimage(minX, minY, maxX - minX + 1, maxY - minY + 1);
}
/** The picture with its empty edges cut off, or null if the file is missing. */
public static BufferedImage trimmedImage(String file) {
    java.net.URL url = Theme.class.getResource("/kldcrimedecoder/images/" + file);
    if (url == null) {
        return null;
    }
    try {
        return trim(javax.imageio.ImageIO.read(url));
    } catch (java.io.IOException e) {
        return null;
    }
}

    // ---- colors ----
    public static final Color BG     = new Color(0x0B1220);  // background
    public static final Color PANEL  = new Color(0x111A2E);  // text boxes
    public static final Color BUTTON = new Color(0x1B2A47);  // button face
    public static final Color AMBER  = new Color(0xFFB000);  // accent
    public static final Color TEXT   = new Color(0xE8E8E8);  // normal text

    // ---- fonts ----
    public static final Font MONO       = new Font("Consolas", Font.PLAIN, 14);
    public static final Font MONO_BOLD  = new Font("Consolas", Font.BOLD, 14);
    public static final Font TITLE      = new Font("Consolas", Font.BOLD, 44);
    public static final Font HEADING    = new Font("Consolas", Font.BOLD, 20);

    /** Dark background for any panel or component. */
    public static void background(JComponent... items) {
        for (JComponent c : items) {
            c.setBackground(BG);
        }
    }

    /** Normal light text in the monospace font. */
    public static void label(JLabel... labels) {
        for (JLabel l : labels) {
            l.setFont(MONO);
            l.setForeground(TEXT);
        }
    }

    /** Amber heading text. */
    public static void heading(JLabel lbl) {
        lbl.setFont(HEADING);
        lbl.setForeground(AMBER);
    }

    /** Amber outlined button. */
    public static void button(JButton... buttons) {
        for (JButton b : buttons) {
            b.setFont(MONO);
            b.setBackground(BUTTON);
            b.setForeground(AMBER);
            b.setFocusPainted(false);
            b.setBorder(BorderFactory.createLineBorder(AMBER, 1));
        }
    }

    /** Dark text field with amber caret. */
    public static void textField(JTextField... fields) {
        for (JTextField f : fields) {
            f.setFont(MONO);
            f.setBackground(PANEL);
            f.setForeground(TEXT);
            f.setCaretColor(AMBER);
            f.setBorder(BorderFactory.createLineBorder(AMBER, 1));
        }
    }

    /** Dark text area that wraps long lines. */
    public static void textArea(JTextArea... areas) {
        for (JTextArea a : areas) {
            a.setFont(MONO);
            a.setBackground(PANEL);
            a.setForeground(TEXT);
            a.setCaretColor(AMBER);
            a.setLineWrap(true);
            a.setWrapStyleWord(true);
        }
    }

    /** Amber border around a scroll pane. */
    public static void scroll(JScrollPane... panes) {
        for (JScrollPane p : panes) {
            p.setBorder(BorderFactory.createLineBorder(AMBER, 1));
            p.getViewport().setBackground(PANEL);
        }
    }
}