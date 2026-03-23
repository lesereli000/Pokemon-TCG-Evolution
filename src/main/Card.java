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

}

