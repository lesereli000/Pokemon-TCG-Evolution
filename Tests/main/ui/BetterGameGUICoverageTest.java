package main.ui;

import main.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import static org.junit.Assert.*;

public class BetterGameGUICoverageTest {

    private GameGUI gui;
    private GameGUITestHarness harness;

    @Before
    public void setUp() {
        gui = new GameGUI();
        harness = new GameGUITestHarness(gui);
        try {
            gui.createGUI();
        } catch (Exception e) {}
        gui.messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
    }

    @After
    public void tearDown() {
        if (gui != null && gui.getFrame() != null) {
            gui.getFrame().dispose();
        }
    }

    @Test(timeout = 10000)
    public void testComprehensiveGUIMethods() throws Exception {
        // test displayCardReport
        Pokemon p = new Pokemon("Pika", "Lightning", 0, 50);
        harness.dismissDialogAsync(100);
        gui.displayCardReport(p);

        // test displayPossibleAttacks
        ArrayList<Attack> attacks = new ArrayList<>();
        ArrayList<Energy> costs = new ArrayList<>();
        costs.add(new Energy(EnergyType.LIGHTNING));
        attacks.add(new Attack("Thunder", costs, 20));
        gui.displayPossibleAttacks(attacks);

        // test displayAttackMessage
        Player p1 = new Player("P1");
        p1.getHand().addCard(p);
        p1.setActivePokemon(p);
        
        Player p2 = new Player("P2");
        Pokemon p2p = new Pokemon("Squirtle", "Water", 0, 50);
        p2p.getEnergyMap().put(EnergyType.WATER, 2);
        p2p.getEnergyMap().put(EnergyType.FIRE, 1);
        p2.getHand().addCard(p2p);
        p2.setActivePokemon(p2p);
        
        harness.dismissDialogAsync(100);
        gui.displayAttackMessage(p1, p2, attacks.get(0));

        // test displayRetreatEnergy
        harness.dismissDialogAsync(100);
        gui.displayRetreatEnergy(p, true);
        harness.dismissDialogAsync(100);
        gui.displayRetreatEnergy(p, false);

        // test displayDeadActiveInfo
        harness.dismissDialogAsync(100);
        gui.displayDeadActiveInfo(p2);

        // test displayWinningMessage
        harness.dismissDialogAsync(100);
        gui.displayWinningMessage(p1, p2);

        // test setters and flags
        gui.setDeckColor(Color.BLUE);
        gui.setupActivePokemon();
        assertFalse(gui.hasCardSelected());
        assertFalse(gui.isCancelled());
        
        // Exercise BoardPanel drawing with different HP/Energy
        p2p.takeDamage(30, EnergyType.LIGHTNING); // 20/50 = 40% (Yellow)
        gui.getFrame().repaint();
        Thread.sleep(100);
        p2p.takeDamage(15, EnergyType.LIGHTNING); // 5/50 = 10% (Red)
        gui.getFrame().repaint();
        Thread.sleep(100);
    }

    @Test(timeout = 10000)
    public void testConfirmAndCancelButtons() throws Exception {
        gui.displayConfirmAndCancelButton();
        
        String cancelMsg = gui.getMessages().getString("cancel");
        harness.clickButtonSync(cancelMsg);
        assertTrue(gui.isCancelled());

        gui.displayConfirmAndCancelButton();
        String confirmMsg = gui.getMessages().getString("confirmSelection");
        harness.clickButtonSync(confirmMsg);
        assertFalse(gui.isCancelled());
    }

    @Test(timeout = 10000)
    public void testLocaleAndDeckOptions() throws Exception {
        // Locale selection
        harness.dismissDialogAsync(100); // Dismiss "Select a language" dialog
        harness.clickButtonAsync("English", 300);
        Locale loc = gui.displayLocaleOptions();
        assertEquals(Locale.US, loc);

        // Deck selection
        harness.dismissDialogAsync(100); // Dismiss "Select a deck" dialog
        harness.clickButtonAsync("Fire", 300);
        String deck = gui.displayDeckOptions();
        assertEquals("FireDeck.txt", deck);
    }

    @Test(timeout = 10000)
    public void testBoardPanelMouseInteractions() throws Exception {
        BoardPanel panel = harness.getHandPanel(); // handPanel is a BoardPanel
        
        // Simulate mouse events for coverage in BoardPanel's MouseAdapter
        MouseEvent me = new MouseEvent(panel, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, 10, 10, 1, false);
        panel.dispatchEvent(me);
        
        me = new MouseEvent(panel, MouseEvent.MOUSE_DRAGGED, System.currentTimeMillis(), 0, 20, 20, 1, false);
        panel.dispatchEvent(me);
        
        me = new MouseEvent(panel, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, 20, 20, 1, false);
        panel.dispatchEvent(me);
    }
}
