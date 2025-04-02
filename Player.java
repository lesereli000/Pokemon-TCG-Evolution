import java.util.Random;

public class Player {

    public Deck deck;
    public Deck hand;
    public Deck bench;
    public Card activePokemon;

    public Player() {
        deck = new Deck();
        hand = new Deck();
        bench = new Deck();
        activePokemon = null;
    }

    public void createFullDeck(Random rand) {
        deck.addRandomCards(60, rand);
    }

    public Card removeTopCard() {
        return deck.removeTopCard();
    }

    public void addCardToHand(Card player1Card) {
        hand.addCard(player1Card);
    }
}
