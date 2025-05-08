package main;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;


public class GameGUI implements GUI {

	static final int frameWidth = 1200;
	static final int frameHeight = frameWidth*3/4;
	static final int frameXLoc = 0;
	static final int frameYLoc = 0;
	static final int origNumPrizeCards = 6;

	private JFrame frame;
	private GamePanel panel;
	private JPanel actionPanel;


	static final int backgroundLineThickness = 4;

	static final int cardWidth = frameWidth / 12;
	static final int cardHeight = cardWidth*7/5;

	static final int marginSide = 40;
	static final int marginTop = 80;
	static final int marginBottom = 75;

	static final int marginPrizeCardVertical = 20;
	static final int prizeCardsOffset = cardWidth / 2;
	static final int pcVerticalOffset = cardHeight / 10;
	static final int benchHorizontalOffset = frameWidth / 19;
	static final int benchHorizontalIncrement = cardHeight / 6;
	static final int benchVerticalOffset = frameHeight / 8;
	static final int activeVerticalOffset = frameHeight / 16;
	static final int activeVerticalMargin = cardHeight / 16;
	static final int deckOffset = 15;

	private Card player1activeCard;
	private Card player2activeCard;

	private ArrayList<Card> player1benchCards = new ArrayList<Card>();
	private ArrayList<Card> player2benchCards = new ArrayList<Card>();

	private Color deckColor = Color.WHITE;
	private Color player1ActiveColor = Color.WHITE;
	private Color player2ActiveColor = Color.WHITE;
	private String lastSelectedButton;

	private Color[] player1PrizeCards = new Color[origNumPrizeCards];
	private Color[] player2PrizeCards = new Color[origNumPrizeCards];



	private ArrayList<JButton> buttons = new ArrayList<>();

	private Font boldFont = new Font("Arial", Font.BOLD, 16);
	private Font plainFont = new Font("Arial", Font.PLAIN, 12);


	private volatile boolean waitForAction = false;
	private volatile Card lastSelectedCard = null;
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

			// ----- USER SIDE (NEAR/BOTTOM SIDE) --------
			g2d.setColor(Color.WHITE);
			g2d.setFont(boldFont);
			if(playerTurn != 0) {
				g2d.drawString("Player turn: " + playerTurn, marginSide/2, frameHeight - marginTop);
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
			g2d.fillRect(marginSide + prizeCardsOffset, frameHeight - (cardHeight*2) - marginBottom - marginPrizeCardVertical - pcVerticalOffset, cardWidth, cardHeight);
			g2d.fillRect(marginSide + prizeCardsOffset, frameHeight - (cardHeight*3) - marginBottom - (marginPrizeCardVertical*2) - pcVerticalOffset, cardWidth, cardHeight);

			for(int i = 0; i < 3; i++) {
				Color currColor = player1PrizeCards[i + 3];
				g2d.setColor(currColor);
				g2d.drawRect(marginSide + prizeCardsOffset, frameHeight - cardHeight * (i + 1) - marginBottom - marginPrizeCardVertical * i - pcVerticalOffset, cardWidth, cardHeight);
			}

			//Bench Cards
			g2d.setColor(Color.WHITE);
			for(int i = 0; i < 5; i++) {
				g2d.drawRect(marginSide + (cardWidth*2) + benchHorizontalOffset + (i*(benchHorizontalIncrement + cardWidth)), frameHeight - cardHeight - marginBottom - benchVerticalOffset, cardWidth, cardHeight);
				if(player1benchCards.size() > i) {
					Card currentCard = player1benchCards.get(i);
					g2d.drawString(currentCard.name, marginSide + (cardWidth*2) + benchHorizontalOffset + (i*(benchHorizontalIncrement + cardWidth)) + (cardWidth/3), frameHeight - cardHeight - marginBottom - benchVerticalOffset + (cardHeight/2));
				}
			}

			//Active Pokemon
			g2d.setColor(player1ActiveColor);
			g2d.drawRect((frameWidth/2) - (cardWidth/2), (frameHeight/2) + activeVerticalMargin -activeVerticalOffset, cardWidth, cardHeight);
			if(player1ActiveColor == Color.GREEN) {
				g2d.drawString("Active Pokemon:", (frameWidth / 2) - (cardWidth / 2) + marginSide / 8, (frameHeight / 2) - activeVerticalOffset + marginTop / 2);
				g2d.drawString(player1activeCard.getName(), (frameWidth / 2) - (cardWidth / 2) + marginSide / 8, (frameHeight / 2) - activeVerticalOffset + marginTop );
			}

			g2d.setColor(Color.WHITE);
			//Discard
			g2d.drawRect(frameWidth - marginSide - cardWidth, frameHeight - marginBottom - cardHeight, cardWidth, cardHeight);

			//Bench
			g2d.setColor(deckColor);
			g2d.drawRect(frameWidth - marginSide - cardWidth, frameHeight - marginBottom - (cardHeight*2) - deckOffset, cardWidth, cardHeight);
			g2d.setColor(Color.WHITE);

			// ----- STADIUM CARD -------
			g2d.drawRect((frameWidth/2) - ((cardWidth/4)*9), (frameHeight/2) - (cardHeight/2) - activeVerticalOffset, cardWidth, cardHeight);


			// ----- OPPONENT SIDE (FAR/TOP SIDE) --------

			//Prize Cards
			//right column (bottom)
			for(int i = 0; i < 3; i++) {
				Color currColor = player2PrizeCards[i];
				g2d.setColor(currColor);
				g2d.drawRect(frameWidth - marginSide - cardWidth, marginTop + (i*(marginPrizeCardVertical+cardHeight)), cardWidth, cardHeight);
			}
			//left column (top of right column)
			g2d.setColor(backgroundBlue);
			for(int i = 0; i < 3; i++) {
				g2d.fillRect(frameWidth - prizeCardsOffset - marginSide - cardWidth, marginTop + (i*(marginPrizeCardVertical+cardHeight)) + pcVerticalOffset, cardWidth, cardHeight);
			}
			for(int i = 0; i < 3; i++) {
				Color currColor = player2PrizeCards[i + 3];
				g2d.setColor(currColor);
				g2d.drawRect(frameWidth - prizeCardsOffset - marginSide - cardWidth, marginTop + (i*(marginPrizeCardVertical+cardHeight)) + pcVerticalOffset, cardWidth, cardHeight);
			}
			g2d.setColor(Color.WHITE);
			//Bench Cards
			for(int i = 0; i < 5; i++) {
				g2d.drawRect(frameWidth - marginSide - (cardWidth*3) - benchHorizontalOffset - (i*(benchHorizontalIncrement + cardWidth)), marginTop, cardWidth, cardHeight);
				if(player2benchCards.size() > i) {
					Card currentCard = player2benchCards.get(i);
					g2d.drawString(currentCard.name, frameWidth - marginSide - (cardWidth*3) - benchHorizontalOffset - (i*(benchHorizontalIncrement + cardWidth)) + (cardWidth/3), marginTop + (cardHeight/2));
				}
			}

			//Active Pokemon
			g2d.setColor(player2ActiveColor);
			if(player2ActiveColor == Color.GREEN) {
				g2d.drawString("Active Pokemon:", (frameWidth / 2) - (cardWidth / 2) + marginSide / 8, (frameHeight / 2) - activeVerticalMargin - cardHeight - activeVerticalOffset/4);
				g2d.drawString(player2activeCard.getName(), (frameWidth / 2) - (cardWidth / 2) + marginSide / 8, (frameHeight / 2) - activeVerticalMargin - cardHeight - activeVerticalOffset/8);
			}
			g2d.drawRect((frameWidth/2) - (cardWidth/2), (frameHeight/2) - activeVerticalMargin - cardHeight - activeVerticalOffset, cardWidth, cardHeight);



			g2d.setColor(Color.WHITE);
			//Discard
			g2d.drawRect(marginSide, marginTop, cardWidth, cardHeight);

			//Deck
			g2d.setColor(deckColor);
			g2d.drawRect(marginSide, marginTop + (cardHeight) + deckOffset, cardWidth, cardHeight);
		}
	}

	public void createGUI() {
		// Creating the JFrame
		frame = new JFrame();
		frame.setTitle("Pokemon Game");
		frame.setSize(frameWidth, frameHeight);
		frame.setLocation(frameXLoc, frameYLoc);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.panel = new GamePanel();
		frame.add(panel);

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
		buttons.remove(button);
		panel.remove(button);
		panel.revalidate();
		panel.repaint();
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
		if(playerTurn == 1) {
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
		if(playerTurn == 1 && this.player1benchCards.size() < 6) {
			this.player1benchCards.add(newBench);
		} else if (playerTurn == 2 && this.player2benchCards.size() < 6) {
			this.player2benchCards.add(newBench);
		}

		frame.repaint();
	}

	@Override
	public void removeBenchCard(Card newBench, int playerTurn) {
		if(playerTurn == 1 && !this.player1benchCards.isEmpty()) {
			this.player1benchCards.remove(newBench);
		} else if (playerTurn == 2 && !this.player2benchCards.isEmpty()) {
			this.player2benchCards.remove(newBench);
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
		Card lastCard = this.lastSelectedCard;
		this.lastSelectedCard = null;
		return lastCard;
	}


	public void setupActivePokemon(){
		lastSelectedCard = null;
		createLinkedButtonAction("Active Selected Pokemon", "AddToBench");
	}

	@Override
	public void waitForPokemonSelected() {
		//TODO: Wait for any Pokemon to be pressed
		while (!waitForAction) {
			Thread.onSpinWait();
		}
		this.waitForAction = false;
	}

	@Override
	public void displayCards(ArrayList<Card> playerCards) {
        for (Card currCard:playerCards){
            createLinkedButtonCard(currCard.getName(), currCard);
        }
	}

	@Override
	public void displayAttacks(ArrayList<Attack> attacks, String submitMessage) {
		for(Attack currAttack: attacks) {
			createLinkedButtonAttack(currAttack.name, currAttack);
		}
		createButton(submitMessage);
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
			while(player1PrizeCards[i].equals(white)) {
				i++;
			}
			player1PrizeCards[i] = white;
		} else {
			while(player2PrizeCards[i].equals(white)) {
				i++;
			}
			player2PrizeCards[i] = white;
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
		createLinkedButtonAction("Add Pokemon Bench", "AddToBench");
		createLinkedButtonAction("Add An Energy", "AddEnergy");
		createLinkedButtonAction("Pass Turn", "PassTurn");
	}


	@Override
	public JButton createButton(String message) {
		JButton btn = new JButton(message);
		btn.addActionListener(e -> {
		});
		buttons.add(btn);
		panel.add(btn);
		panel.repaint();
		frame.revalidate();
		frame.repaint();

		return btn;
	}

	@Override
	public JButton createLinkedButtonCard(String message, Card currCard) {
		JButton btn = new JButton(message);
		btn.addActionListener(e -> {
			setLastSelectedCard(currCard);
		});
		buttons.add(btn);
		panel.add(btn);
		panel.repaint();
		frame.revalidate();
		frame.repaint();

		return btn;
	}

	private JButton createLinkedButtonAttack(String name, Attack currAttack) {
		JButton btn = new JButton(name);
		btn.addActionListener(e -> {
			setLastSelectedAttack(currAttack);
		});
		buttons.add(btn);
		panel.add(btn);
		panel.repaint();
		frame.revalidate();
		frame.repaint();

		return btn;
	}

	private JButton createLinkedButtonAction(String name, String action) {
		JButton btn = new JButton(name);
		btn.addActionListener(e -> {
			this.lastActionButtonPressed = action;
			waitForAction  = true;
		});
		buttons.add(btn);
		panel.add(btn);
		panel.repaint();
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
		panel.add(btn);
		panel.repaint();
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
		panel.repaint();
		frame.revalidate();
		frame.repaint();

		return btn;

	}
	@Override
	public void removeAllButtons(){
		for (JButton btn : buttons) {
			panel.remove(btn);
			panel.revalidate();
			panel.repaint();
		}
		buttons = new ArrayList<>();
	}

	@Override
	public void retreat(Card newCard, int playerTurn) {
		Card oldActive = playerTurn == 1 ? player1activeCard : player2activeCard;
		if(playerTurn == 1) {
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
		frame.repaint();
	}


}