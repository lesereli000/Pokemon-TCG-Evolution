package main;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class PlayerHandlerTest {
    @Test
    public void testSetupPlayers() {

        PlayerHandler playerHandler = new PlayerHandler();
        playerHandler.createPlayers();
        Player player1 = playerHandler.player1;
        Player player2 = playerHandler.player2;
        assertEquals("Player 1", player1.getName());
        assertEquals("Player 2", player2.getName());
    }

    @Test
    public void testCompletePlayerSetupHeads() {
        PlayerHandler handler = new PlayerHandler();
        handler.completePlayerSetup("Heads");

        assertEquals("Player 1", handler.getCurrentPlayer().getName());
        assertEquals(1, handler.getPlayerTurn());
    }

    @Test
    public void testCompletePlayerSetupTails() {
        PlayerHandler handler = new PlayerHandler();
        handler.completePlayerSetup("Tails");

        assertEquals("Player 2", handler.getCurrentPlayer().getName());
        assertEquals(2, handler.getPlayerTurn());
    }


    @Test
    public void testSetupBothDecks() {
        Player player1 = createMock(Player.class);
        Player player2 = createMock(Player.class);
        player1.createCustomDeck();
        player2.createCustomDeck();
        replay(player1, player2);

        PlayerHandler handler = new PlayerHandler();
        handler.player1 = player1;
        handler.player2 = player2;

        handler.setupBothDecks();
        verify(player1, player2);
    }

    @Test
    public void testAddToBench() {
        Player player = createMock(Player.class);
        Pokemon pokemon = createMock(Pokemon.class);

        player.addBenchPokemon(pokemon);
        player.removeFromHand(pokemon);
        replay(player, pokemon);

        PlayerHandler handler = new PlayerHandler();
        handler.currentPlayer = player;
        handler.addToBench(pokemon);

        verify(player, pokemon);
    }




    @Test
    public void testPlayerHand() {
        Player player1 = createMock(Player.class);
        Player player2 = createMock(Player.class);
        player1.drawStartingHand();
        player2.drawStartingHand();
        replay(player1, player2);

        PlayerHandler handler = new PlayerHandler();
        handler.player1 = player1;
        handler.player2 = player2;
        handler.setupBothHands();
        verify(player1, player2);
    }

    @Test
    public void testPlayerTurnResultHeads() {
        Player player1 = createMock(Player.class);
        Player player2 = createMock(Player.class);

        PlayerHandler handler = new PlayerHandler();
        handler.player1 = player1;
        handler.player2 = player2;
        handler.setPlayerTurns("Heads");
        assertEquals(player1, handler.currentPlayer);
        assertEquals(player2, handler.defendingPlayer);
    }

    @Test
    public void testPlayerTurnResultTails() {
        Player player1 = createMock(Player.class);
        Player player2 = createMock(Player.class);

        PlayerHandler handler = new PlayerHandler();
        handler.player1 = player1;
        handler.player2 = player2;
        handler.setPlayerTurns("Tails");
        assertEquals(player2, handler.currentPlayer);
        assertEquals(player1, handler.defendingPlayer);
    }

    @Test
    public void testGetCurrentPlayerHand() {
        Player player1 = createMock(Player.class);
        ArrayList<Card> hand = createMock(ArrayList.class);

        PlayerHandler playerHandler = new PlayerHandler();
        playerHandler.currentPlayer = player1;
        expect(player1.handAsList()).andReturn(hand);
        replay(player1);

        ArrayList<Card> actual = playerHandler.getCurrentPlayerHand();
        assertEquals(hand, actual);

        verify(player1);

    }

}
