package gui;

import com.sun.javafx.stage.EmbeddedWindow;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.imageio.IIOException;
import java.io.IOException;

public class OnlineMenu {
    @FXML private ChoiceBox<String> positionChoiceBox;
    @FXML private Pane mainMenuPane;
    @FXML private Button backOnlineMenu;
    @FXML private Button startOnlineMenu;
    @FXML private Stage stage;
    @FXML private TextField serverIpField;
    @FXML private TextField serverPortField;

    private String selectedSkin;
    private String selectedGame;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setSelectedSkin(String skin) {
        this.selectedSkin = skin;
    }

    public void setSelectedGame(String game) {
        this.selectedGame = game;
    }
    @FXML
    public void initialize() {
        // Initialize the choice boxes with options
        serverPortField.setText("8888");
        positionChoiceBox.setItems(FXCollections.observableArrayList("Server", "Client"));
    }

    @FXML
    private void startGame(ActionEvent event) throws IOException {
        String position = positionChoiceBox.getValue();
        if (position == null || serverPortField.getText().isEmpty()) return;

        int port = Integer.parseInt(serverPortField.getText());
        String ip = serverIpField.getText();

        if (position.equals("Server")) {
            startServer(port);
        } else if (!ip.isEmpty()) {
            startClient(ip, port);
        }
    }
    private void startServer(int port) throws IOException {
        FXMLLoader loader;
        if (selectedGame.equals("Poker")) {
            loader = new FXMLLoader(getClass().getResource("PokerGame.fxml"));
            Parent root = loader.load();
            PokerGameGui controller = loader.getController();
            controller.setupServerGame(selectedSkin, port);
            stage.setScene(new Scene(root));
         //   controller.setupServerGame(selectedSkin, port);
            stage.show();
        }
    }

    private void startClient(String ip, int port) throws IOException {
        FXMLLoader loader;
        if (selectedGame.equals("Poker")) {
            loader = new FXMLLoader(getClass().getResource("PokerGame.fxml"));
            Parent root = loader.load();
            PokerGameGui controller = loader.getController();
            controller.setupClientGame(selectedSkin, ip, port);
            stage.setScene(new Scene(root));
            controller.setupClientGame(selectedSkin, ip, port);
            stage.show();
        }
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


    // reset state method
    @FXML
    public void resetState() {
        positionChoiceBox.getSelectionModel().clearSelection();
        mainMenuPane.setVisible(true);
        startOnlineMenu.setVisible(true);
        backOnlineMenu.setVisible(true);
        // reset button
        backOnlineMenu.setDisable(false);
        startOnlineMenu.setDisable(false);
    }
}