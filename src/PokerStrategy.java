import java.util.Random;

public class PokerStrategy {
    private static final Random random = new Random();

    public static void main(String[] args) {
        String position = "button"; // Can be "button" or "bigBlind"
        String hand = "A9"; // Example hand (you can modify this for testing)

        System.out.println("Preflop Decision: " + preflopDecision(position, hand));

        boolean continuationBet = shouldContinuationBet(true, "K72"); // Example flop
        System.out.println("Continuation Bet: " + (continuationBet ? "Yes" : "No"));
    }

    public static String preflopDecision(String position, String hand) {
        // Optimized hand selection based on strong heads-up strategy
        String[] strongHands = {"AA", "KK", "QQ", "JJ", "AK", "AQ", "TT", "99"};
        String[] playableButtonHands = {"A2", "A3", "A4", "A5", "KT", "QT", "JT", "T9", "98", "77", "66"};
        String[] playableBigBlindHands = {"A2", "A3", "A4", "A5", "KQ", "KJ", "QJ", "JT", "T9", "88", "77"};

        if (isInArray(hand, strongHands)) {
            return "Raise (Strong Hand)";
        }

        if (position.equals("button")) {
            if (isInArray(hand, playableButtonHands)) {
                return "Raise (Button Play)";
            }
        } else if (position.equals("bigBlind")) {
            if (isInArray(hand, playableBigBlindHands)) {
                return "Call or Raise (Defending Big Blind)";
            }
        }

        return "Fold (Weak Hand)";
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



}
