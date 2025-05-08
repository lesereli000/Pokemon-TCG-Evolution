package main;

import java.util.ArrayList;

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

    public void doEffects(Player activePlayer, Player opposingPlayer) {
        switch(this.effects) {
            case "Draw 2 cards.":
                activePlayer.drawCard();
                activePlayer.drawCard();
            break;

            case "Remove up to 2 damage counters from 1 of your Pokémon.":
                //TODO: Change to allow player to pick which pokemon to heal
                activePlayer.healActivePokemon(2);
            break;

            case "Discard 1 Energy card attached to your own Pokémon in order to remove up to 4 damage counters from that Pokémon.":
                if(activePlayer.activePokemon.energies.isEmpty()){
                    throw new CardCreationException("Active pokemon energies cannot be empty");
                }
                //TODO: Change to allow player to choose which pokemon to apply super potion to
                //TODO: Change to let player choose which energy to discard


                Energy e = activePlayer.activePokemon.energies.get(0);
                Pokemon activePokemon = (Pokemon) activePlayer.getActivePokemon();
                activePokemon.removeEnergy(e);
                activePlayer.healActivePokemon(4);
        }
    }
}
