package main;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.Color;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

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

    @Test(timeout = 2000)
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
    
    @Test(timeout = 2000)
    public void testLocaleSelectionEnglish() throws Exception {
        harness.dismissDialogAsync(50);
        // Test language button clicking (US English)
        harness.clickButtonAsync("English", 150);
        Locale selectedLocale = gui.displayLocaleOptions();
        
        assertEquals("Should select Locale.US", Locale.US, selectedLocale);
    }
    
    @Test(timeout = 2000)
    public void testLocaleSelectionGerman() throws Exception {
        harness.dismissDialogAsync(50);
        // Test language button clicking (German)
        harness.clickButtonAsync("Deutsch", 150);
        Locale selectedLocale = gui.displayLocaleOptions();
        
        assertEquals("Should select Locale.GERMANY", Locale.GERMANY, selectedLocale);
    }

    @Test(timeout = 2000)
    public void testDisplayConfirmAndCancelButton() throws Exception {
        ResourceBundle messages = gui.getMessages();
        String confirmMsg = messages.getString("confirmSelection");

        // The method displays choices, then we select confirm.
        // It does not block immediately upon display alone (as it sets state and waits later on if we use wait wrappers).
        // Let's actually simulate the confirm operation.
        gui.displayConfirmAndCancelButton();
        
        // Buttons should be available now.
        assertNotNull("Confirm button should exist", harness.getButtonWithText(confirmMsg));
        
        // Click sync since the display method did not block
        harness.clickButtonSync(confirmMsg);
        
        // Wait-for-action flag should be true after click
        // Note: isCancelled is set to false by confirming
        assertFalse("Action should not be cancelled", gui.isCancelled());
    }
}
