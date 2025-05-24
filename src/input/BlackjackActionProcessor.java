package input;

import games.BlackjackGame;
import games.Game;
import playable.Player;
import server.Client;

public class BlackjackActionProcessor implements ActionProcessor {
    @Override
    public void processAction(String action, Game game, Client client) {
        Player player = (Player) game.getCurrentPlayer();

        if (client == null) {
            // Handle the case where client is null (e.g., local game)
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
        } else {
            // Handle the case where client is not null (e.g., networked game)
            String clientId = String.valueOf(client.getClientId());
            switch (action.toLowerCase()) {
                case "hit":
                    client.sendMessage("Hit:" + clientId);
                    break;
                case "stand":
                    client.sendMessage("Stand:" + clientId);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown action: " + action);
            }
        }
    }
}

