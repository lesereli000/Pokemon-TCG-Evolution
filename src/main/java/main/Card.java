package main;

public abstract class Card {

    String name;
    public Card(String name) {
        if(name == null || name.isEmpty()){
            throw new CardCreationException("Name cannot be empty");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isNull() {
        return false;
    }

    public enum CardType {
        POKEMON, ENERGY, TRAINER, NULL_CARD
    }

    public abstract CardType getCardType();

    public boolean isBasicPokemon() {
        return false;
    }

}

