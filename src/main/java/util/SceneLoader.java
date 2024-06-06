package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Utility class for loading scenes.
 */
public class SceneLoader {

    /**
     * Loads the scene from the given FXML file and sets it to the given stage.
     *
     * @param fxmlPath the path of the FXML file
     * @param stage the stage to set the scene
     */
    public static void loadScene(String fxmlPath, Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource(fxmlPath));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            Logger.error("Failed to load " + fxmlPath, e);
            showErrorAlert("Failed to load the scene. Please try again.");
        }
    }

    /**
     * Loads the scene from the given FXML file, sets it to the given stage and initializes the controller.
     *
     * @param fxmlPath the path of the FXML file
     * @param stage the stage to set the scene
     * @param controllerInitializer the controller initializer
     */
    public static void loadScene(String fxmlPath, Stage stage, Consumer<Object> controllerInitializer) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource(fxmlPath));
            Parent root = loader.load();
            controllerInitializer.accept(loader.getController());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            Logger.error("Failed to load " + fxmlPath, e);
            showErrorAlert("Failed to load the scene. Please try again.");
        }
    }

    /**
     * Shows an error alert with the given message.
     *
     * @param message the message of the alert
     */
    public static void showErrorAlert(String message) {
        var alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
