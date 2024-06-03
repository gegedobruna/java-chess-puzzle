package chesspuzzle.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import puzzle.TwoPhaseMoveState.TwoPhaseMove;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ChessStateTest {

    private ChessState chessState;
    private Position defaultKingPosition;
    private Position defaultKnightPosition;
    private Position kingPos;
    private Position knightPos;

    @BeforeEach
    void setUp() {
        defaultKingPosition = new Position(2, 1);
        defaultKnightPosition = new Position(2, 2);
        chessState = new ChessState(defaultKingPosition, defaultKnightPosition);
        kingPos = new Position(3, 3);
        knightPos = new Position(4, 4);
    }

    @Test
    void testDefaultConstructor() {
        assertEquals(defaultKingPosition, chessState.getKingPosition());
        assertEquals(defaultKnightPosition, chessState.getKnightPosition());
    }

    @Test
    void testCustomConstructor() {
        ChessState customState = new ChessState(kingPos, knightPos);
        assertEquals(kingPos, customState.getKingPosition());
        assertEquals(knightPos, customState.getKnightPosition());
    }

    @Test
    void testIsSolved() {
        assertFalse(chessState.isSolved());
        ChessState solvedState1 = new ChessState(new Position(0, 6), defaultKnightPosition);
        assertTrue(solvedState1.isSolved());
        ChessState solvedState2 = new ChessState(defaultKingPosition, new Position(0, 6));
        assertTrue(solvedState2.isSolved());
        ChessState solvedState3 = new ChessState(new Position(3, 7), defaultKnightPosition);
        assertFalse(solvedState3.isSolved());
        ChessState solvedState4 = new ChessState(defaultKingPosition, new Position(0, 4));
        assertFalse(solvedState4.isSolved());
    }

    @Test
    void testIsLegalToMoveFrom() {
        Position knightAttackingKing = new Position(3, 3);
        chessState = new ChessState(defaultKingPosition, knightAttackingKing);
        assertTrue(chessState.isLegalToMoveFrom(chessState.getKingPosition()));
        assertFalse(chessState.isLegalToMoveFrom(new Position(0, 0)));

        Position kingAttackingKnight = new Position(1, 3);
        chessState = new ChessState(kingAttackingKnight, defaultKnightPosition);
        assertTrue(chessState.isLegalToMoveFrom(chessState.getKnightPosition()));
        assertFalse(chessState.isLegalToMoveFrom(new Position(0, 0)));
    }

    @Test
    public void testIsLegalMove() {
        // Test a legal move for the king
        final Position fromKingLegal = defaultKingPosition;
        final Position toKingLegal = new Position(1, 1);
        final TwoPhaseMove<Position> kingLegalMove = new TwoPhaseMove<>(fromKingLegal, toKingLegal);
        Position knightAttackingKing = new Position(3, 3);
        chessState = new ChessState(defaultKingPosition, knightAttackingKing);
        assertTrue(chessState.isLegalMove(kingLegalMove));

        // Test a legal move for the knight
        final Position fromKnightLegal = defaultKnightPosition;
        final Position toKnightLegal = new Position(0, 1);
        final TwoPhaseMove<Position> knightLegalMove = new TwoPhaseMove<>(fromKnightLegal, toKnightLegal);
        Position kingAttackingKnight = new Position(1, 3);
        chessState = new ChessState(kingAttackingKnight, defaultKnightPosition);
        assertTrue(chessState.isLegalMove(knightLegalMove));

        // Test an illegal move
        final Position fromIllegal = defaultKingPosition;
        final Position toIllegal = new Position(4, 4); // This is not a valid move for either the king or the knight
        final TwoPhaseMove<Position> illegalMove = new TwoPhaseMove<>(fromIllegal, toIllegal);
        chessState = new ChessState(defaultKingPosition, defaultKnightPosition);
        assertFalse(chessState.isLegalMove(illegalMove));
    }

    @Test
    void testMakeKingMove() {
        Position knightAttackingKing = new Position(3, 3);
        chessState = new ChessState(defaultKingPosition, knightAttackingKing);

        final Position fromLegal = defaultKingPosition;
        final Position toLegal = new Position(1, 1);
        final Position toIllegal = new Position(4, 4);

        // Test a legal move for the king
        final TwoPhaseMove<Position> legalMove = new TwoPhaseMove<>(fromLegal, toLegal);
        assertTrue(chessState.isLegalMove(legalMove));
        chessState.makeMove(legalMove);
        assertEquals(toLegal, chessState.getKingPosition());

        // Test an illegal move
        final TwoPhaseMove<Position> illegalMove = new TwoPhaseMove<>(fromLegal, toIllegal);
        assertFalse(chessState.isLegalMove(illegalMove));
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> chessState.makeMove(illegalMove));
        assertEquals("Illegal move: " + illegalMove, thrown.getMessage());
    }

    @Test
    void testMakeKnightMove() {
        Position kingAttackingKnight = new Position(1, 3);
        chessState = new ChessState(kingAttackingKnight, defaultKnightPosition);

        final Position fromLegal = defaultKnightPosition;
        final Position toLegal = new Position(0, 1);
        final Position toIllegal = new Position(4, 4);

        // Test a legal move for the knight
        final TwoPhaseMove<Position> legalMove = new TwoPhaseMove<>(fromLegal, toLegal);
        assertTrue(chessState.isLegalMove(legalMove));
        chessState.makeMove(legalMove);
        assertEquals(toLegal, chessState.getKnightPosition());

        // Test an illegal move
        final TwoPhaseMove<Position> illegalMove = new TwoPhaseMove<>(fromLegal, toIllegal);
        assertFalse(chessState.isLegalMove(illegalMove));
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> chessState.makeMove(illegalMove));
        assertEquals("Illegal move: " + illegalMove, thrown.getMessage());
    }


    @Test
    void testGetLegalMoves() {
        Set<TwoPhaseMove<Position>> legalMoves = chessState.getLegalMoves();
        assertNotNull(legalMoves);

        chessState = new ChessState(defaultKingPosition, new Position(3, 3));
        legalMoves = chessState.getLegalMoves();
        assertEquals(8, legalMoves.size());
    }

    @Test
    void testEqualsAndHashCode() {
        ChessState state1 = new ChessState(defaultKingPosition, defaultKnightPosition);
        ChessState state2 = new ChessState(defaultKingPosition, defaultKnightPosition);
        ChessState state3 = new ChessState(new Position(3, 1), defaultKnightPosition);

        assertEquals(state1, state2);
        assertNotEquals(state1, state3);
        assertEquals(state1.hashCode(), state2.hashCode());
        assertNotEquals(state1.hashCode(), state3.hashCode());
    }

    @Test
    void testClone() {
        ChessState clonedState = chessState.clone();
        assertEquals(chessState, clonedState);
        assertNotSame(chessState, clonedState);
    }

    @Test
    void testToString() {
        ChessState state1 = new ChessState(defaultKingPosition, defaultKnightPosition);
        ChessState state2 = new ChessState(new Position(3, 3), new Position(4, 4));
        ChessState state3 = new ChessState(new Position(0, 0), new Position(7, 7));
        ChessState state4 = new ChessState(new Position(5, 5), new Position(6, 6));
        ChessState state5 = new ChessState(new Position(1, 1), new Position(2, 3));

        assertEquals("King: (2, 1), Knight: (2, 2)", state1.toString());
        assertEquals("King: (3, 3), Knight: (4, 4)", state2.toString());
        assertEquals("King: (0, 0), Knight: (7, 7)", state3.toString());
        assertEquals("King: (5, 5), Knight: (6, 6)", state4.toString());
        assertEquals("King: (1, 1), Knight: (2, 3)", state5.toString());
    }

}
