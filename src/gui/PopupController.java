package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PopupController {
    @FXML private Button againBtn;
    @FXML private Button exitBtn;
    @FXML private Text gameState;
    @FXML private Text winnerText;

    private boolean playAgain = false;

    public boolean isPlayAgain() {
        return playAgain;
    }

    @FXML
    public void initialize() {
        playAgain = false;
        againBtn.setOnAction(event -> {
            playAgain = true;
            closeWindow();
        });

        exitBtn.setOnAction(event -> {
            playAgain = false;
            closeWindow();
        });
    }

    public void setWinnerText(String winner) {
        if (winner != null) {
            if (winner.contains("Tie")) {
                winnerText.setText("It's a tie!");
            } else {
                winnerText.setText("Winner: " + winner);
            }
        } else {
            winnerText.setText("Game Over");
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) againBtn.getScene().getWindow();
        stage.close();
    }
}