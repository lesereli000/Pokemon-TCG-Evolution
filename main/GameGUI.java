package main;

import com.sun.source.doctree.EndElementTree;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;


public class GameGUI implements GUI {

    static final int frameWidth = 1200;
    static final int frameHeight = frameWidth * 3 / 4;
    static final int frameXLoc = 0;
    static final int frameYLoc = 0;
    static final int origNumPrizeCards = 6;

    private JFrame frame;
    private GamePanel handPanel;
    private GamePanel decisionPanel;
    private JPanel actionPanel;

    static final int backgroundLineThickness = 4;

    static final int cardWidth = (frameWidth*2) / 25;
    static final int cardHeight = cardWidth * 7 / 5;

    static final int marginSide = 40;
    static final int marginTop = 80;
    static final int marginBottom = 75;

    static final int marginPrizeCardVertical = 15;
    static final int prizeCardsOffset = cardWidth / 2;
    static final int pcVerticalOffset = cardHeight / 15;
    static final int benchHorizontalOffset = frameWidth / 19;
    static final int benchHorizontalIncrement = cardHeight / 6;
    static final int benchVerticalOffset = frameHeight / 8;
    static final int activeVerticalOffset = frameHeight / 16;
    static final int activeVerticalMargin = cardHeight / 16;
    static final int deckOffset = 15;
    static final int numBenchCards = 5;

    private Card player1activeCard;
    private Card player2activeCard;

    private ArrayList<Card> player1benchCards = new ArrayList<Card>();
    private ArrayList<Card> player2benchCards = new ArrayList<Card>();

    private Color deckColor = Color.WHITE;
    private Color player1ActiveColor = Color.WHITE;
    private Color player2ActiveColor = Color.WHITE;

    private Color[] player1PrizeCards = new Color[origNumPrizeCards];
    private Color[] player2PrizeCards = new Color[origNumPrizeCards];

    private ArrayList<JButton> buttons = new ArrayList<>();
    private ArrayList<JButton> selectedCardActionButtons = new ArrayList<>();

    private Font boldFont = new Font("Arial", Font.BOLD, 16);
    private Font plainFont = new Font("Arial", Font.PLAIN, 12);


    private volatile boolean waitForAction = false;
    private boolean activeTurn = false;
    private boolean confirmPokemonState = false;
    private volatile Card lastSelectedCard = null;
    private JButton lastSelectedButton = null;
    private Attack lastSelectedAttack;
    private volatile String lastActionButtonPressed;
    private int playerTurn = 0;


    private class GamePanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Cast Graphics to Graphics2D
            Graphics2D g2d = (Graphics2D) g;

            // Set stroke thickness to 5 pixels
            g2d.setStroke(new BasicStroke(backgroundLineThickness));

            //background
            Color backgroundBlue = new Color(37, 150, 190);
            g2d.setColor(backgroundBlue);
            g2d.fillRect(0, 0, frameWidth, frameHeight);

            g2d.setColor(Color.WHITE);
            g2d.setFont(boldFont);
            g2d.drawString("Player 1", (marginSide*7)/4 + (cardWidth * 2) + benchHorizontalOffset + (2 * (benchHorizontalIncrement + cardWidth)), frameHeight -  (cardHeight*8)/7 - marginBottom - benchVerticalOffset);
            g2d.drawString("Player 2", frameWidth - (marginSide * 5) / 4 - (cardWidth * 3) - benchHorizontalOffset - (2 * (benchHorizontalIncrement + cardWidth)), marginTop + (cardHeight*17)/14);

            // ----- USER SIDE (NEAR/BOTTOM SIDE) --------
            if (playerTurn != 0) {
                g2d.drawString("Player turn: " + playerTurn, marginSide / 2, frameHeight - marginTop);
            }
            g2d.setFont(plainFont);

            //Prize Cards
            //left column
            for (int i = 0; i < 3; i++) {
                Color currColor = player1PrizeCards[i];
                g2d.setColor(currColor);
                g2d.drawRect(marginSide, frameHeight - (cardHeight * (i + 1)) - marginBottom - marginPrizeCardVertical * i, cardWidth, cardHeight);
            }

            //right column (front of left column)
            g2d.setColor(backgroundBlue);
            g2d.fillRect(marginSide + prizeCardsOffset, frameHeight - cardHeight - marginBottom - pcVerticalOffset, cardWidth, cardHeight);
            g2d.fillRect(marginSide + prizeCardsOffset, frameHeight - (cardHeight * 2) - marginBottom - marginPrizeCardVertical - pcVerticalOffset, cardWidth, cardHeight);
            g2d.fillRect(marginSide + prizeCardsOffset, frameHeight - (cardHeight * 3) - marginBottom - (marginPrizeCardVertical * 2) - pcVerticalOffset, cardWidth, cardHeight);

            for (int i = 0; i < 3; i++) {
                Color currColor = player1PrizeCards[i + 3];
                g2d.setColor(currColor);
                g2d.drawRect(marginSide + prizeCardsOffset, frameHeight - cardHeight * (i + 1) - marginBottom - marginPrizeCardVertical * i - pcVerticalOffset, cardWidth, cardHeight);
            }

            //Bench Cards
            g2d.setColor(Color.WHITE);
            for (int i = 0; i < numBenchCards; i++) {
                g2d.drawRect((marginSide*3)/2 + (cardWidth * 2) + benchHorizontalOffset + (i * (benchHorizontalIncrement + cardWidth)), frameHeight - cardHeight - marginBottom - benchVerticalOffset, cardWidth, cardHeight);
                if (player1benchCards.size() > i) {
                    Card currentCard = player1benchCards.get(i);
                    g2d.drawString(currentCard.name, marginSide + (cardWidth * 2) + benchHorizontalOffset + (i * (benchHorizontalIncrement + cardWidth)) + (cardWidth / 3), frameHeight - cardHeight - marginBottom - benchVerticalOffset + (cardHeight / 2));
                }
            }

            //Active Pokemon
            g2d.setColor(player1ActiveColor);
            g2d.drawRect((frameWidth / 2) - (cardWidth / 2), (frameHeight / 2) + activeVerticalMargin - activeVerticalOffset, cardWidth, cardHeight);
            if (player1ActiveColor == Color.GREEN) {
                g2d.drawString("Active Pokemon:", (frameWidth / 2) - (cardWidth / 2) + marginSide / 8, (frameHeight / 2) - activeVerticalOffset + marginTop / 2);
                g2d.drawString(player1activeCard.getName(), (frameWidth / 2) - (cardWidth / 2) + marginSide / 8, (frameHeight / 2) - activeVerticalOffset + marginTop);
            }

            g2d.setColor(Color.WHITE);
            //Discard
            g2d.drawRect(frameWidth - marginSide - cardWidth, frameHeight - marginBottom - cardHeight, cardWidth, cardHeight);

            //Deck
            g2d.setColor(deckColor);
            g2d.drawRect(frameWidth - marginSide - cardWidth, frameHeight - marginBottom - (cardHeight * 2) - deckOffset, cardWidth, cardHeight);
            g2d.setColor(Color.WHITE);

            // ----- STADIUM CARD -------
            g2d.drawRect((frameWidth / 2) - ((cardWidth / 4) * 9), (frameHeight / 2) - (cardHeight / 2) - activeVerticalOffset, cardWidth, cardHeight);


            // ----- OPPONENT SIDE (FAR/TOP SIDE) --------

            //Prize Cards
            //right column (bottom)
            for (int i = 0; i < 3; i++) {
                Color currColor = player2PrizeCards[i];
                g2d.setColor(currColor);
                g2d.drawRect(frameWidth - marginSide - cardWidth, marginTop + (i * (marginPrizeCardVertical + cardHeight)), cardWidth, cardHeight);
            }
            //left column (top of right column)
            g2d.setColor(backgroundBlue);
            for (int i = 0; i < 3; i++) {
                g2d.fillRect(frameWidth - prizeCardsOffset - marginSide - cardWidth, marginTop + (i * (marginPrizeCardVertical + cardHeight)) + pcVerticalOffset, cardWidth, cardHeight);
            }
            for (int i = 0; i < 3; i++) {
                Color currColor = player2PrizeCards[i + 3];
                g2d.setColor(currColor);
                g2d.drawRect(frameWidth - prizeCardsOffset - marginSide - cardWidth, marginTop + (i * (marginPrizeCardVertical + cardHeight)) + pcVerticalOffset, cardWidth, cardHeight);
            }
            g2d.setColor(Color.WHITE);
            //Bench Cards
            for (int i = 0; i < numBenchCards; i++) {
                g2d.drawRect(frameWidth - (marginSide * 3) / 2 - (cardWidth * 3) - benchHorizontalOffset - (i * (benchHorizontalIncrement + cardWidth)), marginTop, cardWidth, cardHeight);
                if (player2benchCards.size() > i) {
                    Card currentCard = player2benchCards.get(i);
                    g2d.drawString(currentCard.name, frameWidth - marginSide - (cardWidth * 3) - benchHorizontalOffset - (i * (benchHorizontalIncrement + cardWidth)) + (cardWidth / 3), marginTop + (cardHeight / 2));
                }
            }

            //Active Pokemon
            g2d.setColor(player2ActiveColor);
            if (player2ActiveColor == Color.GREEN) {
                g2d.drawString("Active Pokemon:", (frameWidth / 2) - (cardWidth / 2) + marginSide / 8, (frameHeight / 2) - activeVerticalMargin - cardHeight - activeVerticalOffset / 4);
                g2d.drawString(player2activeCard.getName(), (frameWidth / 2) - (cardWidth / 2) + marginSide / 8, (frameHeight / 2) - activeVerticalMargin - cardHeight - activeVerticalOffset / 8);
            }
            g2d.drawRect((frameWidth / 2) - (cardWidth / 2), (frameHeight / 2) - activeVerticalMargin - cardHeight - activeVerticalOffset, cardWidth, cardHeight);


            g2d.setColor(Color.WHITE);
            //Discard
            g2d.drawRect(marginSide, marginTop, cardWidth, cardHeight);

            //Deck
            g2d.setColor(deckColor);
            g2d.drawRect(marginSide, marginTop + (cardHeight) + deckOffset, cardWidth, cardHeight);

            //display current player turn
            g2d.setColor(Color.WHITE);
            g2d.setFont(boldFont);
            String turnText = "Player Turn: " + playerTurn;
            FontMetrics metrics = g2d.getFontMetrics(boldFont);
            int textWidth = metrics.stringWidth(turnText);
            g2d.drawString(turnText, (frameWidth*5)/7 - textWidth, frameHeight/2 - (marginTop*2)/3);
        }
    }

    public void createGUI() {
        // Creating the JFrame
        frame = new JFrame();
        frame.setTitle("Pokemon Game");
        frame.setSize(frameWidth, frameHeight);
        frame.setLocation(frameXLoc, frameYLoc);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLayout(new BorderLayout());
        this.handPanel = new GamePanel();
        frame.add(handPanel, BorderLayout.CENTER);
        this.decisionPanel = new GamePanel();
        frame.add(decisionPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
        setDeckColor(Color.RED);
        setInitialPrizeColors();
    }

    private void setInitialPrizeColors() {
        for (int i = 0; i < 6; i++) {
            player1PrizeCards[i] = Color.YELLOW;
            player2PrizeCards[i] = Color.YELLOW;
        }
    }

    public void createFlipButton() {
        createSDHoldingButton("Flip Coin");
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
        if(decisionPanel.isAncestorOf(button)) {
            decisionPanel.remove(button);
            decisionPanel.revalidate();
            decisionPanel.repaint();
        }
        else if(handPanel.isAncestorOf(button)) {
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
    public void makeActiveCard(Card newActive, int playerTurn) {
        if (playerTurn == 1) {
            player1ActiveColor = Color.GREEN;
            this.player1activeCard = newActive;
        } else {
            player2ActiveColor = Color.GREEN;
            this.player2activeCard = newActive;
        }
        frame.repaint();
    }

    @Override
    public void addBenchCard(Card newBench, int playerTurn) {
        if (playerTurn == 1 && this.player1benchCards.size() < 6) {
            this.player1benchCards.add(newBench);
        } else if (playerTurn == 2 && this.player2benchCards.size() < 6) {
            this.player2benchCards.add(newBench);
        }

        frame.repaint();
    }

    @Override
    public void removeBenchCard(Card card, int playerTurn) {
        if (playerTurn == 1 && !this.player1benchCards.isEmpty()) {
            this.player1benchCards.remove(card);
        } else if (playerTurn == 2 && !this.player2benchCards.isEmpty()) {
            this.player2benchCards.remove(card);
        }
        frame.repaint();
    }


    private void setLastSelectedCard(Card card) {
        this.lastSelectedCard = card;
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
        if(this.lastSelectedCard == null) {
            return new Pokemon("null","Grass",0,10);
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
    public void displayConfirmButton() {
        JButton btn = new JButton("Confirm Pokemon Selection");
        this.confirmPokemonState = true;
        btn.addActionListener(e -> {
            this.waitForAction = true;
            this.confirmPokemonState = false;
        });
        buttons.add(btn);
        decisionPanel.add(btn);
        decisionPanel.repaint();
        frame.revalidate();
        frame.repaint();
    }

    @Override
    public void displayCardReport(Card card) {
        if (card instanceof Pokemon) displayPokemonReport((Pokemon) card);
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
        attackReport.append("Attack Report:\n");
        attackReport.append(currentName).append("'s active Pokemon: ").append(currentPokemon.getName()).append("\n");
        attackReport.append("attacked ").append(defendingName).append("'s active Pokemon: ").append(defendingPokemon.getName()).append("\n\n");
        attackReport.append(defendingName).append("'s active Pokemon took ").append(attack.getDamage()).append(" damage\n");
        attackReport.append(defendingName).append("'s active Pokemon: ").append(defendingPokemon.getName())
                .append(" is now at: ").append(defendingPokemon.getCurHP()).append(" hp");
        displayMessage(attackReport.toString());

    }

    @Override
    public void displayRetreatEnergy(Pokemon pokemon, boolean canRetreat) {
        String retreatMessage = "Requires " + pokemon.retreatCost + " Colorless Energy\n" +
                " for " + pokemon.getName() + " to retreat";
        if (!canRetreat) {
            displayMessage(retreatMessage + "\nYou are currently unable to retreat!");
        } else {
            displayMessage(retreatMessage);
        }
    }

    @Override
    public void replaceActiveCard(Card selectedCard, int playerTurn) {
        if (playerTurn == 1) {
            addBenchCard(player1activeCard, playerTurn);
        } else {
            addBenchCard(player2activeCard, playerTurn);
        }
        removeBenchCard(selectedCard, playerTurn);
        makeActiveCard(selectedCard, playerTurn);
    }

	@Override
	public void displayDeadActiveInfo(Player defendingPlayer) {
		StringBuilder deadPokemonReport = new StringBuilder();
		Pokemon deadPokemon = (Pokemon) defendingPlayer.getActivePokemon();
		deadPokemonReport.append(defendingPlayer.getName()).append("'s active Pokemon: ").append(deadPokemon.getName()).append(" has died!\n");
		deadPokemonReport.append("Select a new basic Pokemon to be your new active Pokemon");
		displayMessage(deadPokemonReport.toString());
	}

	@Override
	public void displayWinningMessage(Player winner, Player loser) {
		String winnerName = winner.getName();
		String loserName = loser.getName();
		displayMessage(winnerName + " has won the game! " + "\nSorry " + loserName + ", better luck next time!");
	}

    @Override
    public boolean hasCardSelected() {
        return lastSelectedCard != null;
    }

    public String generateAttackReport(ArrayList<Attack> attacks) {
		StringBuilder report = new StringBuilder();
		for (Attack attack : attacks) {
			report.append(attack.name).append(":\nCosts:\n");
			for (Energy energy : attack.costs) {
				report.append("• ").append(energy.getName()).append("\n");
			}
			report.append("Damage: ").append(attack.damage).append("\n");
		}
		return report.toString();
	}

    @Override
    public void displayPokemonReport(Pokemon pokemon) {
        StringBuilder report = new StringBuilder();
        //General info
        report.append("Pokemon Report:\n\n");
        report.append("Name: ").append(pokemon.getName()).append("\n");
        report.append("Stage: ").append(pokemon.getStage()).append("\n");
        report.append("Type: ").append(pokemon.type).append("\n");
        report.append("HP: ").append(pokemon.getCurHP()).append("\n");
        report.append("Retreat Cost: ").append(pokemon.retreatCost).append(" Colorless Energy\n");

        //Energies
        report.append("\nAttached Energies:\n");
        if (pokemon.energies.isEmpty()) {
            report.append("None\n");
        } else {
            for (Energy energy : pokemon.energies) {
                report.append("• ").append(energy.getName()).append("\n");
            }
        }

        //Attacks
        report.append("\nAttacks:\n");
        report.append(generateAttackReport(pokemon.attacks));

        displayMessage(report.toString());
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
            createLinkedButtonCard(currCard.getName(), currCard);
        }
    }

    @Override
    public void closeWindow() {
        frame.dispose();
    }

    @Override
    public void removePrizeCard(int playerNum) {
        int i = 0;
        Color white = Color.WHITE;
        if (playerNum == 1) {
            while (player1PrizeCards[i].equals(white)) {
                i++;
            }
            player1PrizeCards[i] = white;
        } else {
            while (player2PrizeCards[i].equals(white)) {
                i++;
            }
            player2PrizeCards[i] = white;
        }
    }
    private void displayPokemonActionButtons(Pokemon card) {
        removeSelectedCardActionButtons();
        if (card.stage == 0) {
            selectedCardActionButtons.add(createLinkedButtonAction("Add Pokemon Bench", "AddToBench"));
        }
    }


    private void displayEnergyActionButtons(Energy energy) {
        removeSelectedCardActionButtons();
        selectedCardActionButtons.add(createLinkedButtonAction("Add An Energy", "AddEnergy"));
    }

    private void displayTrainerActionButtons(Trainer trainer) {
        removeSelectedCardActionButtons();
        selectedCardActionButtons.add(createLinkedButtonAction("Play Trainer", "PlayTrainer"));
    }

    private void displayActiveActionButton(){
        removeSelectedCardActionButtons();
        selectedCardActionButtons.add(createLinkedButtonAction("Active Selected Pokemon", "AddToBench"));
    }

    private void removeSelectedCardActionButtons() {
        if(!selectedCardActionButtons.isEmpty()) {
            for (int i = 0; i<selectedCardActionButtons.size();i++) {
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
        createLinkedButtonAction("Pass Turn", "PassTurn");
        createLinkedButtonAction("Attack Opponent", "Attack");
        createLinkedButtonAction("Retreat Pokemon", "Retreat");
        createLinkedButtonAction("See Card Info", "CardInfo");
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
        btn.addActionListener(e -> {
            if (lastSelectedButton != null) {
                lastSelectedButton.setBackground(Color.WHITE);
            }
            btn.setBackground(Color.GREEN);
            lastSelectedButton = btn;
            if(!getLastSelectedCard().equals(currCard)) {
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
        JButton btn = new JButton("Pass Turn");
        btn.addActionListener(e -> {
            this.waitForAction = true;
            removeButton(btn);
        });
        buttons.add(btn);
        actionPanel.add(btn);
        actionPanel.repaint();
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
    public void retreat(Card newCard, int playerTurn) {
        Card oldActive = playerTurn == 1 ? player1activeCard : player2activeCard;
        if (playerTurn == 1) {
            this.player1benchCards.remove(newCard);
            this.player1benchCards.add(oldActive);
            this.player1activeCard = newCard;
        } else if (playerTurn == 2) {
            this.player2benchCards.remove(newCard);
            this.player2benchCards.add(oldActive);
            this.player2activeCard = newCard;
        }
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