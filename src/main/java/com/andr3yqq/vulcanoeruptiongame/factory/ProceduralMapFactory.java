package com.andr3yqq.vulcanoeruptiongame.factory;

import com.andr3yqq.vulcanoeruptiongame.model.GameMap;
import com.andr3yqq.vulcanoeruptiongame.model.MapPresets;

public class ProceduralMapFactory implements MapFactory {
    private final int houses;
    private final int safeZones;
    private final int width;
    private final int height;

    public ProceduralMapFactory(int width, int height, int houses, int safeZones) {
        this.width = width;
        this.height = height;
        this.houses = houses;
        this.safeZones = safeZones;
    }

    @Override
    public GameMap create(long seed) {
        return MapPresets.proceduralMap(seed, width, height, houses, safeZones);
    }
}
