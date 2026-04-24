package main;

import main.ui.*;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class GameTest {

    @Test
    public void testMakeFlipCoinButton() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler playerHandler = createNiceMock(PlayerHandler.class);
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
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Player player1 = createMock(Player.class);
        Random rand = createMock(Random.class);
        ArrayList<Card> hand = createMock(ArrayList.class);

        gui.removeAllButtons();
        expect(handler.getCurrentPlayerHand()).andReturn(hand);
        gui.displayCards(hand);

        replay(gui, player1, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.displayCurrentPlayerHand();

        verify(gui, player1);
    }

    @Test
    public void testDirectionActivePokemon() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        SetupGame setupGame = createMock(SetupGame.class);
        gui.displayMessage("Select a basic Pokemon to be your Active Pokemon");
        replay(gui);

        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.displayActiveDirections();
        verify(gui);
    }

    @Test
    public void testSelectActivePokemon() {
        GameGUI gui = createMock(GameGUI.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Random rand = createMock(Random.class);
        Pokemon p = createMock(Pokemon.class);
        Player player = createMock(Player.class);
        player.setActivePokemon(p);
        gui.makeActiveCard(anyObject(Player.class), eq(p));
        expect(handler.getPlayerTurn()).andReturn(1).anyTimes();
        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        replay(gui, player, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.makeNewActivePokemon(p);

        verify(gui, player);
    }

    @Test
    public void testCheckBasicPokemonFalse() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
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
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Random rand = createMock(Random.class);

        Game game = new Game(gui, rand, setupGame, handler);
        boolean output = game.checkBasicPokemon(e);
        assertFalse(output);
    }

    @Test
    public void testCheckBasicPokemonTrainer() {
        GameGUI gui = createMock(GameGUI.class);
        Trainer t = createMock(Trainer.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
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
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
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
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Random rand = createMock(Random.class);
        Pokemon p = createMock(Pokemon.class);
        expect(p.getStage()).andReturn(1);
        expect(gui.getLastSelectedCard()).andReturn(p);
        gui.displayMessage("This is not a basic Pokemon and can not place card on bench!");
        replay(p, gui);

        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleBenchAction();

        verify(p, gui);
    }

    @Test
    public void testAddCardToBench() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        SetupGame setupGame = createMock(SetupGame.class);
        Pokemon p = createMock(Pokemon.class);
        Player player = createMock(Player.class);

        expect(gui.getLastSelectedCard()).andReturn(p);
        expect(p.getStage()).andReturn(0);
        handler.addToBench(p);
        expect(handler.getPlayerTurn()).andReturn(1).anyTimes();
        gui.addBenchCard(anyObject(Player.class), eq(p));

        replay(p, player, gui, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.handleBenchAction();

        verify(player, p, gui);
    }

    @Test
    public void testCantAddEnergy() {
        GameGUI gui = createMock(GameGUI.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        Energy e = createMock(Energy.class);

        expect(gui.getLastSelectedCard()).andReturn(e);
        expect(handler.activeCanAddEnergy()).andReturn(false);
        gui.displayMessage("Unable to add energy!");
        replay(gui, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleEnergyAction();
        verify(gui);
    }

    @Test
    public void testSuccessAddingEnergy() {
        // Create mocks
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Player player = createMock(Player.class);
        Pokemon p = createMock(Pokemon.class);
        Energy e = createMock(Energy.class);
        ArrayList<Card> pokemon = new ArrayList<>();

        expect(gui.getLastSelectedCard()).andReturn(e);
        expect(handler.activeCanAddEnergy()).andReturn(true);
        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(handler.getOnlyPokemonFromBench(1)).andReturn(pokemon);
        expect(player.getActivePokemon()).andReturn(p);
        pokemon.add(p);

        //display add energy info
        gui.displayMessage("Select Pokemon to add Energy to");
        gui.removeAllButtons();
        gui.displayCards(pokemon);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();

        expect(gui.getLastSelectedCard()).andReturn(p);
        handler.addEnergyToPokemon(e, p);
        expect(gui.isCancelled()).andReturn(false);
        replay(gui, player, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleEnergyAction();

        verify(gui, player, handler);
    }

    @Test
    public void testSelectActiveLoopBasic() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Player player = createMock(Player.class);
        SetupGame setupGame = createMock(SetupGame.class);
        Pokemon p = createMock(Pokemon.class);
        ArrayList<Card> hand = createMock(ArrayList.class);
        //display directions
        gui.displayMessage("Select a basic Pokemon to be your Active Pokemon");

        //Display hand pre selection
        gui.removeAllButtons();
        expect(handler.getCurrentPlayerHand()).andReturn(hand).anyTimes();
        gui.displayCards(hand);
        gui.setupActivePokemon();
        expect(gui.waitForButtonPressed()).andReturn("");
        expect(gui.getLastSelectedCard()).andReturn(p);
        //check basic pokemon
        expect(p.getStage()).andReturn(0);

        //make new active
        expect(handler.getPlayerTurn()).andReturn(1).anyTimes();
        player.setActivePokemon(p);
        gui.makeActiveCard(anyObject(Player.class), eq(p));

        //display hand post selection
        gui.removeAllButtons();
        gui.displayCards(hand);

        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        replay(gui, player, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.selectActiveLoop();
        verify(gui, player);
    }

    @Test
    public void testSelectActiveLoopNotBasic() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Player player = createMock(Player.class);
        Pokemon p = createMock(Pokemon.class);
        ArrayList<Card> hand = createMock(ArrayList.class);


        //display directions
        gui.displayMessage("Select a basic Pokemon to be your Active Pokemon");

        //Display hand pre selection
        gui.removeAllButtons();
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
        expect(handler.getCurrentPlayerHand()).andReturn(hand).anyTimes();
        gui.displayCards(hand);
        gui.setupActivePokemon();
        expect(gui.waitForButtonPressed()).andReturn("AddToBench");
        expect(gui.getLastSelectedCard()).andReturn(p);
        //check basic pokemon, succeeding this time

        //make new active
        player.setActivePokemon(p);
        gui.makeActiveCard(anyObject(Player.class), eq(p));

        //display hand post selection
        gui.removeAllButtons();

        gui.displayCards(hand);
        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(handler.getPlayerTurn()).andReturn(1).anyTimes();

        replay(gui, player, p, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.selectActiveLoop();
        verify(gui, player, p);
    }

    @Test
    public void testDisplaySetupResults() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);

        Player player = createMock(Player.class);
        gui.displayMessage("The result was Heads Player 1 goes first!");
        replay(gui, player);

        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.displaySetupResults("Heads", 1);

        verify(gui, player);
    }

    @Test
    public void testSetupGame() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);

        Player player = createMock(Player.class);
        Pokemon p = createMock(Pokemon.class);
        ArrayList<Card> hand = createMock(ArrayList.class);
        // Setup expectations

        //decide deck
        expect(gui.displayDeckOptions()).andReturn("Overgrowth.txt");

        //setupFlipButton()
        gui.createFlipButton();

        //coinflip
        expect(setupGame.completeGameSetup()).andReturn("Heads");
        handler.completePlayerSetup("Heads", "Overgrowth.txt");
        gui.setPlayers(handler.player1, handler.player2);

        //player
        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();

        //displaySetupResults
        gui.displayMessage("The result was Heads Player 1 goes first!");

        //selectActiveLoop()
        //displayActiveDirections()
        gui.displayMessage("Select a basic Pokemon to be your Active Pokemon");
        //displayHand()
        gui.removeAllButtons();
        expect(handler.getPlayerTurn()).andReturn(1).anyTimes();
        gui.updateTurn(1);
        expect(handler.getCurrentPlayerHand()).andReturn(hand).anyTimes();
        gui.displayCards(hand);
        gui.setupActivePokemon();
        expect(gui.waitForButtonPressed()).andReturn("");
        expect(gui.getLastSelectedCard()).andReturn(p);
        //check basic
        expect(p.getStage()).andReturn(0);
        player.setActivePokemon(p);
        gui.makeActiveCard(anyObject(Player.class), eq(p));
        gui.removeAllButtons();
        gui.displayCards(hand);

        expect(gui.displayLocaleOptions()).andReturn(Locale.US);
        gui.displayMessage("You have chosen: English");

        replay(gui, rand, setupGame, handler, player, p);

        Game game = new Game(gui, rand, setupGame, handler);
        game.gameOver = true;
        game.setupGame();

        verify(gui, rand, setupGame, player, p);
    }

    @Test
    public void testMainGameLoopAddToBench() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Pokemon p = createMock(Pokemon.class);
        Player player = createMock(Player.class);
        ArrayList<Card> hand = createMock(ArrayList.class);

        expect(handler.getCurrentPlayerHand()).andReturn(hand);
        gui.removeAllButtons();
        gui.displayCards(hand);
        gui.displayActionButtons();
        expect(gui.waitForButtonPressed()).andReturn("AddToBench");
        expect(gui.getLastSelectedCard()).andReturn(p);
        expect(p.getStage()).andReturn(0);
        handler.addToBench(p);
        expect(handler.getPlayerTurn()).andReturn(1).anyTimes();
        gui.addBenchCard(anyObject(Player.class), eq(p));

        replay(gui, rand, setupGame, handler, p, player);

        Game game = new Game(gui, rand, setupGame, handler);
        game.mainGameLoop();

        verify(gui, rand, setupGame, p, player);
    }

    @Test
    public void testMainGameLoopAddEnergy() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Energy e = createMock(Energy.class);
        Pokemon p = createMock(Pokemon.class);
        Player player = createMock(Player.class);
        ArrayList<Card> hand = new ArrayList<>();

        expect(handler.getCurrentPlayerHand()).andReturn(hand);
        gui.removeAllButtons();
        gui.displayCards(hand);
        gui.displayActionButtons();
        expect(gui.waitForButtonPressed()).andReturn("AddEnergy");

        //handleEnergyAction()
        expect(gui.getLastSelectedCard()).andReturn(e);

        //handleAddEnergy()
        expect(handler.activeCanAddEnergy()).andReturn(true);
        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(handler.getOnlyPokemonFromBench(1)).andReturn(hand);
        expect(player.getActivePokemon()).andReturn(p);

        gui.displayMessage("Select Pokemon to add Energy to");
        gui.displayConfirmAndCancelButton();
        gui.removeAllButtons();
        hand.add(p);
        gui.displayCards(hand);
        gui.waitForAction();
        expect(gui.getLastSelectedCard()).andReturn(p);
        handler.addEnergyToPokemon(e, p);
        expect(gui.isCancelled()).andReturn(false);

        replay(gui, player, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.mainGameLoop();

        verify(gui, player, handler);
    }

    @Test
    public void testMainGameLoopAddEnergyWrongType() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Pokemon p = createMock(Pokemon.class);
        ArrayList<Card> hand = new ArrayList<>();

        expect(handler.getCurrentPlayerHand()).andReturn(hand);
        gui.removeAllButtons();
        gui.displayCards(hand);
        gui.displayActionButtons();
        expect(gui.waitForButtonPressed()).andReturn("AddEnergy");

        //handleEnergyAction()
        expect(gui.getLastSelectedCard()).andReturn(p);

        gui.displayMessage("Energy has not been selected!");

        replay(gui, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.mainGameLoop();

        verify(gui);
    }

    @Test
    public void testMainGameLoopAddEnergyCantAddEnergy() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Energy e = createMock(Energy.class);
        ArrayList<Card> hand = new ArrayList<>();

        expect(handler.getCurrentPlayerHand()).andReturn(hand);
        gui.removeAllButtons();
        gui.displayCards(hand);
        gui.displayActionButtons();
        expect(gui.waitForButtonPressed()).andReturn("AddEnergy");

        //handleEnergyAction()
        expect(gui.getLastSelectedCard()).andReturn(e);

        //handleAddEnergy()
        expect(handler.activeCanAddEnergy()).andReturn(false);
        gui.displayMessage("Unable to add energy!");

        replay(gui, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.mainGameLoop();

        verify(gui);
    }

    @Test
    public void testPassTurnActionNotFirstTurn() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);

        expect(handler.passTurn()).andReturn(true);
        expect(handler.getPlayerTurn()).andReturn(1).anyTimes();
        gui.updateTurn(1);
        expect(handler.drawCardFromDeck()).andReturn(true);
        replay(handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.handlePassTurnAction();
        
    }

    @Test
    public void testPassTurnActionFirstTurn() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Pokemon p = createMock(Pokemon.class);
        ArrayList<Card> hand = createMock(ArrayList.class);
        Player player = createMock(Player.class);

        expect(handler.passTurn()).andReturn(false);
        expect(handler.getPlayerTurn()).andReturn(1).anyTimes();
        gui.updateTurn(1);

        //display hand
        gui.removeAllButtons();
        expect(handler.getCurrentPlayerHand()).andReturn(hand);
        gui.displayCards(hand);
        //display directions
        gui.displayMessage("Select a basic Pokemon to be your Active Pokemon");

        gui.setupActivePokemon();
        expect(gui.waitForButtonPressed()).andReturn("");

        expect(gui.getLastSelectedCard()).andReturn(p);

        //check basic
        expect(p.getStage()).andReturn(0);

        //make new active
        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(handler.drawCardFromDeck()).andReturn(true);
        player.setActivePokemon(p);
        gui.makeActiveCard(anyObject(Player.class), eq(p));

        //display hand
        gui.removeAllButtons();
        expect(handler.getCurrentPlayerHand()).andReturn(hand);
        gui.displayCards(hand);

        replay(gui, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handlePassTurnAction();
        verify(gui);
    }

    @Test
    public void testHandleAttackActionPlayerCannotAttack() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        ArrayList<Card> hand = createMock(ArrayList.class);

        expect(handler.getCurrentPlayerHand()).andReturn(hand);
        gui.removeAllButtons();
        gui.displayCards(hand);
        gui.displayActionButtons();
        expect(gui.waitForButtonPressed()).andReturn("Attack");

        expect(handler.playerCanAttack()).andReturn(false);
        gui.displayMessage("You are unable to attack right now!");

        replay(gui, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.mainGameLoop();

        verify(gui);
    }

    @Test
    public void testHandleAttackActionDeadPokemon() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Attack attack = createMock(Attack.class);
        Player player1 = createMock(Player.class);
        Player player2 = createMock(Player.class);
        ArrayList<Attack> attacks = new ArrayList<>();
        attacks.add(attack);
        ArrayList<Card> cards = new ArrayList<>();
        Pokemon p = createMock(Pokemon.class);
        cards.add(p);

        expect(handler.getCurrentPlayerHand()).andReturn(cards);
        gui.removeAllButtons();
        gui.displayCards(cards);
        gui.displayActionButtons();
        expect(gui.waitForButtonPressed()).andReturn("Attack");

        expect(handler.playerCanAttack()).andReturn(true);

        // displayAttackInfo()
        gui.removeAllButtons();
        expect(handler.getCurrentPlayerAttacks()).andReturn(attacks);
        gui.displayPossibleAttacks(attacks);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.getLastSelectedAttack()).andReturn(attack);

        expect(handler.getCurrentPlayer()).andReturn(player1);
        expect(handler.getDefendingPlayer()).andReturn(player2);
        gui.displayAttackMessage(player1, player2, attack);

        expect(handler.attackOpponent(attack)).andReturn(true);
        expect(handler.isDefendingDead()).andReturn(true);
        gui.displayDeadActiveInfo(player2);

        //handleDeadActive
        expect(handler.getOnlyPokemonFromBench(2)).andReturn(cards);

        //display dead active GUI
        gui.removeAllButtons();
        gui.displayCards(cards);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();

        //handleDeadActive
        expect(gui.getLastSelectedCard()).andReturn(p);


        //pickup prize card
        expect(handler.activePickupPrizeCard()).andReturn(5);
        gui.removePrizeCard(anyObject(Player.class));
        handler.killDefenderActive(p);
        gui.makeActiveCard(anyObject(Player.class), eq(p));
        gui.removeBenchCard(anyObject(Player.class), eq(p));

        //pass turn
        expect(handler.passTurn()).andReturn(true);
        expect(handler.getPlayerTurn()).andReturn(1).times(2);
        expect(handler.drawCardFromDeck()).andReturn(true);
        gui.updateTurn(1);
        expect(gui.isCancelled()).andReturn(false);

        replay(gui, handler, p);

        Game game = new Game(gui, rand, setupGame, handler);
        game.mainGameLoop();

        verify(gui, p, handler);
    }

    @Test
    public void testBenchActionNotPokemon() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Energy e = createMock(Energy.class);

        expect(gui.getLastSelectedCard()).andReturn(e);
        gui.displayMessage("Pokemon has not been selected!");
        replay(gui);

        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleBenchAction();

        verify(gui);
    }

    @Test
    public void testHandleRetreatActionSuccess() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Player player = createMock(Player.class);
        Pokemon activePokemon = createMock(Pokemon.class);
        Card newActive = createMock(Card.class);
        ArrayList<Card> bench = new ArrayList<>();
        bench.add(newActive);

        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(player.getActivePokemon()).andReturn(activePokemon);
        expect(activePokemon.canRetreat()).andReturn(true);
        expect(handler.canRetreat()).andReturn(true);

        gui.displayRetreatEnergy(activePokemon, true);

        // Retreat process
        gui.removeAllButtons();
        gui.displayMessage("Select new active Pokemon");
        expect(handler.getOnlyPokemonFromBench(1)).andReturn(bench);
        gui.displayCards(bench);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.getLastSelectedCard()).andReturn(newActive);
        gui.replaceActiveCard(anyObject(Player.class), eq(newActive));
        expect(handler.getPlayerTurn()).andReturn(1).anyTimes();
        handler.setNewActive(newActive);
        expect(gui.isCancelled()).andReturn(false);

        replay(gui, handler, player, activePokemon);

        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleRetreatAction();

        verify(gui, player, activePokemon);
    }

    @Test
    public void testHandleRetreatActionFailed() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Player player = createMock(Player.class);
        Pokemon activePokemon = createMock(Pokemon.class);

        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(player.getActivePokemon()).andReturn(activePokemon);
        expect(activePokemon.canRetreat()).andReturn(false);
        //expect(handler.canRetreat()).andReturn(false); Only called once because previous statement

        gui.displayRetreatEnergy(activePokemon, false);

        replay(gui, handler, player, activePokemon);

        Game game = new Game(gui, rand, setupGame, handler);
        game.handleRetreatAction();

        verify(gui, player, activePokemon);
    }

    @Test
    public void testRetreatPokemonSelectNewActive() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Card newActive = createMock(Card.class);
        ArrayList<Card> bench = new ArrayList<>();
        bench.add(newActive);

        expect(handler.getOnlyPokemonFromBench(1)).andReturn(bench);
        gui.removeAllButtons();
        gui.displayMessage("Select new active Pokemon");
        gui.displayCards(bench);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.getLastSelectedCard()).andReturn(newActive);
        expect(handler.getPlayerTurn()).andReturn(1).anyTimes();
        gui.replaceActiveCard(anyObject(Player.class), eq(newActive));
        expect(gui.isCancelled()).andReturn(false);

        replay(gui, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        Card result = game.retreatPokemon();

        assertEquals(newActive, result);
        verify(gui);
    }

    @Test
    public void testHandleDeadActiveWithBasicPokemon() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Pokemon selectedPokemon = createMock(Pokemon.class);

        ArrayList<Card> bench = new ArrayList<>();
        bench.add(selectedPokemon);

        gui.removeAllButtons();
        expect(handler.getOnlyPokemonFromBench(2)).andReturn(bench);
        gui.displayCards(bench);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.getLastSelectedCard()).andReturn(selectedPokemon);

        expect(handler.getPlayerTurn()).andReturn(1).anyTimes();
        gui.makeActiveCard(anyObject(Player.class), eq(selectedPokemon));
        gui.removeBenchCard(anyObject(Player.class), eq(selectedPokemon));

        handler.killDefenderActive(selectedPokemon);
        expect(handler.activePickupPrizeCard()).andReturn(6);
        gui.removePrizeCard(anyObject(Player.class));

        replay(gui, handler, selectedPokemon);

        Game game = new Game(gui, rand, setupGame, handler);
        game.handleDeadActive();

        verify(gui, selectedPokemon, handler);
    }

    @Test
    public void testHandleDeadActiveWithNullPokemon() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Pokemon selectedPokemon = createMock(Pokemon.class);

        ArrayList<Card> bench = new ArrayList<>();
        bench.add(selectedPokemon);

        expect(handler.getOnlyPokemonFromBench(2)).andReturn(bench).times(2);

        gui.removeAllButtons();
        gui.displayCards(bench);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.getLastSelectedCard()).andReturn(null);

        // Not a basic Pokémon
        gui.displayMessage("Invalid Pokemon entry!");

        gui.removeAllButtons();
        gui.displayCards(bench);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.getLastSelectedCard()).andReturn(selectedPokemon);

        expect(handler.getPlayerTurn()).andReturn(1).anyTimes();
        handler.killDefenderActive(selectedPokemon);
        gui.makeActiveCard(anyObject(Player.class), eq(selectedPokemon));
        gui.removeBenchCard(anyObject(Player.class), eq(selectedPokemon));
        expect(handler.activePickupPrizeCard()).andReturn(6);
        gui.removePrizeCard(anyObject(Player.class));

        replay(gui, handler, selectedPokemon);

        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleDeadActive();

        verify(gui, selectedPokemon, handler);
    }

    @Test
    public void testMainGameLoopPassTurn() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Pokemon p = createMock(Pokemon.class);
        Player player = createMock(Player.class);
        ArrayList<Card> hand = createMock(ArrayList.class);

        expect(handler.getCurrentPlayerHand()).andReturn(hand);
        gui.removeAllButtons();
        gui.displayCards(hand);
        gui.displayActionButtons();
        expect(gui.waitForButtonPressed()).andReturn("PassTurn");

        expect(handler.passTurn()).andReturn(true);
        expect(handler.getPlayerTurn()).andReturn(1).anyTimes();
        gui.updateTurn(1);
        expect(handler.drawCardFromDeck()).andReturn(true);

        replay(gui, setupGame, handler, p, player);

        Game game = new Game(gui, rand, setupGame, handler);
        game.mainGameLoop();

        verify(gui, setupGame, p, player, handler);
    }

    @Test
    public void testMainGameLoopRetreat() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Pokemon p = createMock(Pokemon.class);
        Player player = createMock(Player.class);
        ArrayList<Card> hand = createMock(ArrayList.class);

        expect(handler.getCurrentPlayerHand()).andReturn(hand);
        gui.removeAllButtons();
        gui.displayCards(hand);
        gui.displayActionButtons();
        expect(gui.waitForButtonPressed()).andReturn("Retreat");

        //handler
        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(player.getActivePokemon()).andReturn(p);
        expect(p.canRetreat()).andReturn(false);
        //Only need one to be false to return flase in and statement

        gui.displayRetreatEnergy(p, false);

        replay(gui, setupGame, handler, p, player);

        Game game = new Game(gui, rand, setupGame, handler);
        game.mainGameLoop();

        verify(gui, setupGame, p, player);
    }

    @Test
    public void testHandleCantAttackOpponent() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Attack attack = createMock(Attack.class);
        ArrayList<Attack> attacks = new ArrayList<>();
        attacks.add(attack);

        gui.removeAllButtons();
        expect(handler.getCurrentPlayerAttacks()).andReturn(attacks);
        gui.displayPossibleAttacks(attacks);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.getLastSelectedAttack()).andReturn(attack);
        expect(handler.attackOpponent(attack)).andReturn(false);
        gui.displayMessage("Do not have the energy for that attack!");
        expect(gui.isCancelled()).andReturn(false);
        replay(gui, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleAttackOpponent();
        verify(gui);
    }

    @Test
    public void testHandleCanAttackOpponentDefendingAlive() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Attack attack = createMock(Attack.class);
        ArrayList<Attack> attacks = new ArrayList<>();
        attacks.add(attack);
        Player player1 = createMock(Player.class);
        Player player2 = createMock(Player.class);

        gui.removeAllButtons();
        expect(handler.getCurrentPlayerAttacks()).andReturn(attacks);
        gui.displayPossibleAttacks(attacks);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.getLastSelectedAttack()).andReturn(attack);
        expect(handler.attackOpponent(attack)).andReturn(true);

        expect(handler.isDefendingDead()).andReturn(false);
        expect(handler.getCurrentPlayer()).andReturn(player1);
        expect(handler.getDefendingPlayer()).andReturn(player2);

        gui.displayAttackMessage(player1, player2, attack);
        expect(handler.passTurn()).andReturn(true);
        expect(handler.getPlayerTurn()).andReturn(1).anyTimes();
        gui.updateTurn(1);
        expect(handler.drawCardFromDeck()).andReturn(true);
        expect(gui.isCancelled()).andReturn(false);

        replay(gui, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.handleAttackOpponent();
        verify(gui);
    }

    @Test
    public void testGameOver() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Player winner = createMock(Player.class);
        Player loser = createMock(Player.class);

        gui.displayWinningMessage(winner, loser);
        gui.closeWindow();

        replay(gui);
        Game game = new Game(gui, rand, setupGame, handler);
        game.gameIsOver(winner, loser);

        verify(gui);
    }

    @Test
    public void testHandleDeadActiveGameOver() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Player winner = createMock(Player.class);
        Player loser = createMock(Player.class);
        ArrayList<Card> cards = createMock(ArrayList.class);

        expect(handler.getOnlyPokemonFromBench(2)).andReturn(cards);
        expect(cards.isEmpty()).andReturn(true);
        expect(handler.getCurrentPlayer()).andReturn(winner);
        expect(handler.getDefendingPlayer()).andReturn(loser);

        gui.displayWinningMessage(winner, loser);
        gui.closeWindow();


        replay(gui, handler, cards);

        Game game = new Game(gui, rand, setupGame, handler);
        game.handleDeadActive();

        verify(gui, cards);
    }

    @Test
    public void testMainLoopCardInfo() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        ArrayList<Card> cards = createMock(ArrayList.class);
        Pokemon p = createMock(Pokemon.class);

        expect(handler.getCurrentPlayerHand()).andReturn(cards);
        gui.removeAllButtons();
        gui.displayCards(cards);
        gui.displayActionButtons();
        expect(gui.waitForButtonPressed()).andReturn("CardInfo");
        expect(gui.hasCardSelected()).andReturn(true);
        expect(gui.getLastSelectedCard()).andReturn(p);
        gui.displayCardReport(p);

        replay(gui, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.mainGameLoop();

        verify(gui);
    }

    @Test
    public void testCardInfoNull() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Pokemon p = createMock(Pokemon.class);
        Player player = createMock(Player.class);

        expect(gui.hasCardSelected()).andReturn(false);
        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(player.getActivePokemon()).andReturn(p);
        gui.displayCardReport(p);

        replay(gui, player, handler);
        Game game = new Game(gui, rand, setupGame, handler);
        game.displayCardInfo();

        verify(gui, player);
    }

    @Test
    public void testNullRetreat() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Pokemon p = createMock(Pokemon.class);
        Player player = createMock(Player.class);
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(p);

        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(player.getActivePokemon()).andReturn(p);
        expect(p.canRetreat()).andReturn(true);
        expect(handler.canRetreat()).andReturn(true);
        gui.displayRetreatEnergy(p, true);

        //retreat
        gui.removeAllButtons();
        gui.displayMessage("Select new active Pokemon");
        expect(handler.getOnlyPokemonFromBench(1)).andReturn(cards);
        gui.displayCards(cards);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();

        expect(gui.getLastSelectedCard()).andReturn(null).andReturn(p);
        gui.displayMessage("No card selected!");

        //retreat again
        gui.removeAllButtons();
        gui.displayMessage("Select new active Pokemon");
        expect(handler.getOnlyPokemonFromBench(1)).andReturn(cards);
        gui.displayCards(cards);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(handler.getPlayerTurn()).andReturn(1).anyTimes();
        gui.replaceActiveCard(anyObject(Player.class), eq(p));
        handler.setNewActive(p);
        expect(gui.isCancelled()).andReturn(false).times(2);

        replay(handler, gui, p, player);
        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleRetreatAction();
        verify(gui, p, player);
    }

    @Test
    public void testAddNullEnergy() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        ArrayList<Card> cards = new ArrayList<>();
        Pokemon p = createMock(Pokemon.class);
        cards.add(p);

        gui.displayMessage("Select Pokemon to add Energy to");
        gui.removeAllButtons();
        gui.displayCards(cards);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();

        expect(gui.getLastSelectedCard()).andReturn(null).andReturn(p);
        gui.displayMessage("Pokemon has not been selected!");

        gui.displayMessage("Select Pokemon to add Energy to");
        gui.removeAllButtons();
        gui.displayCards(cards);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.isCancelled()).andReturn(false).times(2);

        replay(gui, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        Pokemon result = game.displayAddEnergyInfo(cards);
        assertEquals(p, result);

        verify(gui, handler);
    }

    @Test
    public void testSelectedAttackNull() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        ArrayList<Attack> attacks = new ArrayList<>();
        Attack atk = createMock(Attack.class);
        attacks.add(atk);

        gui.removeAllButtons();
        expect(handler.getCurrentPlayerAttacks()).andReturn(attacks);
        gui.displayPossibleAttacks(attacks);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();

        expect(gui.getLastSelectedAttack()).andReturn(null).andReturn(atk);

        gui.displayMessage("Attack not selected!");

        gui.removeAllButtons();
        expect(handler.getCurrentPlayerAttacks()).andReturn(attacks);
        gui.displayPossibleAttacks(attacks);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.isCancelled()).andReturn(false).times(2);

        replay(gui, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        Attack result = game.displayAttackInfo();
        assertEquals(atk, result);

        verify(gui, handler);
    }

    @Test
    public void testNullPokemonBasic() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);

        Game game = new Game(gui, rand, setupGame, handler);
        assertFalse(game.checkBasicPokemon(null));
    }

    @Test
    public void testPickupPrizeCardEndGame() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Player winner = createMock(Player.class);
        Player loser = createMock(Player.class);

        expect(handler.activePickupPrizeCard()).andReturn(0);
        gui.removePrizeCard(anyObject(Player.class));
        expect(handler.getCurrentPlayer()).andReturn(winner).anyTimes();
        expect(handler.getDefendingPlayer()).andReturn(loser).anyTimes();
        gui.displayWinningMessage(winner, loser);
        gui.closeWindow();

        replay(gui, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.handlePickupPrizeCard(1);

        verify(gui);
    }

    @Test
    public void testHandleTrainerAction() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Trainer trainerCard = createMock(Trainer.class);
        Player player = createMock(Player.class);
        Energy notTrainerCard = createMock(Energy.class);

        // Case 1: Trainer not selected
        expect(gui.getLastSelectedCard()).andReturn(notTrainerCard);
        gui.displayMessage("Trainer has not been selected!");
        
        // Case 2: Trainer selected
        ArrayList<Card> pokemonList = new ArrayList<>();
        Pokemon selectedPokemon = createMock(Pokemon.class);
        pokemonList.add(selectedPokemon);

        expect(gui.getLastSelectedCard()).andReturn(trainerCard);
        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(handler.getAllPlayerPokemon()).andReturn(pokemonList);
        expect(handler.getAllPlayerEnergy()).andReturn(new ArrayList<>());
        // Policy methods replacing getName() checks in Game
        expect(trainerCard.requiresGuiSwitchUpdate()).andReturn(false).anyTimes();
        expect(trainerCard.requiresPokemonSelection()).andReturn(true).anyTimes();
        expect(trainerCard.requiresEnergySelection()).andReturn(false).anyTimes();
        
        player.removeFromHand(trainerCard);
        
        // select pokemon/energy (displayTrainerPokemonSelection/displayTrainerEnergySelection)
        gui.displayMessage(anyString());
        gui.removeAllButtons();
        gui.displayCards(pokemonList);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.isCancelled()).andReturn(false);
        expect(gui.getLastSelectedCard()).andReturn(selectedPokemon);

        trainerCard.doEffects(eq(player), eq(selectedPokemon), isNull());

        replay(gui, handler, player, trainerCard, selectedPokemon);

        Game game = new Game(gui, rand, setupGame, handler);

        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleTrainerAction(); // No trainer
        game.handleTrainerAction(); // Potion trainer

        verify(gui, handler, player, trainerCard);
    }

    @Test
    public void testHandleUseTrainerSwitch() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Trainer switchCard = createMock(Trainer.class);
        Player player = createMock(Player.class);
        Pokemon active = createMock(Pokemon.class);
        Pokemon selected = createMock(Pokemon.class);
        ArrayList<Card> bench = new ArrayList<>();
        bench.add(selected);

        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(handler.getAllPlayerPokemon()).andReturn(bench);
        expect(handler.getActivePokemon()).andReturn(active);
        expect(handler.getHandPokemon()).andReturn(new ArrayList<>());
        expect(handler.getAllPlayerEnergy()).andReturn(new ArrayList<>());
        // Policy methods replacing getName() checks in Game
        expect(switchCard.requiresGuiSwitchUpdate()).andReturn(true).anyTimes();
        expect(switchCard.requiresPokemonSelection()).andReturn(true).anyTimes();
        expect(switchCard.requiresEnergySelection()).andReturn(false).anyTimes();
        
        player.removeFromHand(switchCard);
        
        // Pokemon Selection for switch
        gui.displayMessage(anyString());
        gui.removeAllButtons();
        gui.displayCards(anyObject());
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.isCancelled()).andReturn(false).anyTimes();
        expect(gui.getLastSelectedCard()).andReturn(selected);
        
        switchCard.doEffects(player, selected, null);
        gui.replaceActiveCard(player, selected);

        replay(gui, handler, player, switchCard, active, selected);

        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleUseTrainer(switchCard);

        verify(gui, handler, player, switchCard);
    }



    @Test
    public void testAddEnergyCancelled() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Energy e = createMock(Energy.class);
        Player p = createMock(Player.class);
        ArrayList<Card> cards = createMock(ArrayList.class);
        Pokemon poke = createMock(Pokemon.class);

        expect(handler.activeCanAddEnergy()).andReturn(true);
        expect(handler.getCurrentPlayer()).andReturn(p);
        expect(handler.getOnlyPokemonFromBench(1)).andReturn(cards);
        expect(p.getActivePokemon()).andReturn(poke);

        //displayAddEnergy()
        gui.displayMessage("Select Pokemon to add Energy to");
        gui.removeAllButtons();
        gui.displayCards(cards);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.isCancelled()).andReturn(true);

        replay(gui, handler, p);

        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleAddEnergy(e);
        verify(gui, p, handler);
    }

    @Test
    public void testRetreatCancelled() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Player p = createMock(Player.class);
        Pokemon poke = createMock(Pokemon.class);
        ArrayList<Card> cards = createMock(ArrayList.class);

        expect(handler.getCurrentPlayer()).andReturn(p);
        expect(p.getActivePokemon()).andReturn(poke);
        expect(poke.canRetreat()).andReturn(true);
        expect(handler.canRetreat()).andReturn(true);
        gui.displayRetreatEnergy(poke, true);

        //retreatPokemon()
        gui.removeAllButtons();
        gui.displayMessage("Select new active Pokemon");
        expect(handler.getOnlyPokemonFromBench(1)).andReturn(cards);
        gui.displayCards(cards);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.isCancelled()).andReturn(true);

        replay(gui, handler, poke, p);

        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleRetreatAction();

        verify(gui, poke, p, handler);
    }

    @Test
    public void testCancelAttack() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        ArrayList<Attack> attacks = createMock(ArrayList.class);

        gui.removeAllButtons();
        expect(handler.getCurrentPlayerAttacks()).andReturn(attacks);
        gui.displayPossibleAttacks(attacks);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.isCancelled()).andReturn(true);

        replay(gui, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.handleAttackOpponent();

        verify(gui, handler);
    }

    @Test
    public void testMainGameLoopEvolveNotPokemon() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame gameSetup = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        ArrayList<Card> cards = createMock(ArrayList.class);
        Trainer t = createMock(Trainer.class);

        expect(handler.getCurrentPlayerHand()).andReturn(cards);
        gui.removeAllButtons();
        gui.displayCards(cards);
        gui.displayActionButtons();
        expect(gui.waitForButtonPressed()).andReturn("Evolve");
        expect(gui.getLastSelectedCard()).andReturn(t);
        gui.displayMessage("Pokemon has not been selected!");

        replay(gui, handler);

        Game game = new Game(gui, rand, gameSetup, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.mainGameLoop();

        verify(gui);
    }

    @Test
    public void testEvolveActionBasic() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame gameSetup = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Pokemon p = createMock(Pokemon.class);

        expect(gui.getLastSelectedCard()).andReturn(p);
        expect(p.getStage()).andReturn(0);
        expect(p.getName()).andReturn("Pikachu");
        gui.displayMessage("This is a basic Pokemon, not an evolution. Try adding Pikachu to the bench if you have room!");

        replay(gui, p);

        Game game = new Game(gui, rand, gameSetup, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleEvolveAction();

        verify(gui, p);
    }

    @Test
    public void testEvolveActionNoPreEvols() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame gameSetup = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Pokemon p = createMock(Pokemon.class);
        ArrayList<Card> cards = createMock(ArrayList.class);

        expect(gui.getLastSelectedCard()).andReturn(p);
        expect(p.getStage()).andReturn(1);
        expect(handler.getOnlyPreEvolutionsFromActivePlayer(p)).andReturn(cards);
        expect(cards.isEmpty()).andReturn(true);
        expect(p.getName()).andReturn("Pikachu");
        gui.displayMessage("You have no Pokemon that can evolve into Pikachu");

        replay(gui, p, handler, cards);

        Game game = new Game(gui, rand, gameSetup, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleEvolveAction();

        verify(gui, p, cards);
    }

    @Test
    public void testEvolveActionCancelled() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame gameSetup = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Pokemon p = createMock(Pokemon.class);
        ArrayList<Card> cards = createMock(ArrayList.class);

        expect(gui.getLastSelectedCard()).andReturn(p);
        expect(p.getStage()).andReturn(1);
        expect(handler.getOnlyPreEvolutionsFromActivePlayer(p)).andReturn(cards);
        expect(cards.isEmpty()).andReturn(false);

        //displayEvolveInfo
        gui.displayMessage("Select Pokemon to evolve from");
        gui.removeAllButtons();
        gui.displayCards(cards);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.isCancelled()).andReturn(true);

        replay(gui, p, handler, cards);

        Game game = new Game(gui, rand, gameSetup, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleEvolveAction();

        verify(gui, p, cards);
    }

    @Test
    public void testEvolveActionNoPokemonSelectedFirstError() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame gameSetup = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Pokemon p = createMock(Pokemon.class);
        Pokemon p2 = createMock(Pokemon.class);
        ArrayList<Card> cards = createMock(ArrayList.class);

        expect(gui.getLastSelectedCard()).andReturn(p);
        expect(p.getStage()).andReturn(1);
        expect(handler.getOnlyPreEvolutionsFromActivePlayer(p)).andReturn(cards);
        expect(cards.isEmpty()).andReturn(false);

        //displayEvolveInfo
        gui.displayMessage("Select Pokemon to evolve from");
        gui.removeAllButtons();
        gui.displayCards(cards);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.isCancelled()).andReturn(false);
        expect(gui.getLastSelectedCard()).andReturn(p2).anyTimes();
        expect(cards.contains(p2)).andReturn(false);
        gui.displayMessage("Pokemon has not been selected!");

        //displayEvolveInfo again
        gui.displayMessage("Select Pokemon to evolve from");
        gui.removeAllButtons();
        gui.displayCards(cards);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.isCancelled()).andReturn(false);
        expect(cards.contains(p2)).andReturn(true);
        expect(handler.evolve(p, p2)).andReturn("Error");
        gui.displayMessage("Evolution could not be completed");

        replay(gui, p, handler, cards);

        Game game = new Game(gui, rand, gameSetup, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleEvolveAction();

        verify(gui, p, cards);
    }

    @Test
    public void testEvolveActionActivePokemon() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame gameSetup = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Pokemon p = createMock(Pokemon.class);
        Pokemon p2 = createMock(Pokemon.class);
        ArrayList<Card> cards = createMock(ArrayList.class);

        expect(gui.getLastSelectedCard()).andReturn(p);
        expect(p.getStage()).andReturn(1);
        expect(handler.getOnlyPreEvolutionsFromActivePlayer(p)).andReturn(cards);
        expect(cards.isEmpty()).andReturn(false);

        //displayEvolveInfo again
        gui.displayMessage("Select Pokemon to evolve from");
        gui.removeAllButtons();
        gui.displayCards(cards);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.isCancelled()).andReturn(false);
        expect(gui.getLastSelectedCard()).andReturn(p2).anyTimes();
        expect(cards.contains(p2)).andReturn(true);
        expect(handler.evolve(p, p2)).andReturn("Active");
        expect(handler.getPlayerTurn()).andReturn(1).anyTimes();
        gui.makeActiveCard(anyObject(Player.class), eq(p));

        replay(gui, p, handler, cards);

        Game game = new Game(gui, rand, gameSetup, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleEvolveAction();

        verify(gui, p, cards);
    }

    @Test
    public void testEvolveActionBenchPokemon() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame gameSetup = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Pokemon p = createMock(Pokemon.class);
        Pokemon p2 = createMock(Pokemon.class);
        ArrayList<Card> cards = createMock(ArrayList.class);

        expect(gui.getLastSelectedCard()).andReturn(p);
        expect(p.getStage()).andReturn(1);
        expect(handler.getOnlyPreEvolutionsFromActivePlayer(p)).andReturn(cards);
        expect(cards.isEmpty()).andReturn(false);

        //displayEvolveInfo again
        gui.displayMessage("Select Pokemon to evolve from");
        gui.removeAllButtons();
        gui.displayCards(cards);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.isCancelled()).andReturn(false);
        expect(gui.getLastSelectedCard()).andReturn(p2).anyTimes();
        expect(cards.contains(p2)).andReturn(true);
        expect(handler.evolve(p, p2)).andReturn("Bench");
        expect(handler.getPlayerTurn()).andReturn(1).anyTimes();
        gui.removeBenchCard(anyObject(Player.class), eq(p2));
        gui.addBenchCard(anyObject(Player.class), eq(p));

        replay(gui, p, handler, cards);

        Game game = new Game(gui, rand, gameSetup, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleEvolveAction();

        verify(gui, p, cards);
    }

    @Test
    public void testEvolveActionJustPlayedPokemon() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame gameSetup = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Pokemon p = createMock(Pokemon.class);
        Pokemon p2 = createMock(Pokemon.class);
        ArrayList<Card> cards = createMock(ArrayList.class);

        expect(gui.getLastSelectedCard()).andReturn(p2);
        expect(p2.getStage()).andReturn(0);
        handler.addToBench(p2);
        expect(handler.getPlayerTurn()).andReturn(1).anyTimes();
        gui.addBenchCard(anyObject(Player.class), eq(p2));

        expect(gui.getLastSelectedCard()).andReturn(p);
        expect(p.getStage()).andReturn(1);
        expect(handler.getOnlyPreEvolutionsFromActivePlayer(p)).andReturn(cards);
        expect(cards.isEmpty()).andReturn(false);

        //displayEvolveInfo again
        gui.displayMessage("Select Pokemon to evolve from");
        gui.removeAllButtons();
        gui.displayCards(cards);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.isCancelled()).andReturn(false);
        expect(gui.getLastSelectedCard()).andReturn(p2);
        expect(cards.contains(p2)).andReturn(true);
        expect(handler.evolve(p, p2)).andReturn("JustPlayed");
        gui.displayMessage("Base Pokemon was just played");

        replay(gui, p, handler, cards);

        Game game = new Game(gui, rand, gameSetup, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleBenchAction();
        game.handleEvolveAction();

        verify(gui, p, cards);
    }

    @Test
    public void testPotionTrainer() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Trainer trainer = createMock(Trainer.class);
        Pokemon p = createMock(Pokemon.class);
        Energy e = createMock(Energy.class);
        Player player = createMock(Player.class);
        ArrayList<Card> hand = new ArrayList<>();
        ArrayList<Card> pokemon = new ArrayList<>();
        ArrayList<Card> energy = new ArrayList<>();
        energy.add(e);
        pokemon.add(p);

        expect(handler.getCurrentPlayerHand()).andReturn(hand);
        gui.removeAllButtons();
        gui.displayCards(hand);
        gui.displayActionButtons();
        expect(gui.waitForButtonPressed()).andReturn("PlayTrainer");

        // handleTrainerAction()
        expect(gui.getLastSelectedCard()).andReturn(trainer);

        // handleUseTrainer()
        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(handler.getAllPlayerPokemon()).andReturn(pokemon);
        // Policy methods replacing getName() checks in Game
        expect(trainer.requiresGuiSwitchUpdate()).andReturn(false).anyTimes();
        expect(trainer.requiresPokemonSelection()).andReturn(true).anyTimes();
        expect(trainer.requiresEnergySelection()).andReturn(false).anyTimes();
        expect(handler.getAllPlayerEnergy()).andReturn(energy);
        player.removeFromHand(trainer);
        expectLastCall();

        // DisplayTrainerPokemonSelection()
        gui.displayMessage("Select Pokemon to use Potion on");
        expectLastCall().anyTimes();
        gui.removeAllButtons();
        expectLastCall().anyTimes();
        gui.displayCards(pokemon);
        expectLastCall().anyTimes();
        gui.displayConfirmAndCancelButton();
        expectLastCall().anyTimes();
        gui.waitForAction();
        expectLastCall().anyTimes();

        expect(gui.isCancelled()).andReturn(false);
        expect(gui.getLastSelectedCard()).andReturn(p);

        trainer.doEffects(player, p, null);
        expectLastCall();

        replay(gui, player, handler, trainer, p);

        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.mainGameLoop();

        verify(gui, player, trainer, p);
    }

    @Test
    public void testSwitchTrainer() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Trainer trainer = createMock(Trainer.class);
        Pokemon p = createMock(Pokemon.class);
        Pokemon active = createMock(Pokemon.class);
        Energy e = createMock(Energy.class);
        Player player = createMock(Player.class);
        ArrayList<Card> hand = new ArrayList<>();
        ArrayList<Card> pokemon = new ArrayList<>();
        ArrayList<Card> energy = new ArrayList<>();
        energy.add(e);
        pokemon.add(p);

        expect(handler.getCurrentPlayerHand()).andReturn(hand);
        gui.removeAllButtons();
        gui.displayCards(hand);
        gui.displayActionButtons();
        expect(gui.waitForButtonPressed()).andReturn("PlayTrainer");

        // handleTrainerAction()
        expect(gui.getLastSelectedCard()).andReturn(trainer);

        // handleUseTrainer()
        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(handler.getAllPlayerPokemon()).andReturn(pokemon);
        // Policy methods replacing getName() checks in Game
        expect(trainer.requiresGuiSwitchUpdate()).andReturn(true).anyTimes();
        expect(trainer.requiresPokemonSelection()).andReturn(true).anyTimes();
        expect(trainer.requiresEnergySelection()).andReturn(false).anyTimes();
        expect(handler.getActivePokemon()).andReturn(active);
        expect(handler.getHandPokemon()).andReturn(hand);
        expect(handler.getAllPlayerEnergy()).andReturn(energy);
        expect(handler.getPlayerTurn()).andReturn(1).anyTimes();
        player.removeFromHand(trainer);
        expectLastCall();

        gui.replaceActiveCard(anyObject(Player.class), eq(p));

        // DisplayTrainerPokemonSelection()
        gui.displayMessage("Select Pokemon to use Switch on");
        expectLastCall().anyTimes();
        gui.removeAllButtons();
        expectLastCall().anyTimes();
        gui.displayCards(pokemon);
        expectLastCall().anyTimes();
        gui.displayConfirmAndCancelButton();
        expectLastCall().anyTimes();
        gui.waitForAction();
        expectLastCall().anyTimes();

        expect(gui.isCancelled()).andReturn(false);
        expect(gui.getLastSelectedCard()).andReturn(p);

        trainer.doEffects(player, p, null);
        expectLastCall();

        replay(gui, player, handler, trainer, p);

        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.mainGameLoop();

        verify(gui, player, trainer, p);
    }

    @Test
    public void testBillTrainer() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Trainer trainer = createMock(Trainer.class);
        Player player = createMock(Player.class);
        ArrayList<Card> hand = new ArrayList<>();
        ArrayList<Card> pokemon = new ArrayList<>();
        ArrayList<Card> energy = new ArrayList<>();

        expect(handler.getCurrentPlayerHand()).andReturn(hand);
        gui.removeAllButtons();
        gui.displayCards(hand);
        gui.displayActionButtons();
        expect(gui.waitForButtonPressed()).andReturn("PlayTrainer");

        // handleTrainerAction
        expect(gui.getLastSelectedCard()).andReturn(trainer);

        // handleUseTrainer
        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(handler.getAllPlayerPokemon()).andReturn(pokemon);
        expect(handler.getAllPlayerEnergy()).andReturn(energy);
        player.removeFromHand(trainer);
        expectLastCall();

        // Policy methods replacing getName() checks in Game (Bill needs none)
        expect(trainer.requiresGuiSwitchUpdate()).andReturn(false).anyTimes();
        expect(trainer.requiresPokemonSelection()).andReturn(false).anyTimes();
        expect(trainer.requiresEnergySelection()).andReturn(false).anyTimes();
        trainer.doEffects(player, null, null);  // No Pokémon or energy needed
        expectLastCall();

        replay(gui, player, handler, trainer);

        Game game = new Game(gui, rand, setupGame, handler);
        game.mainGameLoop();

        verify(gui, player, trainer);
    }

    @Test
    public void testPotionTrainer_noPokemonSelected_displaysMessage() {
        GUI gui = createMock(GUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Trainer trainer = createMock(Trainer.class);
        Player player = createMock(Player.class);

        ArrayList<Card> pokemon = new ArrayList<>(); // assume we have options, but none selected
        Pokemon p1 = createMock(Pokemon.class);
        pokemon.add(p1);

        ArrayList<Card> energy = new ArrayList<>(); // not used for Potion
        expect(handler.getCurrentPlayerHand()).andReturn(pokemon);
        gui.removeAllButtons();
        gui.displayCards(pokemon);
        gui.displayActionButtons();
        expect(gui.waitForButtonPressed()).andReturn("PlayTrainer");

        // handleTrainerAction()
        expect(gui.getLastSelectedCard()).andReturn(trainer).times(1);

        // handleUseTrainer()
        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(handler.getAllPlayerPokemon()).andReturn(pokemon);
        expect(handler.getAllPlayerEnergy()).andReturn(energy);
        player.removeFromHand(trainer);
        expectLastCall();

        // Policy methods replacing getName() checks in Game
        expect(trainer.requiresGuiSwitchUpdate()).andReturn(false).anyTimes();
        expect(trainer.requiresPokemonSelection()).andReturn(true).anyTimes();
        expect(trainer.requiresEnergySelection()).andReturn(false).anyTimes();
        // displayTrainerPokemonSelection
        gui.displayMessage("Select Pokemon to use Potion on");
        expectLastCall().times(2);
        gui.removeAllButtons();
        expectLastCall().times(2);
        gui.displayCards(pokemon);
        expectLastCall().times(2);
        gui.displayConfirmAndCancelButton();
        expectLastCall().times(2);
        gui.waitForAction();
        expectLastCall().times(2);
        expect(gui.isCancelled()).andReturn(false).times(2);
        expect(gui.getLastSelectedCard()).andReturn(null).times(1);
        expect(gui.getLastSelectedCard()).andReturn(p1).times(1);

        // Expected message for no selection
        gui.displayMessage("Pokemon has not been selected!");
        expectLastCall().times(1);

        trainer.doEffects(player, p1, null);
        expectLastCall();

        replay(gui, player, handler, trainer, p1);

        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.mainGameLoop();

        verify(gui, player, trainer, p1);
    }

    @Test
    public void testSuperPotionTrainer() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Trainer trainer = createMock(Trainer.class);
        Pokemon p = createMock(Pokemon.class);
        Player player = createMock(Player.class);
        Energy e1 = new Energy(EnergyType.fromName("Grass Energy"));  // in energy
        Energy e2 = new Energy(EnergyType.fromName("Fire Energy"));   // NOT in energy
        ArrayList<Card> hand = new ArrayList<>();
        ArrayList<Card> pokemon = new ArrayList<>();
        ArrayList<Card> energy = new ArrayList<>();
        pokemon.add(p);
        energy.add(e1);

        expect(handler.getCurrentPlayerHand()).andReturn(hand);
        gui.removeAllButtons();
        expectLastCall().times(4);
        gui.displayCards(hand);
        expectLastCall().anyTimes();
        gui.displayActionButtons();
        expect(gui.waitForButtonPressed()).andReturn("PlayTrainer");

        // handleTrainerAction()
        expect(gui.getLastSelectedCard()).andReturn(trainer);

        // handleUseTrainer()
        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(handler.getAllPlayerPokemon()).andReturn(pokemon);
        expect(handler.getAllPlayerEnergy()).andReturn(energy);
        player.removeFromHand(trainer);
        expectLastCall();

        // Policy methods replacing getName() checks in Game
        expect(trainer.requiresGuiSwitchUpdate()).andReturn(false).anyTimes();
        expect(trainer.requiresPokemonSelection()).andReturn(true).anyTimes();
        expect(trainer.requiresEnergySelection()).andReturn(true).anyTimes();
        // displayTrainerPokemonSelection
        gui.displayMessage("Select Pokemon to use Potion on");
        expectLastCall().times(1);
        gui.displayCards(pokemon);
        expectLastCall().times(1);
        gui.displayConfirmAndCancelButton();
        expectLastCall().times(3);
        gui.waitForAction();
        expectLastCall().times(3);
        expect(gui.isCancelled()).andReturn(false);
        expect(gui.getLastSelectedCard()).andReturn(p).times(1);

        // First call to displayTrainerEnergySelection (invalid selection)
        gui.displayMessage("Select Energy to discard for Super Potion");
        expectLastCall().times(2);
        gui.displayCards(energy);
        expectLastCall().times(2);
        expect(gui.isCancelled()).andReturn(false).times(2);

        // Invalid attempt
        expect(gui.getLastSelectedCard()).andReturn(e2).times(1);
        gui.displayMessage("Energy has not been selected!"); // recursive trigger
        expectLastCall().once();
        expect(gui.getLastSelectedCard()).andReturn(e1).times(1);

        // Valid Attempt
        trainer.doEffects(player, p, e1);
        expectLastCall();

        replay(gui, player, handler, trainer, p);

        Game game = new Game(gui, rand, setupGame, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.mainGameLoop();

        verify(gui, player, trainer, p);
    }

    @Test
    public void testHandleTrainerNotSelected() {
        GUI gui = createMock(GUI.class);
        Random rand = createMock(Random.class);
        SetupGame gameSetup = createMock(SetupGame.class);
        PlayerHandler ph = createNiceMock(PlayerHandler.class);
        Card nonTrainerCard = createMock(Card.class); // Not a Trainer

        expect(gui.getLastSelectedCard()).andReturn(nonTrainerCard);
        gui.displayMessage("Trainer has not been selected!");
        expectLastCall().once();

        // Replay mocks
        replay(gui, nonTrainerCard);

        Game game = new Game(gui, rand, gameSetup, ph);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleTrainerAction();        // Verify interactions

        verify(gui, nonTrainerCard);
    }

    @Test
    public void testPassTurnOutOfCards() {
        GUI gui = createMock(GUI.class);
        Random rand = createMock(Random.class);
        SetupGame gameSetup = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Player winner = createMock(Player.class);
        Player loser = createMock(Player.class);

        expect(handler.passTurn()).andReturn(true);
        expect(handler.getPlayerTurn()).andReturn(1).anyTimes();
        gui.updateTurn(1);
        expect(handler.drawCardFromDeck()).andReturn(false);
        expect(handler.getCurrentPlayer()).andReturn(winner);
        expect(handler.getDefendingPlayer()).andReturn(loser);

        //game is over
        gui.displayWinningMessage(winner, loser);
        gui.closeWindow();

        replay(gui, handler);

        Game game = new Game(gui, rand, gameSetup, handler);
        game.handlePassTurnAction();

        verify(gui);
    }

    @Test
    public void testDisplayTrainerPokemonSelectionCancelled() {
        GUI gui = createMock(GUI.class);
        Random rand = createMock(Random.class);
        SetupGame gameSetup = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Trainer t = createMock(Trainer.class);
        ArrayList<Card> pokemon = createMock(ArrayList.class);

        // Policy methods replacing getName() checks in Game
        expect(t.requiresPokemonSelection()).andReturn(true).anyTimes();
        expect(t.requiresGuiSwitchUpdate()).andReturn(false).anyTimes();
        gui.displayMessage("Select Pokemon to use Potion on");
        gui.removeAllButtons();
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        gui.displayCards(pokemon);
        expect(gui.isCancelled()).andReturn(true);

        replay(gui, t);

        Game game = new Game(gui, rand, gameSetup, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        assertNull(game.displayTrainerPokemonSelection(t, pokemon));

        verify(gui, t);
    }

    @Test
    public void testDisplayTrainerEnergySelectionCancelled() {
        GUI gui = createMock(GUI.class);
        Random rand = createMock(Random.class);
        SetupGame gameSetup = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        Trainer t = createMock(Trainer.class);
        ArrayList<Card> energies = createMock(ArrayList.class);

        expect(t.requiresEnergySelection()).andReturn(true);
        gui.displayMessage("Select Energy to discard for Super Potion");
        gui.removeAllButtons();
        gui.displayCards(energies);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.isCancelled()).andReturn(true);

        replay(gui, t);

        Game game = new Game(gui, rand, gameSetup, handler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        assertNull(game.displayTrainerEnergySelection(t, energies));

        verify(gui, t);
    }

    @Test
    public void testMainGameLoopGameSetupOneTurn() {
        GUI gui = createMock(GUI.class);
        Random rand = createMock(Random.class);
        SetupGame gameSetup = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        ArrayList<Card> cards = createMock(ArrayList.class);
        Pokemon p = createMock(Pokemon.class);
        Player player = createMock(Player.class);

        //decide locale
        expect(gui.displayLocaleOptions()).andReturn(Locale.US);
        gui.displayMessage("You have chosen: English");

        //decide deck
        expect(gui.displayDeckOptions()).andReturn("Overgrowth.txt");

        //setupFlipButton()
        gui.createFlipButton();

        expect(gameSetup.completeGameSetup()).andReturn("Heads");
        handler.completePlayerSetup("Heads", "Overgrowth.txt");
        gui.setPlayers(handler.player1, handler.player2);
        expect(handler.getPlayerTurn()).andReturn(1).anyTimes();
        gui.updateTurn(1);

        //displaySetupResults()
        gui.displayMessage("The result was Heads Player 1 goes first!");

        //selectActiveLoop
        //displayCurrentHand
        gui.removeAllButtons();
        expect(handler.getCurrentPlayerHand()).andReturn(cards);
        gui.displayCards(cards);

        //displayActiveDirections
        gui.displayMessage("Select a basic Pokemon to be your Active Pokemon");

        gui.setupActivePokemon();
        expect(gui.waitForButtonPressed()).andReturn("");
        expect(gui.getLastSelectedCard()).andReturn(p);

        //checkBasic
        expect(p.getStage()).andReturn(0);

        //makeNewActivePokemon
        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        player.setActivePokemon(p);
        gui.makeActiveCard(anyObject(Player.class), eq(p));

        //displayCurrentPlayerHand
        gui.removeAllButtons();
        expect(handler.getCurrentPlayerHand()).andReturn(cards);
        gui.displayCards(cards);

        //mainGameLoop
        expect(handler.getCurrentPlayerHand()).andReturn(cards);
        gui.removeAllButtons();
        gui.displayCards(cards);
        gui.displayActionButtons();
        expect(gui.waitForButtonPressed()).andReturn("displayCardInfo");
        expect(gui.gameIsOver()).andReturn(true);

        replay(gui, handler, player, p, gameSetup);

        Game game = new Game(gui, rand, gameSetup, handler);
        game.setupGame();

        verify(gui, player, p, gameSetup);
    }

    @Test
    public void testMainGameLoopGameSetupTwoTurns() {
        GUI gui = createMock(GUI.class);
        Random rand = createMock(Random.class);
        SetupGame gameSetup = createMock(SetupGame.class);
        PlayerHandler handler = createNiceMock(PlayerHandler.class);
        ArrayList<Card> cards = createMock(ArrayList.class);
        Pokemon p = createMock(Pokemon.class);
        Player player = createMock(Player.class);

        //decide locale
        expect(gui.displayLocaleOptions()).andReturn(Locale.US);
        gui.displayMessage("You have chosen: English");

        //decide deck
        expect(gui.displayDeckOptions()).andReturn("Overgrowth.txt");

        //setupFlipButton()
        gui.createFlipButton();

        expect(gameSetup.completeGameSetup()).andReturn("Heads");
        handler.completePlayerSetup("Heads", "Overgrowth.txt");
        gui.setPlayers(handler.player1, handler.player2);
        expect(handler.getPlayerTurn()).andReturn(1).anyTimes();
        gui.updateTurn(1);

        //displaySetupResults()
        gui.displayMessage("The result was Heads Player 1 goes first!");

        //selectActiveLoop
        //displayCurrentHand
        gui.removeAllButtons();
        expect(handler.getCurrentPlayerHand()).andReturn(cards);
        gui.displayCards(cards);

        //displayActiveDirections
        gui.displayMessage("Select a basic Pokemon to be your Active Pokemon");

        gui.setupActivePokemon();
        expect(gui.waitForButtonPressed()).andReturn("");
        expect(gui.getLastSelectedCard()).andReturn(p);

        //checkBasic
        expect(p.getStage()).andReturn(0);

        //makeNewActivePokemon
        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        player.setActivePokemon(p);
        gui.makeActiveCard(anyObject(Player.class), eq(p));

        //displayCurrentPlayerHand
        gui.removeAllButtons();
        expect(handler.getCurrentPlayerHand()).andReturn(cards);
        gui.displayCards(cards);

        //mainGameLoop
        expect(handler.getCurrentPlayerHand()).andReturn(cards);
        gui.removeAllButtons();
        gui.displayCards(cards);
        gui.displayActionButtons();
        expect(gui.waitForButtonPressed()).andReturn("displayCardInfo");
        expect(gui.gameIsOver()).andReturn(false);

        //again
        expect(handler.getCurrentPlayerHand()).andReturn(cards);
        gui.removeAllButtons();
        gui.displayCards(cards);
        gui.displayActionButtons();
        expect(gui.waitForButtonPressed()).andReturn("displayCardInfo");
        expect(gui.gameIsOver()).andReturn(true);

        replay(gui, handler, player, p, gameSetup);

        Game game = new Game(gui, rand, gameSetup, handler);
        game.setupGame();

        verify(gui, player, p, gameSetup);
    }

    @Test
    public void testDisplayTrainerPokemonSelectionOtherTrainer() {
        GUI gui = createMock(GUI.class);
        Trainer t = new Trainer("Bill", "Draw 2 cards.");
        Game game = new Game(gui, null, null, null);
        assertNull(game.displayTrainerPokemonSelection(t, new ArrayList<>()));
    }

    @Test
    public void testDisplayTrainerEnergySelectionOtherTrainer() {
        GUI gui = createMock(GUI.class);
        Trainer t = new Trainer("Bill", "Draw 2 cards.");
        Game game = new Game(gui, null, null, null);
        assertNull(game.displayTrainerEnergySelection(t, new ArrayList<>()));
    }

    @Test
    public void testHandleUseTrainerWithGenericTrainer() {
        GUI gui = createMock(GUI.class);
        PlayerHandler ph = createMock(PlayerHandler.class);
        Player active = createMock(Player.class);
        Trainer t = new Trainer("Generic", "No Effect"); // Registry does not contain "No Effect"
        
        expect(ph.getCurrentPlayer()).andReturn(active);
        expect(ph.getAllPlayerPokemon()).andReturn(new ArrayList<>());
        expect(ph.getAllPlayerEnergy()).andReturn(new ArrayList<>());
        active.removeFromHand(t);
        
        replay(ph, active);
        Game game = new Game(gui, null, null, ph);
        game.handleUseTrainer(t);
        verify(ph, active);
    }

    @Test
    public void testHandleEvolveActionNotPokemon() {
        GUI gui = createMock(GUI.class);
        expect(gui.getLastSelectedCard()).andReturn(new Energy(EnergyType.GRASS));
        gui.displayMessage("Pokemon has not been selected!");
        replay(gui);

        Game game = new Game(gui, null, null, null);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleEvolveAction();
        verify(gui);
    }

    @Test
    public void testHandleEvolveStage0() {
        GUI gui = createMock(GUI.class);
        Pokemon p = new Pokemon("Pika", "Lightning", 0, 60);
        expect(gui.getLastSelectedCard()).andReturn(p);
        gui.displayMessage("This is a basic Pokemon, not an evolution. Try adding Pika to the bench if you have room!");
        replay(gui);

        Game game = new Game(gui, null, null, null);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleEvolveAction();
        verify(gui);
    }

    @Test
    public void testHandleEvolveNoPreEvolutions() {
        GUI gui = createMock(GUI.class);
        PlayerHandler ph = createMock(PlayerHandler.class);
        Pokemon stage1 = new Pokemon("Ivysaur", "Grass", 1, 60);
        stage1.setEvolvesFrom("Bulbasaur");
        
        expect(gui.getLastSelectedCard()).andReturn(stage1);
        expect(ph.getOnlyPreEvolutionsFromActivePlayer(stage1)).andReturn(new ArrayList<>());
        gui.displayMessage("You have no Pokemon that can evolve into Ivysaur");
        replay(gui, ph);

        Game game = new Game(gui, null, null, ph);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleEvolveAction();
        verify(gui, ph);
    }

    @Test
    public void testHandleEvolveSuccessActive() {
        GUI gui = createMock(GUI.class);
        PlayerHandler ph = createMock(PlayerHandler.class);
        Player player = createMock(Player.class);
        Pokemon basic = new Pokemon("Bulbasaur", "Grass", 0, 40);
        Pokemon stage1 = new Pokemon("Ivysaur", "Grass", 1, 60);
        stage1.setEvolvesFrom("Bulbasaur");
        ArrayList<Card> preEvs = new ArrayList<>();
        preEvs.add(basic);
        
        expect(gui.getLastSelectedCard()).andReturn(stage1);
        expect(ph.getOnlyPreEvolutionsFromActivePlayer(stage1)).andReturn(preEvs);
        
        // displayEvolveInfo
        gui.displayMessage("Select Pokemon to evolve from");
        gui.removeAllButtons();
        gui.displayCards(preEvs);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.isCancelled()).andReturn(false);
        expect(gui.getLastSelectedCard()).andReturn(basic);
        
        expect(ph.evolve(stage1, basic)).andReturn("Active");
        expect(ph.getCurrentPlayer()).andReturn(player);
        gui.makeActiveCard(player, stage1);
        
        replay(gui, ph);
        Game game = new Game(gui, null, null, ph);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleEvolveAction();
        verify(gui, ph);
    }

    @Test
    public void testHandleEvolveSuccessBench() {
        GUI gui = createMock(GUI.class);
        PlayerHandler ph = createMock(PlayerHandler.class);
        Player player = createMock(Player.class);
        Pokemon basic = new Pokemon("Bulbasaur", "Grass", 0, 40);
        Pokemon stage1 = new Pokemon("Ivysaur", "Grass", 1, 60);
        stage1.setEvolvesFrom("Bulbasaur");
        ArrayList<Card> preEvs = new ArrayList<>();
        preEvs.add(basic);
        
        expect(gui.getLastSelectedCard()).andReturn(stage1);
        expect(ph.getOnlyPreEvolutionsFromActivePlayer(stage1)).andReturn(preEvs);
        
        // displayEvolveInfo mock flow
        gui.displayMessage("Select Pokemon to evolve from");
        gui.removeAllButtons();
        gui.displayCards(preEvs);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.isCancelled()).andReturn(false);
        expect(gui.getLastSelectedCard()).andReturn(basic);
        
        expect(ph.evolve(stage1, basic)).andReturn("Bench");
        expect(ph.getCurrentPlayer()).andReturn(player).anyTimes();
        gui.removeBenchCard(player, basic);
        gui.addBenchCard(player, stage1);
        
        replay(gui, ph);
        Game game = new Game(gui, null, null, ph);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleEvolveAction();
        verify(gui, ph);
    }

    @Test
    public void testHandleEvolveErrorCases() {
        GUI gui = createMock(GUI.class);
        PlayerHandler ph = createMock(PlayerHandler.class);
        Pokemon basic = new Pokemon("Bulbasaur", "Grass", 0, 40);
        Pokemon stage1 = new Pokemon("Ivysaur", "Grass", 1, 60);
        stage1.setEvolvesFrom("Bulbasaur");
        ArrayList<Card> preEvs = new ArrayList<>();
        preEvs.add(basic);
        
        expect(gui.getLastSelectedCard()).andReturn(stage1);
        expect(ph.getOnlyPreEvolutionsFromActivePlayer(stage1)).andReturn(preEvs);
        
        // displayEvolveInfo mock flow
        gui.displayMessage("Select Pokemon to evolve from");
        gui.removeAllButtons();
        gui.displayCards(preEvs);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.isCancelled()).andReturn(false);
        expect(gui.getLastSelectedCard()).andReturn(basic);
        
        // Test "Error" path
        expect(ph.evolve(stage1, basic)).andReturn("Error");
        gui.displayMessage("Evolution could not be completed");
        
        replay(gui, ph);
        Game game = new Game(gui, null, null, ph);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleEvolveAction();
        verify(gui, ph);
    }

    @Test
    public void testHandleEvolveJustPlayed() {
        GUI gui = createMock(GUI.class);
        PlayerHandler ph = createMock(PlayerHandler.class);
        Pokemon basic = new Pokemon("Bulbasaur", "Grass", 0, 40);
        Pokemon stage1 = new Pokemon("Ivysaur", "Grass", 1, 60);
        stage1.setEvolvesFrom("Bulbasaur");
        ArrayList<Card> preEvs = new ArrayList<>();
        preEvs.add(basic);
        
        expect(gui.getLastSelectedCard()).andReturn(stage1).times(1);
        expect(ph.getOnlyPreEvolutionsFromActivePlayer(stage1)).andReturn(preEvs).times(1);
        gui.displayMessage("Select Pokemon to evolve from");
        gui.removeAllButtons();
        gui.displayCards(preEvs);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.isCancelled()).andReturn(false);
        expect(gui.getLastSelectedCard()).andReturn(basic);
        
        expect(ph.evolve(stage1, basic)).andReturn("JustPlayed");
        gui.displayMessage("Base Pokemon was just played");
        
        replay(gui, ph);
        Game game = new Game(gui, null, null, ph);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game.handleEvolveAction();
        verify(gui, ph);
    }

    @Test
    public void testSelectActiveLoopNullSelection() {
        GUI gui = createMock(GUI.class);
        PlayerHandler ph = createMock(PlayerHandler.class);
        Player p1 = createMock(Player.class);
        
        Game game = new Game(gui, null, null, ph);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);        // Expectations for setup (called multiple times due to loop/recursion)
        expect(ph.getCurrentPlayer()).andReturn(p1).anyTimes();
        expect(ph.getCurrentPlayerHand()).andReturn(new ArrayList<Card>()).anyTimes();
        expect(p1.getName()).andReturn("Player 1").anyTimes();
        p1.setActivePokemon(anyObject());
        expectLastCall().anyTimes();
        
        gui.displayMessage(anyString());
        expectLastCall().anyTimes();
        gui.displayCards(anyObject());
        expectLastCall().anyTimes();
        gui.setupActivePokemon();
        expectLastCall().anyTimes();
        gui.removeAllButtons();
        expectLastCall().anyTimes();
        gui.makeActiveCard(anyObject(), anyObject());
        expectLastCall().anyTimes();
        
        // Loop 1: Selection is null
        expect(gui.waitForButtonPressed()).andReturn("Select");
        expect(gui.getLastSelectedCard()).andReturn(null);
        
        // Loop 2: Valid selection to break loop
        expect(gui.waitForButtonPressed()).andReturn("Select");
        Pokemon pkmn = new Pokemon("Pika", "Lightning", 0, 60);
        expect(gui.getLastSelectedCard()).andReturn(pkmn);
        
        replay(gui, ph, p1);
        game.selectActiveLoop();
        verify(gui, ph, p1);
    }


    @Test
    public void testHandleUseTrainerCancellation() {
        GUI gui = createMock(GUI.class);
        PlayerHandler ph = createMock(PlayerHandler.class);
        Player p1 = createMock(Player.class);
        
        Game game = new Game(gui, null, null, ph);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);

        expect(ph.getCurrentPlayer()).andReturn(p1).anyTimes();
        expect(ph.getAllPlayerPokemon()).andReturn(new ArrayList<Card>()).anyTimes();
        expect(ph.getActivePokemon()).andReturn(new Pokemon("Pika", "Lightning", 0, 60)).anyTimes();
        expect(ph.getHandPokemon()).andReturn(new ArrayList<Card>()).anyTimes();
        expect(ph.getAllPlayerEnergy()).andReturn(new ArrayList<Card>()).anyTimes();
        
        gui.displayMessage(anyString());
        expectLastCall().anyTimes();
        gui.displayCards(anyObject());
        expectLastCall().anyTimes();
        gui.removeAllButtons();
        expectLastCall().anyTimes();
        
        // Trainer that needs selection (Switch) 
        Trainer trainer = new Trainer("Switch", TrainerSubtype.ITEM, "SWITCH_ACTIVE_WITH_BENCH");
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        expect(gui.isCancelled()).andReturn(true);
        
        // Check that NO effect is called
        replay(gui, ph, p1);
        game.handleUseTrainer(trainer);
        verify(gui, ph, p1);
    }

    @Test
    public void testHandleInstantDropActiveEnergy() {
        GUI gui = createMock(GUI.class);
        PlayerHandler ph = createMock(PlayerHandler.class);
        Player p1 = createMock(Player.class);
        Pokemon pikachu = new Pokemon("Pikachu", "Lightning", 0, 60);
        Energy energy = new Energy(EnergyType.LIGHTNING);
        
        Game game = new Game(gui, null, null, ph);
        
        expect(gui.getLastSelectedCard()).andReturn(energy);
        expect(ph.getPlayerTurn()).andReturn(1).anyTimes();
        expect(ph.getCurrentPlayer()).andReturn(p1).anyTimes();
        expect(p1.getActivePokemon()).andReturn(pikachu);
        expect(ph.activeCanAddEnergy()).andReturn(true);
        
        // Effects of attachment called on ph
        ph.addEnergyToPokemon(energy, pikachu);
        expectLastCall();
        
        // GUI Refresh
        expect(p1.handAsList()).andReturn(new ArrayList<Card>());
        gui.displayCards(anyObject());
        expectLastCall();
        gui.displayActionButtons();
        expectLastCall();
        gui.setLastSelectedCardForDrag(null);
        expectLastCall();
        
        replay(gui, ph, p1);
        game.handleInstantDrop("P1_ACTIVE_DROP");
        verify(gui, ph, p1);
    }

    @Test
    public void testHandleInstantDropBenchBasic() {
        GUI gui = createMock(GUI.class);
        PlayerHandler ph = createMock(PlayerHandler.class);
        Player p1 = createMock(Player.class);
        Pokemon bulba = new Pokemon("Bulbasaur", "Grass", 0, 50);
        
        Game game = new Game(gui, null, null, ph);
        
        expect(gui.getLastSelectedCard()).andReturn(bulba);
        expect(ph.getPlayerTurn()).andReturn(1).anyTimes();
        expect(ph.getCurrentPlayer()).andReturn(p1).anyTimes();
        expect(ph.getOnlyPokemonFromBench(1)).andReturn(new ArrayList<Card>());
        
        // Effects of benching called on ph
        ph.addToBench(bulba);
        expectLastCall();
        gui.addBenchCard(p1, bulba);
        expectLastCall();
        
        // GUI Refresh
        expect(p1.handAsList()).andReturn(new ArrayList<Card>());
        gui.displayCards(anyObject());
        expectLastCall();
        gui.displayActionButtons();
        expectLastCall();
        gui.setLastSelectedCardForDrag(null);
        expectLastCall();
        
        replay(gui, ph, p1);
        game.handleInstantDrop("P1_BENCH_0_DROP");
        verify(gui, ph, p1);
    }
}

