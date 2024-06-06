package chesspuzzle.game;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.tinylog.Logger;
import util.SceneLoader;

import java.io.IOException;

public final class OpeningScreen {
    @FXML
    private TextField nameField;
    @FXML
    private Label errorLabel;

    @FXML
    private Button startGameButton;
    @FXML
    private Button viewScoreboardButton;


    @FXML
    private void startGame(ActionEvent actionEvent) {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            Logger.warn("Name cannot be empty");
            nameField.clear();
        } else {
            try {
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/game.fxml"));
                Parent root = loader.load();
                GameController controller = loader.getController();
                controller.setPlayerName(name); // Pass the player's name to the GameController
                stage.setScene(new Scene(root));
                stage.show();
                Logger.info("The user's name is set to {}, loading game scene", name);
            } catch (IOException e) {
                Logger.error("Failed to load game.fxml", e);
                SceneLoader.showErrorAlert("Failed to load the game. Please try again.");
            } catch (Exception e) {
                Logger.error("An unexpected exception occurred", e);
                SceneLoader.showErrorAlert("An unexpected error occurred. Please try again.");
            }
        }
    }

    @FXML
    private void viewScoreboard(ActionEvent actionEvent) {
        Logger.info("Navigating to Scoreboard");
        SceneLoader.loadScene("/scoreboard.fxml", (Stage) ((Node) actionEvent.getSource()).getScene().getWindow());
    }
}
