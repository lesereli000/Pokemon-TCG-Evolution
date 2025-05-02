package main;

import java.util.ArrayList;
import java.util.Random;

public class Player {
    private static final int PRIZE_CARD_SIZE = 6;
    private static final int HAND_SIZE = 7;
    protected Deck deck;
    protected Deck hand;
    protected Deck bench;
    private Deck discard;
    protected Pokemon activePokemon;
    protected Deck prizeCards;
    private String name;
    private int numPokemonDied;
    private boolean canAddEnergy;


    public Player() {
        this("Unnamed Player");
    }

    public Player(String name) {
        this.deck = new Deck();
        this.hand = new Deck();
        this.bench = new Deck();
        this.activePokemon = null;
        this.name = name;
        this.prizeCards = new Deck();
        this.numPokemonDied = 0;
        this.canAddEnergy = true;
    }

    public void createFullRandomDeck(Random rand) {
        this.deck.addEnergies(15, rand);
        this.deck.addRandomCards(45, rand);
        this.deck.shuffle();
        checkForBasics(rand);
    }

    public void createCustomDeck() {
        this.deck.createDeckFromFile("Overgrowth.txt");
        this.deck.shuffle();
    }

    public void drawCard(){
        Card drawnCard = deck.removeTopCard();
        hand.addCard(drawnCard);
    }

    public void checkForBasics(Random rand) {
        while(this.deck.numberBasicPokemon() == 0) {
            System.out.println(this.name + " does not have any basic cards!");
            this.deck = new Deck();
            createFullRandomDeck(rand);
        }
    }

    public String handAsString() {
        String msg = this.name + " has cards:\n";
        for (Card newCard : hand.getCards()) {
            String card1Class = newCard.getClass().toString();
            String justClass1 = card1Class.substring(11); //Just the class of the card
            msg += newCard.getName() + " which is a " + justClass1 + "\n";
        }
        return msg;
    }

    public ArrayList<Card> handAsList() {
        return hand.getCards();
    }

    public void setActivePokemon(Pokemon activePokemon) {
        this.activePokemon = activePokemon;
        this.hand.removeCard(activePokemon);
    }

    public String getName() {
        return name;
    }

    public int getNumPokemonDied() { return numPokemonDied; }

    public void pokemonDied() {
        this.numPokemonDied = this.numPokemonDied + 1;
    }

    public void drawStartingHand() {
        for (int i = 0; i < HAND_SIZE; i++) {
            drawCard();
        }

        if(hand.numberBasicPokemon() == 0) {
            restartHand();
        }
    }

    public void restartHand() {
        System.out.println("Player " + this.name + " restarts hand");
        for (int i = 0; i < HAND_SIZE; i++) {
            Card cardToRemove = hand.removeTopCard();
            deck.addCard(cardToRemove);
        }
        deck.shuffle();
        drawStartingHand();
    }

    public void addBenchPokemon(Card newPokemon) {
        if(this.bench.size() < 5){
            this.bench.addCard(newPokemon);
        } else {
            throw new InvalidMoveException(this.name +"'s Bench is Full");
        }
    }

    public void removeFromHand(Card lastSelectedCard) {
        this.hand.removeCard(lastSelectedCard);
    }

    public boolean canRetreat() {
        return this.bench.size() != 0 && activePokemon.canRetreat();
    }

    public ArrayList<Card> benchAsList() {
        return this.bench.getCards();
    }

    public void retreat(Pokemon lastSelectedCard) {
        int retreatCost = activePokemon.retreatCost;
        activePokemon.removeColorless(retreatCost);
        bench.removeCard(lastSelectedCard);
        bench.addCard(activePokemon);
        this.activePokemon = lastSelectedCard;
    }

    public boolean canAttack(){
        return activePokemon.canAttack();
    }

    public boolean canAttack(Attack attack) {
        return activePokemon.canAttack(attack);
    }

    public void takeDamage(int damageCounters, String damageType) {
        activePokemon.takeDamage(damageCounters, damageType);
    }

    public int getActiveHP() {
        return activePokemon.getCurHP();
    }

    public Card getActivePokemon() {
        return activePokemon;
    }

    public void addEnergyToPokemon(Pokemon selectedPokemon, Energy selectedEnergy) {
        canAddEnergy = false;
        removeFromHand(selectedEnergy);
        selectedPokemon.addEnergy(selectedEnergy);
    }

    public void removeEnergyForAttack(Attack attack) {
        ArrayList<Energy> costs = attack.costs;
        for (Energy cost : costs) {
            activePokemon.removeEnergy(cost);
        }
    }

    public void drawPrizeCards() {
        for(int i = 0; i < PRIZE_CARD_SIZE; i++) {
            Card cardToAdd = deck.removeTopCard();
            prizeCards.addCard(cardToAdd);
        }
    }

    public boolean benchIsEmpty() {
        return bench.size() <= 0;
    }

    public Card setNewActive() {
        return this.activePokemon = (Pokemon) bench.removeTopCard();
    }

    public void pickupPrizeCard() {
        Card cardToAdd = prizeCards.removeTopCard();
        hand.addCard(cardToAdd);
    }

    public boolean canAddEnergy() {
        return canAddEnergy;
    }

    public void passTurn() {
        canAddEnergy = true;
    }

    public boolean evolvePokemon(Card lastSelectedCard) {
        Pokemon evolvedPokemon = (Pokemon) lastSelectedCard;
        String evolvesFrom = evolvedPokemon.evolvesFrom;
        if(bench.containsCardNamed(evolvesFrom)) {
            Pokemon oldPokemon = (Pokemon) bench.getCardFromName(evolvesFrom);
            replacePokemon(oldPokemon, evolvedPokemon);
            return true;
        }
        return false;
    }

    protected void replacePokemon(Pokemon oldPokemon, Pokemon evolvedPokemon) {

    }
}
