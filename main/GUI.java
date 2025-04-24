package main;

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
    Attack getLastSelectedAttack();
    void displayCards(ArrayList<Card> currentCards, Runnable makeActiveCard, String submitMessage);
    JButton createButton(String message, Runnable toRun);
    JButton createLinkedButtonCard(String message, Card currCard);
    JButton createSelfDestructingButton(String message, Runnable toRun);
    void addBenchCard(Card newBench, int playerTurn);
    void removeBenchCard(Card newBench, int playerTurn);
    void removeAllButtons();
    void retreat(Card newCard, int playerTurn);

    void updateTurn(int playerTurn);

    void displayAttacks(ArrayList<Attack> attacks, Runnable makeActiveCard, String submitMessage);

    void closeWindow();
}
