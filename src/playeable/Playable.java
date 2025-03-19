package playeable;

import card.Card;
import games.Game;

import java.util.List;

public interface Playable {
    int getId();
    void playTurn(Game game);
    String getName();
    List<Card> getHand();
    void setHand(List<Card> hand);
    void setStatus(boolean status);
    boolean getStatus();
}