package main;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.Color;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.*;

public class GameGUISystemTest {

    private GameGUI gui;
    private GameGUITestHarness harness;

    @Before
    public void setUp() {
        gui = new GameGUI();
        harness = new GameGUITestHarness(gui);
        gui.createGUI();
    }

    @After
    public void tearDown() {
        if (gui != null) {
            gui.closeWindow();
        }
    }

    @Test
    public void testGUIInitialization() throws Exception {
        assertNotNull("Frame should be initialized", harness.getFrame());
        assertTrue("Frame should be visible", harness.getFrame().isVisible());
        assertEquals("Initial deck color should be RED", Color.RED, gui.getDeckColor());
    }

    @Test(timeout = 5000)
    public void testWaitForPassTurn() throws Exception {
        ResourceBundle messages = gui.getMessages();
        String passTurnMsg = messages.getString("passTurn");

        // The GUI will block until the button is clicked.
        // We instruct the harness to click it in 100ms on a separate thread.
        harness.clickButtonAsync(passTurnMsg, 100);

        // This will block, but unblock once the async click triggers
        gui.waitForPassTurn();

        // If we reach this assertion within the 2000ms timeout, the behavior succeeds
        assertTrue("GUI unblocked successfully after button click.", true);
    }

    @Test(timeout = 5000)
    public void testLocaleSelectionEnglish() throws Exception {
        harness.dismissDialogAsync(50);
        // Test language button clicking (US English)
        harness.clickButtonAsync("English", 150);
        Locale selectedLocale = gui.displayLocaleOptions();

        assertEquals("Should select Locale.US", Locale.US, selectedLocale);
    }

    @Test(timeout = 5000)
    public void testLocaleSelectionGerman() throws Exception {
        harness.dismissDialogAsync(50);
        // Test language button clicking (German)
        harness.clickButtonAsync("Deutsch", 150);
        Locale selectedLocale = gui.displayLocaleOptions();

        assertEquals("Should select Locale.GERMANY", Locale.GERMANY, selectedLocale);
    }

    @Test(timeout = 5000)
    public void testDisplayConfirmAndCancelButton() throws Exception {
        ResourceBundle messages = gui.getMessages();
        String confirmMsg = messages.getString("confirmSelection");

        // The method displays choices, then we select confirm.
        gui.displayConfirmAndCancelButton();

        // Buttons should be available now.
        assertNotNull("Confirm button should exist", harness.getButtonWithText(confirmMsg));

        // Click sync since the display method did not block
        harness.clickButtonSync(confirmMsg);

        // Wait-for-action flag should be true after click
        assertFalse("Action should not be cancelled", gui.isCancelled());
    }

    @Test(timeout = 5000)
    public void testCreateFlipButton() throws Exception {
        ResourceBundle messages = gui.getMessages();
        String flipCoinMsg = messages.getString("flipCoin");

        harness.clickButtonAsync(flipCoinMsg, 100);
        gui.createFlipButton();

        assertTrue("createFlipButton should return after button click", true);
    }

    @Test(timeout = 5000)
    public void testDisplayCardReport() throws Exception {
        Pokemon p = new Pokemon("Pikachu", "Lightning", 0, 60);
        harness.dismissDialogAsync(100);
        gui.displayCardReport(p);

        assertTrue("displayCardReport should return after dialog dismissal", true);
    }

    @Test(timeout = 5000)
    public void testCardSelectionAndActionButtons() throws Exception {
        // Setup players and turn
        Player p1 = new Player("Player 1");
        Player p2 = new Player("Player 2");
        gui.setPlayers(p1, p2);
        gui.displayActionButtons(); // Enter active turn mode

        // Create a Pokemon card button
        Pokemon pikachu = new Pokemon("Pikachu", "Lightning", 0, 60);
        gui.createLinkedButtonCard("Pikachu", pikachu);

        // Click the card button
        harness.clickButtonSync("Pikachu");

        // Check if "Add to Bench" action button appeared
        ResourceBundle messages = gui.getMessages();
        String addPokBenchMsg = messages.getString("addPokBench");
        assertNotNull("Add to Bench button should exist for Basic Pokemon", harness.getButtonWithText(addPokBenchMsg));

        // Now try an Energy card
        Energy energy = new Energy(EnergyType.LIGHTNING);
        gui.createLinkedButtonCard("Energy", energy);
        harness.clickButtonSync("Energy");

        String addEnergyMsg = messages.getString("addEnergy");
        assertNotNull("Add Energy button should exist for Energy card", harness.getButtonWithText(addEnergyMsg));

        // Now try a Trainer card
        Trainer trainer = new Trainer("Potion", "Remove up to 2 damage counters from 1 of your Pokemon.");
        gui.createLinkedButtonCard("Potion", trainer);
        harness.clickButtonSync("Potion");

        String playTrainerMsg = messages.getString("playTrainer");
        assertNotNull("Play Trainer button should exist for Trainer card", harness.getButtonWithText(playTrainerMsg));
    }

    @Test(timeout = 5000)
    public void testUpdateTurnAndState() throws Exception {
        gui.updateTurn(1);
        assertEquals("Player turn should be 1", 1, gui.getPlayerTurn());

        Player p1 = new Player("P1");
        Player p2 = new Player("P2");
        gui.setPlayers(p1, p2);

        gui.makeActiveCard(p1, new Pokemon("P1 Active", "Grass", 0, 50));
        assertEquals("Player 1 active color should be GREEN", Color.GREEN, harness.getPlayer1ActiveColor());

        gui.setDeckColor(Color.BLUE);
        assertEquals("Deck color should be BLUE", Color.BLUE, gui.getDeckColor());
    }

    @Test(timeout = 5000)
    public void testDisplayWinningMessage() throws Exception {
        Player p1 = new Player("Winner");
        Player p2 = new Player("Loser");

        harness.dismissDialogAsync(100);
        gui.displayWinningMessage(p1, p2);

        assertTrue("displayWinningMessage should return after dialog dismissal", true);
    }
}
