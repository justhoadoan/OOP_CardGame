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
    @FXML private ChoiceBox positionChoiceBox;

    @FXML
    public void initialize() {
        positionChoiceBox.getItems().addAll("Rule based", "Monte Carlo");
    }

    @FXML
    private void backToMainMenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }
}
