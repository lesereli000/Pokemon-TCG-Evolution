package main;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CardGenerator {

    protected String resourcePath = "base1.json";
    
    private static Map<String, JSONArray> cachedDatabases = new HashMap<>();

    public Card generateCard(String name) {
        if(name == null || name.isEmpty()){
            throw new PokemonNotFoundException("Invalid Name");
        }

        try {
            JSONArray database = loadDatabase();
            JSONObject cardData = findCardData(database, name);
            if (cardData == null) {
                return null;
            }

            String supertype = cardData.getString("supertype");

            Card card;
            if (supertype.equals("Pokemon")) {
                card = createPokemon(cardData);
            } else if (supertype.equals("Energy")) {
                card = new Energy(EnergyType.fromName(name));
            } else {
                card = createTrainer(cardData);
            }
            setImageUrl(card, cardData);
            return card;
        } catch (IOException e) {
            throw new RuntimeException("File not found in directory!", e);
        }
    }

    private void setImageUrl(Card card, JSONObject cardData) {
        if (cardData.has("images")) {
            JSONObject images = cardData.getJSONObject("images");
            if (images.has("small")) {
                card.setImageUrl(images.getString("small"));
            }
        }
    }

    private JSONArray loadDatabase() throws IOException {
        if (!cachedDatabases.containsKey(resourcePath)) {
            InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (is == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            try (Scanner s = new Scanner(is, StandardCharsets.UTF_8).useDelimiter("\\A")) {
                String content = s.hasNext() ? s.next() : "";
                cachedDatabases.put(resourcePath, new JSONArray(content));
            }
        }
        return cachedDatabases.get(resourcePath);
    }

    private JSONObject findCardData(JSONArray database, String targetName) {
        for (int i = 0; i < database.length(); i++) {
            JSONObject cardJson = database.getJSONObject(i);
            if (cardJson.getString("name").equals(targetName)) {
                return cardJson;
            }
        }
        return null;
    }

    private Pokemon createPokemon(JSONObject pokemonJson) {
        String name = pokemonJson.getString("name");
        String type = pokemonJson.getJSONArray("types").getString(0);
        String weakness = "";
        String resistance = "";

        switch (type) {
            case "Grass" -> {
                weakness = "Fire";
                resistance = "Water";
            }
            case "Fire" -> {
                weakness = "Water";
                resistance = "";
            }
            case "Water" -> {
                weakness = "Lightning";
                resistance = "";
            }
            case "Lightning" -> {
                weakness = "Fighting";
                resistance = "Metal";
            }
            case "Fighting" -> {
                weakness = "Psychic";
                resistance = "";
            }
            case "Psychic" -> {
                weakness = "Darkness";
                resistance = "Fighting";
            }
        }

        int hp = pokemonJson.getInt("hp");
        int retreatCost;
        try {
            retreatCost = pokemonJson.getInt("convertedRetreatCost");
        } catch (JSONException e) {
            retreatCost = 1;
        }

        int stage;
        String wholeStage = pokemonJson.getJSONArray("subtypes").getString(0);
        if (wholeStage.equals("Basic")) {
            stage = 0;
        } else {
            stage = Integer.parseInt(wholeStage.substring(wholeStage.length() - 1));
        }

        ArrayList<Attack> attacks = parseAttacks(pokemonJson.getJSONArray("attacks"));

        Pokemon pokemon = new Pokemon(name, type, stage, hp, weakness, resistance, attacks, retreatCost);
        if(stage != 0) {
            String evolvesFromPokemon = pokemonJson.getString("evolvesFrom");
            pokemon.setEvolvesFrom(evolvesFromPokemon);
        }
        return pokemon;
    }

    private ArrayList<Attack> parseAttacks(JSONArray jsonAttacks) {
        ArrayList<Attack> parsedAttacks = new ArrayList<>();
        for(int j = 0; j < jsonAttacks.length(); j++) {
            JSONObject attackJson = jsonAttacks.getJSONObject(j);
            String attackName = attackJson.getString("name");
            int damage = Integer.parseInt(attackJson.getString("damage"));
            
            JSONArray jsonCosts = attackJson.getJSONArray("cost");
            List<Energy> attackCosts = new ArrayList<>();
            for(int k = 0; k < jsonCosts.length(); k++) {
                String currentEnergyString = jsonCosts.getString(k);
                attackCosts.add(new Energy(EnergyType.fromName(currentEnergyString + " Energy")));
            }

            parsedAttacks.add(new Attack(attackName, attackCosts, damage));
        }
        return parsedAttacks;
    }

    private Trainer createTrainer(JSONObject trainerJson) {
        String name = trainerJson.getString("name");
        String effects = trainerJson.getJSONArray("rules").getString(0);
        return new Trainer(name, effects);
    }

    public static class PokemonNotFoundException extends RuntimeException {
        public PokemonNotFoundException(String message) {
            super(message);
        }
    }
}