package main;

import main.ui.*;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        GameGUI gui = new GameGUI();
        Random random = new Random();
        SetupGame gameSetup = new SetupGame(random);
        PlayerHandler playerHandler = new PlayerHandler();
        Game game = new Game(gui, gameSetup, playerHandler);
        startGame(gui, game);
    }

    static void startGame(GameGUI gui, Game game){
        gui.createGUI();
        game.setupGame();
    }
}