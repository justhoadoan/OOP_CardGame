package playable;

import card.Card;
import games.Game;

import java.util.List;

public interface Playable {
    int getId();

    String getName();
    List<Card> getHand();
    void setHand(List<Card> hand);
    void setStatus(boolean status);
    boolean getStatus();
    void resetHand();
    void addCard(Card card);
    int getCurrentBalance();
    void addCurrentBalance(int amount);
}