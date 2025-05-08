package main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public interface GUI {
    void createGUI();
    void createFlipButton();

    void waitForPassTurn();

    void removeButton(JButton button);
    void setDeckColor(Color deckColor);
    void displayMessage(String message);
    void makeActiveCard(Card newActive, int playerTurn);
    Card getLastSelectedCard();
    Attack getLastSelectedAttack();
    void displayCards(ArrayList<Card> currentCards);
    JButton createButton(String message);
    JButton createLinkedButtonCard(String message, Card currCard);
    JButton createSDHoldingButton(String message);
    void addBenchCard(Card newBench, int playerTurn);
    void removeBenchCard(Card newBench, int playerTurn);
    JButton createPassTurnButton();

    void removeAllButtons();
    void retreat(Card newCard, int playerTurn);

    void updateTurn(int playerTurn);

    void displayAttacks(ArrayList<Attack> attacks, String submitMessage);

    void closeWindow();

    void removePrizeCard(int playerNum);

    String waitForButtonPressed();

    void displayActionButtons();

    void setupActivePokemon();

    void waitForPokemonSelected();

    void displayConfirmButton();

    void displayPokemonReport(Pokemon pokemon);

    void displayCardReport(Card card);
}
