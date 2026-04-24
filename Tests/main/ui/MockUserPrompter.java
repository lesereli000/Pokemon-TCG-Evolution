package main.ui;

import java.awt.Component;

public class MockUserPrompter implements UserPrompter {
    private String lastMessage;

    @Override
    public void showMessage(Component parent, String message) {
        this.lastMessage = message;
        // Do not block or open UI
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
