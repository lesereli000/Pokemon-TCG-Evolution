package main;

import java.util.ArrayList;
import java.util.HashMap;

public class Pokemon extends Card{
    private static final String[] allTypes = {"Grass", "Fire", "Water", "Lightning", "Psychic", "Fighting", "Darkness",
            "Metal", "Fairy", "Dragon", "Colorless"};
    String type;
    int stage;
    int hp;
    int damageCounters;
    char weakness;
    char resistance;
    ArrayList<Attack> attacks;
    ArrayList<Energy> energies = new ArrayList<Energy>();

    public Pokemon(String name, String type, int stage, int hp, char weakness, char resistance, ArrayList<Attack> attacks) {
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
    }

    public Pokemon(String name, String type, int stage, int hp) {
        this(name, type, stage, hp, 'Z', 'Z', new ArrayList<Attack>());
    }

    public String getName() {
        return super.getName();
    }

    public int getStage() {return this.stage;}

    public void takeDamage(int damageCountersTaken, char damageType) {
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

    public void addEnergy(Energy energy) {
        energies.add(energy);
    }

    public void removeEnergy(Energy energy) {
        energies.remove(energy);
    }
}