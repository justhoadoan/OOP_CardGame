package strategy;


import gameaction.BlackjackAction;
import gameaction.GameAction;
import gameaction.PokerAction;
import games.Game;
import games.GameType;

import java.util.ArrayList;
import java.util.List;

public class PlayerStrategy {
    // return available actions for the player
    public List<GameAction> getAvailableActions(Game game) {
        List<GameAction> actions = new ArrayList<>();

        if (game.getGameType() == GameType.POKER) {
            // Poker
            actions.add(new PokerAction("Raise", true)); // Cần raiseAmount
            actions.add(new PokerAction("Fold", false));
        } else if (game.getGameType() == GameType.BLACKJACK) {
            // Blackjack
            actions.add(new BlackjackAction("Hit"));
            actions.add(new BlackjackAction("Stand"));
        } else {
            // update future game types here
            throw new UnsupportedOperationException("Game type not supported: " + game.getGameType());
        }

        return actions;
    }
}