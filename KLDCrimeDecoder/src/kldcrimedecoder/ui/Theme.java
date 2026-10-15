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

/**
 * One place for colors and fonts. Forms call these in their
 * constructors, after initComponents(), so we never touch the blue code.
 */
public class Theme {

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