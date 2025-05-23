package gui;

import games.BlackjackGame;
import games.GameType;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenu {
    @FXML
    private Button nextMainMenu;
    @FXML
    private Pane skinPane;
    @FXML
    private Pane mainMenuPane;
    @FXML
    private Pane typePane;
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
    public void initialize() {
        // Reset button

        // Initialize the choice boxes with options
        gameChoiceBox.setItems(FXCollections.observableArrayList("Poker", "BlackJack"));
        cardSkinChoiceBox.setItems(FXCollections.observableArrayList("Traditional", "Realistic", "Animated"));
        gameModeChoiceBox.setItems(FXCollections.observableArrayList("Graphic", "Non-Graphic"));
        typeChoiceBox.setItems(FXCollections.observableArrayList("Online", "Offline"));

        // skinPane and typePane initially invisible
        skinPane.setVisible(false);
        typePane.setVisible(false);

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
                typePane.setVisible(true);
            } else {
                typePane.setVisible(false);
            }
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void Next() {
        try {
            FXMLLoader loader;
            String selectedSkin = cardSkinChoiceBox.getValue();
            String selectedGame = gameChoiceBox.getValue();
            if (gameChoiceBox.getValue() != null && gameChoiceBox.getValue().equals("BlackJack")) {
                loader = new FXMLLoader(getClass().getResource("BlackJackBet.fxml"));
                Scene scene = new Scene(loader.load());
                BlackJackBetGui controller = loader.getController();
                controller.setSelectedSkin(selectedSkin);
                controller.initialize();
                stage.setScene(scene);
                stage.show();
            } else {
                loader = new FXMLLoader(getClass().getResource("PokerAIOffline.fxml"));
                Scene scene = new Scene(loader.load());
                PokerAIOffline controller = loader.getController();
                controller.setSelectedSkin(selectedSkin); // Pass the selected skin
                controller.resetState();
                stage.setScene(scene);
                stage.show();
            }
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
        typeChoiceBox.getSelectionModel().clearSelection();
        skinPane.setVisible(false);
        typePane.setVisible(false);
        mainMenuPane.setVisible(true);
        nextMainMenu.setVisible(true);
        // reset button
        nextMainMenu.setDisable(false);
    }
}
