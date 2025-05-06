package main;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

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
        game.setupFlipButton();

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

    @Test
    public void testPlayerHand() {
        GameGUI gui = createMock(GameGUI.class);
        Player player1 = createMock(Player.class);
        Player player2 = createMock(Player.class);
        player1.drawStartingHand();
        player2.drawStartingHand();
        replay(player1, player2);

        Game game = new Game(gui);
        game.player1 = player1;
        game.player2 = player2;
        game.setupBothHands();
        verify(player1, player2);
    }

    @Test
    public void testPlayerTurnResultHeads() {
        GameGUI gui = createMock(GameGUI.class);
        Player player1 = createMock(Player.class);
        Player player2 = createMock(Player.class);

        Game game = new Game(gui);
        game.player1 = player1;
        game.player2 = player2;
        game.setPlayerTurns("Heads");
        assertEquals(player1, game.currentPlayer);
        assertEquals(player2, game.defendingPlayer);
    }

    @Test
    public void testPlayerTurnResultTails() {
        GameGUI gui = createMock(GameGUI.class);
        Player player1 = createMock(Player.class);
        Player player2 = createMock(Player.class);

        Game game = new Game(gui);
        game.player1 = player1;
        game.player2 = player2;
        game.setPlayerTurns("Tails");
        assertEquals(player2, game.currentPlayer);
        assertEquals(player1, game.defendingPlayer);
    }

    @Test
    public void testDisplayPlayerHand() {
        GameGUI gui = createMock(GameGUI.class);
        gui.createGUI();
        Player player1 = createMock(Player.class);
        ArrayList<Card> cards = new ArrayList<>();
        expect(player1.handAsList()).andReturn(cards);
        expect(gui.displayCards(cards)).andReturn(null);
        replay(gui, player1);

        Game game = new Game(gui);
        game.currentPlayer = player1;
        game.displayCurrentPlayerHand();

        verify(gui, player1);
    }

    @Test
    public void testDirectionActivePokemon() {
        GameGUI gui = createMock(GameGUI.class);
        gui.createGUI();
        gui.displayMessage("Select a basic Pokemon to be your Active Pokemon");
        replay(gui);

        Game game = new Game(gui);
        game.displayActiveDirections();
        verify(gui);
    }

    @Test
    public void testSelectActivePokemon() {
        GameGUI gui = createMock(GameGUI.class);
        gui.createGUI();
        Pokemon p = createMock(Pokemon.class);
        Player player = createMock(Player.class);
        player.setActivePokemon(p);

        replay(gui, player);

        Game game = new Game(gui);
        game.currentPlayer = player;
        game.makeNewActivePokemon(p);
        verify(gui, player);
    }

    @Test
    public void testCheckBasicPokemonFalse() {
        GameGUI gui = createMock(GameGUI.class);
        Pokemon p = createMock(Pokemon.class);
        expect(p.getStage()).andReturn(1);
        replay(p);

        Game game = new Game(gui);
        boolean output = game.checkBasicPokemon(p);
        assertFalse(output);

        verify(p);
    }

    @Test
    public void testCheckBasicPokemonNotPokemon() {
        GameGUI gui = createMock(GameGUI.class);
        Energy e = createMock(Energy.class);

        Game game = new Game(gui);
        boolean output = game.checkBasicPokemon(e);
        assertFalse(output);
    }

    @Test
    public void testCheckBasicPokemonTrainer() {
        GameGUI gui = createMock(GameGUI.class);
        Trainer t = createMock(Trainer.class);

        Game game = new Game(gui);
        boolean output = game.checkBasicPokemon(t);
        assertFalse(output);
    }

    @Test
    public void testCheckBasicPokemonTrue() {
        GameGUI gui = createMock(GameGUI.class);
        Pokemon p = createMock(Pokemon.class);
        expect(p.getStage()).andReturn(0);
        replay(p);

        Game game = new Game(gui);
        boolean output = game.checkBasicPokemon(p);
        assertTrue(output);

        verify(p);
    }

    @Test
    public void testCantAddToBench() {
        GameGUI gui = createMock(GameGUI.class);
        gui.createGUI();
        Pokemon p = createMock(Pokemon.class);
        expect(p.getStage()).andReturn(1);
        gui.displayMessage("This is not a basic Pokemon and can not place card on bench!");
        replay(p, gui);

        Game game = new Game(gui);
        game.selectCard(p);

        verify(p, gui);
    }

    @Test
    public void testAddCardToBench() {
        GameGUI gui = createMock(GameGUI.class);
        gui.createGUI();
        Pokemon p = createMock(Pokemon.class);
        Player player = createMock(Player.class);
        player.addBenchPokemon(p);
        expect(p.getStage()).andReturn(0);
        replay(p, player, gui);

        Game game = new Game(gui);
        game.currentPlayer = player;
        game.selectCard(p);

        verify(player, p, gui);
    }

    @Test
    public void testCantAddEnergy() {
        GameGUI gui = createMock(GameGUI.class);
        Player player = createMock(Player.class);
        expect(player.canAddEnergy()).andReturn(false);
        gui.createGUI();
        gui.displayMessage("Can only add one energy per turn!");
        replay(gui,player);
        Energy e = createMock(Energy.class);

        Game game = new Game(gui);
        game.currentPlayer = player;
        game.selectCard(e);
        verify(gui, player);
    }

    @Test
    public void testSuccessAddingEnergy() {
        GameGUI gui = createMock(GameGUI.class);
        Player player = createMock(Player.class);
        expect(player.canAddEnergy()).andReturn(true);

        Pokemon p = createMock(Pokemon.class);
        ArrayList<Card> pokemon = new ArrayList<>();
        pokemon.add(p);
        pokemon.add(p);
        expect(player.getOnlyPokemonFromHand()).andReturn(pokemon);

        gui.createGUI();
        expect(gui.displayCards(pokemon)).andReturn(null);
        gui.displayMessage("Select Pokemon to add card to");

        replay(gui,player);
        Energy e = createMock(Energy.class);

        Game game = new Game(gui);
        game.currentPlayer = player;
        game.selectCard(e);
        verify(gui, player);
    }

}
