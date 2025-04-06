import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Game {
    private GUI gui;
    private Random random;

    private Player player1;
    private Player player2;

    private int playerTurn;

    public Game(GUI gui, Random rand) {
        this.player1 = new Player();
        this.player2 = new Player();
        this.gui = gui;
        this.random = rand;

        // https://docs.oracle.com/javase/8/docs/api/java/lang/Runnable.html
        // https://www.geeksforgeeks.org/runnable-interface-in-java/

        gui.setFlipCoinListener(this::setupGame);
        gui.createFlipButton();
    }

    public void setupGame() {
        flipCoin();
        setupDeck();
    }

    public String flipCoin() {
        String result = random.nextBoolean() ? "Heads" : "Tails";
        this.playerTurn = result.equals("Heads") ? 1 : 2;
        gui.displayMessage("The result was " + result + "\nPlayer " + playerTurn + " turn");
        return result;
    }

    private void setupDeck() {
        //Each players deck setup happens here!
        player1.createFullDeck(random);

        player2.createFullDeck(random);

        gui.setDeckColor(Color.RED);
        setupCards();
    }

    private void setupCards() {
        //Each players original 7 cards are setup here!

        gui.setFlipCoinListener(this::setupCards);
        if(playerTurn == 1) {
            displayPlayer1Hand();
        } else if(playerTurn == 2) {
            displayPlayer2Hand();
        }
    }

    private void displayPlayer1Hand() {
        String msg = "Player 1 has cards:\n";
        ArrayList<Card> currentCards = new ArrayList<Card>();
        for (int i = 0; i < 7; i++) {
            Card player1Card = player1.removeTopCard();
            player1.addCardToHand(player1Card);
            currentCards.add(player1Card);

            String card1Class = player1Card.getClass().toString();
            String justClass1 = card1Class.substring(6);
            msg += player1Card.getName() + " which is a " + justClass1 + "\n";
        }
        gui.displayMessage(msg);
        gui.displayPossibleActiveCards(currentCards);
    }

    private void displayPlayer2Hand() {
        String msg = "Player 2 has cards:\n";
        ArrayList<Card> currentCards = new ArrayList<Card>();
        for (int i = 0; i < 7; i++) {
            Card player2Card = player2.removeTopCard();
            player2.addCardToHand(player2Card);
            currentCards.add(player2Card);

            String card2Class = player2Card.getClass().toString();
            String justClass2 = card2Class.substring(6);
            msg += player2Card.getName() + " which is a " + justClass2 + "\n";
        }
        gui.displayMessage(msg);
    }

    public int currentTurn() {
        return playerTurn;
    }


    public static void main(String[] args) {
        Game game = new Game(new GameGUI(), new Random());
    }
}


