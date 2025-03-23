import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUI extends JPanel {
	
	final int frameWidth = 1000;
    final int frameHeight = 800;
    final int frameXLoc = 0;
    final int frameYLoc = 0;

    final int cardWidth = 40;
    final int cardHeight = 60;
    
    final int numBench = 5;
    final int numPrizeCards = 6;
    
    final int marginSide = 50;
    final int marginTop = 50;
    
    final int marginsPrizeCard = 15;
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		g.drawRect(marginSide, frameHeight - cardHeight - marginTop, cardWidth, cardHeight);
		g.drawRect(marginSide, frameHeight - (cardHeight*2) - marginTop - marginsPrizeCard, cardWidth, cardHeight);
		g.drawRect(marginSide, frameHeight - (cardHeight*3) - marginTop - (marginsPrizeCard*2), cardWidth, cardHeight);
	}

    private void createGUI() {
    	
        // Creating the JFrame
        JFrame frame = new JFrame();
        frame.setTitle("Pokemon Game");
        frame.setSize(frameWidth, frameHeight);
        frame.setLocation(frameXLoc, frameYLoc);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // ---- BOTTOM HALF (this user) -----
        
        //Prize cards (bottom left)
//        Rectangle pc1 = new Rectangle(marginSide, frameHeight - marginTop, cardWidth, cardHeight);
//        Rectangle pc2 = new Rectangle(marginSide, frameHeight - (marginTop + marginsPrizeCard), cardWidth, cardHeight);
//        Rectangle pc3 = new Rectangle(marginSide, frameHeight - (marginTop + (2 * marginsPrizeCard)), cardWidth, cardHeight);
        
        //Bench pokemon
        
        //active pokemon
        
        //
        
        frame.add(this);
        
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        GUI gui = new GUI();
        gui.createGUI();
    }

}


