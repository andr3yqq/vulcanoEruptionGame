package com.andr3yqq.vulcanoeruptiongame.model;

/**
 * Represents a cell on the grid. Houses and safe zones are traversable, walls are not.
 */
public class Tile {
    private TileType type;
    private boolean barricaded;
    private boolean lava;

    public Tile(TileType type) {
        this.type = type;
    }

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public boolean isTraversable() {
        if (lava) {
            return false;
        }
        return switch (type) {
            case ROAD, HOUSE, SAFE_ZONE -> !barricaded;
            case VOLCANO, WALL -> false;
        };
    }

    public boolean isBarricaded() {
        return barricaded;
    }

    public void setBarricaded(boolean barricaded) {
        this.barricaded = barricaded;
    }

    public boolean hasLava() {
        return lava;
    }

    public void setLava(boolean lava) {
        this.lava = lava;
    }
}
