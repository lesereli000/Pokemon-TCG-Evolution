package main.ui;

import main.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;

public interface GUI {
    void createGUI();
    void createFlipButton();
    void setPlayers(Player p1, Player p2);

    void waitForPassTurn();

    void removeButton(JButton button);
    void setDeckColor(Color deckColor);
    void displayMessage(String message);
    void makeActiveCard(Player player, Card newActive);
    Card getLastSelectedCard();
    void setLastSelectedCardForDrag(Card card);
    Attack getLastSelectedAttack();
    void displayCards(ArrayList<Card> currentCards);
    JButton createButton(String message);
    JButton createLinkedButtonCard(String message, Card currCard);
    JButton createSDHoldingButton(String message);
    void addBenchCard(Player player, Card newBench);
    void removeBenchCard(Player player, Card newBench);
    JButton createPassTurnButton();
    boolean cancelled = false;

    void removeAllButtons();
    void retreat(Player player, Card newCard);

    void updateTurn(int playerTurn);

    void closeWindow();

    void removePrizeCard(Player player);

    String waitForButtonPressed();

    void displayActionButtons();

    void setupActivePokemon();

    void waitForAction();

    void displayConfirmAndCancelButton();

    void displayCardReport(Card card);

    void displayPossibleAttacks(ArrayList<Attack> attacks);

    void displayAttackMessage(Player currentPlayer, Player defendingPlayer, Attack attack);

    void displayRetreatEnergy(Pokemon pokemon, boolean canRetreat);

    void replaceActiveCard(Player player, Card selectedCard);

    void displayDeadActiveInfo(Player defendingPlayer);

    void displayWinningMessage(Player winner, Player loser);

    boolean hasCardSelected();

    boolean isCancelled();

    Locale displayLocaleOptions();

    String displayDeckOptions();

    boolean gameIsOver();

    void refreshGUI();
}
