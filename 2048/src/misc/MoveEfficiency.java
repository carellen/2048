package misc;

import model.Move;

public class MoveEfficiency implements Comparable<MoveEfficiency> {

    private int numberOfEmptyTiles;
    private int score;

    private Move move;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public int compareTo(MoveEfficiency another) {

        if (numberOfEmptyTiles != another.numberOfEmptyTiles) {
            return Integer.compare(numberOfEmptyTiles, another.numberOfEmptyTiles);
        } else {
            return Integer.compare(score, another.score);
        }

    }
}
