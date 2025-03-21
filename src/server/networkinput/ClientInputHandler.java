package server.networkinput;
import card.Card;
import server.Client;

import java.util.List;

public interface ClientInputHandler {
    void handleInput( Client client, String gameState, List<Card> playerHand);
}