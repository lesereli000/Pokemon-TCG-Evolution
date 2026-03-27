package main;

import java.util.ArrayList;
import java.util.List;

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
}
