package com.andr3yqq.vulcanoeruptiongame;

import com.andr3yqq.vulcanoeruptiongame.model.GameMap;
import com.andr3yqq.vulcanoeruptiongame.model.MapPresets;
import com.andr3yqq.vulcanoeruptiongame.model.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProceduralConnectivityTest {

    @Test
    void everyHouseHasPathToSafeZone() {
        GameMap map = MapPresets.proceduralMap(42L);
        for (Position house : map.getHouses()) {
            assertTrue(map.shortestPathToSafeZone(house).isPresent(), "House " + house + " must reach safety");
        }
    }
}
