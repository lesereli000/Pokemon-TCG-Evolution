package main;

import java.awt.Rectangle;
import java.util.EnumMap;
import java.util.Map;

public class BoardPositionMap {
    private final Map<DropZoneType, Rectangle> zones;

    public BoardPositionMap(int frameWidth, int frameHeight) {
        zones = new EnumMap<>(DropZoneType.class);
        
        int cardWidth = (frameWidth * 2) / 25;
        int cardHeight = cardWidth * 7 / 5;
        int marginSide = 40;
        int marginTop = 180;
        int marginBottom = 75;
        
        int benchHorizontalOffset = frameWidth / 19;
        int benchHorizontalIncrement = cardHeight / 6;
        int benchVerticalOffset = (frameHeight / 8) - 100;
        
        int activeVerticalOffset = (frameHeight / 16) - 100;
        int activeVerticalMargin = cardHeight / 16;
        
        // P1 Active
        int p1ActiveX = (frameWidth / 2) - (cardWidth / 2);
        int p1ActiveY = (frameHeight / 2) + activeVerticalMargin - activeVerticalOffset;
        zones.put(DropZoneType.P1_ACTIVE, new Rectangle(p1ActiveX, p1ActiveY, cardWidth, cardHeight));
        
        // P2 Active
        int p2ActiveX = (frameWidth / 2) - (cardWidth / 2);
        int p2ActiveY = (frameHeight / 2) - activeVerticalMargin - cardHeight - activeVerticalOffset;
        zones.put(DropZoneType.P2_ACTIVE, new Rectangle(p2ActiveX, p2ActiveY, cardWidth, cardHeight));
        
        // P1 Bench Slots
        for (int i = 0; i < 5; i++) {
            int x = (marginSide * 3) / 2 + (cardWidth * 2) + benchHorizontalOffset
                    + (i * (benchHorizontalIncrement + cardWidth));
            int y = frameHeight - cardHeight - marginBottom - benchVerticalOffset;
            zones.put(DropZoneType.valueOf("P1_BENCH_" + i), new Rectangle(x, y, cardWidth, cardHeight));
        }
        
        // P2 Bench Slots
        for (int i = 0; i < 5; i++) {
            int x = frameWidth - (marginSide * 3) / 2 - (cardWidth * 3) - benchHorizontalOffset
                    - (i * (benchHorizontalIncrement + cardWidth));
            int y = marginTop;
            zones.put(DropZoneType.valueOf("P2_BENCH_" + i), new Rectangle(x, y, cardWidth, cardHeight));
        }
    }

    public Map<DropZoneType, Rectangle> getZones() {
        return zones;
    }
}
