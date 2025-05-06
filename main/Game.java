package main;

import java.util.ArrayList;
import java.util.Random;

public class Game {
    protected GUI gui;
    protected Random random;
    protected Player player1;
    protected Player player2;
    protected Player currentPlayer;
    protected Player defendingPlayer;

    public Game(GUI gui) {
        this.gui = gui;
        this.random = new Random();
        gui.createGUI();
    }

    protected void setupGame() {
        setupFlipButton();
        String coinFlipResult = flipCoin(random);
        createPlayers();
        setPlayerTurns(coinFlipResult);
        setupBothDecks();
        setupBothHands();
        selectActiveLoop();
    }

    private void selectActiveLoop() {
        displayActiveDirections();
        Card selectedCard = displayCurrentPlayerHand();
        if(checkBasicPokemon(selectedCard)) {
            makeNewActivePokemon((Pokemon) selectedCard);
        } else {
            gui.displayMessage("Not a basic Pokemon!");
            selectActiveLoop();
        }
    }

    protected void setupFlipButton() {
        gui.createFlipButton();
    }

    protected void setPlayerTurns(String coinFlipResult) {
        currentPlayer = coinFlipResult.equals("Heads") ? player1 : player2;
        defendingPlayer = coinFlipResult.equals("Heads") ? player2 : player1;
    }

    protected String flipCoin(Random rand) {
        boolean randomBoolean = rand.nextBoolean();
        if(randomBoolean) {
            return "Heads";
        }
        return "Tails";
    }

    protected void createPlayers() {
        player1 = new Player("Player 1");
        player2 = new Player("Player 2");
    }

    protected void setupBothDecks() {
        player1.createCustomDeck();
        player2.createCustomDeck();
    }

    protected void setupBothHands() {
        player1.drawStartingHand();
        player2.drawStartingHand();
    }

    protected Card displayCurrentPlayerHand() {
        ArrayList<Card> playerCards = currentPlayer.handAsList();
        return gui.displayCards(playerCards);
    }

    public void displayActiveDirections() {
        gui.displayMessage("Select a basic Pokemon to be your Active Pokemon");
    }

    public void makeNewActivePokemon(Pokemon p) {
        currentPlayer.setActivePokemon(p);
    }

    public boolean checkBasicPokemon(Card card) {
        if(!(card instanceof Pokemon)) return false;
        Pokemon pokemon = (Pokemon) card;
        pokemon.getStage();
        return false;
    }

    public static void main(String[] args) {
        GUI gui = new GameGUI();
        Game game = new Game(gui);
        game.setupGame();
    }


}
