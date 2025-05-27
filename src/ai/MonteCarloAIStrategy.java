package ai;

import cards.card.Card;
import java.util.*;

public class MonteCarloAIStrategy implements AIStrategy {
    private static final int SIMULATIONS = 10000;
    public String getAction(GameState state) {
        try {
            List<Card> hand = state.getPlayerHand();
            List<Card> communityCards = ((PokerState) state).getCommunityCards();
            int pot = state.getPot();
            int currentBet = state.getCurrentBet();
            double winRate = monteCarloSimulation(hand, communityCards);

            // Debug logs
            System.out.println("AI Hand: " + hand);
            System.out.println("Community Cards: " + communityCards);
            System.out.println("Pot: " + pot + ", Current Bet: " + currentBet);
            System.out.println("Win Rate: " + winRate);

            winRate = winRate * 3;
            if (winRate > 0.5) {
                return "RAISE";
            } else {
                return "FOLD";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "FOLD";
        }
    }

    private double monteCarloSimulation(List<Card> hand, List<Card> communityCards) {
        int wins = 0;
        int losses = 0;
        int draws = 0;

        List<Card> deck = generateDeck(hand, communityCards);
        for (int i = 0; i < SIMULATIONS; i++) {
            Collections.shuffle(deck);
            List<Card> opponentHand = deck.subList(0, 2);
            List<Card> fullBoard = new ArrayList<>(communityCards);
            // Complete the board
            for (int j = communityCards.size(); j < 5; j++) {
                fullBoard.add(deck.get(2 + j - communityCards.size()));
            }
            // Evaluate hands
            PokerHandEvaluator.HandRank myRank = PokerHandEvaluator.evaluateHand(mergeHands(hand, fullBoard));
            PokerHandEvaluator.HandRank opponentRank = PokerHandEvaluator.evaluateHand(mergeHands(opponentHand, fullBoard));
            if (myRank.ordinal() > opponentRank.ordinal()) {
                wins++;
            } else if (myRank.ordinal() < opponentRank.ordinal()) {
                losses++;
            }
            else {
                draws++;
            }
        }
        return (double)(wins + 0.5 * draws) / (wins + losses + draws);
    }

    private List<Card> generateDeck(List<Card> hand, List<Card> communityCards) {
        List<Card> deck = new ArrayList<>();
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};

        for (String suit : suits) {
            for (String rank : ranks) {
                Card card = new Card(rank, suit);
                if (!hand.contains(card) && !communityCards.contains(card)) {
                    deck.add(card);
                }
            }
        }
        return deck;
    }

    private List<Card> mergeHands(List<Card> hand, List<Card> communityCards) {
        List<Card> combined = new ArrayList<>(hand);
        combined.addAll(communityCards);
        return combined;
    }
}
