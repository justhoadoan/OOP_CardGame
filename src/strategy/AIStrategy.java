package strategy;

import gameaction.GameAction;
import games.BlackJackGame;
import games.Game;
import games.GameType;
import games.PokerGame;

import java.util.List;
import java.util.Random;

public class AIStrategy {
    private static Random random;

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
        String[] strongHands = {"AA", "KK", "QQ", "JJ", "AK", "AQ", "TT", "99"};
        String[] playableButtonHands = {"A2", "A3", "A4", "A5", "KT", "QT", "JT", "T9", "98", "77", "66"};
        String[] playableBigBlindHands = {"A2", "A3", "A4", "A5", "KQ", "KJ", "QJ", "JT", "T9", "88", "77"};

        if (isInArray(hand, strongHands)) {
            return "Raise";
        }

        if (position.equals("button")) {
            if (isInArray(hand, playableButtonHands)) {
                return "Raise";
            }
        } else if (position.equals("bigBlind")) {
            if (isInArray(hand, playableBigBlindHands)) {
                return "Call";
            }
        }

        return "Fold";
    }

    public static boolean shouldContinuationBet(boolean wasPreflopRaiser, String flop) {
        // Basic continuation bet logic (70% of the time if preflop raiser and board is dry)
        if (!wasPreflopRaiser) {
            return false;
        }

        if (isDryBoard(flop)) {
            return random.nextDouble() < 0.7; // 70% chance to c-bet
        }
        return false;
    }

    private static boolean isDryBoard(String flop) {
        // Dry board means uncoordinated cards (no straight or flush draws)
        return flop.matches(".*[KQJ72].*");
    }

    private static boolean isInArray(String value, String[] array) {
        for (String s : array) {
            if (s.equals(value)) {
                return true;
            }
        }
        return false;
    }
        // For now, just return the first available action
        return availableActions.get(0);
    }
    public GameAction decideBlackjackAction(BlackJackGame blackJackGame, List<GameAction> availableActions) {
        // AI logic here
        // For now, just return the first available action
        return availableActions.get(0);
    }


}