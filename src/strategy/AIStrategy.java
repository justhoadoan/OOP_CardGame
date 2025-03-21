package strategy;

import gameaction.GameAction;
import games.BlackJackGame;
import games.Game;
import games.GameType;
import games.PokerGame;

import java.util.List;
import java.util.Random;

public class AIStrategy {
    private Random random;

    public AIStrategy() {
        this.random = new Random();
    }
    public GameAction decideAction(Game game, List<GameAction> availableActions) {
        if (availableActions == null || availableActions.isEmpty()) {
            throw new IllegalStateException("No available actions for AI");
        }

        if (game.getGameType() == GameType.POKER) {
            return decidePokerAction((PokerGame) game, availableActions);
        } else if (game.getGameType() == GameType.BLACKJACK) {
            return decideBlackjackAction((BlackJackGame)game, availableActions);
        } else {

            throw new UnsupportedOperationException("Game type not supported: " + game.getGameType());
        }
    }
    //Tu's part
    public GameAction decidePokerAction(PokerGame pokerGame, List<GameAction> availableActions) {

        // AI logic here
        // For now, just return the first available action
        return availableActions.get(0);
    }
    public GameAction decideBlackjackAction(BlackJackGame blackJackGame, List<GameAction> availableActions) {
        // AI logic here
        // For now, just return the first available action
        return availableActions.get(0);
    }


}