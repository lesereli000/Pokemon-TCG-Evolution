package main;

import org.junit.Test;

import java.util.ArrayList;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class PokemonTest {

    @Test
    public void testEmptyName() {
        boolean pass = false;
        String name = "";
        String type = "Lightning";
        int stage = 1;
        int hp = 40;

        try {
            new Pokemon(name, type, stage, hp);

        } catch (CardCreationException e) {
            pass = true;
            assertEquals("Name cannot be empty", e.getMessage());
        } finally {
            assertTrue(pass);
        }
    }

    @Test
    public void testEmptyType() {
        boolean pass = false;
        String name = "Pikachu";
        String type = "";
        int stage = 1;
        int hp = 40;

        try {
            new Pokemon(name, type, stage, hp);
        } catch (CardCreationException e) {
            pass = true;
            assertEquals("Pokemon type cannot be empty", e.getMessage());
        } finally {
            assertTrue(pass);
        }
    }

    @Test
    public void testInvalidType() {
        boolean pass = false;
        String name = "Pikachu";
        String type = "Poison";
        int stage = 1;
        int hp = 40;

        try {
            new Pokemon(name, type, stage, hp);
        } catch (CardCreationException e) {
            pass = true;
            assertEquals("Invalid pokemon type", e.getMessage());
        } finally {
            assertTrue(pass);
        }
    }

    @Test
    public void testZeroHP() {
        boolean pass = false;
        String name = "Pikachu";
        String type = "Lightning";
        int stage = 1;
        int hp = 0;

        try {
            new Pokemon(name, type, stage, hp);
        } catch (CardCreationException e) {
            pass = true;
            assertEquals("Pokemon health must be greater than 0.", e.getMessage());
        } finally {
            assertTrue(pass);
        }
    }

    @Test
    public void testTooLowStage() {
        boolean pass = false;
        String name = "Pikachu";
        String type = "Lightning";
        int stage = -1;
        int hp = 40;

        try {
            new Pokemon(name, type, stage, hp);
        } catch (CardCreationException e) {
            pass = true;
            assertEquals("Stage cannot be less than 0. 0 is Basic.", e.getMessage());
        } finally {
            assertTrue(pass);
        }
    }

    @Test
    public void testTooHighStage() {
        boolean pass = false;
        String name = "Pikachu";
        String type = "Lightning";
        int stage = 3;
        int hp = 40;

        try {
            new Pokemon(name, type, stage, hp);
        } catch (CardCreationException e) {
            pass = true;
            assertEquals("Stage cannot be greater than 2. Stage 2 is the highest evolution.", e.getMessage());
        } finally {
            assertTrue(pass);
        }
    }

    @Test
    public void testCreatePokemon() {
        String name = "Pikachu";
        String type = "Lightning";
        int stage = 1;
        int hp = 40;
        try {
            Pokemon c = new Pokemon(name, type, stage, hp);
            assertEquals("Pikachu", c.name);
            assertEquals("Lightning", c.type);
            assertEquals(stage, c.stage);
            assertEquals(hp, c.hp);

        } catch (CardCreationException e) {
            fail();
        }
    }

    @Test
    public void testBaseDamage() {
        Pokemon p = new Pokemon("Pikachu", "Lightning", 1, 40);
        p.takeDamage(1, "F");
        assertEquals(30, p.getCurHP());
    }

    @Test
    public void testTypeWeakness() {

    }

    @Test
    public void testTypeResistance() {
        CardGenerator cg = new CardGenerator();
        Pokemon attackingMachop = (Pokemon) cg.generateCard("Machop");

        Pokemon resistantPikachu = (Pokemon) cg.generateCard("Pikachu");
        assertEquals(resistantPikachu.getCurHP(), resistantPikachu.hp);
        resistantPikachu.takeDamage(2, attackingMachop.type);
        assertEquals(resistantPikachu.getCurHP(), resistantPikachu.hp - 10);

        Pokemon nonResistantKakuna = (Pokemon) cg.generateCard("Kakuna");
        assertEquals(nonResistantKakuna.getCurHP(), nonResistantKakuna.hp);
        nonResistantKakuna.takeDamage(2, attackingMachop.type);
        assertEquals(nonResistantKakuna.getCurHP(), nonResistantKakuna.hp - 20);
    }

    @Test
    public void testHealthGetters() {

    }

    @Test
    public void testCanAttack() {
        CardGenerator pg = new CardGenerator();
        Energy e = createMock(Energy.class);
        expect(e.getName()).andReturn("Colorless Energy");
        replay(e);
        Pokemon pikachu = (Pokemon) pg.generateCard("Pikachu");
        assertFalse(pikachu.canAttack());
        pikachu.addEnergy(e);

        assertTrue(pikachu.canAttack());
        verify(e);
    }

    @Test
    public void testCanAttackMultiple() {
        CardGenerator pg = new CardGenerator();
        Energy e = createMock(Energy.class);

        // Set expectations BEFORE using the mock
        expect(e.getName()).andReturn("Grass Energy").anyTimes();

        replay(e); // activate the mock

        Pokemon kakuna = (Pokemon) pg.generateCard("Kakuna");
        kakuna.addEnergy(e);
        assertFalse(kakuna.canAttack());
        kakuna.addEnergy(e);
        assertTrue(kakuna.canAttack());

        verify(e); // verify the interactions (optional here because of anyTimes)
    }

    @Test
    public void testColorlessAttack() {
        CardGenerator pg = new CardGenerator();
        Pokemon kakuna = (Pokemon) pg.generateCard("Kakuna");
        Energy water = createMock(Energy.class);
        Energy grass = createMock(Energy.class);
        expect(water.getName()).andReturn("Water").anyTimes();
        expect(grass.getName()).andReturn("Grass").anyTimes();

        replay(water, grass);
        kakuna.addEnergy(water);
        assertFalse(kakuna.canAttack());
        kakuna.addEnergy(grass);
        assertTrue(kakuna.canAttack());
        verify(water, grass);
    }

    @Test
    public void testIsAlive() {
        CardGenerator pg = new CardGenerator();
        Pokemon pikachu = (Pokemon) pg.generateCard("Pikachu");

        pikachu.takeDamage(1, "Q");
        pikachu.takeDamage(1, "Q");
        pikachu.takeDamage(1, "Q");
        assertTrue(pikachu.isAlive());
        pikachu.takeDamage(1, "Q");
        assertFalse(pikachu.isAlive());


    }

}