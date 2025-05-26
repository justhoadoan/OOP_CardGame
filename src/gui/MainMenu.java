package gui;

import games.BlackjackGame;
import games.GameType;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenu {
    @FXML private StackPane rootPane;
    @FXML
    private Button nextMainMenu;
    @FXML
    private Pane skinPane;
    @FXML
    private AnchorPane mainMenuPane;
    @FXML
    private Pane multiplayerPane;
    @FXML
    private ChoiceBox<String> gameChoiceBox;
    @FXML
    private ChoiceBox<String> cardSkinChoiceBox;
    @FXML
    private ChoiceBox<String> gameModeChoiceBox;
    @FXML
    private ChoiceBox<String> typeChoiceBox;
    @FXML
    private Stage stage;
    @FXML
    private ChoiceBox<String> multiplayerChoiceBox;

    @FXML
    public void initialize() {
        // Reset button

        // Initialize the choice boxes with options
        gameChoiceBox.setItems(FXCollections.observableArrayList("Poker", "BlackJack"));
        cardSkinChoiceBox.setItems(FXCollections.observableArrayList("Traditional", "Realistic", "Animated"));
        gameModeChoiceBox.setItems(FXCollections.observableArrayList("Graphic", "Non-Graphic"));
        multiplayerChoiceBox.setItems(FXCollections.observableArrayList("1", "2", "3", "4"));

        // skinPane and typePane initially invisible
        skinPane.setVisible(false);
        multiplayerPane.setVisible(false);

        // Set visibility of skinPane based on gameModeChoiceBox value
        gameModeChoiceBox.setOnAction(e -> {
            if (gameModeChoiceBox.getValue().equals("Graphic")) {
                skinPane.setVisible(true);
            } else {
                skinPane.setVisible(false);
                cardSkinChoiceBox.setValue("Basic");
            }
        });


        // Set visibility of typePane based on gameChoiceBox value
        gameChoiceBox.setOnAction(e -> {
            if (gameChoiceBox.getValue().equals("Poker")) {
                multiplayerPane.setVisible(true);
            } else {
                multiplayerPane.setVisible(false);
            }
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void Next() throws IOException{
        try {
            // Get stage from current scene
            Stage stage = (Stage) cardSkinChoiceBox.getScene().getWindow(); // Use any @FXML injected node

            FXMLLoader loader;
            AnchorPane contentPane = null; // The root element to scale
            Scene scene = null;

            String selectedSkin = cardSkinChoiceBox.getValue();
            String selectedGame = gameChoiceBox.getValue();
            String selectedPlayers = multiplayerChoiceBox.getValue(); // Get the number of

            if (selectedGame != null && selectedGame.equals("Poker")) {
                int numberOfPlayers = Integer.parseInt(selectedPlayers);
                if (numberOfPlayers == 1) {
                    // Load PokerAIOffline for single player with AI
                    loader = new FXMLLoader(getClass().getResource("PokerAIOffline.fxml"));
                    StackPane rootPane = loader.load();
                    scene = new Scene(rootPane);

                    PokerAIOffline controller = loader.getController();
                    controller.setSelectedSkin(selectedSkin);
                    controller.resetState();
                    contentPane = controller.getOfflineMenuPane(); // Get the main content pane
                } else {
                    // Load PokerGame for multiplayer
                    loader = new FXMLLoader(getClass().getResource("PokerGame.fxml"));
                    StackPane rootPane = loader.load();
                    scene = new Scene(rootPane);

                    PokerGameGui controller = loader.getController();
                    contentPane = controller.getGamePane(); // Get the main content pane
                    controller.setupGame(selectedSkin, numberOfPlayers); // Pass the number of players
                }
            } else if (selectedGame != null && selectedGame.equals("BlackJack")) {
                loader = new FXMLLoader(getClass().getResource("BlackJackBet.fxml"));
                StackPane rootPane = loader.load();
                scene = new Scene(rootPane);

                BlackJackBetGui controller = loader.getController();
                controller.setSelectedSkin(selectedSkin);
                contentPane = controller.getBetPane(); // Get the main content pane
                controller.initialize();
            }
            // === SCALING LOGIC ===
            if (contentPane != null) {
                // Ensure design dimensions are set in FXML
                final double DESIGN_WIDTH = contentPane.getPrefWidth();
                final double DESIGN_HEIGHT = contentPane.getPrefHeight();

                Scale scale = new Scale(1, 1);
                contentPane.getTransforms().add(scale);

                // Uniform scaling binding
                scale.xProperty().bind(Bindings.min(
                        scene.widthProperty().divide(DESIGN_WIDTH),
                        scene.heightProperty().divide(DESIGN_HEIGHT)
                ));
                scale.yProperty().bind(scale.xProperty());
            } else {
                throw new RuntimeException("Main content pane not found in controller");
            }

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // reset state method
    @FXML
    public void resetState() {
        gameChoiceBox.getSelectionModel().clearSelection();
        cardSkinChoiceBox.getSelectionModel().clearSelection();
        gameModeChoiceBox.getSelectionModel().clearSelection();
        multiplayerChoiceBox.getSelectionModel().clearSelection();
        skinPane.setVisible(false);
        multiplayerPane.setVisible(false);
        mainMenuPane.setVisible(true);
        nextMainMenu.setVisible(true);
        nextMainMenu.setDisable(false);
    }

    public AnchorPane getMainMenuPane() {
        return mainMenuPane;
    }
}
