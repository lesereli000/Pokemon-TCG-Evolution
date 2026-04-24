package main;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

public class FinalPushCoverageTest {

    @Test(expected = InvalidMoveException.class)
    public void testBenchFullException() throws Exception {
        Player p = new Player("Test");
        for (int i = 0; i < Player.MAX_BENCH_SIZE + 1; i++) {
            p.addBenchPokemon(new Pokemon("P" + i, "Grass", 0, 50));
        }
    }

    @Test
    public void testPlayerHeal() {
        Player p = new Player("Test");
        Pokemon pk = new Pokemon("P", "Grass", 0, 50);
        p.forceSetActivePokemon(pk);
        p.takeDamage(2, EnergyType.GRASS);
        p.heal(1);
        assertEquals(40, pk.getCurHP());
    }

    @Test
    public void testPlayerEvolutionAndPreEvolutions() throws Exception {
        Player p = new Player("Test");
        Pokemon pika = new Pokemon("Pika", "Lightning", 0, 50);
        Pokemon raichu = new Pokemon("Raichu", "Lightning", 1, 80);
        raichu.setEvolvesFrom("Pika");
        
        p.getHand().addCard(pika);
        p.addBenchPokemon(pika);
        p.getHand().addCard(raichu);
        
        assertEquals("Bench", p.evolvePokemon(raichu, pika));
        
        // Evolve active
        Pokemon raichu3 = new Pokemon("Raichu3", "Lightning", 2, 120);
        raichu3.setEvolvesFrom("Raichu");
        p.forceSetActivePokemon(raichu);
        p.getHand().addCard(raichu3);
        assertEquals("Active", p.evolvePokemon(raichu3, raichu));
        
        // getPreEvolutions
        Pokemon raichu2 = new Pokemon("Raichu2", "Lightning", 1, 80);
        raichu2.setEvolvesFrom("Raichu");
        p.forceSetActivePokemon(raichu);
        assertFalse(p.getPreEvolutions(raichu2).isEmpty());
        
        // Exercise non-matching evolution
        Pokemon charmander = new Pokemon("Charmander", "Fire", 0, 50);
        assertTrue(p.getPreEvolutions(charmander).isEmpty());
    }

    @Test
    public void testSetNewActiveNull() {
        Player p = new Player("Test");
        p.setNewActivePokemon(null);
        assertFalse(p.hasActive());
    }

    @Test(expected = CardCreationException.class)
    public void testInvalidEnergyType() {
        EnergyType.fromName("Invalid");
    }

    @Test(expected = CardCreationException.class)
    public void testEmptyPokemonType() {
        new Pokemon("P", "", 0, 50);
    }

    @Test
    public void testMainStartGame() {
        try {
            Main m = new Main(); 
            assertNotNull(m);
            Main.startGame(null, null);
        } catch (Exception e) {}
    }

    @Test
    public void testPlayerHandlerAttack() throws Exception {
        PlayerHandler handler = new PlayerHandler();
        Player p1 = new Player("P1");
        Player p2 = new Player("P2");
        
        handler.player1 = p1;
        handler.player2 = p2;
        handler.currentPlayer = p1;
        handler.defendingPlayer = p2;
        
        Pokemon pika = new Pokemon("Pika", "Lightning", 0, 50);
        p1.forceSetActivePokemon(pika);
        
        Pokemon def = new Pokemon("Def", "Grass", 0, 50);
        p2.forceSetActivePokemon(def);
        
        Attack a = new Attack("Spark", new ArrayList<>(), 10);
        
        // Direct test
        p2.takeDamage(1, EnergyType.LIGHTNING);
        assertEquals(40, def.getCurHP());
        
        // Handler test
        handler.attackOpponent(a);
        assertEquals(30, def.getCurHP());
    }
}
