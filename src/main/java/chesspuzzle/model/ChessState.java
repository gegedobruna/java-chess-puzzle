package chesspuzzle.model;
import puzzle.TwoPhaseMoveState;

import java.util.Set;

public class ChessState implements TwoPhaseMoveState<Position>{

    @Override
    public boolean isLegalToMoveFrom(Position position) {
        return false;
    }

    @Override
    public boolean isSolved() {
        return false;
    }

    @Override
    public boolean isLegalMove(TwoPhaseMove<Position> positionTwoPhaseMove) {
        return false;
    }

    @Override
    public void makeMove(TwoPhaseMove<Position> positionTwoPhaseMove) {

    }

    @Override
    public Set<TwoPhaseMove<Position>> getLegalMoves() {
        return Set.of();
    }

    @Override
    public TwoPhaseMoveState<Position> clone() {
        return null;
    }
}