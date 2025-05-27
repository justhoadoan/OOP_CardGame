package ai;

import cards.card.Card;
import java.util.*;

public class PokerHandEvaluator {

    public enum HandRank {
        HIGH_CARD, ONE_PAIR, TWO_PAIR, THREE_OF_A_KIND, STRAIGHT, FLUSH, FULL_HOUSE, FOUR_OF_A_KIND, STRAIGHT_FLUSH, ROYAL_FLUSH
    }

    public static HandRank evaluateHand(List<Card> hand) {
        if (hand == null || hand.isEmpty()) {
            System.out.println("Warning: Attempting to evaluate null or empty hand");
            return HandRank.HIGH_CARD; // Default to lowest rank
        }
        
        if (hand.size() < 5) {
            System.out.println("Warning: Hand has fewer than 5 cards, evaluating with available cards");
            // Implement simplified evaluation for hands with fewer than 5 cards
            if (hand.size() == 0) return HandRank.HIGH_CARD;
            
            // For hands with fewer cards, just check for pairs
            Map<String, Integer> rankCounts = countRanks(hand);
            if (hand.size() >= 2) {
                if (hasCount(rankCounts, 2)) return HandRank.ONE_PAIR;
                if (hand.size() >= 3 && hasCount(rankCounts, 3)) return HandRank.THREE_OF_A_KIND;
                if (hand.size() >= 4 && hasCount(rankCounts, 4)) return HandRank.FOUR_OF_A_KIND;
            }
            
            return HandRank.HIGH_CARD;
        }
        
        // Original implementation for 5+ cards
        hand.sort(Comparator.comparingInt(PokerHandEvaluator::rankToValue)); // Sort by rank

        boolean isFlush = isFlush(hand);
        boolean isStraight = isStraight(hand);
        Map<String, Integer> rankCounts = countRanks(hand);

        if (isFlush && isStraight && hand.get(0).getRank().equals("A")) {
            return HandRank.ROYAL_FLUSH;
        }
        if (isFlush && isStraight) return HandRank.STRAIGHT_FLUSH;
        if (hasCount(rankCounts, 4)) return HandRank.FOUR_OF_A_KIND;
        if (hasCount(rankCounts, 3) && hasCount(rankCounts, 2)) return HandRank.FULL_HOUSE;
        if (isFlush) return HandRank.FLUSH;
        if (isStraight) return HandRank.STRAIGHT;
        if (hasCount(rankCounts, 3)) return HandRank.THREE_OF_A_KIND;
        if (countPairs(rankCounts) == 2) return HandRank.TWO_PAIR;
        if (hasCount(rankCounts, 2)) return HandRank.ONE_PAIR;

        return HandRank.HIGH_CARD;
    }

    private static boolean isFlush(List<Card> hand) {
        String suit = hand.get(0).getSuit();
        return hand.stream().allMatch(card -> card.getSuit().equals(suit));
    }

    private static boolean isStraight(List<Card> hand) {
        for (int i = 1; i < hand.size(); i++) {
            if (rankToValue(hand.get(i)) != rankToValue(hand.get(i - 1)) + 1) {
                return false;
            }
        }
        return true;
    }

    private static Map<String, Integer> countRanks(List<Card> hand) {
        Map<String, Integer> rankCounts = new HashMap<>();
        for (Card card : hand) {
            rankCounts.put(card.getRank(), rankCounts.getOrDefault(card.getRank(), 0) + 1);
        }
        return rankCounts;
    }

    private static boolean hasCount(Map<String, Integer> rankCounts, int count) {
        return rankCounts.containsValue(count);
    }

    private static int countPairs(Map<String, Integer> rankCounts) {
        int pairs = 0;
        for (int value : rankCounts.values()) {
            if (value == 2) pairs++;
        }
        return pairs;
    }

    private static int rankToValue(Card card) {
        switch (card.getRank()) {
            case "Ace": return 14;
            case "King": return 13;
            case "Queen": return 12;
            case "Jack": return 11;
            default: return Integer.parseInt(card.getRank());
        }
    }

}
