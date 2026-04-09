package main;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class GhostingDragAdapter extends MouseAdapter {

    private final GameGUI gui;
    private final Card card;
    private final CardDropZoneDetector detector;
    private JLabel ghostLabel;
    private final int cardWidth;
    private final int cardHeight;

    public GhostingDragAdapter(GameGUI gui, Card card, CardDropZoneDetector detector) {
        this.gui = gui;
        this.card = card;
        this.detector = detector;
        this.cardWidth = (GameGUI.frameWidth * 2) / 25;
        this.cardHeight = cardWidth * 7 / 5;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        gui.setLastSelectedCardForDrag(card);
        
        JFrame frame = gui.getFrame();
        if (frame == null) return;

        DropZoneHighlightGlassPane glass = (DropZoneHighlightGlassPane) frame.getGlassPane();
        glass.setLayout(null);
        glass.setHighlightsVisible(true, card);
        glass.setVisible(true);

        String url = card.getImageUrl();
        ghostLabel = new JLabel(card.getName());
        ghostLabel.setSize(cardWidth, cardHeight);
        ghostLabel.setOpaque(true);
        ghostLabel.setBackground(new Color(255, 255, 255, 128));
        ghostLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        if (url != null) {
            BufferedImage img = ImageLoader.getImage(url, glass);
            if (img != null) {
                Image scaled = img.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH);
                ghostLabel.setIcon(new ImageIcon(scaled));
                ghostLabel.setText("");
            }
        }
        
        glass.add(ghostLabel);
        updateGhostPosition(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        updateGhostPosition(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        JFrame frame = gui.getFrame();
        if (frame != null) {
            DropZoneHighlightGlassPane glass = (DropZoneHighlightGlassPane) frame.getGlassPane();
            if (ghostLabel != null) {
                glass.remove(ghostLabel);
                ghostLabel = null;
            }
            glass.setHighlightsVisible(false);
            glass.setVisible(false);
            glass.repaint();
        }

        Point releasePoint = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), frame);
        handleDrop(releasePoint);
    }
    
    private void updateGhostPosition(MouseEvent e) {
        if (ghostLabel == null) return;
        JFrame frame = gui.getFrame();
        Point p = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), frame.getGlassPane());
        ghostLabel.setLocation(p.x - cardWidth / 2, p.y - cardHeight / 2);
        frame.getGlassPane().repaint();
    }
    
    void handleDrop(Point globalDropPoint) {
        DropZoneType zone = detector.getZoneFromPoint(globalDropPoint);
        
        if (zone != DropZoneType.NONE) {
            String intent = zone.name() + "_DROP";
            gui.triggerSimulatedAction(intent);
        } else {
            // Generic board drop for non-targeted actions (e.g. Draw 2 Trainers)
            gui.triggerSimulatedAction("BOARD_DROP");
        }
    }
}
