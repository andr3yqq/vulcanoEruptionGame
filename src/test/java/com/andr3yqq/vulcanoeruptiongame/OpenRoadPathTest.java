package com.andr3yqq.vulcanoeruptiongame;

import com.andr3yqq.vulcanoeruptiongame.model.GameMap;
import com.andr3yqq.vulcanoeruptiongame.model.Position;
import com.andr3yqq.vulcanoeruptiongame.model.SimulationConfig;
import com.andr3yqq.vulcanoeruptiongame.simulation.DefaultLavaSpreadStrategy;
import com.andr3yqq.vulcanoeruptiongame.simulation.SimulationEngine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenRoadPathTest {
    @Test
    void openingRoadCreatesPathToSafety() {
        String[] template = {
                "WVWW",
                "WHWW",
                "WWSW",
                "WWWW"
        };
        GameMap map = GameMap.fromTemplate(template);
        SimulationEngine engine = new SimulationEngine(new SimulationConfig(map, 0, 1, 5, new DefaultLavaSpreadStrategy()));
        Position wall = new Position(2,1); // wall between house (1,1) and safe (2,2)
        assertTrue(map.shortestPathToSafeZone(new Position(1,1)).isEmpty(), "Initially no path");
        assertTrue(engine.openRoad(wall));
        assertTrue(map.shortestPathToSafeZone(new Position(1,1)).isPresent(), "Path should exist after opening road");
    }
}
