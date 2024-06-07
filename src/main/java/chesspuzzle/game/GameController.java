package chesspuzzle.game;

import chesspuzzle.model.ChessState;
import chesspuzzle.model.Position;
import chesspuzzle.results.GameResult;
import chesspuzzle.results.GameResultRepo;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import puzzle.TwoPhaseMoveState;
import util.OrdinalImageStorage;
import util.SceneLoader;
import util.javafx.ImageStorage;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class GameController {

    // Fields
    private final ImageStorage<Integer> imageStorage = new OrdinalImageStorage("/chesspieces",
            "king.png",
            "knight.png");
    private final IntegerProperty numberOfMoves = new SimpleIntegerProperty(0);
    private final ReadOnlyObjectWrapper<Position> highlightedPosition = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<Position> goalPosition = new ReadOnlyObjectWrapper<>(new Position(0, 6));
    private ChessState state;
    private final GameResultRepo gameResultRepository = GameResultRepo.getInstance();
    private String playerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean deadEndHandled = false;

    // FXML Fields
    @FXML
    private GridPane grid;
    @FXML
    private TextField numberOfMovesField;
    @FXML
    private Label nameLabel;
    @FXML
    private Button quitButton;
    @FXML
    private Button closeGameButton;
    @FXML
    private Button goToScoreboardButton;


    // Constructor
    public GameController() {
    }

    // Player Name Setter
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
        nameLabel.setText(playerName);
    }

    // Initialization Method
    @FXML
    private void initialize() {
        numberOfMovesField.textProperty().bind(numberOfMoves.asString());
        createState();
        clearAndPopulateGrid();
        registerKeyEventHandler();
        state.kingPositionProperty().addListener((observable, oldPosition, newPosition) -> clearAndPopulateGrid());
        state.knightPositionProperty().addListener((observable, oldPosition, newPosition) -> clearAndPopulateGrid());
        state.deadEndProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && !deadEndHandled && !state.isSolved()) {
                deadEndHandled = true;
                handleDeadEnd();
            }
        });
        startTime = LocalDateTime.now();
        setGridPaneStyle();
    }

    // State and Game Handling Methods
    private void createState() {
        state = new ChessState();
        state.solvedProperty().addListener(this::handleGameOver);
        highlightedPosition.set(null);
    }

    void restartGame() {
        createState();
        numberOfMoves.set(0);
        clearAndPopulateGrid();
        deadEndHandled = false;
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

    private void handleDeadEnd() {
        if (!deadEndHandled) {
            deadEndHandled = true;
            Platform.runLater(() -> {
                endTime = LocalDateTime.now();
                saveResult(false);
                showDeadEndAlert();
            });
        }
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

    // Alert Methods
    private void showDeadEndAlert() {
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Dead End");
        alert.setContentText("No moves can be made and no piece is under attack. Restarting the game.");
        alert.getButtonTypes().setAll(new javafx.scene.control.ButtonType("Restart"));
        alert.showAndWait().ifPresent(response -> restartGame());
    }

    private void showSolvedAlert() {
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Game Over");
        alert.setContentText("Congratulations, you have solved the puzzle!");
        closeGameButton.setVisible(true);
        goToScoreboardButton.setVisible(true);
        alert.showAndWait().ifPresent(response -> restartGame());
    }

    // Input Handling Methods
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
        Logger.info("Click on square ({},{})", row, col);
        handleClickOn(row, col);
    }

    private void handleClickOn(int row, int col) {
        Position clickedPosition = new Position(row, col);

        if (highlightedPosition.get() == null) {
            state.getPieceAt(row, col).ifPresent(pieceIndex -> {
                if (state.isLegalToMoveFrom(clickedPosition)) {
                    highlightedPosition.set(clickedPosition);
                    clearAndPopulateGrid();
                }
            });
        } else {
            TwoPhaseMoveState.TwoPhaseMove<Position> move = new TwoPhaseMoveState.TwoPhaseMove<>(highlightedPosition.get(), clickedPosition);
            if (state.isLegalMove(move)) {
                makeMove(move);
                if (!state.isSolved() && state.isDeadEnd()) {
                    showDeadEndAlert();
                }
            } else {
                Logger.error("Illegal move attempted: from {} to {}", highlightedPosition.get(), clickedPosition);
                highlightedPosition.set(null);
            }
            clearAndPopulateGrid();
        }
    }

    private void makeMove(TwoPhaseMoveState.TwoPhaseMove<Position> move) {
        state.makeMove(move);
        numberOfMoves.set(numberOfMoves.get() + 1);
        highlightedPosition.set(null);
        clearAndPopulateGrid();
    }

    // Grid and UI Methods
    private void clearAndPopulateGrid() {
        grid.getChildren().clear();

        for (var row = 0; row < ChessState.BOARD_SIZE; row++) {
            for (var col = 0; col < ChessState.BOARD_SIZE; col++) {
                grid.add(createSquare(row, col), col, ChessState.BOARD_SIZE - 1 - row);
            }
        }
    }

    @FXML
    private StackPane createSquare(int row, int col) {
        var square = new StackPane();
        var squarePane = createSquarePane(row, col);

        addPieceToSquare(row, col, squarePane);
        addBackgroundLabels(row, col, square, squarePane);

        square.setOnMouseClicked(this::handleMouseClick);
        return square;
    }

    @FXML
    private StackPane createSquarePane(int row, int col) {
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
        return squarePane;
    }

    @FXML
    private void addPieceToSquare(int row, int col, StackPane squarePane) {
        state.getPieceAt(row, col).flatMap(imageStorage::get).ifPresent(image -> {
            var imageView = new ImageView(image);
            imageView.getStyleClass().add("image-view");
            imageView.setVisible(true);
            squarePane.getChildren().add(imageView);
        });
    }

    @FXML
    private void addBackgroundLabels(int row, int col, StackPane square, StackPane squarePane) {
        var backgroundLabelRow = new Label();
        var backgroundLabelCol = new Label();

        if (col == 0) {
            backgroundLabelRow.setText(" " + (1 + row));
            backgroundLabelRow.getStyleClass().add("row-label");
            StackPane.setAlignment(backgroundLabelRow, Pos.TOP_LEFT);
        }
        if (row == 0) {
            backgroundLabelCol.setText((char) ('a' + col) + " ");
            backgroundLabelCol.getStyleClass().add("column-label");
            StackPane.setAlignment(backgroundLabelCol, Pos.BOTTOM_RIGHT);
        }

        if (col == 0 && row == 0) {
            square.getChildren().addAll(squarePane, backgroundLabelRow, backgroundLabelCol);
        } else {
            if (col == 0) {
                square.getChildren().addAll(squarePane, backgroundLabelRow);
            } else if (row == 0) {
                square.getChildren().addAll(squarePane, backgroundLabelCol);
            } else {
                square.getChildren().add(squarePane);
            }
        }
    }

    @FXML
    private void setGridPaneStyle() {
        grid.getStyleClass().add("grid-pane");
    }

    // Game Control Methods
    @FXML
    private void quitGame() {
        endTime = LocalDateTime.now();
        saveResult(false);
        Logger.info("Game quit by player: {}", playerName);
        SceneLoader.loadScene("/fxml/openingscreen.fxml", (Stage) quitButton.getScene().getWindow());
    }

    @FXML
    private void closeGame() {
        Logger.info("Game closed by player: {}", playerName);
        Platform.exit();
    }

    @FXML
    private void goToScoreboard() {
        Logger.info("Navigating to scoreboard by player: {}", playerName);
        SceneLoader.loadScene("/fxml/scoreboard.fxml", (Stage) goToScoreboardButton.getScene().getWindow());
    }
}
