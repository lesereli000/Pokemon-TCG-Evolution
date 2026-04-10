package main;

import org.json.JSONArray;
import org.junit.Test;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.*;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class DeckTest {

    //Tests moved from main.PokemonTest.java

    @Test
    public void testEmptyDeck() {
        Deck d = new Deck();
        assertEquals(0, d.size());
    }

    @Test
    public void testOneCard() {
        Deck d = new Deck();
        Card p = createMock(Pokemon.class);
        expect(p.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
        replay(p);
        d.addCard(p);
        assertEquals(1, d.size());
    }

    @Test
    public void testGetLargeSizeDeck() {
        Deck d = new Deck();
        Card e = createMock(Energy.class);
        expect(e.getCardType()).andReturn(Card.CardType.ENERGY).anyTimes();
        replay(e);
        for (int i = 0; i < 300; i++) {
            d.addCard(e);
        }
        assertEquals(300, d.size());
    }

    @Test
    public void testFirstCard() {
        Deck d = new Deck();
        Card p = createMock(Pokemon.class);
        expect(p.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
        replay(p);
        d.addCard(p);
        ArrayList<Card> pokemons = d.getCards();
        assertEquals(pokemons.get(0), p);
    }

    @Test
    public void testAddTrainerToDeck() {
        Card t = createMock(Trainer.class);
        expect(t.getCardType()).andReturn(Card.CardType.TRAINER).anyTimes();
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
        expect(trainer.getCardType()).andReturn(Card.CardType.TRAINER).anyTimes();
        expect(trainer.getName()).andReturn("Scoop Up").anyTimes();
        replay(trainer);

        Card p1 = createMock(Pokemon.class);
        expect(p1.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
        expect(p1.getName()).andReturn("Charizard").anyTimes();
        replay(p1);

        Card p2 = createMock(Pokemon.class);
        expect(p2.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
        expect(p2.getName()).andReturn("Pikachu").anyTimes();
        replay(p2);

        Card p3 = createMock(Pokemon.class);
        expect(p3.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
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
        expect(e.getCardType()).andReturn(Card.CardType.ENERGY).anyTimes();
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
        expect(trainer.getCardType()).andReturn(Card.CardType.TRAINER).anyTimes();
        expect(trainer.getName()).andReturn("Scoop Up").anyTimes();
        replay(trainer);

        Card e = createMock(Energy.class);
        expect(e.getCardType()).andReturn(Card.CardType.ENERGY).anyTimes();
        expect(e.getName()).andReturn("Lightning Energy").anyTimes();
        replay(e);

        Card e2 = createMock(Energy.class);
        expect(e2.getCardType()).andReturn(Card.CardType.ENERGY).anyTimes();
        expect(e2.getName()).andReturn("Psychic Energy").anyTimes();
        replay(e2);

        Deck d = new Deck();

        Card p1 = createMock(Pokemon.class);
        expect(p1.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
        expect(p1.getName()).andReturn("Charizard").anyTimes();
        replay(p1);

        Card p2 = createMock(Pokemon.class);
        expect(p2.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
        expect(p2.getName()).andReturn("Pikachu").anyTimes();
        replay(p2);

        Card p3 = createMock(Pokemon.class);
        expect(p3.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
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
        expect(p.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
        expect(p.getName()).andReturn("Pikachu").anyTimes();
        replay(p);

        assertEquals(0, d.howManyRepeats(p));

        verify(p);
    }

    @Test
    public void testHowManyRepeatsOne() {
        Deck d = new Deck();

        Card p = createMock(Pokemon.class);
        expect(p.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
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
        expect(p.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
        expect(p.getName()).andReturn("Pikachu").anyTimes();
        replay(p);

        Card p2 = createMock(Pokemon.class);
        expect(p2.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
        expect(p2.getName()).andReturn("Squirtle").anyTimes();
        replay(p2);

        Card e = createMock(Energy.class);
        expect(e.getCardType()).andReturn(Card.CardType.ENERGY).anyTimes();
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
        expect(p.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
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
        expect(e.getCardType()).andReturn(Card.CardType.ENERGY).anyTimes();
        replay(e);
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
        expect(p.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
        expect(p.getName()).andReturn("Charizard").anyTimes();
        replay(p);
        Energy e = createMock(Energy.class);
        expect(e.getCardType()).andReturn(Card.CardType.ENERGY).anyTimes();
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
    public void testForBasicPokemon() {
        Deck d = new Deck();
        Pokemon p = createMock(Pokemon.class);
        expect(p.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
        expect(p.isBasicPokemon()).andReturn(true).anyTimes();
        expect(p.getName()).andReturn("Pikachu").anyTimes();
        replay(p);

        d.addCard(p);
        d.addCard(p);
        d.addCard(p);

        assertEquals(3, d.numberBasicPokemon());
        verify(p);
    }

    @Test
    public void testNoBasicPokemon() {
        Deck d = new Deck();
        Pokemon p = createMock(Pokemon.class);
        expect(p.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
        expect(p.isBasicPokemon()).andReturn(false).anyTimes();
        expect(p.getName()).andReturn("Charizard").anyTimes();
        replay(p);

        d.addCard(p);
        d.addCard(p);
        d.addCard(p);

        assertEquals(0, d.numberBasicPokemon());
        verify(p);
    }

    @Test
    public void testMixedBasic() {
        Deck d = new Deck();
        Pokemon basic = createMock(Pokemon.class);
        expect(basic.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
        expect(basic.isBasicPokemon()).andReturn(true).anyTimes();
        expect(basic.getName()).andReturn("Pikachu").anyTimes();

        Pokemon p = createMock(Pokemon.class);
        expect(p.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
        expect(p.isBasicPokemon()).andReturn(false).anyTimes();
        expect(p.getName()).andReturn("Gyarados").anyTimes();

        replay(basic);
        replay(p);

        d.addCard(basic);
        d.addCard(basic);
        d.addCard(p);
        d.addCard(basic);
        d.addCard(p);
        d.addCard(p);

        assertEquals(3, d.numberBasicPokemon());
    }

    @Test
    public void testRemoveCard() {
        Deck d = new Deck();
        Pokemon p = createMock(Pokemon.class);
        expect(p.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
        replay(p);
        d.addCard(p);

        assertEquals(1, d.size());
        assertTrue(d.removeCard(p));
        assertEquals(0, d.size());
        verify(p);
    }

    @Test
    public void testRemoveNullCard() {
        Deck d = new Deck();
        Pokemon p = createMock(Pokemon.class);
        expect(p.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
        expect(p.getName()).andReturn("Pikachu").anyTimes();
        replay(p);

        boolean pass = false;

        try {
            d.removeCard(p);
        } catch (Deck.CardDoesNotExist e) {
            assertEquals("Card Pikachu does not exist", e.getMessage());
            pass = true;
        }
        assertEquals(0, d.size());
        assertTrue(pass);
        verify(p);
    }

    @Test
    public void testRemoveTopCardFromDeckSizeOne() {
        Deck d = new Deck();

        Card p = createMock(Pokemon.class);
        expect(p.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
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
        expect(p.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
        expect(p.getName()).andReturn("Pikachu").anyTimes();
        replay(p);

        Card e = createMock(Energy.class);
        expect(e.getCardType()).andReturn(Card.CardType.ENERGY).anyTimes();
        expect(e.getName()).andReturn("Grass Energy").anyTimes();
        replay(e);

        Card t = createMock(Trainer.class);
        expect(t.getCardType()).andReturn(Card.CardType.TRAINER).anyTimes();
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

        InputStream is = getClass().getClassLoader().getResourceAsStream("base1.json");
        if (is == null) {
            return null;
        }

        try (Scanner s = new Scanner(is, StandardCharsets.UTF_8).useDelimiter("\\A")) {
            String content = s.hasNext() ? s.next() : "";
            JSONArray pokemonArray = new JSONArray(content);
            for (int i = 0; i < pokemonArray.length(); i++) {
                String PokemonName = pokemonArray.getJSONObject(i).getString("name");
                Card card = cg.generateCard(PokemonName);
                allCards.add(card);
            }
        } catch (Exception e) {
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
        assertTrue(d.shuffle());
        ArrayList<Card> shuffledDeck = d.getCards();
        assertEquals(originalDeck.size(), shuffledDeck.size());

        Set<Card> originalSet = new HashSet<Card>(originalDeck);
        Set<Card> shuffledSet = new HashSet<Card>(shuffledDeck);
        assertEquals(originalSet, shuffledSet);

        assertNotEquals(originalDeck, shuffledDeck);
    }

    @Test
    public void testAddEmptyDeckFromFile(){
        Deck d = new DeckGenerator().generateFromFile("testDeckEmpty.txt");
        assertTrue(d.size() == 0);
    }

    @Test
    public void testAddDeckFromFileWithSize1(){
        Deck d = new DeckGenerator().generateFromFile("testDeckSize1.txt");
        assertTrue(d.size() == 1);
        ArrayList<Card> deck = d.getCards();
        Card pika = deck.get(0);
        assertTrue(pika instanceof Pokemon);
        assertTrue(pika.getName().equals("Pikachu"));
    }

    @Test
    public void testAddDeckFromFileWithMultiples(){
        Deck d = new DeckGenerator().generateFromFile("testDeckMultipleCopies.txt");
        assertTrue(d.size() == 3);
        ArrayList<Card> deck = d.getCards();
        for(int i = 0; i < deck.size(); i++){
            Card bee = deck.get(i);
            assertTrue(bee instanceof Pokemon);
            assertTrue(bee.getName().equals("Beedrill"));
        }
    }

    @Test
    public void testAddDeckFromFileWithTooManyCards(){
        try{
            new DeckGenerator().generateFromFile("testDeckWithTooManyCards.txt");
            fail("Did not throw Too Many Cards exception");
        }catch(RuntimeException e){
            assertTrue(true);
        }
    }

    @Test
    public void testAddDeckFromFileWithWrongFormatCount(){
        try{
            new DeckGenerator().generateFromFile("testDeckWithWrongFormatCount.txt");
            fail("Did not throw wrong format exception");
        }catch(RuntimeException e){
            assertEquals("File testDeckWithWrongFormatCount.txt is in the incorrect format: Wrong format!", e.getMessage());
            assertTrue(true);
        }
    }

    @Test
    public void testAddDeckNonexistentFile(){
        boolean pass = false;
        try{
            new DeckGenerator().generateFromFile("doesntExist.txt");
        }catch(RuntimeException e){
            assertTrue("Expected message containing 'not found', but got: " + e.getMessage(), 
                     e.getMessage().toLowerCase().contains("not found"));
            pass = true;
        }
        assertTrue(pass);
    }

    @Test
    public void testFalseContainsCardNamed() {
        Deck d = new Deck();
        assertTrue(d.getCardFromName("Pikachu").isNull());
    }



    @Test
    public void testTrueContainsCardNamed() {
        Deck d = new Deck();
        Pokemon p = createMock(Pokemon.class);
        expect(p.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
        expect(p.getName()).andReturn("Pikachu").anyTimes();
        expect(p.isNull()).andReturn(false).anyTimes();
        replay(p);
        boolean result = d.addCard(p);
        assertTrue(result);
        assertFalse(d.getCardFromName("Pikachu").isNull());
        verify(p);
    }

    @Test
    public void testAddCardFail() {
        Deck d = new Deck();
        ArrayList<Card> cards = createMock(ArrayList.class);
        Card card = createMock(Card.class);
        expect(card.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
        replay(card);
        Iterator<Card> iterator = createMock(Iterator.class);

        d.cards = cards;

        expect(cards.iterator()).andReturn(iterator);
        expect(iterator.hasNext()).andReturn(false);

        expect(cards.add(card)).andReturn(false);

        replay(cards, iterator);
        assertFalse(d.addCard(card));
        verify(cards, iterator);
    }


    @Test
    public void testContainsCardNameManyCards() {
        Deck d = new Deck();
        Pokemon p = createMock(Pokemon.class);
        expect(p.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
        Trainer t = createMock(Trainer.class);
        expect(t.getCardType()).andReturn(Card.CardType.TRAINER).anyTimes();
        Energy e = createMock(Energy.class);
        expect(e.getCardType()).andReturn(Card.CardType.ENERGY).anyTimes();

        expect(e.getName()).andReturn("Energy").anyTimes();
        expect(t.getName()).andReturn("Trainer").anyTimes();
        expect(p.getName()).andReturn("Pikachu").anyTimes();

        replay(e, t, p);

        d.addCard(e);
        d.addCard(e);
        d.addCard(t);
        d.addCard(t);
        d.addCard(e);
        d.addCard(e);
        d.addCard(t);
        d.addCard(p);
        d.addCard(p);
        d.addCard(p);

        assertTrue(d.getCardFromName("Charizard").isNull());
        verify(e, t, p);
    }

    @Test
    public void testTrueManyCardsContains() {
        Deck d = new Deck();
        Pokemon p = createMock(Pokemon.class);
        expect(p.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
        Pokemon p2 = createMock(Pokemon.class);
        expect(p2.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
        Trainer t = createMock(Trainer.class);
        expect(t.getCardType()).andReturn(Card.CardType.TRAINER).anyTimes();
        Energy e = createMock(Energy.class);
        expect(e.getCardType()).andReturn(Card.CardType.ENERGY).anyTimes();

        expect(e.getName()).andReturn("Energy").anyTimes();
        expect(t.getName()).andReturn("Trainer").anyTimes();
        expect(p.getName()).andReturn("Pikachu").anyTimes();
        expect(p2.getName()).andReturn("Charizard").anyTimes();
        expect(p2.isNull()).andReturn(false).anyTimes();
        replay(e, t, p, p2);

        d.addCard(e);
        d.addCard(e);
        d.addCard(t);
        d.addCard(t);
        d.addCard(e);
        d.addCard(e);
        d.addCard(t);
        d.addCard(p);
        d.addCard(p);
        d.addCard(p);
        d.addCard(p2);

        assertFalse(d.getCardFromName("Charizard").isNull());
        verify(e, t, p, p2);
    }

    @Test
    public void testGetCardFromName() {
        Deck d = new Deck();
        assertTrue(d.getCardFromName("Pikachu").isNull());
    }

    @Test
    public void testGetCardFromNameMany() {
        Deck d = new Deck();
        Pokemon p = createMock(Pokemon.class);
        expect(p.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
        Trainer t = createMock(Trainer.class);
        expect(t.getCardType()).andReturn(Card.CardType.TRAINER).anyTimes();
        Energy e = createMock(Energy.class);
        expect(e.getCardType()).andReturn(Card.CardType.ENERGY).anyTimes();

        expect(e.getName()).andReturn("Energy").anyTimes();
        expect(t.getName()).andReturn("Trainer").anyTimes();
        expect(p.getName()).andReturn("Pikachu").anyTimes();

        replay(e, t, p);

        assertTrue(d.getCardFromName("Charizard").isNull());
    }

    @Test
    public void testFindsCardFromName() {
        Deck d = new Deck();
        CardGenerator pg = new CardGenerator();
        Pokemon charizard = (Pokemon) pg.generateCard("Charizard");
        Pokemon p = createMock(Pokemon.class);
        expect(p.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
        Energy e = createMock(Energy.class);
        expect(e.getCardType()).andReturn(Card.CardType.ENERGY).anyTimes();
        Trainer t = createMock(Trainer.class);
        expect(t.getCardType()).andReturn(Card.CardType.TRAINER).anyTimes();


        expect(e.getName()).andReturn("Energy").anyTimes();
        expect(t.getName()).andReturn("Trainer").anyTimes();
        expect(p.getName()).andReturn("Pikachu").anyTimes();

        replay(e, t, p);

        d.addCard(p);
        d.addCard(e);
        d.addCard(t);
        d.addCard(e);
        d.addCard(e);
        d.addCard(e);
        d.addCard(p);
        d.addCard(t);
        d.addCard(p);
        d.addCard(charizard);

        assertEquals(charizard, d.getCardFromName("Charizard"));
        verify(e, t, p);
    }

    @Test
    public void testGetOnlyPokemonEmpty() {
        Deck d = new Deck();
        ArrayList<Card> output = d.getOnlyPokemon();
        assertEquals(0, output.size());
    }

    @Test
    public void testGetOnlyOnePokemon() {
        Deck d = new Deck();
        Pokemon p = createMock(Pokemon.class);
        expect(p.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
        replay(p);
        ArrayList<Card> expected = new ArrayList<>();
        expected.add(p);
        d.cards = expected;

        ArrayList<Card> output = d.getOnlyPokemon();
        assertEquals(expected, output);
    }

    @Test
    public void testGetOnlyPokemonNoPokemon() {
        Deck d = new Deck();
        Trainer t = createMock(Trainer.class);
        expect(t.getCardType()).andReturn(Card.CardType.TRAINER).anyTimes();
        Energy e = createMock(Energy.class);
        expect(e.getCardType()).andReturn(Card.CardType.ENERGY).anyTimes();
        replay(t, e);
        ArrayList<Card> input = new ArrayList<>();
        input.add(t);
        input.add(e);
        d.cards = input;

        ArrayList<Card> output = d.getOnlyPokemon();
        assertEquals(0, output.size());
    }

    @Test
    public void testGetOnlyPokemonMany() {
        Deck d = new Deck();
        Pokemon p = createMock(Pokemon.class);
        expect(p.getCardType()).andReturn(Card.CardType.POKEMON).anyTimes();
        Trainer t = createMock(Trainer.class);
        expect(t.getCardType()).andReturn(Card.CardType.TRAINER).anyTimes();
        Energy e = createMock(Energy.class);
        expect(e.getCardType()).andReturn(Card.CardType.ENERGY).anyTimes();
        replay(p, t, e);
        ArrayList<Card> input = new ArrayList<>();
        input.add(p);
        input.add(p);
        input.add(e);
        input.add(t);
        input.add(p);
        input.add(t);
        input.add(e);
        d.cards = input;

        ArrayList<Card> expected = new ArrayList<>();
        expected.add(p);
        expected.add(p);
        expected.add(p);

        ArrayList<Card> output = d.getOnlyPokemon();
        assertEquals(expected, output);
    }

    @Test
    public void testGetAllPlayerPokemon() {
        PlayerHandler ph = new PlayerHandler();
        Player mockPlayer = createMock(Player.class);
        Pokemon handPokemon = createMock(Pokemon.class);
        Pokemon activePokemon = createMock(Pokemon.class);
        Pokemon benchPokemon = createMock(Pokemon.class);
        ArrayList<Card> handPokemonList = new ArrayList<>();
        handPokemonList.add(handPokemon);
        ArrayList<Card> benchPokemonList = new ArrayList<>();
        benchPokemonList.add(benchPokemon);

        expect(mockPlayer.getOnlyPokemonFromHand()).andReturn(handPokemonList);
        expect(mockPlayer.getPokemonOnBench()).andReturn(benchPokemonList);

        mockPlayer.activePokemon = activePokemon;

        replay(mockPlayer);

        ph.currentPlayer = mockPlayer;
        ArrayList<Card> result = ph.getAllPlayerPokemon();

        assertEquals(3, result.size());
        assertTrue(result.contains(handPokemon));
        assertTrue(result.contains(activePokemon));
        assertTrue(result.contains(benchPokemon));

        verify(mockPlayer);
    }


    @Test
    public void testGetAllPlayerEnergy() {
        PlayerHandler ph = new PlayerHandler();
        Player mockPlayer = createMock(Player.class);
        Energy e1 = createMock(Energy.class);
        Energy e2 = createMock(Energy.class);
        ArrayList<Card> energyList = new ArrayList<>();
        energyList.add(e1);
        energyList.add(e2);

        expect(mockPlayer.getAllEnergyFromHand()).andReturn(energyList);

        replay(mockPlayer);

        ph.currentPlayer = mockPlayer;

        ArrayList<Card> result = ph.getAllPlayerEnergy();

        assertEquals(2, result.size());
        assertTrue(result.contains(e1));
        assertTrue(result.contains(e2));

        verify(mockPlayer);
    }

    @Test
    public void testGetOnlyEnergy() {
        Deck deck = new Deck();
        Energy e1 = createMock(Energy.class);
        expect(e1.getCardType()).andReturn(Card.CardType.ENERGY).anyTimes();
        Energy e2 = createMock(Energy.class);
        expect(e2.getCardType()).andReturn(Card.CardType.ENERGY).anyTimes();
        Trainer nonEnergyCard = createMock(Trainer.class);
        expect(nonEnergyCard.getCardType()).andReturn(Card.CardType.TRAINER).anyTimes();

        expect(e1.getName()).andReturn("Grass Energy").anyTimes();
        expect(e2.getName()).andReturn("Fire Energy").anyTimes();
        expect(nonEnergyCard.getName()).andReturn("Bill").anyTimes();

        replay(e1, e2, nonEnergyCard);

        deck.addCard(e1);
        deck.addCard(e2);
        deck.addCard(nonEnergyCard);

        ArrayList<Card> result = deck.getOnlyEnergy();

        assertEquals(2, result.size());
        assertTrue(result.contains(e1));
        assertTrue(result.contains(e2));
        assertFalse(result.contains(nonEnergyCard));

        verify(e1, e2, nonEnergyCard);
    }
}
