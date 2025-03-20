package server.networkinput;
import server.Client;
import server.Client;

import java.util.Scanner;

public class BlackjackClientInputHandler implements ClientInputHandler {
    private Scanner scanner;

    public BlackjackClientInputHandler() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void handleInput(Client client, String gameState, String playerHand) {

        System.out.println("\n=== Client-" + client.getClientId() + "'s Turn (Blackjack) ===");
        System.out.println("Your Hand: " + playerHand);
        System.out.println("Game State: " + gameState);

        System.out.println("Choose action: 1. Hit  2. Stand  ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1: // Hit
                client.sendMessage("Hit:" + client.getClientId());
                break;
            case 2: // Stand
                client.sendMessage("Stand:" + client.getClientId());
                break;
            default:
                System.out.println("Invalid action. Try again.");
                handleInput(client, gameState, playerHand);
        }
    }
}