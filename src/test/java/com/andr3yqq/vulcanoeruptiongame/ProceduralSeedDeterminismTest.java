package com.andr3yqq.vulcanoeruptiongame;

import com.andr3yqq.vulcanoeruptiongame.model.MapPresets;
import com.andr3yqq.vulcanoeruptiongame.model.GameMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProceduralSeedDeterminismTest {
    @Test
    void sameSeedProducesSameVolcanoLocation() {
        long seed = 12345L;
        GameMap a = MapPresets.proceduralMap(seed);
        GameMap b = MapPresets.proceduralMap(seed);
        assertEquals(a.getVolcanoSource(), b.getVolcanoSource());
    }
}
