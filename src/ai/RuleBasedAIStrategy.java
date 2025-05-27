package ai;

import cards.card.Card;
import playable.Player;
import java.util.*;
import java.util.stream.Collectors;

public class RuleBasedAIStrategy implements AIStrategy {
    private Random random = new Random();

    @Override
    public String getAction(GameState state) {
        try {
            // Safety checks for null or empty collections
            List<Card> playerHand = state.getPlayerHand();
            List<Card> communityCards = ((PokerState) state).getCommunityCards();
            if (playerHand == null) playerHand = new ArrayList<>();
            if (communityCards == null) communityCards = new ArrayList<>();

            int potValue = 100;
            int currentBet = state.getCurrentBet();
            Player player = state.getplayer();

            // Evaluate hand strength (0-10 scale: 0 = terrible, 10 = excellent)
            int handStrength = evaluateHandStrength(playerHand, communityCards);

            // Decision making based on hand strength and community cards
            if (communityCards.size() == 0) {
                // Pre-flop strategy
                return preFlopStrategy(handStrength, currentBet);
            } else {
                // Post-flop strategy
                return postFlopStrategy(handStrength, potValue, currentBet);
            }
        } catch (Exception e) {
            System.out.println("Error in AI strategy: " + e.getMessage());
            e.printStackTrace();
            // Default to raising in case of errors (more interesting gameplay)
            return "raise";
        }
    }

    private String preFlopStrategy(int handStrength, int currentBet) {
        // Pre-flop strategy based on starting hand strength
        if (handStrength >= 8) {
            // Strong starting hand - raise aggressively
            return "raise";
        } else if (handStrength >= 5) {
            // Decent starting hand - raise with 70% probability
            return (random.nextInt(100) < 70) ? "raise" : "fold";
        } else if (handStrength >= 3) {
            // Weak but playable hand - raise with 40% probability
            return (random.nextInt(100) < 40) ? "raise" : "fold";
        } else {
            // Very weak hand - fold with 80% probability
            return (random.nextInt(100) < 80) ? "fold" : "raise";
        }
    }

    private String postFlopStrategy(int handStrength, int potValue, int currentBet) {
        // Post-flop strategy based on hand strength and pot size
        if (handStrength >= 7) {
            // Strong hand - raise aggressively
            return "raise";
        } else if (handStrength >= 5) {
            // Good hand - raise with 80% probability
            return (random.nextInt(100) < 80) ? "raise" : "fold";
        } else if (handStrength >= 3) {
            // Mediocre hand - raise with 50% probability
            return (random.nextInt(100) < 50) ? "raise" : "fold";
        } else {
            // Weak hand - fold with 70% probability
            return (random.nextInt(100) < 70) ? "fold" : "raise";
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

        // Evaluate based on hand potential
        int strength = 0;

        // Check for premium pairs in hand
        if (isPremiumPair(hand)) strength += 6;
            // Check for any pair in hand
        else if (isPair(hand)) strength += 4;

        // Check for flush potential
        if (hasFlushDraw(allCards)) strength += 2;
        // Check for straight potential
        if (hasStraightDraw(allCards)) strength += 2;

        // Check for made hands with all cards
        if (hasPair(allCards)) strength += 1;
        if (hasTwoPair(allCards)) strength += 3;
        if (hasThreeOfAKind(allCards)) strength += 4;
        if (hasStraight(allCards)) strength += 5;
        if (hasFlush(allCards)) strength += 6;
        if (hasFullHouse(allCards)) strength += 7;
        if (hasFourOfAKind(allCards)) strength += 8;
        if (hasStraightFlush(allCards)) strength += 9;

        // Check for high cards (aces or kings)
        if (hasHighCards(hand)) strength += 1;

        // Cap the strength at 10
        return Math.min(strength, 10);
    }

    // Helper methods for hand evaluation

    private boolean isPremiumPair(List<Card> hand) {
        if (hand.size() < 2) return false;

        // Check for pairs of face cards or aces
        String rank1 = hand.get(0).getRank();
        String rank2 = hand.get(1).getRank();

        return rank1.equals(rank2) &&
                (rank1.equals("A") || rank1.equals("K") ||
                        rank1.equals("Q") || rank1.equals("J"));
    }

    private boolean isPair(List<Card> cards) {
        if (cards == null || cards.size() < 2) return false;

        Map<String, Integer> rankCount = new HashMap<>();
        for (Card card : cards) {
            String rank = card.getRank();
            rankCount.put(rank, rankCount.getOrDefault(rank, 0) + 1);
        }

        return rankCount.values().stream().anyMatch(count -> count == 2);
    }

    private boolean hasPair(List<Card> cards) {
        return isPair(cards);
    }

    private boolean hasTwoPair(List<Card> cards) {
        if (cards == null || cards.size() < 4) return false;

        Map<String, Integer> rankCount = new HashMap<>();
        for (Card card : cards) {
            String rank = card.getRank();
            rankCount.put(rank, rankCount.getOrDefault(rank, 0) + 1);
        }

        int pairCount = 0;
        for (Integer count : rankCount.values()) {
            if (count == 2) pairCount++;
        }

        return pairCount >= 2;
    }

    private boolean hasThreeOfAKind(List<Card> cards) {
        if (cards == null || cards.size() < 3) return false;

        Map<String, Integer> rankCount = new HashMap<>();
        for (Card card : cards) {
            String rank = card.getRank();
            rankCount.put(rank, rankCount.getOrDefault(rank, 0) + 1);
        }

        return rankCount.values().stream().anyMatch(count -> count >= 3);
    }

    private boolean hasStraight(List<Card> cards) {
        if (cards == null || cards.size() < 5) return false;

        List<Integer> values = cards.stream()
                .map(this::getCardValue)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        for (int i = 0; i <= values.size() - 5; i++) {
            if (values.get(i+4) - values.get(i) == 4) return true;
        }

        // Check for A-5 straight
        if (values.contains(14)) { // If we have an ace
            values.add(1);  // Add value 1 for low ace
            values = values.stream().distinct().sorted().collect(Collectors.toList());

            for (int i = 0; i <= values.size() - 5; i++) {
                if (values.get(i+4) - values.get(i) == 4) return true;
            }
        }

        return false;
    }

    private boolean hasFlush(List<Card> cards) {
        if (cards == null || cards.size() < 5) return false;

        Map<String, Integer> suitCount = new HashMap<>();
        for (Card card : cards) {
            suitCount.put(card.getSuit(), suitCount.getOrDefault(card.getSuit(), 0) + 1);
        }

        return suitCount.values().stream().anyMatch(count -> count >= 5);
    }

    private boolean hasFullHouse(List<Card> cards) {
        if (cards == null || cards.size() < 5) return false;

        Map<String, Integer> rankCount = new HashMap<>();
        for (Card card : cards) {
            String rank = card.getRank();
            rankCount.put(rank, rankCount.getOrDefault(rank, 0) + 1);
        }

        boolean hasThree = rankCount.values().stream().anyMatch(count -> count >= 3);
        boolean hasPair = rankCount.values().stream().anyMatch(count -> count == 2);

        // Check if we have both a three of a kind and a pair
        return hasThree && hasPair;
    }

    private boolean hasFourOfAKind(List<Card> cards) {
        if (cards == null || cards.size() < 4) return false;

        Map<String, Integer> rankCount = new HashMap<>();
        for (Card card : cards) {
            String rank = card.getRank();
            rankCount.put(rank, rankCount.getOrDefault(rank, 0) + 1);
        }

        return rankCount.values().stream().anyMatch(count -> count >= 4);
    }

    private boolean hasStraightFlush(List<Card> cards) {
        // Need at least 5 cards for a straight flush
        if (cards == null || cards.size() < 5) return false;

        // Group cards by suit
        Map<String, List<Card>> cardsBySuit = new HashMap<>();
        for (Card card : cards) {
            String suit = card.getSuit();
            if (!cardsBySuit.containsKey(suit)) {
                cardsBySuit.put(suit, new ArrayList<>());
            }
            cardsBySuit.get(suit).add(card);
        }

        // Check each suit group that has at least 5 cards for a straight
        for (List<Card> suitCards : cardsBySuit.values()) {
            if (suitCards.size() >= 5 && hasStraight(suitCards)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasFlushDraw(List<Card> cards) {
        if (cards == null || cards.size() < 4) return false;

        Map<String, Integer> suitCount = new HashMap<>();
        for (Card card : cards) {
            suitCount.put(card.getSuit(), suitCount.getOrDefault(card.getSuit(), 0) + 1);
        }

        return suitCount.values().stream().anyMatch(count -> count == 4);
    }

    private boolean hasStraightDraw(List<Card> cards) {
        if (cards == null || cards.size() < 4) return false;

        List<Integer> values = cards.stream()
                .map(this::getCardValue)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // Check for open-ended straight draw (4 consecutive cards)
        for (int i = 0; i <= values.size() - 4; i++) {
            if (values.get(i+3) - values.get(i) == 3) return true;
        }

        // Check for gutshot straight draw (3 cards with 1 gap)
        for (int i = 0; i <= values.size() - 4; i++) {
            if (values.get(i+3) - values.get(i) == 4) return true;
        }

        return false;
    }

    private boolean hasHighCards(List<Card> cards) {
        if (cards == null || cards.isEmpty()) return false;

        for (Card card : cards) {
            String rank = card.getRank();
            if (rank.equals("A") || rank.equals("K")) {
                return true;
            }
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
                    return 0; // Default value for non-numeric or non-face cards
                }
        }
    }
}