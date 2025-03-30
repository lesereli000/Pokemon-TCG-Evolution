import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Game {
    private GUI gui;
    private Random random;

    private Deck player1Deck;
    private Deck player2Deck;

    private Deck player1Hand;
    private Deck player2Hand;

    private int playerTurn;

    public Game(GUI gui, Random rand) {
        this.gui = gui;
        this.random = rand;
        setupDeck();
        gui.createFlipButton(flipCoin());
    }

    public String flipCoin() {
        String result = random.nextBoolean() ? "Heads" : "Tails";
        this.playerTurn = result.equals("Heads") ? 1 : 2;
        return result;
    }

    private void setupDeck() {
        //Each players deck setup happens here!
        player1Deck = new Deck();
        player1Deck.addRandomCards(60, random);

        player2Deck = new Deck();
        player2Deck.addRandomCards(60, random);

        gui.setDeckColor(Color.RED);
        setupCards();
    }

    private void setupCards() {
        //Each players original 7 cards are setup here!
        player1Hand = new Deck();
        player2Hand = new Deck();

        String msg1 = "Player 1 has cards:\n";
        String msg2 = "Player 2 has cards:\n";
        for (int i = 0; i < 7; i++) {
            Card player1Card = player1Deck.removeTopCard();
            player1Hand.addCard(player1Card);
            String card1Class = player1Card.getClass().toString();
            String justClass1 = card1Class.substring(6);
            msg1 += player1Card.getName() + " which is a " + justClass1 + "\n";

            Card player2Card = player2Deck.removeTopCard();
            player2Hand.addCard(player2Card);
            String card2Class = player2Card.getClass().toString();
            String justClass2 = card2Class.substring(6);
            msg2 += player2Card.getName() + " which is a " + justClass2 + "\n";
        }
        gui.displayMessage(msg1 + "\n\n" + msg2);
    }

    public int currentTurn() {
        return playerTurn;
    }


    public static void main(String[] args) {
        new Game(new GameGUI(), new Random());
    }
}


