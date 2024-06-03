package chesspuzzle.model;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import lombok.Getter;
import puzzle.TwoPhaseMoveState;

import java.util.*;

@Getter
public class ChessState implements TwoPhaseMoveState<Position> {
    public static final int BOARD_SIZE = 8;

    private final ReadOnlyObjectWrapper<Position> kingPosition = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<Position> knightPosition = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyBooleanWrapper solved = new ReadOnlyBooleanWrapper();
    private final List<String[]> board;

    public ChessState() {
        this(new Position(2, 1), new Position(2, 2));
    }

    public ChessState(Position kingPosition, Position knightPosition) {
        this.kingPosition.set(kingPosition);
        this.knightPosition.set(knightPosition);
        this.board = new ArrayList<>(BOARD_SIZE);
        Position goalPosition = new Position(0, 6);
        initializeBoard();
        solved.bind(this.kingPosition.isEqualTo(goalPosition).or(this.knightPosition.isEqualTo(goalPosition)));
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

    public Optional<Integer> getPieceAt(int row, int col) {
        String piece = board.get(row)[col];
        return switch (piece) {
            case "K" -> Optional.of(0);
            case "N" -> Optional.of(1);
            default -> Optional.empty();
        };
    }

    boolean isValid(Position position) {
        return position.row() >= 0 && position.row() < BOARD_SIZE && position.col() >= 0 && position.col() < BOARD_SIZE;
    }

    public boolean isValidKingMove(Position from, Position to) {
        int dr = Math.abs(from.row() - to.row());
        int dc = Math.abs(from.col() - to.col());
        return dr <= 1 && dc <= 1;
    }

    public boolean isValidKnightMove(Position from, Position to) {
        int dr = Math.abs(from.row() - to.row());
        int dc = Math.abs(from.col() - to.col());
        return (dr == 2 && dc == 1) || (dr == 1 && dc == 2);
    }

    boolean isUnderAttack(Position piecePosition, Position attackerPosition) {
        if (piecePosition.equals(kingPosition.get())) {
            return isValidKnightMove(attackerPosition, piecePosition);
        } else if (piecePosition.equals(knightPosition.get())) {
            return isValidKingMove(attackerPosition, piecePosition);
        }
        return false;
    }

    @Override
    public boolean isSolved() {
        return solved.get();
    }

    @Override
    public boolean isLegalToMoveFrom(Position position) {
        if (position.equals(kingPosition.get())) {
            return isUnderAttack(kingPosition.get(), knightPosition.get());
        } else if (position.equals(knightPosition.get())) {
            return isUnderAttack(knightPosition.get(), kingPosition.get());
        }
        return false;
    }

    @Override
    public boolean isLegalMove(TwoPhaseMove<Position> move) {
        Position from = move.from();
        Position to = move.to();
        if (isValid(to)) {
            if (from.equals(knightPosition.get()) && isValidKnightMove(knightPosition.get(), to)) {
                return isUnderAttack(knightPosition.get(), kingPosition.get());
            }
            if (from.equals(kingPosition.get()) && isValidKingMove(kingPosition.get(), to)) {
                return isUnderAttack(kingPosition.get(), knightPosition.get());
            }
        }
        return false;
    }

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
    }

    @Override
    public Set<TwoPhaseMove<Position>> getLegalMoves() {
        Set<TwoPhaseMove<Position>> legalMoves = new HashSet<>();
        collectKingMoves(legalMoves);
        collectKnightMoves(legalMoves);
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

    @Override
    public ChessState clone() {
        return new ChessState(kingPosition.get(), knightPosition.get());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessState other)) {
            return false;
        }
        return kingPosition.get().equals(other.kingPosition.get()) &&
                knightPosition.get().equals(other.knightPosition.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(kingPosition.get(), knightPosition.get());
    }

    @Override
    public String toString() {
        return "King: " + kingPosition.get() + ", Knight: " + knightPosition.get();
    }

    public ReadOnlyObjectWrapper<Position> kingPositionProperty() {
        return kingPosition;
    }

    public ReadOnlyObjectWrapper<Position> knightPositionProperty() {
        return knightPosition;
    }

    public ReadOnlyBooleanProperty solvedProperty() {
        return solved.getReadOnlyProperty();
    }
}
