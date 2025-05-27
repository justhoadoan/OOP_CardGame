package gui;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.function.UnaryOperator;

public class BlackJackBetController {
    @FXML private AnchorPane betPane;
    @FXML private StackPane rootPane;
    @FXML private Slider betSlider;
    @FXML private TextField betTextField;
    @FXML private Button dealButton;

    private String selectedSkin;

    @FXML
    public void initialize() {
        // Set up numeric-only filter for betTextField
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            // Allow empty text or valid integers
            if (newText.isEmpty() || newText.matches("\\d+")) {
                return change;
            }
            return null; // Reject the change
        };

        betTextField.setTextFormatter(new TextFormatter<>(integerFilter));

        // Keep existing slider synchronization
        betSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            betTextField.setText(String.valueOf(newVal.intValue()));
        });

        // Update slider when text field changes
        betTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) {
                try {
                    int value = Integer.parseInt(newVal);
                    if (value >= betSlider.getMin() && value <= betSlider.getMax()) {
                        betSlider.setValue(value);
                    }
                } catch (NumberFormatException ignored) {}
            }
        });
    }

    @FXML
    private void handleDeal() {
        try {
            // Load the BlackJackGame.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BlackJackGame.fxml"));
            StackPane rootPane = loader.load();
            Scene scene = new Scene(rootPane);

            // Get the controller for BlackJackGameGui
            BlackJackGameController gameController = loader.getController();
            gameController.setupGame(selectedSkin);
            AnchorPane contentPane = gameController.getGamePane(); // Get the main content pane

            // Pass the slider value to the gameController
            int betValue = (int) betSlider.getValue();
            gameController.setBetValue(betValue);

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


            // Set the new scene
            Stage stage = (Stage) dealButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSelectedSkin(String selectedSkin) {
        this.selectedSkin = selectedSkin;
    }
    public AnchorPane getBetPane() {
        return betPane;
    }
}