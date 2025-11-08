package com.andr3yqq.vulcanoeruptiongame.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a cell on the grid. Houses and safe zones are traversable, walls are not.
 */
@Getter
@Setter
public class Tile {
    private TileType type;
    private boolean barricaded;
    private boolean lava;

    public Tile(TileType type) {
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

    public boolean hasLava() {
        return lava;
    }

}
