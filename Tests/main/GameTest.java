package main;

import org.junit.Test;

import java.util.Random;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class GameTest {

    // CURRENTLY BROKEN
    // TODO: Fix Tests by Mocking GUI, (Do after adding GUI interface)

    @Test
    public void testFlipCoinHeads() {
        Random rand = createMock(Random.class);
        expect(rand.nextBoolean()).andReturn(true).anyTimes();
        replay(rand);

        GUI gui = createMock(GUI.class);
        Game game = new Game(gui, rand, new Player("Player 1"), new Player("Player 2"));
        assertEquals("Heads", game.flipCoin());

        verify(rand);
    }

    @Test
    public void testFlipCoinTails() {
        Random rand = createMock(Random.class);
        expect(rand.nextBoolean()).andReturn(false).anyTimes();
        replay(rand);

        GUI gui = createMock(GUI.class);
        Game game = new Game(gui, rand, new Player("Player 1"), new Player("Player 2"));
        assertEquals("Tails", game.flipCoin());

        verify(rand);
    }


    @Test
    public void testDeckSetup() {
        Random rand = new Random();
        GUI gui = createMock(GUI.class);

        Player p1 = createMock(Player.class);
        Player p2 = createMock(Player.class);

        p1.createFullDeck(rand);
        p1.drawStartingHand();
        p2.createFullDeck(rand);
        p2.drawStartingHand();
        replay(p1, p2);

        Game game = new Game(gui, rand, p1 ,p2);
        game.setupDecks();

        verify(p1);
        verify(p2);
    }

    @Test
    public void testSelectNotPokemonToActive() {
        Random rand = createMock(Random.class);
        GUI gui = createMock(GUI.class);
        Player p1 = createMock(Player.class);
        Player p2 = createMock(Player.class);
        Card card = new Energy("Grass Energy");
        expect(gui.getLastSelectedCard()).andReturn(card).anyTimes();
        gui.displayMessage("Basic Pokemon has not been selected!");
        replay(gui);
        Game game = new Game(gui, rand, p1, p2, true);
        game.makeActiveCard();
        verify(gui);
    }
    @Test
    public void testSelectNotBasicPokemonToActive() {
        Random rand = createMock(Random.class);
        GUI gui = createMock(GUI.class);
        Player p1 = createMock(Player.class);
        Player p2 = createMock(Player.class);
        Card card = new Pokemon("Beedrill", "Grass", 1, 100, 'f', 'p',1);
        expect(gui.getLastSelectedCard()).andReturn(card).anyTimes();
        gui.displayMessage("Basic Pokemon has not been selected!");
        replay(gui);
        Game game = new Game(gui, rand, p1, p2, true);
        game.makeActiveCard();
        verify(gui);
    }
//    @Test //Doesn't work because of Runnables
//    public void testSelectBasicPokemonToActive() {
//        Random rand = createMock(Random.class);
//        GUI gui = createMock(GUI.class);
//        Player p1 = createMock(Player.class);
//        Player p2 = createMock(Player.class);
//        Card card = new Pokemon("Weedle", "Grass", 0, 60, 'f', 'p',0);
//        expect(gui.getLastSelectedCard()).andReturn(card).anyTimes();
//        gui.makeActiveCard(card,1);
//        p1.setActivePokemon((Pokemon) card);
//        gui.removeAllButtons();
    // Runnables happen here
//        gui.displayMessage("Basic Pokemon has not been selected!");
//        replay(gui);
//        Game game = new Game(gui, rand, p1, p2, true);
//        game.makeActiveCard();
//        verify(gui);
//    }
}
