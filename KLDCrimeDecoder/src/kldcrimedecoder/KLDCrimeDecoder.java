package kldcrimedecoder;

import java.awt.Color;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import kldcrimedecoder.ui.MainFrame;

/**
 * Entry point. Sets the Nimbus look and feel, then opens MainFrame
 * on the Event Dispatch Thread (EDT).
 */
public class KLDCrimeDecoder {

    public static void main(String[] args) {
        // Nimbus theme colors. These must be set BEFORE setLookAndFeel.
        UIManager.put("control", new Color(0x0B1220));              // panel background
        UIManager.put("nimbusBase", new Color(0x1B2A47));           // button base color
        UIManager.put("nimbusLightBackground", new Color(0x111A2E)); // text fields, lists
        UIManager.put("text", new Color(0xE8E8E8));                 // text color
        UIManager.put("nimbusFocus", new Color(0xFFB000));          // amber focus ring
        UIManager.put("nimbusSelectionBackground", new Color(0xFFB000));
        UIManager.put("info", new Color(0x1B2A47));                 // tooltips

        // Use Nimbus if it exists, otherwise keep the default look.
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Nimbus not available: " + e.getMessage());
        }

        // Always build Swing windows on the EDT.
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}