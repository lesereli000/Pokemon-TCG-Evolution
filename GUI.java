import java.awt.*;
import java.util.ArrayList;

public interface GUI {
    void createFlipButton();
    void removeButton();
    void setDeckColor(Color deckColor);
    void displayMessage(String message);
    void setFlipCoinListener(Runnable flipCoinListener);
    void makeActiveCard(Card newActive);
    void displayPossibleActiveCards(ArrayList<Card> currentCards);
}
