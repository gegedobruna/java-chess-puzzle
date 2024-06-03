package chesspuzzle.game;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.tinylog.Logger;
import chesspuzzle.model.ChessState;
import chesspuzzle.model.Position;
import puzzle.TwoPhaseMoveState;
import util.OrdinalImageStorage;
import util.javafx.ImageStorage;

public class GameController {

    private final ImageStorage<Integer> imageStorage = new OrdinalImageStorage("/chesspieces",
            "king.png",
            "knight.png");
    private final IntegerProperty numberOfMoves = new SimpleIntegerProperty(0);
    private final ReadOnlyObjectWrapper<Position> highlightedPosition = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<Position> goalPosition = new ReadOnlyObjectWrapper<>(new Position(0, 6));
    @FXML
    private GridPane grid;
    @FXML
    private TextField numberOfMovesField;
    private ChessState state;

    @FXML
    private void initialize() {
        numberOfMovesField.textProperty().bind(numberOfMoves.asString());
        createState();
        clearAndPopulateGrid();
        registerKeyEventHandler();
        state.kingPositionProperty().addListener((observable, oldPosition, newPosition) -> clearAndPopulateGrid());
        state.knightPositionProperty().addListener((observable, oldPosition, newPosition) -> clearAndPopulateGrid());
    }

    private void restartGame() {
        createState();
        numberOfMoves.set(0);
        clearAndPopulateGrid();
    }

    private void createState() {
        state = new ChessState();
        state.solvedProperty().addListener(this::handleSolved);
        highlightedPosition.set(null);
    }

    private void handleSolved(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
        if (newValue) {
            Platform.runLater(this::showSolvedAlert);
        }
    }

    private void showSolvedAlert() {
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Game Over");
        alert.setContentText("Congratulations, you have solved the puzzle!");
        alert.showAndWait().ifPresent(response -> restartGame());
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
            Platform.exit();
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

    private void clearAndPopulateGrid() {
        grid.getChildren().clear();

        for (var row = 0; row < ChessState.BOARD_SIZE; row++) {
            for (var col = 0; col < ChessState.BOARD_SIZE; col++) {
                grid.add(createSquare(row, col), col, ChessState.BOARD_SIZE - 1 - row);
            }
        }
    }

    private StackPane createSquare(int row, int col) {
        var square = new StackPane();

        if (col % 2 == row % 2) {
            square.getStyleClass().add("light-square");
        } else {
            square.getStyleClass().add("dark-square");
        }

        Position currentPosition = new Position(row, col);
        if (currentPosition.equals(highlightedPosition.get())) {
            square.getStyleClass().add("highlighted-square");
        } else if (state.isLegalToMoveFrom(currentPosition)) {
            square.getStyleClass().add("legal-move-square");
        } else if (currentPosition.equals(goalPosition.get())) {
            square.getStyleClass().add("goal-square");
        }

        state.getPieceAt(row, col).flatMap(imageStorage::get).ifPresent(image -> {
            var imageView = new ImageView(image);
            imageView.getStyleClass().add("image-view");
            imageView.setVisible(true);
            square.getChildren().add(imageView);
        });

        square.setOnMouseClicked(this::handleMouseClick);
        return square;
    }
}
