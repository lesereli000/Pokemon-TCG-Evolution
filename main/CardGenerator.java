package main;

import org.json.JSONArray;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CardGenerator {

    private String name;
    private String type;
    private String effects;
    private int hp;
    private int stage;
    private Card card;
    private int requiredEnergies;

    // When given the name of a card, should be able to create a card object with all desired information
    public Card generateCard(String name) {
        this.name = name;

        if(this.name.isEmpty()){
            throw new PokemonNotFoundException("Invalid Name");
        }

        try (FileReader reader = new FileReader("base1.json")) {
            String content = new String(Files.readAllBytes(Paths.get("base1.json")));
            JSONArray pokemonArray = new JSONArray(content);
            for (int i = 0; i < pokemonArray.length(); i++) {

                // Found JSON examples on https://www.tutorialspoint.com/json/json_java_example.htm
                // Further referenced https://www.geeksforgeeks.org/working-with-json-data-in-java/

                if (pokemonArray.getJSONObject(i).getString("name").equals(name)) {
                    String supertype = pokemonArray.getJSONObject(i).getString("supertype");
                    if (supertype.equals("Pokémon")) {
                        //Normal Pokemon
                        this.type = pokemonArray.getJSONObject(i).getJSONArray("types").getString(0);
                        this.hp = pokemonArray.getJSONObject(i).getInt("hp");
                        String wholeStage = pokemonArray.getJSONObject(i).getJSONArray("subtypes").getString(0);
                        if (wholeStage.equals("Basic")) {
                            this.stage = 0;
                        } else {
                            this.stage = Integer.parseInt(wholeStage.substring(wholeStage.length() - 1));
                        }
                        card = new Pokemon(this.name, type, stage, hp, 'Z', 'Z', 2);
                    } else if (supertype.equals("Energy")) {
                        card = new Energy(this.name);
                    } else {
                        this.effects = pokemonArray.getJSONObject(i).getJSONArray("rules").getString(0);
                        card = new Trainer(this.name, this.effects);
                    }
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("File not found in PokemonGenerator" + e);
        }

        return card;
    }

    public static class PokemonNotFoundException extends RuntimeException {
        public PokemonNotFoundException(String message) {
            super(message);
        }
    }
}
