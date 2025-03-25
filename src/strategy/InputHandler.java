package strategy;

import gameaction.GameAction;
import gamemode.GameMode;
import gamemode.NonGraphicMode;
import games.Game;
import playable.Player;
import server.Client;

import java.util.List;
import java.util.Scanner;

public class InputHandler {

    private Scanner scanner;

    public InputHandler() {
        this.scanner = new Scanner(System.in);
    }

    // Get input from player
    public GameAction getPlayerAction(Player player, Game game, List<GameAction> availableActions) {
        Client client = player.getClient();
        GameMode gameMode = game.getGameMode();

        if (client == null) {
            // Offline
            if (gameMode instanceof NonGraphicMode) {
                // Offline, non-graphic
                System.out.println("Your Hand: " + player.getHand());

                System.out.println("Available actions:");
                for (int i = 0; i < availableActions.size(); i++) {
                    System.out.println((i + 1) + ". " + availableActions.get(i).getActionName());
                }
                System.out.println("Choose action (1-" + availableActions.size() + "):");
                int choice = scanner.nextInt();

                if (choice < 1 || choice > availableActions.size()) {
                    System.out.println("Invalid action. Try again.");
                    return getPlayerAction(player, game, availableActions);
                }

                GameAction selectedAction = availableActions.get(choice - 1);
                if (selectedAction.requiresParameter()) {
                    System.out.println("Enter " + selectedAction.getParameterName() + ":");
                    int paramValue = scanner.nextInt();
                    selectedAction.setParameter(paramValue);
                }
                return selectedAction;
            } else {
                // Offline, graphic
                System.out.println(player.getName() + "'s turn. Waiting for action via GUI...");
                return null; // GUI (PokerView) wil handle this
            }
        } else {
            // Online
            if (gameMode instanceof NonGraphicMode) {
                // Online, non-graphic
                System.out.println(player.getName() + "'s turn. Waiting for action from client...");
            } else {
                // Online, graphic
                System.out.println(player.getName() + "'s turn. Waiting for action via GUI on client...");
            }
            return null; // Client will send action to server
        }
    }
}