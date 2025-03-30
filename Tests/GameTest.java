import org.junit.Test;

import javax.swing.*;
import java.util.Random;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class GameTest {

    //TODO: Tests moved from PokemonTest.java
    // CURRENTLY BROKEN
    // TODO: Fix Tests by Mocking GUI, (Do after adding GUI interface)

    @Test
    public void testFlipCoinHeads() {
        Random rand = createMock(Random.class);
        expect(rand.nextBoolean()).andReturn(true).times(2);
        expect(rand.nextInt()).andReturn(0).anyTimes();
        replay(rand);

        GUI gui = createMock(GUI.class);
        Game game = new Game(gui, rand);
        assertEquals("Heads", game.flipCoin());

        verify(rand);
    }

//    @Test
//    public void testFlipCoinHeads() {
//        Random rand = createMock(Random.class);
//
//        expect(rand.nextBoolean()).andReturn(true);
//
//        replay(rand);
//
//        GameGUI gui = createMock(GameGUI.class);
//
//        replay(gui);
//
//        Game game = new Game(gui, rand);
//        String result = game.flipCoin(rand);
//
//        assertEquals("Heads", result);
//        assertEquals(1, game.currentTurn());
//
//        verify(rand);
//        verify(gui);
//    }

//    @Test
//    public void testFlipCoinTails() {
//        Random rand = createMock(Random.class);
//
//        expect(rand.nextBoolean()).andReturn(false);
//
//        replay(rand);
//
//        Game game = new Game();
//        String result = game.flipCoin(rand);
//
//        assertEquals("Tails", result);
//        assertEquals(2, game.currentTurn());
//
//        verify(rand);
//    }
//
//    @Test
//    public void testFirstPlayersDeck() {
//        Game game = new Game();
//        Deck player1Deck = game.firstDeck();
//
//        assertEquals(60, player1Deck.size());
//    }
//
//    @Test
//    public void testSecondPlayersDeck() {
//        Game game = new Game();
//        Deck player2Deck = game.secondDeck();
//
//        assertEquals(60, player2Deck.size());
//    }
//
//    @Test
//    public void testFirstPlayersHand() {
//        Game game = new Game();
//        Deck player1Deck = game.firstDeck();
//
//        Deck player1Hand = game.player1Hand();
//        assertEquals(7, player1Hand.size());
//    }
//
//    @Test
//    public void testSecondPlayersHand() {
//        Game game = new Game();
//        Deck player2Deck = game.secondDeck();
//        Deck player2Hand = game.player2Hand();
//
//        assertEquals(7, player2Hand.size());
//    }
}
