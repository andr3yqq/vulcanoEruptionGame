package com.andr3yqq.vulcanoeruptiongame.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.LongFunction;

@Getter
@AllArgsConstructor
public enum DifficultyLevel {
    EASY("Lengva", ignored -> MapPresets.easyMap(), 3, 3, 3, false),
    NORMAL("Vidutinė", ignored -> MapPresets.normalMap(), 2, 2, 2, false),
    HARD("Sunki", ignored -> MapPresets.hardMap(), 1, 1, 1, false),
    PROCEDURAL("Procedūrinė", MapPresets::proceduralMap, 3, 3, 1, true);

    private final String displayName;
    private final LongFunction<GameMap> mapFactory;
    private final int barricadeActions;
    private final int openRoadActions;
    private final int lavaInterval;
    private final boolean procedural;

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
