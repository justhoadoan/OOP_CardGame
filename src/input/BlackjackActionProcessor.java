package input;

import games.BlackjackGame;
import games.Game;
import playable.Player;
import server.Client;

public class BlackjackActionProcessor implements ActionProcessor {
    @Override
    public void processAction(String action, Game game, Client client) {
        Player player = (Player) game.getCurrentPlayer();
            String ClientId = String.valueOf(client.getClientId());
            String message;
            switch (action.toLowerCase()){
                case "hit":
                    if(client!=null){
                        client.sendMessage("Hit:" + ClientId);
                    }
                    else{
                        ((BlackjackGame) game).playerHit(player);
                    }

                case "stand":
                    if(client!=null){
                        client.sendMessage("Stand:" + ClientId);
                    }
                    else{
                        ((BlackjackGame) game).playerStand(player);
                    }
            }
        }

    }

