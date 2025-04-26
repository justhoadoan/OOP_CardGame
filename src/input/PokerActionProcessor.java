package input;


import games.Game;
import games.PokerGame;
import playable.Player;
import playable.AI;
import playable.Playable;
import server.Client;

import javax.swing.*;
import java.util.Scanner;
import java.util.Random;

public class PokerActionProcessor implements ActionProcessor {
    private Random random = new Random();

    @Override
    public void processAction(String action, Game game, Client client) {
        Playable currentPlayer = game.getCurrentPlayer();
        
        if (client != null) {
            String clientId = String.valueOf(client.getClientId());
            String message;
            switch (action.toLowerCase()) {
                case "raise":
                    String amountStr = promtForAmount(client, game, (Player) currentPlayer);
                    int amount = Integer.parseInt(amountStr);
                    if (client != null) {
                        client.sendMessage("ACTION:Raise:" + clientId + ":" + amount);
                    } else {
                        ((PokerGame) game).playerRaise((Player) currentPlayer, amount);
                    }
                    break;
                case "fold":
                    if (client != null) {
                        client.sendMessage("ACTION:Fold:" + clientId);
                    } else {
                        ((PokerGame) game).playerFold((Player) currentPlayer);
                    }
                    break;
            }
        } else {
            // Offline mode
            if (currentPlayer instanceof AI) {
                // Xử lý AI player
                AI ai = (AI) currentPlayer;
                
                if (action.equals("raise")) {
                    // Tính toán số tiền raise dựa trên chiến lược AI
                    int amount = calculateAIRaiseAmount(ai);
                    ((PokerGame) game).playerRaise(ai, amount);
                } else {
                    ((PokerGame) game).playerFold(ai);
                }
            } else if (currentPlayer instanceof Player) {
                // Xử lý human player
                Player player = (Player) currentPlayer;
                
                if (action.equals("raise")) {
                    String amountStr = promtForAmount(null, game, player);
                    try {
                        int amount = Integer.parseInt(amountStr);
                        ((PokerGame) game).playerRaise(player, amount);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid amount, using default of 10");
                        ((PokerGame) game).playerRaise(player, 10);
                    }
                } else {
                    ((PokerGame) game).playerFold(player);
                }
            }
        }
    }
    
    // Phương thức mới để tính toán số tiền raise của AI
    public int calculateAIRaiseAmount(AI ai) {
        int balance = ai.getCurrentBalance();
        
        if (ai.getStrategyType().equals("Monte Carlo")) {
            // Monte Carlo thường mạo hiểm hơn
            int minRaise = 30;
            int maxRaise = Math.min(100, balance / 3);
            return minRaise + random.nextInt(Math.max(maxRaise - minRaise + 1, 1));
        } else {
            // Rule Based cẩn thận hơn
            int minRaise = 10;
            int maxRaise = Math.min(40, balance / 5);
            return minRaise + random.nextInt(Math.max(maxRaise - minRaise + 1, 1));
        }
    }

    private String promtForAmount(Client client, Game game, Player player) {
        Scanner scanner = new Scanner(System.in);
        String amountStr = null;

        return amountStr;
    }
    private static boolean isValidAmount(String amountStr, Player player) {
        if (amountStr == null || amountStr.isEmpty()) {
            return false;
        }
        try {
            int amount = Integer.parseInt(amountStr);
            return amount > 0 && amount <= player.getCurrentBalance();
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public String getPlayerAction() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your action (raise/fold): ");
        String action = scanner.nextLine().trim().toLowerCase();

        while (!action.equals("raise") && !action.equals("fold")) {
            System.out.println("Invalid action. Please enter 'raise' or 'fold': ");
            action = scanner.nextLine().trim().toLowerCase();
        }
        return action;
    }
}