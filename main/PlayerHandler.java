package main;

public class PlayerHandler {

    protected Player player1;
    protected Player player2;
    protected int playerTurn;
    protected Player currentPlayer;
    protected Player defendingPlayer;

    public void completePlayerSetup(String coinFlipResult) {
        createPlayers();
        setPlayerTurns(coinFlipResult);
        setupBothDecks();
        setupBothHands();
    }

    protected void createPlayers() {
        player1 = new Player("Player 1");
        player2 = new Player("Player 2");
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

    protected void setupBothDecks() {
        player1.createCustomDeck();
        player2.createCustomDeck();
    }

    protected void setupBothHands() {
        player1.drawStartingHand();
        player2.drawStartingHand();
    }


}
