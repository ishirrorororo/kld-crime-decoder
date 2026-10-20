package kldcrimedecoder.ui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import kldcrimedecoder.data.Story;
import kldcrimedecoder.engine.GameManager;

/**
 * Main menu. Mirror picture as the background, buttons in a column on the
 * left. "Start Investigation" first flashes the glitch girl for a moment
 * (a jumpscare) and only then starts the game.
 *
 * This is plain code (no .form file), so delete MenuPanel.form.
 */
public class MenuPanel extends JPanel {

    // ---- picture files (put them in src/kldcrimedecoder/images/) ----
    private static final String BG_FILE     = "menu_mirror.png";
    private static final String SCARE_FILE  = "menu_glitch.png";

    // ---- jumpscare settings ----
    private static final int SCARE_FRAME_MS = 45;   // time per frame
    private static final int SCARE_FRAMES   = 14;   // 14 x 45 ms = about 0.6 seconds

    private final BufferedImage bgImage    = load(BG_FILE);
    private final BufferedImage scareImage = load(SCARE_FILE);
    private final Random rng = new Random();

    private final JPanel column = new JPanel();
    private final JButton btnStart     = new FlatButton("Start Investigation");
    private final JButton btnHowToPlay = new FlatButton("How to Play");
    private final JButton btnExit      = new FlatButton("Exit");

    private boolean scaring = false;   // true while the jumpscare plays
    private int scareFrame = 0;

    public MenuPanel() {
        setPreferredSize(new Dimension(1000, 600));
        setBackground(Theme.BG);
        setLayout(new BorderLayout());

        // ---- the left column: title, subtitle, buttons ----
        column.setOpaque(false);
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setBorder(BorderFactory.createEmptyBorder(0, 60, 0, 0));

        JLabel lblTitle = new JLabel("<html>" + Story.GAME_TITLE.replace(": ", ":<br>") + "</html>");
        lblTitle.setFont(Theme.TITLE.deriveFont(38f));
        lblTitle.setForeground(Theme.AMBER);
        lblTitle.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("<html><div style='width:250px'>" + Story.SUBTITLE + "</div></html>");
        Theme.label(lblSubtitle);
        lblSubtitle.setAlignmentX(LEFT_ALIGNMENT);

        column.add(Box.createVerticalGlue());
        column.add(lblTitle);
        column.add(Box.createVerticalStrut(14));
        column.add(lblSubtitle);
        column.add(Box.createVerticalStrut(40));
        addButton(btnStart);
        column.add(Box.createVerticalStrut(14));
        addButton(btnHowToPlay);
        column.add(Box.createVerticalStrut(14));
        addButton(btnExit);
        column.add(Box.createVerticalGlue());

        add(column, BorderLayout.WEST);

        // ---- button actions ----
        btnStart.addActionListener(e -> playJumpscareThenStart());
        btnHowToPlay.addActionListener(e ->
                JOptionPane.showMessageDialog(this, Story.HOW_TO_PLAY,
                        "How to Play", JOptionPane.INFORMATION_MESSAGE));
        btnExit.addActionListener(e -> System.exit(0));
    }

    private void addButton(JButton b) {
        b.setAlignmentX(LEFT_ALIGNMENT);
        b.setPreferredSize(new Dimension(260, 46));
        b.setMaximumSize(new Dimension(260, 46));
        column.add(b);
    }

    // ------------------------------------------------------------------
    //  Jumpscare
    // ------------------------------------------------------------------

    private void playJumpscareThenStart() {
        if (scaring) {
            return;                       // ignore double clicks
        }
        if (scareImage == null) {         // picture missing: just start the game
            GameManager.startNewGame();
            return;
        }
        scaring = true;
        scareFrame = 0;
        column.setVisible(false);         // hide the buttons so only the girl shows

        Timer t = new Timer(SCARE_FRAME_MS, null);
        t.addActionListener(e -> {
            scareFrame++;
            if (scareFrame >= SCARE_FRAMES) {
                t.stop();
                scaring = false;
                column.setVisible(true);  // so the menu is normal if the player comes back
                scareFrame = 0;
                repaint();
                GameManager.startNewGame();
            } else {
                repaint();
            }
        });
        t.start();
        repaint();
    }

    // ------------------------------------------------------------------
    //  Painting
    // ------------------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (!scaring) {
            drawCover(g2, bgImage, 0, 0);
        } else {
            paintScare(g2);
        }
        g2.dispose();
    }

    /** One frame of the jumpscare: flicker, shaking slices, and a quick fade. */
    private void paintScare(Graphics2D g2) {
        int w = getWidth();
        int h = getHeight();

        // black flash on frame 0 and every few frames after, like a bad signal
        if (scareFrame == 0 || scareFrame % 5 == 0) {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, w, h);
            return;
        }

        // a little mirror between flickers so it feels like it breaks through
        if (scareFrame % 4 == 0) {
            drawCover(g2, bgImage, 0, 0);
            return;
        }

        // the glitch girl, shaken sideways in horizontal slices
        int slices = 18;
        int sliceH = h / slices + 1;
        for (int i = 0; i < slices; i++) {
            int y = i * sliceH;
            int shake = rng.nextInt(5) == 0 ? rng.nextInt(80) - 40 : rng.nextInt(10) - 5;
            java.awt.Graphics2D s = (Graphics2D) g2.create(0, y, w, sliceH);
            drawCover(s, scareImage, shake, -y);
            s.dispose();
        }

        // red-ish flash on a couple of frames
        if (scareFrame % 3 == 0) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
            g2.setColor(new Color(255, 0, 60));
            g2.fillRect(0, 0, w, h);
        }
    }

    /** Draws a picture so it fills the whole panel. The right side is kept, the left is cropped. */
    private void drawCover(Graphics2D g2, BufferedImage img, int dx, int dy) {
        if (img == null) {
            return;
        }
        double scale = Math.max((double) getWidth() / img.getWidth(),
                                (double) getHeight() / img.getHeight());
        int dw = (int) Math.ceil(img.getWidth() * scale);
        int dh = (int) Math.ceil(img.getHeight() * scale);
        int x = getWidth() - dw + dx;          // stick to the right edge
        int y = (getHeight() - dh) / 2 + dy;   // centered vertically
        g2.drawImage(img, x, y, dw, dh, null);
    }

    private static BufferedImage load(String file) {
        java.net.URL url = MenuPanel.class.getResource("/kldcrimedecoder/images/" + file);
        if (url == null) {
            System.err.println("Missing picture: kldcrimedecoder/images/" + file);
            return null;
        }
        try {
            return javax.imageio.ImageIO.read(url);
        } catch (java.io.IOException e) {
            return null;
        }
    }

    // ------------------------------------------------------------------
    //  Button style: dark see-through box, amber outline, arrow on hover
    // ------------------------------------------------------------------

    private static class FlatButton extends JButton {
        private boolean hover = false;

        FlatButton(String text) {
            super(text);
            setFont(Theme.MONO);
            setForeground(Theme.AMBER);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { hover = true;  repaint(); }
                @Override public void mouseExited(java.awt.event.MouseEvent e)  { hover = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth() - 1;
            int h = getHeight() - 1;

            g2.setColor(hover ? new Color(40, 55, 90, 230) : new Color(11, 18, 32, 200));
            g2.fillRoundRect(0, 0, w, h, 16, 16);
            g2.setColor(Theme.AMBER);
            g2.drawRoundRect(0, 0, w, h, 16, 16);

            // little arrow on the left when hovered (like the reference)
            if (hover) {
                int cy = h / 2;
                g2.fillPolygon(new int[]{12, 12, 20}, new int[]{cy - 5, cy + 5, cy}, 3);
            }
            g2.dispose();
            super.paintComponent(g);   // draws the text
        }
    }
}
