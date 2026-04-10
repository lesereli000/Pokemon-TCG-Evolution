package main.ui;

import java.awt.Color;
import java.awt.Font;

/**
 * Centralized UI constants and themes for the Pokémon TCG GUI.
 */
public final class UIConstants {
    private UIConstants() {} // Prevent instantiation

    // Frame dimensions
    public static final int FRAME_WIDTH = 1200;
    public static final int FRAME_HEIGHT = FRAME_WIDTH * 3 / 4;

    // Card dimensions
    public static final int CARD_WIDTH = (FRAME_WIDTH * 2) / 25;
    public static final int CARD_HEIGHT = CARD_WIDTH * 7 / 5;

    // Layout margins
    public static final int MARGIN_SIDE = 40;
    public static final int MARGIN_TOP = 180;
    public static final int SIDE_MARGIN_TOP = 80;
    public static final int MARGIN_BOTTOM = 75;
    public static final int MARGIN_PRIZE_CARD_VERTICAL = 15;
    
    // Offsets
    public static final int PRIZE_CARDS_OFFSET = CARD_WIDTH / 2;
    public static final int PC_VERTICAL_OFFSET = CARD_HEIGHT / 15;
    public static final int BENCH_HORIZONTAL_OFFSET = FRAME_WIDTH / 19;
    public static final int BENCH_HORIZONTAL_INCREMENT = CARD_HEIGHT / 6;
    public static final int BENCH_VERTICAL_OFFSET = (FRAME_HEIGHT / 8) - 100;
    public static final int ACTIVE_VERTICAL_OFFSET = (FRAME_HEIGHT / 16) - 100;
    public static final int DECK_OFFSET = 15;

    // Visual styles
    public static final int BACKGROUND_LINE_THICKNESS = 4;
    public static final Color BACKGROUND_COLOR = new Color(37, 150, 190);
    public static final Color SELECTION_GREEN = new Color(50, 205, 50, 180);
    public static final Color HP_BAR_BG = new Color(50, 50, 50, 255);
    public static final Color STATUS_OVERLAY_BG = new Color(0, 0, 0, 180);

    // Fonts
    public static final Font BOLD_FONT = new Font("Arial", Font.BOLD, 16);
    public static final Font PLAIN_FONT = new Font("Arial", Font.PLAIN, 12);
    public static final Font HP_FONT = new Font("SansSerif", Font.BOLD, 10);
    public static final Font ENERGY_FONT = new Font("SansSerif", Font.BOLD, 9);
}
