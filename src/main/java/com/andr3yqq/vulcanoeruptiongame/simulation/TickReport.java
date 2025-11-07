package com.andr3yqq.vulcanoeruptiongame.simulation;

import com.andr3yqq.vulcanoeruptiongame.model.Position;
import com.andr3yqq.vulcanoeruptiongame.model.SimulationOutcome;

import java.util.ArrayList;
import java.util.List;

public class TickReport {
    private final int tick;
    private final List<Integer> savedCitizens = new ArrayList<>();
    private final List<Integer> lostCitizens = new ArrayList<>();
    private final List<Position> newLavaTiles = new ArrayList<>();
    private SimulationOutcome outcome;

    public TickReport(int tick) {
        this.tick = tick;
    }

    public int getTick() {
        return tick;
    }

    public List<Integer> getSavedCitizens() {
        return savedCitizens;
    }

    public List<Integer> getLostCitizens() {
        return lostCitizens;
    }

    public List<Position> getNewLavaTiles() {
        return newLavaTiles;
    }

    public SimulationOutcome getOutcome() {
        return outcome;
    }

    public void setOutcome(SimulationOutcome outcome) {
        this.outcome = outcome;
    }
}
