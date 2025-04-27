package gui;

    import javafx.application.Application;
    import javafx.beans.binding.Bindings;
    import javafx.beans.binding.DoubleBinding;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Scene;
    import javafx.scene.layout.AnchorPane;
    import javafx.scene.layout.Pane;
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

           // 1) Load the StackPane (the true FXML root)
           StackPane rootPane = loader.load();

           // 2) Retrieve the child AnchorPane by its fx:id="mainMenuPane"
           AnchorPane mainMenuPane = (AnchorPane) loader.getNamespace().get("mainMenuPane");

           // Get the controller for the main menu and set the primary stage
           MainMenu mainMenuController = loader.getController();
           mainMenuController.setStage(primaryStage);

           // Create a scene with the loaded FXML pane and set it to the primary stage
           Scene scene = new Scene(rootPane);
           primaryStage.setScene(scene);
           // Set the title of the primary stage
           primaryStage.setTitle("Card Game Menu");

           // design-time size, matching the FXML prefWidth/prefHeight:
           double designW = mainMenuPane.getPrefWidth();
           double designH = mainMenuPane.getPrefHeight();

            // create scaleTransform and add it to the game pane
           Scale scale = new Scale(1, 1);
           mainMenuPane.getTransforms().add(scale);

           // 6) bind a uniform scale factor = min(widthRatio, heightRatio)
           //    so it is always the largest scale that fits without cropping
           DoubleBinding scaleFactor = Bindings.createDoubleBinding(() -> {
               double widthRatio  = scene.getWidth()  / designW;
               double heightRatio = scene.getHeight() / designH;
               return Math.min(widthRatio, heightRatio);
           }, scene.widthProperty(), scene.heightProperty());

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