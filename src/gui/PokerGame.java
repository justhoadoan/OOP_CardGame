package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class PokerGame {
    @FXML private ImageView communityCard1;
    @FXML private ImageView communityCard2;
    @FXML private ImageView communityCard3;
    @FXML private ImageView communityCard4;
    @FXML private ImageView communityCard5;
    @FXML private Pane player1CardArea;
    @FXML private Pane player2CardArea;
    @FXML private Pane player3CardArea;
    @FXML private Pane player4CardArea;
    @FXML private ImageView player1Card1;
    @FXML private ImageView player1Card2;
    @FXML private ImageView player2Card1;
    @FXML private ImageView player2Card2;
    @FXML private ImageView player3Card1;
    @FXML private ImageView player3Card2;
    @FXML private ImageView player4Card1;
    @FXML private ImageView player4Card2;
    //@FXML private HBox communityHBox;
    @FXML private TextField raiseField;
    @FXML private Slider raiseSlider;
    @FXML private Label player1Name;
    @FXML private Label player1Money;
    @FXML private Label player2Name;
    @FXML private Label player2Money;
    @FXML private Label player3Name;
    @FXML private Label player3Money;
    @FXML private Label player4Name;
    @FXML private Label player4Money;
    @FXML private Text potMoney;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        //int amount = 500;
        //potMoney.setText(String.format("$%d", amount));
        potMoney.setText("$50000");
        player2Name.setText("Player 2");
        player2Money.setText("$1000");
        player1Name.setText("Player 1");
        player1Money.setText("$1000");
        player3CardArea.setVisible(false);
        player4CardArea.setVisible(false);

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
