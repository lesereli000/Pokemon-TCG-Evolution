package main;

import java.io.InputStream;
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
        InputStream is = getClass().getClassLoader().getResourceAsStream(fileString);
        if (is == null) {
            throw new MissingResourceException("File not found when adding cards from file: " + fileString);
        }
        ArrayList<DeckEntry> parsedEntries = new ArrayList<>();
        int totalCards = 0;

        try (Scanner scanFile = new Scanner(is, java.nio.charset.StandardCharsets.UTF_8)) {
            while (scanFile.hasNextLine()) {
                String line = scanFile.nextLine();
                if (line.trim().isEmpty()) continue;
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

                String name = parts[1].trim();
                parsedEntries.add(new DeckEntry(count, name));
            }
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
