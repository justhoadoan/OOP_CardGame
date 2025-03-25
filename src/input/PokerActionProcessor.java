package input;

import gamemode.GraphicMode;
import games.Game;
import games.PokerGame;
import playable.Player;
import server.Client;

import javax.swing.*;
import java.util.Scanner;

public class PokerActionProcessor implements ActionProcessor {

    @Override
    public void processAction(String action, Game game, Client client) {
        Player player = (Player) game.getCurrentPlayer();
        if (client != null) {
            //offline mode
            String clientId = String.valueOf(client.getClientId());
            String message;
            switch (action.toLowerCase()) {
                case "raise":
                    String amountStr = promtForAmount(client, game, player);
                    int amount = Integer.parseInt(amountStr);
                    if (client != null) {
                        client.sendMessage("ACTION:Raise:" + clientId + ":" + amount);
                    } else {
                        ((PokerGame) game).playerRaise(player, amount);
                    }
                    break;
                case "fold":
                    if (client != null) {
                        client.sendMessage("ACTION:Fold:" + clientId);
                    } else {
                        ((PokerGame) game).playerFold(player);
                    }
                    break;
            }
        }
    }



    private String promtForAmount(Client client, Game game, Player player) {
        Scanner scanner = new Scanner(System.in);
        String amountStr;
        boolean isGraphicMode = game.getGameMode() instanceof GraphicMode;
        if (isGraphicMode) {
            amountStr = JOptionPane.showInputDialog("Enter amount to raise");
        }
        else {
            System.out.println("Enter amount to raise");
            amountStr = scanner.nextLine().trim();
        }
        while (!isValidAmount(amountStr, player)){
            if (isGraphicMode) {
                amountStr = JOptionPane.showInputDialog("Invalid amount. Enter amount to raise");
            }
            else {
                System.out.println("Invalid amount. Enter amount to raise");
                amountStr = scanner.nextLine().trim();
            }
        }
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
}