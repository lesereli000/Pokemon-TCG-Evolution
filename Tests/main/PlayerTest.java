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

//    @Test
//    public void testCreateRandomDeck() {
//        Player player = new Player();
//
//        Random rand = createMock(Random.class);
//        Deck deck = createMock(Deck.class);
//        expect(deck.addEnergies(15, rand)).andReturn(true).anyTimes();
//        expect(deck.addRandomCards(45, rand)).andReturn(true).anyTimes();
//        expect(deck.shuffle()).andReturn(true).andReturn(true).anyTimes();
//        expect(deck.numberBasicPokemon()).andReturn(1);
//        replay(deck);
//
//        player.deck = deck;
//        player.createFullRandomDeck(rand);
//        verify(deck);
//    }

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

//    @Test
//    public void testCanRetreat() {
//        Player player = new Player();
//        Deck bench = createMock(Deck.class);
//        Pokemon active = createMock(Pokemon.class);
//
//        expect(bench.size()).andReturn(2);
//        expect(active.canRetreat()).andReturn(true);
//
//        player.bench = bench;
//        player.activePokemon = active;
//
//        replay(bench, active);
//        assertTrue(player.canRetreat());
//        verify(bench, active);
//    }

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
    public void testTakeDamage() {
        Player player = new Player();
        Pokemon active = createMock(Pokemon.class);
        active.takeDamage(30, "Fire");
        player.activePokemon = active;

        replay(active);
        player.takeDamage(30, "Fire");
        verify(active);
    }

    @Test
    public void testAddEnergyToPokemon() {
        Player player = new Player();
        Deck hand = createMock(Deck.class);
        Energy energy = createMock(Energy.class);
        Pokemon pokemon = createMock(Pokemon.class);

        expect(hand.removeCard(energy)).andReturn(true);
        pokemon.addEnergy(energy);

        player.hand = hand;

        replay(hand, pokemon);
        player.addEnergyToPokemon(pokemon, energy);
        assertFalse(player.canAddEnergy());
        verify(hand, pokemon);
    }


    @Test
    public void testGetActiveHP() {
        Player player = new Player();
        Pokemon active = createMock(Pokemon.class);

        expect(active.getCurHP()).andReturn(40);
        player.activePokemon = active;

        replay(active);
        assertEquals(40, player.getActiveHP());
        verify(active);
    }

    @Test
    public void testCanAttackNoArgs() {
        Player player = new Player();
        Pokemon active = createMock(Pokemon.class);
        expect(active.canAttack()).andReturn(true);
        player.activePokemon = active;

        replay(active);
        assertTrue(player.canAttack());
        verify(active);
    }

    @Test
    public void testCanAttackWithArgs() {
        Player player = new Player();
        Pokemon active = createMock(Pokemon.class);
        Attack attack = createMock(Attack.class);

        expect(active.canAttack(attack)).andReturn(false);
        player.activePokemon = active;

        replay(active);
        assertFalse(player.canAttack(attack));
        verify(active);
    }

    @Test
    public void testRestartHandWithBasicsFirstTry() {
        Player player = new Player();
        Deck deck = createMock(Deck.class);
        Deck hand = createMock(Deck.class);
        Card card = createMock(Card.class);

        expect(deck.size()).andReturn(50).times(7);
        expect(deck.removeTopCard()).andReturn(card).times(7);
        expect(hand.addCard(card)).andReturn(true).times(7);
        expect(hand.numberBasicPokemon()).andReturn(2).once();

        player.deck = deck;
        player.hand = hand;

        replay(deck, hand);
        player.drawStartingHand();
        verify(deck, hand);
    }

    @Test
    public void testSetNewActivePokemon() {
        Player player = new Player();
        Deck bench = createMock(Deck.class);
        Pokemon newActive = createMock(Pokemon.class);

        expect(bench.removeCard(newActive)).andReturn(true);
        player.bench = bench;

        replay(bench);
        player.setNewActivePokemon(newActive);
        assertEquals(newActive, player.getActivePokemon());
        verify(bench);
    }

    @Test
    public void testHasActive() {
        Player player = new Player();
        assertFalse(player.hasActive());
        Pokemon pokemon = createMock(Pokemon.class);
        Deck hand = createMock(Deck.class);
        expect(hand.removeCard(pokemon)).andReturn(true);
        player.hand = hand;

        replay(hand);
        player.setActivePokemon(pokemon);
        assertTrue(player.hasActive());
        verify(hand);
    }

    @Test
    public void testGetNameAndBench() {
        Player player = new Player("Player 1");
        Deck bench = createMock(Deck.class);
        player.bench = bench;

        assertEquals("Player 1", player.getName());
        assertEquals(bench, player.getBench());
    }

    @Test
    public void testEvolvePokemonFail() {
        Player player = new Player();
        Deck bench = createMock(Deck.class);
        Pokemon evolved = createMock(Pokemon.class);

        expect(evolved.getEvolvesFrom()).andReturn("Pikachu");
        expect(bench.containsCardNamed("Pikachu")).andReturn(false);

        player.bench = bench;

        replay(bench, evolved);
        assertFalse(player.evolvePokemon(evolved));
        verify(bench, evolved);
    }

    @Test
    public void testDrawCardFailsWhenDeckEmpty() {
        Player player = new Player();
        Deck deck = createMock(Deck.class);

        expect(deck.size()).andReturn(0);

        player.deck = deck;

        replay(deck);
        assertFalse(player.drawCard());
        verify(deck);
    }

    @Test
    public void testRetreat() {
        Player player = new Player();
        Deck bench = new Deck();
        Pokemon active = createMock(Pokemon.class);
        Pokemon retreatTarget = createMock(Pokemon.class);

        bench.addCard(retreatTarget);
        player.activePokemon = active;
        player.bench = bench;

        player.retreat(retreatTarget);

        assertEquals(retreatTarget, player.getActivePokemon());
        assertTrue(player.getBench().getCards().contains(active));
        assertFalse(player.getBench().getCards().contains(retreatTarget));
    }

    @Test
    public void testHandAsList() {
        Player player = new Player();
        Deck hand = new Deck();
        ArrayList<Card> cards = new ArrayList<>();
        Pokemon p = new Pokemon("Beedrill", "Grass", 2, 80, "Fire", "Fighting", null, 0);

        player.hand.addCard(p);
        cards.add(p);

        assertEquals(player.handAsList(), cards);
    }

}
