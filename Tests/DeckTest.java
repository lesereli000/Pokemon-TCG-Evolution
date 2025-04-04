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
        Card p = createMock(Pokemon.class);
        d.addCard(p);
        assertEquals(1, d.size());
    }

    @Test
    public void testGetLargeSizeDeck() {
        Deck d = new Deck();
        Card e = createMock(Energy.class);
        for (int i = 0; i < 300; i++) {
            d.addCard(e);
        }
        assertEquals(300, d.size());
    }

    @Test
    public void testFirstCard() {
        Deck d = new Deck();
        Card p = createMock(Pokemon.class);
        d.addCard(p);
        ArrayList<Card> pokemons = d.getCards();
        assertEquals(pokemons.get(0), p);
    }

    @Test
    public void testAddTrainerToDeck() {
        Card t = createMock(Trainer.class);
        expect(t.getName()).andReturn("Scoop Up");
        replay(t);
        Deck d = new Deck();

        d.addCard(t);
        assertEquals(1, d.size());
        assertEquals(t, d.getCards().get(0));
        assertEquals("Scoop Up", (d.getCards().get(0)).getName());
        verify(t);
    }

    @Test
    public void mixTrainersAndPokemon() {
        Card trainer = createMock(Trainer.class);
        expect(trainer.getName()).andReturn("Scoop Up").anyTimes();
        replay(trainer);

        Card p1 = createMock(Pokemon.class);
        expect(p1.getName()).andReturn("Charizard").anyTimes();
        replay(p1);

        Card p2 = createMock(Pokemon.class);
        expect(p2.getName()).andReturn("Pikachu").anyTimes();
        replay(p2);

        Card p3 = createMock(Pokemon.class);
        expect(p3.getName()).andReturn("Squirtle").anyTimes();
        replay(p3);

        Deck d = new Deck();

        d.addCard(p1);
        d.addCard(p2);
        d.addCard(trainer);
        d.addCard(p3);

        assertEquals(4, d.size());
        assertEquals(p1.getName(), d.getCards().get(0).getName());
        assertEquals(p2.getName(), d.getCards().get(1).getName());
        assertEquals(trainer.getName(), d.getCards().get(2).getName());
        assertEquals(p3.getName(), d.getCards().get(3).getName());

        verify(trainer);
        verify(p1);
        verify(p2);
        verify(p3);
    }

    @Test
    public void testAddEnergyToDeck() {
        Card e = createMock(Energy.class);
        expect(e.getName()).andReturn("Lightning Energy").anyTimes();
        replay(e);
        Deck d = new Deck();
        d.addCard(e);
        assertEquals(1, d.size());
        assertEquals(e, d.getCards().get(0));
        assertEquals("Lightning Energy", (d.getCards().get(0)).getName());
        verify(e);
    }

    @Test
    public void testMakeFullDeck() {
        Card trainer = createMock(Trainer.class);
        expect(trainer.getName()).andReturn("Scoop Up").anyTimes();
        replay(trainer);

        Card e = createMock(Energy.class);
        expect(e.getName()).andReturn("Lightning Energy").anyTimes();
        replay(e);

        Card e2 = createMock(Energy.class);
        expect(e2.getName()).andReturn("Psychic Energy").anyTimes();
        replay(e2);

        Deck d = new Deck();

        Card p1 = createMock(Pokemon.class);
        expect(p1.getName()).andReturn("Charizard").anyTimes();
        replay(p1);

        Card p2 = createMock(Pokemon.class);
        expect(p2.getName()).andReturn("Pikachu").anyTimes();
        replay(p2);

        Card p3 = createMock(Pokemon.class);
        expect(p3.getName()).andReturn("Squirtle").anyTimes();
        replay(p3);

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

        verify(trainer);
        verify(e);
        verify(e2);
        verify(p1);
        verify(p2);
        verify(p3);
    }

    @Test
    public void testHowManyRepeatsZero() {
        Deck d = new Deck();
        Card p = createMock(Pokemon.class);
        expect(p.getName()).andReturn("Pikachu").anyTimes();
        replay(p);

        assertEquals(0, d.howManyRepeats(p));

        verify(p);
    }

    @Test
    public void testHowManyRepeatsOne() {
        Deck d = new Deck();

        Card p = createMock(Pokemon.class);
        expect(p.getName()).andReturn("Pikachu").anyTimes();
        replay(p);

        d.addCard(p);

        assertEquals(1, d.howManyRepeats(p));

        verify(p);
    }

    @Test
    public void testHowManyRepeatsMany() {
        Deck d = new Deck();

        Card p = createMock(Pokemon.class);
        expect(p.getName()).andReturn("Pikachu").anyTimes();
        replay(p);

        Card p2 = createMock(Pokemon.class);
        expect(p2.getName()).andReturn("Squirtle").anyTimes();
        replay(p2);

        Card e = createMock(Energy.class);
        expect(e.getName()).andReturn("Lightning Energy").anyTimes();
        replay(e);

        d.addCard(p);
        d.addCard(p2);
        d.addCard(e);

        assertEquals(1, d.howManyRepeats(p));
        assertEquals(1, d.howManyRepeats(p2));
        assertEquals(1, d.howManyRepeats(e));

        d.addCard(p);
        d.addCard(e);
        d.addCard(e);
        d.addCard(e);

        assertEquals(2, d.howManyRepeats(p));
        assertEquals(1, d.howManyRepeats(p2));
        assertEquals(4, d.howManyRepeats(e));

        verify(p);
        verify(p2);
        verify(e);
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
    public void testMakeRandomCards() {
        Deck d = new Deck();
        Random rand = createMock(Random.class);
        expect(rand.nextInt(anyInt())).andReturn(100).anyTimes();
        replay(rand);
        d.addRandomCards(10, rand);
        assertEquals(10, d.size());
        verify(rand);
    }

    @Test
    public void testMakeFullDeckCards() {
        Deck d = new Deck();
        Random rand = createMock(Random.class);
        expect(rand.nextInt(anyInt())).andReturn(100).anyTimes();
        replay(rand);
        d.addRandomCards(60, rand);
        assertEquals(60, d.size());
        verify(rand);
    }

//    @Test
//    public void testNotTooManyRepeatsRandom() {
//        Random rand = createMock(Random.class);
//        expect(rand.nextInt(anyInt())).andReturn(1).times(20).andReturn(2).anyTimes();
//        replay(rand);
//
//        boolean pass = false;
//        Deck d = new Deck();
//        d.addRandomCards(4, rand);
//
//        try {
//            d.addRandomCards(1, rand);
//        } catch (Deck.TooManyRepeatsException err) {
//            assertEquals("Too many repeats with card Blastoise", err.getMessage());
//            pass = true;
//        }
//        assertTrue(pass);
//        verify(rand);
//    }

//    @Test
//    public void testNotTooManyEnergyRepeats() {
//        Random rand = createMock(Random.class);
//        expect(rand.nextInt(anyInt())).andReturn(100).anyTimes();
//        replay(rand);
//
//        Card e = createMock(Energy.class);
//        expect(e.getName()).andReturn("Grass Energy").anyTimes();
//        replay(e);
//
//        boolean pass = false;
//        Deck d = new Deck();
//
//        d.addCard(e);
//        d.addCard(e);
//
//        try {
//            d.addCard(e);
//        } catch (Deck.TooManyRepeatsException err) {
//            assertEquals("Too many repeats with card Grass Energy", err.getMessage());
//            pass = true;
//        }
//
//        assertTrue(pass);
//        verify(rand);
//        verify(e);
//    }

    @Test
    public void testRemoveTopCardFromDeckSizeOne() {
        Deck d = new Deck();

        Card p = createMock(Pokemon.class);
        expect(p.getName()).andReturn("Pikachu").anyTimes();
        replay(p);

        d.addCard(p);

        assertEquals(p, d.removeTopCard());
        assertEquals(0, d.size());
        verify(p);
    }

    @Test
    public void testRemoveTopCardFromLargeDeck() {
        Deck d = new Deck();

        Card p = createMock(Pokemon.class);
        expect(p.getName()).andReturn("Pikachu").anyTimes();
        replay(p);

        Card e = createMock(Energy.class);
        expect(e.getName()).andReturn("Grass Energy").anyTimes();
        replay(e);

        Card t = createMock(Trainer.class);
        expect(t.getName()).andReturn("Scoop Up").anyTimes();
        replay(t);

        d.addCard(p);
        d.addCard(e);
        d.addCard(p);
        d.addCard(e);
        d.addCard(t);
        d.addCard(t);
        d.addCard(p);

        assertEquals(p, d.removeTopCard());
        assertEquals(6, d.size());
        assertEquals(t, d.removeTopCard());
        assertEquals(5, d.size());
        assertEquals(t, d.removeTopCard());
        assertEquals(4, d.size());
        assertEquals(e, d.removeTopCard());
        assertEquals(3, d.size());
        assertEquals(p, d.removeTopCard());
        assertEquals(2, d.size());
        assertEquals(e, d.removeTopCard());
        assertEquals(1, d.size());
        assertEquals(p, d.removeTopCard());

        verify(p);
        verify(e);
        verify(t);
    }

    @Test
    public void testRemoveTopCardFromEmptyDeck() {
        Deck d = new Deck();
        boolean pass = false;
        try {
            d.removeTopCard();
        } catch (Deck.EmptyDeckException err) {
            assertEquals("Can not remove card from an empty deck", err.getMessage());
            pass = true;
        }
        assertTrue(pass);
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
            if(card instanceof Pokemon p) {
                Pokemon addedP = (Pokemon) addedCards.get(i);
                assertEquals(addedP.type, p.type);
                assertEquals(addedP.stage, p.stage);
                assertEquals(addedP.hp, p.hp);
            }
        }
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
