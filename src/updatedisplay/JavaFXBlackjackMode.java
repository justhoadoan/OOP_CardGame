package updatedisplay;

import card.Card;
import card.CardSkin;
import games.BlackjackGame;
import games.Game;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Objects;

public class JavaFXBlackjackMode implements GameMode {
    private final ImageView[] playerCards;
    private final ImageView[] dealerCards;
    private final Text playerScoreText;
    private final Text dealerScoreText;
    private final Label playerName;
    private final Label dealerName;
    private BlackjackGame game;
    private CardSkin cardSkin;

    public JavaFXBlackjackMode(ImageView[] playerCards,
                               ImageView[] dealerCards,
                               Text playerScoreText,
                               Text dealerScoreText,
                               Label playerName,
                               Label dealerName) {
        this.playerCards = playerCards;
        this.dealerCards = dealerCards;
        this.playerScoreText = playerScoreText;
        this.dealerScoreText = dealerScoreText;
        this.playerName = playerName;
        this.dealerName = dealerName;
    }

    public void setBlackjackGame(BlackjackGame game) {
        this.game = game;
    }

    @Override
    public void setGame(Game game) {
        // Not used for Blackjack
    }

    @Override
    public void setCardSkin(CardSkin skin) {
        this.cardSkin = skin;
        updateDisplay(null, game.getPublicState(), null);
    }

    @Override
    public void updateDisplay(List<Card> playerHand, String publicState, String winner) {
        Platform.runLater(() -> {
            updatePlayerCards();
            updateDealerCards();
            updateScores();
        });
    }

    private void updatePlayerCards() {
        if (game == null) return;

        List<Card> hand;
        if (game.getCurrentPlayer() == game.getDealer()) {
            hand = game.getPlayerBeforeDealer().getHand();
        } else {
            hand = game.getCurrentPlayer().getHand();
        }

        for (int i = 0; i < playerCards.length; i++) {
            if (i < hand.size()) {
                Card card = hand.get(i);
                String imagePath = cardSkin.getImagePath(card.getRank(), card.getSuit());
                Image cardImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
                playerCards[i].setImage(cardImage);
                playerCards[i].setVisible(true);
            } else {
                playerCards[i].setVisible(false);
            }
        }
    }

    private void updateDealerCards() {
        if (game == null) return;

        List<Card> hand = game.getDealer().getHand();

        for (int i = 0; i < dealerCards.length; i++) {
            if (i < hand.size()) {
                Card card = hand.get(i);
                String imagePath = cardSkin.getImagePath(card.getRank(), card.getSuit());
                Image cardImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
                dealerCards[i].setImage(cardImage);
                dealerCards[i].setVisible(true);
            } else {
                dealerCards[i].setVisible(false);
            }
        }
    }

    private void updateScores() {
        if (game == null) return;

        // Update player score
        List<Card> playerHand;
        if (game.getCurrentPlayer() == game.getDealer()) {
            playerHand = game.getPlayerBeforeDealer().getHand();
        } else {
            playerHand = game.getCurrentPlayer().getHand();
        }
        int playerScore = game.calculateScore(playerHand);
        playerScoreText.setText("" + playerScore);

        // Update dealer score
        List<Card> dealerHand = game.getDealer().getHand();
        int dealerScore = game.calculateScore(dealerHand);
        dealerScoreText.setText("" + dealerScore);
    }

    @Override
    public String getGameState() {
        return game != null ? game.getPublicState() : "";
    }
}