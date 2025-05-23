package input;

import games.Game;
import playable.Playable;


public interface ActionProcessor {
    void processAction(String action, Game game, Playable currentPlayer);
}
