package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.*;

//https://stackoverflow.com/questions/601274/how-do-i-properly-load-a-bufferedimage-in-java

public class GameGUI implements GUI {

    static final int frameWidth = 1200;
    static final int frameHeight = frameWidth * 3 / 4;
    static final int frameXLoc = 0;
    static final int frameYLoc = 0;
    static final int origNumPrizeCards = Player.PRIZE_CARD_SIZE;

    private JFrame frame;
    private BoardPanel handPanel;
    private BoardPanel decisionPanel;
    
    private CardDropZoneDetector dropZoneDetector;

    private Color deckColor = Color.WHITE;
    private Color player1ActiveColor = Color.WHITE;
    private Color player2ActiveColor = Color.WHITE;

    private Player player1;
    private Player player2;

    private ArrayList<JButton> buttons = new ArrayList<>();
    private ArrayList<JButton> selectedCardActionButtons = new ArrayList<>();

    private final Font boldFont = new Font("Arial", Font.BOLD, 16);
    private final Font plainFont = new Font("Arial", Font.PLAIN, 12);

    private volatile boolean waitForAction = false;
    private boolean activeTurn = false;
    private boolean confirmPokemonState = false;
    private volatile Card lastSelectedCard = null;
    private JButton lastSelectedButton = null;
    private Attack lastSelectedAttack;
    private volatile String lastActionButtonPressed;
    private int playerTurn = 0;
    private boolean cancelled;
    private Locale locale;
    private ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
    private BufferedImage flag = null;

    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }
    public int getPlayerTurn() { return playerTurn; }
    public Color getDeckColor() { return deckColor; }
    public Color getPlayer1ActiveColor() { return player1ActiveColor; }
    public Color getPlayer2ActiveColor() { return player2ActiveColor; }
    public Font getBoldFont() { return boldFont; }
    public Font getPlainFont() { return plainFont; }
    public ResourceBundle getMessages() { return messages; }
    public BufferedImage getFlag() { return flag; }
    public int getNumBenchCards() { return Player.MAX_BENCH_SIZE; }
    public JFrame getFrame() { return frame; }

    public void createGUI() {
        // Creating the JFrame
        frame = new JFrame();
        String title = messages.getString("title");
        frame.setTitle(title);
        frame.setSize(frameWidth, frameHeight);
        frame.setLocation(frameXLoc, frameYLoc);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BoardPositionMap map = new BoardPositionMap(frameWidth, frameHeight);
        this.dropZoneDetector = new CardDropZoneDetector(map);

        frame.setLayout(new BorderLayout());
        this.handPanel = new BoardPanel(this);
        frame.add(handPanel, BorderLayout.CENTER);
        this.decisionPanel = new BoardPanel(this);
        frame.add(decisionPanel, BorderLayout.SOUTH);

        DropZoneHighlightGlassPane glass = new DropZoneHighlightGlassPane(this.dropZoneDetector);
        frame.setGlassPane(glass);

        frame.setVisible(true);
        setDeckColor(Color.RED);
    }

    @Override
    public void setPlayers(Player p1, Player p2) {
        this.player1 = p1;
        this.player2 = p2;
    }

    public void createFlipButton() {
        String message = messages.getString("flipCoin");
        createSDHoldingButton(message);
        while (!waitForAction) {
            Thread.onSpinWait();
        }
        this.waitForAction = false;
    }

    @Override
    public void waitForPassTurn() {
        createPassTurnButton();
        while (!waitForAction) {
            Thread.onSpinWait();
        }
        this.waitForAction = false;
    }

    public void removeButton(JButton button) {
        if (decisionPanel.isAncestorOf(button)) {
            decisionPanel.remove(button);
            decisionPanel.revalidate();
            decisionPanel.repaint();
        } else if (handPanel.isAncestorOf(button)) {
            handPanel.remove(button);
            handPanel.revalidate();
            handPanel.repaint();
        }

        buttons.remove(button);
    }

    public void setDeckColor(Color deckColor) {
        this.deckColor = deckColor;
        frame.repaint();
    }

    public void displayMessage(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }

    @Override
    public void makeActiveCard(Player player, Card newActive) {
        if (player == player1) {
            player1ActiveColor = Color.GREEN;
        } else if (player == player2) {
            player2ActiveColor = Color.GREEN;
        }
        frame.repaint();
    }

    @Override
    public void addBenchCard(Player player, Card newBench) {
        frame.repaint();
    }

    @Override
    public void removeBenchCard(Player player, Card card) {
        frame.repaint();
    }

    public void refreshGUI() {
        frame.repaint();
    }

    private void setLastSelectedCard(Card card) {
        this.lastSelectedCard = card;
    }

    public void setLastSelectedCardForDrag(Card card) {
        this.lastSelectedCard = card;
        this.lastSelectedAttack = null;
    }

    public void triggerSimulatedAction(String action) {
        this.lastActionButtonPressed = action;
        this.waitForAction = true;
    }

    private void setLastSelectedAttack(Attack attack) {
        this.lastSelectedAttack = attack;
    }

    public Attack getLastSelectedAttack() {
        Attack lastAttack = this.lastSelectedAttack;
        this.lastSelectedAttack = null;
        return lastAttack;
    }

    @Override
    public Card getLastSelectedCard() {
        if (this.lastSelectedCard == null) {
            return new Pokemon("null", "Grass", 0, 10);
        }
        Card lastCard = this.lastSelectedCard;
        this.lastSelectedCard = null;
        return lastCard;
    }

    public void setupActivePokemon() {
        lastSelectedCard = null;
        activeTurn = false;
    }

    @Override
    public void displayConfirmAndCancelButton() {
        String message = messages.getString("confirmSelection");
        JButton btn = new JButton(message);
        this.confirmPokemonState = true;
        btn.addActionListener(e -> {
            cancelled = false;
            this.waitForAction = true;
            this.confirmPokemonState = false;
        });
        String msg = messages.getString("cancel");
        JButton cancel = new JButton(msg);
        cancel.addActionListener(e -> {
            this.waitForAction = true;
            this.confirmPokemonState = false;
            cancelled = true;
        });
        buttons.add(btn);
        buttons.add(cancel);
        decisionPanel.add(btn);
        decisionPanel.add(cancel);
        decisionPanel.repaint();
        frame.revalidate();
        frame.repaint();
    }

    @Override
    public void displayCardReport(Card card) {
        displayMessage(card.getReport(messages));
    }

    @Override
    public void displayPossibleAttacks(ArrayList<Attack> attacks) {
        String attackReport = generateAttackReport(attacks);
        JOptionPane.showMessageDialog(frame, attackReport);
        for (Attack attack : attacks) {
            createLinkedButtonAttack(attack);
        }
    }

    @Override
    public void displayAttackMessage(Player currentPlayer, Player defendingPlayer, Attack attack) {
        StringBuilder attackReport = new StringBuilder();
        String currentName = currentPlayer.getName();
        String defendingName = defendingPlayer.getName();
        Pokemon currentPokemon = currentPlayer.activePokemon;
        Pokemon defendingPokemon = defendingPlayer.activePokemon;

        String atkReport = messages.getString("atkReport");
        attackReport.append(atkReport).append("\n");

        String pokemonName = messages.getString("pokemonName");
        pokemonName = MessageFormat.format(pokemonName, currentName, currentPokemon.getName());
        attackReport.append(pokemonName).append("\n");

        String attacked = messages.getString("attacked");
        attacked = MessageFormat.format(attacked, defendingName, defendingPokemon.getName());
        attackReport.append(attacked).append("\n\n");

        String tookDmg = messages.getString("tookDmg");
        tookDmg = MessageFormat.format(tookDmg, defendingName, attack.getDamage());
        attackReport.append(tookDmg).append("\n");

        String newHp = messages.getString("newHp");
        newHp = MessageFormat.format(newHp, defendingName, defendingPokemon.getName(), defendingPokemon.getCurHP());
        attackReport.append(newHp);

        displayMessage(attackReport.toString());
    }

    @Override
    public void displayRetreatEnergy(Pokemon pokemon, boolean canRetreat) {
        StringBuilder retreatReport = new StringBuilder();
        String retreatMessageTop = messages.getString("retreatMessageTop");
        retreatMessageTop = MessageFormat.format(retreatMessageTop, pokemon.retreatCost);
        String retreatMessageBottom = messages.getString("retreatMessageBottom");
        retreatMessageBottom = MessageFormat.format(retreatMessageBottom, pokemon.getName());
        retreatReport.append(retreatMessageTop).append("\n").append(retreatMessageBottom).append("\n");

        if (!canRetreat) {
            String cantRetreat = messages.getString("cantRetreat");
            retreatReport.append("\n").append(cantRetreat);
            displayMessage(retreatReport.toString());
        } else {
            displayMessage(retreatReport.toString());
        }
    }

    @Override
    public void replaceActiveCard(Player player, Card selectedCard) {
        frame.repaint();
    }

    @Override
    public void displayDeadActiveInfo(Player defendingPlayer) {
        StringBuilder deadPokemonReport = new StringBuilder();
        String deadName = defendingPlayer.getActivePokemonName();
        String pokemonDied = messages.getString("pokemonDied");
        pokemonDied = MessageFormat.format(pokemonDied, defendingPlayer.getName(), deadName);
        deadPokemonReport.append(pokemonDied).append("\n");
        String selectNewActive = messages.getString("selectNewActive");
        deadPokemonReport.append(selectNewActive);
        displayMessage(deadPokemonReport.toString());
    }

    @Override
    public void displayWinningMessage(Player winner, Player loser) {
        String winnerName = winner.getName();
        String loserName = loser.getName();
        String winningMessageTop = messages.getString("winningMessageTop");
        String winningMessageBottom = messages.getString("winningMessageBottom");
        winningMessageTop = MessageFormat.format(winningMessageTop, winnerName);
        winningMessageBottom = MessageFormat.format(winningMessageBottom, loserName);
        displayMessage(winningMessageTop + "\n" + winningMessageBottom);
    }

    @Override
    public boolean hasCardSelected() {
        return lastSelectedCard != null;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public Locale displayLocaleOptions() {
        displayMessage("Select a language!\n\nWählen Sie eine Sprache aus!");
        JButton engBtn = new JButton("English");
        JButton germanBtn = new JButton("Deutsch");

        buttons.add(engBtn);
        handPanel.add(engBtn);
        buttons.add(germanBtn);
        handPanel.add(germanBtn);
        handPanel.repaint();
        frame.revalidate();
        frame.repaint();

        engBtn.addActionListener(e -> {
            this.waitForAction = true;
            locale = Locale.US;
            messages = ResourceBundle.getBundle("MessagesBundle", locale);
            try {
                flag = ImageIO.read(getClass().getResource("/USFlag.png"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            removeButton(engBtn);
            removeButton(germanBtn);
        });

        germanBtn.addActionListener(e -> {
            this.waitForAction = true;
            locale = Locale.GERMANY;
            messages = ResourceBundle.getBundle("MessagesBundle", locale);
            try {
                flag = ImageIO.read(getClass().getResource("/deutschflag.png"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            removeButton(engBtn);
            removeButton(germanBtn);
        });

        waitForButtonPressed();
        frame.repaint();
        return locale;
    }

    @Override
    public boolean gameIsOver() {
        return buttons.isEmpty();
    }

    public String generateAttackReport(ArrayList<Attack> attacks) {
        StringBuilder report = new StringBuilder();
        for (Attack attack : attacks) {
            report.append(attack.getReport(messages));
        }
        return report.toString();
    }

    @Override
    public void waitForAction() {
        while (!waitForAction) {
            Thread.onSpinWait();
        }
        this.waitForAction = false;
    }

    @Override
    public void displayCards(ArrayList<Card> playerCards) {
        for (Card currCard : playerCards) {
            String cardName = currCard.getName();
            createLinkedButtonCard(cardName, currCard);
        }
    }

    @Override
    public void closeWindow() {
        frame.dispose();
    }

    @Override
    public void removePrizeCard(Player player) {
        frame.repaint();
    }

    private void displayPokemonActionButtons(Pokemon card) {
        removeSelectedCardActionButtons();
        if (card.stage == 0) {
            String addPokBench = messages.getString("addPokBench");
            selectedCardActionButtons.add(createLinkedButtonAction(addPokBench, "AddToBench"));
        } else {
            String evolveToPok = messages.getString("evolveToPok");
            selectedCardActionButtons.add(createLinkedButtonAction(evolveToPok, "Evolve"));
        }
    }

    private void displayEnergyActionButtons(Energy energy) {
        removeSelectedCardActionButtons();
        String addEnergy = messages.getString("addEnergy");
        selectedCardActionButtons.add(createLinkedButtonAction(addEnergy, "AddEnergy"));
    }

    private void displayTrainerActionButtons(Trainer trainer) {
        removeSelectedCardActionButtons();
        String playTrainer = messages.getString("playTrainer");
        selectedCardActionButtons.add(createLinkedButtonAction(playTrainer, "PlayTrainer"));
    }

    private void displayActiveActionButton() {
        removeSelectedCardActionButtons();
        String actvSelectedPok = messages.getString("actvSelectedPok");
        selectedCardActionButtons.add(createLinkedButtonAction(actvSelectedPok, "AddToBench"));
    }

    private void removeSelectedCardActionButtons() {
        if (!selectedCardActionButtons.isEmpty()) {
            for (int i = 0; i < selectedCardActionButtons.size(); i++) {
                JButton btn = selectedCardActionButtons.get(i);
                removeButton(btn);
                selectedCardActionButtons.remove(btn);
            }
        }
    }

    @Override
    public String waitForButtonPressed() {
        while (!waitForAction) {
            Thread.onSpinWait();
        }
        this.waitForAction = false;
        return this.lastActionButtonPressed;
    }

    @Override
    public void displayActionButtons() {
        activeTurn = true;
        String passTurn = messages.getString("passTurn");
        String atkOpp = messages.getString("atkOpp");
        String retreatPok = messages.getString("retreatPok");
        String seeCardInfo = messages.getString("seeCardInfo");
        createLinkedButtonAction(passTurn, "PassTurn");
        createLinkedButtonAction(atkOpp, "Attack");
        createLinkedButtonAction(retreatPok, "Retreat");
        createLinkedButtonAction(seeCardInfo, "CardInfo");
    }

    @Override
    public JButton createButton(String message) {
        JButton btn = new JButton(message);
        btn.addActionListener(e -> {
        });
        buttons.add(btn);
        decisionPanel.add(btn);
        decisionPanel.repaint();
        frame.revalidate();
        frame.repaint();

        return btn;
    }

    @Override
    public JButton createLinkedButtonCard(String message, Card currCard) {
        JButton btn = new JButton(message);
        
        if (dropZoneDetector == null) {
            BoardPositionMap map = new BoardPositionMap(frameWidth, frameHeight);
            this.dropZoneDetector = new CardDropZoneDetector(map);
        }
        
        GhostingDragAdapter dragAdapter = new GhostingDragAdapter(this, currCard, dropZoneDetector);
        btn.addMouseListener(dragAdapter);
        btn.addMouseMotionListener(dragAdapter);
        
        String url = currCard.getImageUrl();
        if (url != null) {
            // Scaling constants from BoardPanel
            int width = (frameWidth * 2) / 25;
            int height = width * 7 / 5;
            ImageLoader.loadIntoButton(url, btn, width, height);
            btn.setPreferredSize(new Dimension(width, height));
        }

        btn.addActionListener(e -> {
            if (lastSelectedButton != null) {
                lastSelectedButton.setBackground(Color.WHITE);
            }
            btn.setBackground(Color.GREEN);
            lastSelectedButton = btn;
            if (!getLastSelectedCard().equals(currCard)) {
                setLastSelectedCard(currCard);
                if (!confirmPokemonState) {
                    if (activeTurn) {
                        if (currCard instanceof Pokemon) {
                            displayPokemonActionButtons((Pokemon) currCard);
                        }
                        if (currCard instanceof Energy) {
                            displayEnergyActionButtons((Energy) currCard);
                        }
                        if (currCard instanceof Trainer) {
                            displayTrainerActionButtons((Trainer) currCard);
                        }
                    } else {
                        if (currCard instanceof Pokemon && ((Pokemon) currCard).stage == 0) {
                            displayActiveActionButton();
                        } else {
                            removeSelectedCardActionButtons();
                        }
                    }
                }
            }
        });
        buttons.add(btn);
        handPanel.add(btn);
        handPanel.repaint();
        frame.revalidate();
        frame.repaint();

        return btn;
    }

    private JButton createLinkedButtonAttack(Attack currAttack) {
        JButton btn = new JButton(currAttack.name);
        btn.addActionListener(e -> {
            setLastSelectedAttack(currAttack);
        });
        buttons.add(btn);
        handPanel.add(btn);
        handPanel.repaint();
        frame.revalidate();
        frame.repaint();

        return btn;
    }

    private JButton createLinkedButtonAction(String name, String action) {
        JButton btn = new JButton(name);
        btn.addActionListener(e -> {
            this.lastActionButtonPressed = action;
            waitForAction = true;
        });
        buttons.add(btn);
        decisionPanel.add(btn);
        decisionPanel.repaint();
        frame.revalidate();
        frame.repaint();

        return btn;
    }

    @Override
    public JButton createSDHoldingButton(String message) {
        JButton btn = new JButton(message);
        btn.addActionListener(e -> {
            this.waitForAction = true;
            removeButton(btn);
        });
        buttons.add(btn);
        handPanel.add(btn);
        handPanel.repaint();
        frame.revalidate();
        frame.repaint();

        return btn;
    }

    @Override
    public JButton createPassTurnButton() {
        String message = messages.getString("passTurn");
        JButton btn = new JButton(message);
        btn.addActionListener(e -> {
            this.waitForAction = true;
            removeButton(btn);
        });
        buttons.add(btn);
        handPanel.repaint();
        frame.revalidate();
        frame.repaint();

        return btn;

    }

    @Override
    public void removeAllButtons() {
        for (JButton btn : buttons) {
            handPanel.remove(btn);
            handPanel.revalidate();
            handPanel.repaint();
            decisionPanel.remove(btn);
            decisionPanel.revalidate();
            decisionPanel.repaint();
        }
        buttons = new ArrayList<>();
    }

    @Override
    public void retreat(Player player, Card newCard) {
        frame.repaint();
    }

    @Override
    public void updateTurn(int playerTurn) {
        this.playerTurn = playerTurn;
        this.lastSelectedCard = null;
        this.lastSelectedAttack = null;

        handPanel.repaint();
        decisionPanel.repaint();
        frame.repaint();
    }
}