package main;

import java.util.ArrayList;
import java.util.Random;

public class Game {
    private final GUI gui;
    private final Random random;

    private final Player player1;
    private final Player player2;

    private int playerTurn;
    private Player curPlayer;
    private int turn;
    private Player defendingPlayer;
    private Energy selectedEnergy;
    private boolean gameOver;

    public Game(GUI gui, Random rand, Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.gui = gui;
        this.random = rand;
        this.turn = 1;
        this.gameOver = false;

        // https://docs.oracle.com/javase/8/docs/api/java/lang/Runnable.html
        // https://www.geeksforgeeks.org/runnable-interface-in-java/

        gui.createFlipButton(this::setupGame);
    }

    Game(GUI gui, Random rand, Player player1, Player player2, boolean test) {
        this.player1 = player1;
        this.player2 = player2;
        this.gui = gui;
        this.random = rand;
        this.turn = 1;
        this.playerTurn = 1;
        this.curPlayer = player1;
        this.gameOver = false;

        // https://docs.oracle.com/javase/8/docs/api/java/lang/Runnable.html
        // https://www.geeksforgeeks.org/runnable-interface-in-java/
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

    void setupDecks() {
        player1.createCustomDeck();
        player1.drawStartingHand();

        player2.createCustomDeck();
        player2.drawStartingHand();

        player1.drawPrizeCards();
        player2.drawPrizeCards();
    }

    private void setCurPlayerPokemon() {
        gui.removeAllButtons();
        gui.displayMessage(curPlayer.handAsString());
        gui.displayCards(curPlayer.handAsList(), this::makeActiveCard, "Select Active Pokemon");
    }

    void makeActiveCard() {
        Card lastSelectedCard = gui.getLastSelectedCard();
        if (isBasicPokemon(lastSelectedCard)) {
            gui.makeActiveCard(lastSelectedCard, playerTurn);
            curPlayer.setActivePokemon((Pokemon) lastSelectedCard);
            gui.removeAllButtons();
            mainGameLoop();
        } else {
            gui.displayMessage("Basic Pokemon has not been selected!");
        }
    }

    private void passTurn() {
        curPlayer.passTurn();
        playerTurn = playerTurn % 2 + 1;
        curPlayer = playerTurn == 1 ? player1 : player2;
        defendingPlayer = playerTurn == 1 ? player2 : player1;
        turn++;
        if (turn == 2) {
            setCurPlayerPokemon();
        } else {
            gui.removeAllButtons();

            if (!gameOver) {
                curPlayer.drawCard();
                mainGameLoop();
            } else {
                System.out.println("Game Over");
            }
        }
        gui.updateTurn(playerTurn);
    }

    private void mainGameLoop() {
        gui.displayCards(curPlayer.handAsList(), this::playCard, "Play Selected Card");
        gui.createButton("Retreat", this::retreatAction);
        gui.createButton("Attack", this::attack);
        gui.createButton("Pass Turn", this::passTurn);
    }

    private void playCard() {
        Card lastSelectedCard = gui.getLastSelectedCard();

        if (isBasicPokemon(lastSelectedCard)) {
            gui.addBenchCard(lastSelectedCard, playerTurn);
            curPlayer.addBenchPokemon(lastSelectedCard);
            curPlayer.removeFromHand(lastSelectedCard);
            gui.removeAllButtons();
            mainGameLoop();
        } else if (lastSelectedCard instanceof Pokemon) {
            // TODO: Check if there is a card on the field to evolve from
        } else if (lastSelectedCard instanceof Energy) {
//            addEnergyToActive((Energy) lastSelectedCard);
            if(curPlayer.canAddEnergy()) {
                selectedEnergy = (Energy)lastSelectedCard;
                displayPossiblePokemon();
            } else {
                gui.displayMessage("Can only add one energy per turn");
            }

        } else if (lastSelectedCard instanceof Trainer) {
            //(Trainer) lastSelectedCard.doEffects(player1, player2, playerTurn);
        } else {
            gui.displayMessage("Playable card has not been selected");
        }
    }

    ArrayList<Card> getOnlyPokemon(ArrayList<Card> cards) {
        ArrayList<Card> pokemon = new ArrayList<>();
        for (Card card : cards) {
            if(card instanceof Pokemon) {
                pokemon.add(card);
            }
        }
        return pokemon;
    }

    private void displayPossiblePokemon() {
        gui.removeAllButtons();
        ArrayList<Card> possiblePokemon = getOnlyPokemon(curPlayer.benchAsList());
        possiblePokemon.add(curPlayer.getActivePokemon());
        gui.displayCards(possiblePokemon, this::addEnergyToPokemon, "Select Pokemon To Add Energy To");
    }

    private void addEnergyToPokemon() {
        Card selectedPokemon = gui.getLastSelectedCard();
        if(!(selectedPokemon instanceof Pokemon)) {
            gui.displayMessage("Select Pokemon To Add Energy To");
            displayPossiblePokemon();
        } else {
            curPlayer.addEnergyToPokemon((Pokemon) selectedPokemon, selectedEnergy);
            gui.displayMessage(selectedEnergy.getName() + " has been added to " + selectedPokemon.getName());
            gui.removeAllButtons();
            mainGameLoop();
        }
    }

    private void retreatAction() {
        Pokemon activePokemon = (Pokemon) curPlayer.getActivePokemon();
        gui.displayMessage("It costs " + activePokemon.retreatCost + " Colorless Energy\nFor " + activePokemon.name + " to retreat");
        if (curPlayer.canRetreat()) {
            gui.removeAllButtons();
            gui.displayCards(curPlayer.benchAsList(), this::handleRetreat, "Select Card to Switch In");
        } else {
            gui.displayMessage("No");
        }
    }

    private void handleRetreat() {
        Card lastSelectedCard = gui.getLastSelectedCard();
        curPlayer.retreat((Pokemon)lastSelectedCard);
        gui.retreat(lastSelectedCard, playerTurn);
        gui.removeAllButtons();
        mainGameLoop();
    }

    private String generateAttackButtonMessage(Pokemon activePokemon, ArrayList<Attack> attacks) {
        String pokemonAttackMessage = activePokemon.getName() + ":\n";
        for(Attack a: attacks) {
            pokemonAttackMessage += "\n" + a.name
                    + "\n - Cost:  \n" + a.getCosts()
                    + "\n - Damage:  " + a.damage + "\n";
        }
        pokemonAttackMessage += "\n" + activePokemon.getName() + " has Energies: \n" +
                activePokemon.getEnergiesString();
        return pokemonAttackMessage;
    }

    private void attack() {
        Pokemon activePokemon = (Pokemon) curPlayer.getActivePokemon();
        ArrayList<Attack> attacks = activePokemon.attacks;
        String pokemonAttackMessage = generateAttackButtonMessage(activePokemon, attacks);
        gui.displayMessage(pokemonAttackMessage);
        gui.removeAllButtons();
        //let player choose attack
        handleAttackLogic(attacks);
    }

    private void handleAttackLogic(ArrayList<Attack> attacks) {
        if (curPlayer.canAttack() && defendingPlayer != null) {
            gui.displayAttacks(attacks, this::sendAttack, "Select Attack");
            //make sure Pokémon has enough energy to attack
        } else {
            gui.removeAllButtons();
            mainGameLoop();
        }
    }

    private void sendAttack() {
        Attack lastSelectedAttack = gui.getLastSelectedAttack();
        if((lastSelectedAttack != null) && curPlayer.canAttack(lastSelectedAttack)) {
            attackPlayer(lastSelectedAttack);
        } else {
            gui.displayMessage("No attack selected or not enough energy to attack!");
            gui.removeAllButtons();
            mainGameLoop();
        }
    }

    private void attackPlayer(Attack lastSelectedAttack) {
        Pokemon actvPokemon = (Pokemon) curPlayer.getActivePokemon();
        defendingPlayer = curPlayer.equals(player1) ? player2 : player1;
        int damage = lastSelectedAttack.damage;
        Pokemon defendingActive = (Pokemon) defendingPlayer.getActivePokemon();
        int dmgCounters = damage/10;
        defendingPlayer.takeDamage(dmgCounters, actvPokemon.type);
        curPlayer.removeEnergyForAttack(lastSelectedAttack);

        gui.displayMessage(defendingPlayer.getName() + "'s active Pokemon: " + defendingActive.getName() + " has taken " + damage +
                " hp of damage!\nThere new hp is: " + defendingActive.getCurHP());
        if(!defendingActive.isAlive()) {
            handleDeadPokemon(defendingPlayer);
        }
        passTurn();
    }

    private boolean checkPrizeCardsGone(Player defendingPlayer) {
        if(defendingPlayer.getNumPokemonDied() >= 6) {
            gui.displayMessage("Congratulations " + curPlayer.getName() + "\n"
            + "You have collected all of your prize cards and win the game!");
            gameOver = true;
        }
        return gameOver;
    }

    private boolean checkDefendingBenchEmpty(Player defendingPlayer) {
        if(defendingPlayer.benchIsEmpty()) {
            gui.displayMessage("Congratulations " + curPlayer.getName() + "\n"
            + defendingPlayer.getName() + " has ran out of playable active Pokemon\nso you win the game!" );
            gameOver = true;
        }
        return gameOver;
    }

    private void continuePlaying(Player defendingPlayer) {
        int activeNum = (curPlayer == player1) ? 2 : 1;
        int oldActive = (curPlayer == player1) ? 1 : 2;

        curPlayer.pickupPrizeCard();
        Card newActive = defendingPlayer.setNewActive();
        curPlayer = (curPlayer == player1) ? player2 : player1;
        this.defendingPlayer = (curPlayer == player1) ? player2 : player1;

        gui.makeActiveCard(newActive, activeNum);
        gui.removeBenchCard(newActive, activeNum);
        gui.removePrizeCard(oldActive);
        mainGameLoop();
    }

    private void handleDeadPokemon(Player defendingPlayer) {
        gui.displayMessage(defendingPlayer.getName() + "'s active Pokemon has died!");
        defendingPlayer.pokemonDied();
        checkPrizeCardsGone(defendingPlayer);
        if(checkPrizeCardsGone(defendingPlayer)) {
            gui.closeWindow();
        } else if(checkDefendingBenchEmpty(defendingPlayer)) {
            gui.closeWindow();
        } else {
            continuePlaying(defendingPlayer);
        }
    }

    private boolean isBasicPokemon(Card card) {
        return card instanceof Pokemon && ((Pokemon) card).stage == 0;
    }

    public static void main(String[] args) {
        Game game = new Game(new GameGUI(), new Random(), new Player("Player 1"), new Player("Player 2"));
    }
}