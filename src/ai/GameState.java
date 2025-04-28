package ai;

import card.Card;
import playable.Player;

import java.util.List;

public interface GameState {
    Player getplayer();
    List<Card> getPlayerHand();

    int getPot();
    int getCurrentBet();
}
