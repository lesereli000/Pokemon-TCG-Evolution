package main;

import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

public class Attack {

    public String name;
    public List<Energy> costs;
    public int damage;

    public Attack(String name, List<Energy> costs, int damage) {
        this.name = name;
        this.costs = costs;
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    public String getReport(ResourceBundle messages) {
        StringBuilder report = new StringBuilder();
        String costsStr = messages.getString("costs");
        report.append("\n").append(this.name).append(":\n").append(costsStr).append("\n");
        for (Energy energy : this.costs) {
            report.append("• ").append(energy.getName()).append("\n");
        }
        String dmgStr = messages.getString("dmg");
        dmgStr = MessageFormat.format(dmgStr, this.getDamage());
        report.append(dmgStr).append("\n");
        return report.toString();
    }
}
