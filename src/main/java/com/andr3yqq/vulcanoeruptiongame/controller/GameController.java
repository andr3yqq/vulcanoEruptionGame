package com.andr3yqq.vulcanoeruptiongame.controller;

import com.andr3yqq.vulcanoeruptiongame.command.BuildBarricadeCommand;
import com.andr3yqq.vulcanoeruptiongame.command.OpenRoadCommand;
import com.andr3yqq.vulcanoeruptiongame.command.PlayerCommand;
import com.andr3yqq.vulcanoeruptiongame.model.DifficultyLevel;
import com.andr3yqq.vulcanoeruptiongame.model.Position;
import com.andr3yqq.vulcanoeruptiongame.model.SimulationConfig;
import com.andr3yqq.vulcanoeruptiongame.model.SimulationOutcome;
import com.andr3yqq.vulcanoeruptiongame.simulation.SimulationEngine;
import com.andr3yqq.vulcanoeruptiongame.simulation.SimulationListener;
import com.andr3yqq.vulcanoeruptiongame.simulation.SimulationStateView;
import com.andr3yqq.vulcanoeruptiongame.simulation.TickReport;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

/** Controller mediates between UI widgets and simulation/commands. */
public class GameController implements SimulationListener {
    private SimulationEngine engine;
    private final Deque<PlayerCommand> history = new ArrayDeque<>();
    @Getter
    @Setter
    private DifficultyLevel difficulty = DifficultyLevel.NORMAL;
    @Getter
    private long lastProceduralSeed;
    @Setter
    private Consumer<TickReport> tickConsumer = t -> {};
    @Setter
    private Consumer<SimulationStateView> stateConsumer = s -> {};

    public SimulationEngine newSimulation() {
        SimulationConfig config;
        if (difficulty.isProcedural()) {
            lastProceduralSeed = System.nanoTime();
            config = difficulty.createConfig(lastProceduralSeed);
        } else {
            lastProceduralSeed = 0L;
            config = difficulty.createConfig();
        }
        engine = new SimulationEngine(config);
        engine.addListener(this);
        history.clear();
        return engine;
    }

    public boolean execute(PlayerCommand command) {
        if (engine == null || engine.getState().getOutcome() != SimulationOutcome.RUNNING) {
            return false;
        }
        boolean success = command.execute(engine);
        if (success) {
            history.push(command);
        }
        return success;
    }

    public boolean barricade(Position p) { return execute(new BuildBarricadeCommand(p)); }
    public boolean openRoad(Position p) { return execute(new OpenRoadCommand(p)); }

    @Override
    public void onTick(TickReport report) {
        tickConsumer.accept(report);
    }

    @Override
    public void onStateChanged(SimulationStateView state) {
        stateConsumer.accept(state);
    }
}
