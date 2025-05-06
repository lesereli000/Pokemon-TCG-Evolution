package main;

import org.junit.Test;

import java.util.Random;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class GameSetupTest {

    @Test
    public void testFlipCoinHeads() {
        Random rand = createMock(Random.class);
        expect(rand.nextBoolean()).andReturn(true);
        replay(rand);

        SetupGame setup = new SetupGame(rand);
        String flipResult = setup.completeGameSetup();
        assertEquals("Heads", flipResult);

        verify(rand);
    }
    @Test
    public void testFlipCoinTails() {
        Random rand = createMock(Random.class);
        expect(rand.nextBoolean()).andReturn(false);
        replay(rand);

        SetupGame setup = new SetupGame(rand);
        String flipResult = setup.completeGameSetup();
        assertEquals("Tails", flipResult);

        verify(rand);
    }

}
