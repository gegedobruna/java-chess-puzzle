package chesspuzzle.model;

public enum Direction {
    KING_UP(-1, 0),
    KING_DOWN(1, 0),
    KING_LEFT(0, -1),
    KING_RIGHT(0, 1),
    KING_UP_LEFT(-1, -1),
    KING_UP_RIGHT(-1, 1),
    KING_DOWN_LEFT(1, -1),
    KING_DOWN_RIGHT(1, 1),
    KNIGHT_MOVE1(2, 1),
    KNIGHT_MOVE2(2, -1),
    KNIGHT_MOVE3(-2, 1),
    KNIGHT_MOVE4(-2, -1),
    KNIGHT_MOVE5(1, 2),
    KNIGHT_MOVE6(1, -2),
    KNIGHT_MOVE7(-1, 2),
    KNIGHT_MOVE8(-1, -2);

    private final int rowChange;
    private final int colChange;

    Direction(int rowChange, int colChange) {
        this.rowChange = rowChange;
        this.colChange = colChange;
    }

    public int getRowChange() {
        return rowChange;
    }

    public int getColChange() {
        return colChange;
    }

    public static Direction of(int rowChange, int colChange) {
        for (Direction direction : values()) {
            if (direction.rowChange == rowChange && direction.colChange == colChange) {
                return direction;
            }
        }
        throw new IllegalArgumentException("No such direction for rowChange: " + rowChange + ", colChange: " + colChange);
    }
}
