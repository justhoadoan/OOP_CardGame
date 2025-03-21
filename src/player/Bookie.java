package player;

import games.Game;
import playable.Playable;

import java.util.HashMap;
import java.util.Map;

public class Bookie {
    private Map<Playable, Integer> initialBets;
    private int totalPot;
    private Game game;

    public Bookie(Game game) {
        this.game = game;
        this.initialBets = new HashMap<>();
        this.totalPot = 0;
    }

    public void placeInitialBet(Playable player, int amount) {
        initialBets.put(player, amount);
        totalPot += amount;
    }

    public void collectBets() {
        for (Map.Entry<Playable, Integer> entry : initialBets.entrySet()) {
            // Logic to collect bets from players


        }
    }

    public void distributeWinnings(Playable winner) {
        int winnings = totalPot;
        totalPot = 0;
        // Logic to distribute winnings to the winner


    }

    public int getPlayerBet(Playable player) {
        return initialBets.getOrDefault(player, 0);
    }

    public int getTotalPot() {
        return totalPot;
    }

    public void reset() {
        initialBets.clear();
        totalPot = 0;
    }

    // Placeholder for broadcastBetUpdate method
    public void broadcastBetUpdate() {
        // Implementation to be added later
    }
}