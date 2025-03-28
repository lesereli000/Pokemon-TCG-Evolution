import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;

public class Deck {

    ArrayList<Card> cards = new ArrayList<>();

    public int size() {
        return cards.size();
    }

    public void addCard(Card card) {
        if(howManyRepeats(card) > 3) {
            throw new TooManyRepeatsException("Too many repeats with card " + card.getName());
        }

        cards.add(card);
    }

    public int howManyRepeats(Card c) {
        int repeats = 0;

        for(Card card : cards) {
            if (c.equals(card)) {
                repeats++;
            }
        }

        return repeats;
    }

    public void addRandomCards(int numCards, Random rand) {
        try(FileReader reader = new FileReader("base1.json")) {
            String content = new String(Files.readAllBytes(Paths.get("base1.json")));
            JSONArray pokemonArray = new JSONArray(content);
            for (int i = 0; i < numCards; i++) {
                int num = rand.nextInt(pokemonArray.length());
                Card card = new CardGenerator().generateCard(pokemonArray.getJSONObject(num).getString("name"));
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

    public Card removeTopCard() {
        return cards.remove(cards.size() - 1);
    }

    public class TooManyRepeatsException extends RuntimeException {
        public TooManyRepeatsException(String message) {
            super(message);
        }
    }
}
