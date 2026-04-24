package main.ui;

import java.awt.Component;
import javax.swing.JOptionPane;

public class DefaultUserPrompter implements UserPrompter {
    @Override
    public void showMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message);
    }
}
