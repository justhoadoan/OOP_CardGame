package gui;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
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

public class OnlineMenu {

    @FXML private StackPane rootPane;
    @FXML private ChoiceBox<String> positionChoiceBox;
    @FXML private AnchorPane onlineMenuPane;
    @FXML private Button backOnlineMenu;
    @FXML private Button startOnlineMenu;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        // Initialize the choice box with options
        positionChoiceBox.setItems(FXCollections.observableArrayList("Server", "Client"));
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

    @FXML
    public void resetState() {
        positionChoiceBox.getSelectionModel().clearSelection();
        onlineMenuPane.setVisible(true);
        startOnlineMenu.setVisible(true);
        backOnlineMenu.setVisible(true);
        backOnlineMenu.setDisable(false);
        startOnlineMenu.setDisable(false);
    }

    public AnchorPane getOnlineMenuPane() {
        return onlineMenuPane;
    }
}