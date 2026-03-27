package main;

import org.junit.Test;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EnergyTest {

    @Test
    public void testEmptyName() {
        boolean pass = false;

        try{
            EnergyType.fromName("");
        } catch (CardCreationException e) {
            pass = true;
            assertEquals("Name cannot be empty", e.getMessage());
        }finally {
            assertTrue(pass);
        }
    }

    @Test
    public void testGetName() {
        String name = "Grass Energy";
        Card c = new Energy(EnergyType.fromName(name));

        assertEquals(name, c.getName());
    }

    @Test
    public void testInvalidName() {
        boolean pass = false;
        String name = "Charizard";

        try{
            EnergyType.fromName(name);
        } catch (CardCreationException e) {
            pass = true;
            assertEquals("Not an energy name", e.getMessage());
        }
        assertTrue(pass);
    }

    @Test
    public void testEachEnergyType() {
        String[] allTypes = {"Grass Energy", "Fire Energy", "Water Energy", "Lightning Energy", "Psychic Energy",
                "Fighting Energy", "Darkness Energy", "Metal Energy", "Fairy Energy", "Dragon Energy", "Colorless Energy"};
        Card[] cards = new Card[allTypes.length];

        for (int i = 0; i < allTypes.length; i++) {
            cards[i] = new Energy(EnergyType.fromName(allTypes[i]));
        }

        checkEachEnergyCard(cards);
    }

    private void checkEachEnergyCard(Card[] cards) {
        assertEquals("Grass Energy", cards[0].getName());
        assertEquals("Fire Energy", cards[1].getName());
        assertEquals("Water Energy", cards[2].getName());
        assertEquals("Lightning Energy", cards[3].getName());
        assertEquals("Psychic Energy", cards[4].getName());
        assertEquals("Fighting Energy", cards[5].getName());
        assertEquals("Darkness Energy", cards[6].getName());
        assertEquals("Metal Energy", cards[7].getName());
        assertEquals("Fairy Energy", cards[8].getName());
        assertEquals("Dragon Energy", cards[9].getName());
        assertEquals("Colorless Energy", cards[10].getName());
    }

}
