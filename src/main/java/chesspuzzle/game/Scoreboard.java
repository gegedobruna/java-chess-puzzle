package chesspuzzle.game;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import chesspuzzle.results.GameResult;
import chesspuzzle.results.JsonGameResultManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.tinylog.Logger;
import util.SceneLoader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Scoreboard {

    @FXML
    private TableView<GameResult> tableView;
    @FXML
    private TableColumn<GameResult, Integer> rankColumn;
    @FXML
    private TableColumn<GameResult, String> playerNameColumn;
    @FXML
    private TableColumn<GameResult, Integer> stepsColumn;
    @FXML
    private TableColumn<GameResult, String> durationColumn;
    @FXML
    private TableColumn<GameResult, String> createdColumn;

    private final JsonGameResultManager gameResultManager = new JsonGameResultManager(Path.of("results.json"));

    @FXML
    private void initialize() {
        rankColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(tableView.getItems().indexOf(cellData.getValue()) + 1).asObject()
        );
        playerNameColumn.setCellValueFactory(new PropertyValueFactory<>("playerName"));
        stepsColumn.setCellValueFactory(new PropertyValueFactory<>("steps"));
        durationColumn.setCellValueFactory(cellData -> {
            Duration duration = cellData.getValue().getDuration();
            String formattedDuration = String.format("%d:%02d:%02d",
                    duration.toHours(),
                    duration.toMinutesPart(),
                    duration.toSecondsPart());
            return new javafx.beans.property.SimpleStringProperty(formattedDuration);
        });
        createdColumn.setCellValueFactory(cellData -> {
            ZonedDateTime date = cellData.getValue().getCreated();
            String formattedDate = date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy - hh:mm"));
            return new javafx.beans.property.SimpleStringProperty(formattedDate);
        });

        loadResults();
    }

    private void loadResults() {
        try {
            List<GameResult> results = gameResultManager.getBest(10);
            ObservableList<GameResult> observableResults = FXCollections.observableArrayList(results);
            tableView.setItems(observableResults);
        } catch (IOException e) {
            Logger.error(e, "Failed to load game results");
        }
    }

    @FXML
    private void goHome() {
        Logger.info("Navigating to home screen");
        SceneLoader.loadScene("/fxml/openingscreen.fxml", (Stage) tableView.getScene().getWindow());
    }
}
