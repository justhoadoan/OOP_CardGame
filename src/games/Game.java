package games;

import card.Card;
import updatedisplay.GameMode;
import playable.Playable;

import java.util.List;

public interface Game {
    // Game setup
    void start();
    void addPlayer(Playable player);
    List<Playable> getPlayers();

    // Game state
    void nextPlayer();
    Playable getCurrentPlayer();

    Playable getPlayerBeforeDealer();

    List<Card> getPlayerHand(int playerId);
    String getPublicState();

    boolean isGameOver();
    // Player actions
    void playerRaise(Playable player, int raiseAmount);
    void playerFold(Playable player );
    void playerHit(Playable player);
    void playerStand(Playable player);

    // Game mode management
    GameMode getGameMode();
    void setGameMode(GameMode gameMode);

    // Network communication
    void broadcastState();

    GameType getGameType();


}
