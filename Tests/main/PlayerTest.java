package main;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class PlayerTest {

    @Test
    public void testHandNoBasics() {
        Player player = new Player();
        Deck deck = createMock(Deck.class);
        Deck hand = createMock(Deck.class);
        Card card = createMock(Card.class);

        //There are 7 cards in the hand, add 7 cards to the hand, then they should be put back in the deck
        //Shuffle should then be called one time, then drawStartingHand() will be called
        //Once again here, 7 cards should be added to the hand and I set hand.numberBasicPokemon() to return 0 so we don't get
        //stuck in an infinite loop (the first 0 returned is just for the test case)

        expect(hand.removeTopCard()).andReturn(card).times(7);
        expect(deck.addCard(card)).andReturn(true).times(7);
        expect(deck.shuffle()).andReturn(true).once();
        expect(deck.removeTopCard()).andReturn(card).times(7);
        expect(hand.numberBasicPokemon()).andReturn(0).andReturn(1).once();
        expect(hand.addCard(card)).andReturn(true).times(7);
        expect(deck.size()).andReturn(50).times(7);
        replay(deck, hand);

        assertEquals(0, hand.numberBasicPokemon());

        player.hand = hand;
        player.deck = deck;
        player.restartHand();
        verify(deck, hand);
    }

    @Test
    public void testPickupPrizeCard() {

        Deck prizeCards = createMock(Deck.class);
        Deck hand = createMock(Deck.class);
        Card card = createMock(Card.class);

        expect(prizeCards.removeTopCard()).andReturn(card).once();
        expect(hand.addCard(card)).andReturn(true).once();

        Player player = new Player();
        player.hand = hand;
        player.prizeCards = prizeCards;

        replay(prizeCards, hand);
        player.pickupPrizeCard();

        verify(prizeCards, hand);
    }

    @Test
    public void testCreateRandomDeck() {
        Player player = new Player();

        Random rand = createMock(Random.class);
        Deck deck = createMock(Deck.class);
        expect(deck.addEnergies(15, rand)).andReturn(true).anyTimes();
        expect(deck.addRandomCards(45, rand)).andReturn(true).anyTimes();
        expect(deck.shuffle()).andReturn(true).andReturn(true).anyTimes();
        expect(deck.numberBasicPokemon()).andReturn(1);
        replay(deck);

        player.deck = deck;
        player.createFullRandomDeck(rand);
        verify(deck);
    }

    @Test
    public void testDrawCard() {
        Player player = new Player();

        Deck deck = createMock(Deck.class);
        Deck hand = createMock(Deck.class);
        Card card = createMock(Card.class);

        expect(deck.removeTopCard()).andReturn(card);
        expect(hand.addCard(card)).andReturn(true);
        expect(deck.size()).andReturn(1);
        player.deck = deck;
        player.hand = hand;

        replay(deck, hand);

        assertTrue(player.drawCard());
        verify(deck, hand);
    }

    @Test
    public void testSetActivePokemonRemovesFromHand() {
        Player player = new Player();
        Deck hand = createMock(Deck.class);
        Pokemon pokemon = createMock(Pokemon.class);

        expect(hand.removeCard(pokemon)).andReturn(true).once();
        player.hand = hand;

        replay(hand);
        player.setActivePokemon(pokemon);
        assertEquals(pokemon, player.getActivePokemon());
        verify(hand);
    }

    @Test
    public void testAddBenchPokemonSuccess() {
        Player player = new Player();
        Deck bench = createMock(Deck.class);
        Card pokemon = createMock(Card.class);

        expect(bench.size()).andReturn(3);
        expect(bench.addCard(pokemon)).andReturn(true);

        player.bench = bench;

        replay(bench);
        player.addBenchPokemon(pokemon);
        verify(bench);
    }

    @Test
    public void testAddBenchPokemonBenchFull() {
        Player player = new Player("Player 1");
        Deck bench = createMock(Deck.class);
        Card pokemon = createMock(Card.class);

        expect(bench.size()).andReturn(5);

        player.bench = bench;

        replay(bench);
        boolean pass = false;
        try {
            player.addBenchPokemon(pokemon);
        } catch (InvalidMoveException e) {
            pass = true;
            assertEquals("Player 1's Bench is Full", e.getMessage());
        }
        assertTrue(pass);
        verify(bench);
    }

    @Test
    public void testPassTurnResetsCanAddEnergy() {
        Player player = new Player();
        player.passTurn();
        assertTrue(player.canAddEnergy());
    }

    @Test
    public void testPokemonDiedIncrementsCounter() {
        Player player = new Player();
        assertEquals(0, player.getNumPokemonDied());
        player.pokemonDied();
        assertEquals(1, player.getNumPokemonDied());
    }

    @Test
    public void testBenchIsEmptyTrue() {
        Player player = new Player();
        Deck bench = createMock(Deck.class);
        expect(bench.size()).andReturn(0);
        player.bench = bench;

        replay(bench);
        assertTrue(player.benchIsEmpty());
        verify(bench);
    }

    @Test
    public void testCanRetreat() {
        Player player = new Player();
        Deck bench = createMock(Deck.class);
        Pokemon active = createMock(Pokemon.class);

        expect(bench.size()).andReturn(2);
        expect(active.canRetreat()).andReturn(true);

        player.bench = bench;
        player.activePokemon = active;

        replay(bench, active);
        assertTrue(player.canRetreat());
        verify(bench, active);
    }

    @Test
    public void testEvolvePokemonSuccess() {
        Player player = new Player();
        Deck bench = createMock(Deck.class);
        Pokemon oldPokemon = createMock(Pokemon.class);
        Pokemon evolvedPokemon = createMock(Pokemon.class);

        expect(evolvedPokemon.getEvolvesFrom()).andReturn("Charmander");
        expect(bench.containsCardNamed("Charmander")).andReturn(true);
        expect(bench.getCardFromName("Charmander")).andReturn(oldPokemon);

        player.bench = bench;

        replay(bench, evolvedPokemon);

        assertTrue(player.evolvePokemon(evolvedPokemon));
        verify(bench, evolvedPokemon);
    }

    @Test
    public void testGetOnlyPokemonFromHand() {
        Player player = new Player();
        Deck hand = createMock(Deck.class);
        expect(hand.getOnlyPokemon()).andReturn(null);
        player.hand = hand;
        replay(hand);

        player.getOnlyPokemonFromHand();
        verify(hand);
    }

    @Test
    public void testRemoveEnergy() {
        Player player = new Player();
        ArrayList<Energy> energyList = new ArrayList<>();
        Energy e = new Energy("Grass Energy");
        energyList.add(e);
        Pokemon activePokemon = new Pokemon("Beedrill", "Grass", 2, 80, "Fire", "Fighting", null, 0);
        player.hand.addCard(activePokemon);
        player.setActivePokemon(activePokemon);
        activePokemon.addEnergy(e);
        assertEquals(activePokemon.energies, energyList);
        player.removeEnergy(energyList);
        energyList.remove(0);
        assertEquals(activePokemon.energies, energyList);
    }
}
