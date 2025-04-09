import java.util.ArrayList;
import java.util.Random;

public class Player {

    private static final int HAND_SIZE = 7;
    private Deck deck;
    private Deck hand;
    private Deck bench;
    private Deck discard;
    private Card activePokemon;

    private String name;


    public Player() {
        this("Unnamed Player");
    }

    public Player(String name) {
        this.deck = new Deck();
        this.hand = new Deck();
        this.bench = new Deck();
        this.activePokemon = null;
        this.name = name;
    }

    public void createFullDeck(Random rand) {
        deck.addRandomCards(60, rand);
        checkForBasics(rand);
    }

    public void drawCard(){
        Card drawnCard = deck.removeTopCard();
        hand.addCard(drawnCard);
    }

    public void checkForBasics(Random rand) {
        while(this.deck.numberBasicPokemon() == 0) {
            System.out.println(this.name + " does not have any basic cards!");
            this.deck = new Deck();
            this.deck.addRandomCards(60, rand);
        }
    }

    public String handAsString() {
        String msg = this.name + " has cards:\n";
        for (Card newCard : hand.getCards()) {
            String card1Class = newCard.getClass().toString();
            String justClass1 = card1Class.substring(6);
            msg += newCard.getName() + " which is a " + justClass1 + "\n";
        }
        return msg;
    }

    public ArrayList<Card> handAsList() {
        return hand.getCards();
    }

    public void setActivePokemon(Card activePokemon) {
        this.activePokemon = activePokemon;
    }

    public String getName() {
        return name;
    }

    public void drawStartingHand() {
        for (int i = 0; i < HAND_SIZE; i++) {
            drawCard();
        }
    }

    public boolean isOverHandLimit() {
        return false;
    }

    public boolean isDeckEmpty() {
        return false;
    }
}
