import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public interface GUI {
    void createFlipButton(Runnable flipListener);
    void removeButton(JButton button);
    void setDeckColor(Color deckColor);
    void displayMessage(String message);
    void makeActiveCard(Card newActive, int playerTurn);

    Card getLastSelectedCard();

    void displayPossibleActiveCards(ArrayList<Card> currentCards, Runnable makeActiveCard);

    JButton createButton(String message, Runnable toRun);

    JButton createButton(String message, Runnable toRun, Card currCard);

    JButton createSelfDestructingButton(String message, Runnable toRun);
}
