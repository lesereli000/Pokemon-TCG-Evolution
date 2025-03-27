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
}
