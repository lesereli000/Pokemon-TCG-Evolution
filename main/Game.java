package main;

import org.easymock.internal.matchers.Null;

import java.awt.*;
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
    private Attack selectedAttack;

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

    Game(GUI gui, Random rand, Player player1, Player player2, boolean test) {
        this.player1 = player1;
        this.player2 = player2;
        this.gui = gui;
        this.random = rand;
        this.turn = 1;
        this.playerTurn = 1;
        this.curPlayer = player1;
        // https://docs.oracle.com/javase/8/docs/api/java/lang/Runnable.html
        // https://www.geeksforgeeks.org/runnable-interface-in-java/

        //gui.createFlipButton(this::setupGame);
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
        player1.createFullRandomDeck(random);
        player1.drawStartingHand();

        player2.createFullRandomDeck(random);
        player2.drawStartingHand();
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
        playerTurn = playerTurn % 2 + 1;
        curPlayer = playerTurn == 1 ? player1 : player2;
        turn++;
        if (turn == 2) {
            setCurPlayerPokemon();
        } else {
            gui.removeAllButtons();

            if (!gameOver()) {
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
            selectedEnergy = (Energy)lastSelectedCard;
            displayPossiblePokemon();
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

    private void attack() {
        Pokemon activePokemon = (Pokemon) curPlayer.getActivePokemon();
        ArrayList<Attack> attacks = activePokemon.attacks;
        String pokemonAttackMessage = activePokemon.getName() + ":\n";

        for(Attack a: attacks) {
            pokemonAttackMessage += "\n" + a.name
                    + "\n - Cost:  \n" + a.getCosts()
                    + "\n - Damage:  " + a.damage + "\n";
        }

        gui.displayMessage(pokemonAttackMessage);

        //let player choose attack\
        if (curPlayer.canAttack()) {
            gui.removeAllButtons();
            gui.displayAttacks(attacks, this::sendAttack, "Select Attack");

            //make sure Pokémon has enough energy to attack

        }


        //conduct attack




//
//            // TODO: the rest of the attack code
//            defendingPlayer.takeDamage(2, 'a');
//            gui.displayMessage(defendingPlayer.getName() + " has been attacked!\nThey have " + defendingPlayer.getActiveHP() + " HP remaining");
//            passTurn();
//        } else {
//            gui.displayMessage("You cannot attack right now");
//        }
        gui.removeAllButtons();
        mainGameLoop();
    }

    private void sendAttack() {
        Attack lastSelectedAttack = gui.getLastSelectedAttack();
        Pokemon actvPokemon = (Pokemon) curPlayer.getActivePokemon();

        if(curPlayer.equals(player1)) {
            Player defendingPlayer = player2;
        } else {
            Player defendingPlayer = player1;
        }

        if(actvPokemon.energies.contains(lastSelectedAttack.costs)) {
            defendingPlayer.takeDamage(lastSelectedAttack.damage, actvPokemon.type);

        }
    }

    private boolean gameOver() {
        // TODO: test win conditions here, not for M3
        return false;
    }

    private boolean isBasicPokemon(Card card) {
        return card instanceof Pokemon && ((Pokemon) card).stage == 0;
    }

    public static void main(String[] args) {
        Game game = new Game(new GameGUI(), new Random(), new Player("Player 1"), new Player("Player 2"));
    }
}