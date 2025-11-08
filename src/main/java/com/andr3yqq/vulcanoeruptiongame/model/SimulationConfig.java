package com.andr3yqq.vulcanoeruptiongame.model;

import lombok.Getter;

@Getter
public class SimulationConfig {
    private final GameMap map;
    private final int barricadeActions;
    private final int openRoadActions;
    private final int lavaSpreadInterval;

    public SimulationConfig(GameMap map, int barricadeActions, int openRoadActions, int lavaSpreadInterval) {
        this.map = map;
        this.barricadeActions = barricadeActions;
        this.openRoadActions = openRoadActions;
        this.lavaSpreadInterval = lavaSpreadInterval;
    }

    public static SimulationConfig defaultConfig() {
        return DifficultyLevel.NORMAL.createConfig();
    }

    public static SimulationConfig forDifficulty(DifficultyLevel level) {
        return level.createConfig();
    }
}
