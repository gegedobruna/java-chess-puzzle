package chesspuzzle.model;

import lombok.Getter;

@Getter
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

    public boolean isKingMove() {
        return this.name().startsWith("KING_");
    }

    public boolean isKnightMove() {
        return this.name().startsWith("KNIGHT_");
    }
}
