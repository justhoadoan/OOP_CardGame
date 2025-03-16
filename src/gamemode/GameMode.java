package gamemode;

import games.Game;
import server.NetworkManager;

import java.util.Map;

public interface GameMode {
    // Core game reference
    void setGame(Game game);
    Game getGame();

    // UI/Display operations
    void displayGameState();
    void getPlayerInput();
    void showResult(String result);

    // Network operations
    void setNetworkManager(NetworkManager networkManager);
    void sendAction(String action);
    void receiveMessage(String message);

    // State synchronization
    Map<Integer, String> getPlayerStates();
}
