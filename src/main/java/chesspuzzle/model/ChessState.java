package chesspuzzle.model;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import lombok.Getter;
import puzzle.TwoPhaseMoveState;

import java.util.*;

/**
 * Represents the state of the chess puzzle.
 */
@Getter
public class ChessState implements TwoPhaseMoveState<Position> {
    /**
     * The size of the chess board.
     */
    public static final int BOARD_SIZE = 8;

    /**
     * The goal position for the king or knight.
     */
    public static final Position goalPosition = new Position(0, 6);

    private final ReadOnlyObjectWrapper<Position> kingPosition = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<Position> knightPosition = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyBooleanWrapper deadEnd = new ReadOnlyBooleanWrapper();
    private final ReadOnlyBooleanWrapper solved = new ReadOnlyBooleanWrapper();
    private final List<String[]> board;

    /**
     * Constructs a ChessState with default initial positions for the king and knight.
     */
    public ChessState() {
        this(new Position(2, 1), new Position(2, 2));
    }

    /**
     * Constructs a ChessState with specified initial positions for the king and knight.
     *
     * @param kingPosition   the initial position of the king
     * @param knightPosition the initial position of the knight
     */
    public ChessState(Position kingPosition, Position knightPosition) {
        this.kingPosition.set(kingPosition);
        this.knightPosition.set(knightPosition);
        this.board = new ArrayList<>(BOARD_SIZE);
        initializeBoard();
        solved.bind(this.kingPosition.isEqualTo(goalPosition).or(this.knightPosition.isEqualTo(goalPosition)));
        checkDeadEndState();
    }

    private void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            String[] row = new String[BOARD_SIZE];
            Arrays.fill(row, ".");
            board.add(row);
        }
        board.get(kingPosition.get().row())[kingPosition.get().col()] = "K";
        board.get(knightPosition.get().row())[knightPosition.get().col()] = "N";
    }

    /**
     * Retrieves the piece at the specified position on the board.
     *
     * @param row the row index of the board
     * @param col the column index of the board
     * @return an Optional containing the piece identifier if present (0 for king, 1 for knight), otherwise an empty Optional
     */
    public Optional<Integer> getPieceAt(int row, int col) {
        String piece = board.get(row)[col];
        return switch (piece) {
            case "K" -> Optional.of(0);
            case "N" -> Optional.of(1);
            default -> Optional.empty();
        };
    }

    /**
     * Checks if a given position is valid on the board.
     *
     * @param position the position to check
     * @return true if the position is valid, false otherwise
     */
    public boolean isValid(Position position) {
        return position.row() >= 0 && position.row() < BOARD_SIZE && position.col() >= 0 && position.col() < BOARD_SIZE;
    }

    /**
     * Checks if a move from a given position to another position is valid for the king.
     *
     * @param from the starting position
     * @param to the target position
     * @return true if the move is valid, false otherwise
     */
    public boolean isValidKingMove(Position from, Position to) {
        int dr = Math.abs(from.row() - to.row());
        int dc = Math.abs(from.col() - to.col());
        return dr <= 1 && dc <= 1;
    }

    /**
     * Checks if a move from a given position to another position is valid for the knight.
     *
     * @param from the starting position
     * @param to the target position
     * @return true if the move is valid, false otherwise
     */
    public boolean isValidKnightMove(Position from, Position to) {
        int dr = Math.abs(from.row() - to.row());
        int dc = Math.abs(from.col() - to.col());
        return (dr == 2 && dc == 1) || (dr == 1 && dc == 2);
    }

    /**
     * Checks if a given position is under attack by another piece.
     *
     * @param piecePosition the position of the piece to check
     * @param attackerPosition the position of the attacking piece
     * @return true if the piece is under attack, false otherwise
     */
    public boolean isUnderAttack(Position piecePosition, Position attackerPosition) {
        if (piecePosition.equals(kingPosition.get())) {
            return isValidKnightMove(attackerPosition, piecePosition);
        } else if (piecePosition.equals(knightPosition.get())) {
            return isValidKingMove(attackerPosition, piecePosition);
        }
        return false;
    }

    /**
     * Returns whether the current state is solved (i.e., the king or knight has reached the goal position).
     *
     * @return true if the state is solved, false otherwise
     */
    @Override
    public boolean isSolved() {
        return solved.get();
    }

    private void checkDeadEndState() {
        Set<TwoPhaseMove<Position>> legalMoves = getLegalMoves();
        deadEnd.set(legalMoves.isEmpty());
    }

    /**
     * Checks if moving from a given position is legal according to the game rules.
     *
     * @param position the position to move from
     * @return true if it is legal to move from the specified position, false otherwise
     */
    @Override
    public boolean isLegalToMoveFrom(Position position) {
        if (position.equals(kingPosition.get())) {
            return isUnderAttack(kingPosition.get(), knightPosition.get());
        } else if (position.equals(knightPosition.get())) {
            return isUnderAttack(knightPosition.get(), kingPosition.get());
        }
        return false;
    }

    /**
     * Checks if a given move is legal according to the game rules.
     *
     * @param move the move to check
     * @return true if the move is legal, false otherwise
     */
    @Override
    public boolean isLegalMove(TwoPhaseMove<Position> move) {
        Position from = move.from();
        Position to = move.to();
        if (!isValid(to)) {
            return false;
        } else {
            boolean validMove = (from.equals(knightPosition.get()) && isValidKnightMove(knightPosition.get(), to)) ||
                    (from.equals(kingPosition.get()) && isValidKingMove(kingPosition.get(), to));

            return validMove && isLegalToMoveFrom(from);
        }
    }

    /**
     * Executes a given move, updating the state of the board.
     *
     * @param move the move to execute
     * @throws IllegalArgumentException if the move is not legal
     */
    @Override
    public void makeMove(TwoPhaseMove<Position> move) {
        if (!isLegalMove(move)) {
            throw new IllegalArgumentException("Illegal move: " + move);
        }

        Position from = move.from();
        Position to = move.to();
        if (from.equals(kingPosition.get())) {
            board.get(kingPosition.get().row())[kingPosition.get().col()] = ".";
            kingPosition.set(to);
            board.get(kingPosition.get().row())[kingPosition.get().col()] = "K";
        } else if (from.equals(knightPosition.get())) {
            board.get(knightPosition.get().row())[knightPosition.get().col()] = ".";
            knightPosition.set(to);
            board.get(knightPosition.get().row())[knightPosition.get().col()] = "N";
        }
        checkDeadEndState();
    }

    /**
     * Retrieves a set of all legal moves available in the current state.
     *
     * @return a set of legal moves
     */
    @Override
    public Set<TwoPhaseMove<Position>> getLegalMoves() {
        Set<TwoPhaseMove<Position>> legalMoves = new HashSet<>();
        collectKingMoves(legalMoves);
        collectKnightMoves(legalMoves);
        deadEnd.set(legalMoves.isEmpty());
        return legalMoves;

    }

    private void collectKingMoves(Set<TwoPhaseMove<Position>> legalMoves) {
        for (Direction direction : Direction.values()) {
            if (direction.isKingMove()) {
                Position newKingPosition = kingPosition.get().move(direction);
                TwoPhaseMove<Position> move = new TwoPhaseMove<>(kingPosition.get(), newKingPosition);
                if (isLegalMove(move)) {
                    legalMoves.add(move);
                }
            }
        }
    }

    private void collectKnightMoves(Set<TwoPhaseMove<Position>> legalMoves) {
        for (Direction direction : Direction.values()) {
            if (direction.isKnightMove()) {
                Position newKnightPosition = knightPosition.get().move(direction);
                TwoPhaseMove<Position> move = new TwoPhaseMove<>(knightPosition.get(), newKnightPosition);
                if (isLegalMove(move)) {
                    legalMoves.add(move);
                }
            }
        }
    }

    /**
     * Creates a copy of the current ChessState.
     *
     * @return a clone of the current ChessState
     */
    @Override
    public ChessState clone() {
        return new ChessState(kingPosition.get(), knightPosition.get());
    }

    /**
     * Checks if this ChessState is equal to another object.
     *
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessState other)) {
            return false;
        }
        return kingPosition.get().equals(other.kingPosition.get()) &&
                knightPosition.get().equals(other.knightPosition.get());
    }

    /**
     * Computes the hash code for this ChessState.
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(kingPosition.get(), knightPosition.get());
    }

    /**
     * Returns a string representation of the ChessState.
     * @return a string representation of the ChessState
     */
    @Override
    public String toString() {
        return "King: " + kingPosition.get() + ", Knight: " + knightPosition.get();
    }

    /**
     * Returns the property representing the king's position.
     *
     * @return the ReadOnlyObjectWrapper for the king's position
     */
    public ReadOnlyObjectWrapper<Position> kingPositionProperty() {
        return kingPosition;
    }

    /**
     * Returns the property representing the knight's position.
     *
     * @return the ReadOnlyObjectWrapper for the knight's position
     */
    public ReadOnlyObjectWrapper<Position> knightPositionProperty() {
        return knightPosition;
    }

    /**
     * Returns the property representing whether the puzzle is solved.
     *
     * @return the ReadOnlyBooleanProperty for the solved state
     */
    public ReadOnlyBooleanProperty solvedProperty() {
        return solved.getReadOnlyProperty();
    }

    /**
     * Returns the property representing whether the current state is a dead end.
     *
     * @return the ReadOnlyBooleanProperty for the dead end state
     */
    public ReadOnlyBooleanProperty deadEndProperty() {
        return deadEnd.getReadOnlyProperty();
    }

    /**
     * Checks if the current state is a dead end (i.e., no legal moves available).
     *
     * @return true if the state is a dead end, false otherwise
     */
    public boolean isDeadEnd() {
        return deadEnd.get();
    }
}
