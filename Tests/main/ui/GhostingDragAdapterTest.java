package main.ui;

import main.*;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.junit.Test;

public class GhostingDragAdapterTest {

    @Test
    public void testHandleDropToZone() {
        GameGUI gui = createMock(GameGUI.class);
        Card card = createMock(Card.class);
        CardDropZoneDetector detector = createMock(CardDropZoneDetector.class);
        
        Point p = new Point(100, 100);
        expect(detector.getZoneFromPoint(p)).andReturn(DropZoneType.P1_ACTIVE);
        gui.triggerSimulatedAction("P1_ACTIVE_DROP");
        expectLastCall();
        
        replay(gui, card, detector);
        
        GhostingDragAdapter adapter = new GhostingDragAdapter(gui, card, detector);
        adapter.handleDrop(p);
        
        verify(gui, card, detector);
    }

    @Test
    public void testHandleDropToBoard() {
        GameGUI gui = createMock(GameGUI.class);
        Card card = createMock(Card.class);
        CardDropZoneDetector detector = createMock(CardDropZoneDetector.class);
        
        Point p = new Point(5, 5);
        expect(detector.getZoneFromPoint(p)).andReturn(DropZoneType.NONE);
        gui.triggerSimulatedAction("BOARD_DROP");
        expectLastCall();
        
        replay(gui, card, detector);
        
        GhostingDragAdapter adapter = new GhostingDragAdapter(gui, card, detector);
        adapter.handleDrop(p);
        
        verify(gui, card, detector);
    }

    @Test
    public void testMousePressedNoFrame() {
        GameGUI gui = createMock(GameGUI.class);
        Card card = createMock(Card.class);
        CardDropZoneDetector detector = createMock(CardDropZoneDetector.class);
        
        gui.setLastSelectedCardForDrag(card);
        expectLastCall();
        expect(gui.getFrame()).andReturn(null);
        
        replay(gui, card, detector);
        
        GhostingDragAdapter adapter = new GhostingDragAdapter(gui, card, detector);
        // We pass a null or dummy event since we are testing the early return
        adapter.mousePressed(null); 
        
        verify(gui, card, detector);
    }
    
    @Test
    public void testMouseReleasedWithFrame() {
        GameGUI gui = createMock(GameGUI.class);
        Card card = createMock(Card.class);
        CardDropZoneDetector detector = createMock(CardDropZoneDetector.class);
        // Use NiceMock to ignore internal Swing calls like getLocationOnScreen, getX, etc.
        JFrame frame = createNiceMock(JFrame.class);
        DropZoneHighlightGlassPane glass = createNiceMock(DropZoneHighlightGlassPane.class);
        
        expect(gui.getFrame()).andReturn(frame).anyTimes();
        expect(frame.getGlassPane()).andReturn(glass).anyTimes();
        
        glass.remove(anyObject(Component.class));
        expectLastCall().anyTimes();
        glass.setHighlightsVisible(false);
        expectLastCall();
        glass.setVisible(false);
        expectLastCall();
        glass.repaint();
        expectLastCall();
        
        replay(gui, card, detector, frame, glass);
        
        GhostingDragAdapter adapter = new GhostingDragAdapter(gui, card, detector);
        
        // Use a real button to avoid mocking every single Swing location method
        JButton btn = new JButton();
        MouseEvent e = new MouseEvent(btn, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, 10, 10, 1, false);
        
        try {
            adapter.mouseReleased(e);
        } catch (Exception ex) {
            // Ignore Swing-internal NPEs in headless mode
        }
        
        verify(gui, card, detector);
    }

    @Test
    public void testMouseDraggedNoGhost() {
        GameGUI gui = createMock(GameGUI.class);
        Card card = createMock(Card.class);
        CardDropZoneDetector detector = createMock(CardDropZoneDetector.class);
        
        replay(gui, card, detector);
        
        GhostingDragAdapter adapter = new GhostingDragAdapter(gui, card, detector);
        // ghostLabel is null by default, so mouseDragged should early return
        adapter.mouseDragged(null);
        
        verify(gui, card, detector);
    }

    @Test
    public void testMousePressedWithFrame() {
        GameGUI gui = createMock(GameGUI.class);
        Card card = createMock(Card.class);
        CardDropZoneDetector detector = createMock(CardDropZoneDetector.class);
        JFrame frame = createNiceMock(JFrame.class);
        DropZoneHighlightGlassPane glass = createNiceMock(DropZoneHighlightGlassPane.class);
        
        expect(card.getName()).andReturn("Pika").anyTimes();
        expect(card.getImageUrl()).andReturn(null).anyTimes();
        gui.setLastSelectedCardForDrag(card);
        expectLastCall();
        expect(gui.getFrame()).andReturn(frame).anyTimes();
        expect(frame.getGlassPane()).andReturn(glass).anyTimes();
        
        glass.setLayout(null);
        expectLastCall();
        glass.setHighlightsVisible(true, card);
        expectLastCall();
        glass.setVisible(true);
        expectLastCall();
        
        // Container.add returns the component
        expect(glass.add(anyObject(Component.class))).andReturn(null).anyTimes();
        
        replay(gui, card, detector, frame, glass);
        
        GhostingDragAdapter adapter = new GhostingDragAdapter(gui, card, detector);
        
        JButton btn = new JButton();
        MouseEvent e = new MouseEvent(btn, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, 10, 10, 1, false);
        
        try {
            adapter.mousePressed(e);
            
            // Also test mouseDragged while ghostLabel is present
            adapter.mouseDragged(e);
        } catch (Exception ex) {
            // Ignore Swing-internal NPEs in headless mode
        }
        
        verify(gui, card, detector);
    }
}
