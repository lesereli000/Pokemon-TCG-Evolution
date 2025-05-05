package main;

public abstract class Card {

    String name;
    public Card(String name) {
        if(name.isEmpty()){
            throw new CardCreationException("Name cannot be empty");
        } else {
            this.name = name;
        }
    }

    public String getName() {
        return name;
    }


}

