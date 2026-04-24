package main.ui;

import main.*;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Locale;

import static org.junit.Assert.*;

public class ExtensiveGameGUICoverageTest {

    private GameGUI gui;
    private GameGUITestHarness harness;

    @Before
    public void setUp() {
        gui = new GameGUI();
        gui.createGUI();
        harness = new GameGUITestHarness(gui);
        
        Player p1 = new Player("Player 1");
        Player p2 = new Player("Player 2");
        gui.setPlayers(p1, p2);
    }

    @Test
    public void testInformationalDisplays() throws Exception {
        // test displayMessage
        harness.dismissDialogAsync(100);
        gui.displayMessage("Test Message");
        
        // test displayCardReport
        Pokemon p = new Pokemon("Pika", "Lightning", 0, 50);
        harness.dismissDialogAsync(100);
        gui.displayCardReport(p);
        
        // test displayAttackMessage
        Attack atk = new Attack("Thundershock", new ArrayList<>(), 20);
        gui.getPlayer1().getHand().addCard(p);
        gui.getPlayer1().setActivePokemon(p);
        Pokemon sq = new Pokemon("Squirtle", "Water", 0, 40);
        gui.getPlayer2().getHand().addCard(sq);
        gui.getPlayer2().setActivePokemon(sq);
        harness.dismissDialogAsync(100);
        gui.displayAttackMessage(gui.getPlayer1(), gui.getPlayer2(), atk);
        
        // test displayRetreatEnergy
        harness.dismissDialogAsync(100);
        gui.displayRetreatEnergy(p, true);
        harness.dismissDialogAsync(100);
        gui.displayRetreatEnergy(p, false);
        
        // test displayDeadActiveInfo
        harness.dismissDialogAsync(100);
        gui.displayDeadActiveInfo(gui.getPlayer2());
        
        // test displayWinningMessage
        harness.dismissDialogAsync(100);
        gui.displayWinningMessage(gui.getPlayer1(), gui.getPlayer2());
    }

    @Test
    public void testActionButtonsAndSubmenus() throws Exception {
        // displayActionButtons
        SwingUtilities.invokeLater(() -> gui.displayActionButtons());
        Thread.sleep(500);
        assertTrue(gui.getButtons().stream().anyMatch(b -> b.getText().equals(gui.messages.getString("passTurn"))));
        
        // displayPossibleAttacks
        ArrayList<Attack> attacks = new ArrayList<>();
        attacks.add(new Attack("Thunder", new ArrayList<>(), 40));
        SwingUtilities.invokeLater(() -> gui.displayPossibleAttacks(attacks));
        Thread.sleep(500);
        assertTrue(gui.getButtons().stream().anyMatch(b -> b.getText().equals("Thunder")));
        
        // Trigger attack selection
        harness.clickButtonAsync("Thunder", 100);
        Thread.sleep(500); // Wait for processing
        Attack selected = gui.getLastSelectedAttack();
        assertEquals("Thunder", selected.name);
    }

    @Test
    public void testCardActionButtons() throws Exception {
        // Need to enable activeTurn to see action buttons on click
        gui.updateTurn(1); 
        gui.displayActionButtons(); // This sets activeTurn = true
        
        Pokemon p = new Pokemon("Bulbasaur", "Grass", 0, 50);
        gui.getPlayer1().getHand().addCard(p);
        gui.createLinkedButtonCard("Bulba", p);
        
        // Click the card to trigger action buttons
        harness.clickButtonAsync("Bulba", 200);
        Thread.sleep(800);
        
        // Check for "Add to Bench"
        assertTrue(gui.getButtons().stream().anyMatch(b -> b.getText().equals(gui.messages.getString("addPokBench"))));
        
        // Test energy
        Energy e = new Energy(EnergyType.GRASS);
        gui.getPlayer1().getHand().addCard(e);
        gui.createLinkedButtonCard("Energy", e);
        harness.clickButtonAsync("Energy", 200);
        Thread.sleep(800);
        assertTrue(gui.getButtons().stream().anyMatch(b -> b.getText().equals(gui.messages.getString("addEnergy"))));
        
        // Test trainer
        Trainer t = new Trainer("Potion", TrainerSubtype.ITEM, "Heal 20");
        gui.getPlayer1().getHand().addCard(t);
        gui.createLinkedButtonCard("Trainer", t);
        harness.clickButtonAsync("Trainer", 200);
        Thread.sleep(800);
        assertTrue(gui.getButtons().stream().anyMatch(b -> b.getText().equals(gui.messages.getString("playTrainer"))));
    }

    @Test
    public void testBoardPanelDrawingEdgeCases() throws Exception {
        BoardPanel panel = new BoardPanel(gui);
        
        // Force drawFlag
        harness.dismissDialogAsync(100);
        harness.clickButtonAsync("English", 200);
        gui.displayLocaleOptions();
        
        // Now flag should be set
        assertNotNull(gui.getFlag());
        
        // Repaint to exercise drawFlag
        panel.repaint();
        Thread.sleep(100);
        
        // Exercise drawPokemonStatus in different positions
        Pokemon p = new Pokemon("Test", "Fire", 0, 60);
        p.addEnergy(new Energy(EnergyType.FIRE));
        p.addEnergy(new Energy(EnergyType.WATER));
        p.addEnergy(new Energy(EnergyType.GRASS));
        p.addEnergy(new Energy(EnergyType.LIGHTNING));
        p.addEnergy(new Energy(EnergyType.FIGHTING));
        p.addEnergy(new Energy(EnergyType.PSYCHIC));
        
        Rectangle r = new Rectangle(0, 0, 100, 150);
        
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        panel.drawPokemonStatus(g2d, p, r, "ABOVE");
        panel.drawPokemonStatus(g2d, p, r, "BELOW");
        panel.drawPokemonStatus(g2d, p, r, "RIGHT");
        panel.drawPokemonStatus(g2d, p, r, "UNKNOWN");
        
        // Test prize cards > 3
        Player p1 = gui.getPlayer1();
        for(int i=0; i<6; i++) p1.getHand().addCard(new Energy(EnergyType.GRASS)); // Not prizes, but need to check prize logic
        panel.drawPrizeCards(g2d, p1, BoardPanel.Side.BOTTOM);
        panel.drawPrizeCards(g2d, p1, BoardPanel.Side.TOP);
    }

    @Test
    public void testImageLoaderError() {
        JButton btn = new JButton();
        ImageLoader.loadIntoButton("invalid_url", btn, 100, 100);
        // Should not crash, should use fallback
    }

    @Test
    public void testCardDropZoneDetectorLogic() {
        CardDropZoneDetector detector = new CardDropZoneDetector(new BoardPositionMap(), gui);
        
        gui.updateTurn(1);
        Player p1 = gui.getPlayer1();
        Pokemon basic = new Pokemon("Pika", "Lightning", 0, 50);
        Pokemon stage1 = new Pokemon("Raichu", "Lightning", 1, 80);
        stage1.setEvolvesFrom("Pika");
        Energy energy = new Energy(EnergyType.LIGHTNING);
        
        // Exercise basic pokemon branches
        detector.isValidForCard(DropZoneType.P1_ACTIVE, basic);
        p1.forceSetActivePokemon(basic);
        detector.isValidForCard(DropZoneType.P1_BENCH_0, basic);
        
        // Exercise energy branches
        detector.isValidForCard(DropZoneType.P1_ACTIVE, energy);
        detector.isValidForCard(DropZoneType.P1_BENCH_0, energy);
        
        // Exercise evolution branches
        detector.isValidForCard(DropZoneType.P1_ACTIVE, stage1);
        p1.getHand().addCard(stage1);
        p1.addBenchPokemon(basic);
        detector.isValidForCard(DropZoneType.P1_BENCH_0, stage1);
        
        // Exercise invalid zones
        detector.isValidForCard(DropZoneType.P2_ACTIVE, basic);
        detector.isValidForCard(DropZoneType.NONE, basic);
    }

    @Test
    public void testImageLoaderCaching() throws Exception {
        JButton btn = new JButton();
        String url = "https://example.com/image.png";
        
        // First load
        ImageLoader.loadIntoButton(url, btn, 50, 50);
        Thread.sleep(200);
        
        // Second load (cache hit)
        ImageLoader.loadIntoButton(url, btn, 50, 50);
        
        // getImage
        ImageLoader.getImage(url, btn);
        ImageLoader.getImage(null, btn);
    }

    @Test
    public void testDragAndDropRelease() throws Exception {
        Pokemon p = new Pokemon("P1", "Grass", 0, 50);
        gui.getPlayer1().getHand().addCard(p);
        CardDropZoneDetector detector = new CardDropZoneDetector(new BoardPositionMap(), gui);
        GhostingDragAdapter adapter = new GhostingDragAdapter(gui, p, detector);
        
        JButton btn = new JButton("Test");
        MouseEvent press = new MouseEvent(btn, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, 10, 10, 1, false);
        adapter.mousePressed(press);
        
        // Release on NONE zone
        MouseEvent release = new MouseEvent(btn, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, -100, -100, 1, false);
        adapter.mouseReleased(release);
    }

    @Test
    public void testGameGUIActions() throws Exception {
        gui.updateTurn(1);
        gui.displayActionButtons();
        
        // Trigger simulated actions to cover release branches
        gui.triggerSimulatedAction("PassTurn");
        assertEquals("PassTurn", gui.waitForButtonPressed());
        
        gui.triggerSimulatedAction("Attack");
        assertEquals("Attack", gui.waitForButtonPressed());
    }

    @Test
    public void testBoardPanelMouseEvents() throws Exception {
        BoardPanel panel = new BoardPanel(gui);
        gui.getFrame().add(panel);
        gui.getFrame().setVisible(true);
        
        // Setup a pokemon on bench
        Pokemon p = new Pokemon("P1", "Grass", 0, 50);
        gui.getPlayer1().addBenchPokemon(p);
        
        // Simulate mouse press on bench slot 0
        Rectangle r = new BoardPositionMap().getZones().get(DropZoneType.P1_BENCH_0);
        MouseEvent press = new MouseEvent(panel, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, r.x + 5, r.y + 5, 1, false);
        // Find the MouseListener added in BoardPanel constructor
        panel.getMouseListeners()[0].mousePressed(press);
        
        // Simulate drag
        MouseEvent drag = new MouseEvent(panel, MouseEvent.MOUSE_DRAGGED, System.currentTimeMillis(), 0, r.x + 50, r.y + 50, 1, false);
        panel.getMouseMotionListeners()[0].mouseDragged(drag);
        
        // Simulate release
        MouseEvent release = new MouseEvent(panel, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, r.x + 50, r.y + 50, 1, false);
        panel.getMouseListeners()[0].mouseReleased(release);
    }
}
