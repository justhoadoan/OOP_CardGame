package updatedisplay;

import cards.card.Card;
import games.BlackjackGame;
import games.Game;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import playable.Playable;
import updatedisplay.GameMode;

import java.util.List;
import java.util.Objects;

public class JavaFXBlackjackMode implements GameMode {
    private final ImageView[] playerCards;
    private final ImageView[] dealerCards;
    private final Text playerScoreText;
    private final Text dealerScoreText;
    private BlackjackGame game;
    private String cardSkin;

    public JavaFXBlackjackMode(ImageView[] playerCards,
                               ImageView[] dealerCards,
                               Text playerScoreText,
                               Text dealerScoreText) {
        this.playerCards = playerCards;
        this.dealerCards = dealerCards;
        this.playerScoreText = playerScoreText;
        this.dealerScoreText = dealerScoreText;
    }

    @Override
    public void setGame(Game game) {
        this.game = (BlackjackGame) game;
    }

    @Override
    public void setCardSkin(String skin) {
        this.cardSkin = skin;
        updateDisplay(null, game.getPublicState(), null);
    }

    @Override
    public void updateDisplay(List<Card> playerHand, String publicState, String winner) {
        Platform.runLater(() -> {
            updatePlayerCards();
            updateDealerCards();
            updatePlayerScore();
            updateDealerScore();
        });
    }

    public void updatePlayerCards() {
        if (game == null) return;

        // Get the human player (ID 1) using the proper method
        List<Card> hand = game.getPlayerHand(1);  // Player ID 1 is the human player
        for (int i = 0; i < playerCards.length; i++) {
            if (i < hand.size()) {
                Card card = hand.get(i);
                String imagePath = card.getImagePath(card.getRank(), card.getSuit());
                Image cardImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
                playerCards[i].setImage(cardImage);
                playerCards[i].setVisible(true);
            } else {
                playerCards[i].setVisible(false);
            }
        }
    }

    public void updateDealerCards() {
        if (game == null) return;

        List<Card> hand = game.getDealer().getHand();
        for (int i = 0; i < dealerCards.length; i++) {
            if (i < hand.size()) {
                Card card = hand.get(i);
                String imagePath = card.getImagePath(card.getRank(), card.getSuit());
                Image cardImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
                dealerCards[i].setImage(cardImage);
                dealerCards[i].setVisible(true);
            } else {
                dealerCards[i].setVisible(false);
            }
        }
    }

    public void updatePlayerScore() {
        if (game == null) return;

        // Use the human player's hand (ID 1)
        List<Card> hand = game.getPlayerHand(1);  // Player ID 1 is the human player
        int playerScore = game.calculateScore(hand);
        playerScoreText.setText("" + playerScore);
    }

    public void updateDealerScore() {
        if (game == null) return;

        List<Card> hand = game.getDealer().getHand();
        int dealerScore = game.calculateScore(hand);
        dealerScoreText.setText("" + dealerScore);
    }

    @Override
    public String getGameState() {
        return game != null ? game.getPublicState() : "";
    }
}