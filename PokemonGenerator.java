import java.io.FileNotFoundException;
import org.json.*;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PokemonGenerator {

    String name;
    String type;
    int hp;
    int stage;

    // When given the name of a pokemon, should be able to create a Pokemon object with all desired information

    public PokemonGenerator(String name) {
        this.name = name;
        try (FileReader reader = new FileReader("base1.json")) {
            String content = new String(Files.readAllBytes(Paths.get("base1.json")));
//            System.out.println(content);
            JSONArray pokemonArray = new JSONArray(content);

            for (int i = 0; i < pokemonArray.length(); i++) {

                // Found JSON examples on https://www.tutorialspoint.com/json/json_java_example.htm

                // Further referenced https://www.geeksforgeeks.org/working-with-json-data-in-java/

                if(pokemonArray.getJSONObject(i).getString("name").equals(name)) {
                      this.type = pokemonArray.getJSONObject(i).getJSONArray("types").getString(0);
                      this.hp = pokemonArray.getJSONObject(i).getInt("hp");
                      String wholeStage = pokemonArray.getJSONObject(i).getJSONArray("subtypes").getString(0);
                      if (wholeStage.equals("Basic")) {
                          this.stage = 0;
                      } else {
                          this.stage = Integer.parseInt(wholeStage.substring(wholeStage.length()-1));
                      }
                      break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("File not found");
            return;
        }
    }

    public Pokemon generate() {
        return new Pokemon(name, type,stage, hp);
    }

}
