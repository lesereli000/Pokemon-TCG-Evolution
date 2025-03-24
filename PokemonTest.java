import static org.junit.Assert.*;

import org.junit.Test;

import org.easymock.EasyMock;

import java.util.ArrayList;


public class PokemonTest {

    @Test
    public void testSetup() {
        Pokemon p = new Pokemon("Pikachu", "Fire", 1, 40);
        assertEquals("Pikachu", p.name);
        assertEquals("Fire", p.type);
        assertEquals(1, p.stage);
        assertEquals(40, p.hp);
    }

    @Test
    public void testDamage() {
        Pokemon p = new Pokemon("Pikachu", "Fire", 1, 40);
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
        Pokemon p = new Pokemon("Pikachu", "Fire", 1, 40);
        d.addCard(p);
        assertEquals(1, d.size());
    }

    @Test
    public void testFirstCard() {
        Deck d = new Deck();
        Pokemon p = new Pokemon("Pikachu", "Fire", 1, 40);
        d.addCard(p);
        ArrayList<Pokemon> pokemons = d.getCards();
        assertEquals(pokemons.get(0), p);
    }

    @Test
    public void testRandomPokemon() {
        Deck d = EasyMock.mock(Deck.class);
        Pokemon charizard = new Pokemon("Charizard", "Fire", 2, 120);
        d.addCard(charizard);
    }

    @Test
    public void testGUI() {
        GUI gui = new GUI();
    }

}