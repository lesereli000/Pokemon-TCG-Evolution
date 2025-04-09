import java.util.ArrayList;
import java.util.Random;

public class Player {

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
    }

    public Card drawCard(){
        Card drawnCard = deck.removeTopCard();
        hand.addCard(drawnCard);
        return drawnCard;
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
        for (int i = 0; i < 7; i++) {
            Card newCard = drawCard();

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
}
