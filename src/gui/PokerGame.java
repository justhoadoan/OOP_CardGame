package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class PokerGame {
    @FXML private ImageView communityCard1;
    @FXML private ImageView communityCard2;
    @FXML private ImageView communityCard3;
    @FXML private ImageView communityCard4;
    @FXML private ImageView communityCard5;
    @FXML private ImageView playerCard1;
    @FXML private ImageView playerCard2;
    //@FXML private HBox communityHBox;
    @FXML private TextField raiseField;
    @FXML private Slider raiseSlider;
    @FXML private StackPane opponentDeck;
    //@FXML private StackPane playerDeck;
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

        //raiseSlider.setMin(0);
        //raiseSlider.setMax(1000);
        //raiseSlider.setIncrement(10);

        // Update TextField when Slider moves
        raiseSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int val = newVal.intValue();
            raiseField.setText(String.valueOf(val));
        });

        // Update Slider when TextField is changed
        raiseField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                int val = Integer.parseInt(newVal);
                if (val >= raiseSlider.getMin() && val <= raiseSlider.getMax()) {
                    raiseSlider.setValue(val);
                }
            } catch (NumberFormatException e) {
                // Ignore invalid input (e.g. empty or non-numeric)
            }
        });

        // Optional: initialize with some default value
        raiseSlider.setValue(100);
    }
}
