package ai;

import card.Card;
import playable.Player;

import java.util.List;

public interface GameState {
    public Player getplayer();
    List<Card> getPlayerHand();
    List<Card> getCommunityCards();
    int getPot();
    int getCurrentBet();
}
