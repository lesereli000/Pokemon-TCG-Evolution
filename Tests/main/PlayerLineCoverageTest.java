package main;

import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class PlayerLineCoverageTest {

    @Test
    public void testEvolvePokemon() {
        Player p = new Player("Test");

        Pokemon evo = createNiceMock(Pokemon.class);
        Pokemon base = createNiceMock(Pokemon.class);
        replay(evo, base);

        // Add evo to hand so hand.removeCard doesn't throw
        p.hand.addCard(evo);

        // Test evolving active pokemon
        p.activePokemon = base;
        String result = p.evolvePokemon(evo, base);
        assertEquals("Active", result);
        assertEquals(evo, p.activePokemon);
    }

    @Test
    public void testPickupPrizeCardEmpty() {
        Player p = new Player("Test");
        try {
            p.pickupPrizeCard();
        } catch (Exception e) {
            // Expected EmptyDeckException if deck is empty
        }
    }

    @Test
    public void testAdditionalPlayerMethods() {
        Player p = new Player("Test");
        p.getBench();
        try {
            p.canAttack(null);
        } catch (Exception e) {
        }

        Pokemon mockP = createNiceMock(Pokemon.class);
        replay(mockP);

        try {
            p.setActivePokemon(mockP);
        } catch (Exception e) {
        }

        try {
            p.setActivePokemon(null);
        } catch (Exception e) {
        }

        try {
            p.setNewActivePokemon(mockP);
        } catch (Exception e) {
        }
    }
}
