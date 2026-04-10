import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.HashSet;

public class CheckBase {
    public static void main(String[] args) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get("src/main/resources/base1.json")));
        Pattern p = Pattern.compile("\"name\":\\s*\"(.*?)\"");
        Matcher m = p.matcher(content);
        Set<String> names = new HashSet<>();
        while(m.find()) {
            names.add(m.group(1));
        }

        String[] testNames = {
"Squirtle", "Wartortle", "Blastoise", "Poliwag", "Poliwhirl", "Poliwrath",
"Charmander", "Charmeleon", "Charizard", "Vulpix", "Ninetales", "Arcanine",
"Water Energy", "Fighting Energy", "Fire Energy", "Grass Energy",
"Bill", "Potion", "Switch", "Energy Retrieval", "Pokedex"
        };
        for(String t : testNames) {
            if(!names.contains(t)) {
                System.out.println("MISSING: " + t);
            }
        }
    }
}
