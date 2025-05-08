package main;

import java.util.Random;

public class Main {
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