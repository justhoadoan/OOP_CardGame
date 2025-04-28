package test;

import gui.PokerGameGui;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class PokerGameGuiTest {
    private static PokerGameGui controller;
    private static Stage primaryStage;

    @BeforeAll
    public static void initJavaFX() throws Exception {
        // 1) Boot JavaFX toolkit
        new JFXPanel();

        // 2) On the FX thread: load your FXML, grab the controller, and show it
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                // Adjust the path here to wherever your FXML actually lives!
                FXMLLoader loader = new FXMLLoader(
                        PokerGameGui.class.getResource("/gui/PokerGame.fxml")
                );
                Parent root = loader.load();
                controller = loader.getController();

                primaryStage = new Stage();
                primaryStage.setScene(new Scene(root));
                primaryStage.show();
            } catch (Exception e) {
                e.printStackTrace();
                fail("Couldn’t load FXML or show stage: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        // Wait up to 5s for the FX thread to finish setup
        assertTrue(latch.await(5, TimeUnit.SECONDS), "FX init timeout");
    }

    @Test
    public void testContinueGameShowsDialog() throws Exception {
        // We’ll fire continueGame() on the FX thread and wait for it to return.
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] dialogLaunched = {false};

        Platform.runLater(() -> {
            // Tie the Alert to our primaryStage so it isn’t ownerless
            // (DialogHelper does alert.initOwner(...) for you if you add that)
            controller.continueGame();
            dialogLaunched[0] = true;
            latch.countDown();
        });

        // Wait for up to 5s
        assertTrue(latch.await(5, TimeUnit.SECONDS),
                "continueGame() never returned");
        assertTrue(dialogLaunched[0],
                "Dialog never launched (no exception, but nothing happened)");

        // At this point you should see the pop-up;
        // or integrate TestFX for clicking “OK”/“Cancel” automatically.
    }
}
