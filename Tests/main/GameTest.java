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
        Player player1 = createMock(Player.class);
        Random rand = createMock(Random.class);
        ArrayList<Card> hand = createMock(ArrayList.class);

        gui.removeAllButtons();
        expect(handler.getCurrentPlayerHand()).andReturn(hand);
        gui.displayCards(hand);

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
        expect(gui.getLastSelectedCard()).andReturn(p);
        gui.displayMessage("This is not a basic Pokemon and can not place card on bench!");
        replay(p, gui);

        Game game = new Game(gui, rand, setupGame, handler);
        game.handleBenchAction();

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

        expect(gui.getLastSelectedCard()).andReturn(p);
        expect(p.getStage()).andReturn(0);
        handler.addToBench(p);
        expect(handler.getPlayerTurn()).andReturn(1);
        gui.addBenchCard(p, 1);

        replay(p, player, gui, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.handleBenchAction();

        verify(player, p, gui, handler);
    }

    @Test
    public void testCantAddEnergy() {
        GameGUI gui = createMock(GameGUI.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        Energy e = createMock(Energy.class);

        expect(gui.getLastSelectedCard()).andReturn(e);
        expect(handler.activeCanAddEnergy()).andReturn(false);
        gui.displayMessage("Unable to add energy!");
        replay(gui, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.handleEnergyAction();
        verify(gui, handler);
    }

    @Test
    public void testSuccessAddingEnergy() {
        // Create mocks
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        Player player = createMock(Player.class);
        Pokemon p = createMock(Pokemon.class);
        Energy e = createMock(Energy.class);
        ArrayList<Card> pokemon = new ArrayList<>();

        expect(gui.getLastSelectedCard()).andReturn(e);
        expect(handler.activeCanAddEnergy()).andReturn(true);
        expect(handler.getCurrentPlayer()).andReturn(player);
        expect(handler.getOnlyPokemonFromBench(1)).andReturn(pokemon);
        expect(player.getActivePokemon()).andReturn(p);
        pokemon.add(p);

        //display add energy info
        gui.displayMessage("Select Pokemon to add Energy to");
        gui.removeAllButtons();
        gui.displayCards(pokemon);
        gui.displayConfirmButton();
        gui.waitForAction();

        expect(gui.getLastSelectedCard()).andReturn(p);
        handler.addEnergyToPokemon(e, p);
        gui.displayCardReport(p);
        replay(gui, player, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.handleEnergyAction();

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
        ArrayList<Card> hand = createMock(ArrayList.class);
        //display directions
        gui.displayMessage("Select a basic Pokemon to be your Active Pokemon");

        //Display hand pre selection
        gui.removeAllButtons();
        gui.displayCards(hand);
        gui.setupActivePokemon();
        expect(gui.waitForButtonPressed()).andReturn("");
        expect(gui.getLastSelectedCard()).andReturn(p);
        //check basic pokemon
        expect(p.getStage()).andReturn(0);

        //make new active
        expect(handler.getPlayerTurn()).andReturn(1);
        player.setActivePokemon(p);
        gui.makeActiveCard(p, 1);

        //display hand post selection
        gui.removeAllButtons();
        expect(handler.getCurrentPlayerHand()).andReturn(hand).anyTimes();
        gui.displayCards(hand);

        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
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
        gui.makeActiveCard(p, 1);

        //display hand post selection
        gui.removeAllButtons();

        gui.displayCards(hand);
        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(handler.getPlayerTurn()).andReturn(1);

        replay(gui, player, p, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.selectActiveLoop();
        verify(gui, player, p, handler);
    }

    @Test
    public void testDisplaySetupResults() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);

        Player player = createMock(Player.class);
        expect(player.getName()).andReturn("Ash");
        gui.displayMessage("The result was Heads Ash goes first!");
        replay(gui, player);

        Game game = new Game(gui, rand, setupGame, handler);
        game.displaySetupResults("Heads", player);

        verify(gui, player);
    }

    @Test
    public void testSetupGame() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);

        Player player = createMock(Player.class);
        Pokemon p = createMock(Pokemon.class);
        ArrayList<Card> hand = createMock(ArrayList.class);
        // Setup expectations

        //setupFlipButton()
        gui.createFlipButton();

        //coinflip
        expect(setupGame.completeGameSetup()).andReturn("Heads");
        handler.completePlayerSetup("Heads");

        //player
        expect(handler.getCurrentPlayer()).andReturn(player);

        //displaySetupResults
        expect(player.getName()).andReturn("Player 1");
        gui.displayMessage("The result was Heads Player 1 goes first!");

        //selectActiveLoop()
        //displayActiveDirections()
        gui.displayMessage("Select a basic Pokemon to be your Active Pokemon");
        //displayHand()
        gui.removeAllButtons();
        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(handler.getCurrentPlayerHand()).andReturn(hand).anyTimes();
        gui.displayCards(hand);
        gui.setupActivePokemon();
        expect(gui.waitForButtonPressed()).andReturn("");
        expect(gui.getLastSelectedCard()).andReturn(p);
        //check basic
        expect(p.getStage()).andReturn(0);
        expect(handler.getPlayerTurn()).andReturn(1);
        player.setActivePokemon(p);
        gui.makeActiveCard(p, 1);
        gui.removeAllButtons();
        gui.displayCards(hand);

        replay(gui, rand, setupGame, handler, player, p);

        Game game = new Game(gui, rand, setupGame, handler);
        game.gameOver = true;
        game.setupGame();

        verify(gui, rand, setupGame, handler, player, p);
    }

    @Test
    public void testMainGameLoopAddToBench() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
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
        expect(handler.getPlayerTurn()).andReturn(1);
        gui.addBenchCard(p, 1);

        replay(gui, rand, setupGame, handler, p, player);

        Game game = new Game(gui, rand, setupGame, handler);
        game.mainGameLoop();

        verify(gui, rand, setupGame, handler, p, player);
    }

    @Test
    public void testMainGameLoopAddEnergy() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
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
        expect(handler.getCurrentPlayer()).andReturn(player);
        expect(handler.getOnlyPokemonFromBench(1)).andReturn(hand);
        expect(player.getActivePokemon()).andReturn(p);

        gui.displayMessage("Select Pokemon to add Energy to");
        gui.displayConfirmButton();
        gui.removeAllButtons();
        hand.add(p);
        gui.displayCards(hand);
        gui.waitForAction();
        expect(gui.getLastSelectedCard()).andReturn(p);
        handler.addEnergyToPokemon(e, p);
        gui.displayCardReport(p);

        replay(gui, player, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.mainGameLoop();

        verify(gui, player, handler);
    }

    @Test
    public void testMainGameLoopAddEnergyWrongType() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
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
        game.mainGameLoop();

        verify(gui, handler);
    }

    @Test
    public void testMainGameLoopAddEnergyCantAddEnergy() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
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
        game.mainGameLoop();

        verify(gui, handler);
    }

    @Test
    public void testPassTurnActionNotFirstTurn() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);

        expect(handler.passTurn()).andReturn(true);
        handler.drawCardFromDeck();
        replay(handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.handlePassTurnAction();
        verify(handler);
    }

    @Test
    public void testPassTurnActionFirstTurn() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        Pokemon p = createMock(Pokemon.class);
        ArrayList<Card> hand = createMock(ArrayList.class);
        Player player = createMock(Player.class);

        expect(handler.passTurn()).andReturn(false);

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
        expect(handler.getCurrentPlayer()).andReturn(player);
        expect(handler.getPlayerTurn()).andReturn(1);
        handler.drawCardFromDeck();
        player.setActivePokemon(p);
        gui.makeActiveCard(p,1);

        //display hand
        gui.removeAllButtons();
        expect(handler.getCurrentPlayerHand()).andReturn(hand);
        gui.displayCards(hand);

        replay(gui, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.handlePassTurnAction();
        verify(gui, handler);
    }

    @Test
    public void testHandleAttackActionPlayerCannotAttack() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
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
        game.mainGameLoop();

        verify(gui, handler);
    }

    @Test
    public void testHandleAttackActionPlayerCanAttack() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        Attack attack = createMock(Attack.class);
        Player player1 = createMock(Player.class);
        Player player2 = createMock(Player.class);
        ArrayList<Attack> attacks = new ArrayList<>();
        attacks.add(attack);

        expect(handler.getCurrentPlayerHand()).andReturn(new ArrayList<>());
        gui.removeAllButtons();
        gui.displayCards(new ArrayList<>());
        gui.displayActionButtons();
        expect(gui.waitForButtonPressed()).andReturn("Attack");

        expect(handler.playerCanAttack()).andReturn(true);

        // displayAttackInfo()
        gui.removeAllButtons();
        expect(handler.getCurrentPlayerAttacks()).andReturn(attacks);
        gui.displayPossibleAttacks(attacks);
        gui.displayConfirmButton();
        gui.waitForAction();
        expect(gui.getLastSelectedAttack()).andReturn(attack);

        expect(handler.getCurrentPlayer()).andReturn(player1);
        expect(handler.getDefendingPlayer()).andReturn(player2);
        gui.displayAttackMessage(player1, player2, attack);

        expect(handler.attackOpponent(attack)).andReturn(true);
        expect(handler.isDefendingDead()).andReturn(false);
        handler.swapPlayerTurns();

        replay(gui, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        game.mainGameLoop();

        verify(gui, handler);
    }

    @Test
    public void testHandleRetreatActionSuccess() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        Player player = createMock(Player.class);
        Pokemon activePokemon = createMock(Pokemon.class);
        Card newActive = createMock(Card.class);
        ArrayList<Card> bench = new ArrayList<>();
        bench.add(newActive);

        expect(handler.getCurrentPlayer()).andReturn(player);
        expect(player.getActivePokemon()).andReturn(activePokemon);
        expect(activePokemon.canRetreat()).andReturn(true);
        expect(handler.canRetreat()).andReturn(true);

        gui.displayRetreatEnergy(activePokemon, true);

        // Retreat process
        gui.removeAllButtons();
        gui.displayMessage("Select new active Pokemon");
        expect(handler.getOnlyPokemonFromBench(1)).andReturn(bench);
        gui.displayCards(bench);
        gui.displayConfirmButton();
        gui.waitForAction();
        expect(gui.getLastSelectedCard()).andReturn(newActive);
        gui.replaceActiveCard(newActive, 1);
        expect(handler.getPlayerTurn()).andReturn(1);
        handler.setNewActive(newActive);

        replay(gui, handler, player, activePokemon);

        Game game = new Game(gui, rand, setupGame, handler);
        game.handleRetreatAction();

        verify(gui, handler, player, activePokemon);
    }

    @Test
    public void testHandleRetreatActionFailed() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        Player player = createMock(Player.class);
        Pokemon activePokemon = createMock(Pokemon.class);

        expect(handler.getCurrentPlayer()).andReturn(player);
        expect(player.getActivePokemon()).andReturn(activePokemon);
        expect(activePokemon.canRetreat()).andReturn(false);
        //expect(handler.canRetreat()).andReturn(false); Only called once because previous statement

        gui.displayRetreatEnergy(activePokemon, false);

        replay(gui, handler, player, activePokemon);

        Game game = new Game(gui, rand, setupGame, handler);
        game.handleRetreatAction();

        verify(gui, handler, player, activePokemon);
    }

    @Test
    public void testRetreatPokemonSelectNewActive() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        Card newActive = createMock(Card.class);
        ArrayList<Card> bench = new ArrayList<>();
        bench.add(newActive);

        expect(handler.getOnlyPokemonFromBench(1)).andReturn(bench);
        gui.removeAllButtons();
        gui.displayMessage("Select new active Pokemon");
        gui.displayCards(bench);
        gui.displayConfirmButton();
        gui.waitForAction();
        expect(gui.getLastSelectedCard()).andReturn(newActive);
        expect(handler.getPlayerTurn()).andReturn(1);
        gui.replaceActiveCard(newActive, 1);

        replay(gui, handler);

        Game game = new Game(gui, rand, setupGame, handler);
        Card result = game.retreatPokemon();

        assertEquals(newActive, result);
        verify(gui, handler);
    }

    @Test
    public void testHandleDeadActiveWithBasicPokemon() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        Pokemon selectedPokemon = createMock(Pokemon.class);

        ArrayList<Card> bench = new ArrayList<>();
        bench.add(selectedPokemon);

        gui.removeAllButtons();
        expect(handler.getOnlyPokemonFromBench(2)).andReturn(bench);
        gui.displayCards(bench);
        gui.displayConfirmButton();
        gui.waitForAction();
        expect(gui.getLastSelectedCard()).andReturn(selectedPokemon);

        expect(handler.getPlayerTurn()).andReturn(1);
        gui.makeActiveCard(selectedPokemon, 2);
        gui.removeBenchCard(selectedPokemon, 2);

        expect(selectedPokemon.getStage()).andReturn(0);
        handler.killDefenderActive(selectedPokemon);

        replay(gui, handler, selectedPokemon);

        Game game = new Game(gui, rand, setupGame, handler);
        game.handleDeadActive();

        verify(gui, handler, selectedPokemon);
    }

    @Test
    public void testHandleDeadActiveWithNonBasicPokemon() {
        GameGUI gui = createMock(GameGUI.class);
        Random rand = createMock(Random.class);
        SetupGame setupGame = createMock(SetupGame.class);
        PlayerHandler handler = createMock(PlayerHandler.class);
        Pokemon selectedPokemon = createMock(Pokemon.class);

        ArrayList<Card> bench = new ArrayList<>();
        bench.add(selectedPokemon);

        gui.removeAllButtons();
        expect(handler.getOnlyPokemonFromBench(2)).andReturn(bench);
        gui.displayCards(bench);
        gui.displayConfirmButton();
        gui.waitForAction();
        expect(gui.getLastSelectedCard()).andReturn(selectedPokemon);

        expect(handler.getPlayerTurn()).andReturn(0);
        gui.makeActiveCard(selectedPokemon, 1);
        gui.removeBenchCard(selectedPokemon, 1);

        // Not a basic Pokémon
        expect(selectedPokemon.getStage()).andReturn(1);
        gui.displayMessage("Not a basic Pokemon!");

        replay(gui, handler, selectedPokemon);

        Game game = new Game(gui, rand, setupGame, handler);
        game.handleDeadActive();

        verify(gui, handler, selectedPokemon);
    }

}

