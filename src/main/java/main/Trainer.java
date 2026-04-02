package main;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class Trainer extends Card{
    private String effects;
    private String subtype;

    public Trainer(String name, String effects) {
        super(name);
        this.subtype = "Trainer";
        if(effects.isEmpty()){
            throw new CardCreationException("Trainer effects cannot be empty");
        } else {
            this.effects = effects;
        }
    }

    public Trainer(String name, String subtype, String effects) {
        super(name);
        if(subtype.equals("Item") || subtype.equals("Supporter") || subtype.equals("Stadium")){
            this.subtype = subtype;
        } else {
            throw new CardCreationException("Trainer subtype must be either Item, Supporter or Stadium");
        }
        if(effects.isEmpty()){
            throw new CardCreationException("Trainer effects cannot be empty");
        } else {
            this.effects = effects;
        }
    }

    public String getTrainerType(){
        return this.subtype;
    }

    public String getEffects(){
        return this.effects;
    }

    public void doEffects(Player activePlayer, Pokemon selectedPokemon, Energy selectedEnergy) {
        switch(this.effects) {
            case "Draw 2 cards.":
                activePlayer.drawCard();
                activePlayer.drawCard();
            break;

            case "Remove up to 2 damage counters from 1 of your Pokemon.":
                selectedPokemon.heal(2);
            break;

            case "Discard 1 Energy card attached to your own Pokemon in order to remove up to 4 damage counters from that Pokemon.":
                activePlayer.removeFromHand(selectedEnergy);
                selectedPokemon.heal(4);
            break;

            case "Switch 1 of your own Benched Pokemon with your Active Pokemon.":
                activePlayer.addBenchPokemon(activePlayer.getActivePokemon());
                activePlayer.setNewActivePokemon(selectedPokemon);
            break;
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
        switch (this.getName()) {
            case "Potion":
                String potStr = messages.getString("potionEffect");
                report.append(potStr);
                break;
            case "Super Potion":
                String superPotStr = messages.getString("superPotionEffect");
                report.append(superPotStr);
                break;
            case "Bill":
                String billStr = messages.getString("billEffect");
                report.append(billStr);
                break;
        }
        return report.toString();
    }
}
