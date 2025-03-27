import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public interface GUI {
    void addButton(ActionListener listener);
    void removeButton();
    void setDeckColor(Color deckColor);
    void displayMessage(String message);
}
