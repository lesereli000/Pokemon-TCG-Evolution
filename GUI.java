import java.awt.*;

public interface GUI {
    void createFlipButton();
    void removeButton();
    void setDeckColor(Color deckColor);
    void displayMessage(String message);
    void setFlipCoinListener(Runnable flipCoinListener);
}
