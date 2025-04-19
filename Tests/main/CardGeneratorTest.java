package main;

import org.junit.Test;

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
        assertEquals("Fire", p.type);
        assertEquals(120, p.hp);
        assertEquals(2, p.stage);
    }

    private void checkManyPokemonAreCorrect(Pokemon p1, Pokemon p2, Pokemon p3, Pokemon p4, Pokemon p5, Pokemon p6) {
        assertEquals("Charizard", p1.getName());
        assertEquals("Fire", p1.type);
        assertEquals(2, p1.stage);
        assertEquals(120, p1.hp);

        assertEquals("Pikachu", p2.getName());
        assertEquals("Lightning", p2.type);
        assertEquals(0, p2.stage);
        assertEquals(40, p2.hp);

        assertEquals("Squirtle", p3.getName());
        assertEquals("Water", p3.type);
        assertEquals(0, p3.stage);
        assertEquals(40, p3.hp);

        assertEquals("Alakazam", p4.getName());
        assertEquals("Psychic", p4.type);
        assertEquals(2, p4.stage);
        assertEquals(80, p4.hp);

        assertEquals("Arcanine", p5.getName());
        assertEquals("Fire", p5.type);
        assertEquals(1, p5.stage);
        assertEquals(100, p5.hp);

        assertEquals("Magikarp", p6.getName());
        assertEquals("Water", p6.type);
        assertEquals(0, p6.stage);
        assertEquals(30, p6.hp);
    }
}
