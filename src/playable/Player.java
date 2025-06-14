package playable;

import cards.card.Card;
import java.util.ArrayList;
import java.util.List;

public class Player implements Playable {
    private String name;
    private int id;
    private List<Card> hand;
    private int currentBalance;
    private boolean status;
    private int currentBet;
    private boolean hasBet;
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

    public boolean getHasBet() {
        return hasBet;
    }
    public void setHasBet(boolean hasBet) {
        this.hasBet = hasBet;
    }
    @Override
    public void addCard(Card card) {
        if (this.hand == null) {
            this.hand = new ArrayList<>();
        }
        this.hand.add(card);
    }

    public void addCurrentBalance(int currentBalance) {this.currentBalance += currentBalance;}

    @Override
    public int getCurrentBet() {return currentBet;}

    @Override
    public void setCurrentBet(int currentBet) {this.currentBet = currentBet;}

    public void resetHand() {this.hand = new ArrayList<>();}

    public int getCurrentBalance() {return currentBalance;}

    public int getId() {return id;}


    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", hand=" + hand +
                '}';
    }
}
