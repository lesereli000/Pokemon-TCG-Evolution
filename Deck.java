import java.util.ArrayList;

public class Deck {

    ArrayList<Pokemon> cards = new ArrayList<>();

    public Deck() {
        //Actually create deck here
    }

    int size = 0;

    public int size() {
        return size;
    }

    public void addCard(Pokemon p) {
        cards.add(p);
        size++;
    }

    public ArrayList<Pokemon> getCards() {
        return cards;
    }

    public static void main(String[] args) {
        GUI gui = new GUI();
        gui.createGUI();
    }

}
