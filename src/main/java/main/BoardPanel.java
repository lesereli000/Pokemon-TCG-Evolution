package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Map;
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
    private final BoardPositionMap posMap;

    public BoardPanel(GameGUI gui) {
        this.gui = gui;
        this.posMap = new BoardPositionMap(frameWidth, frameHeight);
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
            Rectangle bounds = posMap.getZones().get(DropZoneType.valueOf("P1_BENCH_" + i));
            g2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
            
            if (p1Bench.size() > i) {
                Card currentCard = p1Bench.get(i);
                if (!drawCardImage(g2d, currentCard, bounds.x, bounds.y, bounds.width, bounds.height)) {
                    g2d.drawString(currentCard.getName(),
                            bounds.x + (bounds.width / 3),
                            bounds.y + (bounds.height / 2));
                }
                if (currentCard instanceof Pokemon) {
                    drawPokemonStatus(g2d, (Pokemon) currentCard, bounds, "ABOVE");
                }
            }
        }
    }

    private void drawActiveP1(Graphics2D g2d, Player p1) {
        Pokemon p1Active = p1.getActivePokemon();
        g2d.setColor(gui.getPlayer1ActiveColor());
        Rectangle bounds = posMap.getZones().get(DropZoneType.P1_ACTIVE);
        g2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        
        if (p1Active != null && p1.hasActive()) {
            String name = p1Active.getName();
            if (name == null) name = "Unknown";
            if (!drawCardImage(g2d, p1Active, bounds.x, bounds.y, bounds.width, bounds.height)) {
                String actvPok = gui.getMessages().getString("actvPok");
                g2d.drawString(actvPok, bounds.x + marginSide / 8, bounds.y + bounds.height / 3);
                g2d.drawString(name, bounds.x + marginSide / 8, bounds.y + bounds.height / 2);
            }
            drawPokemonStatus(g2d, p1Active, bounds, "RIGHT");
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
            Rectangle bounds = posMap.getZones().get(DropZoneType.valueOf("P2_BENCH_" + i));
            g2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
            
            if (p2Bench.size() > i) {
                Card currentCard = p2Bench.get(i);
                if (!drawCardImage(g2d, currentCard, bounds.x, bounds.y, bounds.width, bounds.height)) {
                    g2d.drawString(currentCard.getName(),
                            bounds.x + (bounds.width / 3),
                            bounds.y + (bounds.height / 2));
                }
                if (currentCard instanceof Pokemon) {
                    drawPokemonStatus(g2d, (Pokemon) currentCard, bounds, "BELOW");
                }
            }
        }
    }

    private void drawActiveP2(Graphics2D g2d, Player p2) {
        Pokemon p2Active = p2.getActivePokemon();
        g2d.setColor(gui.getPlayer2ActiveColor());
        Rectangle bounds = posMap.getZones().get(DropZoneType.P2_ACTIVE);
        
        if (p2Active != null && p2.hasActive()) {
            String name = p2Active.getName();
            if (name == null) name = "Unknown";
            if (!drawCardImage(g2d, p2Active, bounds.x, bounds.y, bounds.width, bounds.height)) {
                String actvPok = gui.getMessages().getString("actvPok");
                g2d.drawString(actvPok, bounds.x + marginSide / 8, bounds.y + bounds.height / 3);
                g2d.drawString(name, bounds.x + marginSide / 8, bounds.y + bounds.height / 2);
            }
            drawPokemonStatus(g2d, p2Active, bounds, "RIGHT");
        }
        g2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    private boolean drawCardImage(Graphics2D g2d, Card card, int x, int y, int width, int height) {
        String url = card.getImageUrl();
        if (url != null) {
            BufferedImage image = ImageLoader.getImage(url, this);
            if (image != null) {
                double imgWidth = image.getWidth();
                double imgHeight = image.getHeight();
                double imgRatio = imgWidth / imgHeight;
                double boxRatio = (double) width / height;

                int drawWidth, drawHeight, drawX, drawY;

                if (imgRatio > boxRatio) {
                    // Image is wider than box
                    drawWidth = width;
                    drawHeight = (int) (width / imgRatio);
                    drawX = x;
                    drawY = y + (height - drawHeight) / 2;
                } else {
                    // Image is taller than box
                    drawHeight = height;
                    drawWidth = (int) (height * imgRatio);
                    drawY = y;
                    drawX = x + (width - drawWidth) / 2;
                }

                g2d.drawImage(image, drawX, drawY, drawWidth, drawHeight, null);
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

    void drawPokemonStatus(Graphics2D g2d, Pokemon pokemon, Rectangle bounds, String position) {
        if (pokemon == null) return;

        int statusHeight = (int) (bounds.height * 0.3);
        int statusWidth = bounds.width;
        int statusX = bounds.x + 2;
        int statusY;
        
        switch (position) {
            case "ABOVE" -> statusY = bounds.y - statusHeight - 5;
            case "BELOW" -> statusY = bounds.y + bounds.height + 5;
            case "RIGHT" -> {
                statusX = bounds.x + bounds.width + 10;
                statusY = bounds.y + (bounds.height - statusHeight) / 2;
            }
            default -> statusY = bounds.y;
        }
        
        // Background Gradient
        GradientPaint gp = new GradientPaint(statusX, statusY, new Color(0, 0, 0, 160), 
                                           statusX, statusY + statusHeight, new Color(0, 0, 0, 200));
        g2d.setPaint(gp);
        g2d.fillRoundRect(statusX, statusY, statusWidth - 4, statusHeight, 8, 8);

        // HP Bar
        int hpY = statusY + 6;
        int hpWidth = statusWidth - 20;
        int hpHeight = 8;
        int curHP = pokemon.getCurHP();
        int maxHP = pokemon.getMaxHP();
        double hpPercent = (double) curHP / maxHP;

        g2d.setColor(new Color(50, 50, 50, 255));
        g2d.fillRoundRect(statusX + 8, hpY, hpWidth, hpHeight, 4, 4);

        Color hpColor = Color.GREEN;
        if (hpPercent < 0.2) hpColor = Color.RED;
        else if (hpPercent < 0.5) hpColor = Color.YELLOW;
        
        g2d.setColor(hpColor);
        g2d.fillRoundRect(statusX + 8, hpY, (int) (hpWidth * Math.max(0, hpPercent)), hpHeight, 4, 4);

        // HP Text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
        g2d.drawString("HP: " + curHP + "/" + maxHP, statusX + 8, hpY + hpHeight + 12);

        // Energy Summary
        Map<EnergyType, Integer> energyMap = pokemon.getEnergyMap();
        int energyX = statusX + 8;
        int energyY = hpY + hpHeight + 20;

        for (Map.Entry<EnergyType, Integer> entry : energyMap.entrySet()) {
            if (entry.getValue() > 0 && entry.getKey() != EnergyType.COLORLESS) {
                EnergyType type = entry.getKey();
                g2d.setColor(getEnergyColor(type));
                g2d.fillOval(energyX, energyY, 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 9));
                g2d.drawString("x" + entry.getValue(), energyX + 12, energyY + 9);
                energyX += 30;
                if (energyX > statusX + statusWidth - 20) {
                    energyX = statusX + 8;
                    energyY += 12;
                }
            }
        }
    }

    Color getEnergyColor(EnergyType type) {
        return switch (type) {
            case FIRE -> new Color(255, 69, 0);
            case WATER -> new Color(30, 144, 255);
            case GRASS -> new Color(50, 205, 50);
            case LIGHTNING -> new Color(255, 215, 0);
            case FIGHTING -> new Color(210, 105, 30);
            case PSYCHIC -> new Color(153, 50, 204);
            case DARKNESS -> new Color(70, 70, 70);
            case METAL -> new Color(169, 169, 169);
            case FAIRY -> new Color(255, 182, 193);
            case DRAGON -> new Color(184, 134, 11);
            case COLORLESS -> Color.LIGHT_GRAY;
            default -> Color.WHITE;
        };
    }
}
