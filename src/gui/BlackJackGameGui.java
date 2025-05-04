package gui;

import card.Card;
import card.CardSkin;
import games.BlackjackGame;
import input.BlackjackActionProcessor;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import playable.Player;

import java.util.List;
import java.util.Objects;

public class BlackJackGameGui {
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
    private CardSkin cardSkin;

    public void initialize() {
        // Initialize the game GUI
        initializeGame();
        // Set up event handlers for buttons
        setupEventsHandlers();
        // Set up the game with a default skin
        setupGame("Traditional");
    }

    private void setupGame(String selectedSkin) {
        this.cardSkin = new CardSkin(selectedSkin != null ? selectedSkin : "Traditional");

        initializeGame();

        this.game = new BlackjackGame(null, null, cardSkin);

        Player player = new Player("Player", 1);
        player.addCurrentBalance(1000);
        game.addPlayer(player);

        Player dealer = new Player("Dealer", 0);
        game.addPlayer(dealer);

        game.start(); // Start the game and initialize the current player

        updatePlayerCards();
        updateDealerCards(); // Ensure dealer cards are updated
        updatePlayerScore();
        updateDealerScore();
    }

    private void setupEventsHandlers() {
        BlackjackActionProcessor actionProcessor = new BlackjackActionProcessor();

        // Set up hit button
        hitButton.setOnAction(event -> {
            actionProcessor.processAction("hit", game, null);
            // Update GUI after action
            updatePlayerCards();
            updatePlayerScore();
            if (game.isGameOver()) {
                hitButton.setDisable(true);
                standButton.setDisable(true);
                endGame(); // End the game if it's over
            }
        });

        // Set up stand button
        standButton.setOnAction(event -> {
            actionProcessor.processAction("stand", game, null);
            // Update GUI after action
            updateDealerCards();
            updateDealerScore();
            if (game.isGameOver()) {
                hitButton.setDisable(true);
                standButton.setDisable(true);
                endGame(); // End the game if it's over
            }
        });
    }

    @FXML
    private void handleHit() {
        hitButton.fire(); // Trigger the action set in setupEventsHandlers
    }

    @FXML
    private void handleStand() {
        standButton.fire(); // Trigger the action set in setupEventsHandlers
    }

    @FXML
    private Label winnerLabel;

    public void displayWinner() {
        if (game != null) {
            String winner = game.getWinner(); // Assuming getWinner() returns the winner's name
            if (winner != null && !winner.isEmpty()) {
                winnerLabel.setText("Winner: " + winner);
            } else {
                winnerLabel.setText("No winner this round.");
            }
        }
    }

    private void updatePlayerCards() {
        if (game == null) return;

        // Get the current player's hand
        List<Card> hand = game.getCurrentPlayer().getHand();

        // Update the player's card images
        ImageView[] playerCards = {playerCard1, playerCard2, playerCard3, playerCard4, playerCard5};
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

    private void updatePlayerScore() {
        if (game == null) return;

        // Get the current player's hand
        List<Card> hand = game.getCurrentPlayer().getHand();

        // Calculate the player's score
        int playerScore = game.calculateScore(hand);

        // Update the player's score text
        playerScoreText.setText("" + playerScore);
    }

    private void updateDealerCards() {
        if (game == null) return;

        // Get the dealer's hand
        List<Card> hand = game.getDealer().getHand();

        // Update the dealer's card images
        ImageView[] dealerCards = {dealerCard1, dealerCard2, dealerCard3, dealerCard4, dealerCard5};
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

    private void updateDealerScore() {
        if (game == null) return;

        // Get the dealer's hand
        List<Card> hand = game.getDealer().getHand();

        // Calculate the dealer's score
        int dealerScore = game.calculateScore(hand);

        // Update the dealer's score text
        dealerScoreText.setText("" + dealerScore);
    }

    private void initializeGame() {
        // Hide all player and dealer cards
        ImageView[] playerCards = {playerCard1, playerCard2, playerCard3, playerCard4, playerCard5};
        ImageView[] dealerCards = {dealerCard1, dealerCard2, dealerCard3, dealerCard4, dealerCard5};

        for (ImageView card : playerCards) {
            card.setVisible(false);
        }
        for (ImageView card : dealerCards) {
            card.setVisible(false);
        }

        // Reset scores
        playerScoreText.setText("Player Score: 0");
        dealerScoreText.setText("Dealer Score: 0");

        // Reset bet value
        betValueText.setText("$0");

        // Enable action buttons
        hitButton.setDisable(false);
        standButton.setDisable(false);
    }

    public void setBetValue(int betValue) {
        betValueText.setText("$" + betValue);
    }

    private void endGame() {
        game.showWinner(); // Log the winner in the console
        displayWinner();   // Update the GUI with the winner
    }
}