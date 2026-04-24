package main;

import java.text.MessageFormat;
import java.util.Map;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Trainer extends Card {
    private String effects;
    private TrainerSubtype subtype;
    private TrainerEffect effectStrategy;

    private static final Map<String, TrainerEffect> effectRegistry = new HashMap<>();

    static {
        effectRegistry.put("Draw 2 cards.", (activePlayer, selectedPokemon, selectedEnergy) -> {
            activePlayer.drawCard();
            activePlayer.drawCard();
        });

        effectRegistry.put("Remove up to 2 damage counters from 1 of your Pokemon.",
                (activePlayer, selectedPokemon, selectedEnergy) -> {
                    selectedPokemon.heal(2);
                });

        effectRegistry.put(
                "Discard 1 Energy card attached to your own Pokemon in order to remove up to 4 damage counters from that Pokemon.",
                (activePlayer, selectedPokemon, selectedEnergy) -> {
                    activePlayer.removeFromHand(selectedEnergy);
                    selectedPokemon.heal(4);
                });

        effectRegistry.put("Switch 1 of your own Benched Pokemon with your Active Pokemon.",
                (activePlayer, selectedPokemon, selectedEnergy) -> {
                    activePlayer.addBenchPokemon(activePlayer.getActivePokemon());
                    activePlayer.setNewActivePokemon(selectedPokemon);
                });
    }

    public static void registerEffect(String name, TrainerEffect effect) {
        effectRegistry.put(name, effect);
    }

    public Trainer(String name, String effects) {
        super(name);
        this.subtype = TrainerSubtype.TRAINER;
        if (effects.isEmpty()) {
            throw new CardCreationException("Trainer effects cannot be empty");
        } else {
            this.effects = effects;
            this.effectStrategy = effectRegistry.get(effects);
        }
    }

    public Trainer(String name, TrainerSubtype subtype, String effects) {
        super(name);
        if (subtype == TrainerSubtype.ITEM || subtype == TrainerSubtype.SUPPORTER
                || subtype == TrainerSubtype.STADIUM) {
            this.subtype = subtype;
        } else {
            throw new CardCreationException("Trainer subtype must be either Item, Supporter or Stadium");
        }
        if (effects.isEmpty()) {
            throw new CardCreationException("Trainer effects cannot be empty");
        } else {
            this.effects = effects;
            this.effectStrategy = effectRegistry.get(effects);
        }
    }

    public TrainerSubtype getTrainerType() {
        return this.subtype;
    }

    public String getEffects() {
        return this.effects;
    }

    public void doEffects(Player activePlayer, Pokemon selectedPokemon, Energy selectedEnergy) {
        if (this.effectStrategy != null) {
            this.effectStrategy.execute(activePlayer, selectedPokemon, selectedEnergy);
        }
    }

    @Override
    public CardType getCardType() {
        return CardType.TRAINER;
    }

    public String getReport(ResourceBundle messages) {
        StringBuilder report = new StringBuilder();
        String nameStr = messages.getString("trainerName");
        nameStr = MessageFormat.format(nameStr, this.getName());
        report.append(nameStr).append("\n");
        String effectStr = messages.getString("trainerEffect");
        report.append(effectStr).append("\n");

        String effectKey = this.getName().substring(0, 1).toLowerCase()
                + this.getName().substring(1).replace(" ", "")
                + "Effect";
        if (messages.containsKey(effectKey)) {
            report.append(messages.getString(effectKey));
        }

        return report.toString();
    }
}
