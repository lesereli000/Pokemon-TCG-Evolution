import java.awt.*;
import java.util.Random;

public class Game {
    private GUI gui;
    private Random random;

    private Player player1;
    private Player player2;

    private int playerTurn;
    private Player curPlayer;
    private int turn;

    public Game(GUI gui, Random rand) {
        this.player1 = new Player("Player 1");
        this.player2 = new Player("Player 2");
        this.gui = gui;
        this.random = rand;
        this.turn = 1;

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
        setCurPlayerPokemon();
    }

    public String flipCoin() {
        return random.nextBoolean() ? "Heads" : "Tails";
    }

    private void setupDecks() {
        player1.createFullDeck(random);
        player1.drawStartingHand();

        player2.createFullDeck(random);
        player2.drawStartingHand();
    }

    private void setCurPlayerPokemon() {
        gui.removeAllButtons();
        gui.displayMessage(curPlayer.handAsString());
        gui.displayCards(curPlayer.handAsList(), this::makeActiveCard, "Select Active Pokemon");
    }

    private void makeActiveCard() {
        Card lastSelectedCard = gui.getLastSelectedCard();
        if(lastSelectedCard instanceof Pokemon && ((Pokemon) lastSelectedCard).stage == 0) {
            gui.makeActiveCard(lastSelectedCard, playerTurn);
            curPlayer.setActivePokemon(lastSelectedCard);
            gui.removeAllButtons();
            displayPickBenchCardsButton();
        } else {
            gui.displayMessage(lastSelectedCard.name + " is not a basic Pokemon");
        }
    }

    private void displayPickBenchCardsButton() {
        gui.displayCards(curPlayer.handAsList(), this::addBenchCard, "Add Bench Cards");
        gui.createButton("Pass Turn", this::passTurn);
    }

    private void addBenchCard() {
        Card lastSelectedCard = gui.getLastSelectedCard();
        if(lastSelectedCard instanceof Pokemon && ((Pokemon) lastSelectedCard).stage == 0) {
            gui.addBenchCard(lastSelectedCard, playerTurn);
            curPlayer.addBenchPokemon(lastSelectedCard);
            curPlayer.removeFromHand(lastSelectedCard);
        } else {
            gui.displayMessage(lastSelectedCard.name + " is not a basic Pokemon");
        }
        gui.removeAllButtons();
        displayPickBenchCardsButton();
    }

    private void passTurn(){
        playerTurn = playerTurn%2 + 1;
        curPlayer = playerTurn == 1 ? player1 : player2;
        turn++;
        if(turn == 2){
            setCurPlayerPokemon();
        } else {
            gui.removeAllButtons();
            mainGameLoop();
        }
    }

    private void mainGameLoop() {
        // TODO: Turn 1 actions go here
    }

    public static void main(String[] args) {
        Game game = new Game(new GameGUI(), new Random());
    }
}