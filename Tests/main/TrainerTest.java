package main;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TrainerTest {

    @Test
    public void testEmptyName() {
        boolean pass = false;
        String effects = "Test";

        try {
            new Trainer("", effects);
        } catch (CardCreationException e) {
            pass = true;
            assertEquals("Name cannot be empty", e.getMessage());
        } finally {
            assertTrue(pass);
        }
    }

    @Test
    public void testGetName() {
        String name = "Potion";
        String effects = "Test";
        Card c = new Trainer(name, effects);

        assertEquals(name, c.getName());
    }

    @Test
    public void testInvalidSubtype() {
        String name = "Potion";
        String effects = "Test";
        String subtype = "Invalid";
        boolean pass = false;

        try {
            new Trainer(name, subtype, effects);
        } catch (CardCreationException e) {
            pass = true;
            assertEquals("Trainer subtype must be either Item, Supporter or Stadium", e.getMessage());
        } finally {
            assertTrue(pass);
        }
    }

    @Test
    public void testAllValidSubtypes() {
        String name = "Potion";
        String effects = "Test";
        String subtype = "Item";
        Trainer c1 = new Trainer(name, subtype, effects);

        name = "Potion";
        effects = "Test";
        subtype = "Supporter";
        Trainer c2 = new Trainer(name, subtype, effects);

        name = "Potion";
        effects = "Test";
        subtype = "Stadium";
        Trainer c3 = new Trainer(name, subtype, effects);

        assertEquals("Item", c1.getTrainerType());
        assertEquals("Supporter", c2.getTrainerType());
        assertEquals("Stadium", c3.getTrainerType());

    }

    @Test
    public void testEmptyEffects() {
        String name = "Potion";
        String effects = "";
        boolean pass = false;

        try {
            new Trainer(name, effects);
        } catch (CardCreationException e) {
            pass = true;
            assertEquals("Trainer effects cannot be empty", e.getMessage());
        } finally {
            assertTrue(pass);
        }
    }

    @Test
    public void testGetEffects() {
        String name = "Potion";
        String effects = "Test";
        Trainer c = new Trainer(name, effects);

        assertEquals(effects, c.getEffects());
    }




}
