package gui;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

public class PopupController {
    @FXML
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}