package main.ui;

import main.*;

import static org.easymock.EasyMock.*;
import org.junit.Test;
import static org.junit.Assert.*;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Map;

public class CardDropZoneDetectorTest {

    @Test
    public void testValidDropZoneRetrieval() {
        BoardPositionMap mockMap = new BoardPositionMap();
        CardDropZoneDetector detector = new CardDropZoneDetector(mockMap, null);
        
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
        BoardPositionMap mockMap = new BoardPositionMap();
        CardDropZoneDetector detector = new CardDropZoneDetector(mockMap, null);
        
        // Point obviously in empty space
        Point emptySpace = new Point(0, 0);
        assertEquals(DropZoneType.NONE, detector.getZoneFromPoint(emptySpace));
    }

    @Test
    public void testUnauthorizedOpponentZone() {
        BoardPositionMap mockMap = new BoardPositionMap();
        CardDropZoneDetector detector = new CardDropZoneDetector(mockMap, null);
        
        Map<DropZoneType, Rectangle> zones = mockMap.getZones();
        Rectangle p2ActiveRect = zones.get(DropZoneType.P2_ACTIVE);
        
        Point insideActiveP2 = new Point(p2ActiveRect.x + 10, p2ActiveRect.y + 10);
        
        assertEquals(DropZoneType.P2_ACTIVE, detector.getZoneFromPoint(insideActiveP2));
    }

    @Test
    public void testIsValidForCardBasicChecks() {
        GameGUI gui = createMock(GameGUI.class);
        CardDropZoneDetector detector = new CardDropZoneDetector(new BoardPositionMap(), gui);
        
        assertFalse(detector.isValidForCard(DropZoneType.NONE, new Pokemon("Pika", "Lightning", 0, 60)));
        assertFalse(detector.isValidForCard(DropZoneType.P1_ACTIVE, null));
    }

    @Test
    public void testIsValidForCardTurnValidation() {
        GameGUI gui = createMock(GameGUI.class);
        Player p1 = createMock(Player.class);
        CardDropZoneDetector detector = new CardDropZoneDetector(new BoardPositionMap(), gui);
        Pokemon pkmn = new Pokemon("Pika", "Lightning", 0, 60);

        expect(gui.getPlayerTurn()).andReturn(1).anyTimes();
        expect(gui.getPlayer1()).andReturn(p1).anyTimes();
        expect(p1.hasActive()).andReturn(true).anyTimes();
        expect(p1.getActivePokemon()).andReturn(pkmn).anyTimes();
        expect(p1.getPokemonOnBench()).andReturn(new ArrayList<>()).anyTimes(); // Added for retreat check
        replay(gui, p1);

        // Valid: P1 drops on P1 side
        assertTrue(detector.isValidForCard(DropZoneType.P1_ACTIVE, pkmn));
        // Invalid: P1 drops on P2 side
        assertFalse(detector.isValidForCard(DropZoneType.P2_ACTIVE, pkmn));
        verify(gui, p1);
    }

    @Test
    public void testIsValidForCardEnergyPlacement() {
        GameGUI gui = createMock(GameGUI.class);
        Player p1 = createMock(Player.class);
        CardDropZoneDetector detector = new CardDropZoneDetector(new BoardPositionMap(), gui);
        Energy energy = new Energy(EnergyType.FIRE);

        expect(gui.getPlayerTurn()).andReturn(1).anyTimes();
        expect(gui.getPlayer1()).andReturn(p1).anyTimes();
        
        // Scenario 1: Energy on Active (has Pokemon)
        expect(p1.hasActive()).andReturn(true);
        expect(p1.getActivePokemon()).andReturn(new Pokemon("Pika", "Lightning", 0, 60));
        
        // Scenario 2: Energy on Active (no Pokemon)
        expect(p1.hasActive()).andReturn(false);
        
        // Scenario 3: Energy on Bench (has Pokemon)
        ArrayList<Card> bench = new ArrayList<>();
        bench.add(new Pokemon("Squirtle", "Water", 0, 50));
        expect(p1.getPokemonOnBench()).andReturn(bench);
        
        // Scenario 4: Energy on Bench (empty slot)
        expect(p1.getPokemonOnBench()).andReturn(bench);

        replay(gui, p1);

        assertTrue("Energy should be valid on P1 Active if Pokemon present", detector.isValidForCard(DropZoneType.P1_ACTIVE, energy));
        assertFalse("Energy should be invalid on active if no Pokemon present", detector.isValidForCard(DropZoneType.P1_ACTIVE, energy));
        assertTrue("Energy should be valid on occupied bench slot", detector.isValidForCard(DropZoneType.P1_BENCH_0, energy));
        assertFalse("Energy should be invalid on empty bench slot", detector.isValidForCard(DropZoneType.P1_BENCH_1, energy));
        
        verify(gui, p1);
    }

    @Test
    public void testIsValidForCardPokemonPlacement() {
        GameGUI gui = createMock(GameGUI.class);
        Player p1 = createMock(Player.class);
        CardDropZoneDetector detector = new CardDropZoneDetector(new BoardPositionMap(), gui);
        Pokemon pkmn = new Pokemon("Pika", "Lightning", 0, 60);

        expect(gui.getPlayerTurn()).andReturn(1).anyTimes();
        expect(gui.getPlayer1()).andReturn(p1).anyTimes();

        // During setup, only active is valid
        expect(p1.hasActive()).andReturn(false).anyTimes();
        expect(p1.getPokemonOnBench()).andReturn(new ArrayList<>()).anyTimes();
        replay(gui, p1);
        
        assertTrue("Basic Pokemon valid on active during setup", detector.isValidForCard(DropZoneType.P1_ACTIVE, pkmn));
        assertFalse("Basic Pokemon invalid on bench during setup", detector.isValidForCard(DropZoneType.P1_BENCH_0, pkmn));
        verify(gui, p1);
        
        reset(gui, p1);
        expect(gui.getPlayerTurn()).andReturn(1).anyTimes();
        expect(gui.getPlayer1()).andReturn(p1).anyTimes();

        // After setup, next empty bench slot is valid
        expect(p1.hasActive()).andReturn(true).anyTimes();
        expect(p1.getActivePokemon()).andReturn(new Pokemon("Raichu", "Lightning", 1, 100)).anyTimes();
        ArrayList<Card> bench = new ArrayList<>();
        expect(p1.getPokemonOnBench()).andReturn(bench).anyTimes();

        replay(gui, p1);

        assertTrue("Basic Pokemon valid on first empty bench slot", detector.isValidForCard(DropZoneType.P1_BENCH_0, pkmn));
        assertFalse("Basic Pokemon invalid on non-consecutive bench slot", detector.isValidForCard(DropZoneType.P1_BENCH_1, pkmn));
        
        verify(gui, p1);
    }
}
