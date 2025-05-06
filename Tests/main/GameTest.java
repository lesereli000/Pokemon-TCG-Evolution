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
        gui.createFlipButton();
        replay(gui);

        Game game = new Game(gui, rand, setupGame, playerHandler);
        game.setupFlipButton();

        verify(gui);
    }

    @Test
    public void testDisplayPlayerHand() {
        GameGUI gui = createMock(GameGUI.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        gui.removeAllButtons();
        Random rand = createMock(Random.class);
        Player player1 = createMock(Player.class);
        ArrayList<Card> cards = new ArrayList<>();
        expect(player1.handAsList()).andReturn(cards);
        expect(gui.displayCards(cards)).andReturn(null);
        expect(handler.getCurrentPlayer()).andReturn(player1);
        replay(gui, player1, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.displayCurrentPlayerHand();

        verify(gui, player1, handler);
    }

    @Test
    public void testDirectionActivePokemon() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        SetupGame setupGame = createMock(SetupGame.class);
        gui.displayMessage("Select a basic Pokemon to be your Active Pokemon");
        replay(gui);

        Game game = new Game(gui, rand, setupGame, handler);
        game.displayActiveDirections();
        verify(gui);
    }

    @Test
    public void testSelectActivePokemon() {
        GameGUI gui = createMock(GameGUI.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        Random rand = createMock(Random.class);
        Pokemon p = createMock(Pokemon.class);
        Player player = createMock(Player.class);
        player.setActivePokemon(p);
        gui.makeActiveCard(p,1);
        expect(handler.getPlayerTurn()).andReturn(1);
        expect(handler.getCurrentPlayer()).andReturn(player);
        replay(gui, player, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.makeNewActivePokemon(p);

        verify(gui, player, handler);
    }

    @Test
    public void testCheckBasicPokemonFalse() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        SetupGame setupGame = createMock(SetupGame.class);
        Pokemon p = createMock(Pokemon.class);
        expect(p.getStage()).andReturn(1);
        replay(p);

        Game game = new Game(gui, rand, setupGame, handler);
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
        Random rand = createMock(Random.class);

        Game game = new Game(gui, rand, setupGame, handler);
        boolean output = game.checkBasicPokemon(e);
        assertFalse(output);
    }

    @Test
    public void testCheckBasicPokemonTrainer() {
        GameGUI gui = createMock(GameGUI.class);
        Trainer t = createMock(Trainer.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        SetupGame setupGame = createMock(SetupGame.class);
        Random rand = createMock(Random.class);

        Game game = new Game(gui, rand, setupGame, handler);
        boolean output = game.checkBasicPokemon(t);
        assertFalse(output);
    }

    @Test
    public void testCheckBasicPokemonTrue() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        Pokemon p = createMock(Pokemon.class);
        expect(p.getStage()).andReturn(0);
        replay(p);

        Game game = new Game(gui, rand, setupGame, handler);
        boolean output = game.checkBasicPokemon(p);
        assertTrue(output);

        verify(p);
    }

    @Test
    public void testCantAddToBench() {
        GameGUI gui = createMock(GameGUI.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        Random rand = createMock(Random.class);
        Pokemon p = createMock(Pokemon.class);
        expect(p.getStage()).andReturn(1);
        gui.displayMessage("This is not a basic Pokemon and can not place card on bench!");
        replay(p, gui);

        Game game = new Game(gui, rand, setupGame, handler);
        game.selectCard(p);

        verify(p, gui);
    }

    @Test
    public void testAddCardToBench() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        SetupGame setupGame = createMock(SetupGame.class);
        Pokemon p = createMock(Pokemon.class);
        Player player = createMock(Player.class);
        player.addBenchPokemon(p);
        expect(p.getStage()).andReturn(0);
        expect(handler.getCurrentPlayer()).andReturn(player);
        replay(p, player, gui, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.selectCard(p);

        verify(player, p, gui, handler);
    }

    @Test
    public void testCantAddEnergy() {
        GameGUI gui = createMock(GameGUI.class);
        Player player = createMock(Player.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        expect(player.canAddEnergy()).andReturn(false);
        gui.displayMessage("Can only add one energy per turn!");
        expect(handler.getCurrentPlayer()).andReturn(player);
        replay(gui,player, handler);
        Energy e = createMock(Energy.class);

        Game game = new Game(gui, rand, setupGame, handler);
        game.selectCard(e);
        verify(gui, player, handler);
    }

    @Test
    public void testSuccessAddingEnergy() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        Player player = createMock(Player.class);
        expect(player.canAddEnergy()).andReturn(true);

        Pokemon p = createMock(Pokemon.class);
        ArrayList<Card> pokemon = new ArrayList<>();
        pokemon.add(p);
        pokemon.add(p);
        expect(player.getOnlyPokemonFromHand()).andReturn(pokemon);
        expect(handler.getCurrentPlayer()).andReturn(player);
        expect(gui.displayCards(pokemon)).andReturn(null);
        gui.displayMessage("Select Pokemon to add card to");

        replay(gui,player, handler);
        Energy e = createMock(Energy.class);

        Game game = new Game(gui, rand, setupGame, handler);
        game.selectCard(e);
        verify(gui, player, handler);
    }

    @Test
    public void testSelectActiveLoopBasic() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        Player player = createMock(Player.class);
        SetupGame setupGame = createMock(SetupGame.class);
        Pokemon p = createMock(Pokemon.class);

        //display directions
        gui.displayMessage("Select a basic Pokemon to be your Active Pokemon");

        //Display hand pre selection
        gui.removeAllButtons();
        expect(player.handAsList()).andReturn(null);
        expect(gui.displayCards(null)).andReturn(p);

        //check basic pokemon
        expect(p.getStage()).andReturn(0);

        //make new active
        player.setActivePokemon(p);
        gui.makeActiveCard(p, 1);

        //display hand post selection
        gui.removeAllButtons();
        expect(player.handAsList()).andReturn(null);
        expect(gui.displayCards(null)).andReturn(p);

        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(handler.getPlayerTurn()).andReturn(1);
        replay(gui, player, handler);

        Game game = new Game(gui, rand, setupGame, handler);
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
        Pokemon p = createMock(Pokemon.class);


        //display directions
        gui.displayMessage("Select a basic Pokemon to be your Active Pokemon");

        //Display hand pre selection
        gui.removeAllButtons();
        expect(player.handAsList()).andReturn(null);
        expect(gui.displayCards(null)).andReturn(p);

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
        expect(player.handAsList()).andReturn(null);
        expect(gui.displayCards(null)).andReturn(p);

        //check basic pokemon, succeeding this time

        //make new active
        player.setActivePokemon(p);
        gui.makeActiveCard(p, 1);

        //display hand post selection
        gui.removeAllButtons();
        expect(player.handAsList()).andReturn(null);
        expect(gui.displayCards(null)).andReturn(p);
        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(handler.getPlayerTurn()).andReturn(1);

        replay(gui, player, p, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.selectActiveLoop();
        verify(gui, player, p, handler);
    }
}

