package com.andr3yqq.vulcanoeruptiongame.simulation;

import com.andr3yqq.vulcanoeruptiongame.model.GameMap;
import com.andr3yqq.vulcanoeruptiongame.model.Position;
import com.andr3yqq.vulcanoeruptiongame.model.SimulationState;

import java.util.Set;

/**
 * Strategy interface for lava propagation.
 */
public interface LavaSpreadStrategy {
    Set<Position> spread(GameMap map, SimulationState state);
}
