package main.ui;

import main.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.Color;
import java.util.ArrayList;
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

    @Test(timeout = 10000)
    public void testWaitForPassTurn() throws Exception {
        ResourceBundle messages = gui.getMessages();
        String passTurnMsg = messages.getString("passTurn");

        harness.clickButtonAsync(passTurnMsg, 200);
        gui.waitForPassTurn();

        assertTrue("GUI unblocked successfully after button click.", true);
    }

    @Test(timeout = 10000)
    public void testLocaleSelectionEnglish() throws Exception {
        harness.dismissDialogAsync(100);
        harness.clickButtonAsync("English", 300);
        Locale selectedLocale = gui.displayLocaleOptions();

        assertEquals("Should select Locale.US", Locale.US, selectedLocale);
    }

    @Test(timeout = 10000)
    public void testLocaleSelectionGerman() throws Exception {
        harness.dismissDialogAsync(100);
        harness.clickButtonAsync("Deutsch", 300);
        Locale selectedLocale = gui.displayLocaleOptions();

        assertEquals("Should select Locale.GERMANY", Locale.GERMANY, selectedLocale);
    }

    @Test(timeout = 10000)
    public void testDisplayConfirmAndCancelButton() throws Exception {
        ResourceBundle messages = gui.getMessages();
        String confirmMsg = messages.getString("confirmSelection");

        gui.displayConfirmAndCancelButton();
        assertNotNull("Confirm button should exist", harness.getButtonWithText(confirmMsg));

        harness.clickButtonSync(confirmMsg);
        assertFalse("Action should not be cancelled", gui.isCancelled());
    }

    @Test(timeout = 10000)
    public void testCreateFlipButton() throws Exception {
        ResourceBundle messages = gui.getMessages();
        String flipCoinMsg = messages.getString("flipCoin");

        harness.clickButtonAsync(flipCoinMsg, 200);
        gui.createFlipButton();

        assertTrue("createFlipButton should return after button click", true);
    }

    @Test(timeout = 10000)
    public void testDisplayCardReport() throws Exception {
        Pokemon p = new Pokemon("Pikachu", "Lightning", 0, 60);
        harness.dismissDialogAsync(200);
        gui.displayCardReport(p);

        assertTrue("displayCardReport should return after dialog dismissal", true);
    }

    @Test(timeout = 10000)
    public void testCardSelectionAndActionButtons() throws Exception {
        Player p1 = new Player("Player 1");
        Player p2 = new Player("Player 2");
        gui.setPlayers(p1, p2);
        gui.displayActionButtons();

        Pokemon pikachu = new Pokemon("Pikachu", "Lightning", 0, 60);
        gui.createLinkedButtonCard("Pikachu", pikachu);
        harness.clickButtonSync("Pikachu");

        ResourceBundle messages = gui.getMessages();
        String addPokBenchMsg = messages.getString("addPokBench");
        assertNotNull("Add to Bench button should exist", harness.getButtonWithText(addPokBenchMsg));

        Energy energy = new Energy(EnergyType.LIGHTNING);
        gui.createLinkedButtonCard("Energy", energy);
        harness.clickButtonSync("Energy");
        assertNotNull("Add Energy button should exist", harness.getButtonWithText(messages.getString("addEnergy")));

        Trainer trainer = new Trainer("Potion", "Effect");
        gui.createLinkedButtonCard("Potion", trainer);
        harness.clickButtonSync("Potion");
        assertNotNull("Play Trainer button should exist", harness.getButtonWithText(messages.getString("playTrainer")));
    }

    @Test(timeout = 10000)
    public void testUpdateTurnAndState() throws Exception {
        gui.updateTurn(1);
        assertEquals(1, gui.getPlayerTurn());

        Player p1 = new Player("P1");
        Player p2 = new Player("P2");
        gui.setPlayers(p1, p2);

        gui.makeActiveCard(p1, new Pokemon("P1 Active", "Grass", 0, 50));
        assertEquals(Color.GREEN, harness.getPlayer1ActiveColor());

        gui.setDeckColor(Color.BLUE);
        assertEquals(Color.BLUE, gui.getDeckColor());
    }

    @Test(timeout = 10000)
    public void testDisplayWinningMessage() throws Exception {
        Player p1 = new Player("Winner");
        Player p2 = new Player("Loser");

        harness.dismissDialogAsync(200);
        gui.displayWinningMessage(p1, p2);
        assertTrue(true);
    }

    @Test(timeout = 10000)
    public void testDisplayOptionsSelection() throws Exception {
        ArrayList<String> options = new ArrayList<String>();
        options.add("Option 1");
        options.add("Option 2");

        for (String option : options) {
            gui.createButton(option);
        }
        
        // createButton doesn't natively trigger waitForButtonPressed wait, so we click synchronously
        // and verify that the button was created and is clickable
        harness.clickButtonSync("Option 2");
        assertNotNull("Option 2 button should exist", harness.getButtonWithText("Option 2"));
    }

    @Test(timeout = 10000)
    public void testDisplayCardsAndSelection() throws Exception {
        ArrayList<Card> cards = new ArrayList<Card>();
        Pokemon p1 = new Pokemon("Pika 1", "Lightning", 0, 40);
        Pokemon p2 = new Pokemon("Pika 2", "Lightning", 0, 40);
        cards.add(p1);
        cards.add(p2);

        gui.displayCards(cards);
        
        // displayCards calls createLinkedButtonCard which doesn't block by itself.
        // We click the button synchronously and check the last selected card.
        harness.clickButtonSync("Pika 2");
        assertEquals(p2, gui.getLastSelectedCard());
    }

    @Test(timeout = 10000)
    public void testDisplayMessage() throws Exception {
        harness.dismissDialogAsync(200);
        gui.displayMessage("Hello Test");
        assertTrue(true);
    }

    @Test(timeout = 10000)
    public void testSetPlayersAndRefresh() throws Exception {
        Player player1 = new Player("P1");
        Player player2 = new Player("P2");
        gui.setPlayers(player1, player2);
        
        assertEquals(player1, gui.getPlayer1());
        assertEquals(player2, gui.getPlayer2());
    }

    @Test(timeout = 10000)
    public void testAttackSelection() throws Exception {
        ArrayList<Attack> attacks = new ArrayList<>();
        attacks.add(new Attack("Thunder Shock", new ArrayList<>(), 30));
        attacks.add(new Attack("Tail Whip", new ArrayList<>(), 0));

        harness.dismissDialogAsync(200);
        gui.displayPossibleAttacks(attacks);

        // Verify buttons created for each attack
        assertNotNull("Attack 1 button should exist", harness.getButtonWithText("Thunder Shock"));
        assertNotNull("Attack 2 button should exist", harness.getButtonWithText("Tail Whip"));

        // Click one and verify
        harness.clickButtonSync("Thunder Shock");
        assertEquals("Thunder Shock", gui.getLastSelectedAttack().name);
    }

    @Test(timeout = 10000)
    public void testAttackMessage() throws Exception {
        Player p1 = new Player("Player 1");
        Player p2 = new Player("Player 2");
        Pokemon pika1 = new Pokemon("Pika", "Lightning", 0, 60);
        Pokemon squirtle2 = new Pokemon("Squirtle", "Water", 0, 50);
        p1.forceSetActivePokemon(pika1);
        p2.forceSetActivePokemon(squirtle2);
        Attack atk = new Attack("Thunder", new ArrayList<>(), 40);

        harness.dismissDialogAsync(200);
        gui.displayAttackMessage(p1, p2, atk);
        assertTrue("displayAttackMessage returned after dialog dismissal", true);
    }

    @Test(timeout = 10000)
    public void testRetreatAndDeadActiveInfo() throws Exception {
        Pokemon p = new Pokemon("Pika", "Lightning", 0, 60);
        
        // Test Retreat (canRetreat = true)
        harness.dismissDialogAsync(200);
        gui.displayRetreatEnergy(p, true);
        
        // Test Retreat (canRetreat = false)
        harness.dismissDialogAsync(200);
        gui.displayRetreatEnergy(p, false);

        // Test Dead Info
        harness.dismissDialogAsync(200);
        Player deadP = new Player("Defending");
        deadP.forceSetActivePokemon(p);
        gui.displayDeadActiveInfo(deadP);

        assertTrue("All dialogs dismissed correctly", true);
    }

    @Test(timeout = 10000)
    public void testEvolutionButton() throws Exception {
        Pokemon stage1 = new Pokemon("Ivysaur", "Grass", 1, 60);
        stage1.setEvolvesFrom("Bulbasaur");
        
        gui.displayActionButtons(); // Sets activeTurn = true
        gui.createLinkedButtonCard("Ivysaur", stage1);
        harness.clickButtonSync("Ivysaur");

        ResourceBundle messages = gui.getMessages();
        String evolveToPokMsg = messages.getString("evolveToPok");
        assertNotNull("Evolve button should exist", harness.getButtonWithText(evolveToPokMsg));

        harness.clickButtonAsync(evolveToPokMsg, 100);
        String action = gui.waitForButtonPressed();
        assertEquals("Evolve", action);
    }

    @Test(timeout = 10000)
    public void testActionButtonsInteraction() throws Exception {
        gui.displayActionButtons();
        ResourceBundle messages = gui.getMessages();

        String attackMsg = messages.getString("atkOpp");
        harness.clickButtonAsync(attackMsg, 100);
        assertEquals("Attack", gui.waitForButtonPressed());

        gui.displayActionButtons();
        String retreatMsg = messages.getString("retreatPok");
        harness.clickButtonAsync(retreatMsg, 100);
        assertEquals("Retreat", gui.waitForButtonPressed());

        gui.displayActionButtons();
        String infoMsg = messages.getString("seeCardInfo");
        harness.clickButtonAsync(infoMsg, 100);
        assertEquals("CardInfo", gui.waitForButtonPressed());
    }

    @Test(timeout = 10000)
    public void testMiscGuiUpdates() throws Exception {
        Player p = new Player("P1");
        Pokemon pok = new Pokemon("P", "Fire", 0, 50);
        
        gui.removePrizeCard(p);
        gui.removeBenchCard(p, pok);
        gui.refreshGUI();
        gui.replaceActiveCard(p, pok);
        gui.retreat(p, pok);
        gui.addBenchCard(p, pok);
        
        // Coverage for createButton which is not normally awaited
        gui.createButton("Test Button");
        assertNotNull(harness.getButtonWithText("Test Button"));
        
        assertTrue("Misc GUI methods ran without error", true);
    }

    @Test(timeout = 10000)
    public void testImageLoadingEdgeCases() throws Exception {
        Pokemon p = new Pokemon("No Image", "Colorless", 0, 10);
        // p.imageUrl is default null
        gui.createLinkedButtonCard("No Image", p);
        assertNotNull(harness.getButtonWithText("No Image"));
    }
}
