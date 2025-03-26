package ai;

import card.Card;
import java.util.*;

public class RuleBasedAIStrategy implements AIStrategy  {
    @Override
    public String getAction(GameState state) {
        List<Card> hand = state.getPlayerHand();
        List<Card> communityCards = state.getCommunityCards();
        int pot = state.getPot();
        int currentBet = state.getCurrentBet();
        HandStrength strength = evaluateHandStrength(hand, communityCards);

        // Decision-making rules
        if (strength == HandStrength.STRONG) {
            return "RAISE";
        } else if (strength == HandStrength.MEDIUM) {
            return (currentBet < pot / 3) ? "CALL" : "FOLD";
        } else if (strength == HandStrength.DRAWING && (currentBet < pot / 4)) {
            return "CALL";
        } else {
            return "FOLD";
        }
    }

    private HandStrength evaluateHandStrength(List<Card> hand, List<Card> communityCards) {
        List<Card> allCards = new ArrayList<>(hand);
        allCards.addAll(communityCards);

        if (isPair(hand) && (hand.get(0).getRank().equals("Ace") ||
                hand.get(0).getRank().equals("King") ||
                hand.get(0).getRank().equals("Queen") ||
                hand.get(0).getRank().equals("Jack") ||
                hand.get(0).getRank().equals("10"))) {
            return HandStrength.STRONG;
        }
        if (isSuitedConnectors(hand)) {
            return HandStrength.DRAWING;
        }
        if (isPair(hand)) {
            return HandStrength.MEDIUM;
        }
        return HandStrength.WEAK;
    }

    private boolean isPair(List<Card> hand) {
        return hand.get(0).getRank().equals(hand.get(1).getRank());
    }

    private boolean isSuitedConnectors(List<Card> hand) {
        return hand.get(0).getSuit().equals(hand.get(1).getSuit()) &&
                Math.abs(rankToValue(hand.get(0).getRank()) - rankToValue(hand.get(1).getRank())) == 1;
    }

    enum HandStrength { STRONG, MEDIUM, DRAWING, WEAK }

    private int rankToValue(String rank) {
        switch (rank) {
            case "Ace":
                return 14;
            case "King":
                return 13;
            case "Queen":
                return 12;
            case "Jack":
                return 11;
            default:
                return Integer.parseInt(rank);
        }
    }
}

