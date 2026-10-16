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
      /**
 * Loads a square picture that sits on a plain white background (like a JPG).
 * The white that touches the edges, and any pale purple glow, becomes see-through.
 * The picture is scaled to size x size. Returns null if the file is missing.
 */
/**
 * Loads a picture that sits on a plain white background (like a JPG).
 * The white that touches the edges, and any pale purple or pink glow, becomes see-through.
 * The empty edges are cut off and the picture is scaled to this height.
 * Returns null if the file is missing.
 */
public static Icon withoutWhiteBackground(String file, int height) {
    java.net.URL url = Theme.class.getResource("/kldcrimedecoder/images/" + file);
    if (url == null) {
        return null;
    }
    try {
        BufferedImage src = javax.imageio.ImageIO.read(url);

        // 1. a half-size working copy: fast, and "nearest" does not blend colors
        int w = src.getWidth() / 2;
        int h = src.getHeight() / 2;
        BufferedImage work = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = work.createGraphics();
        g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(src, 0, 0, w, h, null);
        g.dispose();
        int[] px = work.getRGB(0, 0, w, h, null, 0, w);

        // 2. white that is connected to the edges becomes see-through (flood fill)
        boolean[] seen = new boolean[w * h];
        int[] queue = new int[w * h];
        int head = 0;
        int tail = 0;
        for (int x = 0; x < w; x++) {
            tail = seed(px, seen, queue, tail, x);                    // top row
            tail = seed(px, seen, queue, tail, (h - 1) * w + x);      // bottom row
        }
        for (int y = 0; y < h; y++) {
            tail = seed(px, seen, queue, tail, y * w);                // left column
            tail = seed(px, seen, queue, tail, y * w + w - 1);        // right column
        }
        while (head < tail) {
            int p = queue[head++];
            int x = p % w;
            if (x > 0) {
                tail = seed(px, seen, queue, tail, p - 1);
            }
            if (x < w - 1) {
                tail = seed(px, seen, queue, tail, p + 1);
            }
            if (p >= w) {
                tail = seed(px, seen, queue, tail, p - w);
            }
            if (p < w * (h - 1)) {
                tail = seed(px, seen, queue, tail, p + w);
            }
        }

                // 3. the background and the glow become transparent, and we count what is left
        int[] colCount = new int[w];
        int[] rowCount = new int[h];
        for (int i = 0; i < px.length; i++) {
            int r = (px[i] >> 16) & 0xFF;
            int gr = (px[i] >> 8) & 0xFF;
            int b = px[i] & 0xFF;
            boolean glow = (b - gr) > 20 && (b - r) > -12;     // pale purple or pink
            if (seen[i] || glow) {
                px[i] = 0;                                     // fully transparent
            } else {
                colCount[i % w]++;
                rowCount[i / w]++;
            }
        }

        // the picture starts at the first column and row that hold many pixels,
        // so a few stray specks on the edge cannot move it
        int minX = firstBig(colCount, false);
        int maxX = firstBig(colCount, true);
        int minY = firstBig(rowCount, false);
        int maxY = firstBig(rowCount, true);
        if (minX < 0 || minY < 0) {
            return null;                                       // nothing left
        }

        // 4. cut off the empty edges and scale to the wanted height ("nearest" = sharp)
        int cw = maxX - minX + 1;
        int ch = maxY - minY + 1;
        int outW = Math.max(1, (int) Math.round(cw * (double) height / ch));
        BufferedImage out = new BufferedImage(outW, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            int sy = minY + y * ch / height;
            for (int x = 0; x < outW; x++) {
                int sx = minX + x * cw / outW;
                out.setRGB(x, y, px[sy * w + sx]);
            }
        }
        return new ImageIcon(out);
    } catch (java.io.IOException e) {
        return null;
    }
}
/** Adds a white pixel to the flood-fill queue if it has not been seen yet. */
private static int seed(int[] px, boolean[] seen, int[] queue, int tail, int index) {
    int c = px[index];
    boolean white = ((c >> 16) & 0xFF) >= 228 && ((c >> 8) & 0xFF) >= 228 && (c & 0xFF) >= 228;
    if (!seen[index] && white) {
        seen[index] = true;
        queue[tail] = index;
        return tail + 1;
    }
    return tail;
}  
        
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
    /** Index of the first (or last) entry that holds at least 20 pixels, or -1 if none. */
private static int firstBig(int[] counts, boolean fromEnd) {
    if (fromEnd) {
        for (int i = counts.length - 1; i >= 0; i--) {
            if (counts[i] >= 20) {
                return i;
            }
        }
    } else {
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] >= 20) {
                return i;
            }
        }
    }
    return -1;
}
}