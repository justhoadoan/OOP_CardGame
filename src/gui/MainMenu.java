package gui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
        if (typeChoiceBox.getValue().equals("Online")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("OnlineMenu.fxml"));
            try {
                Scene scene = new Scene(loader.load());
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (gameChoiceBox.getValue().equals("BlackJack")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BlackJackOffline.fxml"));
            try {
                Scene scene = new Scene(loader.load());
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PokerAIOffline.fxml"));
            try {
                Scene scene = new Scene(loader.load());
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}