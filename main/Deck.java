package main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import org.json.JSONArray;

public class Deck {

    ArrayList<Card> cards = new ArrayList<>();

    public int size() {
        return cards.size();
    }

    public boolean addCard(Card card) {
        if(!(card instanceof Energy) && howManyRepeats(card) > 3) {
            throw new TooManyRepeatsException("Too many repeats with card " + card.getName());
        }
        return cards.add(card);
    }

    public int howManyRepeats(Card c) {
        int repeats = 0;
        for(Card card : cards) {
            String cName = c.getName();
            String cardName = card.getName();
            if (cName.equals(cardName)) {
                repeats++;
            }
        }
        return repeats;
    }

    public ArrayList<Card> getCards() {
        return (ArrayList<Card>) cards.clone();
    }

    public boolean shuffle() {
        ArrayList<Card> shuffledCards = new ArrayList<>();
        while(!cards.isEmpty()) {
            Random rand = new Random();
            int num = rand.nextInt(cards.size());
            shuffledCards.add(cards.remove(num));
        }
        cards = shuffledCards;
        return true;
    }

    public boolean removeCard(Card card) {
        if(!cards.remove(card)) {
            throw new CardDoesNotExist("Card " + card.getName() + " does not exist");
        }
        return true;
    }

    public Card removeTopCard() {
        if (cards.isEmpty()) {
            throw new EmptyDeckException("Can not remove card from an empty deck");
        }
        return cards.remove(cards.size() - 1);
    }

    public int numberBasicPokemon() {
        int count = 0;
        for (Card card : cards) {
            if (card instanceof Pokemon && ((Pokemon) card).getStage() == 0) {
                count++;
            }
        }
        return count;
    }

    public ArrayList<Card> getOnlyPokemon() {
        ArrayList<Card> allPokemon = new ArrayList<>();
        for (Card card : cards) {
            if (card instanceof Pokemon) {
                allPokemon.add(card);
            }
        }
        return allPokemon;
    }

    public ArrayList<Card> getOnlyEnergy() {
        ArrayList<Card> allEnergy = new ArrayList<>();
        for (Card card : cards) {
            if (card instanceof Energy) {
                allEnergy.add(card);
            }
        }
        return allEnergy;
    }

    public void createDeckFromFile(String fileString) {
        File file = new File("DeckFiles/"+fileString);
        try (Scanner scanFile = new Scanner(file)) {
            String message = "";
            message = this.fileInCorrectFormat(file);
            if(!message.equals("")) {
                throw new DeckInIncorrectFormatException("File " + fileString + " is in the incorrect format: " +message);
            }
            String currPokemonLine;
                while (scanFile.hasNext()) {
                    currPokemonLine = scanFile.nextLine();
                    int count = Integer.parseInt(currPokemonLine.split(",")[0]);
                    String name = currPokemonLine.split(",")[1];
                    for (int i = 1; i <= count; i++) {
                        Card card = new CardGenerator().generateCard(name);
                        this.addCard(card);
                    }
                }
            }catch (IOException e) {
                throw new RuntimeException("File not found when adding cards from file", e);
        }

    }

    private String fileInCorrectFormat(File file){
        int total = 0;
        try (Scanner scanFile = new Scanner(file)) {
            String currPokemonLine;
                while (scanFile.hasNext()) {
                    currPokemonLine = scanFile.nextLine();
                    String numString = currPokemonLine.split(",")[0];
                    int count;
                    try{
                        count = Integer.parseInt(numString);
                    } catch (Exception e) {
                        return "Wrong format!";
                    }
                    total += count;
                    if(total>60){
                        return "Deck has too many cards";
                    }
                }
        } catch (IOException e) {
            throw new RuntimeException("File not found when adding cards from file", e);
        }
        return "";
    }

    public boolean containsCardNamed(String cardName) {
        for (Card card : cards) {
            String currentName = card.getName();
            if(currentName.equals(cardName)) return true;
        }
        return false;
    }

    public Card getCardFromName(String cardName) {
        for (Card card : cards) {
            String currentName = card.getName();
            if(currentName.equals(cardName)) return card;
        }
        throw new RuntimeException(cardName + " not found in deck!");
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
    public static class DeckInIncorrectFormatException extends RuntimeException {
        public DeckInIncorrectFormatException(String message) {super(message); }
    }
}
