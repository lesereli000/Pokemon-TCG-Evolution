package main.ui;

import main.*;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.easymock.EasyMock.*;

public class BoardPanelTest {

    private GameGUI gui;
    private BoardPanel boardPanel;
    private Player p1;
    private Player p2;
    private ResourceBundle messages;

    @Before
    public void setUp() {
        gui = createMock(GameGUI.class);
        p1 = createMock(Player.class);
        p2 = createMock(Player.class);
        messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);

        expect(gui.getMessages()).andReturn(messages).anyTimes();
        expect(gui.getBoldFont()).andReturn(new Font("Arial", Font.BOLD, 12)).anyTimes();
        expect(gui.getPlainFont()).andReturn(new Font("Arial", Font.PLAIN, 12)).anyTimes();
        expect(gui.getPlayer1()).andReturn(p1).anyTimes();
        expect(gui.getPlayer2()).andReturn(p2).anyTimes();
        expect(gui.getPlayerTurn()).andReturn(1).anyTimes();
        expect(gui.getDeckColor()).andReturn(Color.RED).anyTimes();
        expect(gui.getFlag()).andReturn(null).anyTimes();
        expect(gui.getPlayer1ActiveColor()).andReturn(Color.GREEN).anyTimes();
        expect(gui.getPlayer2ActiveColor()).andReturn(Color.BLUE).anyTimes();
        expect(gui.getNumBenchCards()).andReturn(5).anyTimes();

        boardPanel = new BoardPanel(gui);
    }

    @Test
    public void testPaintComponentBasic() {
        expect(p1.getNumPrizeCards()).andReturn(6).anyTimes();
        expect(p1.getPokemonOnBench()).andReturn(new ArrayList<>()).anyTimes();
        expect(p1.getActivePokemon()).andReturn(null).anyTimes();
        expect(p1.hasActive()).andReturn(false).anyTimes();

        expect(p2.getNumPrizeCards()).andReturn(6).anyTimes();
        expect(p2.getPokemonOnBench()).andReturn(new ArrayList<>()).anyTimes();
        expect(p2.getActivePokemon()).andReturn(null).anyTimes();
        expect(p2.hasActive()).andReturn(false).anyTimes();

        replay(gui, p1, p2);

        BufferedImage img = new BufferedImage(1200, 900, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        // This will call paintComponent and all internal draw methods
        boardPanel.paintComponent(g2d);
        
        verify(gui, p1, p2);
    }

    @Test
    public void testPaintComponentWithContent() {
        Pokemon active1 = new Pokemon("Pikachu", "Lightning", 0, 60);
        ArrayList<Card> bench1 = new ArrayList<>();
        bench1.add(new Pokemon("Squirtle", "Water", 0, 50));
        
        expect(p1.getNumPrizeCards()).andReturn(2).anyTimes();
        expect(p1.getPokemonOnBench()).andReturn(bench1).anyTimes();
        expect(p1.getActivePokemon()).andReturn(active1).anyTimes();
        expect(p1.hasActive()).andReturn(true).anyTimes();

        expect(p2.getNumPrizeCards()).andReturn(4).anyTimes();
        expect(p2.getPokemonOnBench()).andReturn(new ArrayList<>()).anyTimes();
        expect(p2.getActivePokemon()).andReturn(null).anyTimes();
        expect(p2.hasActive()).andReturn(false).anyTimes();

        replay(gui, p1, p2);

        BufferedImage img = new BufferedImage(1200, 900, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        boardPanel.paintComponent(g2d);
        
        verify(gui, p1, p2);
    }

    @Test
    public void testPaintComponentWithP2Content() {
        Pokemon active2 = new Pokemon("Charmander", "Fire", 0, 50);
        ArrayList<Card> bench2 = new ArrayList<>();
        bench2.add(new Pokemon("Bulbasaur", "Grass", 0, 45));
        
        expect(p1.getNumPrizeCards()).andReturn(0).anyTimes();
        expect(p1.getPokemonOnBench()).andReturn(new ArrayList<>()).anyTimes();
        expect(p1.getActivePokemon()).andReturn(null).anyTimes();
        expect(p1.hasActive()).andReturn(false).anyTimes();

        expect(p2.getNumPrizeCards()).andReturn(1).anyTimes();
        expect(p2.getPokemonOnBench()).andReturn(bench2).anyTimes();
        expect(p2.getActivePokemon()).andReturn(active2).anyTimes();
        expect(p2.hasActive()).andReturn(true).anyTimes();

        replay(gui, p1, p2);

        BufferedImage img = new BufferedImage(1200, 900, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        boardPanel.paintComponent(g2d);
        
        verify(gui, p1, p2);
    }

    @Test
    public void testPaintComponentNullPlayers() {
        reset(gui);
        expect(gui.getMessages()).andReturn(messages).anyTimes();
        expect(gui.getBoldFont()).andReturn(new Font("Arial", Font.BOLD, 12)).anyTimes();
        expect(gui.getPlayer1()).andReturn(null).anyTimes();
        expect(gui.getPlayer2()).andReturn(null).anyTimes();
        expect(gui.getPlayerTurn()).andReturn(1).anyTimes();
        expect(gui.getFlag()).andReturn(null).anyTimes();
        
        replay(gui);

        BufferedImage img = new BufferedImage(1200, 900, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        boardPanel.paintComponent(g2d);
        
        verify(gui);
    }

    @Test
    public void testDrawFlag() {
        BufferedImage mockFlag = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        reset(gui);
        expect(gui.getMessages()).andReturn(messages).anyTimes();
        expect(gui.getBoldFont()).andReturn(new Font("Arial", Font.BOLD, 12)).anyTimes();
        expect(gui.getPlainFont()).andReturn(new Font("Arial", Font.PLAIN, 12)).anyTimes();
        expect(gui.getPlayer1()).andReturn(null).anyTimes();
        expect(gui.getPlayer2()).andReturn(null).anyTimes();
        expect(gui.getPlayerTurn()).andReturn(1).anyTimes();
        expect(gui.getFlag()).andReturn(mockFlag).anyTimes();

        replay(gui);

        BufferedImage img = new BufferedImage(1200, 900, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        boardPanel.paintComponent(g2d);
        
        verify(gui);
    }

    @Test
    public void testDrawPrizeCardsMoreThanThree() {
        expect(p1.getNumPrizeCards()).andReturn(5).anyTimes();
        expect(p1.getPokemonOnBench()).andReturn(new ArrayList<>()).anyTimes();
        expect(p1.getActivePokemon()).andReturn(null).anyTimes();
        expect(p1.hasActive()).andReturn(false).anyTimes();

        expect(p2.getNumPrizeCards()).andReturn(5).anyTimes();
        expect(p2.getPokemonOnBench()).andReturn(new ArrayList<>()).anyTimes();
        expect(p2.getActivePokemon()).andReturn(null).anyTimes();
        expect(p2.hasActive()).andReturn(false).anyTimes();

        replay(gui, p1, p2);

        BufferedImage img = new BufferedImage(1200, 900, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        boardPanel.paintComponent(g2d);
        verify(gui, p1, p2);
    }

    @Test
    public void testDrawCardWithImageUrlButNoImage() {
        Pokemon p = new Pokemon("Pika", "Lightning", 0, 60);
        p.setImageUrl("http://example.com/pika.png");
        ArrayList<Card> bench = new ArrayList<>();
        bench.add(p);

        expect(p1.getNumPrizeCards()).andReturn(0).anyTimes();
        expect(p1.getPokemonOnBench()).andReturn(bench).anyTimes();
        expect(p1.getActivePokemon()).andReturn(null).anyTimes();
        expect(p1.hasActive()).andReturn(false).anyTimes();

        expect(p2.getNumPrizeCards()).andReturn(0).anyTimes();
        expect(p2.getPokemonOnBench()).andReturn(new ArrayList<>()).anyTimes();
        expect(p2.getActivePokemon()).andReturn(null).anyTimes();
        expect(p2.hasActive()).andReturn(false).anyTimes();

        replay(gui, p1, p2);

        BufferedImage img = new BufferedImage(1200, 900, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        boardPanel.paintComponent(g2d);
        verify(gui, p1, p2);
    }
}
