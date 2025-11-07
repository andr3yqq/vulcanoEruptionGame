package com.andr3yqq.vulcanoeruptiongame.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Immutable coordinate inside the map grid.
 */
public record Position(int x, int y) {

    public List<Position> neighbors4() {
        List<Position> n = new ArrayList<>(4);
        n.add(new Position(x + 1, y));
        n.add(new Position(x - 1, y));
        n.add(new Position(x, y + 1));
        n.add(new Position(x, y - 1));
        return n;
    }

    public boolean isInside(int width, int height) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
