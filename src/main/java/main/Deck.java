package main;

import java.util.ArrayList;
import java.util.Random;

import java.util.stream.Collectors;

public class Deck {

    ArrayList<Card> cards = new ArrayList<>();

    public int size() {
        return cards.size();
    }

    public boolean addCard(Card card) {
        if (card.getCardType() != Card.CardType.ENERGY && howManyRepeats(card) > 3) {
            throw new TooManyRepeatsException("Too many repeats with card " + card.getName());
        }
        return cards.add(card);
    }

    public int howManyRepeats(Card c) {
        int repeats = 0;
        for (Card card : cards) {
            String cName = c.getName();
            String cardName = card.getName();
            if (cName.equals(cardName)) {
                repeats++;
            }
        }
        return repeats;
    }

    public ArrayList<Card> getCards() {
        return new ArrayList<>(cards);
    }

    public boolean shuffle() {
        ArrayList<Card> shuffledCards = new ArrayList<>();
        while (!cards.isEmpty()) {
            Random rand = new Random();
            int num = rand.nextInt(cards.size());
            shuffledCards.add(cards.remove(num));
        }
        cards = shuffledCards;
        return true;
    }

    public boolean removeCard(Card card) {
        if (!cards.remove(card)) {
            throw new CardDoesNotExist("Card " + card.getName() + " does not exist");
        }
        return true;
    }

    public Card removeTopCard() {
        if (cards.isEmpty()) {
            throw new EmptyDeckException("Can not remove card from an empty deck");
        }
        return cards.remove(cards.size() - 1);
    }

    public int numberBasicPokemon() {
        return (int) cards.stream().filter(Card::isBasicPokemon).count();
    }

    public ArrayList<Card> getOnlyPokemon() {
        return cards.stream()
                .filter(c -> c.getCardType() == Card.CardType.POKEMON)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Card> getOnlyEnergy() {
        return cards.stream()
                .filter(c -> c.getCardType() == Card.CardType.ENERGY)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Card getCardFromName(String cardName) {
        for (Card card : cards) {
            String currentName = card.getName();
            if (currentName.equals(cardName))
                return card;
        }
        return new NullCard();
    }

    public static class TooManyRepeatsException extends RuntimeException {
        public TooManyRepeatsException(String message) {
            super(message);
        }
    }

    public static class EmptyDeckException extends RuntimeException {
        public EmptyDeckException(String message) {
            super(message);
        }
    }

    public static class CardDoesNotExist extends RuntimeException {
        public CardDoesNotExist(String message) {
            super(message);
        }
    }

}
