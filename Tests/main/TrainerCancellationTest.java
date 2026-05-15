package main;

import main.ui.*;

import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

public class TrainerCancellationTest {

    private Game game;
    private GUI gui;
    private PlayerHandler playerHandler;
    private Player p1;

    @Before
    public void setUp() {
        gui = createNiceMock(GUI.class);
        playerHandler = createNiceMock(PlayerHandler.class);
        game = new Game(gui, null, playerHandler);
        game.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        
        p1 = new Player("Player 1");
    }

    @Test
    public void testSwitchCancellation() {
        Trainer switchCard = new Trainer("Switch", "Switch 1 of your own Benched Pokemon with your Active Pokemon.");
        p1.getHand().addCard(switchCard);
        
        Pokemon pika = new Pokemon("Pika", "Lightning", 0, 60);
        p1.getHand().addCard(pika); // Add to hand first so setActivePokemon doesn't fail
        p1.setActivePokemon(pika);
        
        expect(playerHandler.getCurrentPlayer()).andReturn(p1).anyTimes();
        expect(playerHandler.getAllPlayerPokemon()).andReturn(new ArrayList<>()).anyTimes();
        expect(playerHandler.getActivePokemon()).andReturn(pika).anyTimes();
        
        // Mock GUI to return cancelled state
        expect(gui.getLastSelectedCard()).andReturn(switchCard).anyTimes();
        expect(gui.isCancelled()).andReturn(true).anyTimes();
        
        replay(gui, playerHandler);
        
        // This triggers handleUseTrainer
        game.handleInstantDrop("BOARD_DROP");
        
        verify(gui, playerHandler);
        
        // Assertions: Pokemon should still be active, and card should stay in hand
        assertTrue("Player should still have active pokemon", p1.hasActive());
        assertEquals("Active pokemon should remain Pika", pika, p1.getActivePokemon());
        assertTrue("Switch card should stay in hand because action was cancelled", p1.getHand().getCards().contains(switchCard));
    }
}
