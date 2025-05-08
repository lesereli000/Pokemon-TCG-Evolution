package main;

import java.util.ArrayList;
import java.util.HashMap;

public class Attack {

    public String name;
    public ArrayList<Energy> costs;
    public int damage;
    public Attack(String name, ArrayList<Energy> costs, int damage) {
        this.name = name;
        this.costs = costs;
        this.damage = damage;
    }

    public String getCosts() {
        String outputString = "";
        for (Energy e : costs) {
            outputString += e.name + "\n";
        }
        return outputString;
    }

    public ArrayList<Energy> allCosts() {
        return costs;
    }
}
