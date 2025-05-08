package main;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

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
        CardGenerator cg = new CardGenerator();
        Pokemon attackingMachop = (Pokemon) cg.generateCard("Machop");

        Pokemon weakPikachu = (Pokemon) cg.generateCard("Pikachu");
        assertEquals(weakPikachu.getCurHP(), weakPikachu.hp);
        weakPikachu.takeDamage(2, attackingMachop.type);
        assertEquals(weakPikachu.getCurHP(), weakPikachu.hp - 40);

        Pokemon nonWeakKakuna = (Pokemon) cg.generateCard("Kakuna");
        assertEquals(nonWeakKakuna.getCurHP(), nonWeakKakuna.hp);
        nonWeakKakuna.takeDamage(2, attackingMachop.type);
        assertEquals(nonWeakKakuna.getCurHP(), nonWeakKakuna.hp - 20);
    }

    @Test
    public void testTypeResistance() {
        CardGenerator cg = new CardGenerator();
        Pokemon attackingMachop = (Pokemon) cg.generateCard("Machop");

        Pokemon resistantMewtwo = (Pokemon) cg.generateCard("Mewtwo");
        assertEquals(resistantMewtwo.getCurHP(), resistantMewtwo.hp);
        resistantMewtwo.takeDamage(2, attackingMachop.type);
        assertEquals(resistantMewtwo.getCurHP(), resistantMewtwo.hp - 10);

        Pokemon nonResistantKakuna = (Pokemon) cg.generateCard("Kakuna");
        assertEquals(nonResistantKakuna.getCurHP(), nonResistantKakuna.hp);
        nonResistantKakuna.takeDamage(2, attackingMachop.type);
        assertEquals(nonResistantKakuna.getCurHP(), nonResistantKakuna.hp - 20);
    }

    @Test
    public void testHealthGetters() {
        CardGenerator cg = new CardGenerator();
        Pokemon attackingMachop = (Pokemon) cg.generateCard("Machop");
        Pokemon defendingKakuna = (Pokemon) cg.generateCard("Kakuna");
        assertEquals(defendingKakuna.getCurHP(), defendingKakuna.hp);
        assertEquals(defendingKakuna.getMaxHP(), defendingKakuna.hp);

        defendingKakuna.takeDamage(2, attackingMachop.type);
        assertEquals(defendingKakuna.getCurHP(), defendingKakuna.hp - 20);
        assertEquals(defendingKakuna.getMaxHP(), defendingKakuna.hp);
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
    public void testEnergyMapIncludesColorless() {
        Pokemon p = new Pokemon("Pikachu", "Lightning", 1, 40);
        Energy e = createMock(Energy.class);
        expect(e.getName()).andReturn("Lightning Energy").anyTimes();
        replay(e);

        p.addEnergy(e);
        HashMap<String, Integer> map = p.getEnergyMap();
        assertTrue(map.containsKey("Colorless Energy"));
        assertEquals(1, (int) map.get("Colorless Energy"));
        assertEquals(1, (int) map.get("Lightning Energy"));
    }

    @Test
    public void testEvolvesFromSetterGetter() {
        Pokemon p = new Pokemon("Raichu", "Lightning", 2, 90);
        p.setEvolvesFrom("Pikachu");
        assertEquals("Pikachu", p.getEvolvesFrom());
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

    @Test
    public void testRemoveEnergy() {
        CardGenerator pg = new CardGenerator();
        Pokemon pikachu = (Pokemon) pg.generateCard("Pikachu");

        Energy e = createMock(Energy.class);

        e.name = "Grass Energy";
        replay(e);
        ArrayList<Energy> energies = pikachu.energies;
        pikachu.addEnergy(e);
        assertEquals(1, energies.size());
        pikachu.removeEnergy(e);
        assertEquals(0, energies.size());
        verify(e);
    }

    @Test
    public void testRemoveManyEnergies() {
        CardGenerator pg = new CardGenerator();
        Pokemon kakuna = (Pokemon) pg.generateCard("Kakuna");
        Energy e = createMock(Energy.class);
        Energy e2 = createMock(Energy.class);
        Energy e3 = createMock(Energy.class);

        e.name = "Grass Energy";
        e2.name = "Lightning Energy";
        e3.name = "Water Energy";
        replay(e, e2, e3);

        ArrayList<Energy> energies = kakuna.energies;

        kakuna.addEnergy(e);
        kakuna.addEnergy(e2);
        kakuna.addEnergy(e3);
        kakuna.addEnergy(e);
        kakuna.addEnergy(e2);
        kakuna.addEnergy(e3);
        kakuna.addEnergy(e);
        kakuna.addEnergy(e2);
        kakuna.addEnergy(e3);

        assertEquals(9, energies.size());

        kakuna.removeEnergy(e2);
        kakuna.removeEnergy(e2);
        kakuna.removeEnergy(e2);

        assertEquals(6, energies.size());
        for(Energy energy : energies) {
            assertNotEquals("Lightning Energy", energy.name);
        }

        kakuna.removeEnergy(e3);
        kakuna.removeEnergy(e3);
        kakuna.removeEnergy(e3);

        assertEquals(3, energies.size());
        for (Energy energy : energies) {
            assertNotEquals("Water Energy", energy.name);
            assertNotEquals("Lightning Energy", energy.name);
        }

        kakuna.removeEnergy(e);
        kakuna.removeEnergy(e);
        kakuna.removeEnergy(e);
        assertEquals(0, energies.size());

        verify(e, e2, e3);
    }

    @Test
    public void testRemoveNonExistentEnergy() {
        CardGenerator pg = new CardGenerator();
        Pokemon pikachu = (Pokemon) pg.generateCard("Pikachu");
        Energy e = createMock(Energy.class);

        assertThrows(IllegalArgumentException.class, () -> pikachu.removeEnergy(e));
    }

    @Test
    public void testNoColorless() {
        CardGenerator pg = new CardGenerator();
        Pokemon p = (Pokemon) pg.generateCard("Pikachu");

        assertEquals(0, p.numColorless());
    }

    @Test
    public void testManyColorless() {
        CardGenerator pg = new CardGenerator();
        Pokemon p = (Pokemon) pg.generateCard("Pikachu");

        Energy e = createMock(Energy.class);

        p.addEnergy(e);
        p.addEnergy(e);
        p.addEnergy(e);
        p.addEnergy(e);
        p.addEnergy(e);
        p.addEnergy(e);
        p.addEnergy(e);

        assertEquals(7, p.numColorless());
    }

    @Test
    public void testCanRetreat() {
        CardGenerator pg = new CardGenerator();
        Pokemon p = (Pokemon) pg.generateCard("Pikachu");

        assertFalse(p.canRetreat());
        Energy e = createMock(Energy.class);
        p.addEnergy(e);
        assertTrue(p.canRetreat());
    }

    @Test
    public void testCanRetreatExactCost() {
        CardGenerator pg = new CardGenerator();
        Pokemon p = (Pokemon) pg.generateCard("Tangela");

        Energy e = createMock(Energy.class);
        replay(e);
        p.addEnergy(e);
        p.addEnergy(e);

        assertTrue(p.canRetreat());
    }

    @Test
    public void testRemoveColorlessValid() {
        Pokemon p = new Pokemon("Pikachu", "Lightning", 1, 40);
        Energy e = createMock(Energy.class);
        p.addEnergy(e);
        p.addEnergy(e);
        p.addEnergy(e);
        assertEquals(3, p.numColorless());

        p.removeColorless(2);
        assertEquals(1, p.numColorless());
    }

    @Test
    public void testRemoveTooManyColorless() {
        Pokemon p = new Pokemon("Pikachu", "Lightning", 1, 40);
        boolean pass = false;
        try {
            p.removeColorless(1);
        } catch (IllegalArgumentException e) {
            pass = true;
            assertEquals("Can not remove this many energies!", e.getMessage());
        }
        assertTrue(pass);

    }



    @Test
    public void testCanRetreatManyEnergy() {
        CardGenerator pg = new CardGenerator();
        Pokemon p = (Pokemon) pg.generateCard("Charizard");

        Energy e = createMock(Energy.class);

        assertFalse(p.canRetreat());
        p.addEnergy(e);
        assertFalse(p.canRetreat());
        p.addEnergy(e);
        assertFalse(p.canRetreat());
        p.addEnergy(e);
        assertTrue(p.canRetreat());
    }

    @Test
    public void testRemoveColorless() {
        CardGenerator pg = new CardGenerator();
        Pokemon p = (Pokemon) pg.generateCard("Pikachu");

        Energy e = createMock(Energy.class);
        p.addEnergy(e);
        assertTrue(p.canAttack());
        p.removeColorless(1);
        assertFalse(p.canAttack());
    }

    @Test
    public void testRemoveManyColorless() {
        CardGenerator pg = new CardGenerator();
        Pokemon p = (Pokemon) pg.generateCard("Charizard");

        Energy e = createMock(Energy.class);
        Energy e2 = createMock(Energy.class);

        p.addEnergy(e);
        p.addEnergy(e2);
        p.addEnergy(e);
        p.addEnergy(e);
        p.addEnergy(e2);
        p.addEnergy(e);
        p.addEnergy(e2);

        assertEquals(7, p.numColorless());

        p.removeColorless(7);
        assertEquals(0, p.numColorless());
    }

    @Test
    public void testRemoveNoEnergy() {
        CardGenerator pg = new CardGenerator();
        Pokemon p = (Pokemon) pg.generateCard("Pikachu");

        assertThrows(IllegalArgumentException.class, () -> p.removeColorless(1));
    }

    @Test
    public void testGetEnergiesString() {
        Pokemon p = new Pokemon("Pikachu", "Lightning", 1, 40);
        Energy e1 = createMock(Energy.class);
        Energy e2 = createMock(Energy.class);
        e1.name = "Fire";
        e2.name = "Water";
        replay(e1, e2);

        p.addEnergy(e1);
        p.addEnergy(e2);

        String expected = """
                Fire
                Water
                """;
        assertEquals(expected, p.getEnergiesString());
    }


    @Test
    public void testEvolvesFrom() {
        CardGenerator pg = new CardGenerator();
        Pokemon p = (Pokemon) pg.generateCard("Kakuna");
        assertEquals("Weedle", p.evolvesFrom);
    }
}