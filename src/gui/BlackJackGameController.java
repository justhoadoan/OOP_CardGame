package gui;

import updatedisplay.JavaFXBlackjackView;
import games.BlackjackGame;
import processor.BlackjackActionProcessor;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import playable.Player;

public class BlackJackGameController {
    @FXML private StackPane rootPane;
    @FXML private ImageView playerCard1;
    @FXML private ImageView playerCard2;
    @FXML private ImageView dealerCard1;
    @FXML private ImageView dealerCard2;
    @FXML private ImageView dealerCard3;
    @FXML private ImageView playerCard3;
    @FXML private ImageView playerCard4;
    @FXML private ImageView dealerCard4;
    @FXML private ImageView playerCard5;
    @FXML private ImageView dealerCard5;

    @FXML private Text betValueText;
    @FXML private Text dealerScoreText;
    @FXML private Text playerScoreText;
    @FXML private Button hitButton;
    @FXML private Button standButton;
    @FXML private AnchorPane gamePane;

    private BlackjackGame game;
    private String cardSkin;
    private JavaFXBlackjackView blackjackMode;

    @FXML
    private void handleHit() {
        hitButton.fire();
    }

    @FXML
    private void handleStand() {
        standButton.fire();
    }

    @FXML
    private void replayGame() {
        // Reset the game state
        game.start();

        // Reset the GUI components
        initializeGame();
        blackjackMode.updateDisplay(null, game.getPublicState(), null);

        // Re-enable action buttons
        hitButton.setDisable(false);
        standButton.setDisable(false);
    }

    public void initialize() {
        setupEventsHandlers();
        initializeGame();
    }

    void setupGame(String selectedSkin) {
        // Set card skin
        this.cardSkin = selectedSkin != null ? selectedSkin : "Traditional";

        // Initialize game
        this.game = new BlackjackGame();
        Player player = new Player("Player", 1);
        player.addCurrentBalance(1000);
        game.addPlayer(player);
        Player dealer = new Player("Dealer", 0);
        game.addPlayer(dealer);

        // First setup the UI components
        setupBaseComponents();
        game.start();
        // Update display with game state
        blackjackMode.updatePlayerCards();
        blackjackMode.updateDealerCards();
        blackjackMode.updatePlayerScore();
        blackjackMode.updateDealerScore();
    }

    void setupBaseComponents() {
        this.blackjackMode = new JavaFXBlackjackView(
                new ImageView[]{playerCard1, playerCard2, playerCard3, playerCard4, playerCard5},
                new ImageView[]{dealerCard1, dealerCard2, dealerCard3, dealerCard4, dealerCard5},
                playerScoreText,
                dealerScoreText
        );
        blackjackMode.setGame(game);
        blackjackMode.setCardSkin(cardSkin);
    }

    private void setupEventsHandlers() {
        BlackjackActionProcessor actionProcessor = new BlackjackActionProcessor();

        hitButton.setOnAction(event -> {
            actionProcessor.processAction("hit", game, game.getCurrentPlayer());
            blackjackMode.updateDisplay(null, game.getPublicState(), null);
            checkGameOver();
        });

        standButton.setOnAction(event -> {
            actionProcessor.processAction("stand", game, game.getCurrentPlayer());
            blackjackMode.updateDisplay(null, game.getPublicState(), null);
            checkGameOver();
        });
    }

    private void checkGameOver() {
        if (game.isGameOver()) {
            hitButton.setDisable(true);
            standButton.setDisable(true);
            endGame();
        }
    }

    private void initializeGame() {
        // Hide all cards
        ImageView[] playerCards = {playerCard1, playerCard2, playerCard3, playerCard4, playerCard5};
        ImageView[] dealerCards = {dealerCard1, dealerCard2, dealerCard3, dealerCard4, dealerCard5};

        for (ImageView card : playerCards) {
            card.setVisible(false);
        }
        for (ImageView card : dealerCards) {
            card.setVisible(false);
        }

        // Reset scores and bet
        playerScoreText.setText("0");
        dealerScoreText.setText("0");
        betValueText.setText("$0");

        // Enable action buttons
        hitButton.setDisable(false);
        standButton.setDisable(false);
    }

    public void setBetValue(int betValue) {
        betValueText.setText("$" + betValue);
    }

    private void endGame() {
        String winnerName = game.getWinner();
        showWinnerPopup(winnerName);
    }

    private void showWinnerPopup(String winnerName) {
        Stage popupStage = new Stage();
        popupStage.setTitle("Game Over");

        Label winnerLabel = new Label("Winner: " + winnerName);
        winnerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button replayButton = new Button("Replay");
        replayButton.setOnAction(event -> {
            replayGame();
            popupStage.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(winnerLabel, replayButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 300, 150);
        popupStage.setScene(scene);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    }

    public AnchorPane getGamePane() {
        return gamePane;
    }
}
