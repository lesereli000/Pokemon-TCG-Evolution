package main;

import main.ui.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

public class Game {
    protected GUI gui;
    protected Random random;
    protected SetupGame gameSetup;
    protected PlayerHandler playerHandler;
    protected boolean gameOver;
    protected Locale locale;
    protected ResourceBundle messages;

    public Game(GUI gui, Random random, SetupGame gameSetup, PlayerHandler playerHandler) {
        this.gui = gui;
        this.random = random;
        this.gameSetup = gameSetup;
        this.playerHandler = playerHandler;
        this.gameOver = false;
    }

    protected void setupGame() {
        decideLocale();
        String deckFileName = gui.displayDeckOptions();
        setupFlipButton();
        String coinFlipResult = gameSetup.completeGameSetup();
        playerHandler.completePlayerSetup(coinFlipResult, deckFileName);
        gui.setPlayers(playerHandler.player1, playerHandler.player2);
        int playerTurn = playerHandler.getPlayerTurn();
        gui.updateTurn(playerTurn);
        displaySetupResults(coinFlipResult, playerTurn);
        selectActiveLoop();
        runGameLoop();
    }

    protected void runGameLoop() {
        while(!gameOver) {
            mainGameLoop();
            if(gui.gameIsOver()) gameOver = true;
        }
    }

    private void decideLocale() {
        locale = gui.displayLocaleOptions();
        messages = ResourceBundle.getBundle("MessagesBundle", locale);

        String message = messages.getString("language");
        message = MessageFormat.format(message, locale.getDisplayLanguage());
        gui.displayMessage(message);
    }

    protected void displaySetupResults(String coinFlipResult, int turn) {
        String message = messages.getString("coinflip");
        String result = messages.getString(coinFlipResult);
        String player = messages.getString("player");
        message = MessageFormat.format(message, result, player, turn);
        gui.displayMessage(message);
    }

    protected void selectActiveLoop() {
        displayActiveDirections();
        displayCurrentPlayerHand();
        gui.setupActivePokemon();
        gui.waitForButtonPressed();
        Card selectedCard = gui.getLastSelectedCard();
        if(selectedCard != null && checkBasicPokemon(selectedCard)) {
            makeNewActivePokemon((Pokemon) selectedCard);
            displayCurrentPlayerHand();
        } else {
            String message = (selectedCard == null) ? "Please select a card." : messages.getString("notBasic");
            gui.displayMessage(message);
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
            default -> {
                if (action != null && action.endsWith("_DROP")) {
                    handleInstantDrop(action);
                }
            }
        }
    }

    protected void handleInstantDrop(String action) {
        try {
            Card card = gui.getLastSelectedCard();
            if (card == null) return;
            
            int turn = playerHandler.getPlayerTurn();
            Player currentPlayer = playerHandler.getCurrentPlayer();
            String activeZone = "P" + turn + "_ACTIVE_DROP";
            String benchPrefix = "P" + turn + "_BENCH_";
            boolean actionTaken = false;

            if (action.equals("BOARD_DROP")) {
                if (card instanceof Trainer) {
                    handleUseTrainer((Trainer) card);
                    actionTaken = true;
                }
            } else if (action.equals(activeZone)) {
                if (card instanceof Energy) {
                    handleInstantEnergyAttach(card, currentPlayer.getActivePokemon());
                    actionTaken = true;
                } else if (card instanceof Pokemon) {
                    handleEvolve((Pokemon) card, currentPlayer.getActivePokemon());
                    actionTaken = true;
                } else if (card instanceof Trainer) {
                    handleUseTrainer((Trainer) card);
                    actionTaken = true;
                }
            } else if (action.startsWith(benchPrefix)) {
                String slotStr = action.substring(benchPrefix.length());
                if (slotStr.contains("_")) {
                    slotStr = slotStr.split("_")[0];
                }
                int slot = Integer.parseInt(slotStr);
                
                ArrayList<Card> bench = playerHandler.getOnlyPokemonFromBench(1); // 1 = Current Player
                
                if (card instanceof Pokemon && ((Pokemon) card).stage == 0) {
                    handleAddToBench((Pokemon) card);
                    actionTaken = true;
                } else if (card instanceof Energy && bench.size() > slot) {
                    handleInstantEnergyAttach(card, (Pokemon) bench.get(slot));
                    actionTaken = true;
                } else if (card instanceof Pokemon && ((Pokemon) card).stage > 0 && bench.size() > slot) {
                    handleEvolve((Pokemon) card, (Pokemon) bench.get(slot));
                    actionTaken = true;
                } else if (card instanceof Trainer) {
                    handleUseTrainer((Trainer) card);
                    actionTaken = true;
                }
            }
            
            if (actionTaken) {
                // Refresh the hand to show new cards (drawn by Trainers) or removed cards
                gui.displayCards(currentPlayer.handAsList());
                gui.displayActionButtons();
            }
            
            // Clear the drag selection
            gui.setLastSelectedCardForDrag(null);
            
        } catch (Exception e) {
            System.err.println("Instant Drop handling error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleInstantEnergyAttach(Card energy, Pokemon target) {
        if (playerHandler.activeCanAddEnergy()) {
            playerHandler.addEnergyToPokemon((Energy) energy, target);
        } else {
            String message = messages.getString("addEnergyErr");
            gui.displayMessage(message);
        }
    }



    protected void displayCardInfo() {
        boolean hasCardSelected = gui.hasCardSelected();
        if(hasCardSelected) {
            Card lastSelectedCard = gui.getLastSelectedCard();
            gui.displayCardReport(lastSelectedCard);
        } else {
            Player activePlayer = playerHandler.getCurrentPlayer();
            Pokemon currentActive = activePlayer.getActivePokemon();
            gui.displayCardReport(currentActive);
        }
    }

    protected void handleRetreatAction() {
        Player activePlayer = playerHandler.getCurrentPlayer();
        Pokemon activePokemon = activePlayer.getActivePokemon();
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
        String message = messages.getString("newActive");
        gui.displayMessage(message);
        ArrayList<Card> playerCards = playerHandler.getOnlyPokemonFromBench(1);
        gui.displayCards(playerCards);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        if(!gui.isCancelled()) {
            Card selectedCard = gui.getLastSelectedCard();
            if(!playerCards.contains(selectedCard)) {
                message = messages.getString("noSelected");
                gui.displayMessage(message);
                return retreatPokemon();
            } else {
                gui.replaceActiveCard(playerHandler.getCurrentPlayer(), selectedCard);
                return selectedCard;
            }
        } else {
            return null;
        }
    }

    protected void handleAttackAction() {
        if(!playerHandler.playerCanAttack()) {
            String message = messages.getString("noAttack");
            gui.displayMessage(message);
        } else {
            handleAttackOpponent();
        }
    }

    protected void handleAttackOpponent() {
        Attack selectedAttack = displayAttackInfo();
        if(selectedAttack != null) {
            if(!playerHandler.attackOpponent(selectedAttack)) {
                String message = messages.getString("notEnergy");
                gui.displayMessage(message);
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
                String message = messages.getString("invalidPokemon");
                gui.displayMessage(message);
                handleDeadActive();
            } else {
                int playerTurn = playerHandler.getPlayerTurn();
                handlePickupPrizeCard(playerTurn);
                playerHandler.killDefenderActive((Pokemon)lastSelectedCard);
                Player defendingPlayer = playerHandler.getDefendingPlayer();
                gui.makeActiveCard(defendingPlayer, lastSelectedCard);
                gui.removeBenchCard(defendingPlayer, lastSelectedCard);
            }
        }
    }

    protected void handlePickupPrizeCard(int turn) {
        int prizeCardsLeft = playerHandler.activePickupPrizeCard();
        gui.removePrizeCard(playerHandler.getCurrentPlayer());
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
            String message = messages.getString("atkNotSelect");
            gui.displayMessage(message);
            return displayAttackInfo();
        } else {
            return attack;
        }
    }

    protected void handleBenchAction() {
        Card lastSelectedCard = gui.getLastSelectedCard();
        if(!(lastSelectedCard instanceof Pokemon)) {
            String message = messages.getString("noPokemon");
            gui.displayMessage(message);
        } else {
            handleAddToBench((Pokemon)lastSelectedCard);
        }
    }

    protected void handleEnergyAction() {
        Card lastSelectedCard = gui.getLastSelectedCard();
        if(!(lastSelectedCard instanceof Energy)) {
            String message = messages.getString("noEnergy");
            gui.displayMessage(message);
        } else {
            handleAddEnergy((Energy)lastSelectedCard);
        }
    }

    protected void handleTrainerAction() {
        Card lastSelectedCard = gui.getLastSelectedCard();
        if(!(lastSelectedCard instanceof Trainer)) {
            String message = messages.getString("noTrainer");
            gui.displayMessage(message);
        } else {
            handleUseTrainer((Trainer)lastSelectedCard);
        }
    }

    protected void handleUseTrainer(Trainer trainer) {
        Player currentPlayer = playerHandler.getCurrentPlayer();
        ArrayList<Card> playerPokemon = playerHandler.getAllPlayerPokemon();

        if(trainer.getName().equals("Switch")) {
            playerPokemon.remove(playerHandler.getActivePokemon());
            playerPokemon.removeAll(playerHandler.getHandPokemon());
        }

        ArrayList<Card> playerEnergy = playerHandler.getAllPlayerEnergy();

        Pokemon selectedPokemon = displayTrainerPokemonSelection(trainer, playerPokemon);
        Energy selectedEnergy = displayTrainerEnergySelection(trainer, playerEnergy);

        // If a selection was required but not made (e.g. cancelled), abort.
        String name = trainer.getName();
        if (selectedPokemon == null && (name.equals("Switch") || name.equals("Potion") || name.equals("Super Potion"))) {
            return;
        }

        currentPlayer.removeFromHand(trainer);
        trainer.doEffects(currentPlayer, selectedPokemon, selectedEnergy);

        if(trainer.getName().equals("Switch") && selectedPokemon != null) {
            gui.replaceActiveCard(currentPlayer, (Card) selectedPokemon);
        }
    }

    protected Pokemon displayTrainerPokemonSelection(Trainer trainer, ArrayList<Card> pokemon) {
        String trainerName = trainer.getName();
        if(trainerName.equals("Potion") || trainerName.equals("Super Potion") || trainerName.equals("Switch")) {
            String trainerText = "";
            if(trainer.getName().equals("Switch")) trainerText = "selectPokSwitch";
            if(!trainer.getName().equals("Switch")) trainerText = "selectPokPot";
            String message = messages.getString(trainerText);
            gui.displayMessage(message);
            gui.removeAllButtons();
            gui.displayCards(pokemon);
            gui.displayConfirmAndCancelButton();
            gui.waitForAction();
            if(gui.isCancelled()) return null;
            Pokemon selectedPokemon = (Pokemon) gui.getLastSelectedCard();
            if(!pokemon.contains(selectedPokemon)) {
                String msg = messages.getString("noPokemon");
                gui.displayMessage(msg);
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
            String message = messages.getString("selectEnSuper");
            gui.displayMessage(message);
            gui.removeAllButtons();
            gui.displayCards(energy);
            gui.displayConfirmAndCancelButton();
            gui.waitForAction();
            if(gui.isCancelled()) return null;
            Card lastCard = gui.getLastSelectedCard();
            if (!(lastCard instanceof Energy)) {
                String msg = messages.getString("noEnergy");
                gui.displayMessage(msg);
                return displayTrainerEnergySelection(trainer, energy);
            }
            Energy selectedEnergy = (Energy) lastCard;
            if(!energy.contains(selectedEnergy)) {
                String msg = messages.getString("noEnergy");
                gui.displayMessage(msg);
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
        boolean hasCards = playerHandler.drawCardFromDeck();
        if(!hasCards) {
            Player winner = playerHandler.getCurrentPlayer();
            Player loser = playerHandler.getDefendingPlayer();
            gameIsOver(winner, loser);
        }
        if(!hasActiveAlready) {
            selectActiveLoop();
        }
    }

    protected void handleEvolveAction() {
        Card lastSelectedCard = gui.getLastSelectedCard();
        if(!(lastSelectedCard instanceof Pokemon)) {
            String message = messages.getString("noPokemon");
            gui.displayMessage(message);
        } else {
            handleEvolve((Pokemon)lastSelectedCard, null);
        }
    }

    private void handleEvolve(Pokemon evolution, Pokemon target) {
        int pokemonStage = evolution.getStage();
        if(pokemonStage != 0) {
            ArrayList<Card> onlyPreEvolutions = playerHandler.getOnlyPreEvolutionsFromActivePlayer(evolution);

            if(onlyPreEvolutions.isEmpty()) {
                String message = messages.getString("cantEvolve");
                message = MessageFormat.format(message, evolution.getName());
                gui.displayMessage(message);
            } else {
                Pokemon basePokemon;
                if (target != null && onlyPreEvolutions.contains(target)) {
                    basePokemon = target;
                } else {
                    basePokemon = displayEvolveInfo(onlyPreEvolutions);
                }
                
                if (basePokemon != null) {
                    switch(playerHandler.evolve(evolution, basePokemon)){
                        case "Error":
                            String msg = messages.getString("evolveError");
                            gui.displayMessage(msg);
                            break;

                        case "JustPlayed":
                            String msg2 = messages.getString("justPlayed");
                            gui.displayMessage(msg2);
                            break;

                        case "Active":
                            gui.makeActiveCard(playerHandler.getCurrentPlayer(), evolution);
                            break;

                        case "Bench":
                            Player currentPlayer = playerHandler.getCurrentPlayer();
                            gui.removeBenchCard(currentPlayer, basePokemon);
                            gui.addBenchCard(currentPlayer, evolution);
                            break;
                    }
                }
            }
        } else {
            String message = messages.getString("evolveBasic");
            message = MessageFormat.format(message, evolution.getName());
            gui.displayMessage(message);
        }
    }

    protected Pokemon displayEvolveInfo(ArrayList<Card> pokemon) {
        String message = messages.getString("selectEvolve");
        gui.displayMessage(message);
        gui.removeAllButtons();
        gui.displayCards(pokemon);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        if(gui.isCancelled()) return null;
        Pokemon selectedPokemon = (Pokemon) gui.getLastSelectedCard();
        if(!pokemon.contains(selectedPokemon)) {
            String msg = messages.getString("noPokemon");
            gui.displayMessage(msg);
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
        String message = messages.getString("activeDir");
        gui.displayMessage(message);
    }

    public void makeNewActivePokemon(Pokemon p) {
        Player currentPlayer = playerHandler.getCurrentPlayer();
        currentPlayer.setActivePokemon(p);
        gui.makeActiveCard(currentPlayer, p);
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
            gui.addBenchCard(playerHandler.getCurrentPlayer(), selectedPokemon);
        } else {
            String message = messages.getString("noAddBench");
            gui.displayMessage(message);
        }
    }

    protected void handleAddEnergy(Energy energy) {
        if(!playerHandler.activeCanAddEnergy()) {
            String message = messages.getString("addEnergyErr");
            gui.displayMessage(message);
        } else {
            Player currentPlayer = playerHandler.getCurrentPlayer();
            ArrayList<Card> onlyPokemon = playerHandler.getOnlyPokemonFromBench(1);
            Pokemon activePokemon = currentPlayer.getActivePokemon();
            onlyPokemon.add(activePokemon);

            Pokemon selectedPokemon = displayAddEnergyInfo(onlyPokemon);
            if(selectedPokemon != null) {
                playerHandler.addEnergyToPokemon(energy, selectedPokemon);
            }
        }
    }

    protected Pokemon displayAddEnergyInfo(ArrayList<Card> pokemon) {
        String message = messages.getString("pokAddEnergy");
        gui.displayMessage(message);
        gui.removeAllButtons();
        gui.displayCards(pokemon);
        gui.displayConfirmAndCancelButton();
        gui.waitForAction();
        if(gui.isCancelled()) return null;
        Pokemon selectedPokemon = (Pokemon) gui.getLastSelectedCard();
        if(!pokemon.contains(selectedPokemon)) {
            String msg = messages.getString("noPokemon");
            gui.displayMessage(msg);
            return displayAddEnergyInfo(pokemon);
        } else {
            return selectedPokemon;
        }
    }
}
