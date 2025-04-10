package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class PokerGame {
    @FXML private StackPane opponentDeck;
    @FXML private StackPane playerDeck;
    @FXML private Label player;
    @FXML private Label playerMoney;
    @FXML private Label opponentName;
    @FXML private Label opponentMoney;
    @FXML private Text potMoney;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        //int amount = 500;
        //potMoney.setText(String.format("$%d", amount));
        potMoney.setText("$500");
        opponentName.setText("Opponent1");
        opponentMoney.setText("$1000");
        player.setText("Player");
        playerMoney.setText("$1000");
    }
}
