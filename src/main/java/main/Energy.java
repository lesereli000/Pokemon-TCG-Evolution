package main;

import java.util.ResourceBundle;

public class Energy extends Card {
    private final EnergyType type;

    public Energy(EnergyType type) {
        super(type.getName());
        this.type = type;
    }

    public EnergyType getEnergyType() {
        return type;
    }

    @Override
    public CardType getCardType() {
        return CardType.ENERGY;
    }

    @Override
    public String getReport(ResourceBundle messages) {
        return this.getName();
    }
}
