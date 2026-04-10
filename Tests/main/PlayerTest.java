package main;

import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

public class PlayerTest {

    private Player player;

    @Before
    public void setUp() {
        player = new Player("Test Player");
    }

    @Test
    public void testDefaultConstructor() {
        Player p = new Player();
        assertEquals("Unnamed Player", p.getName());
    }

    @Test
    public void testNamedConstructor() {
        assertEquals("Test Player", player.getName());
        assertEquals(0, player.deckSize());
        assertFalse(player.hasActive());
        assertTrue(player.canAddEnergy());
    }

    @Test
    public void testDrawCardSuccess() {
        // Deck starts empty, add a card
        Pokemon p = new Pokemon("Pikachu", "Lightning", 0, 60);
        player.deck.addCard(p);
        
        int initialDeckSize = player.deckSize();
        boolean success = player.drawCard();
        
        assertTrue(success);
        assertEquals(initialDeckSize - 1, player.deckSize());
        assertEquals(1, player.handAsList().size());
        assertEquals(p, player.handAsList().get(0));
    }

    @Test
    public void testDrawCardEmptyDeck() {
        assertEquals(0, player.deckSize());
        boolean success = player.drawCard();
        assertFalse(success);
    }

    @Test
    public void testSetActivePokemon() {
        Pokemon p = new Pokemon("Charmander", "Fire", 0, 50);
        player.hand.addCard(p);
        
        player.setActivePokemon(p);
        
        assertEquals(p, player.getActivePokemon());
        assertEquals("Charmander", player.getActivePokemonName());
        assertTrue(player.hasActive());
        assertEquals(0, player.handAsList().size());
    }

    @Test
    public void testAddBenchPokemonSuccess() {
        Pokemon p = new Pokemon("Squirtle", "Water", 0, 50);
        player.addBenchPokemon(p);
        
        assertEquals(1, player.getPokemonOnBench().size());
        assertEquals(p, player.getPokemonOnBench().get(0));
    }

    @Test(expected = InvalidMoveException.class)
    public void testAddBenchPokemonFull() {
        for (int i = 0; i < Player.MAX_BENCH_SIZE; i++) {
            player.addBenchPokemon(new Pokemon("Bench " + i, "Grass", 0, 40));
        }
        // This should throw exception
        player.addBenchPokemon(new Pokemon("Overflow", "Grass", 0, 40));
    }

    @Test
    public void testDrawPrizeCards() {
        for (int i = 0; i < Player.PRIZE_CARD_SIZE; i++) {
            player.deck.addCard(new Energy(EnergyType.COLORLESS));
        }
        
        player.drawPrizeCards();
        assertEquals(Player.PRIZE_CARD_SIZE, player.getNumPrizeCards());
        assertEquals(0, player.deckSize());
    }

    @Test
    public void testPickupPrizeCard() {
        Energy e = new Energy(EnergyType.COLORLESS);
        player.prizeCards.addCard(e);
        
        player.pickupPrizeCard();
        assertEquals(0, player.getNumPrizeCards());
        assertEquals(1, player.handAsList().size());
        assertEquals(e, player.handAsList().get(0));
    }

    @Test
    public void testPassTurn() {
        Pokemon p = new Pokemon("P", "Grass", 0, 10);
        player.hand.addCard(p);
        player.setActivePokemon(p);
        
        Energy e = new Energy(EnergyType.COLORLESS);
        player.hand.addCard(e);
        player.addEnergyToPokemon(p, e);
        assertFalse(player.canAddEnergy());
        
        player.passTurn();
        assertTrue(player.canAddEnergy());
    }

    @Test
    public void testEvolveActive() {
        Pokemon basic = new Pokemon("Bulbasaur", "Grass", 0, 40);
        Energy energy = new Energy(EnergyType.GRASS);
        basic.addEnergy(energy);
        
        Pokemon stage1 = new Pokemon("Ivysaur", "Grass", 1, 60);
        stage1.setEvolvesFrom("Bulbasaur");
        
        player.hand.addCard(stage1);
        player.hand.addCard(basic);
        player.setActivePokemon(basic);
        
        String result = player.evolvePokemon(stage1, basic);
        
        assertEquals("Active", result);
        assertEquals(stage1, player.getActivePokemon());
        // Verify energy transfer (kills survivor in Player.java)
        assertEquals(1, stage1.getEnergies().size());
        assertEquals(energy, stage1.getEnergies().get(0));
        assertEquals(0, player.handAsList().size());
    }

    @Test
    public void testEvolveBench() {
        Pokemon basic = new Pokemon("Bulbasaur", "Grass", 0, 40);
        Pokemon stage1 = new Pokemon("Ivysaur", "Grass", 1, 60);
        stage1.setEvolvesFrom("Bulbasaur");
        
        player.hand.addCard(stage1);
        player.addBenchPokemon(basic);
        
        String result = player.evolvePokemon(stage1, basic);
        
        assertEquals("Bench", result);
        assertEquals(stage1, player.getPokemonOnBench().get(0));
        assertEquals(0, player.handAsList().size());
    }

    @Test
    public void testGetPreEvolutions() {
        Pokemon active = new Pokemon("Bulbasaur", "Grass", 0, 40);
        Pokemon bench = new Pokemon("Bulbasaur", "Grass", 0, 40);
        Pokemon stage1 = new Pokemon("Ivysaur", "Grass", 1, 60);
        stage1.setEvolvesFrom("Bulbasaur");
        
        player.hand.addCard(active);
        player.setActivePokemon(active);
        player.addBenchPokemon(bench);
        
        ArrayList<Card> preEvs = player.getPreEvolutions(stage1);
        assertEquals(2, preEvs.size());
        assertTrue(preEvs.contains(active));
        assertTrue(preEvs.contains(bench));
    }

    @Test
    public void testRetreat() {
        Pokemon active = new Pokemon("Active", "Grass", 0, 40);
        Pokemon benched = new Pokemon("Benched", "Grass", 0, 40);
        
        player.hand.addCard(active);
        player.setActivePokemon(active);
        player.addBenchPokemon(benched);
        
        player.retreat(benched);
        
        assertEquals(benched, player.getActivePokemon());
        assertEquals(active, player.getPokemonOnBench().get(0));
    }

    @Test
    public void testCanAttack() {
        Pokemon p = createMock(Pokemon.class);
        expect(p.canAttack()).andReturn(true);
        replay(p);
        
        player.forceSetActivePokemon(p);
        assertTrue(player.canAttack());
        verify(p);
    }

    @Test
    public void testTakeDamage() {
        Pokemon p = createMock(Pokemon.class);
        p.takeDamage(10, EnergyType.FIRE);
        expectLastCall();
        p.takeDamage(0, EnergyType.FIRE); // Boundary test
        expectLastCall();
        replay(p);
        
        player.forceSetActivePokemon(p);
        player.takeDamage(10, EnergyType.FIRE);
        player.takeDamage(0, EnergyType.FIRE);
        verify(p);
    }

    @Test
    public void testGetActiveHP() {
        Pokemon p = createMock(Pokemon.class);
        expect(p.getCurHP()).andReturn(50);
        replay(p);
        
        player.forceSetActivePokemon(p);
        assertEquals(50, player.getActiveHP());
        verify(p);
    }

    @Test
    public void testBenchIsEmpty() {
        assertTrue(player.benchIsEmpty());
        player.addBenchPokemon(new Pokemon("P", "Fire", 0, 10));
        assertFalse(player.benchIsEmpty());
    }

    @Test
    public void testGetOnlyPokemonAndEnergyFromHand() {
        Pokemon p = new Pokemon("P", "Fire", 0, 10);
        Energy e = new Energy(EnergyType.GRASS);
        player.hand.addCard(p);
        player.hand.addCard(e);
        
        assertEquals(1, player.getOnlyPokemonFromHand().size());
        assertEquals(p, player.getOnlyPokemonFromHand().get(0));
        assertEquals(1, player.getAllEnergyFromHand().size());
        assertEquals(e, player.getAllEnergyFromHand().get(0));
    }

    @Test
    public void testDrawStartingHandAndRestart() {
        // Prepare deck with 7 cards but no basic pokemon
        // Then add a basic pokemon that will be drawn after restart
        Energy energy = new Energy(EnergyType.COLORLESS);
        for (int i = 0; i < 7; i++) {
            player.deck.addCard(energy);
        }
        
        // Add a basic pokemon that will be found eventually 
        // to stop the recursion in drawStartingHand -> restartHand.
        Pokemon basic = new Pokemon("Stop Recursion", "Grass", 0, 10);
        player.deck.addCard(basic);
        
        int initialDeckSize = player.deckSize();
        player.drawStartingHand();
        
        assertTrue("Hand should eventually contain a basic pokemon after restart", 
                   player.hand.numberBasicPokemon() > 0);
        // Verify deck size remains consistent (kills mutants in restartHand)
        assertEquals(initialDeckSize - 7, player.deckSize());
    }

    @Test
    public void testRestartHandLogic() {
        // Prepare deck with basic pokemon to break recursion
        Pokemon basic = new Pokemon("Basic", "Grass", 0, 10);
        player.deck.addCard(basic);
        for (int i = 0; i < 15; i++) {
            player.deck.addCard(new Energy(EnergyType.GRASS));
        }

        // Fill hand with 7 cards as restartHand() expects HAND_SIZE (7) removals
        for (int i = 0; i < 7; i++) {
            player.hand.addCard(new Energy(EnergyType.GRASS));
        }
        
        player.restartHand(); // Should move hand to deck and draw again
        
        assertTrue("Deck should have received the cards from hand", player.deckSize() >= 16);
        assertEquals(7, player.handAsList().size());
    }

    @Test
    public void testHeal() {
        Pokemon p = createMock(Pokemon.class);
        p.heal(10);
        expectLastCall();
        p.heal(0);
        expectLastCall();
        replay(p);
        
        player.forceSetActivePokemon(p);
        player.heal(10);
        player.heal(0);
        verify(p);
    }

    @Test
    public void testCreateCustomDeck() {
        // This relies on Overgrowth.txt existing in resources
        player.createCustomDeck();
        assertTrue("Deck should be populated from file", player.deckSize() > 0);
    }

    @Test
    public void testCreateCustomDeckOverload() {
        // This relies on WaterDeck.txt existing in resources
        player.createCustomDeck("WaterDeck.txt");
        assertEquals("Water Deck should have exactly 60 cards", 60, player.deckSize());
    }
}
