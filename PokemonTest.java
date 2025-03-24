import static org.junit.Assert.*;

import org.junit.Test;

import org.easymock.EasyMock;

import java.util.ArrayList;


public class PokemonTest {

    @Test
    public void testSetup() {
        Pokemon p = new Pokemon("Pikachu", "Lightning", 1, 40);
        assertEquals("Pikachu", p.name);
        assertEquals("Lightning", p.type);
        assertEquals(1, p.stage);
        assertEquals(40, p.hp);
    }

    @Test
    public void testDamage() {
        Pokemon p = new Pokemon("Pikachu", "Lightning", 1, 40);
        p.takeDamage(10);
        assertEquals(30, p.hp);
    }

    @Test
    public void testEmptyDeck() {
        Deck d = new Deck();
        assertEquals(0, d.size());
    }

    @Test
    public void testOneCard() {
        Deck d = new Deck();
        Pokemon p = new Pokemon("Pikachu", "Lightning", 1, 40);
        d.addCard(p);
        assertEquals(1, d.size());
    }

    @Test
    public void testFirstCard() {
        Deck d = new Deck();
        Pokemon p = new Pokemon("Pikachu", "Lightning", 1, 40);
        d.addCard(p);
        ArrayList<Pokemon> pokemons = d.getCards();
        assertEquals(pokemons.get(0), p);
    }

    @Test
    public void testPokemonGen() {
        Pokemon p = new PokemonGenerator("Charizard").generate();

        assertEquals("Charizard", p.name);
        assertEquals("Fire", p.type);
        assertEquals(120, p.hp);
        assertEquals(2, p.stage);
    }

    @Test
    public void testManyPokemon() {
        Pokemon p1 = new PokemonGenerator("Charizard").generate();
        Pokemon p2 = new PokemonGenerator("Pikachu").generate();
        Pokemon p3 = new PokemonGenerator("Squirtle").generate();
        Pokemon p4 = new PokemonGenerator("Alakazam").generate();
        Pokemon p5 = new PokemonGenerator("Arcanine").generate();

        assertEquals("Charizard", p1.name);
        assertEquals("Fire", p1.type);
        assertEquals(2, p1.stage);
        assertEquals(120, p1.hp);

        assertEquals("Pikachu", p2.name);
        assertEquals("Lightning", p2.type);
        assertEquals(0, p2.stage);
        assertEquals(40, p2.hp);

        assertEquals("Squirtle", p3.name);
        assertEquals("Water", p3.type);
        assertEquals(0, p3.stage);
        assertEquals(40, p3.hp);

        assertEquals("Alakazam", p4.name);
        assertEquals("Psychic", p4.type);
        assertEquals(2, p4.stage);
        assertEquals(80, p4.hp);

        assertEquals("Arcanine", p5.name);
        assertEquals("Fire", p5.type);
        assertEquals(1, p5.stage);
        assertEquals(100, p5.hp);




    }

    @Test
    public void testGUI() {
        GUI gui = new GUI();
    }

}