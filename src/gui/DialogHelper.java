package gui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

import java.util.Optional;

public class DialogHelper {
    /**
     * Ask the user if they want to continue, using the given owner window.
     * @param owner the Stage or Window that should own this dialog
     * @return true if they clicked OK, false otherwise
     */
    public static boolean askToContinue(Window owner) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(owner);                     // ← tie it to your window
        alert.setTitle("Continue?");
        alert.setHeaderText(null);
        alert.setContentText("Do you want to start a new game?");

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}