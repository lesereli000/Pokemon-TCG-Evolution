import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GameTest {

    //TODO: Tests moved from PokemonTest.java
    @Test
    public void testSetup() {
        Pokemon p = new Pokemon("Pikachu", "Lightning", 1, 40);
        assertEquals("Pikachu", p.getName());
        assertEquals("Lightning", p.type);
        assertEquals(1, p.stage);
        assertEquals(40, p.hp);
    }

    @Test
    public void testGUI() {
        GUI gui = new GUI();
    }
}
