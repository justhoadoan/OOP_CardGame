package test;

import card.CardSkin;
import gamemode.NonGraphicMode;
import games.GameType;
import games.PokerGame;
import input.PokerActionProcessor;
import playable.Player;
import playable.Playable;
import playable.AI;

import java.util.Scanner;
import java.util.Random;

public class TestNonGuiPoker {
    private final PokerGame game;
    private final Scanner scanner;
    private PokerActionProcessor processor;
    private final NonGraphicMode gameMode;
    private Random random;
    private boolean gameOver = false;

    public TestNonGuiPoker() {
        CardSkin skin = new CardSkin("Classic");
        gameMode = new NonGraphicMode(GameType.POKER);
        gameMode.setStateChangeCallback(this::displayState);
        game = new PokerGame(gameMode, null, skin);
        scanner = new Scanner(System.in);
        processor = new PokerActionProcessor();
        random = new Random();
        setupGame();
    }

    private void displayState(String state) {
        // Nếu trạng thái chứa thông tin người thắng, chỉ hiển thị thông tin đó
        if (state.contains("Winner:")) {
            slowPrint("\n=== GAME OVER ===");
            // Chỉ lấy dòng thông tin người thắng
            String[] lines = state.split("\n");
            for (String line : lines) {
                if (line.contains("Winner:")) {
                    slowPrint(line);
                    break;
                }
            }
            slowPrint("===============\n");
            
            gameOver = true;
            promptForNewGame();
            return;
        }
        
        // Nếu đây là thông tin bài của người chơi thật, hiển thị đầy đủ
        if (state.contains("Your Hand:")) {
            slowPrint("\n=== Game State ===");
            slowPrint(state);
            slowPrint("=================\n");
        }
        // Nếu không phải thông tin người thắng hay bài của người chơi thật, không hiển thị gì cả
    }

    // Phương thức hiển thị chậm, tăng thời gian delay
    private void slowPrint(String message) {
        System.out.println(message);
        try {
            // Tăng từ 800ms lên 1000ms để hiển thị chậm hơn
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void promptForNewGame() {
        slowPrint("\nGame over! Do you want to play another round? (yes/no)");
        String answer = scanner.nextLine().trim().toLowerCase();
        
        if (answer.equals("yes") || answer.equals("y")) {
            resetGame();
            gameOver = false;
            play();
        } else {
            slowPrint("Thanks for playing! Goodbye.");
            System.exit(0);
        }
    }
    
    private void resetGame() {
        // Reset lại trạng thái trò chơi
        slowPrint("\nStarting a new game...");
        
        // Reset người chơi
        for (Playable player : game.getPlayers()) {
            player.resetHand();
            player.setStatus(true);
            
            // Cấp lại tiền nếu cần
            if (player instanceof Player) {
                Player humanPlayer = (Player) player;
                if (humanPlayer.getCurrentBalance() < 200) {
                    humanPlayer.addCurrentBalance(1000 - humanPlayer.getCurrentBalance());
                    slowPrint(humanPlayer.getName() + " receives more chips. New balance: $" + humanPlayer.getCurrentBalance());
                }
            } else if (player instanceof AI) {
                AI aiPlayer = (AI) player;
                if (aiPlayer.getCurrentBalance() < 200) {
                    aiPlayer.addCurrentBalance(1000 - aiPlayer.getCurrentBalance());
                    slowPrint(aiPlayer.getName() + " receives more chips. New balance: $" + aiPlayer.getCurrentBalance());
                }
            }
        }
    }

    public void setupGame() {
        // Add human player
        Player humanPlayer = new Player("Human Player", 1);
        humanPlayer.addCurrentBalance(1000);
        game.addPlayer(humanPlayer);

        // Add AI players
        game.addAIPlayer("AI Player 1", "Monte Carlo");
        game.addAIPlayer("AI Player 2", "Rule Based");
        
        // Cài đặt theo dõi hành động
        setupActionTracking();
    }
    
    /**
     * Thiết lập theo dõi hành động của cả người chơi và AI
     */
    private void setupActionTracking() {
        // Tạo processor tùy chỉnh để xử lý và hiển thị hành động
        PokerActionProcessor customProcessor = new PokerActionProcessor() {
            @Override
            public void processAction(String action, games.Game game, server.Client client) {
                // Kiểm tra nếu trò chơi đã kết thúc thì không xử lý hành động nữa
                if (gameOver) {
                    return;
                }
                
                Playable currentPlayer = game.getCurrentPlayer();
                
                if (currentPlayer instanceof Player) {
                    // Xử lý hành động người chơi
                    displayPlayerAction(currentPlayer, action);
                    
                    // Gọi phương thức gốc để xử lý logic raise/fold
                    super.processAction(action, game, client);
                } 
                else if (currentPlayer instanceof AI) {
                    // Xử lý hành động AI
                    AI ai = (AI) currentPlayer;
                    
                    if (action.equals("raise")) {
                        // Lấy số tiền AI sẽ raise từ processor có sẵn
                        int amount = calculateAIRaiseAmount(ai);
                        
                        // Hiển thị hành động
                        displayAIAction(ai, action, amount);
                        
                        // Gọi phương thức playerRaise trực tiếp để đảm bảo số tiền chính xác
                        ((PokerGame) game).playerRaise(ai, amount);
                    } else {
                        // Hiển thị thông báo fold
                        displayAIAction(ai, action, 0);
                        
                        // Gọi phương thức fold trực tiếp thay vì qua super.processAction
                        ((PokerGame) game).playerFold(ai);
                    }
                }
            }
            
            // Tận dụng phương thức tính toán số tiền raise của AI từ processor gốc
            public int calculateAIRaiseAmount(AI ai) {
                return super.calculateAIRaiseAmount(ai);
            }
        };
        
        // Thay thế processor mặc định
        this.processor = customProcessor;
    }
    
    /**
     * Hiển thị thông báo về hành động của người chơi
     */
    private void displayPlayerAction(Playable player, String action) {
        slowPrint("\n=== Player Action ===");
        slowPrint(player.getName() + " decides to " + action.toUpperCase());
        slowPrint("=====================\n");
    }
    
    /**
     * Hiển thị thông báo về hành động của AI
     */
    private void displayAIAction(Playable player, String action, int amount) {
        try {
            Thread.sleep(1500); // Pause trước khi hiển thị hành động AI
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Chỉ hiển thị thông báo hành động của AI, không hiển thị game state
        System.out.println("\n\n");
        System.out.println("===================================================");
        System.out.println("                   AI ACTION                       ");
        System.out.println("===================================================");
        
        if (action.equals("raise")) {
            System.out.println("    " + player.getName() + " decides to RAISE: $" + amount);
        } else {
            System.out.println("    " + player.getName() + " decides to FOLD");
        }
        
        System.out.println("===================================================\n");
        
        try {
            Thread.sleep(1500); // Pause sau khi hiển thị hành động AI
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void playerAction() {
        // Kiểm tra nếu trò chơi đã kết thúc thì không yêu cầu hành động nữa
        if (gameOver) {
            return;
        }
        
        slowPrint("Enter your action (raise/fold): ");
        String action = scanner.nextLine().trim().toLowerCase();

        while (!action.equals("raise") && !action.equals("fold")) {
            slowPrint("Invalid action. Please enter 'raise' or 'fold': ");
            action = scanner.nextLine().trim().toLowerCase();
        }
        
        processor.processAction(action, game, null);
    }

    public void play() {
        try {
            // Reset trạng thái gameOver
            gameOver = false;
            
            if (game.getPlayers() != null && !game.getPlayers().isEmpty()) {
                slowPrint("Starting game with " + game.getPlayers().size() + " players:");
                for (Playable player : game.getPlayers()) {
                    slowPrint(" - " + player.getName() + " (ID: " + player.getId() + ")");
                }
                
                // Tạm dừng để người chơi đọc thông tin
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                // Bắt đầu trò chơi
                game.start();
            } else {
                slowPrint("Error: No players available to start the game.");
            }
        } catch (Exception e) {
            slowPrint("Exception during game play: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TestNonGuiPoker test = new TestNonGuiPoker();
        test.play();
    }
}