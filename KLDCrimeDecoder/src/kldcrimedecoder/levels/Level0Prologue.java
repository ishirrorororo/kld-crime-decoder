package kldcrimedecoder.levels;
 
import kldcrimedecoder.engine.TimerManager;
import kldcrimedecoder.ui.HoverGlow;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import kldcrimedecoder.data.Story;
import kldcrimedecoder.engine.GameManager;
import kldcrimedecoder.engine.GameState;
import kldcrimedecoder.engine.LevelManager;
import kldcrimedecoder.ui.ScenePanel;
import kldcrimedecoder.ui.Theme;
import kldcrimedecoder.ui.Fade;                      
import java.awt.RenderingHints;
import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import javax.swing.JComponent;
 

 /* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
/**
 *
 * @author Alli Ocampo
 */
public class Level0Prologue extends ScenePanel {
    
    private static final String KEY_MODE = "prologue.mode";
    private static final String MODE_NAME = "name";
    private static final int MAX_LETTERS = 8;
    private static final String DEFAULT_NAME = "ARNIE";
 
    // the position of each sub-panel in the Navigator (top = 0)
    private static final int SCENE_BEDROOM = 0;
    private static final int SCENE_CLASSROOM = 1;
    private static final int SCENE_NAME = 2;
 
    private int current = SCENE_BEDROOM;     // which scene is showing
    private int collected;                   // 0A: how many of the three things were picked up
    private boolean busy;                    // true while a dialogue is playing
    private boolean awaitingOpen;        // true while the lock screen waits for a click
 
    // ---- bedroom phones: the two phone labels come from the Design tab.
    // PHONE_X / PHONE_Y must match the x / y you set for them in Design.
    private static final int PHONE_X = 288;
    private static final int PHONE_Y = 45;       // back to the original
    private JLabel lblMomText;
    private JButton btnNotif;
    private boolean profSpeaking;                // true while Danel is the speaker
    private boolean profConfused;
    private KeyFx keyFx;
     // clickable area of the "Alli's Mom" pill, relative to the top-left of lblPhoneLock    
    
    public Level0Prologue() {
        initComponents();
        lblProf.setIcon(Theme.image("danel_neutral.png", 340, 430));
        lblProfConfused.setIcon(Theme.image("danel_confused.png", 340, 430));
        lblKey.setVisible(false);                     // placeholder only: KeyFx draws the key
        buildKeyFx();
        buildPhones();
        Theme.background(this);
        Theme.label(lblTutorial, lblPrompt);
        styleTutorial();
        stylePrompt();
        Theme.button(btnContinueA, btnContinueB, btnUnlock);
        txtNickname.setFont(new java.awt.Font("Consolas", java.awt.Font.BOLD, 28));
        txtNickname.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        HoverGlow.install(btnPhone, btnPhoto, btnBag);
    }
    
    // ---- hardcoded 5x7 pixel font (uppercase only)
    private static final Map<Character, String[]> GLYPHS = new HashMap<>();
    static {
        glyph('A', ".###.|#...#|#...#|#####|#...#|#...#|#...#");
        glyph('B', "####.|#...#|#...#|####.|#...#|#...#|####.");
        glyph('C', ".###.|#...#|#....|#....|#....|#...#|.###.");
        glyph('D', "####.|#...#|#...#|#...#|#...#|#...#|####.");
        glyph('E', "#####|#....|#....|####.|#....|#....|#####");
        glyph('F', "#####|#....|#....|####.|#....|#....|#....");
        glyph('G', ".###.|#...#|#....|#.###|#...#|#...#|.###.");
        glyph('H', "#...#|#...#|#...#|#####|#...#|#...#|#...#");
        glyph('I', ".###.|..#..|..#..|..#..|..#..|..#..|.###.");
        glyph('J', "..###|...#.|...#.|...#.|...#.|#..#.|.##..");
        glyph('K', "#...#|#..#.|#.#..|##...|#.#..|#..#.|#...#");
        glyph('L', "#....|#....|#....|#....|#....|#....|#####");
        glyph('M', "#...#|##.##|#.#.#|#.#.#|#...#|#...#|#...#");
        glyph('N', "#...#|##..#|#.#.#|#..##|#...#|#...#|#...#");
        glyph('O', ".###.|#...#|#...#|#...#|#...#|#...#|.###.");
        glyph('P', "####.|#...#|#...#|####.|#....|#....|#....");
        glyph('Q', ".###.|#...#|#...#|#...#|#.#.#|#..#.|.##.#");
        glyph('R', "####.|#...#|#...#|####.|#.#..|#..#.|#...#");
        glyph('S', ".####|#....|#....|.###.|....#|....#|####.");
        glyph('T', "#####|..#..|..#..|..#..|..#..|..#..|..#..");
        glyph('U', "#...#|#...#|#...#|#...#|#...#|#...#|.###.");
        glyph('V', "#...#|#...#|#...#|#...#|#...#|.#.#.|..#..");
        glyph('W', "#...#|#...#|#...#|#.#.#|#.#.#|##.##|#...#");
        glyph('X', "#...#|#...#|.#.#.|..#..|.#.#.|#...#|#...#");
        glyph('Y', "#...#|#...#|.#.#.|..#..|..#..|..#..|..#..");
        glyph('Z', "#####|....#|...#.|..#..|.#...|#....|#####");
        glyph(',', ".....|.....|.....|.....|..##.|..#..|.#...");
        glyph('.', ".....|.....|.....|.....|.....|..##.|..##.");
        glyph('?', ".###.|#...#|....#|...#.|..#..|.....|..#..");
        glyph('!', "..#..|..#..|..#..|..#..|..#..|.....|..#..");
    }
 
     private static void glyph(char c, String rows) {
        GLYPHS.put(c, rows.split("\\|"));
    }
     
     /** Called every time this card is shown (see ScenePanel). */
    @Override
    protected void startScene() {
        GameState gs = GameState.getInstance();
        if (gs.getChoice(KEY_MODE, "intro").equals(MODE_NAME)) {
            gs.putChoice(KEY_MODE, "intro");         // Level 10 asked for the name prompt only
            startNamePrompt();
        } else {
            startBedroom();
        }
    }
 
    /** Shows one sub-panel by its position in the Navigator (no card names needed). */
    private void showScene(int index) {
        current = index;
        CardLayout cards = (CardLayout) getLayout();
        cards.first(this);
        for (int i = 0; i < index; i++) {
            cards.next(this);
        }
    }
    // ------------------------------------------------------------ Scene 0A: bedroom
    /** Draws text with the hardcoded pixel font onto a bubble image. */
     
    private ImageIcon extendDown(ImageIcon icon, int extra) {
        int w = icon.getIconWidth();
        int h = icon.getIconHeight();
        BufferedImage src = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = src.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();

        int last = h - 1;                        // last row that has any visible pixel
        while (last > 0) {
            boolean any = false;
            for (int x = 0; x < w && !any; x++) {
                any = (src.getRGB(x, last) >>> 24) > 0;
            }
            if (any) {
                break;
            }
            last--;
        }

        BufferedImage out = new BufferedImage(w, h + extra, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.drawImage(src, 0, 0, null);
        g2.dispose();
        for (int y = last + 1; y < h + extra; y++) {
            for (int x = 0; x < w; x++) {
                out.setRGB(x, y, src.getRGB(x, last));
            }
        }
        return new ImageIcon(out);
    }

    /** Draws text with the hardcoded pixel font onto a bubble image. */
    private ImageIcon pixelBubble(String text, int width, int scale, Color fg, Color bg) {
        int pad = 4 * scale;
        int advance = 6 * scale;                 // 5 pixels + 1 gap
        int lineHeight = 10 * scale;             // 7 pixels + 3 gap
        int maxChars = (width - 2 * pad) / advance;

        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String word : text.toUpperCase().split(" ")) {
            if (line.length() > 0 && line.length() + 1 + word.length() > maxChars) {
                lines.add(line.toString());
                line = new StringBuilder();
            }
            if (line.length() > 0) {
                line.append(' ');
            }
            line.append(word);
        }
        lines.add(line.toString());

        int height = 2 * pad + lines.size() * lineHeight - 3 * scale;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(bg);
        g.fillRect(0, 0, width, height);
        g.setColor(fg);
        for (int li = 0; li < lines.size(); li++) {
            String s = lines.get(li);
            for (int ci = 0; ci < s.length(); ci++) {
                String[] rows = GLYPHS.get(s.charAt(ci));
                if (rows == null) {
                    continue;
                }
                int x0 = pad + ci * advance;
                int y0 = pad + li * lineHeight;
                for (int r = 0; r < 7; r++) {
                    for (int c = 0; c < 5; c++) {
                        if (rows[r].charAt(c) == '#') {
                            g.fillRect(x0 + c * scale, y0 + r * scale, scale, scale);
                        }
                    }
                }
            }
        }
        g.setColor(fg);
        g.drawRect(0, 0, width - 1, height - 1);
        g.dispose();
        return new ImageIcon(img);
    }
    
    private void buildPhones() {
    int extra = 130;
    lblPhoneLock.setIcon(extendDown((ImageIcon) lblPhoneLock.getIcon(), extra));
    lblPhoneMsgs.setIcon(extendDown((ImageIcon) lblPhoneMsgs.getIcon(), extra));
    pnlBedroom.add(lblPhoneLock, new AbsoluteConstraints(PHONE_X, PHONE_Y, 430, 430 + extra));
    pnlBedroom.add(lblPhoneMsgs, new AbsoluteConstraints(PHONE_X, PHONE_Y, 430, 430 + extra));
    pnlBedroom.setComponentZOrder(lblPhoneLock, 0);
    pnlBedroom.setComponentZOrder(lblPhoneMsgs, 0);

    btnNotif = new JButton();
    btnNotif.setOpaque(false);
    btnNotif.setContentAreaFilled(false);
    btnNotif.setFocusPainted(false);
    btnNotif.setBorderPainted(false);
    btnNotif.addActionListener(e -> openMessage());
    pnlBedroom.add(btnNotif, new AbsoluteConstraints(0, 0, 1000, 600));
    pnlBedroom.setComponentZOrder(btnNotif, 0);

    ImageIcon bubble = pixelBubble(Story.P0A_MOM_TEXT, 202, 3,
            new Color(0x2B2B3B), Color.WHITE);
    lblMomText = new JLabel(bubble);
    pnlBedroom.add(lblMomText, new AbsoluteConstraints(PHONE_X + 87, PHONE_Y + 87,
            bubble.getIconWidth(), bubble.getIconHeight()));
    pnlBedroom.setComponentZOrder(lblMomText, 0);
    hidePhones();

    lblTornPhoto.setVisible(false);                   // hidden until the photo is clicked
    pnlBedroom.setComponentZOrder(lblTornPhoto, 0);   // last, so it is in front of everything above
}
   
    
    private void styleTutorial() {
        lblTutorial.setOpaque(true);
     lblTutorial.setBackground(new Color(0x0A0F1A));
    lblTutorial.setForeground(Color.WHITE);
    lblTutorial.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    lblTutorial.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0xE0A100), 1));
    pnlBedroom.add(lblTutorial, new AbsoluteConstraints(150, 12, 700, 30));
    pnlBedroom.setComponentZOrder(lblTutorial, 0);
}
    
    private void stylePrompt() {
        lblPrompt.setOpaque(true);
        lblPrompt.setBackground(new Color(0x0A0F1A));
        lblPrompt.setForeground(Color.WHITE);
        lblPrompt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPrompt.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0xE0A100), 1));
        pnlNamePrompt.add(lblPrompt, new AbsoluteConstraints(390, 165, 300, 28));
        pnlNamePrompt.setComponentZOrder(lblPrompt, 0);   // in front of the phone image
    }
    
    private void showLockScreen() {
        lblPhoneLock.setVisible(true);
        lblPhoneMsgs.setVisible(false);
        lblMomText.setVisible(false);
        btnNotif.setVisible(true);
    }
 
    private void showMessages() {
        lblPhoneLock.setVisible(false);
        lblPhoneMsgs.setVisible(true);
        lblMomText.setVisible(true);
        btnNotif.setVisible(false);
    }
 
    private void hidePhones() {
        lblPhoneLock.setVisible(false);
        lblPhoneMsgs.setVisible(false);
        lblMomText.setVisible(false);
        btnNotif.setVisible(false);
    }
 
    private void startBedroom() {
        showScene(SCENE_BEDROOM);
        TimerManager.pause();                       // the clock waits: it starts in the classroom
        GameManager.setTimerVisible(false);         // no timer in the bedroom
        collected = 0;
        awaitingOpen = false;
        busy = true;                                // clicks wait until the first line ends
        for (JButton b : new JButton[] { btnPhone, btnPhoto, btnBag }) {
            b.setVisible(true);
            b.setBorderPainted(false);              // clear any leftover glow
        }
        btnContinueA.setVisible(false);
        lblTutorial.setText(Story.P0A_TUTORIAL);
 
        hidePhones();
        lblTornPhoto.setVisible(false); 
        scene(Story.P0A_INTRO, () -> {
            busy = false;
        });
    }
    
    private void collect(JButton button, String[][] lines, String item) {
        collect(button, lines, item, null);
    }
 
     private void collect(JButton button, String[][] lines, String item, Runnable extra) {
        if (busy) {
            return;
        }
        busy = true;
        collected++;
        button.setVisible(false);
        GameState.getInstance().addItem(item);
        scene(lines, () -> {
            if (extra != null) {
                extra.run();
            }
            busy = false;
            if (collected == 3) {
                // all three collected: go straight to the classroom
                javax.swing.SwingUtilities.invokeLater(() -> Fade.through(this, null, this::startClassroom));
            }
        });
    }
    // ------------------------------------------------------------ Scene 0B: classroom

    private void startClassroom() {
        showScene(SCENE_CLASSROOM);
        lblTornPhoto.setVisible(false);
        profSpeaking = false;
        profConfused = false;
        refreshProf();
        keyFx.cancel();
        btnContinueB.setVisible(false);               // the scene changes by itself
        scene(Story.P0B_CLASS, () -> {
            scene(Story.P0B_KEY, () -> {
                keyFx.play();                         // key appears with the inventory line
                scene(Story.P0B_KEY_ADDED, () -> {
                    GameState gs = GameState.getInstance();
                    gs.addItem(Story.ITEM_ALLI_PHONE);
                    gs.addItem(Story.ITEM_KEY);
                    keyFx.leave(() -> Fade.through(this, null, this::goToPhone));
                });
            });
        });
        GameManager.setTimerVisible(true);
        if (!GameState.getInstance().isGameOver()) {
            TimerManager.resume();
        }
    }
    
    private void goToPhone() {
        startNamePrompt();
        txtNickname.setEnabled(false);                // wait for the intro lines
        btnUnlock.setEnabled(false);
        scene(Story.P0C_INTRO, () -> {
            txtNickname.setEnabled(true);
            btnUnlock.setEnabled(true);
            txtNickname.requestFocusInWindow();
            txtNickname.selectAll();
        });
    }
    
    private void startNamePrompt() {
        showScene(SCENE_NAME);
        lblBgC.setVisible(true);                      // lock screen
        lblPhoneOpen.setVisible(false);               // the opened phone comes after unlocking
        lblPrompt.setVisible(true);
        lblPrompt.setText(Story.P0C_PROMPT);          // no <html> needed anymore
        txtNickname.setVisible(true);
        txtNickname.setText("");                      // empty: no default name
        txtNickname.setEnabled(true);
        btnUnlock.setVisible(true);
        btnUnlock.setEnabled(true);
    }
    
    /** Shrinks a label's icon to w x h, keeping pixel art sharp. */
    private void fitIcon(JLabel lbl, int w, int h) {
    ImageIcon ic = (ImageIcon) lbl.getIcon();
    BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = out.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    g.drawImage(ic.getImage(), 0, 0, w, h, null);
    g.dispose();
    lbl.setIcon(new ImageIcon(out));
}
    
    // ------------------------------------------------------------ POLYMORPHISM
    /** "Prof. Danel [confused]" in the script swaps his picture (classroom only). */
    @Override
    protected void onMood(String mood) {
        if (current != SCENE_CLASSROOM) {
            return;
        }
        profConfused = mood.equals("confused");
        refreshProf();
    }
    
    @Override
    protected void onSpeaker(String speaker) {
        if (current != SCENE_CLASSROOM) {
            return;
        }
        profSpeaking = speaker.startsWith("Prof. Danel");
        if (!profSpeaking) {
            profConfused = false;                 // reset for his next line
        }
        refreshProf();
    }
    
    private void refreshProf() {
        lblProf.setVisible(profSpeaking && !profConfused);
        lblProfConfused.setVisible(profSpeaking && profConfused);
    }
    
    private void buildKeyFx() {
        keyFx = new KeyFx();
        pnlClassroom.add(keyFx, new AbsoluteConstraints(0, 0, 1000, 600));
        pnlClassroom.setComponentZOrder(keyFx, 0);        // in front of everything
    }

    /** The brass key reveal. Position and size come from lblKey in the Design tab. */
    private class KeyFx extends JComponent {
        private static final int EXIT_X = 950;             // where it flies to (top right)
        private static final int EXIT_Y = 25;

        private BufferedImage img;
        private int cx;
        private int cy;
        private double u;
        private javax.swing.Timer timer;
        private long t0;
        private long exitT0;
        private int phase;                                 // 0 off, 1 showing, 2 leaving
        private Runnable afterExit;

        KeyFx() {
            setVisible(false);
        }

        private void prepare() {
            java.awt.Rectangle b = lblKey.getBounds();
            if (b.width < 10 || b.height < 10) {
                b = new java.awt.Rectangle(400, 190, 200, 90);
            }
            cx = b.x + b.width / 2;
            cy = b.y + b.height / 2;

            BufferedImage src = Theme.trimmedImage("brass_key_small.png");
            if (src == null) {
                src = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            }
            double fit = Math.min((double) b.width / src.getWidth(), (double) b.height / src.getHeight());
            int w = Math.max(1, (int) Math.round(src.getWidth() * fit));
            int h = Math.max(1, (int) Math.round(src.getHeight() * fit));
            img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.drawImage(src, 0, 0, w, h, null);
            g.dispose();
            u = w / 264.0;
        }

        void play() {
            cancel();
            prepare();
            t0 = System.nanoTime();
            phase = 1;
            setVisible(true);
            timer = new javax.swing.Timer(16, e -> tick());
            timer.start();
        }

        void leave(Runnable after) {
            if (phase != 1) {
                if (after != null) {
                    after.run();
                }
                return;
            }
            phase = 2;
            exitT0 = System.nanoTime();
            afterExit = after;
        }

        void cancel() {
            if (timer != null) {
                timer.stop();
                timer = null;
            }
            phase = 0;
            setVisible(false);
        }

        private void tick() {
            if (phase == 2 && (System.nanoTime() - exitT0) / 1e6 >= 600) {
                Runnable after = afterExit;
                afterExit = null;
                cancel();
                pnlClassroom.repaint();
                if (after != null) {
                    after.run();
                }
                return;
            }
            pnlClassroom.repaint();
        }

        private double clamp(double v) {
            return Math.max(0, Math.min(1, v));
        }

        private double easeOut(double t) {
            return 1 - Math.pow(1 - t, 3);
        }

        private double easeOutBack(double t) {
            double c1 = 1.70158;
            double c3 = c1 + 1;
            return 1 + c3 * Math.pow(t - 1, 3) + c1 * Math.pow(t - 1, 2);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            if (phase == 0 || img == null) {
                return;
            }
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            long now = System.nanoTime();
            double ms = (now - t0) / 1e6;
            double in = clamp(ms / 700.0);
            double out = phase == 2 ? clamp((now - exitT0) / 1e6 / 600.0) : 0;
            double leave = out * out;

            g.setColor(new Color(0, 0, 0, (int) (120 * easeOut(in) * (1 - out))));
            g.fillRect(0, 0, getWidth(), getHeight());

            double scale = (0.2 + 0.8 * easeOutBack(in)) * (1 - 0.85 * leave);
            double rot = -(1 - easeOut(in)) * 0.6 + leave * 6.28;
            double bob = Math.sin(ms * 0.004) * 6 * u * in;
            double x = cx + (EXIT_X - cx) * leave;
            double y = cy - (1 - easeOut(in)) * 90 * u + bob + (EXIT_Y - cy) * leave;
            float alpha = (float) clamp(clamp(in * 1.5) * (1 - out * out * out));

            float r = (float) (190 * u * scale * (1 + 0.06 * Math.sin(ms * 0.006)));
            if (r > 1) {
                g.setPaint(new RadialGradientPaint(new Point2D.Double(x, y), r,
                        new float[] { 0f, 1f },
                        new Color[] { new Color(255, 200, 60, (int) (150 * alpha)),
                                      new Color(255, 200, 60, 0) }));
                g.fillOval((int) (x - r), (int) (y - r), (int) (2 * r), (int) (2 * r));
            }

            if (out == 0) {
                int sw = (int) (190 * u * scale);
                g.setColor(new Color(0, 0, 0, (int) (70 * alpha)));
                g.fillOval(cx - sw / 2, cy + (int) (img.getHeight() / 2.0 + 12 * u), sw, 14);
            }

            if (in > 0.2) {
                double burst = easeOut(clamp((in - 0.2) / 0.8));
                for (int i = 0; i < 12; i++) {
                    double ang = i * Math.PI / 6 + ms * 0.0006;
                    double dist = (95 + 35 * (i % 3)) * u * burst * scale;
                    double tw = 0.5 + 0.5 * Math.sin(ms * 0.008 + i * 1.7);
                    int sx = (int) (x + Math.cos(ang) * dist * 1.5);
                    int sy = (int) (y + Math.sin(ang) * dist * 0.8);
                    int s = 3 + (i % 3) * 2;
                    g.setColor(new Color(255, 236, 150, (int) (255 * tw * alpha)));
                    g.fillRect(sx - s * 3 / 2, sy - s / 2, s * 3, s);
                    g.fillRect(sx - s / 2, sy - s * 3 / 2, s, s * 3);
                }
            }

            g.setComposite(AlphaComposite.SrcOver.derive(alpha));
            AffineTransform at = new AffineTransform();
            at.translate(x, y);
            at.rotate(rot);
            at.scale(scale, scale);
            at.translate(-img.getWidth() / 2.0, -img.getHeight() / 2.0);
            g.drawImage(img, at, null);
            g.dispose();
        }
    }
    
        /** The key drops in, bounces three times, then floats gently. */
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlBedroom = new javax.swing.JPanel();
        lblTutorial = new javax.swing.JLabel();
        btnContinueA = new javax.swing.JButton();
        btnPhone = new javax.swing.JButton();
        btnPhoto = new javax.swing.JButton();
        btnBag = new javax.swing.JButton();
        lblPhoneLock = new javax.swing.JLabel();
        lblPhoneMsgs = new javax.swing.JLabel();
        lblTornPhoto = new javax.swing.JLabel();
        lblBgA = new javax.swing.JLabel();
        pnlClassroom = new javax.swing.JPanel();
        btnContinueB = new javax.swing.JButton();
        lblKey = new javax.swing.JLabel();
        lblProfConfused = new javax.swing.JLabel();
        lblProf = new javax.swing.JLabel();
        lblBgB = new javax.swing.JLabel();
        pnlNamePrompt = new javax.swing.JPanel();
        txtNickname = new javax.swing.JTextField();
        btnUnlock = new javax.swing.JButton();
        lblPrompt = new javax.swing.JLabel();
        lblBgC = new javax.swing.JLabel();
        lblPhoneOpen = new javax.swing.JLabel();
        lblBgCRoom = new javax.swing.JLabel();

        setAlignmentX(1000.0F);
        setAlignmentY(580.0F);
        setPreferredSize(new java.awt.Dimension(1000, 580));
        setLayout(new java.awt.CardLayout());

        pnlBedroom.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        pnlBedroom.add(lblTutorial, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 15, 500, 26));

        btnContinueA.addActionListener(this::btnContinueAActionPerformed);
        pnlBedroom.add(btnContinueA, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 15, -1, -1));

        btnPhone.addActionListener(this::btnPhoneActionPerformed);
        pnlBedroom.add(btnPhone, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 318, 90, 42));

        btnPhoto.addActionListener(this::btnPhotoActionPerformed);
        pnlBedroom.add(btnPhoto, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 212, 32, 52));

        btnBag.addActionListener(this::btnBagActionPerformed);
        pnlBedroom.add(btnBag, new org.netbeans.lib.awtextra.AbsoluteConstraints(805, 410, 135, 158));

        lblPhoneLock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/arnie_phone.png"))); // NOI18N
        pnlBedroom.add(lblPhoneLock, new org.netbeans.lib.awtextra.AbsoluteConstraints(288, 45, 430, 430));

        lblPhoneMsgs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/arnie_msgs.png"))); // NOI18N
        pnlBedroom.add(lblPhoneMsgs, new org.netbeans.lib.awtextra.AbsoluteConstraints(288, 45, 430, 430));

        lblTornPhoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/torn_photo_small.png"))); // NOI18N
        pnlBedroom.add(lblTornPhoto, new org.netbeans.lib.awtextra.AbsoluteConstraints(328, 40, 344, 392));

        lblBgA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/room_bedroom.png"))); // NOI18N
        lblBgA.setText("jLabel1");
        lblBgA.setAlignmentX(1000.0F);
        lblBgA.setAlignmentY(580.0F);
        pnlBedroom.add(lblBgA, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 600));

        add(pnlBedroom, "card2");

        pnlClassroom.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnContinueB.addActionListener(this::btnContinueBActionPerformed);
        pnlClassroom.add(btnContinueB, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 530, 120, 36));

        lblKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/brass_key_small.png"))); // NOI18N
        pnlClassroom.add(lblKey, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 190, 200, 90));

        lblProfConfused.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/danel_confused.png"))); // NOI18N
        pnlClassroom.add(lblProfConfused, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 340, 430));

        lblProf.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/danel_neutral.png"))); // NOI18N
        pnlClassroom.add(lblProf, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 340, 430));

        lblBgB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/room_classroom.png"))); // NOI18N
        pnlClassroom.add(lblBgB, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 600));

        add(pnlClassroom, "classroom");

        pnlNamePrompt.setBackground(new java.awt.Color(39, 82, 58));
        pnlNamePrompt.setAlignmentX(1000.0F);
        pnlNamePrompt.setAlignmentY(600.0F);
        pnlNamePrompt.setPreferredSize(new java.awt.Dimension(1000, 600));
        pnlNamePrompt.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtNickname.addActionListener(this::txtNicknameActionPerformed);
        pnlNamePrompt.add(txtNickname, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 264, 270, 50));

        btnUnlock.setText("Unlock");
        btnUnlock.addActionListener(this::btnUnlockActionPerformed);
        pnlNamePrompt.add(btnUnlock, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 330, 140, 36));
        pnlNamePrompt.add(lblPrompt, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 195, 90));

        lblBgC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/alli_lockscreen_580_transparent.png"))); // NOI18N
        lblBgC.setAlignmentX(580.0F);
        lblBgC.setAlignmentY(580.0F);
        pnlNamePrompt.add(lblBgC, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 10, 580, 580));

        lblPhoneOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/alli_phone_580_transparent.png"))); // NOI18N
        lblPhoneOpen.setAlignmentX(580.0F);
        lblPhoneOpen.setAlignmentY(580.0F);
        pnlNamePrompt.add(lblPhoneOpen, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 0, 580, 580));

        lblBgCRoom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/room_classroom.png"))); // NOI18N
        lblBgCRoom.setAlignmentX(1000.0F);
        lblBgCRoom.setAlignmentY(580.0F);
        pnlNamePrompt.add(lblBgCRoom, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 600));

        add(pnlNamePrompt, "card4");
    }// </editor-fold>//GEN-END:initComponents
    
    
    private void btnPhoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPhoneActionPerformed
        if (busy) {
            return;
        }
        busy = true;                                  // stays true so nothing else can be clicked
        showLockScreen();                             // 7:02 with the Alli's Mom notification
        scene(Story.P0A_PHONE_LOCK, () -> {           // Arnie: "Alli's mom?"
            awaitingOpen = true;                      // now any click opens the message
        });  
    }//GEN-LAST:event_btnPhoneActionPerformed
    
    private void openMessage() {
        if (!awaitingOpen) {
        return;
    }
    awaitingOpen = false;
    busy = false;
    showMessages();
    collect(btnPhone, Story.P0A_PHONE, Story.ITEM_OWN_PHONE, this::hidePhones);
    }
    
    private void btnContinueBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContinueBActionPerformed
    keyFx.cancel();
    Fade.through(this, null, this::goToPhone);
    }//GEN-LAST:event_btnContinueBActionPerformed

    private void btnPhotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPhotoActionPerformed
        if (busy) {
        return;                                   // do not show the photo while a dialogue is playing
    }
    lblTornPhoto.setVisible(true);                // the photo appears in front of the player
    collect(btnPhoto, Story.P0A_PHOTO, Story.ITEM_TORN_PHOTO,
            () -> lblTornPhoto.setVisible(false)); // gone after "Alli... where are you?"
    }//GEN-LAST:event_btnPhotoActionPerformed

    private void btnUnlockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUnlockActionPerformed
        if (txtNickname.getText().trim().isEmpty()) {
            return;                                   // must type something
        }
        
        String name = txtNickname.getText().trim().toUpperCase();
        if (name.isEmpty()) {
            name = DEFAULT_NAME;
        }
        if (name.length() > MAX_LETTERS) {
            name = name.substring(0, MAX_LETTERS);
        }
        GameState.getInstance().setNickname(name);    // any answer unlocks the phone
 
        lblBgC.setVisible(false);                     // the phone opens: 9:02 with the notification
        lblPhoneOpen.setVisible(true);
        lblPrompt.setVisible(false);
        txtNickname.setVisible(false);
        btnUnlock.setVisible(false);
        busy = true;
        scene(Story.P0C_OPEN, () -> Fade.through(this,
        "Break time. You hide Alli's phone under your desk.", LevelManager::nextLevel));
    }//GEN-LAST:event_btnUnlockActionPerformed

    private void txtNicknameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNicknameActionPerformed
        if (busy) {
    return;
}
        btnUnlockActionPerformed(evt);
    }//GEN-LAST:event_txtNicknameActionPerformed

    private void btnContinueAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContinueAActionPerformed
        startClassroom();
    }//GEN-LAST:event_btnContinueAActionPerformed

    private void btnBagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBagActionPerformed
        if (collected < 2) {
            return;                                   // pick the phone and the photo first
        }
        collect(btnBag, Story.P0A_BAG, Story.ITEM_BAG);
    }//GEN-LAST:event_btnBagActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBag;
    private javax.swing.JButton btnContinueA;
    private javax.swing.JButton btnContinueB;
    private javax.swing.JButton btnPhone;
    private javax.swing.JButton btnPhoto;
    private javax.swing.JButton btnUnlock;
    private javax.swing.JLabel lblBgA;
    private javax.swing.JLabel lblBgB;
    private javax.swing.JLabel lblBgC;
    private javax.swing.JLabel lblBgCRoom;
    private javax.swing.JLabel lblKey;
    private javax.swing.JLabel lblPhoneLock;
    private javax.swing.JLabel lblPhoneMsgs;
    private javax.swing.JLabel lblPhoneOpen;
    private javax.swing.JLabel lblProf;
    private javax.swing.JLabel lblProfConfused;
    private javax.swing.JLabel lblPrompt;
    private javax.swing.JLabel lblTornPhoto;
    private javax.swing.JLabel lblTutorial;
    private javax.swing.JPanel pnlBedroom;
    private javax.swing.JPanel pnlClassroom;
    private javax.swing.JPanel pnlNamePrompt;
    private javax.swing.JTextField txtNickname;
    // End of variables declaration//GEN-END:variables
}
