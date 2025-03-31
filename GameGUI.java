import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class GameGUI implements GUI {

	//TODO Update GameGUI to display a spot for the active players hand

	static final int frameWidth = 1200;
	static final int frameHeight = frameWidth*3/4;
	static final int frameXLoc = 0;
	static final int frameYLoc = 0;

	private JFrame frame;
	private GamePanel panel;

	private JButton flipBtn;
	private int playerTurn;

	static final int backgroundLineThickness = 4;

	static final int cardWidth = frameWidth / 12;
	static final int cardHeight = cardWidth*7/5;

	static final int marginSide = 40;
	static final int marginTop = 40;
	static final int marginBottom = 75;

	static final int marginPrizeCardVertical = 20;
	static final int prizeCardsOffset = cardWidth / 2;
	static final int pcVerticalOffset = cardHeight / 10;
	static final int benchHorizontalOffset = frameWidth / 19;
	static final int benchHorizontalIncrement = cardHeight / 6;
	static final int benchVerticalOffset = frameHeight / 6;
	static final int activeVerticalOffset = frameHeight / 10;
	static final int activeVerticalMargin = cardHeight / 16;
	static final int deckOffset = 15;

	private Color deckColor = Color.WHITE;

	public GameGUI() {
		createGUI();
	}

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

			//Prize Cards
			//left column
			g2d.setColor(Color.WHITE);
			g2d.drawRect(marginSide, frameHeight - cardHeight - marginBottom, cardWidth, cardHeight);
			g2d.drawRect(marginSide, frameHeight - (cardHeight*2) - marginBottom - marginPrizeCardVertical, cardWidth, cardHeight);
			g2d.drawRect(marginSide, frameHeight - (cardHeight*3) - marginBottom - (marginPrizeCardVertical*2), cardWidth, cardHeight);

			//right column (front of left column)
			g2d.setColor(backgroundBlue);
			g2d.fillRect(marginSide + prizeCardsOffset, frameHeight - cardHeight - marginBottom - pcVerticalOffset, cardWidth, cardHeight);
			g2d.fillRect(marginSide + prizeCardsOffset, frameHeight - (cardHeight*2) - marginBottom - marginPrizeCardVertical - pcVerticalOffset, cardWidth, cardHeight);
			g2d.fillRect(marginSide + prizeCardsOffset, frameHeight - (cardHeight*3) - marginBottom - (marginPrizeCardVertical*2) - pcVerticalOffset, cardWidth, cardHeight);
			g2d.setColor(Color.WHITE);
			g2d.drawRect(marginSide + prizeCardsOffset, frameHeight - cardHeight - marginBottom - pcVerticalOffset, cardWidth, cardHeight);
			g2d.drawRect(marginSide + prizeCardsOffset, frameHeight - (cardHeight*2) - marginBottom - marginPrizeCardVertical - pcVerticalOffset, cardWidth, cardHeight);
			g2d.drawRect(marginSide + prizeCardsOffset, frameHeight - (cardHeight*3) - marginBottom - (marginPrizeCardVertical*2) - pcVerticalOffset, cardWidth, cardHeight);

			//Bench Cards
			g2d.setColor(Color.WHITE);
			for(int i = 0; i < 5; i++) {
				g2d.drawRect(marginSide + (cardWidth*2) + benchHorizontalOffset + (i*(benchHorizontalIncrement + cardWidth)), frameHeight - cardHeight - marginBottom - benchVerticalOffset, cardWidth, cardHeight);
			}

			//Active Pokemon
			g2d.drawRect((frameWidth/2) - (cardWidth/2), (frameHeight/2) + activeVerticalMargin -activeVerticalOffset, cardWidth, cardHeight);

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
				g2d.drawRect(frameWidth - marginSide - cardWidth, marginTop + (i*(marginPrizeCardVertical+cardHeight)), cardWidth, cardHeight);
			}
			//left column (top of right column)
			g2d.setColor(backgroundBlue);
			for(int i = 0; i < 3; i++) {
				g2d.fillRect(frameWidth - prizeCardsOffset - marginSide - cardWidth, marginTop + (i*(marginPrizeCardVertical+cardHeight)) + pcVerticalOffset, cardWidth, cardHeight);
			}
			g2d.setColor(Color.WHITE);
			for(int i = 0; i < 3; i++) {
				g2d.drawRect(frameWidth - prizeCardsOffset - marginSide - cardWidth, marginTop + (i*(marginPrizeCardVertical+cardHeight)) + pcVerticalOffset, cardWidth, cardHeight);
			}

			//Bench Cards
			for(int i = 0; i < 5; i++) {
				g2d.drawRect(frameWidth - marginSide - (cardWidth*3) - benchHorizontalOffset - (i*(benchHorizontalIncrement + cardWidth)), marginTop, cardWidth, cardHeight);
			}

			//Active Pokemon
			g2d.drawRect((frameWidth/2) - (cardWidth/2), (frameHeight/2) - activeVerticalMargin - cardHeight - activeVerticalOffset, cardWidth, cardHeight);

			//Discard
			g2d.drawRect(marginSide, marginTop, cardWidth, cardHeight);

			//Bench
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
	}

	public void createFlipButton(String flipResult) {
		this.flipBtn = new JButton("Flip Coin");
		this.playerTurn = flipResult.equals("Heads") ? 1 : 2;
		flipBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = "The result was: " + flipResult + "\nPlayer " + playerTurn + " goes first!";
				displayMessage(msg);
				removeButton();
			}
		});
		panel.add(flipBtn);
		panel.revalidate();
		panel.repaint();
	}

	public void removeButton() {
		panel.remove(flipBtn);
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

}


