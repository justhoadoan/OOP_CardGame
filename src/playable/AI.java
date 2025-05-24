package playable;

import card.Card;
import games.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AI implements Playable {
    private int id;
    private String strategyType;
    private String name;
    private List<Card> hand;
    private boolean status;
    private Random random;
    private int currentBalance;
    private int currentBet;

    public AI(int id, String name) {
        this.id = id;
        this.name = name;
        this.hand = new ArrayList<>();
        this.status = true;
        this.random = new Random();
        this.currentBalance = 1000; // Starting balance
    }
    public String getStrategyType() {
        return strategyType;
    }
    public void setStrategyType(String strategyType) {
        this.strategyType = strategyType;
    }

    @Override
    public int getId() {
        return id;
    }



    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Card> getHand() {
        return hand;
    }

    @Override
    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    @Override
    public boolean getStatus() {
        return status;
    }

    @Override
    public void resetHand() {
        hand.clear();
    }

    @Override
    public void setStatus(boolean status) {
        this.status = status;
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public int getCurrentBalance() {
        return currentBalance;
    }

    @Override
    public void addCurrentBalance(int amount) {
        this.currentBalance += amount;

    }

    @Override
    public int getCurrentBet() {return currentBet;}

    @Override
    public void setCurrentBet(int currentBet) {this.currentBet = currentBet;}

    public void setCurrentBalance(int balance) {
        this.currentBalance = balance;
    }
}