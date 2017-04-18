package com.tjewell.csse2048;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
    private static final int startingMaxValue = 2048;
    //Odd state = game is not active
    //Even state = game is active
    //Win state = active state + 1
    private static final int GAME_WIN = 1;
    private static final int GAME_LOST = -1;
    private static final int GAME_NORMAL = 0;
    private static final int GAME_ENDLESS = 2;
    private static final int GAME_ENDLESS_WON = 3;
    private static final String HIGH_SCORE = "high score";
    private static int endingMaxValue;
    final int numSquaresX = 4;
    final int numSquaresY = 4;
    private final Context mContext;
    private final GridView mView;
    public int gameState = GAME_NORMAL;
    public int lastGameState = GAME_NORMAL;
    public Grid grid = null;
    public boolean canUndo;
    public long score = 0;
    public long highScore = 0;
    public long lastScore = 0;
    private int bufferGameState = GAME_NORMAL;
    private long bufferScore = 0;

    public Game(Context context, GridView view) {
        mContext = context;
        mView = view;
        endingMaxValue = (int) Math.pow(2, view.numCellTypes - 1);
    }

    public void newGame() {
        if (grid == null) {
            grid = new Grid(numSquaresX, numSquaresY);
        } else {
            prepareUndoState();
            saveUndoState();
            grid.clearGrid();
        }
        highScore = getHighScore();
        if (score >= highScore) {
            highScore = score;
            recordHighScore();
        }
        score = 0;
        gameState = GAME_NORMAL;
        addStartTiles();
        mView.refreshLastTime = true;
        mView.resyncTime();
        mView.invalidate();
    }

    private void addStartTiles() {
        int startTiles = 2;
        for (int xx = 0; xx < startTiles; xx++) {
            this.addRandomTile();
        }
    }

    private void addRandomTile() {
        if (grid.isCellsAvailable()) {
            int value = Math.random() < 0.75 ? 2 : 4;
            TileModel tile = new TileModel(grid.randomAvailableCell(), value);
            spawnTile(tile);
        }
    }

    private void spawnTile(TileModel tile) {
        grid.insertTile(tile);
    }

    private void recordHighScore() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(HIGH_SCORE, highScore);
        editor.apply();
    }

    private long getHighScore() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        return settings.getLong(HIGH_SCORE, -1);
    }

    private void prepareTiles() {
        for (TileModel[] array : grid.field) {
            for (TileModel tile : array) {
                if (grid.isCellOccupied(tile)) {
                    tile.setMergedFrom(null);
                }
            }
        }
    }

    private void moveTile(TileModel tile, Cell cell) {
        grid.field[tile.getX()][tile.getY()] = null;
        grid.field[cell.getX()][cell.getY()] = tile;
        tile.setPosition(cell);
    }

    private void saveUndoState() {
        grid.saveTiles();
        canUndo = true;
        lastScore = bufferScore;
        lastGameState = bufferGameState;
    }

    private void prepareUndoState() {
        grid.prepareSaveTiles();
        bufferScore = score;
        bufferGameState = gameState;
    }

    public void revertUndoState() {
        if (canUndo) {
            canUndo = false;
            grid.revertTiles();
            score = lastScore;
            gameState = lastGameState;
            mView.refreshLastTime = true;
            mView.invalidate();
        }
    }

    public boolean gameWon() {
        return (gameState > 0 && gameState % 2 != 0);
    }

    public boolean gameLost() {
        return (gameState == GAME_LOST);
    }

    public boolean isActive() {
        return !(gameWon() || gameLost());
    }

    public void move(int direction) {
        // 0: up, 1: right, 2: down, 3: left
        if (!isActive()) {
            return;
        }
        prepareUndoState();
        Cell vector = getVector(direction);
        List<Integer> traversalsX = buildTraversalsX(vector);
        List<Integer> traversalsY = buildTraversalsY(vector);
        boolean moved = false;

        prepareTiles();

        for (int xx : traversalsX) {
            for (int yy : traversalsY) {
                Cell cell = new Cell(xx, yy);
                TileModel tile = grid.getCellContent(cell);

                if (tile != null) {
                    Cell[] positions = findFarthestPosition(cell, vector);
                    TileModel next = grid.getCellContent(positions[1]);

                    if (next != null && next.getTileValue() == tile.getTileValue() && next.getMergedFrom() == null) {
                        TileModel merged = new TileModel(positions[1], tile.getTileValue() * 2);
                        TileModel[] temp = {tile, next};
                        merged.setMergedFrom(temp);

                        grid.insertTile(merged);
                        grid.removeTile(tile);

                        // Converge the two tiles' positions
                        tile.setPosition(positions[1]);

                        int[] extras = {xx, yy};
                        // Update the score
                        score = score + merged.getTileValue();
                        highScore = Math.max(score, highScore);

                        // The mighty 2048 tile
                        if (merged.getTileValue() >= winValue() && !gameWon()) {
                            gameState = gameState + GAME_WIN; // Set win state
                            endGame();
                        }
                    } else {
                        moveTile(tile, positions[0]);
                        int[] extras = {xx, yy, 0};
                    }

                    if (!positionsEqual(cell, tile)) {
                        moved = true;
                    }
                }
            }
        }

        if (moved) {
            saveUndoState();
            addRandomTile();
            checkLose();
        }
        mView.resyncTime();
        mView.invalidate();
    }

    private void checkLose() {
        if (!movesAvailable() && !gameWon()) {
            gameState = GAME_LOST;
            endGame();
        }
    }

    private void endGame() {
        if (score >= highScore) {
            highScore = score;
            recordHighScore();
        }
    }

    private Cell getVector(int direction) {
        Cell[] map = {
                new Cell(0, -1), // up
                new Cell(1, 0),  // right
                new Cell(0, 1),  // down
                new Cell(-1, 0)  // left
        };
        return map[direction];
    }

    private List<Integer> buildTraversalsX(Cell vector) {
        List<Integer> traversals = new ArrayList<>();

        for (int xx = 0; xx < numSquaresX; xx++) {
            traversals.add(xx);
        }
        if (vector.getX() == 1) {
            Collections.reverse(traversals);
        }

        return traversals;
    }

    private List<Integer> buildTraversalsY(Cell vector) {
        List<Integer> traversals = new ArrayList<>();

        for (int xx = 0; xx < numSquaresY; xx++) {
            traversals.add(xx);
        }
        if (vector.getY() == 1) {
            Collections.reverse(traversals);
        }

        return traversals;
    }

    private Cell[] findFarthestPosition(Cell cell, Cell vector) {
        Cell previous;
        Cell nextCell = new Cell(cell.getX(), cell.getY());
        do {
            previous = nextCell;
            nextCell = new Cell(previous.getX() + vector.getX(),
                    previous.getY() + vector.getY());
        } while (grid.isCellWithinBounds(nextCell) && grid.isCellAvailable(nextCell));

        return new Cell[]{previous, nextCell};
    }

    private boolean movesAvailable() {
        return grid.isCellsAvailable() || tileMatchesAvailable();
    }

    private boolean tileMatchesAvailable() {
        TileModel tile;

        for (int xx = 0; xx < numSquaresX; xx++) {
            for (int yy = 0; yy < numSquaresY; yy++) {
                tile = grid.getCellContent(new Cell(xx, yy));

                if (tile != null) {
                    for (int direction = 0; direction < 4; direction++) {
                        Cell vector = getVector(direction);
                        Cell cell = new Cell(xx + vector.getX(), yy + vector.getY());

                        TileModel other = grid.getCellContent(cell);

                        if (other != null && other.getTileValue() == tile.getTileValue()) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean positionsEqual(Cell first, Cell second) {
        return first.getX() == second.getX() && first.getY() == second.getY();
    }

    private int winValue() {
        if (!canContinue()) {
            return endingMaxValue;
        } else {
            return startingMaxValue;
        }
    }

    public void setEndlessMode() {
        gameState = GAME_ENDLESS;
        mView.invalidate();
        mView.refreshLastTime = true;
    }

    public boolean canContinue() {
        return !(gameState == GAME_ENDLESS || gameState == GAME_ENDLESS_WON);
    }
}
