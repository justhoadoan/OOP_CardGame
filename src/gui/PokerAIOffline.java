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
    private String selectedSkin;
    @FXML
    public void initialize() {
        // Initialize the choice boxes with options
        typeChoiceBox.setValue("Rule based");
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

        controller.setStage(stage); // important!
        controller.resetState();    // now it's safe to call
    }


    public void setSelectedSkin(String skin) {
        this.selectedSkin = skin;
    }

    @FXML
    private void startGame(ActionEvent event) throws IOException {
        String aiStrategy = (String) typeChoiceBox.getValue();
        if (aiStrategy == null) {
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("PokerGame.fxml"));
        Parent root = loader.load();

        PokerGameGui controller = loader.getController();
        controller.setupGame(selectedSkin, aiStrategy, false);

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
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
