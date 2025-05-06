package main;

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

}
