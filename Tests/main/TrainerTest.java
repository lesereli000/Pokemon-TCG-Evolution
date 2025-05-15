package main;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class TrainerTest {

    @Test
    public void testEmptyName() {
        boolean pass = false;
        String effects = "Test";

        try {
            new Trainer("", effects);
        } catch (CardCreationException e) {
            pass = true;
            assertEquals("Name cannot be empty", e.getMessage());
        } finally {
            assertTrue(pass);
        }
    }

    @Test
    public void testGetName() {
        String name = "Potion";
        String effects = "Test";
        Card c = new Trainer(name, effects);

        assertEquals(name, c.getName());
    }

    @Test
    public void testInvalidSubtype() {
        String name = "Potion";
        String effects = "Test";
        String subtype = "Invalid";
        boolean pass = false;

        try {
            new Trainer(name, subtype, effects);
        } catch (CardCreationException e) {
            pass = true;
            assertEquals("Trainer subtype must be either Item, Supporter or Stadium", e.getMessage());
        } finally {
            assertTrue(pass);
        }
    }

    @Test
    public void testAllValidSubtypes() {
        String name = "Potion";
        String effects = "Test";
        String subtype = "Item";
        Trainer c1 = new Trainer(name, subtype, effects);

        name = "Potion";
        effects = "Test";
        subtype = "Supporter";
        Trainer c2 = new Trainer(name, subtype, effects);

        name = "Potion";
        effects = "Test";
        subtype = "Stadium";
        Trainer c3 = new Trainer(name, subtype, effects);

        assertEquals("Item", c1.getTrainerType());
        assertEquals("Supporter", c2.getTrainerType());
        assertEquals("Stadium", c3.getTrainerType());

    }

    @Test
    public void testEmptyEffects() {
        String name = "Potion";
        String effects = "";
        boolean pass = false;

        try {
            new Trainer(name, effects);
        } catch (CardCreationException e) {
            pass = true;
            assertEquals("Trainer effects cannot be empty", e.getMessage());
        } finally {
            assertTrue(pass);
        }
    }

    @Test
    public void testGetEffects() {
        String name = "Potion";
        String effects = "Test";
        Trainer c = new Trainer(name, effects);

        assertEquals(effects, c.getEffects());
    }

    @Test
    public void testBillTrainer(){
        Player p1 = createMock(Player.class);
        Player p2 = createMock(Player.class);
        CardGenerator pg = new CardGenerator();
        Pokemon p = null;
        Energy e = null;
        Trainer c = (Trainer) pg.generateCard("Bill");
        assertEquals("Bill", c.getName());
        expect(p1.drawCard()).andReturn(true);
        expect(p1.drawCard()).andReturn(true);
        replay(p1,p2);
        c.doEffects(p1,p,e);
        verify(p1,p2);
    }

    @Test
    public void testPotion() {
        Player p1 = new Player();
        Player p2 = createMock(Player.class);
        CardGenerator pg = new CardGenerator();
        Pokemon activePokemon = new Pokemon("Squirtle", "Water", 2, 80, "Grass", "Fighting", null, 0);
        ArrayList<Card> pokemon = new ArrayList<>();
        pokemon.add(activePokemon);

        p1.hand.addCard(activePokemon);
        p1.setActivePokemon(activePokemon);
        Trainer c1 = (Trainer) pg.generateCard("Potion");
        Trainer c2 = (Trainer) pg.generateCard("Potion");
        replay(p2);
        assertEquals(activePokemon.damageCounters, 0);
        activePokemon.takeDamage(3, "Water");
        assertEquals(3,activePokemon.damageCounters);
        c1.doEffects(p1, activePokemon, null);
        assertEquals(1, activePokemon.damageCounters);
        c2.doEffects(p1, activePokemon, null);
        assertEquals(activePokemon.damageCounters, 0);
        verify(p2);
    }

    @Test
    public void testSuperPotion() {
        Player p1 = new Player();
        CardGenerator pg = new CardGenerator();
        Pokemon activePokemon = new Pokemon("Beedrill", "Grass", 2, 80, "Fire", "Fighting", null, 0);
        ArrayList<Card> pokemon = new ArrayList<>();
        pokemon.add(activePokemon);
        Energy e1 = new Energy("Grass Energy");
        Energy e2 = new Energy("Fire Energy");
        ArrayList<Energy> expectedEnergy = new ArrayList<>();

        p1.hand.addCard(activePokemon);
        p1.setActivePokemon(activePokemon);
        Trainer c1 = (Trainer) pg.generateCard("Super Potion");
        Trainer c2 = (Trainer) pg.generateCard("Super Potion");

        activePokemon.takeDamage(6, "Water");
        assertEquals(activePokemon.damageCounters, 6);

        p1.hand.addCard(e1);
        c1.doEffects(p1, activePokemon, e1);
        assertEquals(activePokemon.energies, expectedEnergy);
        assertEquals(activePokemon.damageCounters, 2);

        p1.hand.addCard(e2);
        c2.doEffects(p1, activePokemon, e2);
        assertEquals(activePokemon.energies, expectedEnergy);
        assertEquals(activePokemon.damageCounters, 0);
    }

    @Test
    public void testTrainerConstructorEmptyEffectsThrowsException() {
        try {
            new Trainer("Potion", "Item", "");
            fail("CardCreationException is not thrown");
        } catch (CardCreationException e) {
            assertEquals("Trainer effects cannot be empty", e.getMessage());
        }
    }
}
