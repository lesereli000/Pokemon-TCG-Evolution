package main;

import java.util.Random;

public class Game {
    protected GUI gui;
    public Game(GUI gui) {
        this.gui = gui;
        gui.createGUI();
    }

    public void setupGame() {
        gui.createFlipButton();
    }

    public static void main(String[] args) {
        GameGUI gui = new GameGUI();
        new Game(gui);
    }

    public String flipCoin(Random rand) {
        boolean randomBoolean = rand.nextBoolean();
        if(randomBoolean) {
            return "Heads";
        }
        return "Tails";
    }
}
