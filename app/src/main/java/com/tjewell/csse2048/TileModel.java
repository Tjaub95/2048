package com.tjewell.csse2048;

import java.io.Serializable;

/**
 * Model for tile's within the 4x4 2048 grid.
 * Each model has a position within the grid and a value associated to it.
 */

public class TileModel extends Cell implements Serializable {
    private TileModel[] mergedFrom = null;
    private int tileValue;

    public TileModel(int x, int y, int value) {
        super(x, y);
        this.tileValue = value;
    }

    public TileModel(Cell cell, int value) {
        super(cell.getX(), cell.getY());
        this.tileValue = value;
    }

    public void setPosition(Cell cell) {
        this.setX(cell.getX());
        this.setY(cell.getY());
    }

    public int getTileValue() {
        return tileValue;
    }

    // A tiles value will be set whenever the tile collides
    // with a tile that is of the same value.
    public void setTileValue(int tileValue) {
        this.tileValue = tileValue;
    }

    public TileModel[] getMergedFrom() {
        return mergedFrom;
    }

    public void setMergedFrom(TileModel[] tile) {
        mergedFrom = tile;
    }

}
