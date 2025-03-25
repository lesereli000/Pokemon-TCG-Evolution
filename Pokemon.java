public class Pokemon extends Card{

    String type;
    int stage;
    int hp;
    public Pokemon(String name, String type, int stage, int hp) {
        super(name);
        this.type = type;
        this.stage = stage;
        this.hp = hp;
    }

    public String getName() {
        return super.getName();
    }

    public void takeDamage(int damage) {
        hp -= damage;
    }

}