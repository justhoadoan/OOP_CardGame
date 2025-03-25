package server.networkinput;

import card.Card;
import server.Client;

import java.util.List;
import java.util.Scanner;

public class PokerClientInputHandler implements ClientInputHandler {
    private  Scanner scanner;
//
    public PokerClientInputHandler() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void handleInput(Client client, String gameState, List<Card> playerHand) {

        System.out.println("\n=== Client-" + client.getClientId() + "'s Turn (Poker) ===");
        System.out.println("Your Hand: " + playerHand);
        System.out.println("Game State: " + gameState);


        System.out.println("Choose action: 1. Raise  2. Fold ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1: // Raise
                System.out.println("Enter raise amount: ");
                int raiseAmount = scanner.nextInt();
                client.sendMessage("Raise:" + client.getClientId() + ":" + raiseAmount);
                break;
            case 2: // Fold
                client.sendMessage("Fold:" + client.getClientId());
                break;
            default:
                System.out.println("Invalid action. Try again.");
                handleInput(client, gameState, playerHand);
        }
    }
}