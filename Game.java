public class Game {

    Deck deck1 = new Deck();
    Deck deck2 = new Deck();
    static GUI gui;

    public static void main(String[] args) {
        gui = new GUI();
        gui.createGUI();
    }

    public int flipCoin() {
        return Math.random() < 0.5 ? 1 : 0;
    }
}


