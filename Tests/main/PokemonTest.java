package main;

import org.junit.Test;

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
    public void testWeakness() {

    }

    @Test
    public void testResistance() {

    }

    @Test
    public void testHealthAndDamageGetters() {

    }

    @Test
    public void testCanAttack() {
        CardGenerator pg = new CardGenerator();
        Energy e = createMock(Energy.class);
        expect(e.getName()).andReturn("Colorless Energy");
        Pokemon pikachu = (Pokemon) pg.generateCard("Pikachu");
        pikachu.addEnergy(e);

        replay(e);
        assertTrue(pikachu.canAttack());
        verify(e);

    }
}