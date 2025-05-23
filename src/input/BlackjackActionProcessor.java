package input;

import games.BlackjackGame;
import games.Game;
import playable.Player;


public class BlackjackActionProcessor implements ActionProcessor {
    @Override
    public void processAction(String action, Game game) {
        Player player = (Player) game.getCurrentPlayer();
            switch (action.toLowerCase()) {
                case "hit":
                    ((BlackjackGame) game).playerHit(player);
                    break;
                case "stand":
                    ((BlackjackGame) game).playerStand(player);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown action: " + action);
            }

    }
}

