import java.awt.*;
import java.util.Random;

public class Game {
    private final GUI gui;
    private final Random random;

    private final Player player1;
    private final Player player2;

    private int playerTurn;
    private Player curPlayer;
    private int turn;

    public Game(GUI gui, Random rand, Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
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
        gui.displayCards(curPlayer.handAsList(), this::addBenchCard, "Add Card to Bench");
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

            if(!gameOver()) {
                mainGameLoop();
            } else {
                System.out.println("Game Over");
            }
        }
    }

    private void mainGameLoop() {
        curPlayer.drawCard();
        gui.displayCards(curPlayer.handAsList(), this::playCard, "Play Selected Card");
        gui.createButton("Retreat", this::retreatAction);
        gui.createButton("Attack", this::attack);
        gui.createButton("Pass Turn", this::passTurn);
    }

    private void playCard() {
        Card lastSelectedCard = gui.getLastSelectedCard();

        if(lastSelectedCard instanceof Pokemon) {


        } else if(lastSelectedCard instanceof Energy) {


        } else if(lastSelectedCard instanceof Trainer) {


        } else {
            gui.displayMessage(lastSelectedCard.name + " is not a basic Pokemon");
        }

        gui.removeAllButtons();
        mainGameLoop();
    }

    private void retreatAction() {
        if(curPlayer.canRetreat()){
            gui.removeAllButtons();
            gui.displayCards(curPlayer.benchAsList(), this::handleRetreat, "Select Card to Switch In");
        }else {
            gui.displayMessage("No");
        }
    }

    private void handleRetreat(){
        Card lastSelectedCard = gui.getLastSelectedCard();
        gui.makeActiveCard(lastSelectedCard, playerTurn);
        curPlayer.retreat(lastSelectedCard);
        gui.removeAllButtons();
        mainGameLoop();
    }

    private void attack(){
        // TODO: implement attacking

        passTurn();
    }

    private boolean gameOver() {
        // TODO: test win conditions here
        return false;
    }

    public static void main(String[] args) {
        Game game = new Game(new GameGUI(), new Random(), new Player("Player 1"), new Player("Player 2"));
    }
}