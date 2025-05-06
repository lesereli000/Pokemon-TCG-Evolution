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

    @Test
    public void testFlipCoinTails() {
        Random rand = createMock(Random.class);
        GameGUI gui = createMock(GameGUI.class);
        expect(rand.nextBoolean()).andReturn(false);
        replay(rand);

        Game game = new Game(gui);
        String flipResult = game.flipCoin(rand);
        assertEquals("Tails", flipResult);

        verify(rand);
    }

    @Test
    public void testSetupPlayers() {
        GameGUI gui = createMock(GameGUI.class);

        Game game = new Game(gui);
        game.createPlayers();
        Player player1 = game.player1;
        Player player2 = game.player2;
        assertEquals("Player 1", player1.getName());
        assertEquals("Player 2", player2.getName());
    }

    @Test
    public void testSetupBothDecks() {
        GameGUI gui = createMock(GameGUI.class);
        Player player1 = createMock(Player.class);
        Player player2 = createMock(Player.class);
        player1.createCustomDeck();
        player2.createCustomDeck();
        replay(player1, player2);

        Game game = new Game(gui);
        game.player1 = player1;
        game.player2 = player2;

        game.setupBothDecks();
        verify(player1, player2);
    }
}
