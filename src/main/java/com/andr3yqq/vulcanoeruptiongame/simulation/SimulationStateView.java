package com.andr3yqq.vulcanoeruptiongame.simulation;

import com.andr3yqq.vulcanoeruptiongame.model.SimulationState;
import com.andr3yqq.vulcanoeruptiongame.model.SimulationOutcome;
import lombok.Getter;

/**
 * Read-only snapshot exposed to observers to preserve encapsulation.
 */
@Getter
public class SimulationStateView {
    private final int tick;
    private final int savedCount;
    private final int lostCount;
    private final int barricadeActionsLeft;
    private final int openRoadActionsLeft;
    private final SimulationOutcome outcome;

    public SimulationStateView(SimulationState state) {
        this.tick = state.getTick();
        this.savedCount = state.getSavedCount();
        this.lostCount = state.getLostCount();
        this.barricadeActionsLeft = state.getBarricadeActionsLeft();
        this.openRoadActionsLeft = state.getOpenRoadActionsLeft();
        this.outcome = state.getOutcome();
    }
}
