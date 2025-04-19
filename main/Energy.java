package main;

public class Energy extends Card {
    private static final String[] allTypes = {"Grass Energy", "Fire Energy", "Water Energy", "Lightning Energy",
            "Psychic Energy", "Fighting Energy", "Darkness Energy", "Metal Energy", "Fairy Energy", "Dragon Energy",
            "Colorless Energy"};

    public Energy(String name) {
        super(name);
        boolean validName = false;
        for (String type : allTypes) {
            if (type.equals(name)) {
                validName = true;
            }
        }
        if (!validName) {
            throw new CardCreationException("Not an energy name");
        }
    }
}

