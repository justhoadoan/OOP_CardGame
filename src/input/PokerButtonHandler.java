package input;

import gamemode.JavaFXPokerMode;
import games.PokerGame;
import playable.Player;

public class PokerButtonHandler {
    private final JavaFXPokerMode gameMode;

    public PokerButtonHandler(JavaFXPokerMode gameMode) {
        this.gameMode = gameMode;
    }

    public void handleRaise(int amount) {
        PokerGame game = (PokerGame) gameMode.getGame();
        if (game != null) {
            Player currentPlayer = (Player) game.getCurrentPlayer();
            if (currentPlayer != null && amount > 0 && amount <= currentPlayer.getCurrentBalance()) {
                game.playerRaise(currentPlayer, amount);
            }
        }
    }

    public void handleFold() {
        PokerGame game = (PokerGame) gameMode.getGame();
        if (game != null) {
            Player currentPlayer = (Player) game.getCurrentPlayer();
            if (currentPlayer != null) {
                game.playerFold(currentPlayer);
            }
        }
    }
}