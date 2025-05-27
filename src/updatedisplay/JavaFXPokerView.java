package updatedisplay;

import cards.card.Card;
import games.Game;
import games.PokerGame;

import gui.PopupController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import playable.AI;
import playable.Playable;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class JavaFXPokerView extends DisplayUpdating {
    private final ImageView[] communityCards;
    private final ImageView[][] playerCards;
    private final Label[] playerNames;
    private final Label[] playerMoney;
    private final Text potMoney;
    private PokerGame game;
    private String cardSkin;

    private final int playerId;
    private final TextField raiseField;
    private final Slider raiseSlider;

    public JavaFXPokerView(ImageView[] communityCards,
                           ImageView[][] playerCards,
                           Label[] playerNames,
                           Label[] playerMoney,
                           Text potMoney,
                           int playerId,
                           TextField raiseField, Slider raiseSlider) {
        this.communityCards = communityCards;
        this.playerCards = playerCards;
        this.playerNames = playerNames;
        this.playerMoney = playerMoney;
        this.potMoney = potMoney;

        this.playerId = playerId;
        this.raiseField = raiseField;
        this.raiseSlider = raiseSlider;
        setupSliderSync();
    }

    @Override
    public void setGame(Game game) {
        this.game = (PokerGame) game;
    }

    @Override
    public void setCardSkin(String skin) {
        this.cardSkin = skin;
        if (game != null) {
            updateDisplay(null, game.getPublicState(), null);
        }
    }
    private void setupRaiseSliderConstraints() {
        if (game == null) return;


        int currentBet = game.getCurrentBetGame();
//        int minBalance = game.getPlayers().stream()
//                .filter(Playable::getStatus)
//                .mapToInt(Playable::getCurrentBalance)
//                .min()
//                .orElse(0);
        int maxBalance = 1000000000;
        Playable currentPlayer = game.getCurrentPlayer();
        for (Playable player : game.getPlayers()) {
            if (player.getStatus()) {
                maxBalance = Math.min(maxBalance, player.getCurrentBalance() + player.getCurrentBet() - currentPlayer.getCurrentBet());
            }
        }
        final int finalMaxBalance = maxBalance;

        Platform.runLater(() -> {
            raiseSlider.setMin(currentBet - currentPlayer.getCurrentBet());
            raiseSlider.setMax(finalMaxBalance);
            raiseSlider.setValue(currentBet);
            raiseField.setText(String.valueOf(currentBet));
        });
    }

    private void setupSliderSync() {
        raiseSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double value = newVal.doubleValue();
            if (value < raiseSlider.getMin()) {
                raiseSlider.setValue(raiseSlider.getMin());
            } else if (value > raiseSlider.getMax()) {
                raiseSlider.setValue(raiseSlider.getMax());
            } else {
                raiseField.setText(String.valueOf((int)value));
            }
        });

        raiseField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                int value = Integer.parseInt(newVal);
                if (value < raiseSlider.getMin()) {
                    raiseField.setText(String.valueOf((int)raiseSlider.getMin()));
                } else if (value > raiseSlider.getMax()) {
                    raiseField.setText(String.valueOf((int)raiseSlider.getMax()));
                } else {
                    raiseSlider.setValue(value);
                }
            } catch (NumberFormatException ignored) {
                raiseField.setText(oldVal);
            }
        });
    }

    private boolean popupShown = false;

    @Override
    public void updateDisplay(List<Card> playerHand, String publicState, String winner) {
        Platform.runLater(() -> {
            updateCommunityCards();
            updateAllPlayerCards(playerHand);
            updatePlayerInfo();
            updatePotMoney();
            setupRaiseSliderConstraints();
            if (winner != null && !winner.isEmpty() && !popupShown) {
                popupShown = true;
                try {
                    showGameOverDialog(winner);
                } catch (IOException e) {
                    System.err.println("Error showing game over dialog: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    public void resetPopupFlag() {
        popupShown = false;
    }

    private void showGameOverDialog(String winner) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/Popup.fxml"));
        Parent root = loader.load();

        PopupController controller = loader.getController();
        controller.setWinnerText(winner);

        Stage popupStage = new Stage();
        popupStage.setScene(new Scene(root));
        popupStage.setTitle("Game Over");
        popupStage.setAlwaysOnTop(true);  // Make sure popup stays on top

        // Show and wait - this forces it to be shown immediately
        popupStage.showAndWait();

        // After window is closed
        if (controller.isPlayAgain()) {
            resetPopupFlag(); // Reset the flag here too
            if (game != null) {
                game.start(); // Start a new game
            }
        } else {
            Platform.runLater(() -> {
                Stage stage = (Stage) communityCards[0].getScene().getWindow();
                stage.close(); // Exit application
            });
        }
    }

    private void updateCommunityCards() {
        if (game == null) return;
        List<Card> community = game.getCommunityCards();
        for (int i = 0; i < communityCards.length; i++) {
            if (i < community.size()) {
                Card card = community.get(i);
                if (cardSkin != null) {
                    card.setSkin(cardSkin);
                    String imagePath = card.getImagePath(card.getRank(), card.getSuit());
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
        boolean isRiverRound = game.getCommunityCards().size() > 5;

        for (int i = 0; i < playerCards.length && i < players.size(); i++) {
            Playable player = players.get(i);
            List<Card> playerHand = player.getHand();

            for (int j = 0; j < playerCards[i].length; j++) {
                if (j < playerHand.size()) {
                    // Show all cards if game is over, river round is reached, or it's the current player's hand
                    if (isGameOver || isRiverRound || (player == currentPlayer && !(player instanceof AI))) {
                        Card card = playerHand.get(j);
                        if (cardSkin != null) {
                            card.setSkin(cardSkin);
                        }
                        String path = card.getImagePath(card.getRank(), card.getSuit());
                        Image cardImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
                        playerCards[i][j].setImage(cardImage);
                    } else {
                        Card card = playerHand.get(j);
                        if (cardSkin != null) {
                            card.setSkin(cardSkin);
                        }
                        String backPath = card.getImagePath("Opponent","");
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