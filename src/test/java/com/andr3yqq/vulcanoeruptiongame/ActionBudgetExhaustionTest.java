package com.andr3yqq.vulcanoeruptiongame;

import com.andr3yqq.vulcanoeruptiongame.model.GameMap;
import com.andr3yqq.vulcanoeruptiongame.model.Position;
import com.andr3yqq.vulcanoeruptiongame.model.SimulationConfig;
import com.andr3yqq.vulcanoeruptiongame.simulation.DefaultLavaSpreadStrategy;
import com.andr3yqq.vulcanoeruptiongame.simulation.SimulationEngine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActionBudgetExhaustionTest {
    @Test
    void openRoadBudgetExhausts() {
        String[] template = {
                "WWWWW",
                "WVWWW",
                "WWWWS",
                "WWWWW"
        };
        GameMap map = GameMap.fromTemplate(template);
        SimulationEngine engine = new SimulationEngine(new SimulationConfig(map,0,1,2,new DefaultLavaSpreadStrategy()));
        Position wall = new Position(2,2);
        assertTrue(engine.openRoad(wall), "First open road should succeed on wall");
        assertFalse(engine.openRoad(new Position(3,2)), "Second open road should fail due to budget/wrong tile");
    }
}
