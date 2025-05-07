package main;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class GameTest {


    @Test
    public void testMakeFlipCoinButton() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler playerHandler = createMock(PlayerHandler.class);
        CardManager cardManager = createMock(CardManager.class);
        gui.createFlipButton();
        replay(gui);

        Game game = new Game(gui, rand, setupGame, playerHandler, cardManager);
        game.setupFlipButton();

        verify(gui);
    }

    @Test
    public void testDisplayPlayerHand() {
        GameGUI gui = createMock(GameGUI.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        CardManager cardManager = createMock(CardManager.class);
        gui.removeAllButtons();
        Random rand = createMock(Random.class);
        Player player1 = createMock(Player.class);
        ArrayList<Card> cards = new ArrayList<>();
        expect(player1.handAsList()).andReturn(cards);
        gui.displayCards(cards);
        expect(handler.getCurrentPlayer()).andReturn(player1);
        replay(gui, player1, handler);

        Game game = new Game(gui, rand, setupGame, handler, cardManager);
        game.displayCurrentPlayerHand();

        verify(gui, player1, handler);
    }

    @Test
    public void testDirectionActivePokemon() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        CardManager cardManager = createMock(CardManager.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        SetupGame setupGame = createMock(SetupGame.class);
        gui.displayMessage("Select a basic Pokemon to be your Active Pokemon");
        replay(gui);

        Game game = new Game(gui, rand, setupGame, handler, cardManager);
        game.displayActiveDirections();
        verify(gui);
    }

    @Test
    public void testSelectActivePokemon() {
        GameGUI gui = createMock(GameGUI.class);
        SetupGame setupGame = createMock(SetupGame.class);
        CardManager cardManager = createMock(CardManager.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        Random rand = createMock(Random.class);
        Pokemon p = createMock(Pokemon.class);
        Player player = createMock(Player.class);
        player.setActivePokemon(p);
        gui.makeActiveCard(p,1);
        expect(handler.getPlayerTurn()).andReturn(1);
        expect(handler.getCurrentPlayer()).andReturn(player);
        replay(gui, player, handler);

        Game game = new Game(gui, rand, setupGame, handler, cardManager);
        game.makeNewActivePokemon(p);

        verify(gui, player, handler);
    }

    @Test
    public void testCheckBasicPokemonFalse() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        SetupGame setupGame = createMock(SetupGame.class);
        CardManager cardManager = createMock(CardManager.class);
        Pokemon p = createMock(Pokemon.class);
        expect(p.getStage()).andReturn(1);
        replay(p);

        Game game = new Game(gui, rand, setupGame, handler, cardManager);
        boolean output = game.checkBasicPokemon(p);
        assertFalse(output);

        verify(p);
    }

    @Test
    public void testCheckBasicPokemonNotPokemon() {
        GameGUI gui = createMock(GameGUI.class);
        Energy e = createMock(Energy.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        CardManager cardManager = createMock(CardManager.class);
        Random rand = createMock(Random.class);

        Game game = new Game(gui, rand, setupGame, handler, cardManager);
        boolean output = game.checkBasicPokemon(e);
        assertFalse(output);
    }

    @Test
    public void testCheckBasicPokemonTrainer() {
        GameGUI gui = createMock(GameGUI.class);
        Trainer t = createMock(Trainer.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        CardManager cardManager = createMock(CardManager.class);
        SetupGame setupGame = createMock(SetupGame.class);
        Random rand = createMock(Random.class);

        Game game = new Game(gui, rand, setupGame, handler, cardManager);
        boolean output = game.checkBasicPokemon(t);
        assertFalse(output);
    }

    @Test
    public void testCheckBasicPokemonTrue() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        CardManager cardManager = createMock(CardManager.class);
        Pokemon p = createMock(Pokemon.class);
        expect(p.getStage()).andReturn(0);
        replay(p);

        Game game = new Game(gui, rand, setupGame, handler, cardManager);
        boolean output = game.checkBasicPokemon(p);
        assertTrue(output);

        verify(p);
    }

    @Test
    public void testCantAddToBench() {
        GameGUI gui = createMock(GameGUI.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        CardManager cardManager = createMock(CardManager.class);
        Random rand = createMock(Random.class);
        Pokemon p = createMock(Pokemon.class);
        expect(p.getStage()).andReturn(1);
        expect(gui.getLastSelectedCard()).andReturn(p);
        gui.displayMessage("This is not a basic Pokemon and can not place card on bench!");
        replay(p, gui);

        Game game = new Game(gui, rand, setupGame, handler, cardManager);
        game.handleBenchAction();

        verify(p, gui);
    }

    @Test
    public void testAddCardToBench() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        CardManager cardManager = createMock(CardManager.class);
        SetupGame setupGame = createMock(SetupGame.class);
        Pokemon p = createMock(Pokemon.class);
        Player player = createMock(Player.class);
        player.addBenchPokemon(p);
        expect(p.getStage()).andReturn(0);
        expect(handler.getCurrentPlayer()).andReturn(player);
        expect(gui.getLastSelectedCard()).andReturn(p);
        replay(p, player, gui, handler);

        Game game = new Game(gui, rand, setupGame, handler, cardManager);
        game.handleBenchAction();

        verify(player, p, gui, handler);
    }

    @Test
    public void testCantAddEnergy() {
        GameGUI gui = createMock(GameGUI.class);
        Player player = createMock(Player.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        Random rand = createMock(Random.class);
        CardManager cardManager = createMock(CardManager.class);
        SetupGame setupGame = createMock(SetupGame.class);
        expect(player.canAddEnergy()).andReturn(false);
        gui.displayMessage("Can only add one energy per turn!");
        Energy e = createMock(Energy.class);
        expect(handler.getCurrentPlayer()).andReturn(player);
        expect(gui.getLastSelectedCard()).andReturn(e);
        replay(gui,player, handler);

        Game game = new Game(gui, rand, setupGame, handler, cardManager);
        game.handleEnergyAction();
        verify(gui, player, handler);
    }

    @Test
    public void testSuccessAddingEnergy() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        Player player = createMock(Player.class);
        CardManager cardManager = createMock(CardManager.class);
        expect(player.canAddEnergy()).andReturn(true);

        Pokemon p = createMock(Pokemon.class);
        ArrayList<Card> pokemon = new ArrayList<>();
        pokemon.add(p);
        pokemon.add(p);
        expect(player.getOnlyPokemonFromHand()).andReturn(pokemon);
        expect(handler.getCurrentPlayer()).andReturn(player);
        gui.displayCards(pokemon);
        gui.displayMessage("Select Pokemon to add card to");
        Energy e = createMock(Energy.class);
        expect(gui.getLastSelectedCard()).andReturn(e);

        replay(gui,player, handler);

        Game game = new Game(gui, rand, setupGame, handler, cardManager);
        game.handleEnergyAction();
        verify(gui, player, handler);
    }

    @Test
    public void testSelectActiveLoopBasic() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        Player player = createMock(Player.class);
        CardManager cardManager = createMock(CardManager.class);
        SetupGame setupGame = createMock(SetupGame.class);
        Pokemon p = createMock(Pokemon.class);

        //display directions
        gui.displayMessage("Select a basic Pokemon to be your Active Pokemon");

        //Display hand pre selection
        gui.removeAllButtons();
        expect(player.handAsList()).andReturn(null);
        gui.displayCards(null);
        gui.setupActivePokemon();
        expect(gui.waitForButtonPressed()).andReturn("AddToBench");
        expect(gui.getLastSelectedCard()).andReturn(p);
        //check basic pokemon
        expect(p.getStage()).andReturn(0);

        //make new active
        player.setActivePokemon(p);
        gui.makeActiveCard(p, 1);

        //display hand post selection
        gui.removeAllButtons();
        expect(player.handAsList()).andReturn(null);
        gui.displayCards(null);

        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(handler.getPlayerTurn()).andReturn(1);
        replay(gui, player, handler);

        Game game = new Game(gui, rand, setupGame, handler, cardManager);
        game.selectActiveLoop();
        verify(gui, player, handler);
    }

    @Test
    public void testSelectActiveLoopNotBasic() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        Player player = createMock(Player.class);
        CardManager cardManager = createMock(CardManager.class);
        Pokemon p = createMock(Pokemon.class);
        ArrayList<Card> hand = new ArrayList<Card>();


        //display directions
        gui.displayMessage("Select a basic Pokemon to be your Active Pokemon");

        //Display hand pre selection
        gui.removeAllButtons();
        expect(player.handAsList()).andReturn(hand);
        gui.displayCards(hand);
        gui.setupActivePokemon();
        expect(gui.waitForButtonPressed()).andReturn("AddToBench");
        expect(gui.getLastSelectedCard()).andReturn(p);
        //check basic pokemon, fail then succeed
        expect(p.getStage()).andReturn(1).andReturn(0);

        //Failed
        gui.displayMessage("Not a basic Pokemon!");
        gui.removeAllButtons();
        //Now succeed
        //display directions
        gui.displayMessage("Select a basic Pokemon to be your Active Pokemon");

        //Display hand pre selection
        gui.removeAllButtons();
        expect(player.handAsList()).andReturn(hand);
        gui.displayCards(hand);
        gui.setupActivePokemon();
        expect(gui.waitForButtonPressed()).andReturn("AddToBench");
        expect(gui.getLastSelectedCard()).andReturn(p);
        //check basic pokemon, succeeding this time

        //make new active
        player.setActivePokemon(p);
        gui.makeActiveCard(p, 1);

        //display hand post selection
        gui.removeAllButtons();

        expect(player.handAsList()).andReturn(hand);
        gui.displayCards(hand);
        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(handler.getPlayerTurn()).andReturn(1);

        replay(gui, player, p, handler);

        Game game = new Game(gui, rand, setupGame, handler, cardManager);
        game.selectActiveLoop();
        verify(gui, player, p, handler);
    }

    @Test
    public void testDisplaySetupResults() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        CardManager cardManager = createMock(CardManager.class);

        Player player = createMock(Player.class);
        expect(player.getName()).andReturn("Ash");
        gui.displayMessage("The result was Heads Ash goes first!");
        replay(gui, player);

        Game game = new Game(gui, rand, setupGame, handler, cardManager);
        game.displaySetupResults("Heads", player);

        verify(gui, player);
    }

    @Test
    public void testSetupGame() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        CardManager cardManager = createMock(CardManager.class);

        Player player = createMock(Player.class);
        Pokemon p = createMock(Pokemon.class);

        // Setup expectations
        gui.createFlipButton();
        expect(setupGame.completeGameSetup()).andReturn("Heads");
        handler.completePlayerSetup("Heads");
        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(player.getName()).andReturn("Player 1");

        gui.displayMessage("The result was Heads Player 1 goes first!");

        // selectActiveLoop()
        gui.displayMessage("Select a basic Pokemon to be your Active Pokemon");
        gui.removeAllButtons();
        expect(player.handAsList()).andReturn(null);
        expect(gui.displayCards(null)).andReturn(p);
        expect(p.getStage()).andReturn(0);
        player.setActivePokemon(p);
        expect(handler.getPlayerTurn()).andReturn(1);
        gui.makeActiveCard(p, 1);
        gui.removeAllButtons();
        expect(player.handAsList()).andReturn(null);
        expect(gui.displayCards(null)).andReturn(p);

        replay(gui, rand, setupGame, handler, player, p);

        Game game = new Game(gui, rand, setupGame, handler, cardManager);
        game.setupGame();

        verify(gui, rand, setupGame, handler, player, p);
    }

    @Test
    public void testMainGameLoopAddToBench() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        CardManager cardManager = createMock(CardManager.class);
        Pokemon p = createMock(Pokemon.class);
        Player player = createMock(Player.class);

        expect(gui.waitForButtonPressed()).andReturn("AddToBench");
        expect(gui.getLastSelectedCard()).andReturn(p);
        expect(p.getStage()).andReturn(0);
        expect(handler.getCurrentPlayer()).andReturn(player);
        player.addBenchPokemon(p);

        replay(gui, rand, setupGame, handler, cardManager, p, player);

        Game game = new Game(gui, rand, setupGame, handler, cardManager);
        game.mainGameLoop();

        verify(gui, rand, setupGame, handler, cardManager, p, player);
    }
}

