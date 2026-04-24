package main;

import main.ui.GUI;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

import static org.easymock.EasyMock.*;

public class ComprehensiveCoverageTest {

    private GUI gui;
    private Random rand;
    private SetupGame setupGame;
    private PlayerHandler handler;
    private Game game;
    private ResourceBundle messages;

    @Before
    public void setUp() {
        gui = createMock(GUI.class);
        rand = createMock(Random.class);
        setupGame = createMock(SetupGame.class);
        handler = createMock(PlayerHandler.class);
        messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        game = new Game(gui, rand, setupGame, handler);
        game.messages = messages; // Initialize protected field
    }

    @Test
    public void testHandleInstantDropRetreat() {
        Player player = createMock(Player.class);
        Pokemon p1 = new Pokemon("Active", "Grass", 0, 50);
        Pokemon benched = new Pokemon("Bench", "Grass", 0, 50);
        ArrayList<Card> bench = new ArrayList<>();
        bench.add(benched);

        expect(gui.getLastSelectedCard()).andReturn(benched);
        expect(handler.getPlayerTurn()).andReturn(1).anyTimes();
        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(player.getActivePokemon()).andReturn(p1).anyTimes();
        expect(player.getPokemonOnBench()).andReturn(bench).anyTimes();
        
        // handleRetreat
        gui.replaceActiveCard(anyObject(), anyObject());
        expectLastCall().anyTimes();
        handler.setNewActive(anyObject());
        expectLastCall().anyTimes();
        player.retreat(anyObject());
        expectLastCall().anyTimes();
        
        expect(player.handAsList()).andReturn(new ArrayList<>()).anyTimes();
        gui.displayCards(anyObject());
        expectLastCall().anyTimes();
        gui.displayActionButtons();
        expectLastCall().anyTimes();
        gui.setLastSelectedCardForDrag(anyObject());
        expectLastCall().anyTimes();

        replay(gui, handler, player);
        game.handleInstantDrop("P1_ACTIVE_DROP");
        verify(gui, handler, player);
    }

    @Test
    public void testHandleInstantDropTrainerOnActive() {
        Player player = createMock(Player.class);
        Trainer trainer = new Trainer("Potion", "Effect");

        expect(gui.getLastSelectedCard()).andReturn(trainer);
        expect(handler.getPlayerTurn()).andReturn(1).anyTimes();
        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(player.getActivePokemon()).andReturn(null).anyTimes();

        // handleUseTrainer
        expect(handler.getAllPlayerPokemon()).andReturn(new ArrayList<>()).anyTimes();
        expect(handler.getAllPlayerEnergy()).andReturn(new ArrayList<>()).anyTimes();
        
        gui.displayMessage(anyString());
        expectLastCall().anyTimes();
        gui.removeAllButtons();
        expectLastCall().anyTimes();
        gui.displayCards(anyObject());
        expectLastCall().anyTimes();
        gui.displayConfirmAndCancelButton();
        expectLastCall().anyTimes();
        gui.waitForAction();
        expectLastCall().anyTimes();
        expect(gui.waitForButtonPressed()).andReturn("Cancel").anyTimes();
        expect(gui.isCancelled()).andReturn(true).anyTimes();
        
        player.removeFromHand(anyObject());
        expectLastCall().anyTimes();
        
        expect(player.handAsList()).andReturn(new ArrayList<>()).anyTimes();
        gui.displayActionButtons();
        expectLastCall().anyTimes();
        gui.setLastSelectedCardForDrag(anyObject());
        expectLastCall().anyTimes();

        replay(gui, handler, player);
        game.handleInstantDrop("P1_ACTIVE_DROP");
        verify(gui, handler, player);
    }

    @Test
    public void testHandleInstantDropEvolveOnBench() {
        Player player = createMock(Player.class);
        Pokemon stage1 = new Pokemon("Ivysaur", "Grass", 1, 60);
        stage1.setEvolvesFrom("Bulbasaur");
        Pokemon benched = new Pokemon("Bulbasaur", "Grass", 0, 40);
        ArrayList<Card> bench = new ArrayList<>();
        bench.add(benched);

        expect(gui.getLastSelectedCard()).andReturn(stage1);
        expect(handler.getPlayerTurn()).andReturn(1).anyTimes();
        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(handler.getOnlyPokemonFromBench(1)).andReturn(bench).anyTimes();
        
        // handleEvolve
        expect(handler.getOnlyPreEvolutionsFromActivePlayer(anyObject())).andReturn(bench).anyTimes();
        expect(handler.evolve(anyObject(), anyObject())).andReturn("Success").anyTimes();
        
        expect(player.handAsList()).andReturn(new ArrayList<>()).anyTimes();
        gui.displayCards(anyObject());
        expectLastCall().anyTimes();
        gui.displayActionButtons();
        expectLastCall().anyTimes();
        gui.setLastSelectedCardForDrag(anyObject());
        expectLastCall().anyTimes();

        replay(gui, handler, player);
        game.handleInstantDrop("P1_BENCH_0");
        verify(gui, handler, player);
    }

    @Test
    public void testHandleInstantDropTrainerOnBench() {
        Player player = createMock(Player.class);
        Trainer trainer = new Trainer("Potion", "Effect");
        ArrayList<Card> bench = new ArrayList<>();

        expect(gui.getLastSelectedCard()).andReturn(trainer);
        expect(handler.getPlayerTurn()).andReturn(1).anyTimes();
        expect(handler.getCurrentPlayer()).andReturn(player).anyTimes();
        expect(handler.getOnlyPokemonFromBench(1)).andReturn(bench).anyTimes();

        // handleUseTrainer
        expect(handler.getAllPlayerPokemon()).andReturn(new ArrayList<>()).anyTimes();
        expect(handler.getAllPlayerEnergy()).andReturn(new ArrayList<>()).anyTimes();
        
        gui.displayMessage(anyString());
        expectLastCall().anyTimes();
        gui.removeAllButtons();
        expectLastCall().anyTimes();
        gui.displayCards(anyObject());
        expectLastCall().anyTimes();
        gui.displayConfirmAndCancelButton();
        expectLastCall().anyTimes();
        gui.waitForAction();
        expectLastCall().anyTimes();
        expect(gui.waitForButtonPressed()).andReturn("Cancel").anyTimes();
        expect(gui.isCancelled()).andReturn(true).anyTimes();
        
        player.removeFromHand(anyObject());
        expectLastCall().anyTimes();
        
        expect(player.handAsList()).andReturn(new ArrayList<>()).anyTimes();
        gui.displayActionButtons();
        expectLastCall().anyTimes();
        gui.setLastSelectedCardForDrag(anyObject());
        expectLastCall().anyTimes();

        replay(gui, handler, player);
        game.handleInstantDrop("P1_BENCH_0");
        verify(gui, handler, player);
    }

    @Test
    public void testDisplayTrainerEnergySelectionError() {
        Trainer trainer = new Trainer("Energy Search", "Effect");
        ArrayList<Card> energy = new ArrayList<>();
        
        gui.displayMessage(anyString());
        expectLastCall().anyTimes();
        
        replay(gui);
        game.displayTrainerEnergySelection(trainer, energy);
        verify(gui);
    }
}
