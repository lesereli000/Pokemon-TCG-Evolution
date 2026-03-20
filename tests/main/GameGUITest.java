package main;

import java.awt.*;
import javax.swing.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

//https://stackoverflow.com/questions/601274/how-do-i-properly-load-a-bufferedimage-in-java

public class GameGUITest implements main.GUI {

    private Card player1activeCard;
    private Card player2activeCard;

    private ArrayList<Card> player1benchCards = new ArrayList<Card>();
    private ArrayList<Card> player2benchCards = new ArrayList<Card>();
    
    private ArrayList<JButton> selectedCardActionButtons = new ArrayList<>();

    private volatile boolean waitForAction = false;
    private boolean activeTurn = false;
    private boolean confirmPokemonState = false;
    private volatile Card lastSelectedCard = null;
    private Attack lastSelectedAttack;
    private volatile String lastActionButtonPressed;
    private int playerTurn = 0;
    private boolean cancelled;
    private Locale locale;
    private ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", Locale.US);

    public void createGUI() {}

    public void createFlipButton() {
        while (!waitForAction) {
            Thread.onSpinWait();
        }
        this.waitForAction = false;
    }

    @Override
    public void waitForPassTurn() {
        createPassTurnButton();
        while (!waitForAction) {
            Thread.onSpinWait();
        }
        this.waitForAction = false;
    }

    @Override
    public void removeButton(JButton button) {}

    public void setDeckColor(Color deckColor) {}

    public void displayMessage(String message) {}

    @Override
    public void makeActiveCard(Card newActive, int playerTurn) {
        if (playerTurn == 1) {
            this.player1activeCard = newActive;
        } else {
            this.player2activeCard = newActive;
        }
    }

    @Override
    public void addBenchCard(Card newBench, int playerTurn) {
        if (playerTurn == 1 && this.player1benchCards.size() < 6) {
            this.player1benchCards.add(newBench);
        } else if (playerTurn == 2 && this.player2benchCards.size() < 6) {
            this.player2benchCards.add(newBench);
        }
    }

    @Override
    public void removeBenchCard(Card card, int playerTurn) {
        if (playerTurn == 1 && !this.player1benchCards.isEmpty()) {
            this.player1benchCards.remove(card);
        } else if (playerTurn == 2 && !this.player2benchCards.isEmpty()) {
            this.player2benchCards.remove(card);
        }
    }

    public void refreshGUI() {}

    private void setLastSelectedCard(Card card) {
        this.lastSelectedCard = card;
    }

    private void setLastSelectedAttack(Attack attack) {
        this.lastSelectedAttack = attack;
    }

    public Attack getLastSelectedAttack() {
        Attack lastAttack = this.lastSelectedAttack;
        this.lastSelectedAttack = null;
        return lastAttack;
    }

    @Override
    public Card getLastSelectedCard() {
        if(this.lastSelectedCard == null) {
            return new Pokemon("null","Grass",0,10);
        }
        Card lastCard = this.lastSelectedCard;
        this.lastSelectedCard = null;
        return lastCard;
    }


    public void setupActivePokemon() {
        lastSelectedCard = null;
        activeTurn = false;
    }

    @Override
    public void displayConfirmAndCancelButton() {
        String message = messages.getString("confirmSelection");
        JButton btn = new JButton(message);
        this.confirmPokemonState = true;
        btn.addActionListener(e -> {
            cancelled = false;
            this.waitForAction = true;
            this.confirmPokemonState = false;
        });
        String msg = messages.getString("cancel");
        JButton cancel = new JButton(msg);
        cancel.addActionListener(e -> {
            this.waitForAction = true;
            this.confirmPokemonState = false;
            cancelled = true;
        });
    }

    @Override
    public void displayCardReport(Card card) {
        if (card instanceof Pokemon) displayPokemonReport((Pokemon) card);
        else if (card instanceof Trainer) displayTrainerReport((Trainer) card);
    }

    private void displayTrainerReport(Trainer trainer) {
        StringBuilder report = new StringBuilder();
        String name = messages.getString("trainerName");
        name = MessageFormat.format(name, trainer.getName());
        report.append(name).append("\n");
        String effect = messages.getString("trainerEffect");
        report.append(effect).append("\n");
        switch(trainer.getName()) {
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
        displayMessage(report.toString());
    }

    @Override
    public void displayPossibleAttacks(ArrayList<Attack> attacks) {
        String attackReport = generateAttackReport(attacks);
        for (Attack attack : attacks) {
            createLinkedButtonAttack(attack);
        }
    }

    @Override
    public void displayAttackMessage(Player currentPlayer, Player defendingPlayer, Attack attack) {
        StringBuilder attackReport = new StringBuilder();
        String currentName = currentPlayer.getName();
        String defendingName = defendingPlayer.getName();
        Pokemon currentPokemon = currentPlayer.activePokemon;
        Pokemon defendingPokemon = defendingPlayer.activePokemon;

        String atkReport = messages.getString("atkReport");
        attackReport.append(atkReport).append("\n");

        String pokemonName = messages.getString("pokemonName");
        pokemonName = MessageFormat.format(pokemonName, currentName, currentPokemon.getName());
        attackReport.append(pokemonName).append("\n");

        String attacked = messages.getString("attacked");
        attacked = MessageFormat.format(attacked, defendingName, defendingPokemon.getName());
        attackReport.append(attacked).append("\n\n");

        String tookDmg = messages.getString("tookDmg");
        tookDmg = MessageFormat.format(tookDmg, defendingName, attack.getDamage());
        attackReport.append(tookDmg).append("\n");

        String newHp = messages.getString("newHp");
        newHp = MessageFormat.format(newHp, defendingName, defendingPokemon.getName(), defendingPokemon.getCurHP());
        attackReport.append(newHp);

        displayMessage(attackReport.toString());
    }


    @Override
    public void displayRetreatEnergy(Pokemon pokemon, boolean canRetreat) {
        StringBuilder retreatReport = new StringBuilder();
        String retreatMessageTop = messages.getString("retreatMessageTop");
        retreatMessageTop = MessageFormat.format(retreatMessageTop, pokemon.retreatCost);
        String retreatMessageBottom = messages.getString("retreatMessageBottom");
        retreatMessageBottom = MessageFormat.format(retreatMessageBottom, pokemon.getName());
        retreatReport.append(retreatMessageTop).append("\n").append(retreatMessageBottom).append("\n");

        if (!canRetreat) {
            String cantRetreat = messages.getString("cantRetreat");
            retreatReport.append("\n").append(cantRetreat);
            displayMessage(retreatReport.toString());
        } else {
            displayMessage(retreatReport.toString());
        }
    }

    @Override
    public void replaceActiveCard(Card selectedCard, int playerTurn) {
        if (playerTurn == 1) {
            addBenchCard(player1activeCard, playerTurn);
        } else {
            addBenchCard(player2activeCard, playerTurn);
        }
        removeBenchCard(selectedCard, playerTurn);
        makeActiveCard(selectedCard, playerTurn);
    }

    @Override
    public void displayDeadActiveInfo(Player defendingPlayer) {
        StringBuilder deadPokemonReport = new StringBuilder();
        Pokemon deadPokemon = (Pokemon) defendingPlayer.getActivePokemon();
        String pokemonDied = messages.getString("pokemonDied");
        pokemonDied = MessageFormat.format(pokemonDied, defendingPlayer.getName(), deadPokemon.getName());
        deadPokemonReport.append(pokemonDied).append("\n");
        String selectNewActive = messages.getString("selectNewActive");
        deadPokemonReport.append(selectNewActive);
        displayMessage(deadPokemonReport.toString());
    }

    @Override
    public void displayWinningMessage(Player winner, Player loser) {
        String winnerName = winner.getName();
        String loserName = loser.getName();
        String winningMessageTop = messages.getString("winningMessageTop");
        String winningMessageBottom = messages.getString("winningMessageBottom");
        winningMessageTop = MessageFormat.format(winningMessageTop, winnerName);
        winningMessageBottom = MessageFormat.format(winningMessageBottom, loserName);
        displayMessage(winningMessageTop + "\n" + winningMessageBottom);
    }

    @Override
    public boolean hasCardSelected() {
        return lastSelectedCard != null;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public Locale displayLocaleOptions() {
//        displayMessage("Select a language!\n\nWählen Sie eine Sprache aus!");
//        JButton engBtn = new JButton("English");
//        JButton germanBtn = new JButton("Deutsch");

//        engBtn.addActionListener(e -> {
//            this.waitForAction = true;
//            locale = Locale.US;
//            messages = ResourceBundle.getBundle("MessagesBundle", locale);
//            try {
//                flag = ImageIO.read(getClass().getResource("/USFlag.png"));
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//            removeButton(engBtn);
//            removeButton(germanBtn);
//        });

//        germanBtn.addActionListener(e -> {
//            this.waitForAction = true;
//            locale = Locale.GERMANY;
//            messages = ResourceBundle.getBundle("MessagesBundle", locale);
//            try {
//                flag = ImageIO.read(getClass().getResource("/deutschflag.png"));
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//            removeButton(engBtn);
//            removeButton(germanBtn);
//        });

        waitForButtonPressed();
        return locale;
    }

    @Override
    public boolean gameIsOver() {
//        return buttons.isEmpty();
        return true;
    }

    public String generateAttackReport(ArrayList<Attack> attacks) {
        StringBuilder report = new StringBuilder();
        for (Attack attack : attacks) {
            String costs = messages.getString("costs");
            report.append("\n").append(attack.name).append(":\n").append(costs).append("\n");
            for (Energy energy : attack.costs) {
                report.append("• ").append(energy.getName()).append("\n");
            }
            String dmg = messages.getString("dmg");
            dmg = MessageFormat.format(dmg, attack.getDamage());
            report.append(dmg).append("\n");
        }
        return report.toString();
    }

    @Override
    public void displayPokemonReport(Pokemon pokemon) {
        StringBuilder report = new StringBuilder();
        int stage = pokemon.getStage();
        //General info
        String pokReport = messages.getString("pokReport");
        report.append(pokReport).append("\n\n");

        String pokName = messages.getString("pokName");
        pokName = MessageFormat.format(pokName, pokemon.getName());
        report.append(pokName).append("\n");

        String pokStage = messages.getString("pokStage");
        pokStage = MessageFormat.format(pokStage, stage);
        report.append(pokStage).append("\n");

        String pokType = messages.getString("pokType");
        pokType = MessageFormat.format(pokType, pokemon.getType());
        report.append(pokType).append("\n");

        String pokHP= messages.getString("pokHP");
        pokHP = MessageFormat.format(pokHP, pokemon.getCurHP());
        report.append(pokHP).append("\n");

        String retreatCost = messages.getString("retreatCost");
        retreatCost = MessageFormat.format(retreatCost, pokemon.retreatCost);
        report.append(retreatCost).append("\n");

        if(stage > 0) {
            String evolvesFrom = messages.getString("evolvesFrom");
            evolvesFrom = MessageFormat.format(evolvesFrom, pokemon.getEvolvesFrom());
            report.append(evolvesFrom).append("\n");
        }


        //Energies
        String pokEnergies = messages.getString("pokEnergies");
        report.append("\n").append(pokEnergies).append("\n");
        if (pokemon.energies.isEmpty()) {
            String none = messages.getString("none");
            report.append(none).append("\n");
        } else {
            for (Energy energy : pokemon.energies) {
                report.append("• ").append(energy.getName()).append("\n");
            }
        }

        //Attacks
        String atks = messages.getString("atks");
        report.append("\n").append(atks).append("\n");
        report.append(generateAttackReport(pokemon.attacks));

        displayMessage(report.toString());
    }

    @Override
    public void waitForAction() {
        while (!waitForAction) {
            Thread.onSpinWait();
        }
        this.waitForAction = false;
    }

    @Override
    public void displayCards(ArrayList<Card> playerCards) {
        for (Card currCard : playerCards) {
            String cardName = currCard.getName();
            createLinkedButtonCard(cardName, currCard);
        }
    }

    @Override
    public void closeWindow() {}

    @Override
    public void removePrizeCard(int playerNum) {}

    private void displayPokemonActionButtons(Pokemon card) {
        removeSelectedCardActionButtons();
        if (card.stage == 0) {
            String addPokBench = messages.getString("addPokBench");
            selectedCardActionButtons.add(createLinkedButtonAction(addPokBench, "AddToBench"));
        } else {
            String evolveToPok = messages.getString("evolveToPok");
            selectedCardActionButtons.add(createLinkedButtonAction(evolveToPok, "Evolve"));
        }
    }


    private void displayEnergyActionButtons(Energy energy) {
        removeSelectedCardActionButtons();
        String addEnergy = messages.getString("addEnergy");
        selectedCardActionButtons.add(createLinkedButtonAction(addEnergy, "AddEnergy"));
    }

    private void displayTrainerActionButtons(Trainer trainer) {
        removeSelectedCardActionButtons();
        String playTrainer = messages.getString("playTrainer");
        selectedCardActionButtons.add(createLinkedButtonAction(playTrainer, "PlayTrainer"));
    }

    private void displayActiveActionButton(){
        removeSelectedCardActionButtons();
        String actvSelectedPok = messages.getString("actvSelectedPok");
        selectedCardActionButtons.add(createLinkedButtonAction(actvSelectedPok, "AddToBench"));
    }

    private void removeSelectedCardActionButtons() {
        if(!selectedCardActionButtons.isEmpty()) {
            for (int i = 0; i<selectedCardActionButtons.size();i++) {
                JButton btn = selectedCardActionButtons.get(i);
                removeButton(btn);
                selectedCardActionButtons.remove(btn);
            }
        }
    }

    @Override
    public String waitForButtonPressed() {
        while (!waitForAction) {
            Thread.onSpinWait();
        }
        this.waitForAction = false;
        return this.lastActionButtonPressed;
    }

    @Override
    public void displayActionButtons() {
        activeTurn = true;
        String passTurn = messages.getString("passTurn");
        String atkOpp = messages.getString("atkOpp");
        String retreatPok = messages.getString("retreatPok");
        String seeCardInfo = messages.getString("seeCardInfo");
        createLinkedButtonAction(passTurn, "PassTurn");
        createLinkedButtonAction(atkOpp, "Attack");
        createLinkedButtonAction(retreatPok, "Retreat");
        createLinkedButtonAction(seeCardInfo, "CardInfo");
    }


    @Override
    public JButton createButton(String message) {
        JButton btn = new JButton(message);
        btn.addActionListener(e -> {
        });

        return btn;
    }

    @Override
    public JButton createLinkedButtonCard(String message, Card currCard) {
        JButton btn = new JButton(message);
//        btn.addActionListener(e -> {
//            btn.setBackground(Color.GREEN);
//            lastSelectedButton = btn;
//            if(!getLastSelectedCard().equals(currCard)) {
//                setLastSelectedCard(currCard);
//                if (!confirmPokemonState) {
//                    if (activeTurn) {
//                        if (currCard instanceof Pokemon) {
//                            displayPokemonActionButtons((Pokemon) currCard);
//                        }
//                        if (currCard instanceof Energy) {
//                            displayEnergyActionButtons((Energy) currCard);
//                        }
//                        if (currCard instanceof Trainer) {
//                            displayTrainerActionButtons((Trainer) currCard);
//                        }
//                    } else {
//                        if (currCard instanceof Pokemon && ((Pokemon) currCard).stage == 0) {
//                            displayActiveActionButton();
//                        } else {
//                            removeSelectedCardActionButtons();
//                        }
//                    }
//                }
//            }
//        });

        return btn;
    }

    private JButton createLinkedButtonAttack(Attack currAttack) {
        JButton btn = new JButton(currAttack.name);
        btn.addActionListener(e -> {
            setLastSelectedAttack(currAttack);
        });

        return btn;
    }

    private JButton createLinkedButtonAction(String name, String action) {
        JButton btn = new JButton(name);
        btn.addActionListener(e -> {
            this.lastActionButtonPressed = action;
            waitForAction = true;
        });

        return btn;
    }

    @Override
    public JButton createSDHoldingButton(String message) {
        JButton btn = new JButton(message);
        btn.addActionListener(e -> {
            this.waitForAction = true;
            removeButton(btn);
        });

        return btn;
    }

    @Override
    public JButton createPassTurnButton() {
        String message = messages.getString("passTurn");
        JButton btn = new JButton(message);
        btn.addActionListener(e -> {
            this.waitForAction = true;
            removeButton(btn);
        });

        return btn;

    }

    @Override
    public void removeAllButtons() {

    }

    @Override
    public void retreat(Card newCard, int playerTurn) {
        Card oldActive = playerTurn == 1 ? player1activeCard : player2activeCard;
        if (playerTurn == 1) {
            this.player1benchCards.remove(newCard);
            this.player1benchCards.add(oldActive);
            this.player1activeCard = newCard;
        } else if (playerTurn == 2) {
            this.player2benchCards.remove(newCard);
            this.player2benchCards.add(oldActive);
            this.player2activeCard = newCard;
        }
    }

    @Override
    public void updateTurn(int playerTurn) {

    }
}