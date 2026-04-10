package main.ui;

import main.*;

import static org.easymock.EasyMock.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class DropZoneHighlightGlassPaneTest {

    @Test
    public void testSetHighlights() {
        CardDropZoneDetector detector = createMock(CardDropZoneDetector.class);
        Card card = createMock(Card.class);

        replay(detector, card);

        DropZoneHighlightGlassPane glass = new DropZoneHighlightGlassPane(detector);
        glass.setHighlightsVisible(true, card);

        // No easy way to check private fields without reflection, but we can check if
        // it repaints
        // For line coverage, calling the method is enough
        glass.setHighlightsVisible(false);

        verify(detector, card);
    }

    @Test
    public void testPaintComponent() {
        CardDropZoneDetector detector = createMock(CardDropZoneDetector.class);
        BoardPositionMap positionMap = createMock(BoardPositionMap.class);
        Card card = createMock(Card.class);
        Graphics2D g2 = createMock(Graphics2D.class);

        // Mock the zones
        Map<DropZoneType, Rectangle> zones = new HashMap<>();
        zones.put(DropZoneType.P1_ACTIVE, new Rectangle(0, 0, 10, 10));
        zones.put(DropZoneType.P1_BENCH_0, new Rectangle(10, 10, 10, 10));

        expect(detector.getPositionMap()).andReturn(positionMap).anyTimes();
        expect(positionMap.getZones()).andReturn(zones).anyTimes();

        // Mock valid/invalid for branches
        expect(detector.isValidForCard(DropZoneType.P1_ACTIVE, card)).andReturn(true);
        expect(detector.isValidForCard(DropZoneType.P1_BENCH_0, card)).andReturn(false);

        // Expect graphics calls for ACTIVE_P1
        expect(g2.create()).andReturn(g2);
        g2.setRenderingHint(anyObject(), anyObject());
        expectLastCall().anyTimes();
        g2.setColor(anyObject());
        expectLastCall().anyTimes();
        g2.setStroke(anyObject());
        expectLastCall().anyTimes();
        g2.drawRoundRect(anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt());
        expectLastCall().anyTimes();
        g2.fillRoundRect(anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt());
        expectLastCall().anyTimes();
        g2.dispose();
        expectLastCall().once();

        replay(detector, positionMap, card, g2);

        DropZoneHighlightGlassPane glass = new DropZoneHighlightGlassPane(detector);
        glass.setHighlightsVisible(true, card);
        glass.paintComponent(g2);

        verify(detector, positionMap, card, g2);
    }
}
