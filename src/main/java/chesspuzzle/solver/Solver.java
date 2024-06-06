package chesspuzzle.solver;

import chesspuzzle.model.ChessState;
import chesspuzzle.model.Position;
import puzzle.TwoPhaseMoveState;
import puzzle.solver.BreadthFirstSearch;

/**
 * Solves the chess puzzle using the breadth-first search algorithm.
 */
public class Solver {
    public static void main(String[] args) {
        var bfs = new BreadthFirstSearch<TwoPhaseMoveState.TwoPhaseMove<Position>>();
        ChessState initialState = new ChessState();
        bfs.solveAndPrintSolution(initialState);
    }
}
