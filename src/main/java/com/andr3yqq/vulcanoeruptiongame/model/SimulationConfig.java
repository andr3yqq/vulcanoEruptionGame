package com.andr3yqq.vulcanoeruptiongame.model;

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

    public GameMap getMap() {
        return map;
    }

    public int getBarricadeActions() {
        return barricadeActions;
    }

    public int getOpenRoadActions() {
        return openRoadActions;
    }

    public int getLavaSpreadInterval() {
        return lavaSpreadInterval;
    }

    public static SimulationConfig defaultConfig() {
        return new SimulationConfig(GameMap.demoMap(), 2, 2, 2);
    }
}
