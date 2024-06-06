package chesspuzzle.game;

import chesspuzzle.results.GameResult;
import chesspuzzle.results.GameResultRepo;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.tinylog.Logger;
import chesspuzzle.model.ChessState;
import chesspuzzle.model.Position;
import puzzle.TwoPhaseMoveState;
import util.OrdinalImageStorage;
import util.javafx.ImageStorage;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class GameController {

    private final ImageStorage<Integer> imageStorage = new OrdinalImageStorage("/chesspieces",
            "king.png",
            "knight.png");
    private final IntegerProperty numberOfMoves = new SimpleIntegerProperty(0);
    private final ReadOnlyObjectWrapper<Position> highlightedPosition = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<Position> goalPosition = new ReadOnlyObjectWrapper<>(new Position(0, 6)); // Example goal position (g1)
    @FXML
    private GridPane grid;
    @FXML
    private TextField numberOfMovesField;
    private ChessState state;

    private final GameResultRepo gameResultRepository = GameResultRepo.getInstance();

    // Change TextField for player name to Label
    @FXML
    private Label nameLabel;
    private String playerName;

    // Add necessary fields for tracking time
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @FXML
    private Button quitButton;  // Add quit button

    @FXML
    private Button closeGameButton; // Add button to close game after solving
    @FXML
    private Button goToScoreboardButton; // Add button to go to scoreboard after solving

    public GameController() {
    }

    @FXML
    private void initialize() {
        numberOfMovesField.textProperty().bind(numberOfMoves.asString());
        createState();
        clearAndPopulateGrid();
        registerKeyEventHandler();

        // Bind listeners to the king and knight positions
        state.kingPositionProperty().addListener((observable, oldPosition, newPosition) -> clearAndPopulateGrid());
        state.knightPositionProperty().addListener((observable, oldPosition, newPosition) -> clearAndPopulateGrid());

        // Initialize start time when game starts
        startTime = LocalDateTime.now();
        setGridPaneStyle();
    }

    void restartGame() {
        createState();
        numberOfMoves.set(0);
        clearAndPopulateGrid();
        startTime = LocalDateTime.now();
    }

    private void createState() {
        state = new ChessState();
        state.solvedProperty().addListener(this::handleGameOver);
        highlightedPosition.set(null);
    }

    private void handleGameOver(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
        Platform.runLater(() -> {
            if (newValue) {
                endTime = LocalDateTime.now();
                saveResult(true);
                showSolvedAlert();
            }
        });
    }

    private void showSolvedAlert() {
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Game Over");
        alert.setContentText("Congratulations, you have solved the puzzle!");
        closeGameButton.setVisible(true);
        goToScoreboardButton.setVisible(true);
        alert.showAndWait().ifPresent(response -> restartGame());
    }

    private void saveResult(boolean solved) {
        Logger.info("Saving result for player: {}", playerName);
        int steps = numberOfMoves.get();
        Duration duration = Duration.between(startTime, endTime);
        ZonedDateTime created = ZonedDateTime.now();

        GameResult result = new GameResult(playerName, solved, steps, duration, created);

        try {
            gameResultRepository.add(result);
        } catch (IOException e) {
            Logger.error("Error saving game result", e);
        }
    }

    @FXML
    private void handleKeyPress(KeyEvent keyEvent) {
        var restartKeyCombination = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
        var quitKeyCombination = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
        if (restartKeyCombination.match(keyEvent)) {
            Logger.debug("Restarting game");
            restartGame();
        } else if (quitKeyCombination.match(keyEvent)) {
            Logger.debug("Exiting");
            quitGame();
        }
    }

    private void registerKeyEventHandler() {
        Platform.runLater(() -> grid.getScene().setOnKeyPressed(this::handleKeyPress));
    }

    @FXML
    private void handleMouseClick(MouseEvent event) {
        var source = (Node) event.getSource();
        var row = ChessState.BOARD_SIZE - 1 - GridPane.getRowIndex(source);
        var col = GridPane.getColumnIndex(source);
        Logger.debug("Click on square ({},{})", row, col);

        handleClickOn(row, col);
    }

    private void handleClickOn(int row, int col) {
        Position clickedPosition = new Position(row, col);

        if (highlightedPosition.get() == null) { // No piece is currently selected
            state.getPieceAt(row, col).ifPresent(pieceIndex -> {
                if (state.isLegalToMoveFrom(clickedPosition)) {  // Check if the piece is under attack
                    highlightedPosition.set(clickedPosition);
                    clearAndPopulateGrid(); // Update to show possible moves
                }
            });
        } else {
            TwoPhaseMoveState.TwoPhaseMove<Position> move = new TwoPhaseMoveState.TwoPhaseMove<>(highlightedPosition.get(), clickedPosition);
            if (state.isLegalMove(move)) {
                makeMove(move);
            } else {
                Logger.error("Illegal move attempted: from {} to {}", highlightedPosition.get(), clickedPosition);
                highlightedPosition.set(null); // Deselect the piece
            }
            clearAndPopulateGrid(); // Update the grid after a move (or invalid move)
        }
    }

    private void makeMove(TwoPhaseMoveState.TwoPhaseMove<Position> move) {
        state.makeMove(move);
        numberOfMoves.set(numberOfMoves.get() + 1);
        highlightedPosition.set(null); // Deselect after the move
        clearAndPopulateGrid(); // Ensure the grid is updated after the move
    }

    private void clearAndPopulateGrid() {
        grid.getChildren().clear();

        for (var row = 0; row < ChessState.BOARD_SIZE; row++) {
            for (var col = 0; col < ChessState.BOARD_SIZE; col++) {
                grid.add(createSquare(row, col), col, ChessState.BOARD_SIZE - 1 - row);
            }
        }
    }

    @FXML
    private void setGridPaneStyle() {
        grid.getStyleClass().add("grid-pane");
    }

    @FXML
    private StackPane createSquare(int row, int col) {
        var square = new StackPane();

        // Create the background square pane
        var squarePane = new StackPane();
        if (col % 2 == row % 2) {
            squarePane.getStyleClass().add("light-square");
        } else {
            squarePane.getStyleClass().add("dark-square");
        }

        Position currentPosition = new Position(row, col);
        if (currentPosition.equals(highlightedPosition.get())) {
            squarePane.getStyleClass().add("highlighted-square");
        } else if (state.isLegalToMoveFrom(currentPosition)) {
            squarePane.getStyleClass().add("legal-move-square");
        } else if (currentPosition.equals(goalPosition.get())) {
            squarePane.getStyleClass().add("goal-square");
        }

        state.getPieceAt(row, col).flatMap(imageStorage::get).ifPresent(image -> {
            var imageView = new ImageView(image);
            imageView.getStyleClass().add("image-view");
            imageView.setVisible(true);
            squarePane.getChildren().add(imageView);
        });

        // Add background labels for row and column indicators
        var backgroundLabelRow = new Label();
        var backgroundLabelCol = new Label();

        if (col == 0) {  // First column for row indicators
            backgroundLabelRow.setText(" " + (1 + row));
            backgroundLabelRow.getStyleClass().add("row-label");
            StackPane.setAlignment(backgroundLabelRow, Pos.TOP_LEFT);
        }
        if (row == 0) {  // Last row for column indicators
            backgroundLabelCol.setText((char) ('a' + col) + " ");
            backgroundLabelCol.getStyleClass().add("column-label");
            StackPane.setAlignment(backgroundLabelCol, Pos.BOTTOM_RIGHT);
        }

        // Check for the corner square and add both labels if it is the corner
        if (col == 0 && row == 0) {
            square.getChildren().addAll(squarePane, backgroundLabelRow, backgroundLabelCol);
        } else {
            // Add the appropriate background label(s) and the squarePane to the square
            if (col == 0) {
                square.getChildren().addAll(squarePane, backgroundLabelRow);
            } else if (row == 0) {
                square.getChildren().addAll(squarePane, backgroundLabelCol);
            } else {
                square.getChildren().addAll(squarePane);
            }
        }

        square.setOnMouseClicked(this::handleMouseClick);
        return square;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
        nameLabel.setText(playerName);
    }

    @FXML
    private void quitGame() {
        endTime = LocalDateTime.now();
        saveResult(false);
        Logger.info("Game quit by player: {}", playerName);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/openingscreen.fxml"));
            Stage stage = (Stage) quitButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            Logger.error("Failed to load openingscreen.fxml", e);
        }
    }

    @FXML
    private void closeGame() {
        Logger.info("Game closed by player: {}", playerName);
        Platform.exit();
    }

    @FXML
    private void goToScoreboard() {
        Logger.info("Navigating to scoreboard by player: {}", playerName);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scoreboard.fxml"));
            Stage stage = (Stage) goToScoreboardButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            Logger.error("Failed to load scoreboard.fxml", e);
        }
    }
}
