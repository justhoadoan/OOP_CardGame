package server.networkinput;
import server.Client;
public interface ClientInputHandler {
    void handleInput( Client client, String gameState, String playerHand);
}