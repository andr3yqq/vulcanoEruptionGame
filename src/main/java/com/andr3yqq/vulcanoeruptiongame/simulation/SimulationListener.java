package com.andr3yqq.vulcanoeruptiongame.simulation;

public interface SimulationListener {
    void onTick(TickReport report);
    void onStateChanged(SimulationStateView state);
}
