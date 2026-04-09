package main;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.Paint;
import java.util.ArrayList;

public class AdvancedBoardPanelTest {

    private void setupGraphicsMocks(GameGUI gui, Graphics2D g2d) {
        expect(gui.getBoldFont()).andReturn(new Font("Arial", Font.BOLD, 12)).anyTimes();
        expect(gui.getPlainFont()).andReturn(new Font("Arial", Font.PLAIN, 12)).anyTimes();
        
        g2d.translate(anyInt(), anyInt());
        expectLastCall().anyTimes();
        g2d.setColor(anyObject(Color.class));
        expectLastCall().anyTimes();
        g2d.setPaint(anyObject(Paint.class));
        expectLastCall().anyTimes();
        g2d.fillRect(anyInt(), anyInt(), anyInt(), anyInt());
        expectLastCall().anyTimes();
        g2d.fillOval(anyInt(), anyInt(), anyInt(), anyInt());
        expectLastCall().anyTimes();
        g2d.fillRoundRect(anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt());
        expectLastCall().anyTimes();
        g2d.drawRect(anyInt(), anyInt(), anyInt(), anyInt());
        expectLastCall().anyTimes();
        g2d.setFont(anyObject(Font.class));
        expectLastCall().anyTimes();
        g2d.drawString(anyString(), anyInt(), anyInt());
        expectLastCall().anyTimes();
        g2d.setClip(anyObject());
        expectLastCall().anyTimes();
    }

    @Test
    public void testEnergyColors() {
        BoardPanel panel = new BoardPanel(null);
        
        assertEquals(new Color(255, 69, 0), panel.getEnergyColor(EnergyType.FIRE));
        assertEquals(new Color(30, 144, 255), panel.getEnergyColor(EnergyType.WATER));
        assertEquals(new Color(50, 205, 50), panel.getEnergyColor(EnergyType.GRASS));
        assertEquals(new Color(255, 215, 0), panel.getEnergyColor(EnergyType.LIGHTNING));
        assertEquals(new Color(210, 105, 30), panel.getEnergyColor(EnergyType.FIGHTING));
        assertEquals(new Color(153, 50, 204), panel.getEnergyColor(EnergyType.PSYCHIC));
        assertEquals(new Color(70, 70, 70), panel.getEnergyColor(EnergyType.DARKNESS));
        assertEquals(new Color(169, 169, 169), panel.getEnergyColor(EnergyType.METAL));
        assertEquals(new Color(255, 182, 193), panel.getEnergyColor(EnergyType.FAIRY));
        assertEquals(new Color(184, 134, 11), panel.getEnergyColor(EnergyType.DRAGON));
        assertEquals(Color.LIGHT_GRAY, panel.getEnergyColor(EnergyType.COLORLESS));
    }

    @Test
    public void testDrawPokemonStatusHPBarColors() {
        GameGUI gui = createMock(GameGUI.class);
        BoardPanel panel = new BoardPanel(gui);
        Graphics2D g2d = createMock(Graphics2D.class);
        
        setupGraphicsMocks(gui, g2d);
        replay(gui, g2d);

        Rectangle bounds = new Rectangle(100, 100, 80, 120);
        
        // Green HP (> 50%)
        Pokemon greenPkmn = new Pokemon("Bulba", "Grass", 0, 100);
        panel.drawPokemonStatus(g2d, greenPkmn, bounds, "ABOVE");
        
        // Yellow HP (< 50%)
        Pokemon yellowPkmn = new Pokemon("Bulba", "Grass", 0, 100);
        yellowPkmn.takeDamage(60, EnergyType.FIRE);
        panel.drawPokemonStatus(g2d, yellowPkmn, bounds, "BELOW");
        
        // Red HP (< 20%)
        Pokemon redPkmn = new Pokemon("Bulba", "Grass", 0, 100);
        redPkmn.takeDamage(90, EnergyType.FIRE);
        panel.drawPokemonStatus(g2d, redPkmn, bounds, "RIGHT");
        
        verify(gui, g2d);
    }

    @Test
    public void testDrawPokemonStatusEnergySummary() {
        GameGUI gui = createMock(GameGUI.class);
        BoardPanel panel = new BoardPanel(gui);
        Graphics2D g2d = createMock(Graphics2D.class);
        
        setupGraphicsMocks(gui, g2d);
        replay(gui, g2d);

        Rectangle bounds = new Rectangle(100, 100, 80, 120);
        Pokemon complexPkmn = new Pokemon("Ryu", "Dragon", 0, 150);
        
        complexPkmn.addEnergy(new Energy(EnergyType.FIRE));
        complexPkmn.addEnergy(new Energy(EnergyType.WATER));
        complexPkmn.addEnergy(new Energy(EnergyType.GRASS));
        complexPkmn.addEnergy(new Energy(EnergyType.LIGHTNING)); // LIGHTNING is GOLD in the enum name
        complexPkmn.addEnergy(new Energy(EnergyType.FIGHTING));
        complexPkmn.addEnergy(new Energy(EnergyType.COLORLESS));
        
        panel.drawPokemonStatus(g2d, complexPkmn, bounds, "RIGHT");
        
        verify(gui, g2d);
    }
}
