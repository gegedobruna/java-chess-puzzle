package chesspuzzle.model;


import puzzle.TwoPhaseMoveState;

import lombok.Getter;
import java.util.*;

@Getter
public class ChessState implements TwoPhaseMoveState<Position> {
    public static final int BOARD_SIZE = 8;

    private Position kingPosition;
    private Position knightPosition;
    private final List<String[]> board;

    // Constructors
    public ChessState() {
        this(new Position(2, 1), new Position(2, 2));
    }

    public ChessState(Position kingPosition, Position knightPosition) {
        this.kingPosition = kingPosition;
        this.knightPosition = knightPosition;
        this.board = new ArrayList<>(BOARD_SIZE);
        initializeBoard();
    }

    // Private helper methods
    private void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            String[] row = new String[BOARD_SIZE];
            Arrays.fill(row, ".");
            board.add(row);
        }
        board.get(kingPosition.row())[kingPosition.col()] = "K";
        board.get(knightPosition.row())[knightPosition.col()] = "N";
    }

    private boolean isValid(Position position) {
        return position.row() >= 0 && position.row() < BOARD_SIZE && position.col() >= 0 && position.col() < BOARD_SIZE;
    }

    private boolean isValidKingMove(Position from, Position to) {
        int dr = Math.abs(from.row() - to.row());
        int dc = Math.abs(from.col() - to.col());
        return (dr <= 1 && dc <= 1);
    }

    private boolean isValidKnightMove(Position from, Position to) {
        int dr = Math.abs(from.row() - to.row());
        int dc = Math.abs(from.col() - to.col());
        return (dr == 2 && dc == 1) || (dr == 1 && dc == 2);
    }

    private boolean isUnderAttack(Position piecePosition, Position attackerPosition) {
        if (piecePosition.equals(kingPosition)) {
            return isValidKnightMove(attackerPosition, piecePosition);
        } else if (piecePosition.equals(knightPosition)) {
            return isValidKingMove(attackerPosition, piecePosition);
        }
        return false;
    }

    // Public methods (state checks and core logic)
    @Override
    public boolean isSolved() {
        return kingPosition.equals(new Position(0, 6)) || knightPosition.equals(new Position(0, 6));
    }

    @Override
    public boolean isLegalToMoveFrom(Position position) {
        if (position.equals(kingPosition)) {
            return isUnderAttack(kingPosition, knightPosition);
        } else if (position.equals(knightPosition)) {
            return isUnderAttack(knightPosition, kingPosition);
        }
        return false;
    }

    @Override
    public boolean isLegalMove(TwoPhaseMove<Position> move) {
        Position from = move.from();
        Position to = move.to();
        if (isValid(to)) {
            if (from.equals(knightPosition) && isValidKnightMove(knightPosition, to)) {
                return isUnderAttack(knightPosition, kingPosition);
            }
            if (from.equals(kingPosition) && isValidKingMove(kingPosition, to)) {
                return isUnderAttack(kingPosition, knightPosition);
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
        if (from.equals(kingPosition)) {
            kingPosition = to;
        } else if (from.equals(knightPosition)) {
            knightPosition = to;
        }

    }

    // Public methods (move generation)
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
                Position newKingPosition = kingPosition.move(direction);
                TwoPhaseMove<Position> move = new TwoPhaseMove<>(kingPosition, newKingPosition);
                if (isLegalMove(move)) {
                    legalMoves.add(move);
                }
            }
        }
    }

    private void collectKnightMoves(Set<TwoPhaseMove<Position>> legalMoves) {
        for (Direction direction : Direction.values()) {
            if (direction.isKnightMove()) {
                Position newKnightPosition = knightPosition.move(direction);
                TwoPhaseMove<Position> move = new TwoPhaseMove<>(knightPosition, newKnightPosition);
                if (isLegalMove(move)) {
                    legalMoves.add(move);
                }
            }
        }
    }

    // Overridden methods
    @Override
    public ChessState clone() {
        return new ChessState(kingPosition, knightPosition);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessState other)) {
            return false;
        }
        return kingPosition.equals(other.kingPosition) &&
                knightPosition.equals(other.knightPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kingPosition, knightPosition);
    }

    @Override
    public String toString() {
        return "King: " + kingPosition + ", Knight: " + knightPosition;
    }
}