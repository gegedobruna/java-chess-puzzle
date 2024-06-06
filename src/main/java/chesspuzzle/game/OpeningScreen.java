package chesspuzzle.game;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.tinylog.Logger;
import util.SceneLoader;

public final class OpeningScreen {

    // FXML Fields
    @FXML
    private TextField nameField;
    @FXML
    private Button startGameButton;
    @FXML
    private Button viewScoreboardButton;

    // Start Game Method
    @FXML
    private void startGame(ActionEvent actionEvent) {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            Logger.warn("Name cannot be empty");
            nameField.clear();
        } else {
            try {
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                SceneLoader.loadScene("/game.fxml", stage, controller -> {
                    GameController gameController = (GameController) controller;
                    gameController.setPlayerName(name);
                });
                Logger.info("The user's name is set to {}, loading game scene", name);
            } catch (Exception e) {
                Logger.error("An unexpected exception occurred", e);
                SceneLoader.showErrorAlert("An unexpected error occurred. Please try again.");
            }
        }
    }

    // View Scoreboard Method
    @FXML
    private void viewScoreboard(ActionEvent actionEvent) {
        Logger.info("Navigating to Scoreboard");
        SceneLoader.loadScene("/scoreboard.fxml", (Stage) ((Node) actionEvent.getSource()).getScene().getWindow());
    }
}
