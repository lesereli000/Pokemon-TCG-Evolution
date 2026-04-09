package main;

import org.junit.Test;
import java.util.ResourceBundle;
import java.util.Locale;
import static org.junit.Assert.*;

public class NullCardTest {

    @Test
    public void testNullCardProperties() {
        NullCard nc = new NullCard();
        assertTrue(nc.isNull());
        assertEquals("Unknown Card", nc.getName());
        assertEquals(Card.CardType.NULL_CARD, nc.getCardType());
        
        ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        assertEquals("Unknown Card", nc.getReport(messages));
    }
}
