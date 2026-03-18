package main;

import org.junit.Test;

import java.util.ArrayList;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;

public class AttackTest {

    @Test
    public void testGetDamage() {
        ArrayList<Energy> costs = createMock(ArrayList.class);

        Attack atk = new Attack("attack", costs, 10);
        assertEquals(10, atk.getDamage());
    }

}
