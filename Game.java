import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    public Game(GameGUI gui, Random rand) {
        this.gui = gui;
        this.random = rand;
        gui.addButton(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                flipCoin(random);
                setupDeck();
            }
        });
    }

    public String flipCoin(Random random) {
        String output = random.nextBoolean() ? "Heads" : "Tails";
        playerTurn = output.equals("Heads") ? 1 : 2;
        String msg = "The coin landed on " + output + ".\n" + "Player " + playerTurn + " goes first!";
        gui.displayMessage(msg);
        gui.removeButton();
        return output;
    }

    private void setupDeck() {
        //Each players deck setup happens here!
        player1Deck = new Deck();
        player1Deck.addRandomCards(60, new Random());

        player2Deck = new Deck();
        player2Deck.addRandomCards(60, new Random());
        gui.setDeckColor(Color.RED);
        String msg = "Decks have been created, it is player " + playerTurn + "'s turn";
        gui.displayMessage(msg);
        setupCards();
    }

    private void setupCards() {
        //Each players original 7 cards are setup here!
        player1Hand = new Deck();
        player2Hand = new Deck();

        for (int i = 0; i < 7; i++) {
            player1Hand.addCard(player1Deck.removeTopCard());
            player2Hand.addCard(player2Deck.removeTopCard());
        }

        String msg = "Player " + playerTurn + " goes first and has:\n";
        if(playerTurn == 1) {
            ArrayList<Card> cards = player1Hand.getCards();
            for (int i = 0; i < player1Hand.size(); i++) {
                msg += cards.get(i).getName() + "\n";
            }
        } else {
            ArrayList<Card> cards = player2Hand.getCards();
            for (int i = 0; i < player2Hand.size(); i++) {
                msg += cards.get(i).getName() + "\n";
            }
        }
        gui.displayMessage(msg);
    }

    public int currentTurn() {
        return playerTurn;
    }

    public Deck firstDeck() {
        player1Deck = new Deck();
        player1Deck.addRandomCards(60, new Random());
        return player1Deck;
    }

    public Deck secondDeck() {
        player2Deck = new Deck();
        player2Deck.addRandomCards(60, new Random());
        return player2Deck;
    }

    public Deck player1Hand() {
        return new Deck();
    }

    public Deck player2Hand() {
        return new Deck();
    }


    public static void main(String[] args) {
        new Game(new GameGUI(), new Random());
    }
}


