package input;

import games.Game;


public interface ActionProcessor {
    void processAction(String action, Game game);
}
