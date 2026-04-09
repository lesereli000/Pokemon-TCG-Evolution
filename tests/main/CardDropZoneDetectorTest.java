package main;

import org.junit.Test;
import static org.junit.Assert.*;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Map;

public class CardDropZoneDetectorTest {

    @Test
    public void testValidDropZoneRetrieval() {
        BoardPositionMap mockMap = new BoardPositionMap(1200, 900);
        CardDropZoneDetector detector = new CardDropZoneDetector(mockMap);
        
        // Let's get the known rectangle for P1_BENCH_0 to find a good point inside it
        Map<DropZoneType, Rectangle> zones = mockMap.getZones();
        Rectangle p1Bench0Rect = zones.get(DropZoneType.P1_BENCH_0);
        
        Point insideBench0 = new Point(p1Bench0Rect.x + 10, p1Bench0Rect.y + 10);
        assertEquals(DropZoneType.P1_BENCH_0, detector.getZoneFromPoint(insideBench0));
        
        Rectangle p1ActiveRect = zones.get(DropZoneType.P1_ACTIVE);
        Point insideActiveP1 = new Point(p1ActiveRect.x + 20, p1ActiveRect.y + 20);
        assertEquals(DropZoneType.P1_ACTIVE, detector.getZoneFromPoint(insideActiveP1));
    }

    @Test
    public void testInvalidVoidDropZone() {
        BoardPositionMap mockMap = new BoardPositionMap(1200, 900);
        CardDropZoneDetector detector = new CardDropZoneDetector(mockMap);
        
        // Point obviously in empty space
        Point emptySpace = new Point(0, 0);
        assertEquals(DropZoneType.NONE, detector.getZoneFromPoint(emptySpace));
    }

    @Test
    public void testUnauthorizedOpponentZone() {
        BoardPositionMap mockMap = new BoardPositionMap(1200, 900);
        CardDropZoneDetector detector = new CardDropZoneDetector(mockMap);
        
        Map<DropZoneType, Rectangle> zones = mockMap.getZones();
        Rectangle p2ActiveRect = zones.get(DropZoneType.P2_ACTIVE);
        
        Point insideActiveP2 = new Point(p2ActiveRect.x + 10, p2ActiveRect.y + 10);
        
        // Should detect it as P2_ACTIVE. 
        // Note: In Phase 4, Game logic will use this returned P2_ACTIVE to reject the play 
        // since P1 cannot drop on P2's active natively.
        assertEquals(DropZoneType.P2_ACTIVE, detector.getZoneFromPoint(insideActiveP2));
    }
}
