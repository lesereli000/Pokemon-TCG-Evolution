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
    protected int curTurn;
    protected SetupGame gameSetup;

    public Game(GUI gui, Random random, SetupGame gameSetup) {
        this.gui = gui;
        this.random = random;
        this.gameSetup = gameSetup;
    }

    protected void setupGame() {
        setupFlipButton();
        createPlayers();
        String coinFlipResult = gameSetup.completeGameSetup();
        setPlayerTurns(coinFlipResult);
        setupBothDecks();
        setupBothHands();
        selectActiveLoop();
    }

    protected void selectActiveLoop() {
        displayActiveDirections();
        Card selectedCard = displayCurrentPlayerHand();
        if(checkBasicPokemon(selectedCard)) {
            makeNewActivePokemon((Pokemon) selectedCard);
            displayCurrentPlayerHand();
        } else {
            gui.displayMessage("Not a basic Pokemon!");
            gui.removeAllButtons();
            selectActiveLoop();
        }
    }

    protected void setupFlipButton() {
        gui.createFlipButton();
    }

    protected void setPlayerTurns(String coinFlipResult) {
        currentPlayer = coinFlipResult.equals("Heads") ? player1 : player2;
        defendingPlayer = coinFlipResult.equals("Heads") ? player2 : player1;
        curTurn = coinFlipResult.equals("Heads") ? 1 : 2;
        gui.displayMessage("The result was " + coinFlipResult + " " + currentPlayer.getName() + " goes first!");
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
        gui.removeAllButtons();
        ArrayList<Card> playerCards = currentPlayer.handAsList();
        return gui.displayCards(playerCards);
    }

    public void displayActiveDirections() {
        gui.displayMessage("Select a basic Pokemon to be your Active Pokemon");
    }

    public void makeNewActivePokemon(Pokemon p) {
        currentPlayer.setActivePokemon(p);
        gui.makeActiveCard(p,curTurn);
//        gui.waitForPassTurn();
        //TODO add Pass Turn
        //passTurn();
    }

    public boolean checkBasicPokemon(Card card) {
        if(!(card instanceof Pokemon pokemon)) return false;
        int stage = pokemon.getStage();
        return stage == 0;
    }

    public void selectCard(Card selectedCard) {
        if(selectedCard instanceof Pokemon) {
            handlePokemon((Pokemon) selectedCard);
        } else if (selectedCard instanceof Energy){
            handleAddEnergy((Energy) selectedCard);
        } else {
            gui.displayMessage("Can not handle that card right now");
        }
    }

    private void handlePokemon(Pokemon selectedPokemon) {
        int pokemonStage = selectedPokemon.getStage();
        if(pokemonStage == 0) {
            currentPlayer.addBenchPokemon(selectedPokemon);
        } else {
            gui.displayMessage("This is not a basic Pokemon and can not place card on bench!");
        }
    }

    private void handleAddEnergy(Energy energy) {
        if(!currentPlayer.canAddEnergy()) {
            gui.displayMessage("Can only add one energy per turn!");
        } else {
            ArrayList<Card> onlyPokemon = currentPlayer.getOnlyPokemonFromHand();
            gui.displayCards(onlyPokemon);
            gui.displayMessage("Select Pokemon to add card to");
        }
    }


    public static void main(String[] args) {
        GUI gui = new GameGUI();
        Random random = new Random();
        SetupGame gameSetup = new SetupGame(random);
        Game game = new Game(gui, random, gameSetup);
        gui.createGUI();
        game.setupGame();
    }
}
