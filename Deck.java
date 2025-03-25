import org.json.JSONArray;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class Deck {

    ArrayList<Card> cards = new ArrayList<>();

    public int size() {
        return cards.size();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void addRandomCards(int numCards) {
        try(FileReader reader = new FileReader("base1.json")) {
            String content = new String(Files.readAllBytes(Paths.get("base1.json")));
            JSONArray pokemonArray = new JSONArray(content);
            for (int i = 0; i < numCards; i++) {
                Random rand = new Random();
                int num = rand.nextInt(pokemonArray.length());
                Card card = new PokemonGenerator(pokemonArray.getJSONObject(num).getString("name")).generate();
                cards.add(card);
            }
        } catch (IOException e) {
            System.out.println("File not found when adding random cards" + e);
        }
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void shuffle() {
        ArrayList<Card> shuffledCards = new ArrayList<>();
        while(!cards.isEmpty()) {
            Random rand = new Random();
            int num = rand.nextInt(cards.size());
            shuffledCards.add(cards.remove(num));
        }
        cards = shuffledCards;
    }
}
