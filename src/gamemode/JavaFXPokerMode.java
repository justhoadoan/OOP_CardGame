package gamemode;

import card.Card;
import card.CardSkin;
import games.Game;
import games.PokerGame;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import playable.AI;
import playable.Playable;

import java.util.List;
import java.util.Objects;

public class JavaFXPokerMode implements GameMode {
    private final ImageView[] communityCards;
    private final ImageView[][] playerCards;
    private final Label[] playerNames;
    private final Label[] playerMoney;
    private final Text potMoney;
    private PokerGame game;
    private CardSkin cardSkin;

    private final int playerId;

    private final TextField raiseField;
    private final Slider raiseSlider;
    public JavaFXPokerMode(ImageView[] communityCards,
                           ImageView[][] playerCards,
                           Label[] playerNames,
                           Label[] playerMoney,
                           Text potMoney,
                           int playerId, TextField raiseField, Slider raiseSlider) {
        this.communityCards = communityCards;
        this.playerCards = playerCards;
        this.playerNames = playerNames;
        this.playerMoney = playerMoney;
        this.potMoney = potMoney;
        this.playerId = playerId;
        this.raiseField = raiseField;
        this.raiseSlider = raiseSlider;
    }

    @Override
    public void setGame(Game game) {
        this.game = (PokerGame) game;
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
            updateCommunityCards();
            updateAllPlayerCards(playerHand);
            updatePlayerInfo();
            setupRaiseSliderConstraints();
            updatePotMoney();
            if (winner != null) {
                showGameOverDialog(winner);
            }
        });
    }
    private void setupRaiseSliderConstraints() {
        if (game == null) return;

        // Get current bet and minimum balance
        int currentBet = game.getCurrentBetGame();
        int minBalance = game.getPlayers().stream()
                .filter(Playable::getStatus)
                .mapToInt(Playable::getCurrentBalance)
                .min()
                .orElse(0);

        // Set slider constraints
        Platform.runLater(() -> {
            raiseSlider.setMin(currentBet);
            raiseSlider.setMax(minBalance);
            raiseSlider.setValue(currentBet);
            raiseField.setText(String.valueOf(currentBet));

            // Sync slider and text field
            raiseSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                    raiseField.setText(String.valueOf(newVal.intValue()))
            );

            raiseField.textProperty().addListener((obs, oldVal, newVal) -> {
                try {
                    int value = Integer.parseInt(newVal);
                    if (value >= currentBet && value <= minBalance) {
                        raiseSlider.setValue(value);
                    }
                } catch (NumberFormatException ignored) {}
            });
        });
    }

    private void showGameOverDialog(String winner) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(winner + " has won the game!");
        alert.setContentText("Would you like to play again?");

        ButtonType playAgainButton = new ButtonType("Play Again");
        ButtonType exitButton = new ButtonType("Exit", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(playAgainButton, exitButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == playAgainButton) {
                if (game != null) {
                    game.start();
                }
            } else {
                Stage stage = (Stage) communityCards[0].getScene().getWindow();
                stage.close();
            }
        });
    }

    private void updateCommunityCards() {
        if (game == null) return;
        List<Card> community = game.getCommunityCards();
        for (int i = 0; i < communityCards.length; i++) {
            if (i < community.size()) {
                Card card = community.get(i);
                if (cardSkin != null) {
                    card.setSkin(cardSkin);
                    String imagePath = cardSkin.getImagePath(card.getRank(), card.getSuit());
                    Image cardImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
                    communityCards[i].setImage(cardImage);
                    communityCards[i].setVisible(true);
                }
            } else {
                communityCards[i].setVisible(false);
            }
        }
    }

    private void updateAllPlayerCards(List<Card> currentPlayerHand) {
        if (game == null) return;
        List<Playable> players = game.getPlayers();
        Playable currentPlayer = game.getCurrentPlayer();
        boolean isGameOver = game.isGameOver();
        boolean isRiverRound = game.getCommunityCards().size() == 5;

        for (int i = 0; i < playerCards.length && i < players.size(); i++) {
            Playable player = players.get(i);
            List<Card> playerHand = player.getHand();

            for (int j = 0; j < playerCards[i].length; j++) {
                if (j < playerHand.size()) {
                    // Show all cards if game is over, river round is reached, or it's the current player's hand
                    if (isGameOver || isRiverRound || (player == currentPlayer && !(player instanceof AI))) {
                        Card card = playerHand.get(j);
                        String path = cardSkin.getImagePath(card.getRank(), card.getSuit());
                        Image cardImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
                        playerCards[i][j].setImage(cardImage);
                    } else {
                        String backPath = cardSkin.getImagePath("Opponent", "");
                        Image backImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(backPath)));
                        playerCards[i][j].setImage(backImage);
                    }
                    playerCards[i][j].setVisible(true);
                } else {
                    playerCards[i][j].setVisible(false);
                }
            }
        }
    }


    private void updatePlayerInfo() {
        if (game == null) return;
        List<Playable> players = game.getPlayers();
        boolean isGameOver = game.isGameOver();

        for (int i = 0; i < playerNames.length && i < players.size(); i++) {
            Playable player = players.get(i);
            playerNames[i].setText(player.getName());
            playerMoney[i].setText("$" + player.getCurrentBalance());
        }
    }


    private void updatePotMoney() {
        if (game == null) return;
        potMoney.setText("$" + game.getPot());
    }

    private void showWinner(String winner) {
        // Optional: Add winner announcement UI
    }

    @Override
    public String getGameState() {
        return game != null ? game.getPublicState() : "";
    }


    public Object getGame() {
        return game;
    }
}