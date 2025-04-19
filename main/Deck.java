package main;

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
        if(!(card instanceof Energy) && howManyRepeats(card) > 3) {
            throw new TooManyRepeatsException("Too many repeats with card " + card.getName());
        }
        cards.add(card);
    }

    public int howManyRepeats(Card c) {
        int repeats = 0;
        for(Card card : cards) {
            if (c.getName().equals(card.getName())) {
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
                try {
                    addCard(card);
                } catch (TooManyRepeatsException e) {
                    //Continue to add random cards, accounting for the i cards we have already added
                    addRandomCards(numCards - i, rand);
                }
            }
        } catch (IOException e) {
            System.out.println("File not found when adding random cards" + e);
        }
    }

    public ArrayList<Card> getCards() {
        return (ArrayList<Card>) cards.clone();
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

    public void removeCard(Card card) {
        if(!cards.remove(card)) {
            throw new CardDoesNotExist("Card " + card.getName() + " does not exist");
        }
    }

    public Card removeTopCard() {
        if (cards.isEmpty()) {
            throw new EmptyDeckException("Can not remove card from an empty deck");
        }
        return cards.remove(cards.size() - 1);
    }

    public int numberBasicPokemon() {
        int count = 0;
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i) instanceof Pokemon && ((Pokemon) cards.get(i)).getStage() == 0) {
                count++;
            }
        }
        return count;
    }

    public void replace(Card toReplace, Card replaceWith) {

    }

    public void addEnergies(int numberEnergies, Random rand) {
        try(FileReader reader = new FileReader("base1.json")) {
            String content = new String(Files.readAllBytes(Paths.get("base1.json")));
            JSONArray pokemonArray = new JSONArray(content);
            for (int i = 0; i < numberEnergies; i++) {
                //97 is the first energy card in the pokemonArray
                int num = rand.nextInt(97, pokemonArray.length());
                Card card = new CardGenerator().generateCard(pokemonArray.getJSONObject(num).getString("name"));
                try {
                    addCard(card);
                } catch (TooManyRepeatsException e) {
                    //Continue to add random cards, accounting for the i cards we have already added
                    addEnergies(numberEnergies - i, rand);
                }
            }
        } catch (IOException e) {
            System.out.println("File not found when adding random cards" + e);
        }
    }

    public static class TooManyRepeatsException extends RuntimeException {
        public TooManyRepeatsException(String message) {
            super(message);
        }
    }

    public static class EmptyDeckException extends RuntimeException {
        public EmptyDeckException(String message) {
            super(message);
        }
    }

    public static class CardDoesNotExist extends RuntimeException {
        public CardDoesNotExist(String message) {
            super(message);
        }
    }
}
