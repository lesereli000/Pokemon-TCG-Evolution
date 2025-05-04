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
}
