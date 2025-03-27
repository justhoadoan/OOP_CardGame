package gui;

import card.CardSkin;
import gamemode.GameMode;
import games.GameType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import server.Client;


import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainFrame extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Card Game Menu");

        // Create Game selection ComboBox
        ComboBox<String> gameComboBox = new ComboBox<>();
        gameComboBox.getItems().addAll("Poker", "Blackjack");

        // Create Game Mode selection RadioButtons
        ToggleGroup gameModeGroup = new ToggleGroup();
        RadioButton graphicMode = new RadioButton("Graphic");
        RadioButton nonGraphicMode = new RadioButton("Non-Graphic");
        graphicMode.setToggleGroup(gameModeGroup);
        nonGraphicMode.setToggleGroup(gameModeGroup);

        // Create Card Skin selection ComboBox (only for Graphic Mode)
        ComboBox<String> cardSkinComboBox = new ComboBox<>();
        cardSkinComboBox.getItems().addAll("Traditional", "Realistic");
        cardSkinComboBox.setDisable(true); // Initially disabled

        // Create Game Type selection ComboBox
        ComboBox<String> gameTypeComboBox = new ComboBox<>();
        gameTypeComboBox.getItems().addAll("Server", "Client", "Offline");

        // Create Server IP and Port fields (only for Client Mode)
        TextField serverIpTextField = new TextField();
        serverIpTextField.setPromptText("Server IP");
        serverIpTextField.setDisable(true); // Initially disabled

        TextField serverPortTextField = new TextField();
        serverPortTextField.setPromptText("Server Port");
        serverPortTextField.setDisable(true); // Initially disabled

        // Create a label for the form title
        Text formTitle = new Text("Card Game Setup");
        formTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        formTitle.setFill(javafx.scene.paint.Color.WHITE);
        formTitle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // Set up layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        // Add form components to the layout
        layout.getChildren().add(formTitle);
        layout.getChildren().add(new Label("Game:"));
        layout.getChildren().add(gameComboBox);

        layout.getChildren().add(new Label("Game Mode:"));
        HBox gameModeBox = new HBox(10, graphicMode, nonGraphicMode);
        layout.getChildren().add(gameModeBox);

        layout.getChildren().add(new Label("Card Skin:"));
        layout.getChildren().add(cardSkinComboBox);

        layout.getChildren().add(new Label("Type:"));
        layout.getChildren().add(gameTypeComboBox);

        layout.getChildren().add(new Label("Server IP:"));
        layout.getChildren().add(serverIpTextField);
        layout.getChildren().add(new Label("Server Port:"));
        layout.getChildren().add(serverPortTextField);


        // Add listener for enabling/disabling fields based on user selection
        gameTypeComboBox.setOnAction(e -> {
            if (gameTypeComboBox.getValue().equals("Client")) {
                serverIpTextField.setDisable(false);
                serverPortTextField.setDisable(false);
            } else {
                serverIpTextField.setDisable(true);
                serverPortTextField.setDisable(true);
            }
        });

        gameModeGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldToggle, Toggle newToggle) {
                if (graphicMode.isSelected()) {
                    cardSkinComboBox.setDisable(false); // Enable card skin selection
                } else {
                    cardSkinComboBox.setDisable(true); // Disable card skin selection
                }
            }
        });

        // Create a Button to simulate the game start (can be customized further)
        Button startButton = new Button("Start Game");
        startButton.setOnAction(e -> {
            String selectedGame = gameComboBox.getValue();
            String selectedGameMode = gameModeGroup.getSelectedToggle() == graphicMode ? "Graphic" : "Non-Graphic";
            String selectedCardSkin = cardSkinComboBox.getValue();
            String selectedType = gameTypeComboBox.getValue();
            String serverIp = serverIpTextField.getText();
            String serverPort = serverPortTextField.getText();

            System.out.println("Starting Game...");
            System.out.println("Game: " + selectedGame);
            System.out.println("Mode: " + selectedGameMode);
            System.out.println("Card Skin: " + (selectedCardSkin != null ? selectedCardSkin : "N/A"));
            System.out.println("Type: " + selectedType);
            if ("Client".equals(selectedType)) {
                System.out.println("Server IP: " + serverIp);
                System.out.println("Server Port: " + serverPort);
            }
        });

        // Add the start button to the layout
        layout.getChildren().add(startButton);

        // Set up the scene
        Scene scene = new Scene(layout, 400, 400);
        File cssFile = new File("./OOP_CardGame/src/gui/style.css");
        scene.getStylesheets().add(cssFile.toURI().toString());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}