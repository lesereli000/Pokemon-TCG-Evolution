package main;

import java.util.ArrayList;
import java.util.Random;

public class Game {
    protected GUI gui;
    protected Random random;
    protected SetupGame gameSetup;
    protected PlayerHandler playerHandler;
    protected CardManager cardManager;

    public Game(GUI gui, Random random, SetupGame gameSetup, PlayerHandler playerHandler, CardManager cardManager) {
        this.gui = gui;
        this.random = random;
        this.gameSetup = gameSetup;
        this.playerHandler = playerHandler;
        this.cardManager = cardManager;
    }

    protected void setupGame() {
        setupFlipButton();
        String coinFlipResult = gameSetup.completeGameSetup();
        playerHandler.completePlayerSetup(coinFlipResult);
        Player currentPlayer = playerHandler.getCurrentPlayer();
        displaySetupResults(coinFlipResult, currentPlayer);
        selectActiveLoop();
    }

    private void displaySetupResults(String coinFlipResult, Player currentPlayer) {
        gui.displayMessage("The result was " + coinFlipResult + " " + currentPlayer.getName() + " goes first!");
    }

    protected void selectActiveLoop() {
        displayActiveDirections();
        Card selectedCard = displayCurrentPlayerHand();
        if(checkBasicPokemon(selectedCard)) {
            makeNewActivePokemon((Pokemon) selectedCard);
            displayCurrentPlayerHand();
            //mainGameLoop();
        } else {
            gui.displayMessage("Not a basic Pokemon!");
            gui.removeAllButtons();
            selectActiveLoop();
        }
    }

    protected void mainGameLoop() {
        String action = gui.waitForButtonPressed();
        if(action.equals("AddToBench")) {
            handleBenchAction();
        }
    }

    protected void handleBenchAction() {
        Card lastSelectedCard = gui.getLastSelectedCard();
        if(!(lastSelectedCard instanceof Pokemon)) {
            gui.displayMessage("Pokemon has not been selected!");
        } else {
            handleAddToBench((Pokemon)lastSelectedCard);
        }
    }

    protected void handleEnergyAction() {
        Card lastSelectedCard = gui.getLastSelectedCard();
        if(!(lastSelectedCard instanceof Energy)) {
            gui.displayMessage("Energy has not been selected!");
        } else {
            handleAddEnergy((Energy)lastSelectedCard);
        }
    }

    protected void setupFlipButton() {
        gui.createFlipButton();
    }

    protected Card displayCurrentPlayerHand() {
        gui.removeAllButtons();
        Player currentPlayer = playerHandler.getCurrentPlayer();
        ArrayList<Card> playerCards = currentPlayer.handAsList();
        return gui.displayCards(playerCards);
    }

    public void displayActiveDirections() {
        gui.displayMessage("Select a basic Pokemon to be your Active Pokemon");
    }

    public void makeNewActivePokemon(Pokemon p) {
        Player currentPlayer = playerHandler.getCurrentPlayer();
        int curTurn = playerHandler.getPlayerTurn();
        currentPlayer.setActivePokemon(p);
        gui.makeActiveCard(p,curTurn);
    }

    public boolean checkBasicPokemon(Card card) {
        if(!(card instanceof Pokemon pokemon)) return false;
        int stage = pokemon.getStage();
        return stage == 0;
    }

    private void handleAddToBench(Pokemon selectedPokemon) {
        int pokemonStage = selectedPokemon.getStage();
        Player currentPlayer = playerHandler.getCurrentPlayer();
        if(pokemonStage == 0) {
            currentPlayer.addBenchPokemon(selectedPokemon);
        } else {
            gui.displayMessage("This is not a basic Pokemon and can not place card on bench!");
        }
    }

    protected void handleAddEnergy(Energy energy) {
        Player currentPlayer = playerHandler.getCurrentPlayer();
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
        PlayerHandler playerHandler = new PlayerHandler();
        CardManager cardManager = new CardManager();
        Game game = new Game(gui, random, gameSetup, playerHandler, cardManager);
        gui.createGUI();
        game.setupGame();
    }
}
