package main;

import org.junit.Test;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

public class GameDropIntegrationTest {

    @Test
    public void testInstantEnergyAttachToActive() {
        GUI mockGui = createNiceMock(GUI.class);
        PlayerHandler mockHandler = createNiceMock(PlayerHandler.class);
        Player mockPlayer = createNiceMock(Player.class);
        Pokemon mockActive = createNiceMock(Pokemon.class);
        Energy energy = new Energy(EnergyType.LIGHTNING);
        
        // Setup state
        expect(mockHandler.getCurrentPlayer()).andReturn(mockPlayer).anyTimes();
        expect(mockPlayer.getActivePokemon()).andReturn(mockActive).anyTimes();
        expect(mockHandler.activeCanAddEnergy()).andReturn(true).anyTimes();
        
        // Trigger action
        expect(mockGui.waitForButtonPressed()).andReturn("P1_ACTIVE_DROP");
        expect(mockGui.getLastSelectedCard()).andReturn(energy);
        
        // Expectation: Instant attachment
        mockHandler.addEnergyToPokemon(energy, mockActive);
        expectLastCall().once();
        
        // Verification that it does NOT display the selection logic
        // We will use a mock that would fail if un-expected methods are called,
        // or just verify the specific call.
        
        replay(mockGui, mockHandler, mockPlayer, mockActive);
        
        Game game = new Game(mockGui, new Random(), createNiceMock(SetupGame.class), mockHandler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        
        game.mainGameLoop();
        
        verify(mockHandler);
    }

    @Test
    public void testInstantBenchDrop() {
        GUI mockGui = createNiceMock(GUI.class);
        PlayerHandler mockHandler = createNiceMock(PlayerHandler.class);
        Pokemon pikachu = new Pokemon("Pikachu", "Lightning", 0, 60);
        
        // Trigger action
        expect(mockGui.waitForButtonPressed()).andReturn("P1_BENCH_0_DROP");
        expect(mockGui.getLastSelectedCard()).andReturn(pikachu);
        
        // Expectation: Instant bench addition
        mockHandler.addToBench(pikachu);
        expectLastCall().once();
        mockGui.addBenchCard(anyObject(Player.class), eq(pikachu));
        expectLastCall().once();
        
        replay(mockGui, mockHandler);
        
        Game game = new Game(mockGui, new Random(), createNiceMock(SetupGame.class), mockHandler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        
        game.mainGameLoop();
        
        verify(mockHandler, mockGui);
    }
}
