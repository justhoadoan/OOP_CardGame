package gui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class BlackJackGame extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create "Hit" Button
        Button hitButton = new Button("Hit");
        hitButton.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        hitButton.setOnAction(e -> handleHit());

        // Create "Stand" Button
        Button standButton = new Button("Stand");
        standButton.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        standButton.setOnAction(e -> handleStand());

        // Add buttons to HBox (horizontal layout)
        HBox buttonBox = new HBox(20, hitButton, standButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Set up the scene
        Scene scene = new Scene(buttonBox, 400, 100);
        primaryStage.setTitle("Blackjack");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @FXML
    private void handleHit() {
        System.out.println("Player hits!");
        // Logic for drawing a new card
    }

    @FXML
    private void handleStand() {
        System.out.println("Player stands!");
        // Logic for ending the player's turn
    }

    public static void main(String[] args) {
        launch(args);
    }
}