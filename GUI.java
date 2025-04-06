import java.awt.*;
import java.util.ArrayList;

public interface GUI {
    void createFlipButton();
    void removeButton();
    void setDeckColor(Color deckColor);
    void displayMessage(String message);
    void setFlipCoinListener(Runnable flipCoinListener);
    void makeActiveCard(Card newActive);
    void setLastSelectedCard(Card card);

    Card getLastSelectedCard();

    void displayPossibleActiveCards(ArrayList<Card> currentCards, Runnable makeActiveCard);
}
