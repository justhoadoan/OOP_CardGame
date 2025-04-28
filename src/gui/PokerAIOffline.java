package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class PokerAIOffline {
    @FXML private Pane mainMenuPane;
    @FXML private Button backOnlineMenu;
    @FXML private Button startOnlineMenu;
    @FXML private ChoiceBox typeChoiceBox;
    @FXML private Stage stage;

    @FXML
    public void initialize() {
        // Initialize the choice boxes with options
        typeChoiceBox.getItems().addAll("Rule based", "Monte Carlo");
    }

    @FXML
    private void backToMainMenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
        Parent root = loader.load();
        MainMenu controller = loader.getController();

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        controller.setStage(stage);
        controller.resetState();
    }

    // reset state method
    @FXML
    public void resetState() {
        typeChoiceBox.getSelectionModel().clearSelection();
        mainMenuPane.setVisible(true);
        startOnlineMenu.setVisible(true);
        backOnlineMenu.setVisible(true);
        // reset button
        backOnlineMenu.setDisable(false);
        startOnlineMenu.setDisable(false);
    }
}
