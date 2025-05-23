package input;


import games.Game;
import games.PokerGame;
import playable.Player;
import playable.AI;
import playable.Playable;


import javax.swing.*;
import java.util.Scanner;
import java.util.Random;

public class PokerActionProcessor implements ActionProcessor {
    private Random random = new Random();
    @Override
    public void processAction(String action, Game game, Playable currentPlayer) {
        action=action.toLowerCase();
        System.out.println("action: "+action);
        System.out.println("currentPlayer: "+currentPlayer.getName());
            if (currentPlayer instanceof AI) {
                AI ai = (AI) currentPlayer;
                if (action.equals("raise")) {
                    System.out.println("ai rasing");
                    int amount = calculateAIRaiseAmount(ai);
                    ((PokerGame) game).playerRaise(ai, amount);
                } else {
                    System.out.println("ai folding");
                    ((PokerGame) game).playerFold(ai);
                }
            } else if (currentPlayer instanceof Player) {
                System.out.println(action);
                if (action.split(":")[0].equals("raise")) {
                    System.out.println("rasing");
                    ((PokerGame) game).playerRaise(currentPlayer, Integer.parseInt(action.split(":")[1]));
                } else if (action.equals("fold")) {
                    System.out.println("folding");
                    ((PokerGame) game).playerFold((Player) currentPlayer);
                }

            }

    }

    public int calculateAIRaiseAmount(AI ai) {
        int balance = ai.getCurrentBalance();
        
        if (ai.getStrategyType().equals("Monte Carlo")) {

            int minRaise = 30;
            int maxRaise = Math.min(100, balance / 3);
            return minRaise + random.nextInt(Math.max(maxRaise - minRaise + 1, 1));
        } else if (ai.getStrategyType().equals("Rule based")) {
            int minRaise = 10;
            int maxRaise = Math.min(40, balance / 5);
            return minRaise + random.nextInt(Math.max(maxRaise - minRaise + 1, 1));
        }
        return 0; // Default case, should not happen
    }


}