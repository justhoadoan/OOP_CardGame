package test;

import card.CardSkin;
import games.GameType;
import games.PokerGame;
import input.PokerActionProcessor;
import playable.Player;
import server.Client;
import server.NetworkManager;

import java.util.Scanner;

public class TestOnlinePokerClient {
    private final PokerGame game;
    private final Scanner scanner;
    private final Client client;
    private final PokerActionProcessor processor;
    private boolean gameOver = false;

    public TestOnlinePokerClient(String host, int port) {
        CardSkin skin = new CardSkin("Classic");
        client = new Client(host, port, null, GameType.POKER, skin); // Connect to the server
        game = new PokerGame(null, client, skin);
        scanner = new Scanner(System.in);
        processor = new PokerActionProcessor();
        setupGame();
    }

    private void setupGame() {
        // Create a player for this client
        Player player = new Player("Player " + client.getClientId(), Integer.parseInt(client.getClientId()));
        player.addCurrentBalance(1000);
        game.addPlayer(player);
    }

    public void play() {
        try {
            System.out.println("Connecting to online poker game...");
            System.out.println("You are: " + game.getPlayers().get(0).getName());
            
            // Start the game
            game.start();
            
            // Handle player turns
            while (!gameOver) {
                Player currentPlayer = (Player) game.getCurrentPlayer();
                
                // Only process if it's this player's turn
                if (currentPlayer.getId() == Integer.parseInt(client.getClientId())) {
                    System.out.println("\nYour turn!");
                    
                    // Get player action
                    System.out.println("Enter action (raise/fold): ");
                    String action = scanner.nextLine().trim().toLowerCase();
                    
                    if (action.equals("raise")) {
                        System.out.println("Enter raise amount: ");
                        int amount = Integer.parseInt(scanner.nextLine().trim());
                        game.playerRaise(currentPlayer, amount);
                    } else if (action.equals("fold")) {
                        game.playerFold(currentPlayer);
                    }
                }
                
                // Check if game is over
                if (game.isGameOver()) {
                    gameOver = true;
                    System.out.println("\nGame Over!");
                    System.out.println("Winner: " + game.getWinner());
                    break;
                }
                
                // Small delay to prevent busy waiting
                Thread.sleep(100);
            }
            
            // Ask if player wants to play again
            System.out.println("\nDo you want to play another round? (yes/no)");
            String answer = scanner.nextLine().trim().toLowerCase();
            
            if (answer.equals("yes") || answer.equals("y")) {
                resetGame();
                play();
            } else {
                System.out.println("Thanks for playing! Goodbye.");
                client.close(); // Close the client connection
            }
            
        } catch (Exception e) {
            System.out.println("Error during game play: " + e.getMessage());
            e.printStackTrace();
            client.close(); // Make sure to close client on error
        }
    }

    private void resetGame() {
        System.out.println("\nStarting a new game...");
        
        // Reset player
        Player player = (Player) game.getPlayers().get(0);
        player.resetHand();
        player.setStatus(true);
        
        // Add more chips if needed
        if (player.getCurrentBalance() < 200) {
            player.addCurrentBalance(1000 - player.getCurrentBalance());
            System.out.println(player.getName() + " receives more chips. New balance: $" + player.getCurrentBalance());
        }
        
        gameOver = false;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Get host IP
        System.out.println("Enter server IP address (default: localhost):");
        String host = scanner.nextLine().trim();
        if (host.isEmpty()) {
            host = "localhost";
        }
        
        // Get port number
        int port = 5000; // default port
        while (true) {
            try {
                System.out.println("Enter server port (default: 5000):");
                String portStr = scanner.nextLine().trim();
                if (portStr.isEmpty()) {
                    break;
                }
                port = Integer.parseInt(portStr);
                if (port > 0 && port < 65536) {
                    break;
                }
                System.out.println("Port must be between 1 and 65535");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid port number");
            }
        }
        
        System.out.println("\nConnecting to server at " + host + ":" + port);
        TestOnlinePokerClient client = new TestOnlinePokerClient(host, port);
        client.play();
    }
} 