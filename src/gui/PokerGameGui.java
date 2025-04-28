package gui;

import card.CardSkin;
import gamemode.JavaFXPokerMode;
import games.PokerGame;
import input.PokerActionProcessor;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import playable.AI;
import playable.Playable;
import playable.Player;
import server.NetworkManager;

import java.util.List;

public class PokerGameGui {
    @FXML private AnchorPane gamePane;
    @FXML private StackPane rootPane;
    @FXML private ImageView communityCard1;
    @FXML private ImageView communityCard2;
    @FXML private ImageView communityCard3;
    @FXML private ImageView communityCard4;
    @FXML private ImageView communityCard5;

    @FXML private Pane player1CardArea;
    @FXML private Pane player2CardArea;
    @FXML private Pane player3CardArea;
    @FXML private Pane player4CardArea;

    @FXML private ImageView player1Card1;
    @FXML private ImageView player1Card2;
    @FXML private ImageView player2Card1;
    @FXML private ImageView player2Card2;
    @FXML private ImageView player3Card1;
    @FXML private ImageView player3Card2;
    @FXML private ImageView player4Card1;
    @FXML private ImageView player4Card2;

    @FXML private TextField raiseField;
    @FXML private Slider raiseSlider;
    @FXML private Button raiseButton;
    @FXML private Button foldButton;

    @FXML private Label player1Name;
    @FXML private Label player1Money;
    @FXML private Label player2Name;
    @FXML private Label player2Money;
    @FXML private Label player3Name;
    @FXML private Label player3Money;
    @FXML private Label player4Name;
    @FXML private Label player4Money;

    @FXML private Text potMoney;

    private PokerGame game;
    private CardSkin cardSkin;
    private JavaFXPokerMode gameMode;
    private NetworkManager networkManager;
    private boolean isOnlineMode;
    private String aiStrategy;
    private int playerId = 1;

    @FXML
    public void initialize() {
        setupComponents();
        setupEventHandlers();
        initializeGame();
    }
    private void setupComponents() {
        // Hide unused player areas initially
        player3CardArea.setVisible(false);
        player4CardArea.setVisible(false);

        // Set initial slider range
        raiseSlider.setMin(0);
        raiseSlider.setMax(1000);
        raiseSlider.setValue(100);
        // Set consistent card sizes
        setCardSize(communityCard1, communityCard2, communityCard3, communityCard4, communityCard5);
        setCardSize(player1Card1, player1Card2);
        setCardSize(player2Card1, player2Card2);
        setCardSize(player3Card1, player3Card2);
        setCardSize(player4Card1, player4Card2);
        // Set initial money amounts
        potMoney.setText("$0");
        player1Money.setText("$1000");
        player2Money.setText("$1000");
        player3Money.setText("$1000");
        player4Money.setText("$1000");

        // Set initial player names
        player1Name.setText("Player 1");
        player2Name.setText("AI Player");
        player3Name.setText("Player 3");
        player4Name.setText("Player 4");

        // Enable buttons
        raiseButton.setDisable(false);
        foldButton.setDisable(false);
    }
    private void setCardSize(ImageView... cards) {
        double cardWidth = 60;
        double cardHeight = 87;

        for (ImageView card : cards) {
            card.setFitWidth(cardWidth);
            card.setFitHeight(cardHeight);
            card.setPreserveRatio(true);
            card.setSmooth(true);
        }
    }

    public void setupGame(String selectedSkin, String selectedAI, boolean isOnline) {
        this.cardSkin = new CardSkin(selectedSkin != null ? selectedSkin : "Traditional");
        this.aiStrategy = aiStrategy != null ? aiStrategy : "Rule based";
        this.isOnlineMode = isOnline;

        // Set up game components
        ImageView[] communityCards = {
                communityCard1, communityCard2, communityCard3,
                communityCard4, communityCard5
        };

        ImageView[][] playerCards = {
                {player1Card1, player1Card2},
                {player2Card1, player2Card2},
                {player3Card1, player3Card2},
                {player4Card1, player4Card2}
        };

        // Create game mode with proper skin
        gameMode = new JavaFXPokerMode(
                communityCards,
                playerCards,
                new Label[]{player1Name, player2Name, player3Name, player4Name},
                new Label[]{player1Money, player2Money, player3Money, player4Money},
                potMoney,
                isOnline,
                playerId
        );
        gameMode.setCardSkin(cardSkin);

        // Create and start game
        if (!isOnline) {
            game = new PokerGame(gameMode, networkManager, cardSkin);
            gameMode.setGame(game);

            Player humanPlayer = new Player("Player 1", playerId);
            humanPlayer.addCurrentBalance(1000);
            game.addPlayer(humanPlayer);

            AI aiPlayer = new AI(2, "AI Player");
            aiPlayer.addCurrentBalance(1000);
            aiPlayer.setStrategyType(this.aiStrategy);
            game.addPlayer(aiPlayer);

            game.start();
        }
    }

    private void setupEventHandlers() {
        PokerActionProcessor processor = new PokerActionProcessor();

        raiseButton.setOnAction(e -> {
            if (game != null && game.getCurrentPlayer() != null) {
                try {
                    int amount = Integer.parseInt(raiseField.getText());
                    if (amount > 0) {
                        processor.processAction("raise", game, null);
                        updateMoneyDisplays();
                        game.progressGame();
                        // Progress game after action
                    }
                } catch (NumberFormatException ex) {
                    // Handle invalid input
                }
            }
        });

        foldButton.setOnAction(e -> {
            if (game != null && game.getCurrentPlayer() != null) {
                processor.processAction("fold", game, null);
                game.progressGame(); // Progress game after action
            }
        });

        setupSliderAndTextField();
    }
    private void updateMoneyDisplays() {
        if (game != null) {
            // Update pot money
            potMoney.setText("$" + game.getPot());

            // Update player money
            List<Playable> players = game.getPlayers();
            Label[] moneyLabels = {player1Money, player2Money, player3Money, player4Money};

            for (int i = 0; i < moneyLabels.length && i < ((List<?>) players).size(); i++) {
                Playable player = players.get(i);
                moneyLabels[i].setText("$" + player.getCurrentBalance());
            }
        }
    }

    private void setupSliderAndTextField() {
        raiseSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                raiseField.setText(String.valueOf(newVal.intValue()))
        );

        raiseField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                int val = Integer.parseInt(newVal);
                if (val >= raiseSlider.getMin() && val <= raiseSlider.getMax()) {
                    raiseSlider.setValue(val);
                }
            } catch (NumberFormatException ignored) {}
        });
    }

    private void initializeGame() {
        player3CardArea.setVisible(false);
        player4CardArea.setVisible(false);

        raiseSlider.setValue(100);
        potMoney.setText("$0");

        for (Label money : new Label[]{
                player1Money, player2Money, player3Money, player4Money
        }) {
            money.setText("$1000");
        }
    }

    public void cleanup() {
        if (networkManager != null) {
            networkManager.close();
        }
    }

    public JavaFXPokerMode getGameMode() {
        return gameMode;
    }

    public AnchorPane getGamePane() {
        return gamePane;
    }

    public void continueGame() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        boolean continueGame = DialogHelper.askToContinue(stage);
        if (continueGame) {
            // Reset game state
            initialize();
        } else {
            // maybe close the application or go back to main menu
            stage.close();
        }
    }

}