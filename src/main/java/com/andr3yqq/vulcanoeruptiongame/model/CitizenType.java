package com.andr3yqq.vulcanoeruptiongame.model;

public enum CitizenType {
    SLOW(1),
    FAST(2);

    private final int tilesPerTick;

    CitizenType(int tilesPerTick) {
        this.tilesPerTick = tilesPerTick;
    }

    public int getTilesPerTick() {
        return tilesPerTick;
    }
}
