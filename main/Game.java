package main;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class Game {
    protected GUI gui;
    protected Random random;
    protected SetupGame gameSetup;
    protected PlayerHandler playerHandler;
    protected boolean gameOver;
    protected Locale locale;

    public Game(GUI gui, Random random, SetupGame gameSetup, PlayerHandler playerHandler) {
        this.gui = gui;
        this.random = random;
        this.gameSetup = gameSetup;
        this.playerHandler = playerHandler;
        this.gameOver = false;
    }

    protected void setupGame() {
        decideLocale();
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

    private void decideLocale() {
        locale = gui.displayLocaleOptions();
        gui.displayMessage("You have chosen: " + locale.getLanguage());
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
            case "Evolve" -> handleEvolveAction();
            case "PlayTrainer" -> handleTrainerAction();
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
            if(newActive != null) {
                playerHandler.setNewActive(newActive);
            }
        }
    }

    protected Card retreatPokemon() {
        gui.removeAllButtons();
        gui.displayMessage("Select new active Pokemon");
        ArrayList<Card> playerCards = playerHandler.getOnlyPokemonFromBench(1);
        gui.displayCards(playerCards);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        if(!gui.isCancelled()) {
            Card selectedCard = gui.getLastSelectedCard();
            if(!playerCards.contains(selectedCard)) {
                gui.displayMessage("No card selected!");
                return retreatPokemon();

            } else {
                gui.replaceActiveCard(selectedCard, playerHandler.getPlayerTurn());
                return selectedCard;
            }
        } else {
            return null;
        }
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
        if(selectedAttack != null) {
            if(!playerHandler.attackOpponent(selectedAttack)) {
                gui.displayMessage("Do not have the energy for that attack!");
            } else {
                boolean defendingIsDead = playerHandler.isDefendingDead();
                displayPostAttackInfo(selectedAttack, defendingIsDead);
                if(defendingIsDead) {
                    handleDeadActive();
                }
                handlePassTurnAction();
            }
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
        ArrayList<Card> activeBench = playerHandler.getOnlyPokemonFromBench(2);
        displayDeadActiveGUI(activeBench);
        if(!gameOver) {
            Card lastSelectedCard = gui.getLastSelectedCard();

            if(!activeBench.contains(lastSelectedCard)) {
                gui.displayMessage("Invalid Pokemon entry!");
                handleDeadActive();
            } else {
                int playerTurn = playerHandler.getPlayerTurn();
                handlePickupPrizeCard(playerTurn);
                int defendingNum = playerTurn % 2 + 1;
                playerHandler.killDefenderActive((Pokemon)lastSelectedCard);
                gui.makeActiveCard(lastSelectedCard, defendingNum);
                gui.removeBenchCard(lastSelectedCard, defendingNum);
            }
        }
    }

    protected void handlePickupPrizeCard(int turn) {
        int prizeCardsLeft = playerHandler.activePickupPrizeCard();
        gui.removePrizeCard(turn);
        if(prizeCardsLeft == 0) {
            Player winner = playerHandler.getCurrentPlayer();
            Player loser = playerHandler.getDefendingPlayer();
            gameIsOver(winner, loser);
        }
    }


    private void displayDeadActiveGUI(ArrayList<Card> playerPokemon) {
        if(playerPokemon.isEmpty()) {
            Player winner = playerHandler.getCurrentPlayer();
            Player loser = playerHandler.getDefendingPlayer();
            gameIsOver(winner, loser);
        } else {
            gui.removeAllButtons();
            gui.displayCards(playerPokemon);
            gui.displayConfirmAndCancelButton();
            gui.waitForAction();
        }

    }

    protected void gameIsOver(Player winner, Player loser) {
        gameOver = true;
        gui.displayWinningMessage(winner, loser);
        gui.closeWindow();
    }

    protected Attack displayAttackInfo() {
        gui.removeAllButtons();
        ArrayList<Attack> attacks = playerHandler.getCurrentPlayerAttacks();
        gui.displayPossibleAttacks(attacks);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        if(gui.isCancelled()) return null;
        Attack attack = gui.getLastSelectedAttack();
        if(!attacks.contains(attack)) {
            gui.displayMessage("Attack not selected!");
            return displayAttackInfo();
        } else {
            return attack;
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

    protected void handleTrainerAction() {
        Card lastSelectedCard = gui.getLastSelectedCard();
        if(!(lastSelectedCard instanceof Trainer)) {
            gui.displayMessage("Trainer has not been selected!");
        } else {
            handleUseTrainer((Trainer)lastSelectedCard);
        }
    }

    protected void handleUseTrainer(Trainer trainer) {
        Player currentPlayer = playerHandler.getCurrentPlayer();
        ArrayList<Card> playerPokemon = playerHandler.getAllPlayerPokemon();
        ArrayList<Card> playerEnergy = playerHandler.getAllPlayerEnergy();
        currentPlayer.removeFromHand(trainer);

        Pokemon selectedPokemon = displayTrainerPokemonSelection(trainer, playerPokemon);
        Energy selectedEnergy = displayTrainerEnergySelection(trainer, playerEnergy);
        trainer.doEffects(currentPlayer, selectedPokemon, selectedEnergy);
    }

    protected Pokemon displayTrainerPokemonSelection(Trainer trainer, ArrayList<Card> pokemon) {
        String trainerName = trainer.getName();
        if(trainerName.equals("Potion") || trainerName.equals("Super Potion")) {
            gui.displayMessage("Select Pokemon to use Potion on");
            gui.removeAllButtons();
            gui.displayCards(pokemon);
            gui.displayConfirmAndCancelButton();
            gui.waitForAction();
            if(gui.isCancelled()) return null;
            Pokemon selectedPokemon = (Pokemon) gui.getLastSelectedCard();
            if(!pokemon.contains(selectedPokemon)) {
                gui.displayMessage("No Pokemon selected!");
                return displayTrainerPokemonSelection(trainer, pokemon);
            } else {
                return selectedPokemon;
            }
        } else {
            return null;
        }
    }

    protected Energy displayTrainerEnergySelection(Trainer trainer, ArrayList<Card> energy) {
        if(trainer.getName().equals("Super Potion")) {
            gui.displayMessage("Select Energy to discard for Super Potion");
            gui.removeAllButtons();
            gui.displayCards(energy);
            gui.displayConfirmAndCancelButton();
            gui.waitForAction();
            if(gui.isCancelled()) return null;
            Energy selectedEnergy = (Energy) gui.getLastSelectedCard();
            if(!energy.contains(selectedEnergy)) {
                gui.displayMessage("No Energy selected!");
                return displayTrainerEnergySelection(trainer, energy);
            } else {
                return selectedEnergy;
            }
        } else {
            return null;
        }
    }

    protected void handlePassTurnAction() {
        boolean hasActiveAlready = playerHandler.passTurn();
        gui.updateTurn(playerHandler.getPlayerTurn());
        playerHandler.drawCardFromDeck();
        if(!hasActiveAlready) {
            selectActiveLoop();
        }
    }

    protected void handleTrainerAction() {
        Card lastSelectedCard = gui.getLastSelectedCard();
        if(!(lastSelectedCard instanceof Trainer)) {
            gui.displayMessage("Trainer has not been selected!");
        } else {
            handlePlayTrainer((Trainer)lastSelectedCard);
        }
    }
    private void handlePlayTrainer(Trainer trainer) {

        playerHandler.playTrainerCard(trainer);
    }

    protected void handleEvolveAction() {
        Card lastSelectedCard = gui.getLastSelectedCard();
        if(!(lastSelectedCard instanceof Pokemon)) {
            gui.displayMessage("Pokemon has not been selected!");
        } else {
            handleEvolve((Pokemon)lastSelectedCard);
        }
    }

    private void handleEvolve(Pokemon evolution) {
        int pokemonStage = evolution.getStage();
        if(pokemonStage != 0) {
            ArrayList<Card> onlyPreEvolutions = playerHandler.getOnlyPreEvolutionsFromActivePlayer(evolution);

            if(onlyPreEvolutions.isEmpty()) {
                gui.displayMessage("You have no Pokemon that can evolve into " + evolution.getName());
            } else {
                Pokemon basePokemon = displayEvolveInfo(onlyPreEvolutions);
                if (basePokemon != null) {
                    switch(playerHandler.evolve(evolution, basePokemon)){
                        case "Error":
                            gui.displayMessage("Evolution could not be completed");
                            break;

                        case "JustPlayed":
                            gui.displayMessage("Base Pokemon was just played");
                            break;

                        case "Active":
                            gui.makeActiveCard(evolution, playerHandler.getPlayerTurn());
                            break;

                        case "Bench":
                            int playerTurn = playerHandler.getPlayerTurn();
                            gui.removeBenchCard(basePokemon, playerTurn);
                            gui.addBenchCard(evolution, playerTurn);
                            break;
                    }
                }
            }
        } else {
            gui.displayMessage("This is a basic Pokemon, not an evolution. Try adding " + evolution.getName() + " to the bench if you have room!");
        }
    }

    protected Pokemon displayEvolveInfo(ArrayList<Card> pokemon) {
        gui.displayMessage("Select Pokemon to evolve from");
        gui.removeAllButtons();
        gui.displayCards(pokemon);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        if(gui.isCancelled()) return null;
        Pokemon selectedPokemon = (Pokemon) gui.getLastSelectedCard();
        if(!pokemon.contains(selectedPokemon)) {
            gui.displayMessage("No Pokemon selected!");
            return displayEvolveInfo(pokemon);
        } else {
            return selectedPokemon;
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
        if(!(card instanceof Pokemon pokemon)) {
            return false;
        }
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
            if(selectedPokemon != null) {
                playerHandler.addEnergyToPokemon(energy, selectedPokemon);
            }
        }
    }

    protected Pokemon displayAddEnergyInfo(ArrayList<Card> pokemon) {
        gui.displayMessage("Select Pokemon to add Energy to");
        gui.removeAllButtons();
        gui.displayCards(pokemon);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        if(gui.isCancelled()) return null;
        Pokemon selectedPokemon = (Pokemon) gui.getLastSelectedCard();
        if(!pokemon.contains(selectedPokemon)) {
            gui.displayMessage("No Pokemon selected!");
            return displayAddEnergyInfo(pokemon);
        } else {
            return selectedPokemon;
        }
    }
}
