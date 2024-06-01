package chesspuzzle.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PositionTest {

    @Test
    void testMove() {
        Position initialPosition = new Position(4, 3);

        // King moves
        assertEquals(new Position(3, 3), initialPosition.move(Direction.KING_UP));
        assertEquals(new Position(4, 4), initialPosition.move(Direction.KING_RIGHT));
        assertEquals(new Position(5, 3), initialPosition.move(Direction.KING_DOWN));
        assertEquals(new Position(4, 2), initialPosition.move(Direction.KING_LEFT));
        assertEquals(new Position(3, 2), initialPosition.move(Direction.KING_UP_LEFT));
        assertEquals(new Position(3, 4), initialPosition.move(Direction.KING_UP_RIGHT));
        assertEquals(new Position(5, 2), initialPosition.move(Direction.KING_DOWN_LEFT));
        assertEquals(new Position(5, 4), initialPosition.move(Direction.KING_DOWN_RIGHT));
        assertNotEquals(new Position(2, 2), initialPosition.move(Direction.KING_UP));
        assertNotEquals(new Position(4, 5), initialPosition.move(Direction.KING_RIGHT));
        assertNotEquals(new Position(2, 3), initialPosition.move(Direction.KING_DOWN));
        assertNotEquals(new Position(4, 4), initialPosition.move(Direction.KING_LEFT));
        assertNotEquals(new Position(3, 3), initialPosition.move(Direction.KING_UP_LEFT));
        assertNotEquals(new Position(5, 5), initialPosition.move(Direction.KING_UP_RIGHT));
        assertNotEquals(new Position(3, 4), initialPosition.move(Direction.KING_DOWN_LEFT));
        assertNotEquals(new Position(5, 2), initialPosition.move(Direction.KING_DOWN_RIGHT));


        // Knight moves
        assertEquals(new Position(6, 4), initialPosition.move(Direction.KNIGHT_MOVE1));
        assertEquals(new Position(6, 2), initialPosition.move(Direction.KNIGHT_MOVE2));
        assertEquals(new Position(2, 4), initialPosition.move(Direction.KNIGHT_MOVE3));
        assertEquals(new Position(2, 2), initialPosition.move(Direction.KNIGHT_MOVE4));
        assertEquals(new Position(5, 5), initialPosition.move(Direction.KNIGHT_MOVE5));
        assertEquals(new Position(5, 1), initialPosition.move(Direction.KNIGHT_MOVE6));
        assertEquals(new Position(3, 5), initialPosition.move(Direction.KNIGHT_MOVE7));
        assertEquals(new Position(3, 1), initialPosition.move(Direction.KNIGHT_MOVE8));
        assertNotEquals(new Position(6, 3), initialPosition.move(Direction.KNIGHT_MOVE1));
        assertNotEquals(new Position(6, 7), initialPosition.move(Direction.KNIGHT_MOVE2));
        assertNotEquals(new Position(2, 3), initialPosition.move(Direction.KNIGHT_MOVE3));
        assertNotEquals(new Position(2, 1), initialPosition.move(Direction.KNIGHT_MOVE4));
        assertNotEquals(new Position(5, 7), initialPosition.move(Direction.KNIGHT_MOVE5));
        assertNotEquals(new Position(5, 2), initialPosition.move(Direction.KNIGHT_MOVE6));
        assertNotEquals(new Position(3, 4), initialPosition.move(Direction.KNIGHT_MOVE7));
        assertNotEquals(new Position(7, 2), initialPosition.move(Direction.KNIGHT_MOVE8));


    }

    @Test
    void testEquals() {
        Position p1 = new Position(4, 3);
        Position p2 = new Position(4, 3);    // Same
        Position p3 = new Position(2, 1);    // Different row and column
        Position p4 = new Position(4, 5);    // Different column
        Position p5 = new Position(1, 3);    // Different row

        // Reflexivity
        assertEquals(p1, p1);
        // Symmetry
        assertEquals(p1, p2);
        assertEquals(p2, p1);
        // Transitivity
        assertEquals(p1, p2);
        assertEquals(p2, new Position(4, 3));
        assertEquals(p1, new Position(4, 3));
        // Consistency
        assertEquals(p1, p2);
        assertEquals(p1, p2); // Multiple calls should give the same result

        // Inequality
        assertNotEquals(p1, p3);
        assertNotEquals(p1, p4);
        assertNotEquals(p1, p5);
        assertNotEquals(p1, null);
        assertNotEquals(p1, "Not a Position object"); // Different type
    }

    @Test
    void testHashCode() {
        Position p1 = new Position(4, 3);
        Position p2 = new Position(4, 3);   // Equal
        Position p3 = new Position(2, 1);   // Not equal

        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1.hashCode(), p3.hashCode());
    }

    @Test
    void testToString() {
        assertEquals("(4, 3)", new Position(4, 3).toString());
        assertEquals("(0, 0)", new Position(0, 0).toString());
        assertEquals("(7, 7)", new Position(7, 7).toString()); // Corner
        assertEquals("(2, 5)", new Position(2, 5).toString());
    }
}
