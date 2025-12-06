package com.andr3yqq.vulcanoeruptiongame;

import com.andr3yqq.vulcanoeruptiongame.model.GameMap;
import com.andr3yqq.vulcanoeruptiongame.model.SimulationConfig;
import com.andr3yqq.vulcanoeruptiongame.simulation.DefaultLavaSpreadStrategy;
import com.andr3yqq.vulcanoeruptiongame.simulation.SimulationEngine;
import com.andr3yqq.vulcanoeruptiongame.simulation.TickReport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LavaKillsCitizenTest {
    @Test
    void citizenDiesOnLavaTile() {
        String[] template = {
                "WWWWW",
                "WVWWW",
                "WHWWS",
                "WWWWW"
        };
        GameMap map = GameMap.fromTemplate(template);
        SimulationEngine engine = new SimulationEngine(new SimulationConfig(map, 0,0,1,new DefaultLavaSpreadStrategy()));
        TickReport report = engine.tick(); // lava spreads immediately and reaches the house below volcano
        assertFalse(report.getLostCitizens().isEmpty(), "Citizen on lava path should die");
    }
}
