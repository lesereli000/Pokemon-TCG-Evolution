package main;

import java.util.ArrayList;
import java.util.Random;

public class Game {
    protected GUI gui;
    protected Random random;
    protected SetupGame gameSetup;
    protected PlayerHandler playerHandler;
    protected boolean gameOver;

    public Game(GUI gui, Random random, SetupGame gameSetup, PlayerHandler playerHandler) {
        this.gui = gui;
        this.random = random;
        this.gameSetup = gameSetup;
        this.playerHandler = playerHandler;
        this.gameOver = false;
    }

    protected void setupGame() {
        setupFlipButton();
        String coinFlipResult = gameSetup.completeGameSetup();
        playerHandler.completePlayerSetup(coinFlipResult);
        Player currentPlayer = playerHandler.getCurrentPlayer();
        displaySetupResults(coinFlipResult, currentPlayer);
        selectActiveLoop();
        while(!gameOver) {
            mainGameLoop();
        }
    }

    protected void displaySetupResults(String coinFlipResult, Player currentPlayer) {
        gui.displayMessage("The result was " + coinFlipResult + " " + currentPlayer.getName() + " goes first!");
    }

    protected void selectActiveLoop() {
        displayActiveDirections();
        displayCurrentPlayerHand();
        gui.setupActivePokemon();
        gui.waitForButtonPressed();
        Card selectedCard = gui.getLastSelectedCard();
        if(checkBasicPokemon(selectedCard)) {
            makeNewActivePokemon((Pokemon) selectedCard);
            displayCurrentPlayerHand();
        } else {
            gui.displayMessage("Not a basic Pokemon!");
            gui.removeAllButtons();
            selectActiveLoop();
        }
    }

    protected void mainGameLoop() {
        ArrayList<Card> playerHand = playerHandler.getCurrentPlayerHand();
        gui.removeAllButtons();
        gui.displayCards(playerHand);
        gui.displayActionButtons();
        String action = gui.waitForButtonPressed();
        if(action.equals("AddToBench")) {
            handleBenchAction();
        } else if (action.equals("AddEnergy")) {
            handleEnergyAction();
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

    protected void displayCurrentPlayerHand() {
        gui.removeAllButtons();
        ArrayList<Card> playerCards = playerHandler.getCurrentPlayerHand();
        gui.displayCards(playerCards);
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

    protected void handleAddToBench(Pokemon selectedPokemon) {
        int pokemonStage = selectedPokemon.getStage();
        if(pokemonStage == 0) {
            playerHandler.addToBench(selectedPokemon);
            int playerTurn = playerHandler.getPlayerTurn();
            gui.addBenchCard(selectedPokemon, playerTurn);
        } else {
            gui.displayMessage("This is not a basic Pokemon and can not place card on bench!");
        }
    }

    protected void handleAddEnergy(Energy energy) {
        if(!playerHandler.activeCanAddEnergy()) {
            gui.displayMessage("Unable to add energy!");
        } else {
            Player currentPlayer = playerHandler.getCurrentPlayer();
            ArrayList<Card> onlyPokemon = currentPlayer.getOnlyPokemonFromHand();
            Card activePokemon = currentPlayer.getActivePokemon();
            onlyPokemon.add(activePokemon);

            gui.displayMessage("Select Pokemon to add Energy to");
            gui.removeAllButtons();
            gui.displayCards(onlyPokemon);
            gui.waitForPokemonSelected();
            Pokemon selectedPokemon = (Pokemon) gui.getLastSelectedCard();
            playerHandler.addEnergyToPokemon(energy, selectedPokemon);
        }
    }


    public static void main(String[] args) {
        GUI gui = new GameGUI();
        Random random = new Random();
        SetupGame gameSetup = new SetupGame(random);
        PlayerHandler playerHandler = new PlayerHandler();
        Game game = new Game(gui, random, gameSetup, playerHandler);
        gui.createGUI();
        game.setupGame();
    }
}
