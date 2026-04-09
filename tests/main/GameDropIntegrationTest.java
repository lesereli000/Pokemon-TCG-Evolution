package main;

import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

public class GameDropIntegrationTest {

    private Game game;
    private GUI gui;
    private PlayerHandler playerHandler;
    private Player p1;
    private Player p2;

    @Before
    public void setUp() {
        // Use NiceMocks for both to avoid strict call-counting issues
        gui = createNiceMock(GUI.class);
        playerHandler = createNiceMock(PlayerHandler.class);
        game = new Game(gui, null, null, playerHandler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        
        p1 = new Player("Player 1");
        // Initialize deck with cards so drawCard works
        for(int i=0; i<10; i++) {
            p1.getDeck().addCard(new Energy(EnergyType.FIGHTING));
        }
        
        p2 = new Player("Player 2");
        p1.activePokemon = new Pokemon("Pika 1", "Lightning", 0, 60);
        p2.activePokemon = new Pokemon("Squirtle 2", "Water", 0, 50);
    }

    @Test
    public void testEnergyDropOnActive() {
        Energy energy = new Energy(EnergyType.LIGHTNING);
        
        expect(gui.getLastSelectedCard()).andReturn(energy).anyTimes();
        expect(playerHandler.getPlayerTurn()).andReturn(1).anyTimes();
        expect(playerHandler.getCurrentPlayer()).andReturn(p1).anyTimes();
        expect(playerHandler.activeCanAddEnergy()).andReturn(true).anyTimes();
        
        // Logical side effect
        playerHandler.addEnergyToPokemon(energy, p1.activePokemon);
        expectLastCall().once();
        
        replay(gui, playerHandler);
        game.handleInstantDrop("P1_ACTIVE_DROP");
        verify(gui, playerHandler);
    }

    @Test
    public void testEnergyDropOnBench() {
        Energy energy = new Energy(EnergyType.LIGHTNING);
        Pokemon benchPkmn = new Pokemon("Bench 1", "Grass", 0, 40);
        ArrayList<Card> benchList = new ArrayList<>();
        benchList.add(benchPkmn);
        
        expect(gui.getLastSelectedCard()).andReturn(energy).anyTimes();
        expect(playerHandler.getPlayerTurn()).andReturn(1).anyTimes();
        expect(playerHandler.getCurrentPlayer()).andReturn(p1).anyTimes();
        expect(playerHandler.getOnlyPokemonFromBench(1)).andReturn(benchList).anyTimes();
        expect(playerHandler.activeCanAddEnergy()).andReturn(true).anyTimes();
        
        playerHandler.addEnergyToPokemon(energy, benchPkmn);
        expectLastCall().once();
        
        replay(gui, playerHandler);
        game.handleInstantDrop("P1_BENCH_0_DROP");
        verify(gui, playerHandler);
    }

    @Test
    public void testTrainerBoardDrop() {
        Trainer bill = new Trainer("Bill", "Draw 2 cards.");
        p1.getHand().addCard(bill);
        
        expect(gui.getLastSelectedCard()).andReturn(bill);
        expect(playerHandler.getPlayerTurn()).andReturn(1).anyTimes();
        expect(playerHandler.getCurrentPlayer()).andReturn(p1).anyTimes();
        
        replay(gui, playerHandler);
        game.handleInstantDrop("BOARD_DROP");
        verify(gui, playerHandler);
        
        assertFalse("Bill should be removed from hand", p1.getHand().getCards().contains(bill));
        // Started with 1 card (Bill), Bill removed (-1), drew 2 (+2) = 2
        assertEquals("Bill should have triggered 2 draws", 2, p1.getHand().getCards().size());
    }

    @Test
    public void testEvolveDropOnActive() {
        Pokemon raichu = new Pokemon("Raichu", "Lightning", 1, 90);
        raichu.setEvolvesFrom("Pika 1"); // Matches active pokemon name
        
        expect(gui.getLastSelectedCard()).andReturn(raichu).anyTimes();
        expect(playerHandler.getPlayerTurn()).andReturn(1).anyTimes();
        expect(playerHandler.getCurrentPlayer()).andReturn(p1).anyTimes();
        
        ArrayList<Card> preEvolutions = new ArrayList<>();
        preEvolutions.add(p1.activePokemon);
        expect(playerHandler.getOnlyPreEvolutionsFromActivePlayer(raichu)).andReturn(preEvolutions).anyTimes();
        
        // Logical side effect: evolve should be called directly with p1.activePokemon
        expect(playerHandler.evolve(raichu, p1.activePokemon)).andReturn("Active").once();
        
        replay(gui, playerHandler);
        game.handleInstantDrop("P1_ACTIVE_DROP");
        verify(gui, playerHandler);
    }
}
