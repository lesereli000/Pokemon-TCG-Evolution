package main;

import org.easymock.EasyMock;
import org.junit.Test;
import static org.junit.Assert.*;

import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

public class GhostingDragAdapterTest {

    @Test
    public void testSuccessfulDropOnActiveZone() {
        // Mock the GUI
        GameGUI mockGui = EasyMock.createNiceMock(GameGUI.class);
        
        // Mock a Card
        Card mockCard = new Pokemon("Pikachu", "Lightning", 0, 60);
        
        // Initialize detector
        BoardPositionMap map = new BoardPositionMap(1200, 900);
        CardDropZoneDetector detector = new CardDropZoneDetector(map, mockGui);
        
        // The adapter
        GhostingDragAdapter adapter = new GhostingDragAdapter(mockGui, mockCard, detector);
        
        // Setup expectations on GUI mock
        mockGui.setLastSelectedCardForDrag(mockCard);
        EasyMock.expectLastCall().once();
        
        // "InstantAttach" payload, or "MakeActive" depending on Game layer, but we can verify the trigger string maps
        mockGui.triggerSimulatedAction("P1_ACTIVE_DROP");
        EasyMock.expectLastCall().once();
        
        EasyMock.replay(mockGui);
        
        // Simulate dragging to P1_ACTIVE
        JButton sourceButton = new JButton();
        
        java.awt.Rectangle activeRect = map.getZones().get(DropZoneType.P1_ACTIVE);
        Point centerOfActive = new Point(activeRect.x + activeRect.width / 2, activeRect.y + activeRect.height / 2);
        
        System.out.println("TEST Active Rect: " + activeRect);
        System.out.println("TEST centerOfActive: " + centerOfActive);
        System.out.println("TEST getZoneFromPoint: " + detector.getZoneFromPoint(centerOfActive));
        
        // Mouse event coords relative to source button, but GhostingDragAdapter converts to screen
        // We will just directly test the drop handling if we can, or simulate the event
        MouseEvent e = new MouseEvent(sourceButton, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, 
                centerOfActive.x, centerOfActive.y, 1, false);
        
        // Setting event source manually might be tricky if adapter uses e.getLocationOnScreen()
        // since component is not visible. 
        // We will invoke a package private method on adapter for testing: handleDrop(Point p)
        adapter.mousePressed(e);
        adapter.handleDrop(centerOfActive);
        
        EasyMock.verify(mockGui);
    }
    
    @Test
    public void testDropOnInvalidSpace() {
        GameGUI mockGui = EasyMock.createNiceMock(GameGUI.class);
        Card mockCard = new Pokemon("Pikachu", "Lightning", 0, 60);
        BoardPositionMap map = new BoardPositionMap(1200, 900);
        CardDropZoneDetector detector = new CardDropZoneDetector(map, mockGui);
        
        GhostingDragAdapter adapter = new GhostingDragAdapter(mockGui, mockCard, detector);
        
        // Expect BOARD_DROP for non-targeted drop in invalid space
        mockGui.triggerSimulatedAction("BOARD_DROP");
        EasyMock.expectLastCall().once();
        EasyMock.replay(mockGui);
        
        Point voidPoint = new Point(0, 0); // Top left, background
        adapter.handleDrop(voidPoint);
        
        EasyMock.verify(mockGui);
    }
}
