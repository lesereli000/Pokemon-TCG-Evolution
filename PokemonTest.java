import static org.junit.Assert.*;

import org.json.JSONArray;
import org.junit.Test;


import org.easymock.EasyMock;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;


import java.io.FileReader;
import java.util.ArrayList;

public class PokemonTest {

    @Test
    public void testSetup() {
        Pokemon p = new Pokemon("Pikachu", "Lightning", 1, 40);
        assertEquals("Pikachu", p.getName());
        assertEquals("Lightning", p.type);
        assertEquals(1, p.stage);
        assertEquals(40, p.hp);
    }

    @Test
    public void testDamage() {
        Pokemon p = new Pokemon("Pikachu", "Lightning", 1, 40);
        p.takeDamage(10);
        assertEquals(30, p.hp);
    }

    @Test
    public void testEmptyDeck() {
        Deck d = new Deck();
        assertEquals(0, d.size());
    }

    @Test
    public void testOneCard() {
        Deck d = new Deck();
        Pokemon p = new Pokemon("Pikachu", "Lightning", 1, 40);
        d.addCard(p);
        assertEquals(1, d.size());
    }

    @Test
    public void testFirstCard() {
        Deck d = new Deck();
        Pokemon p = new Pokemon("Pikachu", "Lightning", 1, 40);
        d.addCard(p);
        ArrayList<Card> pokemons = d.getCards();
        assertEquals(pokemons.get(0), p);
    }

    @Test
    public void testPokemonGen() {
        Pokemon p = new PokemonGenerator("Charizard").generate();

        assertEquals("Charizard", p.getName());
        assertEquals("Fire", p.type);
        assertEquals(120, p.hp);
        assertEquals(2, p.stage);
    }

    @Test
    public void testManyPokemon() {
        Pokemon p1 = new PokemonGenerator("Charizard").generate();
        Pokemon p2 = new PokemonGenerator("Pikachu").generate();
        Pokemon p3 = new PokemonGenerator("Squirtle").generate();
        Pokemon p4 = new PokemonGenerator("Alakazam").generate();
        Pokemon p5 = new PokemonGenerator("Arcanine").generate();
        Pokemon p6 = new PokemonGenerator("Magikarp").generate();

        assertEquals("Charizard", p1.getName());
        assertEquals("Fire", p1.type);
        assertEquals(2, p1.stage);
        assertEquals(120, p1.hp);

        assertEquals("Pikachu", p2.getName());
        assertEquals("Lightning", p2.type);
        assertEquals(0, p2.stage);
        assertEquals(40, p2.hp);

        assertEquals("Squirtle", p3.getName());
        assertEquals("Water", p3.type);
        assertEquals(0, p3.stage);
        assertEquals(40, p3.hp);

        assertEquals("Alakazam", p4.getName());
        assertEquals("Psychic", p4.type);
        assertEquals(2, p4.stage);
        assertEquals(80, p4.hp);

        assertEquals("Arcanine", p5.getName());
        assertEquals("Fire", p5.type);
        assertEquals(1, p5.stage);
        assertEquals(100, p5.hp);

        assertEquals("Magikarp", p6.getName());
        assertEquals("Water", p6.type);
        assertEquals(0, p6.stage);
        assertEquals(30, p6.hp);
    }

    public ArrayList<Pokemon> getPokemon() {
        ArrayList<Pokemon> allPokemon = new ArrayList<Pokemon>();
        try (FileReader reader = new FileReader("base1.json")) {
            String content = new String(Files.readAllBytes(Paths.get("base1.json")));
            JSONArray pokemonArray = new JSONArray(content);
            for (int i = 0; i < pokemonArray.length(); i++) {
                String PokemonName = pokemonArray.getJSONObject(i).getString("name");
                Pokemon p = new PokemonGenerator(PokemonName).generate();
                if(p != null) {
                    allPokemon.add(p);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("File not found");
            return null;
        }
        return allPokemon;
    }

    @Test
    public void testGetPokemon() {
        ArrayList<Pokemon> allPokemon = getPokemon();
        //allPokemon should be in same order of base1.json file

        assertEquals("Alakazam", allPokemon.get(0).getName());
        assertEquals("Psychic", allPokemon.get(0).type);
        assertEquals(2, allPokemon.get(0).stage);
        assertEquals(80, allPokemon.get(0).hp);

        assertEquals("Blastoise", allPokemon.get(1).getName());
        assertEquals("Water", allPokemon.get(1).type);
        assertEquals(2, allPokemon.get(1).stage);
        assertEquals(100, allPokemon.get(1).hp);

        assertEquals("Charmander", allPokemon.get(45).getName());
        assertEquals("Fire", allPokemon.get(45).type);
        assertEquals(50, allPokemon.get(45).hp);
        assertEquals(0, allPokemon.get(45).stage);

        assertEquals("Weedle", allPokemon.get(68).getName());
        assertEquals("Grass", allPokemon.get(68).type);
        assertEquals(0, allPokemon.get(68).stage);
        assertEquals(40, allPokemon.get(68).hp);
    }

        @Test
    public void testGetSize() {
        Deck d = new Deck();
        ArrayList<Pokemon> allPokemon = getPokemon();
        for (int i = 0; i < 60; i++) {
            Random rand = new Random();
            int num = rand.nextInt(allPokemon.size());
            d.addCard(allPokemon.get(num));
            allPokemon.remove(num);
        }
        assertEquals(60, d.size());
    }

    @Test
    public void testGetPokemonFromDeck() {
        Deck d = new Deck();
        ArrayList<Pokemon> allPokemon = getPokemon();
        ArrayList<Pokemon> addedPokemon = new ArrayList<Pokemon>();
        for (int i = 0; i < allPokemon.size(); i++) {
            Random rand = new Random();
            int num = rand.nextInt(allPokemon.size());
            d.addCard(allPokemon.get(num));
            addedPokemon.add(allPokemon.get(num));
            allPokemon.remove(num);
        }

        for (int i = 0; i < allPokemon.size(); i++) {
            Pokemon card = (Pokemon) d.getCards().get(i);
            assertEquals(addedPokemon.get(i).getName(), d.getCards().get(i).getName());
            assertEquals(addedPokemon.get(i).type, card.type);
            assertEquals(addedPokemon.get(i).stage, card.stage);
            assertEquals(addedPokemon.get(i).hp, card.hp);
        }
    }

    @Test
    public void testMakeRandomCards() {
        Deck d = new Deck();
        d.addRandomCards(10);
        assertEquals(10, d.size());
    }

    @Test
    public void testMakeFullDeckCards() {
        Deck d = new Deck();
        d.addRandomCards(60);
        assertEquals(60, d.size());
    }

    @Test
    public void testMakeTrainer() {
        Trainer t = new Trainer("Keiji Kinebuchi");
            assertEquals("Keiji Kinebuchi", t.getName());
    }

    @Test
    public void testAddTrainerToDeck() {
        Card t = new Trainer("Keiji Kinebuchi");
        Deck d = new Deck();
        d.addCard(t);
        assertEquals(1, d.size());
        assertEquals(t, d.getCards().get(0));
        assertEquals("Keiji Kinebuchi", ((Trainer) d.getCards().get(0)).getName());
    }

    @Test
    public void mixTrainersAndPokemon() {
        Card trainer = new Trainer("Keiji Kinebuchi");
        Deck d = new Deck();
        Pokemon p1 = new PokemonGenerator("Charizard").generate();
        Pokemon p2 = new PokemonGenerator("Pikachu").generate();
        Pokemon p3 = new PokemonGenerator("Squirtle").generate();

        d.addCard(p1);
        d.addCard(p2);
        d.addCard(trainer);
        d.addCard(p3);

        assertEquals(4, d.size());
        assertEquals(p1, d.getCards().get(0));
        assertEquals(p2, d.getCards().get(1));
        assertEquals(trainer, d.getCards().get(2));
        assertEquals(p3, d.getCards().get(3));
    }


    @Test
    public void testGUI() {
        GUI gui = new GUI();
    }

}