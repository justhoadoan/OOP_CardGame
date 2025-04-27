package gui;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class PokerAIOffline {
    @FXML private StackPane rootPane;
    @FXML private AnchorPane offlineMenuPane;
    @FXML private Button backOnlineMenu;
    @FXML private Button startOnlineMenu;
    @FXML private ChoiceBox typeChoiceBox;
    @FXML private Stage stage;
    private String selectedSkin;
    @FXML
    public void initialize() {
        // Initialize the choice boxes with options
        typeChoiceBox.setValue("Rule based");
        typeChoiceBox.getItems().addAll("Rule based", "Monte Carlo");
    }


    @FXML
    private void backToMainMenu(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainMenu.fxml"));
            StackPane root = loader.load();
            Scene scene = new Scene(root);

            MainMenu controller = loader.getController();
            controller.setStage(stage);
            controller.resetState();

            // Access mainMenuPane via controller (ensure it's exposed)
            AnchorPane contentPane = controller.getMainMenuPane();
            if (contentPane == null) {
                throw new RuntimeException("MainMenuPane not found in controller.");
            }

            // Design dimensions (ensure these are set in FXML)
            double designW = contentPane.getPrefWidth();
            double designH = contentPane.getPrefHeight();

            Scale scale = new Scale(1, 1);
            contentPane.getTransforms().add(scale);

            // Bind scale to scene dimensions
            scale.xProperty().bind(
                    Bindings.min(
                            scene.widthProperty().divide(designW),
                            scene.heightProperty().divide(designH)
                    )
            );
            scale.yProperty().bind(scale.xProperty()); // Uniform scaling

            stage.setScene(scene);
            stage.show();

        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            // Show error alert to user
        }
    }

    public void setSelectedSkin(String skin) {
        this.selectedSkin = skin;
    }

    @FXML
    private void startGame(ActionEvent event) {
        try {
            String aiStrategy = (String) typeChoiceBox.getValue();
            if (aiStrategy == null) {
                return;
            }

            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/gui/PokerGame.fxml")));
            StackPane root = loader.load();
            Scene scene = new Scene(root);
            AnchorPane contentPane;

            PokerGameGui controller = loader.getController();
            controller.setupGame(selectedSkin, aiStrategy, false);

            contentPane = controller.getGamePane(); // Expose via getter in PokerGameGui
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
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // reset state method
    @FXML
    public void resetState() {
        typeChoiceBox.getSelectionModel().clearSelection();
        offlineMenuPane.setVisible(true);
        startOnlineMenu.setVisible(true);
        backOnlineMenu.setVisible(true);
        // reset button
        backOnlineMenu.setDisable(false);
        startOnlineMenu.setDisable(false);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public AnchorPane getOfflineMenuPane() {
        return offlineMenuPane;
    }
}
