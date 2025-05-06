package main;

import java.util.Random;

public class SetupGame {
    protected Random random;

    public SetupGame(Random random) {
        this.random = random;
    }

    public String completeGameSetup() {
        return flipCoin();
    }

    protected String flipCoin() {
        boolean randomBoolean = random.nextBoolean();
        if(randomBoolean) {
            return "Heads";
        }
        return "Tails";
    }
}
