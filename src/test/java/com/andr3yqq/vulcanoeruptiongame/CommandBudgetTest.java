package com.andr3yqq.vulcanoeruptiongame;

import com.andr3yqq.vulcanoeruptiongame.model.Position;
import com.andr3yqq.vulcanoeruptiongame.simulation.SimulationEngine;
import com.andr3yqq.vulcanoeruptiongame.model.SimulationConfig;
import com.andr3yqq.vulcanoeruptiongame.simulation.DefaultLavaSpreadStrategy;
import com.andr3yqq.vulcanoeruptiongame.model.MapPresets;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandBudgetTest {
    @Test
    void barricadeBudgetDecreases() {
        SimulationEngine engine = new SimulationEngine(new SimulationConfig(MapPresets.normalMap(),1,0,2,new DefaultLavaSpreadStrategy()));
        assertTrue(engine.buildBarricade(new Position(1,1)));
        assertFalse(engine.buildBarricade(new Position(2,1)), "Budget should be exhausted");
    }
}
