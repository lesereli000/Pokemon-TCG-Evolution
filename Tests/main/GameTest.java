package main;

import org.junit.Test;

import java.util.Random;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class GameTest {

    @Test
    public void testGameMakesGUI() {
        GameGUI gui = createMock(GameGUI.class);
        gui.createGUI();
        replay(gui);
        new Game(gui);
        verify(gui);
    }

    @Test
    public void testMakeFlipCoinButton() {
        GameGUI gui = createMock(GameGUI.class);
        gui.createGUI();
        gui.createFlipButton();
        replay(gui);

        Game game = new Game(gui);
        game.setupGame();

        verify(gui);
    }

    @Test
    public void testFlipCoinHeads() {
        Random rand = createMock(Random.class);
        GameGUI gui = createMock(GameGUI.class);
        expect(rand.nextBoolean()).andReturn(true);
        replay(rand);

        Game game = new Game(gui);
        String flipResult = game.flipCoin(rand);
        assertEquals("Heads", flipResult);

        verify(rand);
    }
}
