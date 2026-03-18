package main;

import org.junit.Test;

import java.util.ArrayList;

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
    public void testBenchIsEmptyFalse() {
        Player player = new Player();
        Deck bench = createMock(Deck.class);
        expect(bench.size()).andReturn(1);
        player.bench = bench;

        replay(bench);
        assertFalse(player.benchIsEmpty());
        verify(bench);
    }

    @Test
    public void testGetOnlyPokemonFromHand() {
        Player player = new Player();
        Deck hand = createMock(Deck.class);
        ArrayList<Card> cards = createMock(ArrayList.class);
        expect(hand.getOnlyPokemon()).andReturn(cards);
        player.hand = hand;
        replay(hand);

        ArrayList<Card> result = player.getOnlyPokemonFromHand();
        assertEquals(result, cards);
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
    public void testEmptyStartingHand() {
        Deck hand = createMock(Deck.class);
        Deck deck = createMock(Deck.class);
        Card card = createMock(Card.class);

        expect(hand.removeTopCard()).andReturn(card).times(7);
        expect(deck.addCard(card)).andReturn(true).times(7);

        expect(deck.size()).andReturn(0).anyTimes();
        expect(hand.numberBasicPokemon()).andReturn(0);

        expect(deck.shuffle()).andReturn(true);

        //draw again
        expect(hand.numberBasicPokemon()).andReturn(1);

        replay(deck, hand);
        Player p = new Player();
        p.hand = hand;
        p.deck = deck;
        p.drawStartingHand();
        verify(deck, hand);
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
    public void testCantAttack() {
        Player player = new Player();
        Pokemon active = createMock(Pokemon.class);
        expect(active.canAttack()).andReturn(false);
        player.activePokemon = active;

        replay(active);
        assertFalse(player.canAttack());
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
    public void testCantAttackWithArgs() {
        Player player = new Player();
        Pokemon active = createMock(Pokemon.class);
        Attack attack = createMock(Attack.class);

        expect(active.canAttack(attack)).andReturn(true);
        player.activePokemon = active;

        replay(active);
        assertTrue(player.canAttack(attack));
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

    @Test
    public void testDrawPrizeCards() {
        Deck deck = createMock(Deck.class);
        Deck prizeCards = createMock(Deck.class);
        Card card = createMock(Card.class);

        expect(deck.removeTopCard()).andReturn(card).times(6);
        expect(prizeCards.addCard(card)).andReturn(true).times(6);

        replay(deck, prizeCards);

        Player p = new Player();
        p.prizeCards = prizeCards;
        p.deck = deck;
        p.drawPrizeCards();

        verify(deck, prizeCards);
    }

    @Test
    public void testGetNumPrizeCards() {
        Player player = new Player();
        Deck prizeDeck = createMock(Deck.class);
        expect(prizeDeck.size()).andReturn(3).once();

        player.prizeCards = prizeDeck;

        replay(prizeDeck);
        assertEquals(3, player.getNumPrizeCards());
        verify(prizeDeck);
    }

    @Test
    public void testEvolvePokemonActive() {
        Pokemon p1 = createMock(Pokemon.class);
        Pokemon p2 = createMock(Pokemon.class);
        Deck hand = createMock(Deck.class);

        expect(p2.getDamageCounters()).andReturn(0);
        p1.takeDamage(0, "");
        expect(p2.getEnergies()).andReturn(new ArrayList<Energy>());
        p1.addEnergies(anyObject());

        expect(hand.removeCard(p1)).andReturn(true);

        replay(p1, p2, hand);
        Player player = new Player();
        player.activePokemon = p2;
        player.hand = hand;
        assertEquals("Active", player.evolvePokemon(p1, p2));
        verify(p1, p2, hand);
    }

    @Test
    public void testEvolvePokemonBench() {
        Pokemon p1 = createMock(Pokemon.class);
        Pokemon p2 = createMock(Pokemon.class);
        Pokemon active = createMock(Pokemon.class);
        Deck hand = createMock(Deck.class);
        Deck bench = createMock(Deck.class);

        expect(p2.getDamageCounters()).andReturn(0);
        p1.takeDamage(0, "");
        expect(p2.getEnergies()).andReturn(new ArrayList<Energy>());
        p1.addEnergies(anyObject());

        expect(hand.removeCard(p1)).andReturn(true);
        expect(bench.removeCard(p2)).andReturn(true);
        expect(bench.addCard(p1)).andReturn(true);

        replay(p1, p2, hand, bench);
        Player player = new Player();
        player.activePokemon = active;
        player.hand = hand;
        player.bench = bench;
        assertEquals("Bench", player.evolvePokemon(p1, p2));
        verify(p1, p2, hand, bench);
    }

    @Test
    public void testPreEvsEmpty() {
        Pokemon p = createMock(Pokemon.class);
        Pokemon active = createMock(Pokemon.class);
        Deck bench = createMock(Deck.class);
        ArrayList<Card> cards = new ArrayList<>();

        expect(active.getName()).andReturn("Charizard");
        expect(p.getEvolvesFrom()).andReturn("Pikachu");
        expect(bench.getOnlyPokemon()).andReturn(cards);

        replay(active, p, bench);

        Player player = new Player();
        player.activePokemon = active;
        player.bench = bench;
        ArrayList<Card> result = player.getPreEvolutions(p);
        assertEquals(cards, result);

        verify(active, p, bench);
    }

    @Test
    public void testPreEvsActive() {
        Pokemon p = createMock(Pokemon.class);
        Pokemon active = createMock(Pokemon.class);
        Deck bench = createMock(Deck.class);
        ArrayList<Card> cards = new ArrayList<>();

        expect(active.getName()).andReturn("Pikachu");
        expect(p.getEvolvesFrom()).andReturn("Pikachu");
        expect(bench.getOnlyPokemon()).andReturn(cards);

        replay(active, p, bench);

        Player player = new Player();
        player.activePokemon = active;
        player.bench = bench;
        player.getPreEvolutions(p);

        verify(active, p, bench);
    }

    @Test
    public void testPreEvsActiveNotOnBench() {
        Pokemon p = createMock(Pokemon.class);
        Pokemon p2 = createMock(Pokemon.class);
        Pokemon active = createMock(Pokemon.class);
        Deck bench = createMock(Deck.class);
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(p2);

        expect(active.getName()).andReturn("Bulbasaur");
        expect(p.getEvolvesFrom()).andReturn("Pikachu");
        expect(p2.getName()).andReturn("Magikarp");
        expect(bench.getOnlyPokemon()).andReturn(cards);

        replay(active, p, p2, bench);

        Player player = new Player();
        player.activePokemon = active;
        player.bench = bench;
        ArrayList<Card> result = player.getPreEvolutions(p);
        assertTrue(result.isEmpty());

        verify(active, p, p2, bench);
    }

    @Test
    public void testPreEvsManyPokemon() {
        Pokemon p = createMock(Pokemon.class);
        Pokemon p2 = createMock(Pokemon.class);
        Pokemon p3 = createMock(Pokemon.class);
        Pokemon p4 = createMock(Pokemon.class);
        Pokemon p5 = createMock(Pokemon.class);
        Pokemon active = createMock(Pokemon.class);
        Deck bench = createMock(Deck.class);
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(p2);
        cards.add(p3);
        cards.add(p4);
        cards.add(p5);

        expect(active.getName()).andReturn("Charizard");
        expect(p.getEvolvesFrom()).andReturn("Pikachu");
        expect(bench.getOnlyPokemon()).andReturn(cards);
        expect(p2.getName()).andReturn("Pikachu");
        expect(p3.getName()).andReturn("Pikachu");
        expect(p4.getName()).andReturn("Pikachu");
        expect(p5.getName()).andReturn("Pikachu");

        replay(active, p, p2, p3, p4, p5, bench);

        Player player = new Player();
        player.activePokemon = active;
        player.bench = bench;
        player.getPreEvolutions(p);

        verify(active, p, p2, p3, p4, p5, bench);
    }

    @Test
    public void testEvolveError() {

        Pokemon p1 = createMock(Pokemon.class);
        Pokemon p2 = createMock(Pokemon.class);
        Deck hand = createMock(Deck.class);
        Deck bench = createMock(Deck.class);
        InvalidMoveException e = createMock(InvalidMoveException.class);

        expect(p2.getDamageCounters()).andReturn(3);
        p1.takeDamage(3, "");
        expect(p2.getEnergies()).andReturn(new ArrayList<Energy>());
        p1.addEnergies(anyObject());

        expect(hand.removeCard(p1)).andReturn(true);
        expect(bench.removeCard(p2)).andThrow(e);

        replay(p1, p2, hand, bench);

        Player p = new Player();
        p.hand = hand;
        p.bench = bench;
        assertEquals("Error", p.evolvePokemon(p1, p2));

        verify(p1, p2, hand, bench);
    }

    @Test
    public void testGetPokemonOnBench() {
        Player player = new Player("Test Player");
        Pokemon benchPokemon1 = createMock(Pokemon.class);
        Pokemon benchPokemon2 = createMock(Pokemon.class);
        Deck bench = createMock(Deck.class);
        ArrayList<Card> benchCards = new ArrayList<>();
        benchCards.add(benchPokemon1);
        benchCards.add(benchPokemon2);

        expect(bench.getCards()).andReturn(benchCards);

        replay(bench);

        player.bench = bench;
        ArrayList<Card> result = player.getPokemonOnBench();

        assertEquals(2, result.size());
        assertTrue(result.contains(benchPokemon1));
        assertTrue(result.contains(benchPokemon2));

        verify(bench);
    }

    @Test
    public void testGetAllEnergyFromHand() {
        Player player = new Player("Test Player");
        Energy energyCard1 = createMock(Energy.class);
        Energy energyCard2 = createMock(Energy.class);
        Deck hand = createMock(Deck.class);
        ArrayList<Card> energy = new ArrayList<>();
        energy.add(energyCard1);
        energy.add(energyCard2);

        expect(hand.getOnlyEnergy()).andReturn(energy);

        replay(hand);

        player.hand = hand;
        ArrayList<Card> result = player.getAllEnergyFromHand();

        assertEquals(2, result.size());
        assertTrue(result.contains(energyCard1));
        assertTrue(result.contains(energyCard2));

        verify(hand);
    }

}
