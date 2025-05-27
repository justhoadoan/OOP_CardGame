package gui;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

// MainFrame class extends the JavaFX Application class to create the main GUI window
public class MainFrame extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        // Load the FXML file for the main menu
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/gui/MainMenu.fxml")));

        // Load the StackPane (the true FXML root)
        StackPane rootPane = loader.load();

        // Retrieve the child AnchorPane by its fx:id="mainMenuPane"
        AnchorPane mainMenuPane = (AnchorPane) loader.getNamespace().get("mainMenuPane");

        // Get the controller for the main menu and set the primary stage
        MainMenuController mainMenuController = loader.getController();
        mainMenuController.setStage(primaryStage);

        // Create a scene with the loaded FXML pane and set it to the primary stage
        Scene scene = new Scene(rootPane);
        primaryStage.setScene(scene);
        // Set the title of the primary stage
        primaryStage.setTitle("Card Game Menu");

        // design-time size, matching the FXML prefWidth/prefHeight:
        double designW = mainMenuPane.getPrefWidth();
        double designH = mainMenuPane.getPrefHeight();

        // Create a Scale transform object with initial scale factors of 1 (no scaling)
        Scale scale = new Scale(1, 1);

        // Apply the Scale transform to the mainMenuPane so that it can be dynamically resized
        mainMenuPane.getTransforms().add(scale);

        // Create a DoubleBinding that dynamically calculates the scale factor
        // This binding will automatically update whenever the scene’s width or height changes
        // It ensures the UI scales proportionally (uniformly) to fit the window without cropping
        DoubleBinding scaleFactor = Bindings.createDoubleBinding(() -> {
            double widthRatio  = scene.getWidth()  / designW; // Ratio of current width to original design width
            double heightRatio = scene.getHeight() / designH; // Ratio of current height to original design height

            // Use the smaller of the two ratios to avoid stretching or cropping
            return Math.min(widthRatio, heightRatio);
        }, scene.widthProperty(), scene.heightProperty()); // These properties trigger re-evaluation when changed

        // Bind the scale's X and Y properties to the computed scaleFactor
        // This ensures both dimensions scale uniformly whenever the window resizes
        // The bind method links the property to the binding, so updates happen automatically
        scale.xProperty().bind(scaleFactor);
        scale.yProperty().bind(scaleFactor);

        // Display the primary stage
        primaryStage.show();
    }
    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}