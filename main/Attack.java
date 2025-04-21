package main;

import java.util.HashMap;

public class Attack {

    public String name;
    public HashMap<String, Integer> costs;
    public int damage;
    public Attack(String name, HashMap<String, Integer> costs, int damage) {
        this.name = name;
        this.costs = costs;
        this.damage = damage;
    }



}
