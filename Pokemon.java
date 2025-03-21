public class Pokemon {

    String name;
    String type;
    int stage;
    int hp;
    public Pokemon(String name, String type, int stage, int hp) {
        this.name = name;
        this.type = type;
        this.stage = stage;
        this.hp = hp;
    }

    public void takeDamage(int damage) {
        hp -= damage;
    }


}