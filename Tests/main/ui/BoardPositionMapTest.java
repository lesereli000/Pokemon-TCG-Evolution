package main.ui;

import org.junit.Test;
import java.awt.Rectangle;
import java.util.Map;
import static org.junit.Assert.*;

public class BoardPositionMapTest {

    @Test
    public void testP1ActiveGeometries() {
        BoardPositionMap positionMap = new BoardPositionMap();
        Map<DropZoneType, Rectangle> zones = positionMap.getZones();

        Rectangle p1Active = zones.get(DropZoneType.P1_ACTIVE);
        assertNotNull("P1_ACTIVE DropZone Rectangle should exist", p1Active);

        // Expected calculations based on original BoardPanel constants
        // cardWidth = (1200 * 2) / 25 = 96
        // cardHeight = 96 * 7 / 5 = 134
        // activeVerticalMargin = 134 / 16 = 8
        // activeVerticalOffset = (900 / 16) - 100 = 56 - 100 = -44
        // P1 Active x = (1200 / 2) - (96 / 2) = 600 - 48 = 552
        // P1 Active y = (900 / 2) + 8 - (-44) = 450 + 8 + 44 = 502
        assertEquals("X coordinate of P1 active slot", 552, p1Active.x);
        assertEquals("Y coordinate of P1 active slot", 502, p1Active.y);
        assertEquals(96, p1Active.width);
        assertEquals(134, p1Active.height);
    }

    @Test
    public void testP1BenchGeometries() {
        BoardPositionMap positionMap = new BoardPositionMap();
        Map<DropZoneType, Rectangle> zones = positionMap.getZones();

        Rectangle p1Bench0 = zones.get(DropZoneType.P1_BENCH_0);
        assertNotNull(p1Bench0);

        // Expected calculations
        // marginSide = 40
        // marginBottom = 75
        // benchHorizontalOffset = 1200 / 19 = 63
        // benchVerticalOffset = (900 / 8) - 100 = 112 - 100 = 12
        // benchHorizontalIncrement = 134 / 6 = 22
        // P1 Bench 0 x = 60 + 192 + 63 = 315
        // P1 Bench 0 y = 900 - 134 - 75 - 12 = 679
        assertEquals("X coordinate of P1 bench slot 0", 315, p1Bench0.x);
        assertEquals("Y coordinate of P1 bench slot 0", 679, p1Bench0.y);
        assertEquals(96, p1Bench0.width);
        assertEquals(134, p1Bench0.height);

        Rectangle p1Bench1 = zones.get(DropZoneType.P1_BENCH_1);
        assertEquals(315 + (118), p1Bench1.x); // 315 + (22 + 96) = 433
    }
}
