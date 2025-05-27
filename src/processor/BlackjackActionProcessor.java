package processor;

import gamemode.JavaFXBlackjackMode;
import games.BlackjackGame;
import games.Game;
import playable.Playable;


public class BlackjackActionProcessor implements ActionProcessor {
    @Override
    public void processAction(String action, Game game, Playable currentPlayer) {

            switch (action.toLowerCase()) {
                case "hit":
                    game.playerHit(currentPlayer);
                    break;
                case "stand":
                    game.playerStand(currentPlayer);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown action: " + action);
            }

    }
}

