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
        replay(p1,p2);

        Game game = new Game(gui, rand,p1 ,p2);
        game.setupDecks();

        verify(p1);
        verify(p2);
    }

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
