package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainFrame extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/gui/MainMenu.fxml")));
        Pane mainMenuPane = loader.load();

        // Get the controller for MainMenu.fxml
        MainMenu mainMenuController = loader.getController();
        mainMenuController.setStage(primaryStage);

        primaryStage.setTitle("Card Game Menu");
        Scene scene = new Scene(mainMenuPane);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}