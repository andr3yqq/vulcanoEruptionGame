package com.andr3yqq.vulcanoeruptiongame.model;

import lombok.Getter;

@Getter
public class SimulationConfig {
    private final GameMap map;
    private final int barricadeActions;
    private final int openRoadActions;
    private final int lavaSpreadInterval;
    private final com.andr3yqq.vulcanoeruptiongame.simulation.LavaSpreadStrategy lavaStrategy;

    public SimulationConfig(GameMap map, int barricadeActions, int openRoadActions, int lavaSpreadInterval,
                           com.andr3yqq.vulcanoeruptiongame.simulation.LavaSpreadStrategy lavaStrategy) {
        this.map = map;
        this.barricadeActions = barricadeActions;
        this.openRoadActions = openRoadActions;
        this.lavaSpreadInterval = lavaSpreadInterval;
        this.lavaStrategy = lavaStrategy;
    }

    public static SimulationConfig defaultConfig() {
        return DifficultyLevel.NORMAL.createConfig();
    }

    public static SimulationConfig forDifficulty(DifficultyLevel level) {
        return level.createConfig();
    }
}
