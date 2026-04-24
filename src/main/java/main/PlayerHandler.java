package main;

import java.util.ArrayList;

public class PlayerHandler {

    protected Player player1;
    protected Player player2;
    protected int playerTurn;
    protected Player currentPlayer;
    protected Player defendingPlayer;
    protected ArrayList<Pokemon> playedThisTurn = new ArrayList<>();

    public PlayerHandler() {
        this.player1 = new Player("Player 1");
        this.player2 = new Player("Player 2");
    }

    public PlayerHandler(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public void completePlayerSetup(String coinFlipResult, String deckFileName) {
        setPlayerTurns(coinFlipResult);
        setupDecks(p1Deck, p2Deck);
        setupBothHands();
        setupPrizeCards();
    }

    protected void setupPrizeCards() {
        player1.drawPrizeCards();
        player2.drawPrizeCards();
    }



    protected void setPlayerTurns(String coinFlipResult) {
        currentPlayer = coinFlipResult.equals("Heads") ? player1 : player2;
        defendingPlayer = coinFlipResult.equals("Heads") ? player2 : player1;
        playerTurn = coinFlipResult.equals("Heads") ? 1 : 2;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    protected void setupDecks(String p1Deck, String p2Deck) {
        player1.createCustomDeck(p1Deck);
        player2.createCustomDeck(p2Deck);
    }

    protected void setupBothHands() {
        player1.drawStartingHand();
        player2.drawStartingHand();
    }


    public void addToBench(Pokemon lastSelectedCard) {
        currentPlayer.addBenchPokemon(lastSelectedCard);
        currentPlayer.removeFromHand(lastSelectedCard);
        playedThisTurn.add(lastSelectedCard);
    }

    public ArrayList<Card> getCurrentPlayerHand() {
        return currentPlayer.handAsList();
    }

    public boolean activeCanAddEnergy() {
        return currentPlayer.canAddEnergy();
    }

    public void addEnergyToPokemon(Energy energy, Pokemon pokemon) {
        currentPlayer.addEnergyToPokemon(pokemon, energy);
    }

    public boolean passTurn() {
        currentPlayer.passTurn();
        swapPlayerTurns();
        playedThisTurn.clear();
        return currentPlayer.hasActive();
    }

    public void swapPlayerTurns() {
        playerTurn = playerTurn == 1 ? 2 : 1;
        Player tempPlayer = defendingPlayer;
        defendingPlayer = currentPlayer;
        currentPlayer = tempPlayer;
    }

    protected ArrayList<Card> getOnlyPokemonFromBench(int isCurrentPlayer) {
        Deck bench;
        if(isCurrentPlayer == 1) {
            bench = currentPlayer.getBench();
        } else {
            bench = defendingPlayer.getBench();
        }
        return bench.getOnlyPokemon();
    }

    public boolean playerCanAttack() {
        return currentPlayer.canAttack() && defendingPlayer.hasActive();
    }

    public ArrayList<Attack> getCurrentPlayerAttacks() {
        Pokemon currentPlayerActive = currentPlayer.getActivePokemon();
        return currentPlayerActive.getAttacks();
    }

    public boolean attackOpponent(Attack selectedAttack) {
        if(!currentPlayer.canAttack(selectedAttack)) {
            return false;
        }
        int damage = selectedAttack.getDamage();
        int dmgCounters = damage/10;
        Pokemon activePokemon = currentPlayer.getActivePokemon();
        defendingPlayer.takeDamage(dmgCounters, activePokemon.type);
        return true;
    }

    public Player getDefendingPlayer() {
        return defendingPlayer;
    }

    public boolean canRetreat() {
        return !currentPlayer.benchIsEmpty();
    }

    public void setNewActive(Card newActive) {
        currentPlayer.retreat((Pokemon)newActive);
    }

    public boolean isDefendingDead() {
        Pokemon defendingPokemon = defendingPlayer.getActivePokemon();
        return defendingPokemon.getCurHP() <= 0;
    }

    public void killDefenderActive(Pokemon newActive) {
        defendingPlayer.setNewActivePokemon(newActive);
    }

    public boolean drawCardFromDeck() {
        return currentPlayer.drawCard();
    }

    public int activePickupPrizeCard() {
        currentPlayer.pickupPrizeCard();
        return currentPlayer.getNumPrizeCards();
    }

    public String evolve(Pokemon evolution, Pokemon evolvesFrom){
        if(playedThisTurn.contains(evolvesFrom)) {
            return "JustPlayed";
        }

        return currentPlayer.evolvePokemon(evolution, evolvesFrom);
    }

    public ArrayList<Card> getOnlyPreEvolutionsFromActivePlayer(Pokemon evolution) {
        return currentPlayer.getPreEvolutions(evolution);
    }

    public ArrayList<Card> getAllPlayerPokemon() {
        ArrayList<Card> playerPokemon = currentPlayer.getOnlyPokemonFromHand();
        playerPokemon.add(currentPlayer.getActivePokemon());
        ArrayList<Card> benchPokemon = currentPlayer.getPokemonOnBench();
        playerPokemon.addAll(benchPokemon);
        return playerPokemon;
    }

    public ArrayList<Card> getAllPlayerEnergy() {
        return currentPlayer.getAllEnergyFromHand();
    }

    public Pokemon getActivePokemon() { return currentPlayer.getActivePokemon(); }

    public ArrayList<Card> getHandPokemon() { return currentPlayer.getOnlyPokemonFromHand(); }
}

