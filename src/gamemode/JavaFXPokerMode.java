package gamemode;

import card.Card;
import card.CardSkin;
import games.PokerGame;
import gui.DialogHelper;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import playable.AI;
import playable.Playable;
import test.TestImagePath;

import java.io.IOException;
import java.io.InputStream;
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
    private final boolean isOnlineMode;
    private final int playerId;


    public JavaFXPokerMode(ImageView[] communityCards,
                           ImageView[][] playerCards,
                           Label[] playerNames,
                           Label[] playerMoney,
                           Text potMoney,
                           boolean isOnlineMode,
                           int playerId) {
        this.communityCards = communityCards;
        this.playerCards = playerCards;
        this.playerNames = playerNames;
        this.playerMoney = playerMoney;
        this.potMoney = potMoney;
        this.isOnlineMode = isOnlineMode;
        this.playerId = playerId;
    }

    @Override
    public void setGame(PokerGame game) {
        this.game = game;
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
            updatePotMoney();
            if (winner != null) {
                try {
                    showGameOverDialog(winner);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void showGameOverDialog(String winner) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/Popup.fxml"));
        Parent root = loader.load();
        Stage popupStage = new Stage();
        popupStage.setScene(new Scene(root));
        popupStage.setTitle("Popup");
        //popupStage.initModality(Modality.APPLICATION_MODAL); // Optional
        popupStage.showAndWait();
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