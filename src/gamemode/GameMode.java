package gamemode;

import games.Game;
import server.NetworkManager;

import java.util.Map;

public interface GameMode {
    // Core game reference
    void setGame(Game game);
    void updateDisplay(String playerHand, String publicState, String winner);
}
