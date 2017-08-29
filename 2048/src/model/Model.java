package model;

import misc.MoveEfficiency;

import java.util.*;

public class Model {
    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles;

    private Stack<Tile[][]> previousStates = new Stack<>();
    private Stack<Integer> previousScores = new Stack<>();
    private boolean isSaveNeeded = true;

    private int score;
    private int maxTile;

    public Model() {
        resetGameTiles();
        this.score = 0;
        this.maxTile = 2;
    }

    public int getScore() {
        return score;
    }

    public int getMaxTile() {
        return maxTile;
    }

    public void setScore(int score) {
        this.score = score;
    }

    private void saveState(Tile[][] tiles) {
        Tile[][] forSave = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                forSave[i][j] = new Tile();
                forSave[i][j].value = tiles[i][j].value;
            }
        }
        previousStates.push(forSave);
        previousScores.push(score);
        isSaveNeeded = false;
    }
    public void rollback() {
        if (!previousStates.empty() && !previousScores.empty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }

    public void randomMove() {
        int n = ((int) (Math.random() * 100)) % 4;
        switch (n) {
            case 0 : left();
                break;
            case 1 : right();
                break;
            case 2 : up();
                break;
            case 3 : down();
                break;
        }
    }

    private List<Tile> getEmptyTiles() {
        List<Tile> list = new ArrayList<>();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].value == 0) {
                    list.add(gameTiles[i][j]);
                }
            }
        }
        return list;
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    private void addTile() {
        List<Tile> emptyTiles = getEmptyTiles();
        if (emptyTiles != null && emptyTiles.size() != 0) {
            int randNumTile = (int) (emptyTiles.size() * Math.random());
            emptyTiles.get(randNumTile).value = Math.random() < 0.9 ? 2 : 4;
        }
    }

    public boolean canMove() {

        for (int q = 0 ; q < 4 ; q++) {
            for (int i = 0; i < gameTiles.length; i++) {
                for (int j = 0; j < gameTiles[i].length; j++) {
                    if (gameTiles[i][j].value != 0 && j > 0 && gameTiles[i][j - 1].value == 0) {
                        for (int k = 0; k < 4 - q; k++) {
                            rotate();
                        }
                        return true;
                    }
                    else if (gameTiles[i][j].value != 0 && (j + 1) < gameTiles.length && gameTiles[i][j].value == gameTiles[i][j + 1].value) {
                        for (int k = 0; k < 4 - q; k++) {
                            rotate();
                        }
                        return true;
                    }
                }
            }
            rotate();
        }
        return false;
    }

    public void resetGameTiles() {
        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }
    private void rotate() {
        int layers = gameTiles.length / 2;
        for (int start = 0; start < layers; start++) {
            int border = gameTiles.length - 1;
            for (int j = start; j < border - start; j++) {
                int tempForFirst = gameTiles[start][j].value;
                gameTiles[start][j].value = gameTiles[border - j][start].value;
                gameTiles[border - j][start].value = gameTiles[border - start][border - j].value;
                gameTiles[border - start][border - j].value = gameTiles[j][border - start].value;
                gameTiles[j][border - start].value = tempForFirst;
            }
        }
    }

    private boolean compressTiles(Tile[] tiles) {
        boolean b = false;
        int place = 0;
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i].value != 0 && i > 0 && tiles[i - 1].value == 0) {
                b = true;
                place = i - 1;
                tiles[place].value = tiles[i].value;
                tiles[i].value = 0;
                while (place > 0 && tiles[place - 1].value == 0) {
                    tiles[place - 1].value = tiles[place].value;
                    tiles[place].value = 0;
                    place--;
                }
            }
        }
        return b;
    }

    private boolean mergeTiles(Tile[] tiles) {
        boolean b = false;
        int place = 0;
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i].value != 0 && (i + 1) < tiles.length && tiles[i].value == tiles[i + 1].value) {
                tiles[i].value *= 2;
                if (tiles[i].value > maxTile) {
                    maxTile = tiles[i].value;
                }
                score += tiles[i].value;
                place = i + 1;
                tiles[place].value = 0;
                while (place + 1 < tiles.length) {
                    tiles[place].value = tiles[place + 1].value;
                    place++;
                    tiles[place].value = 0;
                }
                b = true;
            }

        }
        return b;
    }

    public void left() {
        if (isSaveNeeded) {
            saveState(gameTiles);
        }
        boolean a = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                a = true;
            }
        }
        if (a) {
            addTile();
        }
        isSaveNeeded = true;
    }
    public void right() {
        saveState(gameTiles);
        boolean a = false;
        rotate();
        rotate();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                a = true;
            }
        }
        rotate();
        rotate();
        if (a) {
            addTile();
        }
    }
    public void up() {
        saveState(gameTiles);
        boolean a = false;
        rotate();
        rotate();
        rotate();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                a = true;
            }
        }
        rotate();
        if (a) {
            addTile();
        }
    }
    public void down() {
        saveState(gameTiles);
        boolean a = false;
        rotate();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                a = true;
            }
        }
        rotate();
        rotate();
        rotate();
        if (a) {
            addTile();
        }
    }

    private boolean hasBoardChanged() {
        int gTiles = 0;
        int sTiles = 0;
        Tile[][] fromStack = previousStates.peek();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                gTiles += gameTiles[i][j].value;
                sTiles += fromStack[i][j].value;
            }
        }
        if (gTiles != sTiles) {
            return true;
        }
        else {
            return false;
        }
    }

    public void autoMove() {
        PriorityQueue<MoveEfficiency> queue = new PriorityQueue<MoveEfficiency>(4,Collections.reverseOrder());
        queue.offer(getMoveEfficiency(this::left));
        queue.offer(getMoveEfficiency(this::right));
        queue.offer(getMoveEfficiency(this::up ));
        queue.offer(getMoveEfficiency(this::down));
        queue.peek().getMove().move();
    }

    private MoveEfficiency getMoveEfficiency(Move move) {
        move.move();
        MoveEfficiency result = null;
        if (!hasBoardChanged()) {
            result = new MoveEfficiency(-1, 0, move);
        }
        else {
            result = new MoveEfficiency(getEmptyTiles().size(), score, move);
        }
        rollback();
        return result;
    }
}
