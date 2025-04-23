package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Player {

    private static final int HAND_SIZE = 7;
    private Deck deck;
    private Deck hand;
    private Deck bench;
    private Deck discard;
    private Pokemon activePokemon;

    private String name;


    public Player() {
        this("Unnamed Player");
    }

    public Player(String name) {
        this.deck = new Deck();
        this.hand = new Deck();
        this.bench = new Deck();
        this.activePokemon = null;
        this.name = name;
    }

    public void createFullRandomDeck(Random rand) {
        this.deck.addEnergies(15, rand);
        this.deck.addRandomCards(45, rand);
        this.deck.shuffle();
        checkForBasics(rand);
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

    public void drawStartingHand() {
        for (int i = 0; i < HAND_SIZE; i++) {
            drawCard();
        }
    }

    public boolean isDeckEmpty() {
        return this.deck.size() <= 0;
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
        // TODO: check that there is enough energy to retreat
        return this.bench.size() != 0;
    }

    public ArrayList<Card> benchAsList() {
        return this.bench.getCards();
    }

    public void retreat(Pokemon lastSelectedCard) {
        // TODO: remove energy from retreating pokemon
        bench.removeCard(lastSelectedCard);
        bench.addCard(activePokemon);
        this.activePokemon = lastSelectedCard;
    }

    public boolean canAttack(){
        Pokemon activePokemon = this.activePokemon;

        return false;
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
        removeFromHand(selectedEnergy);
        selectedPokemon.addEnergy(selectedEnergy);
    }
}
