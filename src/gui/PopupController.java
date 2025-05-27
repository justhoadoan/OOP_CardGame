package gui;

import javafx.application.Platform;
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
        Platform.runLater(() -> {
            try {
                if (winnerText != null) {
                    winnerText.setText(winner);
                } else {
                    System.err.println("winnerText is null in PopupController");
                }
            } catch (Exception e) {
                System.err.println("Error setting winner text: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    private void closeWindow() {
        Stage stage = (Stage) againBtn.getScene().getWindow();
        stage.close();
    }
}