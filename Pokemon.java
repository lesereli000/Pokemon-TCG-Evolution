public class Pokemon extends Card{

    String type;
    int stage;
    int hp;
    int damageCounters;
    char weakness;
    char resistance;

    public Pokemon(String name, String type, int stage, int hp) {
        super(name);
        this.type = type;
        this.stage = stage;
        this.hp = hp;
        damageCounters = 0;
        this.weakness = 'Z';
        this.resistance = 'Z';
    }

    public Pokemon(String name, String type, int stage, int hp, char weakness, char resistance) {
        super(name);
        this.type = type;
        this.stage = stage;
        this.hp = hp;
        damageCounters = 0;
        this.weakness = weakness;
        this.resistance = resistance;
    }

    public String getName() {
        return super.getName();
    }

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

}