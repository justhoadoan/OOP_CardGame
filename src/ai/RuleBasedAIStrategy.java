package ai;

import card.Card;
import playable.Player;

import java.util.*;

public class RuleBasedAIStrategy implements AIStrategy  {
    @Override
    public String getAction(GameState state) {
        try {
            // Safety checks for null or empty collections
            List<Card> playerHand = state.getPlayerHand();
            List<Card> communityCards = ((PokerState) state).getCommunityCards();
            if (playerHand == null) playerHand = new ArrayList<>();
            if (communityCards == null) communityCards = new ArrayList<>();

            int handStrength = evaluateHandStrength(playerHand, communityCards);
            int currentBet = state.getCurrentBet();
            Player player = state.getplayer();

            // Simple decision making:
            // Strong hands - raise
            // Decent hands - call if bet is low
            // Weak hands - fold
            if (handStrength > 1) {
                return "raise";
            }
            else if(handStrength == 1 && currentBet < player.getCurrentBalance() / 10) {
                return "raise";
            }
            else if (handStrength == 0 && isSuitedConnectors(playerHand)) {
                return "raise";
            }
            else if (handStrength == 0 && currentBet > player.getCurrentBalance() / 5) {
                return "fold";
            }
            else if (handStrength == 0 && currentBet < player.getCurrentBalance() / 5) {
                return "raise";
            }
            else {
                return "fold";
            }
        } catch (Exception e) {
            System.out.println("Error in AI strategy: " + e.getMessage());
            e.printStackTrace();
            // Default to folding in case of errors
            return "raise";
        }
    }

    private int evaluateHandStrength(List<Card> hand, List<Card> communityCards) {
        // Add null checks and empty checks
        if (hand == null) hand = new ArrayList<>();
        if (communityCards == null) communityCards = new ArrayList<>();
        
        // Combine hand and community cards
        List<Card> allCards = new ArrayList<>(hand);
        allCards.addAll(communityCards);
        
        // If no cards at all, return lowest strength
        if (allCards.isEmpty()) {
            return 0;
        }
        
        // Basic evaluation:
        if (isPair(allCards) || isFlush(allCards) || isStraight(allCards)) return 2;
        
        // Check for high card value
        int highestValue = 0;
        for (Card card : allCards) {
            int value = getCardValue(card);
            if (value > highestValue) {
                highestValue = value;
            }
        }
        
        // For high cards: 10 or above is decent
        return highestValue >= 3 ? 1 : 0;
    }

    private boolean isPair(List<Card> hand) {
        // Kiểm tra null và empty
        if (hand == null || hand.isEmpty()) {
            return false;
        }
        
        // Tạo map để đếm số lần xuất hiện của mỗi rank
        Map<String, Integer> rankCount = new HashMap<>();
        for (Card card : hand) {
            String rank = card.getRank();
            rankCount.put(rank, rankCount.getOrDefault(rank, 0) + 1);
        }
        
        // Kiểm tra xem có rank nào xuất hiện 2 lần không
        return rankCount.values().stream().anyMatch(count -> count == 2);
    }

    private boolean isFlush(List<Card> cards) {
        if (cards == null || cards.size() < 5) return false;
        Map<String, Integer> suitCount = new HashMap<>();
        for (Card card : cards) {
            suitCount.put(card.getSuit(), suitCount.getOrDefault(card.getSuit(), 0) + 1);
        }
        return suitCount.values().stream().anyMatch(count -> count >= 5);
    }

    private boolean isStraight(List<Card> cards) {
        if (cards == null || cards.isEmpty()) return false;
        List<Integer> values = cards.stream()
                .map(this::getCardValue)
                .distinct()
                .sorted()
                .toList();
        for (int i = 0; i <= values.size() - 5; i++) {
            if (values.get(i + 4) - values.get(i) == 4) return true;
        }
        return false;
    }

    private int getCardValue(Card card) {
        switch (card.getRank()) {
            case "A": return 14;
            case "K": return 13;
            case "Q": return 12;
            case "J": return 11;
            default:
                try {
                    return Integer.parseInt(card.getRank());
                } catch (NumberFormatException e) {
                    return 0; // Giá trị mặc định cho trường hợp không phải số hoặc face card
                }
        }
    }

    private boolean isSuitedConnectors(List<Card> hand) {
        // Kiểm tra xem hand có đủ 2 lá không
        if (hand == null || hand.size() < 2) {
            return false;
        }
        
        return hand.get(0).getSuit().equals(hand.get(1).getSuit()) &&
                Math.abs(rankToValue(hand.get(0).getRank()) - rankToValue(hand.get(1).getRank())) == 1;
    }

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

