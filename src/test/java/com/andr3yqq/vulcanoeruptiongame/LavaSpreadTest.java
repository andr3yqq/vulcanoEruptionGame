package com.andr3yqq.vulcanoeruptiongame;

import com.andr3yqq.vulcanoeruptiongame.model.GameMap;
import com.andr3yqq.vulcanoeruptiongame.model.MapPresets;
import com.andr3yqq.vulcanoeruptiongame.model.Position;
import com.andr3yqq.vulcanoeruptiongame.model.SimulationConfig;
import com.andr3yqq.vulcanoeruptiongame.simulation.SimulationEngine;
import com.andr3yqq.vulcanoeruptiongame.simulation.TickReport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LavaSpreadTest {

    @Test
    void lavaResumesAfterBarricadeMelts() {
        GameMap map = MapPresets.normalMap();
        var config = new SimulationConfig(map, 1, 0, 1, new com.andr3yqq.vulcanoeruptiongame.simulation.DefaultLavaSpreadStrategy());
        SimulationEngine engine = new SimulationEngine(config);
        Position neighbor = new Position(map.getVolcanoSource().x() + 1, map.getVolcanoSource().y());
        engine.buildBarricade(neighbor);
        TickReport t1 = engine.tick(); // lava tries, melts barricade
        assertFalse(map.getTile(neighbor).isBarricaded());
        TickReport t2 = engine.tick(); // next tick should flood
        assertTrue(map.getTile(neighbor).hasLava(), "Lava should spread after barricade melts");
        assertTrue(t2.getNewLavaTiles().contains(neighbor));
    }
}
