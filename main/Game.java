package main;

import java.util.Random;

public class Game {
    protected GUI gui;
    protected Random random;
    protected Player player1;
    protected Player player2;
    public Game(GUI gui) {
        this.gui = gui;
        this.random = new Random();
        gui.createGUI();
    }

    protected void setupGame() {
        gui.createFlipButton();
        String coinFlipResult = flipCoin(random);
        createPlayers();
        setupBothDecks();
        setupBothHands();
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



    public static void main(String[] args) {
        GameGUI gui = new GameGUI();
        Game game = new Game(gui);
        game.setupGame();
    }



}
