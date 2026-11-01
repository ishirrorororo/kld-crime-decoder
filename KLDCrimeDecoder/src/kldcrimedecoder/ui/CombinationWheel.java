
package kldcrimedecoder.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/** A draggable digit wheel for the four-number combination padlock. */
public class CombinationWheel extends JPanel {

    private int digit;

    private final JLabel value =
            new JLabel("0", SwingConstants.CENTER);

    private final Runnable onChange;

    private int lastY;

    private int dragDistance;

    private static final int DRAG_THRESHOLD = 12;

    public CombinationWheel(Runnable onChange) {
        super(new BorderLayout());

        this.onChange = onChange;

        setOpaque(true);
        setBackground(new Color(0x2B1A08));

        setBorder(BorderFactory.createLineBorder(
                new Color(0xF5C542), 1));

        value.setOpaque(true);
        value.setBackground(new Color(0xF2C65D));
        value.setForeground(new Color(0x241707));

        value.setFont(new Font(
                "Consolas", Font.BOLD, 25));

        value.setBorder(BorderFactory.createMatteBorder(
                1, 0, 1, 0, new Color(0x80520C)));

        add(value, BorderLayout.CENTER);

        MouseAdapter dragListener = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (!isEnabled()) return;

                lastY = e.getYOnScreen();
                dragDistance = 0;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (!isEnabled()) return;

                int currentY = e.getYOnScreen();

                int difference = lastY - currentY;

                dragDistance += difference;

                while (dragDistance >= DRAG_THRESHOLD) {
                    step(1);
                    dragDistance -= DRAG_THRESHOLD;
                }

                while (dragDistance <= -DRAG_THRESHOLD) {
                    step(-1);
                    dragDistance += DRAG_THRESHOLD;
                }

                lastY = currentY;
            }
        };

        addMouseListener(dragListener);
        addMouseMotionListener(dragListener);

        value.addMouseListener(dragListener);
        value.addMouseMotionListener(dragListener);

        setPreferredSize(new Dimension(42, 82));
        setCursor(new java.awt.Cursor(
                java.awt.Cursor.N_RESIZE_CURSOR));
    }

    private void step(int amount) {

        if (!isEnabled()) return;

        digit = (digit + amount + 10) % 10;

        refresh();

        if (onChange != null) {
            onChange.run();
        }
    }

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

        if (value != null) {
            value.setForeground(enabled
                    ? new Color(0x241707)
                    : new Color(0x6D5B3A));

            value.setBackground(enabled
                    ? new Color(0xF2C65D)
                    : new Color(0xB5A47D));
        }
    }
}
