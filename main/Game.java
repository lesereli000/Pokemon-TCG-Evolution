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
        setupGame();
    }

    public void setupGame() {
        gui.createFlipButton();
        String coinFlipResult = flipCoin(random);
        createPlayers();
    }

    public String flipCoin(Random rand) {
        boolean randomBoolean = rand.nextBoolean();
        if(randomBoolean) {
            return "Heads";
        }
        return "Tails";
    }

    public static void main(String[] args) {
        GameGUI gui = new GameGUI();
        new Game(gui);
    }

    public void createPlayers() {
        player1 = new Player("Player 1");
        player2 = new Player("Player 2");
    }
}
