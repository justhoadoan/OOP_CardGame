package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PopupController {
    @FXML private Button againBtn;
    @FXML private Button exitBtn;
    @FXML private Text gameState;

    private boolean playAgain = false;

    public boolean isPlayAgain() {
        return playAgain;
    }

    @FXML
    public void initialize() {
        playAgain = false;
        againBtn.setOnAction(event -> {
            System.out.println("Button pressed");
            playAgain = true;
            closeWindow();
        });

        exitBtn.setOnAction(event -> {
            System.out.println("Button pressed");
            playAgain = false;
            closeWindow();
        });
    }

    private void closeWindow() {
        Stage stage = (Stage) againBtn.getScene().getWindow();
        stage.close();
    }
}