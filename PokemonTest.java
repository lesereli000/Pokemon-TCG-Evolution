import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.json.JSONArray;
import org.junit.Test;

public class PokemonTest {

    @Test
    public void testSetup() {
        Pokemon p = new Pokemon("Pikachu", "Lightning", 1, 40);
        assertEquals("Pikachu", p.getName());
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
        ArrayList<Card> pokemons = d.getCards();
        assertEquals(pokemons.get(0), p);
    }

    @Test
    public void testPokemonGen() {
        Pokemon p = (Pokemon) new PokemonGenerator("Charizard").generate();

        assertEquals("Charizard", p.getName());
        assertEquals("Fire", p.type);
        assertEquals(120, p.hp);
        assertEquals(2, p.stage);
    }

    @Test
    public void testManyPokemon() {
        Pokemon p1 = (Pokemon) new PokemonGenerator("Charizard").generate();
        Pokemon p2 = (Pokemon) new PokemonGenerator("Pikachu").generate();
        Pokemon p3 = (Pokemon) new PokemonGenerator("Squirtle").generate();
        Pokemon p4 = (Pokemon) new PokemonGenerator("Alakazam").generate();
        Pokemon p5 = (Pokemon) new PokemonGenerator("Arcanine").generate();
        Pokemon p6 = (Pokemon) new PokemonGenerator("Magikarp").generate();

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

    public ArrayList<Card> getAllCards() {
        ArrayList<Card> allCards = new ArrayList<Card>();
        try (FileReader reader = new FileReader("base1.json")) {
            String content = new String(Files.readAllBytes(Paths.get("base1.json")));
            JSONArray pokemonArray = new JSONArray(content);
            for (int i = 0; i < pokemonArray.length(); i++) {
                String PokemonName = pokemonArray.getJSONObject(i).getString("name");
                Card card = new PokemonGenerator(PokemonName).generate();
                allCards.add(card);
            }
        } catch (IOException e) {
            System.out.println("File not found in getAllCards" + e);
            return null;
        }
        return allCards;
    }

    @Test
    public void testGetPokemon() {
        ArrayList<Card> allCards = getAllCards();
        //allPokemon should be in same order of base1.json file

        Pokemon p1 = (Pokemon) allCards.get(0);
        assertEquals("Alakazam", p1.getName());
        assertEquals("Psychic", p1.type);
        assertEquals(2, p1.stage);
        assertEquals(80, p1.hp);

        Pokemon p2 = (Pokemon) allCards.get(1);
        assertEquals("Blastoise", p2.getName());
        assertEquals("Water", p2.type);
        assertEquals(2, p2.stage);
        assertEquals(100, p2.hp);

        Pokemon p3 = (Pokemon) allCards.get(45);
        assertEquals("Charmander", p3.getName());
        assertEquals("Fire", p3.type);
        assertEquals(50, p3.hp);
        assertEquals(0, p3.stage);

        Pokemon p4 = (Pokemon) allCards.get(68);
        assertEquals("Weedle", p4.getName());
        assertEquals("Grass", p4.type);
        assertEquals(0, p4.stage);
        assertEquals(40, p4.hp);
    }

        @Test
    public void testGetSize() {
        Deck d = new Deck();
        ArrayList<Card> allPokemon = getAllCards();
        for (int i = 0; i < 60; i++) {
            Random rand = new Random();
            int num = rand.nextInt(allPokemon.size());
            d.addCard(allPokemon.get(num));
            allPokemon.remove(num);
        }
        assertEquals(60, d.size());
    }

    @Test
    public void testGetPokemonFromDeck() {
        Deck d = new Deck();
        ArrayList<Card> allCards = getAllCards();
        ArrayList<Card> addedCards = new ArrayList<Card>();
        for (int i = 0; i < allCards.size(); i++) {
            Random rand = new Random();
            int num = rand.nextInt(allCards.size());
            d.addCard(allCards.get(num));
            addedCards.add(allCards.get(num));
            allCards.remove(num);
        }

        for (int i = 0; i < allCards.size(); i++) {
            Card card = d.getCards().get(i);
            assertEquals(addedCards.get(i).getName(), d.getCards().get(i).getName());
            if(card instanceof Pokemon) {
                Pokemon p = (Pokemon) card;
                Pokemon addedP = (Pokemon) addedCards.get(i);
                assertEquals(addedP.type, p.type);
                assertEquals(addedP.stage, p.stage);
                assertEquals(addedP.hp, p.hp);
            }

        }
    }

    @Test
    public void testMakeRandomCards() {
        Deck d = new Deck();
        d.addRandomCards(10);
        assertEquals(10, d.size());
    }

    @Test
    public void testMakeFullDeckCards() {
        Deck d = new Deck();
        d.addRandomCards(60);
        assertEquals(60, d.size());
    }

    @Test
    public void testMakeTrainer() {
        Trainer t = new Trainer("Scoop Up");
            assertEquals("Scoop Up", t.getName());
    }

    @Test
    public void testAddTrainerToDeck() {
        Card t = new Trainer("Scoop Up");
        Deck d = new Deck();
        d.addCard(t);
        assertEquals(1, d.size());
        assertEquals(t, d.getCards().get(0));
        assertEquals("Scoop Up", ((Trainer) d.getCards().get(0)).getName());
    }

    @Test
    public void mixTrainersAndPokemon() {
        Card trainer = new Trainer("Defender");
        Deck d = new Deck();
        Pokemon p1 = (Pokemon) new PokemonGenerator("Charizard").generate();
        Pokemon p2 = (Pokemon) new PokemonGenerator("Pikachu").generate();
        Pokemon p3 = (Pokemon) new PokemonGenerator("Squirtle").generate();

        d.addCard(p1);
        d.addCard(p2);
        d.addCard(trainer);
        d.addCard(p3);

        assertEquals(4, d.size());
        assertEquals(p1, d.getCards().get(0));
        assertEquals(p2, d.getCards().get(1));
        assertEquals(trainer, d.getCards().get(2));
        assertEquals(p3, d.getCards().get(3));
    }

    @Test
    public void testMakeEnergy() {
        Card e = new Energy("Lightning Energy");
        assertEquals("Lightning Energy", e.getName());
    }

    @Test
    public void testAddEnergyToDeck() {
        Card e = new Energy("Lightning Energy");
        Deck d = new Deck();
        d.addCard(e);
        assertEquals(1, d.size());
        assertEquals(e, d.getCards().get(0));
        assertEquals("Lightning Energy", ((Energy) d.getCards().get(0)).getName());
    }

    @Test
    public void testMakeFullDeck() {
        Card trainer = new Trainer("Scoop Up");
        Card e = new Energy("Lightning Energy");
        Card e2 = new Energy("Psychic Energy");
        Deck d = new Deck();
        Pokemon p1 = (Pokemon) new PokemonGenerator("Charizard").generate();
        Pokemon p2 = (Pokemon) new PokemonGenerator("Pikachu").generate();
        Pokemon p3 = (Pokemon) new PokemonGenerator("Squirtle").generate();

        d.addCard(p1);
        d.addCard(e);
        d.addCard(p2);
        d.addCard(trainer);
        d.addCard(p3);
        d.addCard(e2);

        assertEquals(6, d.size());
        assertEquals(p1, d.getCards().get(0));
        assertEquals(e, d.getCards().get(1));
        assertEquals(p2, d.getCards().get(2));
        assertEquals(trainer, d.getCards().get(3));
        assertEquals(p3, d.getCards().get(4));
        assertEquals(e2, d.getCards().get(5));
    }

    @Test
    public void testDeckShuffle() {
        Deck d = new Deck();
        d.addCard(new PokemonGenerator("Pikachu").generate());
        d.addCard(new PokemonGenerator("Squirtle").generate());
        d.addCard(new PokemonGenerator("Psychic Energy").generate());
        d.addCard(new PokemonGenerator("Potion").generate());
        d.addCard(new PokemonGenerator("Water Energy").generate());

        ArrayList<Card> originalDeck = new ArrayList<Card>(d.getCards());
        d.shuffle();
        ArrayList<Card> shuffledDeck = d.getCards();
        assertEquals(originalDeck.size(), shuffledDeck.size());

        Set<Card> originalSet = new HashSet<Card>(originalDeck);
        Set<Card> shuffledSet = new HashSet<Card>(shuffledDeck);
        assertEquals(originalSet, shuffledSet);

        assertNotEquals(originalDeck, shuffledDeck);
    }

    @Test
    public void testGUI() {
        GUI gui = new GUI();
    }

}