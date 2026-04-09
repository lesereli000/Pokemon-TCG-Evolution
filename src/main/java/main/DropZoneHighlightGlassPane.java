package main;

import java.awt.*;
import java.util.Map;
import javax.swing.*;

/**
 * A custom GlassPane that renders visual highlights over valid drop zones.
 * Used during drag-and-drop operations to give spatial feedback.
 */
public class DropZoneHighlightGlassPane extends JPanel {
    private final CardDropZoneDetector detector;
    private boolean highlightsVisible = false;
    private Card draggedCard = null;
    
    // Premium electric blue color palette
    private final Color zoneFillColor = new Color(0, 150, 255, 40);
    private final Color zoneBorderColor = new Color(0, 180, 255, 180);
    private final Color glowColor = new Color(0, 200, 255, 20);

    public DropZoneHighlightGlassPane(CardDropZoneDetector detector) {
        this.detector = detector;
        setOpaque(false);
    }

    public void setHighlightsVisible(boolean visible, Card card) {
        this.highlightsVisible = visible;
        this.draggedCard = card;
        repaint();
    }

    public void setHighlightsVisible(boolean visible) {
        setHighlightsVisible(visible, null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (!highlightsVisible) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Map<DropZoneType, Rectangle> zones = detector.getPositionMap().getZones();
        
        for (Map.Entry<DropZoneType, Rectangle> entry : zones.entrySet()) {
            DropZoneType type = entry.getKey();
            
            // Check validity using the detector's logic
            if (!detector.isValidForCard(type, draggedCard)) continue;
            
            Rectangle rect = entry.getValue();
            
            // Draw Glow (Outer)
            g2.setColor(glowColor);
            g2.setStroke(new BasicStroke(8f));
            g2.drawRoundRect(rect.x - 4, rect.y - 4, rect.width + 8, rect.height + 8, 15, 15);
            
            // Draw Fill
            g2.setColor(zoneFillColor);
            g2.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 10, 10);
            
            // Draw Border
            g2.setColor(zoneBorderColor);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 10, 10);
        }
        
        g2.dispose();
    }
}
