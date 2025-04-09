import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Game {
    private GUI gui;
    private Random random;

    private Player player1;
    private Player player2;

    private int playerTurn;
    Player curPlayer;
    private boolean addedBenchButton = false;

    public Game(GUI gui, Random rand) {
        this.player1 = new Player("Player 1");
        this.player2 = new Player("Player 2");
        this.gui = gui;
        this.random = rand;

        // https://docs.oracle.com/javase/8/docs/api/java/lang/Runnable.html
        // https://www.geeksforgeeks.org/runnable-interface-in-java/

        gui.createFlipButton(this::setupGame);
    }

    public void setupGame() {
        String result = flipCoin();
        playerTurn = result.equals("Heads") ? 1 : 2;
        curPlayer = playerTurn == 1 ? player1 : player2;
        gui.displayMessage("The result was " + result + "! \n" + curPlayer.getName() + "'s turn");

        setupDecks();
    }

    public String flipCoin() {
        return random.nextBoolean() ? "Heads" : "Tails";
    }

    private void setupDecks() {
        //Each players deck setup happens here!
        player1.createFullDeck(random);
        player1.checkForBasics(random);

        player2.createFullDeck(random);
        player2.checkForBasics(random);

        gui.setDeckColor(Color.RED);
        drawPlayerHands();
    }

    private void drawPlayerHands() {
        //Each players original 7 cards are setup here!

        if(playerTurn == 1) {
            gui.displayMessage(player1.handAsString());
            gui.displayPossibleActiveCards(player1.handAsList(), this::makeActiveCard);
        } else if(playerTurn == 2) {
            gui.displayMessage(player2.handAsString());
            gui.displayPossibleActiveCards(player2.handAsList(), this::makeActiveCard);
        }
    }

    private void makeActiveCard() {
        Card lastSelectedCard = gui.getLastSelectedCard();
        if(lastSelectedCard instanceof Pokemon && ((Pokemon) lastSelectedCard).stage == 0) {
            gui.makeActiveCard(lastSelectedCard, playerTurn);
            curPlayer.setActivePokemon(lastSelectedCard);
            displayPickBenchCardsButton();
        } else {
            gui.displayMessage(lastSelectedCard.name + " is not a basic Pokemon");
        }
    }


    private void addBenchCard() {
        //Do logic with adding the bench cards here
        //Maybe add another button to switch back to switching out the active Pokemon
        //Any other basic Pokemon clicked from here can be added to the bench
    }

    private void displayPickBenchCardsButton() {
        if(!addedBenchButton) {
            addedBenchButton = true;
            gui.createButton("Add Bench Cards", this::addBenchCard);
        }
    }


    public static void main(String[] args) {
        Game game = new Game(new GameGUI(), new Random());
    }
}