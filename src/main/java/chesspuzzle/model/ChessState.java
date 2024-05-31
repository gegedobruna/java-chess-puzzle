package chesspuzzle.model;
import puzzle.TwoPhaseMoveState;

import java.util.Set;

public class ChessState implements TwoPhaseMoveState<Direction>{


    @Override
    public boolean isLegalToMoveFrom(Direction direction) {
        return false;
    }

    @Override
    public boolean isSolved() {
        return false;
    }

    @Override
    public boolean isLegalMove(TwoPhaseMove<Direction> directionTwoPhaseMove) {
        return false;
    }

    @Override
    public void makeMove(TwoPhaseMove<Direction> directionTwoPhaseMove) {

    }

    @Override
    public Set<TwoPhaseMove<Direction>> getLegalMoves() {
        return null;
    }

    @Override
    public TwoPhaseMoveState<Direction> clone() {
        return null;
    }
}
