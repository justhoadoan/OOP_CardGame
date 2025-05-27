package processor;

import games.BlackjackGame;
import games.Game;
import playable.Playable;

public class BlackjackActionProcessor implements ActionProcessor {
    @Override
    public void processAction(String action, Game game, Playable currentPlayer) {

        switch (action.toLowerCase()) {
            case "hit":
                ((BlackjackGame) game).playerHit(currentPlayer);
                break;
            case "stand":
                ((BlackjackGame) game).playerStand(currentPlayer);
                break;
            default:
                throw new IllegalArgumentException("Unknown action: " + action);
        }

    }

}
