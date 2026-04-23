package main.ui;

import main.Card;
import main.Pokemon;

/**
 * A dedicated service for validating evolution rules.
 * Extracted as a "Seam" to allow testing without the full GUI environment.
 */
public class EvolutionValidator {

    /**
     * Checks if a card can evolve from a specific base pokemon.
     * @param evolution The card attempting to evolve (e.g. Stage 1 or Stage 2)
     * @param base The base pokemon already on the board
     * @return true if evolution is valid
     */
    public boolean canEvolveFrom(Card evolution, Card base) {
        if (!(evolution instanceof Pokemon evolPkmn) || !(base instanceof Pokemon basePkmn)) {
            return false;
        }

        // Must have a valid "evolvesFrom" name
        String evolvesFrom = evolPkmn.getEvolvesFrom();
        if (evolvesFrom == null || evolvesFrom.isEmpty()) {
            return false;
        }

        // Name must match and stage must be sequential
        return evolvesFrom.equalsIgnoreCase(basePkmn.getName()) && 
               evolPkmn.getStage() == basePkmn.getStage() + 1;
    }
}
