package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.ArrayList;
import javax.swing.*;

/**
 * BoardPanel handles the actual rendering of the Pokémon TCG board.
 * Extracted from GameGUI to resolve Smell #1 (Large Class) and Smell #2 (Long Method).
 */
public class BoardPanel extends JPanel {

    // Layout Constants moved from GameGUI
    static final int frameWidth = 1200;
    static final int frameHeight = frameWidth * 3 / 4;
    static final int backgroundLineThickness = 4;
    static final int cardWidth = (frameWidth * 2) / 25;
    static final int cardHeight = cardWidth * 7 / 5;
    static final int marginSide = 40;
    static final int marginTop = 180;
    static final int sideMarginTop = 80;
    static final int marginBottom = 75;
    static final int marginPrizeCardVertical = 15;
    static final int prizeCardsOffset = cardWidth / 2;
    static final int pcVerticalOffset = cardHeight / 15;
    static final int benchHorizontalOffset = frameWidth / 19;
    static final int benchHorizontalIncrement = cardHeight / 6;
    static final int benchVerticalOffset = (frameHeight / 8) - 100;
    static final int activeVerticalOffset = (frameHeight / 16) - 100;
    static final int activeVerticalMargin = cardHeight / 16;
    static final int deckOffset = 15;

    private final GameGUI gui;

    public BoardPanel(GameGUI gui) {
        this.gui = gui;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(backgroundLineThickness));

        drawBackground(g2d);
        drawPlayerLabels(g2d);
        drawFlag(g2d);

        // Draw Player 1 (Bottom/User)
        drawPlayerSection(g2d, gui.getPlayer1(), 1);

        // Draw Player 2 (Top/Opponent)
        drawPlayerSection(g2d, gui.getPlayer2(), 2);

        drawSharedArea(g2d);
        drawTurnText(g2d);
    }

    private static final Color BACKGROUND_COLOR = new Color(37, 150, 190);

    private void drawBackground(Graphics2D g2d) {
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRect(0, 0, frameWidth, frameHeight);
    }

    private void drawPlayerLabels(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(gui.getBoldFont());
        String playerLabel = gui.getMessages().getString("player");
        
        // P1 Label
        g2d.drawString(playerLabel + " 1",
                (marginSide * 7) / 4 + (cardWidth * 2) + benchHorizontalOffset
                        + (2 * (benchHorizontalIncrement + cardWidth)),
                frameHeight - (cardHeight * 8) / 7 - marginBottom - benchVerticalOffset);
        
        // P2 Label
        g2d.drawString(playerLabel + " 2", 
                frameWidth - (marginSide * 5) / 4 - (cardWidth * 3) - benchHorizontalOffset
                - (2 * (benchHorizontalIncrement + cardWidth)), marginTop + (cardHeight * 17) / 14);
    }

    private void drawFlag(Graphics2D g2d) {
        BufferedImage flag = gui.getFlag();
        if (flag != null) {
            int x = 7 * frameWidth / 11;
            int y = frameHeight / 2 - 100;
            Image scaledFlag = flag.getScaledInstance(120, 80, Image.SCALE_SMOOTH);
            g2d.drawImage(scaledFlag, x, y, null);
        }
    }

    private void drawPlayerSection(Graphics2D g2d, Player player, int side) {
        if (player == null) return;
        
        g2d.setFont(gui.getPlainFont());
        
        if (side == 1) {
            drawPrizeCardsP1(g2d, player);
            drawBenchP1(g2d, player);
            drawActiveP1(g2d, player);
            drawDeckAndDiscardP1(g2d);
        } else {
            drawPrizeCardsP2(g2d, player);
            drawBenchP2(g2d, player);
            drawActiveP2(g2d, player);
            drawDeckAndDiscardP2(g2d);
        }
    }

    private void drawPrizeCardsP1(Graphics2D g2d, Player p1) {
        int p1Prizes = p1.getNumPrizeCards();
        for (int i = 0; i < Math.min(p1Prizes, 3); i++) {
            g2d.setColor(Color.YELLOW);
            g2d.drawRect(marginSide,
                    frameHeight - (cardHeight * (i + 1)) - marginBottom - marginPrizeCardVertical * i, cardWidth,
                    cardHeight);
        }
        if (p1Prizes > 3) {
            for (int i = 0; i < p1Prizes - 3; i++) {
                g2d.setColor(Color.YELLOW);
                g2d.drawRect(marginSide + prizeCardsOffset, frameHeight - cardHeight * (i + 1) - marginBottom
                        - marginPrizeCardVertical * i - pcVerticalOffset, cardWidth, cardHeight);
            }
        }
    }

    private void drawBenchP1(Graphics2D g2d, Player p1) {
        ArrayList<Card> p1Bench = p1.getPokemonOnBench();
        g2d.setColor(Color.WHITE);
        for (int i = 0; i < gui.getNumBenchCards(); i++) {
            int x = (marginSide * 3) / 2 + (cardWidth * 2) + benchHorizontalOffset
                    + (i * (benchHorizontalIncrement + cardWidth));
            int y = frameHeight - cardHeight - marginBottom - benchVerticalOffset;
            g2d.drawRect(x, y, cardWidth, cardHeight);
            
            if (p1Bench.size() > i) {
                Card currentCard = p1Bench.get(i);
                if (!drawCardImage(g2d, currentCard, x, y, cardWidth, cardHeight)) {
                    g2d.drawString(currentCard.getName(),
                            marginSide + (cardWidth * 2) + benchHorizontalOffset
                                    + (i * (benchHorizontalIncrement + cardWidth)) + (cardWidth / 3),
                            frameHeight - cardHeight - marginBottom - benchVerticalOffset + (cardHeight / 2));
                }
            }
        }
    }

    private void drawActiveP1(Graphics2D g2d, Player p1) {
        Pokemon p1Active = p1.getActivePokemon();
        g2d.setColor(gui.getPlayer1ActiveColor());
        int x = (frameWidth / 2) - (cardWidth / 2);
        int y = (frameHeight / 2) + activeVerticalMargin - activeVerticalOffset;
        g2d.drawRect(x, y, cardWidth, cardHeight);
        
        if (p1Active != null && p1.hasActive()) {
            if (!drawCardImage(g2d, p1Active, x, y, cardWidth, cardHeight)) {
                String actvPok = gui.getMessages().getString("actvPok");
                g2d.drawString(actvPok, x + marginSide / 8,
                        (frameHeight / 2) - activeVerticalOffset + marginTop / 2);
                g2d.drawString(p1Active.getName(), x + marginSide / 8,
                        (frameHeight / 2) - activeVerticalOffset + marginTop);
            }
        }
    }

    private void drawDeckAndDiscardP1(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        // Discard
        g2d.drawRect(frameWidth - marginSide - cardWidth, frameHeight - marginBottom - cardHeight, cardWidth,
                cardHeight);
        // Deck
        g2d.setColor(gui.getDeckColor());
        g2d.drawRect(frameWidth - marginSide - cardWidth,
                frameHeight - marginBottom - (cardHeight * 2) - deckOffset, cardWidth, cardHeight);
    }

    private void drawPrizeCardsP2(Graphics2D g2d, Player p2) {
        int p2Prizes = p2.getNumPrizeCards();
        for (int i = 0; i < Math.min(p2Prizes, 3); i++) {
            g2d.setColor(Color.YELLOW);
            g2d.drawRect(frameWidth - marginSide - cardWidth,
                    sideMarginTop + (i * (marginPrizeCardVertical + cardHeight)), cardWidth, cardHeight);
        }
        if (p2Prizes > 3) {
            for (int i = 0; i < p2Prizes - 3; i++) {
                g2d.setColor(Color.YELLOW);
                g2d.drawRect(frameWidth - prizeCardsOffset - marginSide - cardWidth,
                        sideMarginTop + (i * (marginPrizeCardVertical + cardHeight)) + pcVerticalOffset, cardWidth,
                        cardHeight);
            }
        }
    }

    private void drawBenchP2(Graphics2D g2d, Player p2) {
        ArrayList<Card> p2Bench = p2.getPokemonOnBench();
        g2d.setColor(Color.WHITE);
        for (int i = 0; i < gui.getNumBenchCards(); i++) {
            int x = frameWidth - (marginSide * 3) / 2 - (cardWidth * 3) - benchHorizontalOffset
                    - (i * (benchHorizontalIncrement + cardWidth));
            int y = marginTop;
            g2d.drawRect(x, y, cardWidth, cardHeight);
            
            if (p2Bench.size() > i) {
                Card currentCard = p2Bench.get(i);
                if (!drawCardImage(g2d, currentCard, x, y, cardWidth, cardHeight)) {
                    g2d.drawString(currentCard.getName(),
                            frameWidth - marginSide - (cardWidth * 3) - benchHorizontalOffset
                                    - (i * (benchHorizontalIncrement + cardWidth)) + (cardWidth / 3),
                            marginTop + (cardHeight / 2));
                }
            }
        }
    }

    private void drawActiveP2(Graphics2D g2d, Player p2) {
        Pokemon p2Active = p2.getActivePokemon();
        g2d.setColor(gui.getPlayer2ActiveColor());
        int x = (frameWidth / 2) - (cardWidth / 2);
        int y = (frameHeight / 2) - activeVerticalMargin - cardHeight - activeVerticalOffset;
        
        if (p2Active != null && p2.hasActive()) {
            if (!drawCardImage(g2d, p2Active, x, y, cardWidth, cardHeight)) {
                String actvPok = gui.getMessages().getString("actvPok");
                g2d.drawString(actvPok, x + marginSide / 8,
                        (frameHeight / 2) - activeVerticalMargin - cardHeight - activeVerticalOffset / 4);
                g2d.drawString(p2Active.getName(), x + marginSide / 8,
                        (frameHeight / 2) - activeVerticalMargin - cardHeight - activeVerticalOffset / 8);
            }
        }
        g2d.drawRect(x, y, cardWidth, cardHeight);
    }

    private boolean drawCardImage(Graphics2D g2d, Card card, int x, int y, int width, int height) {
        String url = card.getImageUrl();
        if (url != null) {
            BufferedImage image = ImageLoader.getImage(url, this);
            if (image != null) {
                g2d.drawImage(image, x, y, width, height, null);
                return true;
            }
        }
        return false;
    }

    private void drawDeckAndDiscardP2(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        // Discard
        g2d.drawRect(marginSide, sideMarginTop, cardWidth, cardHeight);
        // Deck
        g2d.setColor(gui.getDeckColor());
        g2d.drawRect(marginSide, sideMarginTop + (cardHeight) + deckOffset, cardWidth, cardHeight);
    }

    private void drawSharedArea(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        // Stadium Card
        g2d.drawRect((frameWidth / 2) - ((cardWidth / 4) * 9),
                (frameHeight / 2) - (cardHeight / 2) - activeVerticalOffset, cardWidth, cardHeight);
    }

    private void drawTurnText(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(gui.getBoldFont());
        String turnText = gui.getMessages().getString("playerTurn");
        turnText = MessageFormat.format(turnText, gui.getPlayerTurn());
        FontMetrics metrics = g2d.getFontMetrics(gui.getBoldFont());
        int textWidth = metrics.stringWidth(turnText);
        g2d.drawString(turnText, (frameWidth * 5) / 7 - textWidth, frameHeight / 2 + 100 - (80 * 2) / 3);
    }
}
