package kldcrimedecoder.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * One slot of the padlock. It holds at most one DigitTile.
 * It hides how it stores the tile: the level only asks isEmpty(),
 * getDigit(), releaseTo() and freeze().
 */
public class LockSlot extends JPanel {

    private final Runnable onFilled;

    /** onFilled runs after a tile has landed in this slot. */
    public LockSlot(Runnable onFilled) {
        super(new GridLayout(1, 1));
        this.onFilled = onFilled;
        setPreferredSize(new Dimension(56, 64));
        setBackground(new Color(0x182033));
        setBorder(BorderFactory.createLineBorder(Theme.AMBER, 2));
        DragDropHelper.makeDropTarget(this, (label, target) -> accept(label));
    }

    /** Decides if a dropped label may stay. DragDropHelper moves it only if this returns true. */
    private boolean accept(JLabel label) {
        if (!(label instanceof DigitTile) || !isEnabled() || !isEmpty()) {
            return false;                          // frozen, full, or not a tile: it jumps back
        }
        if (onFilled != null) {
            SwingUtilities.invokeLater(onFilled);  // run AFTER the tile has moved in
        }
        return true;
    }

    public boolean isEmpty() {
        return getComponentCount() == 0;
    }

    /** The digit in this slot, or '_' if the slot is empty. */
    public char getDigit() {
        return isEmpty() ? '_' : ((DigitTile) getComponent(0)).getDigit();
    }

    /** Sends the tile (if any) back to the tray. add() takes it out of this slot. */
    public void releaseTo(Container tray) {
        if (!isEmpty()) {
            tray.add(getComponent(0));
        }
    }

    /** After the puzzle is solved: the slot takes no more drops and its tile cannot be dragged. */
    public void freeze() {
        setEnabled(false);
        if (!isEmpty()) {
            DragDropHelper.lock((JLabel) getComponent(0));
        }
    }
}
