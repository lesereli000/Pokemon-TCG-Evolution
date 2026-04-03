package main;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class CardGeneratorTest {

    @Test
    public void testEmptyName() {
        String name = "";
        CardGenerator pg = new CardGenerator();
        boolean pass = false;

        try {
            pg.generateCard(name);
        } catch (CardGenerator.PokemonNotFoundException e) {
            assertEquals("Invalid Name", e.getMessage());
            pass = true;
        }

        assertTrue(pass);
    }

    @Test
    public void testPokemonGen() {
        Pokemon p = (Pokemon) new CardGenerator().generateCard("Charizard");

        checkSinglePokemonIsCorrect(p);
    }

    @Test
    public void testManyPokemon() {
        CardGenerator pg = new CardGenerator();
        Pokemon p1 = (Pokemon) pg.generateCard("Charizard");
        Pokemon p2 = (Pokemon) pg.generateCard("Pikachu");
        Pokemon p3 = (Pokemon) pg.generateCard("Squirtle");
        Pokemon p4 = (Pokemon) pg.generateCard("Alakazam");
        Pokemon p5 = (Pokemon) pg.generateCard("Arcanine");
        Pokemon p6 = (Pokemon) pg.generateCard("Magikarp");

        checkManyPokemonAreCorrect(p1, p2, p3, p4, p5, p6);
    }

    private void checkSinglePokemonIsCorrect(Pokemon p) {
        assertEquals("Charizard", p.getName());
        assertEquals("Fire", p.getType());
        assertEquals(120, p.hp);
        assertEquals(2, p.stage);
        assertEquals(3, p.retreatCost);
        checkSingleCosts(p);
    }

    private void checkManyPokemonAreCorrect(Pokemon p1, Pokemon p2, Pokemon p3, Pokemon p4, Pokemon p5, Pokemon p6) {
        assertEquals("Charizard", p1.getName());
        assertEquals("Fire", p1.getType());
        assertEquals(2, p1.stage);
        assertEquals(120, p1.hp);
        assertEquals(3, p1.retreatCost);

        assertEquals("Pikachu", p2.getName());
        assertEquals("Lightning", p2.getType());
        assertEquals(0, p2.stage);
        assertEquals(40, p2.hp);
        assertEquals(1, p2.retreatCost);

        assertEquals("Squirtle", p3.getName());
        assertEquals("Water", p3.getType());
        assertEquals(0, p3.stage);
        assertEquals(40, p3.hp);
        assertEquals(1, p3.retreatCost);


        assertEquals("Alakazam", p4.getName());
        assertEquals("Psychic", p4.getType());
        assertEquals(2, p4.stage);
        assertEquals(80, p4.hp);
        assertEquals(3, p4.retreatCost);


        assertEquals("Arcanine", p5.getName());
        assertEquals("Fire", p5.getType());
        assertEquals(1, p5.stage);
        assertEquals(100, p5.hp);
        assertEquals(3, p5.retreatCost);
        assertEquals("Growlithe", p5.getEvolvesFrom()); // Kills survivor in CardGenerator

        assertEquals("Magikarp", p6.getName());
        assertEquals("Water", p6.getType());
        assertEquals(0, p6.stage);
        assertEquals(30, p6.hp);
        assertEquals(1, p6.retreatCost);

        checkManyCosts(p1, p2, p3, p4, p5, p6);
    }

    private void checkManyCosts(Pokemon p1, Pokemon p2, Pokemon p3, Pokemon p4, Pokemon p5, Pokemon p6) {

        String fireEnergy = "Fire Energy";
        String colorlessEnergy = "Colorless Energy";
        String lightingEnergy = "Lightning Energy";
        String waterEnergy = "Water Energy";
        String psychicEnergy = "Psychic Energy";

        ArrayList<Attack> p1attacks = p1.attacks;
        Attack p1attack1 = p1attacks.get(0);
        assertEquals("Fire Spin", p1attack1.name);
        assertEquals(100, p1attack1.damage);
        ArrayList<String> expectedCosts = new ArrayList<>();
        expectedCosts.add(fireEnergy);
        expectedCosts.add(fireEnergy);
        expectedCosts.add(fireEnergy);
        expectedCosts.add(fireEnergy);

        assertEquals(expectedCosts.size(), p1attack1.costs.size());
        for(Energy e : p1attack1.costs) {
            assertTrue(expectedCosts.contains(e.getName()));
        }

        ArrayList<Attack> p2Attacks = p2.attacks;
        Attack p2attack1 = p2Attacks.get(0);

        assertEquals("Gnaw", p2attack1.name);
        assertEquals(10, p2attack1.damage);
        ArrayList<String> p2attack1ExpectedCosts = new ArrayList<>();
        p2attack1ExpectedCosts.add(colorlessEnergy);
        assertEquals(p2attack1ExpectedCosts.size(), p2attack1.costs.size());
        for(Energy e : p2attack1.costs) {
            assertTrue(p2attack1ExpectedCosts.contains(e.getName()));
        }
        Attack p2attack2 = p2Attacks.get(1);
        assertEquals("Thunder Jolt", p2attack2.name);
        assertEquals(30, p2attack2.damage);
        ArrayList<String> p2attack2ExpectedCosts = new ArrayList<>();
        p2attack2ExpectedCosts.add(colorlessEnergy);
        p2attack2ExpectedCosts.add(lightingEnergy);
        assertEquals(p2attack2ExpectedCosts.size(), p2attack2.costs.size());
        for(Energy e : p2attack2.costs) {
            assertTrue(p2attack2ExpectedCosts.contains(e.getName()));
        }

        ArrayList<Attack> p3Attacks = p3.attacks;
        Attack p3attack1 = p3Attacks.get(0);
        assertEquals("Bubble", p3attack1.name);
        assertEquals(10, p3attack1.damage);
        ArrayList<String> p3attackExpectedCosts = new ArrayList<>();
        p3attackExpectedCosts.add(waterEnergy);
        assertEquals(p3attackExpectedCosts.size(), p3attack1.costs.size());
        for(Energy e : p3attack1.costs) {
            assertTrue(p3attackExpectedCosts.contains(e.getName()));
        }
        Attack p3attack2 = p3Attacks.get(1);
        assertEquals("Withdraw", p3attack2.name);
        assertEquals(20, p3attack2.damage);
        p3attackExpectedCosts.add(colorlessEnergy);
        assertEquals(p3attackExpectedCosts.size(), p3attack2.costs.size());
        for(Energy e : p3attack2.costs) {
            assertTrue(p3attackExpectedCosts.contains(e.getName()));
        }

        ArrayList<Attack> p4Attacks = p4.attacks;
        Attack p4attack = p4Attacks.get(0);
        assertEquals("Confuse Ray", p4attack.name);
        assertEquals(30, p4attack.damage);
        ArrayList<String> p4attackExpectedCost = new ArrayList<>();
        p4attackExpectedCost.add(psychicEnergy);
        p4attackExpectedCost.add(psychicEnergy);
        p4attackExpectedCost.add(psychicEnergy);
        assertEquals(p4attackExpectedCost.size(), p4attack.costs.size());
        for(Energy e : p4attack.costs) {
            assertTrue(p4attackExpectedCost.contains(e.getName()));
        }

        ArrayList<Attack> p5Attacks = p5.attacks;
        Attack p5attack1 = p5Attacks.get(0);
        assertEquals("Flamethrower", p5attack1.name);
        assertEquals(50, p5attack1.damage);
        ArrayList<String> p5attack1ExpectedCost = new ArrayList<>();
        p5attack1ExpectedCost.add(fireEnergy);
        p5attack1ExpectedCost.add(fireEnergy);
        p5attack1ExpectedCost.add(colorlessEnergy);
        assertEquals(p5attack1ExpectedCost.size(), p5attack1.costs.size());
        for(Energy e : p5attack1.costs) {
            assertTrue(p5attack1ExpectedCost.contains(e.getName()));
        }
        Attack p5attack2 = p5Attacks.get(1);
        assertEquals("Take Down", p5attack2.name);
        assertEquals(80, p5attack2.damage);
        p5attack1ExpectedCost.add(colorlessEnergy);
        assertEquals(p5attack1ExpectedCost.size(), p5attack2.costs.size());
        for(Energy e : p5attack2.costs) {
            assertTrue(p5attack1ExpectedCost.contains(e.getName()));
        }

        ArrayList<Attack> p6Attacks = p6.attacks;
        Attack p6attack1 = p6Attacks.get(0);
        assertEquals("Tackle", p6attack1.name);
        assertEquals(10, p6attack1.damage);
        ArrayList<String> p6attack1ExpectedCost = new ArrayList<>();
        p6attack1ExpectedCost.add(colorlessEnergy);
        assertEquals(p6attack1ExpectedCost.size(), p6attack1.costs.size());
        for(Energy e : p6attack1.costs) {
            assertTrue(p6attack1ExpectedCost.contains(e.getName()));
        }
        Attack p6attack2 = p6Attacks.get(1);
        assertEquals("Flail", p6attack2.name);
        assertEquals(10, p6attack2.damage);
        ArrayList<String> p6attack2ExpectedCost = new ArrayList<>();
        p6attack2ExpectedCost.add(waterEnergy);
        assertEquals(p6attack2ExpectedCost.size(), p6attack2.costs.size());
        for(Energy e : p6attack2.costs) {
            assertTrue(p6attack2ExpectedCost.contains(e.getName()));
        }
    }

    private void checkSingleCosts(Pokemon p) {
        String fire = "Fire Energy";
        ArrayList<Attack> attacks = p.attacks;
        Attack attack1 = attacks.get(0);

        assertEquals("Fire Spin", attack1.name);
        assertEquals(100, attack1.damage);
        ArrayList<String> expectedCosts = new ArrayList<>();
        expectedCosts.add(fire);
        expectedCosts.add(fire);
        expectedCosts.add(fire);
        expectedCosts.add(fire);

        assertEquals(expectedCosts.size(), attack1.costs.size());
        for (Energy cost : attack1.costs) {
            assertTrue(expectedCosts.contains(cost.getName()));
        }
    }

    @Test
    public void testFakeFilepath() {
        CardGenerator cg = new CardGenerator();
        cg.resourcePath = "nonexistent.json";
        boolean pass = false;
        try {
            cg.generateCard("Pikachu");
        } catch (RuntimeException e) {
            pass = true;
            assertEquals("File not found in directory!", e.getMessage());
        }
        assertTrue(pass);
    }

    @Test
    public void testNoPokemon() {
        CardGenerator cg = new CardGenerator();
        cg.resourcePath = "empty.json"; // This file should exist in src/test/resources or just be missing

        try {
            cg.generateCard("Pikachu");
        } catch (RuntimeException e) {
            assertEquals("File not found in directory!", e.getMessage());
        }
    }
    @Test
    public void testCardImageUrls() {
        CardGenerator pg = new CardGenerator();
        
        // Test Pokemon
        Card alakazam = pg.generateCard("Alakazam");
        assertEquals("https://images.pokemontcg.io/base1/1.png", alakazam.getImageUrl());

        // Test Energy
        Card waterEnergy = pg.generateCard("Water Energy");
        assertEquals("https://images.pokemontcg.io/base1/102.png", waterEnergy.getImageUrl());

        // Test Trainer
        Card bill = pg.generateCard("Bill");
        assertEquals("https://images.pokemontcg.io/base1/91.png", bill.getImageUrl());
    }

}
