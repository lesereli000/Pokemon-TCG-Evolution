package main;

import java.util.ArrayList;

public class Player {
    private static final int PRIZE_CARD_SIZE = 6;
    private static final int HAND_SIZE = 7;
    protected Deck deck;
    protected Deck hand;
    protected Deck bench;
    protected Pokemon activePokemon;
    protected Deck prizeCards;
    private String name;
    private boolean canAddEnergy;
    protected boolean hasActive;

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
        this.canAddEnergy = true;
        this.hasActive = false;

    }

    public void createCustomDeck() {
        this.deck.createDeckFromFile("Overgrowth.txt");
        this.deck.shuffle();
    }

    public boolean drawCard(){
        if(this.deckSize() != 0) {
            Card drawnCard = deck.removeTopCard();
            hand.addCard(drawnCard);
            return true;
        }else{
            return false;
        }
    }

    public ArrayList<Card> handAsList() {
        return hand.getCards();
    }

    public void setActivePokemon(Pokemon activePokemon) {
        this.activePokemon = activePokemon;
        hasActive = true;
        removeFromHand(this.activePokemon);
    }

    public ArrayList<Card> getOnlyPokemonFromHand() {
        return hand.getOnlyPokemon();
    }

    public ArrayList<Card> getPokemonOnBench() {
        return bench.getCards();
    }

    public ArrayList<Card> getAllEnergyFromHand() {
        return hand.getOnlyEnergy();
    }

    public String getName() {
        return name;
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
        for (int i = 0; i < HAND_SIZE; i++) {
            Card cardToRemove = hand.removeTopCard();
            deck.addCard(cardToRemove);
        }
        deck.shuffle();
        drawStartingHand();
    }

    public void addBenchPokemon(Card newPokemon) {
        if(this.bench.size() < 5){
            System.out.println("4");
            this.bench.addCard(newPokemon);
        } else {
            System.out.println("5");
            throw new InvalidMoveException(this.name +"'s Bench is Full");
        }
    }

    public void removeFromHand(Card lastSelectedCard) {
        this.hand.removeCard(lastSelectedCard);
    }

    public void retreat(Pokemon lastSelectedCard) {
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

    public void drawPrizeCards() {
        for(int i = 0; i < PRIZE_CARD_SIZE; i++) {
            Card cardToAdd = deck.removeTopCard();
            prizeCards.addCard(cardToAdd);
        }
    }

    public boolean benchIsEmpty() {
        return bench.size() <= 0;
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

    public int deckSize() {
        return deck.size();
    }

    public String evolvePokemon(Pokemon evolution, Pokemon evolvesFrom) {
        // TODO: Prepare "evolution" to be inserted (damage, energies, conditions, attached cards, etc.)

        hand.removeCard(evolution);

        if(evolvesFrom == activePokemon) {
            activePokemon = evolution;
            return "Active";
        }

        try{
            bench.removeCard(evolvesFrom);
            bench.addCard(evolution);
            return "Bench";
        } catch (InvalidMoveException e) {
            return "Error";
        }
    }

    protected boolean hasActive() {
        return hasActive;
    }

    protected void setNewActivePokemon(Pokemon newActive) {
        this.activePokemon = newActive;
        bench.removeCard(newActive);
    }

    public Deck getBench() {
        return bench;
    }

    public int getNumPrizeCards() {
        return prizeCards.size();
    }

    public ArrayList<Card> getPreEvolutions(Pokemon evolution) {
        ArrayList<Card> preEvs = new ArrayList<>();
        String evolvesFrom = evolution.getEvolvesFrom();
        if(activePokemon.getName().equals(evolvesFrom)) {
            System.out.println("9");
            preEvs.add(activePokemon);
        }

        for(Card pokemon : bench.getOnlyPokemon()){
            if(pokemon.getName().equals(evolvesFrom)) {
                System.out.println("10");
                preEvs.add(pokemon);
            }
        }

        return preEvs;
    }
}
