package test;

import gamemode.NonGraphicMode;
import games.GameType;
import games.PokerGame;
import server.Server;

import java.util.Scanner;

public class TestOnlinePokerHost {
    private final PokerGame game;
    private final NonGraphicMode gameMode;
    private final Server server;
    private final Scanner scanner;
    private boolean gameOver = false;

    public TestOnlinePokerHost() {
        // Khởi tạo server trước
        server = new Server(5000, null);
        
        // Khởi tạo game mode và game
        gameMode = new NonGraphicMode(GameType.POKER);
        gameMode.setStateChangeCallback(this::displayState);
        game = new PokerGame(gameMode, server, null); // Không cần CardSkin trong NonGraphicMode
        
        // Cập nhật game cho server
        server.setGame(game);
        
        scanner = new Scanner(System.in);
    }

    private void displayState(String state) {
        System.out.println("\n=== Game State ===");
        System.out.println(state);
        System.out.println("=================\n");
    }

    public void start() {
        try {
            // Bắt đầu server
            server.start();
            System.out.println("Waiting for players to connect...");
            System.out.println("Enter 'start' when all players are connected to begin the game.");

            String command;
            do {
                command = scanner.nextLine().trim().toLowerCase();
                if (!command.equals("start")) {
                    System.out.println("Invalid command. Please enter 'start' to begin the game.");
                }
            } while (!command.equals("start"));

            // Bắt đầu game
            if (game.getPlayers().size() >= 2) {
                System.out.println("Starting game with " + game.getPlayers().size() + " players:");
                for (var player : game.getPlayers()) {
                    System.out.println(" - " + player.getName() + " (ID: " + player.getId() + ")");
                }
                game.start();
            } else {
                System.out.println("Not enough players to start the game. Need at least 2 players.");
            }
        } catch (Exception e) {
            System.err.println("Error during game: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Đóng server khi kết thúc
            server.close();
        }
    }

    public static void main(String[] args) {
        TestOnlinePokerHost host = new TestOnlinePokerHost();
        host.start();
    }
} 