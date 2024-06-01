package chesspuzzle.model;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class DirectionTest {

    @Test
    public void testKingMoveDirections() {
        assertTrue(Direction.KING_UP.isKingMove());
        assertTrue(Direction.KING_DOWN.isKingMove());
        assertTrue(Direction.KING_LEFT.isKingMove());
        assertTrue(Direction.KING_RIGHT.isKingMove());
        assertTrue(Direction.KING_UP_LEFT.isKingMove());
        assertTrue(Direction.KING_UP_RIGHT.isKingMove());
        assertTrue(Direction.KING_DOWN_LEFT.isKingMove());
        assertTrue(Direction.KING_DOWN_RIGHT.isKingMove());
        assertFalse(Direction.KNIGHT_MOVE1.isKingMove());
        assertFalse(Direction.KNIGHT_MOVE2.isKingMove());
        assertFalse(Direction.KNIGHT_MOVE3.isKingMove());
        assertFalse(Direction.KNIGHT_MOVE4.isKingMove());
        assertFalse(Direction.KNIGHT_MOVE5.isKingMove());
        assertFalse(Direction.KNIGHT_MOVE6.isKingMove());
        assertFalse(Direction.KNIGHT_MOVE7.isKingMove());
        assertFalse(Direction.KNIGHT_MOVE8.isKingMove());

    }

    @Test
    public void testKnightMoveDirections() {
        assertTrue(Direction.KNIGHT_MOVE1.isKnightMove());
        assertTrue(Direction.KNIGHT_MOVE2.isKnightMove());
        assertTrue(Direction.KNIGHT_MOVE3.isKnightMove());
        assertTrue(Direction.KNIGHT_MOVE4.isKnightMove());
        assertTrue(Direction.KNIGHT_MOVE5.isKnightMove());
        assertTrue(Direction.KNIGHT_MOVE6.isKnightMove());
        assertTrue(Direction.KNIGHT_MOVE7.isKnightMove());
        assertTrue(Direction.KNIGHT_MOVE8.isKnightMove());
        assertFalse(Direction.KING_UP.isKnightMove());
        assertFalse(Direction.KING_DOWN.isKnightMove());
        assertFalse(Direction.KING_LEFT.isKnightMove());
        assertFalse(Direction.KING_RIGHT.isKnightMove());
        assertFalse(Direction.KING_UP_LEFT.isKnightMove());
        assertFalse(Direction.KING_UP_RIGHT.isKnightMove());
        assertFalse(Direction.KING_DOWN_LEFT.isKnightMove());
        assertFalse(Direction.KING_DOWN_RIGHT.isKnightMove());

    }
}
