package controller;

import model.Model;
import model.Tile;
import view.View;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static java.awt.event.KeyEvent.VK_ESCAPE;

public class Controller extends KeyAdapter {

    private static final int WINNING_TILE = 2048;

    private Model model;
    private View view;

    public Controller(Model model) {
        this.model = model;
        this.view = new View(this);
    }

    public View getView() {
        return view;
    }

    public Tile[][] getGameTiles() {
        return model.getGameTiles();
    }
    public int getScore() {
        return model.getScore();
    }

    public void resetGame() {
        model.setScore(0);
        view.setGameWon(false);
        view.setGameLost(false);
        model.resetGameTiles();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == VK_ESCAPE) {
            resetGame();
        }
        if (!model.canMove()) {
            view.setGameLost(true);
        }
        if (!view.isGameLost() && !view.isGameWon()) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT : model.left();
                    break;
                case KeyEvent.VK_RIGHT : model.right();
                    break;
                case KeyEvent.VK_UP : model.up();
                    break;
                case KeyEvent.VK_DOWN : model.down();
                    break;
                case KeyEvent.VK_Z : model.rollback();
                    break;
                case KeyEvent.VK_R : model.randomMove();
                    break;
                case KeyEvent.VK_A : model.autoMove();
                    break;
            }
        }
        if (model.getMaxTile() == WINNING_TILE) {
            view.setGameWon(true);
        }
        view.repaint();
        view.checkGameStatus();
    }
}
