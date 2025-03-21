import org.junit.Test;

import static org.junit.Assert.*;

public class PokemonTest {

    @Test
    public void testSetup() {
        Pokemon p = new Pokemon("Pikachu", "Fire", 1, 40);
        assertEquals("Pikachu", p.name);
        assertEquals("Fire", p.type);
        assertEquals(1, p.stage);
        assertEquals(40, p.hp);
    }

    @Test
    public void testTakeDamage() {
        Pokemon p = new Pokemon("Pikachu", "Fire", 1, 40);
        p.takeDamage(10);
        assertEquals(30, p.hp);
    }

}