package main;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * A testing harness for GameGUI to facilitate systems-level and automated GUI testing.
 * Provides reflective access to internal components and utility methods to interact
 * with the GUI asynchronously, which is necessary due to GameGUI's blocking spin loops.
 */
public class GameGUITestHarness {
    private GameGUI gui;

    public GameGUITestHarness(GameGUI gui) {
        this.gui = gui;
    }

    public GameGUI getGUI() {
        return gui;
    }

    /**
     * Retrieves the internal JFrame of the GameGUI.
     */
    public JFrame getFrame() throws Exception {
        Field frameField = GameGUI.class.getDeclaredField("frame");
        frameField.setAccessible(true);
        return (JFrame) frameField.get(gui);
    }

    /**
     * Retrieves the internal list of buttons managed by GameGUI.
     */
    @SuppressWarnings("unchecked")
    public ArrayList<JButton> getButtons() throws Exception {
        Field buttonsField = GameGUI.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        return (ArrayList<JButton>) buttonsField.get(gui);
    }

    /**
     * Finds a button by its exact display text.
     */
    public JButton getButtonWithText(String text) throws Exception {
        for (JButton btn : getButtons()) {
            if (btn.getText().equals(text)) {
                return btn;
            }
        }
        return null;
    }

    /**
     * Clicks a button asynchronously after a short delay.
     * This is required for methods in GameGUI that block the caller thread (like waitForAction).
     *
     * @param text The text of the button to click.
     * @param delayMs The delay in milliseconds before attempting to click.
     */
    public void clickButtonAsync(String text, long delayMs) {
        new Thread(() -> {
            try {
                Thread.sleep(delayMs);
                JButton btn = getButtonWithText(text);
                if (btn != null) {
                    SwingUtilities.invokeLater(() -> btn.doClick());
                } else {
                    System.err.println("GameGUITestHarness: Button not found: " + text);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Clicks a button synchronously. To be used when the main thread isn't blocked.
     */
    public void clickButtonSync(String text) throws Exception {
        JButton btn = getButtonWithText(text);
        if (btn != null) {
            SwingUtilities.invokeAndWait(() -> btn.doClick());
        } else {
            throw new Exception("Button not found: " + text);
        }
    }

    /**
     * Asynchronously closes any active JDialog (e.g. JOptionPane).
     * Necessary because JOptionPane blocks the current thread.
     */
    public void dismissDialogAsync(long delayMs) {
        new Thread(() -> {
            try {
                Thread.sleep(delayMs);
                SwingUtilities.invokeLater(() -> {
                    Window[] windows = Window.getWindows();
                    for (Window window : windows) {
                        if (window instanceof JDialog && window.isVisible()) {
                            window.dispose();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
