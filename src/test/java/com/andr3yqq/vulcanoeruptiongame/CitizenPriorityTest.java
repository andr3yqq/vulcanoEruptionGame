package com.andr3yqq.vulcanoeruptiongame;

import com.andr3yqq.vulcanoeruptiongame.model.GameMap;
import com.andr3yqq.vulcanoeruptiongame.model.MapPresets;
import com.andr3yqq.vulcanoeruptiongame.model.Position;
import com.andr3yqq.vulcanoeruptiongame.model.SimulationConfig;
import com.andr3yqq.vulcanoeruptiongame.simulation.SimulationEngine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CitizenPriorityTest {

    @Test
    void closerCitizensMoveFirst() {
        GameMap map = MapPresets.normalMap();
        SimulationEngine engine = new SimulationEngine(new SimulationConfig(map, 0,0,1, new com.andr3yqq.vulcanoeruptiongame.simulation.DefaultLavaSpreadStrategy()));
        // citizens 0 and 1 exist; id 0 is closer to volcano by construction
        int firstTick = engine.tick().getTick();
        assertEquals(1, firstTick);
        Position firstCitizenPos = engine.getState().getCitizens().get(0).getPosition();
        Position secondCitizenPos = engine.getState().getCitizens().get(1).getPosition();
        // both should have moved at least one step, but id0 (higher priority) should not lag behind
        assertTrue(firstCitizenPos.x() <= secondCitizenPos.x());
    }
}
