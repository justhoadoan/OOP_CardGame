package processor;

import games.Game;
import playable.Playable;


public abstract class ActionProcessor {
    void processAction(String action, Game game, Playable currentPlayer) {
    }
}
