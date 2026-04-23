package main.ui;

import main.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.*;

/**
 * BoardPanel handles the actual rendering of the Pokémon TCG board.
 * Extracted from GameGUI to resolve Smell #1 (Large Class) and Smell #2 (Long Method).
 */
public class BoardPanel extends JPanel {


    private final GameGUI gui;
    private final BoardPositionMap posMap;

    enum Side {
        TOP("P2"), BOTTOM("P1");
        final String prefix;
        Side(String prefix) { this.prefix = prefix; }
    }

    public BoardPanel(GameGUI gui) {
        this.gui = gui;
        this.posMap = new BoardPositionMap();
        
        // Add mouse listener for dragging from bench
        MouseAdapter boardDragAdapter = new MouseAdapter() {
            private GhostingDragAdapter activeDrag = null;

            @Override
            public void mousePressed(MouseEvent e) {
                Card cardUnderMouse = getCardAtPoint(e.getPoint());
                if (cardUnderMouse != null) {
                    activeDrag = new GhostingDragAdapter(gui, cardUnderMouse, new CardDropZoneDetector(posMap, gui));
                    activeDrag.mousePressed(e);
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (activeDrag != null) {
                    activeDrag.mouseDragged(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (activeDrag != null) {
                    activeDrag.mouseReleased(e);
                    activeDrag = null;
                }
            }
        };
        addMouseListener(boardDragAdapter);
        addMouseMotionListener(boardDragAdapter);
    }

    private Card getCardAtPoint(Point p) {
        // Check Bench P1
        for (int i = 0; i < Player.MAX_BENCH_SIZE; i++) {
            Rectangle rect = posMap.getZones().get(DropZoneType.valueOf("P1_BENCH_" + i));
            if (rect != null && rect.contains(p)) {
                Player p1 = gui.getPlayer1();
                if (p1 != null && p1.getPokemonOnBench().size() > i) {
                    return p1.getPokemonOnBench().get(i);
                }
            }
        }
        // Check Bench P2 (if allowed, but usually only current player)
        // Check Active P1
        Rectangle activeRect = posMap.getZones().get(DropZoneType.P1_ACTIVE);
        if (activeRect != null && activeRect.contains(p)) {
            Player p1 = gui.getPlayer1();
            if (p1 != null && p1.getActivePokemon() != null) return p1.getActivePokemon();
        }
        
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(UIConstants.BACKGROUND_LINE_THICKNESS));

        drawBackground(g2d);
        drawPlayerLabels(g2d);
        drawFlag(g2d);

        // Draw Player 1 (Bottom/User)
        drawPlayerSection(g2d, gui.getPlayer1(), Side.BOTTOM);

        // Draw Player 2 (Top/Opponent)
        drawPlayerSection(g2d, gui.getPlayer2(), Side.TOP);

        drawSharedArea(g2d);
        drawTurnText(g2d);
    }

    private void drawBackground(Graphics2D g2d) {
        // Subtle vertical gradient for the background
        GradientPaint gp = new GradientPaint(0, 0, UIConstants.BACKGROUND_COLOR, 
                                           0, UIConstants.FRAME_HEIGHT, new Color(10, 15, 25));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, UIConstants.FRAME_WIDTH, UIConstants.FRAME_HEIGHT);
        
        // Draw a subtle grid or separator line
        g2d.setColor(new Color(255, 255, 255, 20));
        g2d.drawLine(0, UIConstants.FRAME_HEIGHT / 2, UIConstants.FRAME_WIDTH, UIConstants.FRAME_HEIGHT / 2);
    }

    private void drawPlayerLabels(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 255, 180));
        g2d.setFont(gui.getBoldFont());
        String playerLabel = gui.getMessages().getString("player");
        
        // Use a more dynamic position for labels
        int centerX = UIConstants.FRAME_WIDTH / 2;
        
        // P1 Label
        g2d.drawString(playerLabel + " 1 (You)", 20, UIConstants.FRAME_HEIGHT - 20);
        
        // P2 Label
        g2d.drawString(playerLabel + " 2 (Opponent)", 20, 30);
    }

    private void drawFlag(Graphics2D g2d) {
        BufferedImage flag = gui.getFlag();
        if (flag != null) {
            int x = 7 * UIConstants.FRAME_WIDTH / 11;
            int y = UIConstants.FRAME_HEIGHT / 2 - 100;
            Image scaledFlag = flag.getScaledInstance(120, 80, Image.SCALE_SMOOTH);
            g2d.drawImage(scaledFlag, x, y, null);
        }
    }

    void drawPlayerSection(Graphics2D g2d, Player player, Side side) {
        if (player == null) return;
        
        g2d.setFont(gui.getPlainFont());
        
        drawPrizeCards(g2d, player, side);
        drawBench(g2d, player, side);
        drawActive(g2d, player, side);
        drawDeckAndDiscard(g2d, player, side);
    }

    void drawPrizeCards(Graphics2D g2d, Player player, Side side) {
        int prizeCount = player.getNumPrizeCards();
        g2d.setColor(Color.YELLOW);
        
        for (int i = 0; i < Math.min(prizeCount, 3); i++) {
            int x, y;
            if (side == Side.BOTTOM) {
                x = UIConstants.MARGIN_SIDE;
                y = UIConstants.FRAME_HEIGHT - (UIConstants.CARD_HEIGHT * (i + 1)) - UIConstants.MARGIN_BOTTOM - UIConstants.MARGIN_PRIZE_CARD_VERTICAL * i;
            } else {
                x = UIConstants.FRAME_WIDTH - UIConstants.MARGIN_SIDE - UIConstants.CARD_WIDTH;
                y = UIConstants.SIDE_MARGIN_TOP + (i * (UIConstants.MARGIN_PRIZE_CARD_VERTICAL + UIConstants.CARD_HEIGHT));
            }
            g2d.drawRoundRect(x, y, UIConstants.CARD_WIDTH, UIConstants.CARD_HEIGHT, 12, 12);
        }
        
        if (prizeCount > 3) {
            for (int i = 0; i < prizeCount - 3; i++) {
                int x, y;
                if (side == Side.BOTTOM) {
                    x = UIConstants.MARGIN_SIDE + UIConstants.PRIZE_CARDS_OFFSET;
                    y = UIConstants.FRAME_HEIGHT - UIConstants.CARD_HEIGHT * (i + 1) - UIConstants.MARGIN_BOTTOM - UIConstants.MARGIN_PRIZE_CARD_VERTICAL * i - UIConstants.PC_VERTICAL_OFFSET;
                } else {
                    x = UIConstants.FRAME_WIDTH - UIConstants.PRIZE_CARDS_OFFSET - UIConstants.MARGIN_SIDE - UIConstants.CARD_WIDTH;
                    y = UIConstants.SIDE_MARGIN_TOP + (i * (UIConstants.MARGIN_PRIZE_CARD_VERTICAL + UIConstants.CARD_HEIGHT)) + UIConstants.PC_VERTICAL_OFFSET;
                }
                g2d.drawRoundRect(x, y, UIConstants.CARD_WIDTH, UIConstants.CARD_HEIGHT, 12, 12);
            }
        }
    }

    void drawBench(Graphics2D g2d, Player player, Side side) {
        ArrayList<Card> bench = player.getPokemonOnBench();
        g2d.setColor(Color.WHITE);
        String statusPos = (side == Side.BOTTOM) ? "ABOVE" : "BELOW";
        
        for (int i = 0; i < gui.getNumBenchCards(); i++) {
            Rectangle bounds = posMap.getZones().get(DropZoneType.valueOf(side.prefix + "_BENCH_" + i));
            g2d.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);
            
            if (bench.size() > i) {
                Card currentCard = bench.get(i);
                if (!drawCardImage(g2d, currentCard, bounds.x, bounds.y, bounds.width, bounds.height)) {
                    g2d.drawString(currentCard.getName(),
                            bounds.x + (bounds.width / 3),
                            bounds.y + (bounds.height / 2));
                }
                if (currentCard instanceof Pokemon) {
                    drawPokemonStatus(g2d, (Pokemon) currentCard, bounds, statusPos);
                }
            }
        }
    }

    void drawActive(Graphics2D g2d, Player player, Side side) {
        Pokemon active = player.getActivePokemon();
        Color activeColor = (side == Side.BOTTOM) ? gui.getPlayer1ActiveColor() : gui.getPlayer2ActiveColor();
        g2d.setColor(activeColor);
        
        Rectangle bounds = posMap.getZones().get(DropZoneType.valueOf(side.prefix + "_ACTIVE"));
        g2d.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 15, 15);
        
        if (active != null && player.hasActive()) {
            String name = active.getName();
            if (name == null) name = "Unknown";
            if (!drawCardImage(g2d, active, bounds.x, bounds.y, bounds.width, bounds.height)) {
                String actvPok = gui.getMessages().getString("actvPok");
                g2d.drawString(actvPok, bounds.x + UIConstants.MARGIN_SIDE / 8, bounds.y + bounds.height / 3);
                g2d.drawString(name, bounds.x + UIConstants.MARGIN_SIDE / 8, bounds.y + bounds.height / 2);
            }
            drawPokemonStatus(g2d, active, bounds, "RIGHT");
        }
    }

    void drawDeckAndDiscard(Graphics2D g2d, Player player, Side side) {
        g2d.setColor(Color.WHITE);
        int discardX, discardY, deckX, deckY;
        
        if (side == Side.BOTTOM) {
            discardX = UIConstants.FRAME_WIDTH - UIConstants.MARGIN_SIDE - UIConstants.CARD_WIDTH;
            discardY = UIConstants.FRAME_HEIGHT - UIConstants.MARGIN_BOTTOM - UIConstants.CARD_HEIGHT;
            deckX = discardX;
            deckY = UIConstants.FRAME_HEIGHT - UIConstants.MARGIN_BOTTOM - (UIConstants.CARD_HEIGHT * 2) - UIConstants.DECK_OFFSET;
        } else {
            discardX = UIConstants.MARGIN_SIDE;
            discardY = UIConstants.SIDE_MARGIN_TOP;
            deckX = discardX;
            deckY = UIConstants.SIDE_MARGIN_TOP + UIConstants.CARD_HEIGHT + UIConstants.DECK_OFFSET;
        }
        
        // Discard
        g2d.drawRoundRect(discardX, discardY, UIConstants.CARD_WIDTH, UIConstants.CARD_HEIGHT, 10, 10);
        // Deck
        g2d.setColor(gui.getDeckColor());
        g2d.drawRoundRect(deckX, deckY, UIConstants.CARD_WIDTH, UIConstants.CARD_HEIGHT, 10, 10);
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


    private void drawSharedArea(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        // Stadium Card
        g2d.drawRect((UIConstants.FRAME_WIDTH / 2) - ((UIConstants.CARD_WIDTH / 4) * 9),
                (UIConstants.FRAME_HEIGHT / 2) - (UIConstants.CARD_HEIGHT / 2) - UIConstants.ACTIVE_VERTICAL_OFFSET, UIConstants.CARD_WIDTH, UIConstants.CARD_HEIGHT);
    }

    private void drawTurnText(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(gui.getBoldFont());
        String turnText = gui.getMessages().getString("playerTurn");
        turnText = MessageFormat.format(turnText, gui.getPlayerTurn());
        FontMetrics metrics = g2d.getFontMetrics(gui.getBoldFont());
        int textWidth = metrics.stringWidth(turnText);
        g2d.drawString(turnText, (UIConstants.FRAME_WIDTH * 5) / 7 - textWidth, UIConstants.FRAME_HEIGHT / 2 + 100 - (80 * 2) / 3);
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
