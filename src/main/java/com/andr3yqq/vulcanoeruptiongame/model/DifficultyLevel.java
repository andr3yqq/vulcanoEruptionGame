package com.andr3yqq.vulcanoeruptiongame.model;

import com.andr3yqq.vulcanoeruptiongame.factory.MapFactory;
import com.andr3yqq.vulcanoeruptiongame.factory.ProceduralMapFactory;
import com.andr3yqq.vulcanoeruptiongame.factory.PresetMapFactory;
import com.andr3yqq.vulcanoeruptiongame.simulation.DefaultLavaSpreadStrategy;
import com.andr3yqq.vulcanoeruptiongame.simulation.LavaSpreadStrategy;
import lombok.Getter;

@Getter
public enum DifficultyLevel {
    EASY("Lengva", new PresetMapFactory(MapPresets.easyMap()), 3, 3, 3, false),
    NORMAL("Vidutinė", new PresetMapFactory(MapPresets.normalMap()), 2, 2, 2, false),
    HARD("Sunki", new PresetMapFactory(MapPresets.hardMap()), 1, 1, 1, false),
    PROCEDURAL("Procedūrinė", new ProceduralMapFactory(29, 23, 18, 3), 3, 3, 1, true);

    private final String displayName;
    private final MapFactory mapFactory;
    private final int barricadeActions;
    private final int openRoadActions;
    private final int lavaInterval;
    private final boolean procedural;
    private final LavaSpreadStrategy lavaStrategy = new DefaultLavaSpreadStrategy();

    DifficultyLevel(String displayName, MapFactory mapFactory, int barricadeActions, int openRoadActions,
                    int lavaInterval, boolean procedural) {
        this.displayName = displayName;
        this.mapFactory = mapFactory;
        this.barricadeActions = barricadeActions;
        this.openRoadActions = openRoadActions;
        this.lavaInterval = lavaInterval;
        this.procedural = procedural;
    }

    public SimulationConfig createConfig() {
        return createConfig(System.nanoTime());
    }

    public SimulationConfig createConfig(long seed) {
        long mapSeed = procedural ? seed : 0L;
        GameMap map = mapFactory.create(mapSeed);
        return new SimulationConfig(map, barricadeActions, openRoadActions, lavaInterval, lavaStrategy);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
