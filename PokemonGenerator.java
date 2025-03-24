public class PokemonGenerator {

    String name;
    String type;
    int hp;
    int stage;

    public PokemonGenerator(String name) {
        this.name = name;
        this.type = "Fire";
        this.hp = 120;
        this.stage = 2;
    }

    public Pokemon generate() {
        return new Pokemon(name, type, stage, hp);
    }

}
