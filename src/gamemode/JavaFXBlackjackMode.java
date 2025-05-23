package gamemode;

import card.Card;
import card.CardSkin;
import games.BlackjackGame;
import games.PokerGame;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import playable.Playable;

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


    @Override
    public void setGame(PokerGame game) {

    }

    @Override
    public void setCardSkin(CardSkin skin) {
        this.cardSkin = skin;
        if (game != null) {
            updateDisplay(null, game.getPublicState(), null);
        }
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
        List<Card> hand = game.getCurrentPlayer().getHand();
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
        int playerScore = game.calculateScore(game.getCurrentPlayer().getHand());
        int dealerScore = game.calculateScore(game.getDealer().getHand());
        playerScoreText.setText("Player Score: " + playerScore);
        dealerScoreText.setText("Dealer Score: " + dealerScore);
    }

    @Override
    public String getGameState() {
        return game != null ? game.getPublicState() : "";
    }


}