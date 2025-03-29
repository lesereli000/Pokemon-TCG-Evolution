import org.json.JSONArray;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class DeckTest {

    //TODO: Tests moved from PokemonTest.java

    @Test
    public void testEmptyDeck() {
        Deck d = new Deck();
        assertEquals(0, d.size());
    }

    @Test
    public void testOneCard() {
        Deck d = new Deck();
        Pokemon p = createMock(Pokemon.class);
        d.addCard(p);
        assertEquals(1, d.size());
    }

    @Test
    public void testGetLargeSizeDeck() {
        Deck d = new Deck();
        Energy e = createMock(Energy.class);
        for (int i = 0; i < 300; i++) {
            d.addCard(e);
        }
        assertEquals(300, d.size());
    }

    @Test
    public void testFirstCard() {
        Deck d = new Deck();
        Pokemon p = createMock(Pokemon.class);
        d.addCard(p);
        ArrayList<Card> pokemons = d.getCards();
        assertEquals(pokemons.get(0), p);
    }

    // https://stackoverflow.com/questions/34233447/is-there-any-difference-between-andreturn-anytimes-and-andstubreturn
    //Explains anyTimes();

    @Test
    public void testNoMoreFourRepeats() {
        Deck d = new Deck();
        Pokemon p = createMock(Pokemon.class);
        expect(p.getName()).andReturn("Pikachu").anyTimes();
        replay(p);
        boolean pass = false;
        d.addCard(p);
        d.addCard(p);
        d.addCard(p);

        try {
            d.addCard(p);
            pass = true;
        } catch (Deck.TooManyRepeatsException e) {
            assertNotEquals("Too many repeats with card " + "Pikachu", e.getMessage());
        }

        assertTrue(pass);
        pass = false;

        try {
            d.addCard(p);
        } catch (Deck.TooManyRepeatsException e) {
            assertEquals("Too many repeats with card " + "Pikachu", e.getMessage());
            pass = true;
        }

        assertTrue(pass);
        verify(p);
    }

    @Test
    public void testEnergyCardsCanRepeat() {
        Deck d = new Deck();
        Energy e = createMock(Energy.class);
        boolean pass = false;

        try {
            d.addCard(e);
            d.addCard(e);
            d.addCard(e);
            d.addCard(e);
            d.addCard(e);
            d.addCard(e);
            pass = true;
        } catch (Deck.TooManyRepeatsException err) {
            assertNotEquals("Too many repeats with card Fighting Energy", err.getMessage());
        }
        assertTrue(pass);
    }

    @Test
    public void testPokemonAndEnergyRepeats() {
        Deck d = new Deck();
        Pokemon p = createMock(Pokemon.class);
        expect(p.getName()).andReturn("Charizard").anyTimes();
        replay(p);
        Energy e = createMock(Energy.class);
        expect(e.getName()).andReturn("Grass Energy").anyTimes();
        replay(e);

        d.addCard(p);
        d.addCard(e);
        d.addCard(p);
        d.addCard(p);
        d.addCard(e);
        d.addCard(e);
        d.addCard(p);
        d.addCard(e);

        // Deck now has 4 Energy and 4 Pokemon

        d.addCard(e);
        d.addCard(e);
        d.addCard(e);

        boolean pass = false;

        try {
            d.addCard(p);
        } catch (Deck.TooManyRepeatsException err) {
            assertEquals("Too many repeats with card Charizard", err.getMessage());
            pass = true;
        }

        assertTrue(pass);
        verify(p);
        verify(e);
    }

    @Test
    public void testNotTooManyRepeatsRandom() {
        Random rand = createMock(Random.class);
        expect(rand.nextInt(anyInt())).andReturn(1).anyTimes();
        replay(rand);

        boolean pass = false;
        Deck d = new Deck();
        d.addRandomCards(4, rand);

        try {
            d.addRandomCards(1, rand);
        } catch (Deck.TooManyRepeatsException err) {
            assertEquals("Too many repeats with card Blastoise", err.getMessage());
            pass = true;
        }
        assertTrue(pass);
        verify(rand);
    }


    public ArrayList<Card> getAllCards() {
        ArrayList<Card> allCards = new ArrayList<Card>();
        CardGenerator cg = new CardGenerator();

        try (FileReader reader = new FileReader("base1.json")) {
            String content = new String(Files.readAllBytes(Paths.get("base1.json")));
            JSONArray pokemonArray = new JSONArray(content);
            for (int i = 0; i < pokemonArray.length(); i++) {
                String PokemonName = pokemonArray.getJSONObject(i).getString("name");
                Card card = cg.generateCard(PokemonName);
                allCards.add(card);
            }
        } catch (IOException e) {
            System.out.println("File not found in getAllCards" + e);
            return null;
        }
        return allCards;
    }
    /*

    @Test
    public void testGetPokemon() {
        // TODO: This method appears to be testing code written to set up tests rather than production code. We have enough other tests of Pokemon Generator
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
     */

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
            if(card instanceof Pokemon p) {
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
        d.addRandomCards(10, new Random());
        assertEquals(10, d.size());
    }

    @Test
    public void testMakeFullDeckCards() {
        Deck d = new Deck();
        d.addRandomCards(60, new Random());
        assertEquals(60, d.size());
    }

    @Test
    public void testAddTrainerToDeck() {
        Card t = new Trainer("Scoop Up", "Test");
        Deck d = new Deck();
        d.addCard(t);
        assertEquals(1, d.size());
        assertEquals(t, d.getCards().get(0));
        assertEquals("Scoop Up", ((Trainer) d.getCards().get(0)).getName());
    }

    @Test
    public void mixTrainersAndPokemon() {
        Card trainer = new Trainer("Defender", "Test");
        Deck d = new Deck();
        CardGenerator pg = new CardGenerator();
        Pokemon p1 = (Pokemon) pg.generateCard("Charizard");
        Pokemon p2 = (Pokemon) pg.generateCard("Pikachu");
        Pokemon p3 = (Pokemon) pg.generateCard("Squirtle");

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
        Card trainer = new Trainer("Scoop Up", "Test");
        Card e = new Energy("Lightning Energy");
        Card e2 = new Energy("Psychic Energy");
        Deck d = new Deck();
        Pokemon p1 = (Pokemon) new CardGenerator().generateCard("Charizard");
        Pokemon p2 = (Pokemon) new CardGenerator().generateCard("Pikachu");
        Pokemon p3 = (Pokemon) new CardGenerator().generateCard("Squirtle");

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
        d.addCard(new CardGenerator().generateCard("Pikachu"));
        d.addCard(new CardGenerator().generateCard("Squirtle"));
        d.addCard(new CardGenerator().generateCard("Psychic Energy"));
        d.addCard(new CardGenerator().generateCard("Potion"));
        d.addCard(new CardGenerator().generateCard("Water Energy"));

        ArrayList<Card> originalDeck = new ArrayList<Card>(d.getCards());
        d.shuffle();
        ArrayList<Card> shuffledDeck = d.getCards();
        assertEquals(originalDeck.size(), shuffledDeck.size());

        Set<Card> originalSet = new HashSet<Card>(originalDeck);
        Set<Card> shuffledSet = new HashSet<Card>(shuffledDeck);
        assertEquals(originalSet, shuffledSet);

        assertNotEquals(originalDeck, shuffledDeck);
    }
}
