package kldcrimedecoder.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.event.MouseWheelListener;

/**
 * A single digit wheel for the four-number combination padlock.
 * The wheel keeps its digit private (encapsulation) and handles its own drops.
 */
public class CombinationWheel extends JPanel {

    private int digit;

    private final JLabel value =
            new JLabel("0", SwingConstants.CENTER);

    private final JButton up = new JButton("▲");
    private final JButton down = new JButton("▼");

    private final Runnable onChange;

    /** For the NetBeans designer, which needs a constructor with no arguments. */
    public CombinationWheel() {
        this(null);
    }

    public CombinationWheel(Runnable onChange) {
        super(new BorderLayout(0, 0));

        this.onChange = onChange;

        setOpaque(true);
        setBackground(new Color(0x2B1A08));
        setBorder(BorderFactory.createLineBorder(
                new Color(0xF5C542), 1));

        styleArrow(up);
        styleArrow(down);

        value.setOpaque(true);
        value.setBackground(new Color(0xF2C65D));
        value.setForeground(new Color(0x241707));
        value.setFont(new Font("Consolas", Font.BOLD, 25));
        value.setBorder(BorderFactory.createMatteBorder(
                1, 0, 1, 0, new Color(0x80520C)));

        up.addActionListener(e -> step(1));
        down.addActionListener(e -> step(-1));

        add(up, BorderLayout.NORTH);
        add(value, BorderLayout.CENTER);
        add(down, BorderLayout.SOUTH);
        
        MouseWheelListener onScroll = e -> {
            int rotation = e.getWheelRotation();
            if (rotation < 0) {
                step(1);        // scroll up = next digit
            } else if (rotation > 0) {
                step(-1);       // scroll down = previous digit
            }
        };

        addMouseWheelListener(onScroll);
        up.addMouseWheelListener(onScroll);
        down.addMouseWheelListener(onScroll);
        value.addMouseWheelListener(onScroll);

        setPreferredSize(new Dimension(32, 82));
    }

    private void styleArrow(JButton button) {
        button.setFocusable(false);
        button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        button.setFont(new Font("SansSerif", Font.BOLD, 9));
        button.setForeground(new Color(0x3A2305));
        button.setBackground(new Color(0xD99B22));
        button.setBorder(BorderFactory.createEmptyBorder());
    }

    private void step(int amount) {
        if (!isEnabled()) return;

        digit = (digit + amount + 10) % 10;

        refresh();

        if (onChange != null) {
            onChange.run();
        }
    }

    /**
     * A tile dropped on the wheel sets its digit. Returning false makes
     * DragDropHelper send the tile back to the keypad, so it can be reused.
     */

    public int getDigit() {
        return digit;
    }

    public void setDigit(int digit) {
        this.digit = Math.floorMod(digit, 10);
        refresh();
    }

    private void refresh() {
        value.setText(String.valueOf(digit));
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (up != null) up.setEnabled(enabled);
        if (down != null) down.setEnabled(enabled);

        if (value != null) {
            value.setForeground(enabled
                    ? new Color(0x241707)
                    : new Color(0x6D5B3A));
        }
    }
}