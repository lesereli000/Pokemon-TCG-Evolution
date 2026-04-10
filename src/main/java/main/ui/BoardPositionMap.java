package main.ui;

import java.awt.Rectangle;
import java.util.EnumMap;
import java.util.Map;

public class BoardPositionMap {
    private final Map<DropZoneType, Rectangle> zones;

    public BoardPositionMap() {
        zones = new EnumMap<>(DropZoneType.class);

        // P1 Active
        int p1ActiveX = (UIConstants.FRAME_WIDTH / 2) - (UIConstants.CARD_WIDTH / 2);
        int p1ActiveY = (UIConstants.FRAME_HEIGHT / 2) + (UIConstants.CARD_HEIGHT / 16)
                - UIConstants.ACTIVE_VERTICAL_OFFSET;
        zones.put(DropZoneType.P1_ACTIVE,
                new Rectangle(p1ActiveX, p1ActiveY, UIConstants.CARD_WIDTH, UIConstants.CARD_HEIGHT));

        // P2 Active
        int p2ActiveX = (UIConstants.FRAME_WIDTH / 2) - (UIConstants.CARD_WIDTH / 2);
        int p2ActiveY = (UIConstants.FRAME_HEIGHT / 2) - (UIConstants.CARD_HEIGHT / 16) - UIConstants.CARD_HEIGHT
                - UIConstants.ACTIVE_VERTICAL_OFFSET;
        zones.put(DropZoneType.P2_ACTIVE,
                new Rectangle(p2ActiveX, p2ActiveY, UIConstants.CARD_WIDTH, UIConstants.CARD_HEIGHT));

        // P1 Bench Slots
        for (int i = 0; i < 5; i++) {
            int x = (UIConstants.MARGIN_SIDE * 3) / 2 + (UIConstants.CARD_WIDTH * 2)
                    + UIConstants.BENCH_HORIZONTAL_OFFSET
                    + (i * (UIConstants.BENCH_HORIZONTAL_INCREMENT + UIConstants.CARD_WIDTH));
            int y = UIConstants.FRAME_HEIGHT - UIConstants.CARD_HEIGHT - UIConstants.MARGIN_BOTTOM
                    - UIConstants.BENCH_VERTICAL_OFFSET;
            zones.put(DropZoneType.valueOf("P1_BENCH_" + i),
                    new Rectangle(x, y, UIConstants.CARD_WIDTH, UIConstants.CARD_HEIGHT));
        }

        // P2 Bench Slots
        for (int i = 0; i < 5; i++) {
            int x = (UIConstants.FRAME_WIDTH) - (UIConstants.MARGIN_SIDE * 3) / 2 - (UIConstants.CARD_WIDTH * 3)
                    - UIConstants.BENCH_HORIZONTAL_OFFSET
                    - (i * (UIConstants.BENCH_HORIZONTAL_INCREMENT + UIConstants.CARD_WIDTH));
            int y = UIConstants.MARGIN_TOP;
            zones.put(DropZoneType.valueOf("P2_BENCH_" + i),
                    new Rectangle(x, y, UIConstants.CARD_WIDTH, UIConstants.CARD_HEIGHT));
        }
    }

    public Map<DropZoneType, Rectangle> getZones() {
        return zones;
    }
}
