package player;
import card.*;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private List<Card> hand;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public Card playCard() {
        if (hand.isEmpty()) {
            throw new IllegalStateException("No cards left in hand!");
        }
        return hand.remove(0);
    }

    public int getHandSize() {
        return hand.size();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " has " + hand.size() + " cards.";
    }
}