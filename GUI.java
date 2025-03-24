import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUI extends JPanel {

	final int frameWidth = 1000;
	final int frameHeight = 800;
	final int frameXLoc = 0;
	final int frameYLoc = 0;

	final int backgroundLineThickness = 5;

	final int cardWidth = frameWidth / 10;
	final int cardHeight = frameHeight / 7;

	final int numBench = 5;
	final int numPrizeCards = 6;

	final int marginSide = 40;
	final int marginTop = 75;

	final int marginPrizeCardVertical = 20;
	final int prizeCardsOffset = cardWidth / 2;
	final int pcVerticalOffset = cardHeight / 10;
	final int benchHorizontalOffset = cardWidth / 6;
	final int activeVerticalOffset = cardHeight / 3;
	final int deckOffset = 15;

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
		g2d.drawRect(marginSide, frameHeight - cardHeight - marginTop, cardWidth, cardHeight);
		g2d.drawRect(marginSide, frameHeight - (cardHeight*2) - marginTop - marginPrizeCardVertical, cardWidth, cardHeight);
		g2d.drawRect(marginSide, frameHeight - (cardHeight*3) - marginTop - (marginPrizeCardVertical*2), cardWidth, cardHeight);

		//right column (front of left column)
		g2d.setColor(backgroundBlue);
		g2d.fillRect(marginSide + prizeCardsOffset, frameHeight - cardHeight - marginTop - pcVerticalOffset, cardWidth, cardHeight);
		g2d.fillRect(marginSide + prizeCardsOffset, frameHeight - (cardHeight*2) - marginTop - marginPrizeCardVertical - pcVerticalOffset, cardWidth, cardHeight);
		g2d.fillRect(marginSide + prizeCardsOffset, frameHeight - (cardHeight*3) - marginTop - (marginPrizeCardVertical*2) - pcVerticalOffset, cardWidth, cardHeight);
		g2d.setColor(Color.WHITE);
		g2d.drawRect(marginSide + prizeCardsOffset, frameHeight - cardHeight - marginTop - pcVerticalOffset, cardWidth, cardHeight);
		g2d.drawRect(marginSide + prizeCardsOffset, frameHeight - (cardHeight*2) - marginTop - marginPrizeCardVertical - pcVerticalOffset, cardWidth, cardHeight);
		g2d.drawRect(marginSide + prizeCardsOffset, frameHeight - (cardHeight*3) - marginTop - (marginPrizeCardVertical*2) - pcVerticalOffset, cardWidth, cardHeight);

		//Bench Cards
		g2d.setColor(Color.WHITE);
		for(int i = 0; i < 5; i++) {
			g2d.drawRect(marginSide + (cardWidth*2) + (i*(benchHorizontalOffset + cardWidth)), frameHeight - cardHeight - marginTop, cardWidth, cardHeight);
		}

		//Active Pokemon
		g2d.drawRect((frameWidth/2) - (cardWidth/2), (frameHeight/2) + activeVerticalOffset, cardWidth, cardHeight);

		//Discard
		g2d.drawRect(frameWidth - marginSide - cardWidth, frameHeight - marginTop - cardHeight, cardWidth, cardHeight);

		//Trash
		g2d.drawRect(frameWidth - marginSide - cardWidth, frameHeight - marginTop - (cardHeight*2) - deckOffset, cardWidth, cardHeight);


		// ----- STADIUM CARD -------
		g2d.drawRect((frameWidth/2) - ((cardWidth/4)*9), (frameHeight/2) - (cardHeight/2), cardWidth, cardHeight);


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
			g2d.drawRect(frameWidth - marginSide - (cardWidth*3) - (i*(benchHorizontalOffset + cardWidth)), marginTop, cardWidth, cardHeight);
		}

		//Active Pokemon
		g2d.drawRect((frameWidth/2) - (cardWidth/2), (frameHeight/2) - activeVerticalOffset - cardHeight, cardWidth, cardHeight);

		//Discard
		g2d.drawRect(marginSide, marginTop, cardWidth, cardHeight);

		//Trash
		g2d.drawRect(marginSide, marginTop + (cardHeight) + deckOffset, cardWidth, cardHeight);
	}

	public void createGUI() {

		// Creating the JFrame
		JFrame frame = new JFrame();
		frame.setTitle("Pokemon Game");
		frame.setSize(frameWidth, frameHeight);
		frame.setLocation(frameXLoc, frameYLoc);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.add(this);

		frame.setVisible(true);
	}
}


