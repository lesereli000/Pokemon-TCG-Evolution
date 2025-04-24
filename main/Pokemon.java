package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Collectors;

public class Pokemon extends Card{
    private static final String[] allTypes = {"Grass", "Fire", "Water", "Lightning", "Psychic", "Fighting", "Darkness",
            "Metal", "Fairy", "Dragon", "Colorless"};
    String type;
    int stage;
    int hp;
    int damageCounters;
    String weakness;
    String resistance;
    ArrayList<Attack> attacks;
    ArrayList<Energy> energies = new ArrayList<Energy>();
    int retreatCost;

    public Pokemon(String name, String type, int stage, int hp, String weakness, String resistance, ArrayList<Attack> attacks, int retreatCost) {
        super(name);

        if(type.isEmpty()){
            throw new CardCreationException("Pokemon type cannot be empty");
        } else {
            boolean validType = false;
            for (String poketype : allTypes) {
                if (poketype.equals(type)) {
                    validType = true;
                }
            }
            if (!validType) {
                throw new CardCreationException("Invalid pokemon type");
            } else {
                this.type = type;
            }
        }

        if(stage < 0){
            throw new CardCreationException("Stage cannot be less than 0. 0 is Basic.");
        } else if(stage > 2){
            throw new CardCreationException("Stage cannot be greater than 2. Stage 2 is the highest evolution.");
        } else{
            this.stage = stage;
        }

        if(hp < 1){
            throw new CardCreationException("Pokemon health must be greater than 0.");
        } else {
            this.hp = hp;
        }
        damageCounters = 0;
        this.weakness = weakness;
        this.resistance = resistance;
        this.attacks = attacks;
        this.retreatCost = retreatCost;
    }

    public Pokemon(String name, String type, int stage, int hp) {
        this(name, type, stage, hp, "ids", "sdad", new ArrayList<Attack>(), 2);
    }

    public String getName() {
        return super.getName();
    }

    public int getStage() {return this.stage;}

    public void takeDamage(int damageCountersTaken, String damageType) {
        if(damageType == resistance) {
            damageCountersTaken--;
        } else if (damageType == weakness) {
            damageCountersTaken = damageCountersTaken * 2;
        }
        damageCounters += damageCountersTaken;
    }

    public int getMaxHP(){
        return hp;
    }

    public int getDamageCounters(){
        return damageCounters;
    }

    public int getCurHP(){
        return hp - (10 * damageCounters);
    }

    public boolean isAlive() {
        return getCurHP() > 0;
    }

    public void addEnergy(Energy energy) {
        energies.add(energy);
    }

    public void removeEnergy(Energy energy) {
        Iterator<Energy> iterator = energies.iterator();
        while (iterator.hasNext()) {
            Energy e = iterator.next();
            String actualName = e.name;
            String energyName = energy.name;
            if (actualName.equals(energyName)) {
                iterator.remove();
                break;
            }
        }
    }

    public boolean canAttack(Attack attack) {
        HashMap<String, Integer> energyCount = getEnergyMap();
        HashMap<String, Integer> costCount = getCostMap(attack);
        return canPay(energyCount, costCount);
    }

    private HashMap<String, Integer> getCostMap(Attack attack) {
        HashMap<String, Integer> costCount = new HashMap<>();
        for (Energy energy : attack.costs) {
            String name = energy.name;
            int amount = costCount.getOrDefault(name, 0) + 1;
            costCount.put(name, amount);
        }
        return costCount;
    }

    public boolean canAttack() {
        HashMap<String, Integer> energyCount = getEnergyMap();
        for (Attack atk : attacks) {
            HashMap<String, Integer> costCount = getCostMap(atk);
            boolean canPay = canPay(energyCount, costCount);
            if(canPay) return true;
        }

        return false;
    }

    private boolean canPay(HashMap<String, Integer> energyCount, HashMap<String, Integer> costCount) {
        for (String energyType : costCount.keySet()) {
            int required = costCount.get(energyType);
            int available = energyCount.getOrDefault(energyType, 0);
            if (available < required) {
                return false;
            }
        }
        return true;
    }

    public HashMap<String, Integer> getEnergyMap() {
        HashMap<String, Integer> energyCount = new HashMap<>();
        for (Energy energy : energies) {
            String name = energy.getName();
            int numEnergy = energyCount.getOrDefault(name, 0) + 1;
            energyCount.put(name, numEnergy);
        }
        energyCount.put("Colorless Energy", numColorless());
        return energyCount;
    }

    public int numColorless() {
        return energies.size();
    }
}