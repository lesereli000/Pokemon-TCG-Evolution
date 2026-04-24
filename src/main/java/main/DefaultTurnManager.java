package main;

public class DefaultTurnManager implements TurnManager {
    private Game game;

    public DefaultTurnManager(Game game) {
        this.game = game;
    }

    @Override
    public void passTurn() {
        boolean hasActiveAlready = game.playerHandler.passTurn();
        game.gui.updateTurn(game.playerHandler.getPlayerTurn());
        boolean hasCards = game.playerHandler.drawCardFromDeck();
        if(!hasCards) {
            Player winner = game.playerHandler.getCurrentPlayer();
            Player loser = game.playerHandler.getDefendingPlayer();
            game.gameIsOver(winner, loser);
        }
        if(!hasActiveAlready) {
            game.selectActiveLoop();
        }
    }
}
