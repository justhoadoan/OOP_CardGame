package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class BlackJackBetGui {
    @FXML private Slider betSlider;
    @FXML private TextField betTextField;
    @FXML private Button dealButton;

    @FXML
    public void initialize() {
        // Sync slider and text field
        betSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            betTextField.setText(String.valueOf(newVal.intValue()));
        });
    }

    @FXML
    private void handleDeal() {
        try {
            // Load the BlackJackGame.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BlackJackGame.fxml"));
            Parent root = loader.load();

            // Get the controller for BlackJackGameGui
            BlackJackGameGui gameController = loader.getController();

            // Pass the slider value to the gameController
            int betValue = (int) betSlider.getValue();
            gameController.setBetValue(betValue);

            // Set the new scene
            Stage stage = (Stage) dealButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}