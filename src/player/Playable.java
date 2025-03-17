package player;

import card.Card;
import games.Game;

import java.util.List;

public interface Playable {
    void playTurn(Game game);
    String getName();
    List<Card> getHand();
    void setHand(List<Card> hand);
}