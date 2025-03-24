import org.json.JSONArray;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class Deck {

    ArrayList<Card> cards = new ArrayList<>();

    int size = 0;
    public int size() {
        return size;
    }

    public void addCard(Card card) {
        cards.add(card);
        size++;
    }

    public void addRandomCards(int numCards) {
        try(FileReader reader = new FileReader("base1.json")) {
            String content = new String(Files.readAllBytes(Paths.get("base1.json")));
            JSONArray pokemonArray = new JSONArray(content);
            for (int i = 0; i < numCards; i++) {
                Random rand = new Random();
                int num = rand.nextInt(pokemonArray.length());
                Pokemon p = new PokemonGenerator(pokemonArray.getJSONObject(num).getString("name")).generate();
                cards.add(p);
                size++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public ArrayList<Card> getCards() {
        return cards;
    }
}
