package main;

import org.junit.Test;

import static org.easymock.EasyMock.*;

public class MainTest {

    @Test
    public void testStartGame() {
        GameGUI gui = createMock(GameGUI.class);
        Game game = createMock(Game.class);

        gui.createGUI();
        game.setupGame();

        replay(gui, game);

        Main.startGame(gui, game);

        verify(gui, game);
    }
}
