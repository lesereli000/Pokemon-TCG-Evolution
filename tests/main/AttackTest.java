package main;

import org.junit.Test;

import java.util.ArrayList;
import java.util.ResourceBundle;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AttackTest {

    @Test
    public void testGetDamage() {
        ArrayList<Energy> costs = createMock(ArrayList.class);

        Attack atk = new Attack("attack", costs, 10);
        assertEquals(10, atk.getDamage());
    }

    @Test
    public void testGetReport() {
        // ResourceBundle.getString() is final, so we use a ListResourceBundle instead of a mock
        ResourceBundle bundle = new java.util.ListResourceBundle() {
            @Override
            protected Object[][] getContents() {
                return new Object[][] {
                    {"costs", "Costs"},
                    {"dmg", "Damage: {0}"}
                };
            }
        };

        ArrayList<Energy> costs = new ArrayList<>();
        costs.add(new Energy(EnergyType.LIGHTNING));
        Attack atk = new Attack("Thunder", costs, 30);

        String report = atk.getReport(bundle);
        assertTrue(report.contains("Thunder"));
        assertTrue(report.contains("Costs"));
        assertTrue(report.contains("Damage: 30"));
        assertTrue(report.contains("Lightning"));
    }

}
