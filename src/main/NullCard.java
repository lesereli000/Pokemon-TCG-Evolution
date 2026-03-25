package main;

public class NullCard extends Card {
    
    public NullCard() {
        super("Unknown Card");
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public String getName() {
        return "Unknown Card";
    }

    @Override
    public CardType getCardType() {
        return CardType.NULL_CARD;
    }
}
