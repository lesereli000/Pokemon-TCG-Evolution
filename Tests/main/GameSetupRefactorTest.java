package main;

import main.ui.GUI;
import org.easymock.EasyMock;
import org.junit.Test;
import java.util.Random;
import java.util.Locale;
import java.util.ArrayList;

import static org.easymock.EasyMock.*;

public class GameSetupRefactorTest {

    private static class TestGame extends Game {
        public boolean loopCalled = false;
        public int mainLoopCalls = 0;

        public TestGame(GUI gui, Random random, SetupGame gameSetup, PlayerHandler playerHandler) {
            super(gui, random, gameSetup, playerHandler);
        }

        @Override
        protected void runGameLoop() {
            loopCalled = true;
            super.runGameLoop();
        }

        @Override
        protected void mainGameLoop() {
            mainLoopCalls++;
        }
    }

    @Test(timeout = 2000)
    public void testSetupGameInitializesAndSkipsLoop() {
        GUI gui = mock(GUI.class);
        Random random = mock(Random.class);
        SetupGame setup = mock(SetupGame.class);
        PlayerHandler handler = mock(PlayerHandler.class);
        
        Player mockPlayer = mock(Player.class);
        handler.currentPlayer = mockPlayer;
        
        TestGame game = new TestGame(gui, random, setup, handler) {
            @Override
            protected void runGameLoop() {
                this.loopCalled = true;
            }
        };

        expect(gui.displayLocaleOptions()).andReturn(Locale.ENGLISH);
        gui.displayMessage(anyString());
        expectLastCall().anyTimes();
        
        expect(gui.displayDeckOptions()).andReturn("deck.txt");
        gui.createFlipButton();
        
        expect(setup.completeGameSetup()).andReturn("Heads");
        handler.completePlayerSetup("Heads", "deck.txt");
        
        gui.setPlayers(anyObject(), anyObject());
        expect(handler.getPlayerTurn()).andReturn(1);
        gui.updateTurn(1);
        
        gui.setupActivePokemon();
        expect(gui.waitForButtonPressed()).andReturn("Select");
        Pokemon mockPokemon = mock(Pokemon.class);
        expect(mockPokemon.getStage()).andReturn(0);
        expect(gui.getLastSelectedCard()).andReturn(mockPokemon);
        
        expect(handler.getCurrentPlayer()).andReturn(mockPlayer);
        mockPlayer.setActivePokemon(mockPokemon);
        gui.makeActiveCard(anyObject(), eq(mockPokemon));
        
        expect(handler.getCurrentPlayerHand()).andReturn(new ArrayList<>()).times(2);
        gui.removeAllButtons();
        expectLastCall().times(2);
        gui.displayCards(anyObject());
        expectLastCall().times(2);

        replay(gui, setup, handler, mockPlayer, mockPokemon);

        game.setupGame();
        
        verify(gui, setup, handler);
        org.junit.Assert.assertTrue("Game loop should have been triggered", game.loopCalled);
    }

    @Test(timeout = 2000)
    public void testGameLoopTerminatesWhenGuiSignalsGameOver() {
        GUI gui = mock(GUI.class);
        Random random = mock(Random.class);
        SetupGame setup = mock(SetupGame.class);
        PlayerHandler handler = mock(PlayerHandler.class);
        
        TestGame game = new TestGame(gui, random, setup, handler);
        
        // Record expectations for the loop
        // Iteration 1: gameIsOver is false -> mainGameLoop called
        // Iteration 2: mainGameLoop called -> gameIsOver becomes true -> exit
        expect(gui.gameIsOver()).andReturn(false).times(1);
        expect(gui.gameIsOver()).andReturn(true).times(1);
        
        replay(gui);
        
        game.runGameLoop();
        
        verify(gui);
        // Corrected assertion: mainGameLoop is called twice (once before false, once before true)
        org.junit.Assert.assertEquals("mainGameLoop should have been called twice", 2, game.mainLoopCalls);
        org.junit.Assert.assertTrue("Game should be marked as over", game.gameOver);
    }
}
