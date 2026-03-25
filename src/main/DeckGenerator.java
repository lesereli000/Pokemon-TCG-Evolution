package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class DeckGenerator {

    private static class DeckEntry {
        public final int count;
        public final String name;

        public DeckEntry(int count, String name) {
            this.count = count;
            this.name = name;
        }
    }

    public Deck generateFromFile(String fileString) {
        Deck newDeck = new Deck();
        File file = new File("src/main/resources/" + fileString);
        ArrayList<DeckEntry> parsedEntries = new ArrayList<>();
        int totalCards = 0;

        try (Scanner scanFile = new Scanner(file)) {
            while (scanFile.hasNext()) {
                String line = scanFile.nextLine();
                String[] parts = line.split(",");

                if (parts.length < 2) {
                    throw new DeckInIncorrectFormatException(
                            "File " + fileString + " is in the incorrect format: Wrong format!");
                }

                int count;
                try {
                    count = Integer.parseInt(parts[0]);
                } catch (NumberFormatException e) {
                    throw new DeckInIncorrectFormatException(
                            "File " + fileString + " is in the incorrect format: Wrong format!");
                }

                totalCards += count;
                if (totalCards > 60) {
                    throw new DeckInIncorrectFormatException(
                            "File " + fileString + " is in the incorrect format: Deck has too many cards");
                }

                String name = parts[1];
                parsedEntries.add(new DeckEntry(count, name));
            }
        } catch (IOException e) {
            throw new RuntimeException("File not found when adding cards from file", e);
        }

        CardGenerator generator = new CardGenerator();
        for (DeckEntry entry : parsedEntries) {
            for (int i = 0; i < entry.count; i++) {
                newDeck.addCard(generator.generateCard(entry.name));
            }
        }

        return newDeck;
    }

    public static class DeckInIncorrectFormatException extends RuntimeException {
        public DeckInIncorrectFormatException(String message) {
            super(message);
        }
    }
}
