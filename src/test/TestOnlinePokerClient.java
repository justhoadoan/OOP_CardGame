package test;

import gamemode.NonGraphicMode;
import games.GameType;
import games.PokerGame;
import input.PokerActionProcessor;
import playable.Player;
import server.Client;

import java.util.Scanner;

public class TestOnlinePokerClient {
    private final PokerGame game;
    private final Scanner scanner;
    private final Client client;
    private final PokerActionProcessor processor;
    private boolean gameOver = false;

    public TestOnlinePokerClient(String serverIp, int port) {
        // Khởi tạo game mode và game
        NonGraphicMode gameMode = new NonGraphicMode(GameType.POKER);
        gameMode.setStateChangeCallback(this::displayState);
        
        // Khởi tạo client và game
        client = new Client(serverIp, port, gameMode, GameType.POKER, null);
        game = new PokerGame(gameMode, client, null);
        
        scanner = new Scanner(System.in);
        processor = new PokerActionProcessor();
        setupGame();
    }

    private void displayState(String state) {
        System.out.println("\n=== Game State ===");
        System.out.println(state);
        System.out.println("=================\n");
    }

    private void setupGame() {
        try {
            // Kết nối tới server
            System.out.println("Connecting to server...");
            client.start();
            
            // Tạo player cho client này sau khi nhận được ID từ server
            Player player = new Player("Player-" + client.getClientId(), Integer.parseInt(client.getClientId()));
            player.addCurrentBalance(1000);
            game.addPlayer(player);
            
        } catch (Exception e) {
            System.err.println("Error setting up game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void play() {
        try {
            System.out.println("Connected to online poker game!");
            System.out.println("You are: " + game.getPlayers().get(0).getName());
            
            // Handle player turns
            while (!gameOver) {
                Player currentPlayer = (Player) game.getCurrentPlayer();
                
                // Only process if it's this player's turn
                if (currentPlayer != null && currentPlayer.getId() == Integer.parseInt(client.getClientId())) {
                    System.out.println("\nYour turn!");
                    
                    // Get player action
                    System.out.println("Enter action (raise/fold): ");
                    String action = scanner.nextLine().trim().toLowerCase();
                    
                    while (!action.equals("raise") && !action.equals("fold")) {
                        System.out.println("Invalid action. Please enter 'raise' or 'fold': ");
                        action = scanner.nextLine().trim().toLowerCase();
                    }
                    
                    if (action.equals("raise")) {
                        System.out.println("Enter raise amount: ");
                        try {
                            int amount = Integer.parseInt(scanner.nextLine().trim());
                            client.sendMessage("ACTION:Raise:" + client.getClientId() + ":" + amount);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid amount. Using default of 10");
                            client.sendMessage("ACTION:Raise:" + client.getClientId() + ":10");
                        }
                    } else {
                        client.sendMessage("ACTION:Fold:" + client.getClientId());
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
                client.close();
            }
            
        } catch (Exception e) {
            System.err.println("Error during game play: " + e.getMessage());
            e.printStackTrace();
            client.close();
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
        
        // Get server IP
        System.out.println("Enter server IP address (default: localhost):");
        String serverIp = scanner.nextLine().trim();
        if (serverIp.isEmpty()) {
            serverIp = "localhost";
        }
        
        // Get port number with proper validation
        int port = 5000; // default port
        System.out.println("Enter server port (default: 5000):");
        String portStr = scanner.nextLine().trim();
        
        if (!portStr.isEmpty()) {
            try {
                port = Integer.parseInt(portStr);
                if (port <= 0 || port > 65535) {
                    System.out.println("Invalid port number. Using default port 5000");
                    port = 5000;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid port format. Using default port 5000");
                port = 5000;
            }
        }
        
        TestOnlinePokerClient client = new TestOnlinePokerClient(serverIp, port);
        client.play();
    }
} 