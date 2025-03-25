package input;

import games.Game;
import server.Client;

public interface ActionProcessor {
    void processAction(String action, Game game, Client client);
}
