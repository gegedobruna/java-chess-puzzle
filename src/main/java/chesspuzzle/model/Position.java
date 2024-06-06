package chesspuzzle.model;

/**
 * Represents a position on a chess board.
 *
 * @param row the row of the position
 * @param col the column of the position
 */
public record Position(int row, int col) {

    /**
     * {@return the position after moving in the specified direction}
     *
     * @param direction the direction to move in
     */
    public Position move(Direction direction) {
        return new Position(row + direction.getRowChange(), col + direction.getColChange());
    }

    /**
     * {@return a string representation of the position}
     */
    @Override
    public String toString() {
        return String.format("(%d, %d)", row, col);
    }
}
