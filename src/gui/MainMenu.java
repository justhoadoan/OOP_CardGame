package gui;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenu {

    @FXML
    private StackPane rootPane;
    @FXML
    private Button nextMainMenu;
    @FXML
    private Pane skinPane;
    @FXML
    private AnchorPane mainMenuPane;
    @FXML
    private Pane multiplayerPane;
    @FXML
    private ChoiceBox<String> gameChoiceBox;
    @FXML
    private ChoiceBox<String> cardSkinChoiceBox;
    @FXML
    private ChoiceBox<String> gameModeChoiceBox;
    @FXML
    private ChoiceBox<String> multiplayerChoiceBox;

    private Stage stage;

    @FXML
    public void initialize() {
        // Initialize choice boxes
        gameChoiceBox.setItems(FXCollections.observableArrayList("Poker", "BlackJack"));
        cardSkinChoiceBox.setItems(FXCollections.observableArrayList("Traditional", "Realistic", "Animated"));
        gameModeChoiceBox.setItems(FXCollections.observableArrayList("Graphic", "Non-Graphic"));
        multiplayerChoiceBox.setItems(FXCollections.observableArrayList("1", "2", "3", "4"));

        // Initially hide optional panes
        skinPane.setVisible(false);
        multiplayerPane.setVisible(false);

        // Handle Graphic/Non-Graphic selection
        gameModeChoiceBox.setOnAction(e -> {
            skinPane.setVisible("Graphic".equals(gameModeChoiceBox.getValue()));
        });

        // Handle Poker/Blackjack selection
        gameChoiceBox.setOnAction(e -> {
            multiplayerPane.setVisible("Poker".equals(gameChoiceBox.getValue()));
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void Next() throws IOException {
            // Get stage from current scene
            Stage stage = (Stage) cardSkinChoiceBox.getScene().getWindow(); // Use any @FXML injected node

            FXMLLoader loader;
            AnchorPane contentPane; // The root element to scale
            Scene scene;

            if ("Online".equals(multiplayerChoiceBox.getValue())) {
                loader = new FXMLLoader(getClass().getResource("/gui/OnlineMenu.fxml"));
                StackPane rootPane = loader.load();
                scene = new Scene(rootPane);

                OnlineMenu controller = loader.getController();
                controller.resetState();
                contentPane = controller.getOnlineMenuPane(); // Expose via getter in OnlineMenu
            } else {
                loader = new FXMLLoader(getClass().getResource("/gui/PokerAIOffline.fxml"));
                StackPane rootPane = loader.load();
                scene = new Scene(rootPane);

                PokerAIOffline controller = loader.getController();
                controller.setSelectedSkin(cardSkinChoiceBox.getValue());
                controller.resetState();
                contentPane = controller.getOfflineMenuPane(); // Expose via getter in PokerAIOffline
            }

            // === SCALING LOGIC ===
            if (contentPane != null) {
                // Ensure design dimensions are set in FXML
                final double DESIGN_WIDTH = contentPane.getPrefWidth();
                final double DESIGN_HEIGHT = contentPane.getPrefHeight();

                Scale scale = new Scale(1, 1);
                contentPane.getTransforms().add(scale);

                // Uniform scaling binding
                scale.xProperty().bind(Bindings.min(
                        scene.widthProperty().divide(DESIGN_WIDTH),
                        scene.heightProperty().divide(DESIGN_HEIGHT)
                ));
                scale.yProperty().bind(scale.xProperty());
            } else {
                throw new RuntimeException("Main content pane not found in controller");
            }

            stage.setScene(scene);
            stage.show();
    }

    @FXML
    public void resetState() {
        gameChoiceBox.getSelectionModel().clearSelection();
        cardSkinChoiceBox.getSelectionModel().clearSelection();
        gameModeChoiceBox.getSelectionModel().clearSelection();
        multiplayerChoiceBox.getSelectionModel().clearSelection();
        skinPane.setVisible(false);
        multiplayerPane.setVisible(false);
        mainMenuPane.setVisible(true);
        nextMainMenu.setVisible(true);
        nextMainMenu.setDisable(false);
    }

    public AnchorPane getMainMenuPane() {
        return mainMenuPane;
    }
}
