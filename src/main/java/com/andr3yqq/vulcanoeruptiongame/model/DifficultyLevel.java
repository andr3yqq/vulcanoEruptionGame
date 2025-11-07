package com.andr3yqq.vulcanoeruptiongame.model;

import java.util.function.LongFunction;

public enum DifficultyLevel {
    EASY("Lengva", ignored -> MapPresets.easyMap(), 3, 3, 3, false),
    NORMAL("Vidutinė", ignored -> MapPresets.normalMap(), 2, 2, 2, false),
    HARD("Sunki", ignored -> MapPresets.hardMap(), 1, 1, 1, false),
    PROCEDURAL("Procedūrinė", MapPresets::proceduralMap, 3, 3, 2, true);

    private final String displayName;
    private final LongFunction<GameMap> mapFactory;
    private final int barricadeActions;
    private final int openRoadActions;
    private final int lavaInterval;
    private final boolean procedural;

    DifficultyLevel(String displayName,
                    LongFunction<GameMap> mapFactory,
                    int barricadeActions,
                    int openRoadActions,
                    int lavaInterval,
                    boolean procedural) {
        this.displayName = displayName;
        this.mapFactory = mapFactory;
        this.barricadeActions = barricadeActions;
        this.openRoadActions = openRoadActions;
        this.lavaInterval = lavaInterval;
        this.procedural = procedural;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isProcedural() {
        return procedural;
    }

    public SimulationConfig createConfig() {
        return createConfig(System.nanoTime());
    }

    public SimulationConfig createConfig(long seed) {
        long mapSeed = procedural ? seed : 0L;
        GameMap map = mapFactory.apply(mapSeed);
        return new SimulationConfig(map, barricadeActions, openRoadActions, lavaInterval);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
