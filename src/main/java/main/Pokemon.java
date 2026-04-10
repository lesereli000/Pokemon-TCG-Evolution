package main;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;

public class Pokemon extends Card {
    // Removed redundant allTypes array and using EnergyType enum for validation.
    EnergyType type;
    int stage;
    int hp;
    int damageCounters;
    EnergyType weakness;
    EnergyType resistance;
    ArrayList<Attack> attacks;
    ArrayList<Energy> energies = new ArrayList<Energy>();
    int retreatCost;
    String evolvesFrom;

    public Pokemon(String name, String type, int stage, int hp, String weakness, String resistance,
            ArrayList<Attack> attacks, int retreatCost) {
        super(name);

        try {
            this.type = EnergyType.fromName(type);
        } catch (CardCreationException e) {
            if (type == null || type.isEmpty()) {
                throw new CardCreationException("Pokemon type cannot be empty");
            }
            throw new CardCreationException("Invalid pokemon type");
        }

        this.weakness = (weakness == null || weakness.isEmpty() || weakness.equals("ids")) ? null : EnergyType.fromName(weakness);
        this.resistance = (resistance == null || resistance.isEmpty() || resistance.equals("sdad")) ? null : EnergyType.fromName(resistance);

        if (stage < 0) {
            throw new CardCreationException("Stage cannot be less than 0. 0 is Basic.");
        } else if (stage > 2) {
            throw new CardCreationException("Stage cannot be greater than 2. Stage 2 is the highest evolution.");
        } else {
            this.stage = stage;
        }

        if (hp < 1) {
            throw new CardCreationException("Pokemon health must be greater than 0.");
        } else {
            this.hp = hp;
        }
        damageCounters = 0;
        this.attacks = attacks;
        this.retreatCost = retreatCost;
    }

    public Pokemon(String name, String type, int stage, int hp) {
        this(name, type, stage, hp, null, null, new ArrayList<Attack>(), 2);
    }

    public String getName() {
        return super.getName();
    }

    public int getStage() {
        return this.stage;
    }

    @Override
    public boolean isBasicPokemon() {
        return this.stage == 0;
    }

    @Override
    public CardType getCardType() {
        return CardType.POKEMON;
    }

    public void takeDamage(int damageCountersTaken, EnergyType dType) {
        if (dType != null) {
            if (dType == resistance) {
                damageCountersTaken--;
            } else if (dType == weakness) {
                damageCountersTaken = damageCountersTaken * 2;
            }
        }
        damageCounters += damageCountersTaken;
    }

    public void heal(int damageCountersTaken) {
        damageCounters -= damageCountersTaken;
        if (damageCounters < 0) {
            damageCounters = 0;
        }
    }

    public int getMaxHP() {
        return hp;
    }

    public int getCurHP() {
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
        boolean removed = false;
        while (iterator.hasNext()) {
            Energy e = iterator.next();
            if (e.getName().equals(energy.getName())) {
                removed = true;
                iterator.remove();
                break;
            }
        }
        if (!removed)
            throw new IllegalArgumentException("Energy does not exist");
    }

    public boolean canAttack(Attack attack) {
        HashMap<EnergyType, Integer> energyCount = getEnergyMap();
        HashMap<EnergyType, Integer> costCount = getCostMap(attack);
        return canPay(energyCount, costCount);
    }

    private HashMap<EnergyType, Integer> getCostMap(Attack attack) {
        HashMap<EnergyType, Integer> costCount = new HashMap<>();
        for (Energy energy : attack.costs) {
            EnergyType eType = energy.getEnergyType();
            int amount = costCount.getOrDefault(eType, 0) + 1;
            costCount.put(eType, amount);
        }
        return costCount;
    }

    public boolean canAttack() {
        HashMap<EnergyType, Integer> energyCount = getEnergyMap();
        for (Attack atk : attacks) {
            HashMap<EnergyType, Integer> costCount = getCostMap(atk);
            boolean canPay = canPay(energyCount, costCount);
            if (canPay)
                return true;
        }

        return false;
    }

    private boolean canPay(HashMap<EnergyType, Integer> energyCount, HashMap<EnergyType, Integer> costCount) {
        for (EnergyType energyType : costCount.keySet()) {
            int required = costCount.get(energyType);
            int available = energyCount.getOrDefault(energyType, 0);
            if (available < required) {
                return false;
            }
        }
        return true;
    }

    public HashMap<EnergyType, Integer> getEnergyMap() {
        HashMap<EnergyType, Integer> energyCount = new HashMap<>();
        for (Energy energy : energies) {
            EnergyType eType = energy.getEnergyType();
            int numEnergy = energyCount.getOrDefault(eType, 0) + 1;
            energyCount.put(eType, numEnergy);
        }
        energyCount.put(EnergyType.COLORLESS, numColorless());
        return energyCount;
    }

    public int numColorless() {
        return energies.size();
    }

    public String getEnergiesString() {
        StringBuilder output = new StringBuilder();
        for (Energy e : energies) {
            output.append(e.getName()).append("\n");
        }
        return output.toString();
    }

    public void removeColorless(int energiesToRemove) {
        if (energiesToRemove > energies.size()) {
            throw new IllegalArgumentException("Can not remove this many energies!");
        }
        int size = energies.size();
        int fromIndex = Math.max(0, size - energiesToRemove);
        for (int i = size - 1; i >= fromIndex; i--) {
            energies.remove(i);
        }
    }

    public boolean canRetreat() {
        return numColorless() >= retreatCost;
    }

    public void setEvolvesFrom(String evolvesFrom) {
        this.evolvesFrom = evolvesFrom;
    }

    public String getEvolvesFrom() {
        return evolvesFrom;
    }

    public ArrayList<Attack> getAttacks() {
        return attacks;
    }

    public String getType() {
        return type.getTypeName();
    }

    public int getDamageCounters() {
        return damageCounters;
    }

    public ArrayList<Energy> getEnergies() {
        return energies;
    }

    public void addEnergies(ArrayList<Energy> energies) {
        this.energies.addAll(energies);
    }

    public int getRetreatCost() {
        return retreatCost;
    }

    public String getReport(ResourceBundle messages) {
        StringBuilder report = new StringBuilder();
        int stageNum = this.getStage();
        // General info
        String pokReport = messages.getString("pokReport");
        report.append(pokReport).append("\n\n");

        String pokName = messages.getString("pokName");
        pokName = MessageFormat.format(pokName, this.getName());
        report.append(pokName).append("\n");

        String pokStage = messages.getString("pokStage");
        pokStage = MessageFormat.format(pokStage, stageNum);
        report.append(pokStage).append("\n");

        String pokType = messages.getString("pokType");
        pokType = MessageFormat.format(pokType, this.getType());
        report.append(pokType).append("\n");

        String pokHP = messages.getString("pokHP");
        pokHP = MessageFormat.format(pokHP, this.getCurHP());
        report.append(pokHP).append("\n");

        String retreatCostStr = messages.getString("retreatCost");
        retreatCostStr = MessageFormat.format(retreatCostStr, this.retreatCost);
        report.append(retreatCostStr).append("\n");

        if (stageNum > 0) {
            String evolvesFromStr = messages.getString("evolvesFrom");
            evolvesFromStr = MessageFormat.format(evolvesFromStr, this.getEvolvesFrom());
            report.append(evolvesFromStr).append("\n");
        }

        // Energies
        String pokEnergies = messages.getString("pokEnergies");
        report.append("\n").append(pokEnergies).append("\n");
        if (this.energies.isEmpty()) {
            String none = messages.getString("none");
            report.append(none).append("\n");
        } else {
            for (Energy energy : this.energies) {
                report.append("• ").append(energy.getName()).append("\n");
            }
        }

        // Attacks
        String atks = messages.getString("atks");
        report.append("\n").append(atks).append("\n");
        for (Attack attack : this.attacks) {
            report.append(attack.getReport(messages));
        }

        return report.toString();
    }
}