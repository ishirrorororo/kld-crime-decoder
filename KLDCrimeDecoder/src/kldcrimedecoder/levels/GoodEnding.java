/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package kldcrimedecoder.levels;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.border.LineBorder;
import kldcrimedecoder.engine.GameManager;
import kldcrimedecoder.engine.GameState;
import kldcrimedecoder.ui.DialogueBox;
import kldcrimedecoder.ui.ScenePlayer;
import kldcrimedecoder.ui.Theme;
/**
 *
 * @author Rosaldo
 */
public class GoodEnding extends javax.swing.JPanel {
    // background pictures (the files are in the images folder)
    private static final String BG_BATHROOM    = "bathroom_last.png";    
    private static final String BG_HALLWAY  = "room_hallway.png";
    private static final String BG_ALLI_OUT = "alliOut.png";      // Alli in the dark room
    private static final String ITEM_KEY    = "brass_key.png";    // pops up on the first line
 
    // rank by score, change the numbers if the ranks feel too easy or too hard
    private static final int RANK_MASTER = 1200;
    private static final int RANK_DETECTIVE = 800;
    private static final int RANK_JUNIOR = 400;
 
    // {bg:file.png} at the start of a line changes the background
    private static final String[][] STORY = {
        {"Arnie [scared]", "{bg:" + BG_BATHROOM + "}{item:" + ITEM_KEY + "}The key. It was in my bag the whole time. The reflection thins, then goes."},
        {"", "{bg:" + BG_HALLWAY + "}You walk back to the closet and open the small door behind the shelves with the brass key."},
        {"", "{bg:" + BG_ALLI_OUT + "} Alli steps out, blinking in the light."},
        {"Alli [furious]", "{bg:" + BG_HALLWAY + "}You locked me in a closet, Arnie."},
        {"Arnie [sad]", "I know. I'm so sorry. I didn't know why until now."},
        {"Alli [angry]", "Don't say it like it fixes anything."},
        {"Alli [sad]", "I'm not ready to forgive you. But I'm not leaving either."},
        {"Arnie [neutral]", "I'll talk to the guidance counselor today. All of it."},
        {"Alli [neutral]", "Good. I'll walk you there."},
        {"", "Some things can't be fixed in a day. But she stayed to listen."}
    };
 
    private ScenePlayer player;     // plays the story on this screen
 

    public GoodEnding() {
        initComponents();
        // use the same dark theme as the other screens
        Theme.background(this);
        Theme.heading(lblEndTitle);
        Theme.label(lblScore, lblRank, lblAwareness);
        Theme.button(btnMenu);
        lblEndTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblScore.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRank.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAwareness.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
 
        // the final box with the results, hidden until the story is over
        pnlFinal.setOpaque(true);
        pnlFinal.setBackground(Theme.PANEL);
        pnlFinal.setBorder(new LineBorder(Theme.AMBER, 2));
        pnlFinal.setVisible(false);
 
        player = new ScenePlayer(lblBg, lblLeft, lblRight, lblItem);
 
        // start the ending every time this screen is shown
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                startEnding();
            }
        });
    }

   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblLeft = new javax.swing.JLabel();
        lblRight = new javax.swing.JLabel();
        lblItem = new javax.swing.JLabel();
        pnlFinal = new javax.swing.JPanel();
        lblEndTitle = new javax.swing.JLabel();
        lblScore = new javax.swing.JLabel();
        lblRank = new javax.swing.JLabel();
        lblAwareness = new javax.swing.JLabel();
        btnMenu = new javax.swing.JButton();
        lblBg = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        add(lblLeft, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 165, 220, 300));
        add(lblRight, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 165, 220, 300));
        add(lblItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 80, 200, 200));

        pnlFinal.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblEndTitle.setText("jLabel1");
        lblEndTitle.setPreferredSize(new java.awt.Dimension(500, 40));
        pnlFinal.add(lblEndTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 15, 500, 40));

        lblScore.setText("jLabel1");
        lblScore.setPreferredSize(new java.awt.Dimension(500, 30));
        pnlFinal.add(lblScore, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 500, 30));

        lblRank.setText("jLabel1");
        pnlFinal.add(lblRank, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 120, 500, 30));

        lblAwareness.setText("jLabel1");
        pnlFinal.add(lblAwareness, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 160, 500, 30));

        btnMenu.setText("Back to Menu");
        btnMenu.addActionListener(this::btnMenuActionPerformed);
        pnlFinal.add(btnMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 210, 150, 32));

        add(pnlFinal, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 60, 500, 260));

        lblBg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kldcrimedecoder/images/room_hallway.png"))); // NOI18N
        lblBg.setToolTipText("");
        add(lblBg, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 580));
    }// </editor-fold>//GEN-END:initComponents

    private void btnMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuActionPerformed
        GameManager.backToMenu();
    }//GEN-LAST:event_btnMenuActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMenu;
    private javax.swing.JLabel lblAwareness;
    private javax.swing.JLabel lblBg;
    private javax.swing.JLabel lblEndTitle;
    private javax.swing.JLabel lblItem;
    private javax.swing.JLabel lblLeft;
    private javax.swing.JLabel lblRank;
    private javax.swing.JLabel lblRight;
    private javax.swing.JLabel lblScore;
    private javax.swing.JPanel pnlFinal;
    // End of variables declaration//GEN-END:variables

    // plays the story first, then shows the results
    private void startEnding() {
        pnlFinal.setVisible(false);
        player.hideSprites();
        player.showBackground(BG_HALLWAY);
        player.play(STORY, this::showFinalScreen);
    }
 
    private void showFinalScreen() {
        DialogueBox box = GameManager.getDialogue();
        if (box != null) {
            box.hideBox();                      // close the dialogue box
        }
        player.hideSprites();
        GameState gs = GameState.getInstance();
        lblEndTitle.setText("Case Closed");
        lblScore.setText("Score: " + gs.getScore());
        lblRank.setText("Rank: " + getRank(gs.getScore()));
        lblAwareness.setText("Self-Awareness: " + getSelfAwareness(gs.getDenialMeter()) + "%");
        pnlFinal.setVisible(true);
    }
 
    private String getRank(int score) {
        if (score >= RANK_MASTER) {
            return "Master Detective";
        } else if (score >= RANK_DETECTIVE) {
            return "Detective";
        } else if (score >= RANK_JUNIOR) {
            return "Junior Investigator";
        }
        return "Rookie";
    }
 
    // the denial meter is 0 to 50 here (50 or more is the bad ending), so 0 denial = 100%
    private int getSelfAwareness(int denial) {
        return Math.max(0, Math.min(100, 100 - denial * 2));
    }
}
