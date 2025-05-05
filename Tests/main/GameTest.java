package main;

import org.junit.Test;

import static org.easymock.EasyMock.*;

public class GameTest {

    @Test
    public void testGameMakesGUI() {
        GameGUI gui = createMock(GameGUI.class);
        gui.createGUI();
        replay(gui);
        Game game = new Game(gui);
        verify(gui);
    }
}
