package playable;

import gamemode.GameMode;
import card.Card;
import games.Game;
import server.Client;

import java.util.ArrayList;
import java.util.List;

public class Player implements Playable {
    private String name;
    private int id;
    private List<Card> hand;
    private GameMode gameMode;
    private int currentBalance;
    private boolean status;
    private Client client;

    public Player(String name, int id) {
        this.name = name;
        this.id = id;
        this.hand = new ArrayList<>();
        this.currentBalance = 0;
        this.status = true;
    }

    public boolean getStatus() {return this.status;}

    public void setStatus(boolean status) {this.status = status;}


    @Override
    public String getName() {return name;}

    @Override
    public List<Card> getHand() {return new ArrayList<>(hand);} // Avoid exposing internal data.

    @Override
    public void setHand(List<Card> hand) {this.hand = new ArrayList<>(hand);}

    public void setGameMode(GameMode gameMode) {this.gameMode = gameMode;}

    public void addCard(Card card) {hand.add(card);}

    public void removeCard(Card card) {hand.remove(card);}

    public void resetBalance() {this.currentBalance = 0;}

    public void addCurrentBalance(int currentBalance) {this.currentBalance += currentBalance;}

    public void resetHand() {hand.clear();}

    public int getCurrentBalance() {return currentBalance;}

    public int getId() {return id;}

    @Override
    public void playTurn(Game game) {}

    public Client getClient() {
        return client;
    }
    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", hand=" + hand +
                '}';
    }
    public void deductCurrentBalance(int amount) {
        currentBalance -= amount;
    }

}
