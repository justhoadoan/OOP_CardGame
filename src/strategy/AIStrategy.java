package strategy;

import gameaction.GameAction;
import games.Game;
import games.PokerGame;

import java.util.List;
import java.util.Random;

public class AIStrategy {
    private Random random;

    public AIStrategy() {
        this.random = new Random();
    }
    //Tu's part
    public GameAction decidePokerAction(PokerGame pokerGame, List<GameAction> availableActions) {
        // AI logic here
        // For now, just return the first available action
        return availableActions.get(0);
    }
    public GameAction decideBlackjackAction(List<GameAction> availableActions) {
        // AI logic here
        // For now, just return the first available action
        return availableActions.get(0);
    }


}