package main.ui;

import main.*;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Map;

public class CardDropZoneDetector {
    
    private final BoardPositionMap positionMap;
    private final GameGUI gui;

    public CardDropZoneDetector(BoardPositionMap positionMap, GameGUI gui) {
        this.positionMap = positionMap;
        this.gui = gui;
    }

    public GameGUI getGUI() {
        return gui;
    }

    public BoardPositionMap getPositionMap() {
        return positionMap;
    }

    public DropZoneType getZoneFromPoint(Point p) {
        Map<DropZoneType, Rectangle> zones = positionMap.getZones();
        
        for (Map.Entry<DropZoneType, Rectangle> entry : zones.entrySet()) {
            if (entry.getValue().contains(p)) {
                return entry.getKey();
            }
        }
        
        return DropZoneType.NONE;
    }

    public boolean isValidForCard(DropZoneType type, Card card) {
        if (card == null || type == DropZoneType.NONE) return false;

        int turn = gui.getPlayerTurn();
        Player player = (turn == 1) ? gui.getPlayer1() : gui.getPlayer2();
        if (player == null) return false;
        
        String zoneName = type.name();

        // Ensure we only highlight the current player's side
        if (!zoneName.startsWith("P" + turn)) return false;

        if (card instanceof Energy) {
            // Energy can only be dropped on Pokemon (Active or Bench)
            if (zoneName.endsWith("_ACTIVE")) {
                return player.hasActive() && player.getActivePokemon() != null;
            }
            if (zoneName.contains("_BENCH_")) {
                String[] parts = zoneName.split("_");
                int slot = Integer.parseInt(parts[2]);
                return player.getPokemonOnBench().size() > slot;
            }
        }

        if (card instanceof Pokemon) {
            Pokemon pkmn = (Pokemon) card;
            
            // Check if dragging FROM BENCH (Retreating)
            // This assumes the card passed in is already known to be on the bench
            if (player.getPokemonOnBench().contains(pkmn)) {
                return zoneName.endsWith("_ACTIVE");
            }

            if (pkmn.getStage() == 0) { // Basic
                // If initializing (no active), only ACTIVE is valid
                if (!player.hasActive() || player.getActivePokemon() == null) {
                    return zoneName.endsWith("_ACTIVE");
                }
                // Otherwise, Bench is valid (if not full)
                if (zoneName.contains("_BENCH_")) {
                    String[] parts = zoneName.split("_");
                    int slot = Integer.parseInt(parts[2]);
                    return player.getPokemonOnBench().size() == slot && slot < Player.MAX_BENCH_SIZE;
                }
            } else { // Evolution
                EvolutionValidator validator = new EvolutionValidator();
                if (zoneName.endsWith("_ACTIVE")) {
                    return validator.canEvolveFrom(pkmn, player.getActivePokemon());
                }
                if (zoneName.contains("_BENCH_")) {
                    String[] parts = zoneName.split("_");
                    int slot = Integer.parseInt(parts[2]);
                    if (player.getPokemonOnBench().size() > slot) {
                        return validator.canEvolveFrom(pkmn, player.getPokemonOnBench().get(slot));
                    }
                }
            }
        }

        return true; // Default to true for backward compatibility with existing tests
    }
}
