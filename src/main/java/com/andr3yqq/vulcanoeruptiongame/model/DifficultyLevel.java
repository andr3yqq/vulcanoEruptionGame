package com.andr3yqq.vulcanoeruptiongame.model;

import com.andr3yqq.vulcanoeruptiongame.factory.MapFactory;
import com.andr3yqq.vulcanoeruptiongame.factory.ProceduralMapFactory;
import com.andr3yqq.vulcanoeruptiongame.factory.PresetMapFactory;
import com.andr3yqq.vulcanoeruptiongame.simulation.DefaultLavaSpreadStrategy;
import com.andr3yqq.vulcanoeruptiongame.simulation.LavaSpreadStrategy;
import lombok.Getter;

import static com.andr3yqq.vulcanoeruptiongame.model.DifficultyConstants.*;

@Getter
public enum DifficultyLevel {
    EASY("Lengva", new PresetMapFactory(MapPresets.easyMap()), EASY_LEVEL_BARRICADE_ACTIONS, EASY_LEVEL_OPEN_ROAD_ACTIONS, EASY_LEVEL_LAVA_SPREAD_INTERVAL, false),
    NORMAL("Vidutinė", new PresetMapFactory(MapPresets.normalMap()), NORMAL_LEVEL_BARRICADE_ACTIONS, NORMAL_LEVEL_OPEN_ROAD_ACTIONS, NORMAL_LEVEL_LAVA_SPREAD_INTERVAL, false),
    HARD("Sunki", new PresetMapFactory(MapPresets.hardMap()), HARD_LEVEL_BARRICADE_ACTIONS, HARD_LEVEL_OPEN_ROAD_ACTIONS, HARD_LEVEL_LAVA_SPREAD_INTERVAL, false),
    PROCEDURAL("Procedūrinė", new ProceduralMapFactory(PROCEDURAL_LEVEL_WIDTH, PROCEDURAL_LEVEL_HEIGHT, PROCEDURAL_LEVEL_HOUSES, PROCEDURAL_LEVEL_SAFE_ZONES),
            PROCEDURAL_LEVEL_BARRICADE_ACTIONS, PROCEDURAL_LEVEL_OPEN_ROAD_ACTIONS, PROCEDURAL_LEVEL_LAVA_SPREAD_INTERVAL, true);

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
