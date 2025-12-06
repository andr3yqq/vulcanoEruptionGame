package com.andr3yqq.vulcanoeruptiongame.command;

import com.andr3yqq.vulcanoeruptiongame.simulation.SimulationEngine;

public interface PlayerCommand {
    boolean execute(SimulationEngine engine);
    String name();
}
