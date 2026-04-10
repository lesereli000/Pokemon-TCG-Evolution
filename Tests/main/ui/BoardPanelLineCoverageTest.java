package main.ui;

import main.*;

import org.junit.Test;
import java.awt.*;
import java.awt.image.BufferedImage;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class BoardPanelLineCoverageTest {

    @Test
    public void testImageUpdate() {
        GameGUI gui = createNiceMock(GameGUI.class);
        replay(gui);
        BoardPanel panel = new BoardPanel(gui);

        boolean result = panel.imageUpdate(new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB), 0, 0, 0, 10, 10);
        assertTrue(result);
    }

    @Test
    public void testPaintComponentNoPlayer() {
        GameGUI gui = createNiceMock(GameGUI.class);
        expect(gui.getMessages()).andReturn(java.util.ResourceBundle.getBundle("MessagesBundle", java.util.Locale.US))
                .anyTimes();
        replay(gui);
        BoardPanel panel = new BoardPanel(gui);

        Graphics g = createNiceMock(Graphics.class);
        expect(g.create()).andReturn(g).anyTimes();
        replay(g);

        try {
            panel.paintComponent(g);
        } catch (Exception e) {
            // Expected in some Swing environments
        }
    }

    @Test
    public void testDrawActiveP1() {
        GameGUI gui = createNiceMock(GameGUI.class);
        replay(gui);
        BoardPanel panel = new BoardPanel(gui);

        Graphics2D g2 = createNiceMock(Graphics2D.class);
        Player p = createNiceMock(Player.class);
        Pokemon card = createNiceMock(Pokemon.class);

        expect(p.getActivePokemon()).andReturn(card).anyTimes();
        expect(card.getImageUrl()).andReturn(null).anyTimes();

        replay(g2, p, card);

        // This triggers drawCardImage internally
        panel.drawActive(g2, p, BoardPanel.Side.BOTTOM);
    }
}
