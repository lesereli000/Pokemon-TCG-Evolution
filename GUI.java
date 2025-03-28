import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public interface GUI {
    void createFlipButton(String flipResult);
    void removeButton();
    void setDeckColor(Color deckColor);
    void displayMessage(String message);
}
