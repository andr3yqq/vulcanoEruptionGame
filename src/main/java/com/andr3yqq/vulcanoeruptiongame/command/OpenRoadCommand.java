package com.andr3yqq.vulcanoeruptiongame.command;

import com.andr3yqq.vulcanoeruptiongame.model.Position;
import com.andr3yqq.vulcanoeruptiongame.simulation.SimulationEngine;

public record OpenRoadCommand(Position target) implements PlayerCommand {
    @Override
    public boolean execute(SimulationEngine engine) {
        return engine.openRoad(target);
    }

    @Override
    public String name() {
        return "Kelias atvertas ties " + target;
    }
}
