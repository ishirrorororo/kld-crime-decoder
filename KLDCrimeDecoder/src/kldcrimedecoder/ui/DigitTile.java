package kldcrimedecoder.ui;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * One number tile the player can drag. It remembers its own digit,
 * so nobody has to read the digit back out of the label text.
 * It is a JLabel, so DragDropHelper.makeDraggable() works on it directly.
 */
public class DigitTile extends JLabel {

    private final char digit;

    public DigitTile(char digit) {
        super(String.valueOf(digit), SwingConstants.CENTER);
        this.digit = digit;
        setOpaque(true);
        setBackground(new Color(0xD7E3EE));
        setForeground(new Color(0x1F3A93));
        setFont(Theme.MONO_BOLD.deriveFont(24f));
        setBorder(BorderFactory.createLineBorder(Theme.AMBER, 2));
        setPreferredSize(new Dimension(48, 56));
        DragDropHelper.makeDraggable(this);
    }

    public char getDigit() {
        return digit;
    }
}
