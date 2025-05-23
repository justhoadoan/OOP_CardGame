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
    public void processAction(String action, Game game) {
        Playable currentPlayer = game.getCurrentPlayer();
            if (currentPlayer instanceof AI) {

                AI ai = (AI) currentPlayer;
                
                if (action.equals("raise")) {

                    int amount = calculateAIRaiseAmount(ai);
                    ((PokerGame) game).playerRaise(ai, amount);
                } else {
                    ((PokerGame) game).playerFold(ai);
                }
            } else if (currentPlayer instanceof Player) {
                if (action.equals("raise")) {
                    ((PokerGame) game).playerRaise(currentPlayer, Integer.parseInt(action.split(":")[1]));
                } else if (action.equals("fold")) {
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