package kldcrimedecoder.ui;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

/**
 * Drag a JLabel from one JPanel and drop it into another.
 * Used by Level 4 (and maybe Level 8).
 *   makeDraggable(label)           -> the label can be dragged
 *   makeDropTarget(panel, listener) -> the panel accepts labels
 *   lock(label)                    -> the label can no longer be dragged
 */
public class DragDropHelper {

    /** Return true to accept the drop (the label then moves into the panel). */
    public interface DropListener {
        boolean onDrop(JLabel label, JPanel target);
    }

    private static final DataFlavor LABEL_FLAVOR = new DataFlavor(JLabel.class, "JLabel");

    /** Carries the label itself while it is being dragged. */
    private static class LabelTransferable implements Transferable {
        private final JLabel label;

        LabelTransferable(JLabel label) {
            this.label = label;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] { LABEL_FLAVOR };
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return LABEL_FLAVOR.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return label;
        }
    }

    private static final TransferHandler DRAG_SOURCE = new TransferHandler() {
        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            return new LabelTransferable((JLabel) c);
        }
    };

    /** Starts the drag when the mouse moves with the button held down. */
    private static final MouseAdapter DRAG_STARTER = new MouseAdapter() {
        @Override
        public void mouseDragged(MouseEvent e) {
            JComponent c = (JComponent) e.getSource();
            TransferHandler th = c.getTransferHandler();
            if (th != null) {                       // null = locked label
                th.exportAsDrag(c, e, TransferHandler.MOVE);
            }
        }
    };

    public static void makeDraggable(JLabel label) {
    label.setTransferHandler(DRAG_SOURCE);
    label.setDropTarget(null);      // drops on this label go to the panel behind it
    label.addMouseMotionListener(DRAG_STARTER);
    label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
}

    public static void lock(JLabel label) {
        label.setTransferHandler(null);
        label.setCursor(Cursor.getDefaultCursor());
    }

    public static void makeDropTarget(final JPanel panel, final DropListener listener) {
        panel.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(LABEL_FLAVOR);
            }

            @Override
            public boolean importData(TransferSupport support) {
                if (!canImport(support)) {
                    return false;
                }
                try {
                    JLabel label = (JLabel) support.getTransferable().getTransferData(LABEL_FLAVOR);
                    if (!listener.onDrop(label, panel)) {
                        return false;               // rejected: label stays where it was
                    }
                    Container old = label.getParent();
                    panel.add(label);               // add() removes it from the old panel
                    if (old != null) {
                        old.revalidate();
                        old.repaint();
                    }
                    panel.revalidate();
                    panel.repaint();
                    return true;
                } catch (UnsupportedFlavorException | IOException ex) {
                    return false;
                }
            }
        });
    }
}