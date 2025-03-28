package gamemode;

import card.Card;
import card.CardSkin;
import games.GameType;
import games.PokerGame;
import input.InputHandler;
import server.Client;

import java.util.List;
import java.util.function.Consumer;

public class NonGraphicMode implements GameMode {
    private GameType gameType;
    private Client client;
    private CardSkin cardSkin;
    private Consumer<String> stateChangeCallback;

    public NonGraphicMode(GameType gameType) {
        this.gameType = gameType;
    }

    public void setStateChangeCallback(Consumer<String> callback) {
        this.stateChangeCallback = callback;
    }

    @Override
    public void setGame(PokerGame game) {
        // Not needed for non-graphic mode
    }

    @Override
    public void setCardSkin(CardSkin skin) {
        this.cardSkin = skin;
    }

    @Override
    public void updateDisplay(List<Card> playerHand, String publicState, String winner) {
        // Nếu là người thắng, chỉ hiển thị kết quả
        if (winner != null) {
            StringBuilder display = new StringBuilder();
            display.append("\n======== GAME RESULT ========\n");
            display.append("Winner: ").append(winner);
            display.append("\n============================\n");
            
            if (stateChangeCallback != null) {
                stateChangeCallback.accept(display.toString());
            }
            return;
        }
        
        // Nếu là AI (không có playerHand), không hiển thị gì cả
        if (playerHand == null || playerHand.isEmpty()) {
            return;
        }
        
        // Chỉ hiển thị thông tin đầy đủ cho lượt của người chơi thật
        StringBuilder display = new StringBuilder();
        display.append("Your Hand: ")
                .append(playerHand.stream()
                        .map(card -> card.getRank() + " of " + card.getSuit())
                        .collect(java.util.stream.Collectors.joining(", ")))
                .append("\n");
        
        if (publicState != null) {
            display.append(publicState);
        }

        if (stateChangeCallback != null) {
            stateChangeCallback.accept(display.toString());
        }
    }

    @Override
    public InputHandler getInputHandler() {
        return null; // Not needed for non-graphic mode
    }




    // Thêm phương thức để hiển thị hành động của AI

    public void displayAIAction(String aiName, String action, int amount) {
        StringBuilder message = new StringBuilder();
        message.append("\n=== AI Action ===\n");
        message.append(aiName).append(" decides to ");
        
        if (action.equalsIgnoreCase("raise")) {
            message.append("RAISE $").append(amount);
        } else if (action.equalsIgnoreCase("fold")) {
            message.append("FOLD");
        }
        
        message.append("\n=================\n");
        
        if (stateChangeCallback != null) {
            // Make display slower by breaking this into multiple callbacks
            stateChangeCallback.accept("\n=== AI Action ===");
            
            try {
                Thread.sleep(1000); // Delay between messages
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            if (action.equalsIgnoreCase("raise")) {
                stateChangeCallback.accept(aiName + " decides to RAISE $" + amount);
            } else {
                stateChangeCallback.accept(aiName + " decides to FOLD");
            }
            
            try {
                Thread.sleep(1000); // Delay between messages
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            stateChangeCallback.accept("=================\n");
        }
    }
}