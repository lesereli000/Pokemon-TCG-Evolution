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
    public void testOneHP() {
        String name = "Pikachu";
        String type = "Lightning";
        int stage = 1;
        int hp = 1;

        Pokemon p = new Pokemon(name, type, stage, hp);
        assertEquals(1, p.getCurHP());
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
            assertEquals("Lightning", c.getType());
            assertEquals(stage, c.stage);
            assertEquals(hp, c.hp);

        } catch (CardCreationException e) {
            fail();
        }
    }

    @Test
    public void testBaseDamage() {
        Pokemon p = new Pokemon("Pikachu", "Lightning", 1, 40);
        p.takeDamage(1, null);
        assertEquals(30, p.getCurHP());
    }

    @Test
    public void testHeal() {
        Pokemon p = new Pokemon("Beedrill", "Grass", 1, 40);
        assertEquals(0, p.damageCounters);
        p.takeDamage(3, null);
        assertEquals(3, p.damageCounters);
        p.heal(2);
        assertEquals(1, p.damageCounters);
        p.heal(2);
        assertEquals(0, p.damageCounters);
        p.heal(2);
        assertEquals(0, p.damageCounters);
    }

    @Test
    public void testHealBoundary() {
        Pokemon p = new Pokemon("Bulbasaur", "Grass", 1, 40);
        p.takeDamage(1, null);
        assertEquals(1, p.damageCounters);

        p.heal(1);
        assertEquals(0, p.damageCounters);

        p.damageCounters = 0;
        p.heal(1);
        assertEquals(0, p.damageCounters);
    }

    @Test
    public void testCanAttackWithArgs() {
        CardGenerator pg = new CardGenerator();
        Pokemon p = (Pokemon) pg.generateCard("Pikachu");
        Attack a = createMock(Attack.class);
        Energy e = createMock(Energy.class);
        ArrayList<Energy> energies = new ArrayList<>();
        energies.add(e);
        p.energies = energies;

        // getEnergyMap()
        expect(e.getEnergyType()).andReturn(EnergyType.LIGHTNING).anyTimes();
        expect(e.getName()).andReturn("Lightning Energy").anyTimes();
        a.costs = energies;
        e.name = "Lightning Energy";

        replay(a, e);
        assertTrue(p.canAttack(a));
        verify(a, e);
    }

    @Test
    public void testCantAttackWithArgs() {
        CardGenerator pg = new CardGenerator();
        Pokemon p = (Pokemon) pg.generateCard("Pikachu");
        Attack a = createMock(Attack.class);
        Energy e = createMock(Energy.class);
        ArrayList<Energy> energies = new ArrayList<>();
        energies.add(e);
        p.energies = energies;

        // getEnergyMap()
        expect(e.getEnergyType()).andReturn(EnergyType.FIRE).anyTimes();
        expect(e.getName()).andReturn("Fire Energy").anyTimes();
        a.costs = energies; // Attack costs 1 Fire Energy
        // But the energy e mocked as FIRE is what the Pokemon has. 
        // Wait, I need them to be different.
        Energy e2 = createMock(Energy.class);
        expect(e2.getEnergyType()).andReturn(EnergyType.WATER).anyTimes();
        expect(e2.getName()).andReturn("Water Energy").anyTimes();
        ArrayList<Energy> costEnergies = new ArrayList<>();
        costEnergies.add(e2);
        a.costs = costEnergies; // Attack costs 1 Water Energy
        replay(e2);

        replay(a, e);
        assertFalse(p.canAttack(a));
        verify(a, e, e2);
    }

    @Test
    public void testGetAttacks() {
        CardGenerator pg = new CardGenerator();
        Pokemon p = (Pokemon) pg.generateCard("Pikachu");
        ArrayList<Attack> attacks = createMock(ArrayList.class);
        p.attacks = attacks;
        ArrayList<Attack> result = p.getAttacks();
        assertEquals(attacks, result);
    }

    @Test
    public void testGetType() {
        CardGenerator pg = new CardGenerator();
        Pokemon p = (Pokemon) pg.generateCard("Pikachu");
        assertEquals("Lightning", p.getType());
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
        expect(e.getEnergyType()).andReturn(EnergyType.COLORLESS).anyTimes();
        expect(e.getName()).andReturn("Colorless Energy").anyTimes();
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
        expect(e.getEnergyType()).andReturn(EnergyType.GRASS).anyTimes();

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
        expect(water.getName()).andReturn("Water Energy").anyTimes();
        expect(water.getEnergyType()).andReturn(EnergyType.WATER).anyTimes();
        expect(grass.getName()).andReturn("Grass Energy").anyTimes();
        expect(grass.getEnergyType()).andReturn(EnergyType.GRASS).anyTimes();

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
        expect(e.getEnergyType()).andReturn(EnergyType.LIGHTNING).anyTimes();
        replay(e);

        p.addEnergy(e);
        HashMap<EnergyType, Integer> map = p.getEnergyMap();
        assertTrue(map.containsKey(EnergyType.COLORLESS));
        assertEquals(1, (int) map.get(EnergyType.COLORLESS));
        assertEquals(1, (int) map.get(EnergyType.LIGHTNING));
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

        pikachu.takeDamage(1, null);
        pikachu.takeDamage(1, null);
        pikachu.takeDamage(1, null);
        assertTrue(pikachu.isAlive());
        pikachu.takeDamage(1, null);
        assertFalse(pikachu.isAlive());
    }

    @Test
    public void testRemoveEnergy() {
        CardGenerator pg = new CardGenerator();
        Pokemon pikachu = (Pokemon) pg.generateCard("Pikachu");

        Energy e = createMock(Energy.class);

        expect(e.getEnergyType()).andReturn(EnergyType.GRASS).anyTimes();
        expect(e.getName()).andReturn("Grass Energy").anyTimes();
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

        expect(e.getEnergyType()).andReturn(EnergyType.GRASS).anyTimes();
        expect(e.getName()).andReturn("Grass Energy").anyTimes();
        expect(e2.getEnergyType()).andReturn(EnergyType.LIGHTNING).anyTimes();
        expect(e2.getName()).andReturn("Lightning Energy").anyTimes();
        expect(e3.getEnergyType()).andReturn(EnergyType.WATER).anyTimes();
        expect(e3.getName()).andReturn("Water Energy").anyTimes();
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
        for (Energy energy : energies) {
            assertNotEquals(EnergyType.LIGHTNING, energy.getEnergyType());
        }

        kakuna.removeEnergy(e3);
        kakuna.removeEnergy(e3);
        kakuna.removeEnergy(e3);

        assertEquals(3, energies.size());
        for (Energy energy : energies) {
            assertNotEquals(EnergyType.WATER, energy.getEnergyType());
            assertNotEquals(EnergyType.LIGHTNING, energy.getEnergyType());
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
        expect(e1.getName()).andReturn("Fire Energy").anyTimes();
        expect(e1.getEnergyType()).andReturn(EnergyType.FIRE).anyTimes();
        expect(e2.getName()).andReturn("Water Energy").anyTimes();
        expect(e2.getEnergyType()).andReturn(EnergyType.WATER).anyTimes();
        replay(e1, e2);

        p.addEnergy(e1);
        p.addEnergy(e2);

        String expected = """
                Fire Energy
                Water Energy
                """;
        assertEquals(expected, p.getEnergiesString());
    }

    @Test
    public void testGetStage() {
        CardGenerator pg = new CardGenerator();
        Pokemon p = (Pokemon) pg.generateCard("Charizard");
        assertEquals(2, p.getStage());
    }

    @Test
    public void testEvolvesFrom() {
        CardGenerator pg = new CardGenerator();
        Pokemon p = (Pokemon) pg.generateCard("Kakuna");
        assertEquals("Weedle", p.evolvesFrom);
    }

    @Test
    public void testGetDamageCounters() {
        Pokemon p = new Pokemon("Pikachu", "Lightning", 1, 40);

        p.takeDamage(2, null);
        assertEquals(2, p.getDamageCounters());

        p.takeDamage(1, null);
        assertEquals(3, p.getDamageCounters());

    }

    @Test
    public void testGetEnergies() {
        Pokemon p = new Pokemon("Pikachu", "Lightning", 1, 40);

        p.addEnergy(createMock(Energy.class));
        p.addEnergy(createMock(Energy.class));
        assertEquals(2, p.getEnergies().size());

        p.addEnergy(createMock(Energy.class));
        assertEquals(3, p.getEnergies().size());

        p.addEnergy(createMock(Energy.class));
        assertEquals(4, p.getEnergies().size());
    }

    @Test
    public void testAddEnergies() {
        Pokemon p = new Pokemon("Pikachu", "Lightning", 0, 40);
        ArrayList<Energy> energies = new ArrayList<>();
        energies.add(new Energy(EnergyType.LIGHTNING));
        energies.add(new Energy(EnergyType.COLORLESS));

        p.addEnergies(energies);
        assertEquals(2, p.getEnergies().size());

        p.addEnergies(energies);
        assertEquals(4, p.getEnergies().size());
    }

    @Test
    public void testIsBasicPokemon() {
        Pokemon p0 = new Pokemon("Pikachu", "Lightning", 0, 40);
        assertTrue(p0.isBasicPokemon());
        Pokemon p1 = new Pokemon("Raichu", "Lightning", 1, 90);
        assertFalse(p1.isBasicPokemon());
    }

    @Test
    public void testGetCardType() {
        Pokemon p = new Pokemon("Pikachu", "Lightning", 0, 40);
        assertEquals(Card.CardType.POKEMON, p.getCardType());
    }

    @Test
    public void testGetReport() {
        java.util.ResourceBundle messages = new java.util.ListResourceBundle() {
            @Override
            protected Object[][] getContents() {
                return new Object[][] {
                    {"pokReport", "Report"}, {"pokName", "Name: {0}"}, {"pokStage", "Stage: {0}"},
                    {"pokType", "Type: {0}"}, {"pokHP", "HP: {0}"}, {"retreatCost", "Retreat: {0}"},
                    {"evolvesFrom", "From: {0}"}, {"pokEnergies", "Energies:"}, {"none", "None"},
                    {"atks", "Attacks"}, {"costs", "Costs"}, {"dmg", "D: {0}"}
                };
            }
        };

        Pokemon p = new Pokemon("Ivysaur", "Grass", 1, 60);
        p.setEvolvesFrom("Bulbasaur");
        
        String report = p.getReport(messages);
        assertTrue(report.contains("Ivysaur"));
        assertTrue(report.contains("Stage: 1"));
        assertTrue(report.contains("Type: Grass"));
        assertTrue(report.contains("From: Bulbasaur"));
        assertTrue(report.contains("None")); // No energies yet
    }

    @Test
    public void testGetReportWithEnergiesAndAttacks() {
        java.util.ResourceBundle messages = new java.util.ListResourceBundle() {
            @Override
            protected Object[][] getContents() {
                return new Object[][] {
                    {"pokReport", "Report"}, {"pokName", "Name: {0}"}, {"pokStage", "Stage: {0}"},
                    {"pokType", "Type: {0}"}, {"pokHP", "HP: {0}"}, {"retreatCost", "Retreat: {0}"},
                    {"pokEnergies", "Energies:"}, {"atks", "Attacks"}, {"costs", "Costs"}, {"dmg", "D: {0}"}
                };
            }
        };

        ArrayList<Attack> attacks = new ArrayList<>();
        attacks.add(new Attack("Thunder", new ArrayList<>(), 30));
        Pokemon p = new Pokemon("Pikachu", "Lightning", 0, 40, null, null, attacks, 1);
        p.addEnergy(new Energy(EnergyType.LIGHTNING));
        
        String report = p.getReport(messages);
        assertTrue(report.contains("Pikachu"));
        assertTrue(report.contains("Lightning Energy"));
        assertTrue(report.contains("Thunder"));
    }

    @Test
    public void testTakeDamageWithNullType() {
        Pokemon p = new Pokemon("P", "Grass", 0, 100);
        p.takeDamage(2, null);
        assertEquals(80, p.getCurHP());
    }

    @Test
    public void testRemoveEnergyNotFound() {
        Pokemon p = new Pokemon("P", "Grass", 0, 100);
        Energy e = new Energy(EnergyType.FIRE);
        assertThrows(IllegalArgumentException.class, () -> p.removeEnergy(e));
    }

    @Test
    public void testFullConstructorSpecialValues() {
        // Test "ids" and "sdad" special strings for weakness/resistance
        Pokemon p = new Pokemon("P", "Grass", 0, 100, "ids", "sdad", new ArrayList<>(), 1);
        assertNull(p.weakness);
        assertNull(p.resistance);
    }
}