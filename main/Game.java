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
        gui.updateTurn(playerHandler.getPlayerTurn());
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
        displayCurrentPlayerHand();
        displayActiveDirections();
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
        switch (action) {
            case "AddToBench" -> handleBenchAction();
            case "AddEnergy" -> handleEnergyAction();
            case "PassTurn" -> handlePassTurnAction();
            case "Attack" -> handleAttackAction();
            case "Retreat" -> handleRetreatAction();
            case "CardInfo" -> displayCardInfo();
        }
    }

    protected void displayCardInfo() {
        boolean hasCardSelected = gui.hasCardSelected();
        if(hasCardSelected) {
            Card lastSelectedCard = gui.getLastSelectedCard();
            gui.displayCardReport(lastSelectedCard);
        } else {
            //display Active Pokemon Info
            Player activePlayer = playerHandler.getCurrentPlayer();
            Pokemon currentActive = (Pokemon) activePlayer.getActivePokemon();
            gui.displayCardReport(currentActive);
        }
    }

    protected void handleRetreatAction() {
        Player activePlayer = playerHandler.getCurrentPlayer();
        Pokemon activePokemon = (Pokemon) activePlayer.getActivePokemon();
        boolean canRetreat = activePokemon.canRetreat() && playerHandler.canRetreat();
        gui.displayRetreatEnergy(activePokemon, canRetreat);
        if(canRetreat) {
            Card newActive = retreatPokemon();
            playerHandler.setNewActive(newActive);
        }
    }

    protected Card retreatPokemon() {
        gui.removeAllButtons();
        gui.displayMessage("Select new active Pokemon");
        gui.displayCards(playerHandler.getOnlyPokemonFromBench(1));
        gui.displayConfirmButton();
        gui.waitForAction();
        Card selectedCard = gui.getLastSelectedCard();
        gui.replaceActiveCard(selectedCard, playerHandler.getPlayerTurn());
        return selectedCard;
    }

    protected void handleAttackAction() {
        if(!playerHandler.playerCanAttack()) {
            gui.displayMessage("You are unable to attack right now!");
        } else {
            handleAttackOpponent();
        }
    }

    protected void handleAttackOpponent() {
        Attack selectedAttack = displayAttackInfo();
        if(!playerHandler.attackOpponent(selectedAttack)) {
            gui.displayMessage("Do not have the energy for that attack!");
        } else {
            boolean defendingIsDead = playerHandler.isDefendingDead();
            displayPostAttackInfo(selectedAttack, defendingIsDead);
            if(defendingIsDead) {
                handleDeadActive();
            }
            playerHandler.swapPlayerTurns();
        }
    }

    protected void displayPostAttackInfo(Attack attack, boolean isDead) {
        Player currentPlayer = playerHandler.getCurrentPlayer();
        Player defendingPlayer = playerHandler.getDefendingPlayer();
        gui.displayAttackMessage(currentPlayer, defendingPlayer, attack);
        if(isDead) {
            gui.displayDeadActiveInfo(defendingPlayer);
        }
    }

    protected void handleDeadActive() {
        displayDeadActiveGUI();
        if(!gameOver) {
            Card lastSelectedCard = gui.getLastSelectedCard();
            int playerTurn = playerHandler.getPlayerTurn();
            int defendingNum = playerTurn % 2 + 1;

            gui.makeActiveCard(lastSelectedCard, defendingNum);
            gui.removeBenchCard(lastSelectedCard, defendingNum);
            if(!checkBasicPokemon(lastSelectedCard)) {
                gui.displayMessage("Not a basic Pokemon!");
            } else {
                playerHandler.killDefenderActive((Pokemon)lastSelectedCard);
            }
        }
    }

    private void displayDeadActiveGUI() {
        ArrayList<Card> playerPokemon = playerHandler.getOnlyPokemonFromBench(2);
        if(playerPokemon.isEmpty()) {
            Player winner = playerHandler.getCurrentPlayer();
            Player loser = playerHandler.getDefendingPlayer();
            gameIsOver(winner, loser);
        } else {
            gui.removeAllButtons();
            gui.displayCards(playerPokemon);
            gui.displayConfirmButton();
            gui.waitForAction();
        }

    }

    protected void gameIsOver(Player winner, Player loser) {
        gameOver = true;
        gui.displayWinningMessage(winner, loser);
        gui.closeWindow();
    }

    private Attack displayAttackInfo() {
        gui.removeAllButtons();
        ArrayList<Attack> attacks = playerHandler.getCurrentPlayerAttacks();
        gui.displayPossibleAttacks(attacks);
        gui.displayConfirmButton();
        gui.waitForAction();
        return gui.getLastSelectedAttack();
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

    public void handlePassTurnAction() {
        boolean hasActiveAlready = playerHandler.passTurn();
        gui.updateTurn(playerHandler.getPlayerTurn());
        playerHandler.drawCardFromDeck();
        if(!hasActiveAlready) {
            selectActiveLoop();
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
            ArrayList<Card> onlyPokemon = playerHandler.getOnlyPokemonFromBench(1);
            Card activePokemon = currentPlayer.getActivePokemon();
            onlyPokemon.add(activePokemon);

            Pokemon selectedPokemon = displayAddEnergyInfo(onlyPokemon);
            playerHandler.addEnergyToPokemon(energy, selectedPokemon);
        }
    }

    protected Pokemon displayAddEnergyInfo(ArrayList<Card> pokemon) {
        gui.displayMessage("Select Pokemon to add Energy to");
        gui.removeAllButtons();
        gui.displayCards(pokemon);
        gui.displayConfirmButton();
        gui.waitForAction();
        return (Pokemon) gui.getLastSelectedCard();
    }
}
