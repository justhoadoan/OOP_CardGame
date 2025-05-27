package games;

import card.Card;
import playable.Playable;

import java.util.List;

public interface Game {
    // Game setup
    void start();
    void addPlayer(Playable player);
    List<Playable> getPlayers();

    // Game state
    Playable getCurrentPlayer();
    List<Card> getPlayerHand(int playerId);
    String getPublicState();
    boolean isGameOver();
}
