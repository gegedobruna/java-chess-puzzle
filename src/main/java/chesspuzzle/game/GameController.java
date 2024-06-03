package chesspuzzle.game;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import chesspuzzle.model.ChessState;
import chesspuzzle.model.Position;
import puzzle.TwoPhaseMoveState;
import util.OrdinalImageStorage;
import util.javafx.ImageStorage;

public class GameController {

    private final ImageStorage<Integer> imageStorage = new OrdinalImageStorage("/chesspieces",
            "king.png",
            "knight.png");

    @FXML
    private GridPane grid;
    @FXML
    private TextField numberOfMovesField;

    private ChessState state;
    private final IntegerProperty numberOfMoves = new SimpleIntegerProperty(0);
    private final ReadOnlyObjectWrapper<Position> highlightedPosition = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<Position> goalPosition = new ReadOnlyObjectWrapper<>();

    @FXML
    private void initialize() {
        // TODO: Bind numberOfMovesField to numberOfMoves
        // TODO: Create initial game state
        // TODO: Clear and populate grid
        // TODO: Add listeners to state properties
    }

    private void restartGame() {
        // TODO: Create initial game state
        // TODO: Reset number of moves
        // TODO: Clear and populate grid
    }

    private void createState() {
        // TODO: Initialize state with a new ChessState
        // TODO: Reset highlightedPosition to null
    }

    private void handleSolved(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
        // TODO: If puzzle is solved (newValue is true), show solved alert
    }

    private void showSolvedAlert() {
        // TODO: Create an alert with "Game Over" header
        // TODO: Set content text to "Congratulations, you have solved the puzzle!"
        // TODO: Show the alert and wait for user response
        // TODO: Restart the game
    }

    @FXML
    private void handleKeyPress(KeyEvent keyEvent) {
        // TODO: Check if Ctrl+R is pressed to restart the game
        // TODO: Check if Ctrl+Q is pressed to exit the application
    }

    @FXML
    private void handleMouseClick(MouseEvent event) {
        // TODO: Get source node from event
        // TODO: Calculate row and column from GridPane indices
        // TODO: Log the click position
        // TODO: Handle the click on the specific row and column
    }

    private void handleClickOn(int row, int col) {
        // TODO: Create position object from row and col
        // TODO: If no piece is selected, check if clicked position has a piece that can be moved
        // TODO: If a piece is selected, check if move is legal and make the move
        // TODO: Clear and populate grid
    }

    private void makeMove(TwoPhaseMoveState.TwoPhaseMove<Position> move) {
        // TODO: Make the move in the game state
        // TODO: Increment the number of moves
        // TODO: Deselect the highlighted piece
    }

    private void clearAndPopulateGrid() {
        // TODO: Clear all children from the grid
        // TODO: Iterate over board rows and columns
        // TODO: Create and add square to the grid for each position
    }

    private StackPane createSquare(int row, int col) {
        // TODO: Create a new StackPane
        // TODO: Apply CSS class for light or dark square
        // TODO: Highlight square if it is selected or can move
        // TODO: Add piece image to square if present
        // TODO: Set click handler for the square
        // TODO: Return the square
        return null;
    }
}