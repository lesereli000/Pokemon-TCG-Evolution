package main;

import java.util.Arrays;

public enum EnergyType {
    GRASS("Grass Energy", "Grass"),
    FIRE("Fire Energy", "Fire"),
    WATER("Water Energy", "Water"),
    LIGHTNING("Lightning Energy", "Lightning"),
    PSYCHIC("Psychic Energy", "Psychic"),
    FIGHTING("Fighting Energy", "Fighting"),
    DARKNESS("Darkness Energy", "Darkness"),
    METAL("Metal Energy", "Metal"),
    FAIRY("Fairy Energy", "Fairy"),
    DRAGON("Dragon Energy", "Dragon"),
    COLORLESS("Colorless Energy", "Colorless");

    private final String name;
    private final String typeName;

    EnergyType(String name, String typeName) {
        this.name = name;
        this.typeName = typeName;
    }

    public String getName() {
        return name;
    }

    public String getTypeName() {
        return typeName;
    }

    public static EnergyType fromName(String name) {
        if (name == null || name.isEmpty()) {
            throw new CardCreationException("Name cannot be empty");
        }
        return Arrays.stream(values())
                .filter(type -> type.getName().equalsIgnoreCase(name) || 
                                type.getTypeName().equalsIgnoreCase(name) ||
                                (name.equalsIgnoreCase("Colorless Energy") && type == COLORLESS))
                .findFirst()
                .orElseThrow(() -> new CardCreationException("Not an energy name"));
    }
}
