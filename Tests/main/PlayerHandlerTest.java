package main;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class PlayerHandlerTest {
    @Test
    public void testSetupPlayers() {

        PlayerHandler playerHandler = new PlayerHandler();
        playerHandler.createPlayers();
        Player player1 = playerHandler.player1;
        Player player2 = playerHandler.player2;
        assertEquals("Player 1", player1.getName());
        assertEquals("Player 2", player2.getName());
    }

    @Test
    public void testCompletePlayerSetupHeads() {
        PlayerHandler handler = new PlayerHandler();
        handler.completePlayerSetup("Heads");

        assertEquals("Player 1", handler.getCurrentPlayer().getName());
        assertEquals(1, handler.getPlayerTurn());
    }

    @Test
    public void testCompletePlayerSetupTails() {
        PlayerHandler handler = new PlayerHandler();
        handler.completePlayerSetup("Tails");

        assertEquals("Player 2", handler.getCurrentPlayer().getName());
        assertEquals(2, handler.getPlayerTurn());
    }


    @Test
    public void testSetupBothDecks() {
        Player player1 = createMock(Player.class);
        Player player2 = createMock(Player.class);
        player1.createCustomDeck();
        player2.createCustomDeck();
        replay(player1, player2);

        PlayerHandler handler = new PlayerHandler();
        handler.player1 = player1;
        handler.player2 = player2;

        handler.setupBothDecks();
        verify(player1, player2);
    }

    @Test
    public void testAddToBench() {
        Player player = createMock(Player.class);
        Pokemon pokemon = createMock(Pokemon.class);

        player.addBenchPokemon(pokemon);
        player.removeFromHand(pokemon);
        replay(player, pokemon);

        PlayerHandler handler = new PlayerHandler();
        handler.currentPlayer = player;
        handler.addToBench(pokemon);

        verify(player, pokemon);
    }




    @Test
    public void testPlayerHand() {
        Player player1 = createMock(Player.class);
        Player player2 = createMock(Player.class);
        player1.drawStartingHand();
        player2.drawStartingHand();
        replay(player1, player2);

        PlayerHandler handler = new PlayerHandler();
        handler.player1 = player1;
        handler.player2 = player2;
        handler.setupBothHands();
        verify(player1, player2);
    }

    @Test
    public void testPlayerTurnResultHeads() {
        Player player1 = createMock(Player.class);
        Player player2 = createMock(Player.class);

        PlayerHandler handler = new PlayerHandler();
        handler.player1 = player1;
        handler.player2 = player2;
        handler.setPlayerTurns("Heads");
        assertEquals(player1, handler.currentPlayer);
        assertEquals(player2, handler.defendingPlayer);
    }

    @Test
    public void testPlayerTurnResultTails() {
        Player player1 = createMock(Player.class);
        Player player2 = createMock(Player.class);

        PlayerHandler handler = new PlayerHandler();
        handler.player1 = player1;
        handler.player2 = player2;
        handler.setPlayerTurns("Tails");
        assertEquals(player2, handler.currentPlayer);
        assertEquals(player1, handler.defendingPlayer);
    }

    @Test
    public void testGetCurrentPlayerHand() {
        Player player1 = createMock(Player.class);
        ArrayList<Card> hand = createMock(ArrayList.class);

        PlayerHandler playerHandler = new PlayerHandler();
        playerHandler.currentPlayer = player1;
        expect(player1.handAsList()).andReturn(hand);
        replay(player1);

        ArrayList<Card> actual = playerHandler.getCurrentPlayerHand();
        assertEquals(hand, actual);

        verify(player1);
    }

    @Test
    public void testSwapPlayerTurn() {
        PlayerHandler handler = new PlayerHandler();
        Player player1 = createMock(Player.class);
        Player player2 = createMock(Player.class);

        handler.player1 = player1;
        handler.player2 = player2;
        handler.playerTurn = 1;
        handler.currentPlayer = player1;
        handler.defendingPlayer = player2;

        handler.swapPlayerTurns();

        assertEquals(player2, handler.currentPlayer);
        assertEquals(player1, handler.defendingPlayer);
        assertEquals(2, handler.playerTurn);
    }

    @Test
    public void testPassTurn() {
        PlayerHandler handler = new PlayerHandler();
        Player player1 = createMock(Player.class);
        Player player2 = createMock(Player.class);

        handler.player1 = player1;
        handler.player2 = player2;
        handler.playerTurn = 1;
        handler.currentPlayer = player1;
        handler.defendingPlayer = player2;
        expect(player2.hasActive()).andReturn(true);
        replay(player2);
        assertTrue(handler.passTurn());
        verify(player2);

        assertEquals(player2, handler.currentPlayer);
        assertEquals(player1, handler.defendingPlayer);
        assertEquals(2, handler.playerTurn);
    }

    @Test
    public void testPlayerCanAttack() {
        Player player1 = createMock(Player.class);
        Player player2 = createMock(Player.class);

        PlayerHandler handler = new PlayerHandler();
        handler.currentPlayer = player1;
        handler.defendingPlayer = player2;

        expect(player1.canAttack()).andReturn(true);
        expect(player2.hasActive()).andReturn(true);

        replay(player1, player2);

        assertTrue(handler.playerCanAttack());

        verify(player1, player2);
    }

    @Test
    public void testPlayerCanAttackFalse1() {
        Player player1 = createMock(Player.class);
        Player player2 = createMock(Player.class);

        PlayerHandler handler = new PlayerHandler();
        handler.currentPlayer = player1;
        handler.defendingPlayer = player2;

        expect(player1.canAttack()).andReturn(false);
        //expect(player2.hasActive()).andReturn(true);  //First if will return false, not even check

        replay(player1, player2);

        assertFalse(handler.playerCanAttack());

        verify(player1, player2);
    }

    @Test
    public void testPlayerCanAttackFalse2() {
        Player player1 = createMock(Player.class);
        Player player2 = createMock(Player.class);

        PlayerHandler handler = new PlayerHandler();
        handler.currentPlayer = player1;
        handler.defendingPlayer = player2;

        expect(player1.canAttack()).andReturn(true);
        expect(player2.hasActive()).andReturn(false);

        replay(player1, player2);

        assertFalse(handler.playerCanAttack());

        verify(player1, player2);
    }

    @Test
    public void testGetCurrentPlayerAttacks() {
        Player player = createMock(Player.class);
        Pokemon p = createMock(Pokemon.class);
        ArrayList<Attack> hand = createMock(ArrayList.class);

        expect(player.getActivePokemon()).andReturn(p);
        expect(p.getAttacks()).andReturn(hand);

        replay(p, player);

        PlayerHandler handler = new PlayerHandler();
        handler.currentPlayer = player;

        assertEquals(hand, handler.getCurrentPlayerAttacks());
        verify(p, player);
    }

    @Test
    public void testAttackOpponent() {
        Player player1 = createMock(Player.class);
        Player player2 = createMock(Player.class);
        Attack attack = createMock(Attack.class);
        Pokemon p = createMock(Pokemon.class);

        expect(attack.getDamage()).andReturn(20);
        expect(player1.getActivePokemon()).andReturn(p);
        expect(player1.canAttack(attack)).andReturn(true);
        expect(p.getType()).andReturn("ABC");
        player2.takeDamage(2, "ABC");

        replay(player1, player2, p, attack);

        PlayerHandler handler = new PlayerHandler();
        handler.currentPlayer = player1;
        handler.defendingPlayer = player2;
        assertTrue(handler.attackOpponent(attack));

        verify(player1, player2, p, attack);
    }

    @Test
    public void testCantAttackOpponent() {
        Player player1 = createMock(Player.class);
        Attack attack = createMock(Attack.class);

        expect(player1.canAttack(attack)).andReturn(false);

        replay(player1);

        PlayerHandler handler = new PlayerHandler();
        handler.currentPlayer = player1;
        assertFalse(handler.attackOpponent(attack));

        verify(player1);
    }

    @Test
    public void testActiveCanAddEnergy() {
        Player player = createMock(Player.class);
        expect(player.canAddEnergy()).andReturn(true);
        replay(player);

        PlayerHandler handler = new PlayerHandler();
        handler.currentPlayer = player;

        assertTrue(handler.activeCanAddEnergy());
        verify(player);
    }

    @Test
    public void testAddEnergyToPokemon() {
        Player player = createMock(Player.class);
        Energy energy = createMock(Energy.class);
        Pokemon pokemon = createMock(Pokemon.class);

        player.addEnergyToPokemon(pokemon, energy);
        replay(player);

        PlayerHandler handler = new PlayerHandler();
        handler.currentPlayer = player;

        handler.addEnergyToPokemon(energy, pokemon);
        verify(player);
    }

    @Test
    public void testCanRetreatTrue() {
        Player player = createMock(Player.class);
        expect(player.benchIsEmpty()).andReturn(false);
        replay(player);

        PlayerHandler handler = new PlayerHandler();
        handler.currentPlayer = player;

        assertTrue(handler.canRetreat());
        verify(player);
    }

    @Test
    public void testCanRetreatFalse() {
        Player player = createMock(Player.class);
        expect(player.benchIsEmpty()).andReturn(true);
        replay(player);

        PlayerHandler handler = new PlayerHandler();
        handler.currentPlayer = player;

        assertFalse(handler.canRetreat());
        verify(player);
    }

    @Test
    public void testSetNewActive() {
        Player player = createMock(Player.class);
        Pokemon newActive = createMock(Pokemon.class);

        player.retreat(newActive);
        replay(player);

        PlayerHandler handler = new PlayerHandler();
        handler.currentPlayer = player;

        handler.setNewActive(newActive);
        verify(player);
    }

    @Test
    public void testDefendingPokemonDead() {
        Player player = createMock(Player.class);
        Pokemon p = createMock(Pokemon.class);
        expect(player.getActivePokemon()).andReturn(p);
        expect(p.getCurHP()).andReturn(0);

        replay(player, p);

        PlayerHandler handler = new PlayerHandler();
        handler.defendingPlayer = player;
        assertTrue(handler.isDefendingDead());

        verify(player, p);
    }

    @Test
    public void testDefendingPokemonNotDead() {
        Player player = createMock(Player.class);
        Pokemon p = createMock(Pokemon.class);
        expect(player.getActivePokemon()).andReturn(p);
        expect(p.getCurHP()).andReturn(10);

        replay(player, p);

        PlayerHandler handler = new PlayerHandler();
        handler.defendingPlayer = player;
        assertFalse(handler.isDefendingDead());

        verify(player, p);
    }

    @Test
    public void testGetOnlyPokemonFromBenchCurrentPlayer() {
        Player currentPlayer = createMock(Player.class);
        Deck bench = createMock(Deck.class);
        ArrayList<Card> pokemonCards = new ArrayList<>();
        expect(currentPlayer.getBench()).andReturn(bench);
        expect(bench.getOnlyPokemon()).andReturn(pokemonCards);

        replay(currentPlayer, bench);

        PlayerHandler handler = new PlayerHandler();
        handler.currentPlayer = currentPlayer;

        assertEquals(pokemonCards, handler.getOnlyPokemonFromBench(1));
        verify(currentPlayer, bench);
    }

    @Test
    public void testGetOnlyPokemonFromBenchDefendingPlayer() {
        Player defendingPlayer = createMock(Player.class);
        Deck bench = createMock(Deck.class);
        ArrayList<Card> pokemonCards = new ArrayList<>();
        expect(defendingPlayer.getBench()).andReturn(bench);
        expect(bench.getOnlyPokemon()).andReturn(pokemonCards);

        replay(defendingPlayer, bench);

        PlayerHandler handler = new PlayerHandler();
        handler.defendingPlayer = defendingPlayer;

        assertEquals(pokemonCards, handler.getOnlyPokemonFromBench(2));
        verify(defendingPlayer, bench);
    }

    @Test
    public void testGetDefendingPlayer() {
        Player defending = createMock(Player.class);
        PlayerHandler handler = new PlayerHandler();
        handler.defendingPlayer = defending;

        assertEquals(defending, handler.getDefendingPlayer());
    }

    @Test
    public void testKillDefenderActive() {
        Player defending = createMock(Player.class);
        Pokemon newActive = createMock(Pokemon.class);

        defending.setNewActivePokemon(newActive);
        replay(defending);

        PlayerHandler handler = new PlayerHandler();
        handler.defendingPlayer = defending;

        handler.killDefenderActive(newActive);
        verify(defending);
    }

    @Test
    public void testDrawCardFromDeck() {
        Player current = createMock(Player.class);

        expect(current.drawCard()).andReturn(true);
        replay(current);

        PlayerHandler handler = new PlayerHandler();
        handler.currentPlayer = current;

        handler.drawCardFromDeck();
        verify(current);
    }

    @Test
    public void testSetupPrizeCards() {
        Player p1 = createMock(Player.class);
        Player p2 = createMock(Player.class);

        p1.drawPrizeCards();
        p2.drawPrizeCards();

        replay(p1, p2);

        PlayerHandler handler = new PlayerHandler();
        handler.player1 = p1;
        handler.player2 = p2;
        handler.setupPrizeCards();

        verify(p1, p2);
    }

    @Test
    public void testActivePickupPrizeCard() {
        Player p1 = createMock(Player.class);

        p1.pickupPrizeCard();
        replay(p1);

        PlayerHandler handler = new PlayerHandler();
        handler.currentPlayer = p1;
        handler.activePickupPrizeCard();
        verify(p1);
    }


}
