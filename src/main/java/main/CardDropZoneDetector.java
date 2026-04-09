package main;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Map;

public class CardDropZoneDetector {
    
    private final BoardPositionMap positionMap;

    public CardDropZoneDetector(BoardPositionMap positionMap) {
        this.positionMap = positionMap;
    }

    public BoardPositionMap getPositionMap() {
        return positionMap;
    }

    public DropZoneType getZoneFromPoint(Point p) {
        Map<DropZoneType, Rectangle> zones = positionMap.getZones();
        
        for (Map.Entry<DropZoneType, Rectangle> entry : zones.entrySet()) {
            if (entry.getValue().contains(p)) {
                return entry.getKey();
            }
        }
        
        return DropZoneType.NONE;
    }
}
