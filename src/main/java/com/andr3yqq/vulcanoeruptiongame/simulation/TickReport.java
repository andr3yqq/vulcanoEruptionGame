package com.andr3yqq.vulcanoeruptiongame.simulation;

import com.andr3yqq.vulcanoeruptiongame.model.Position;
import com.andr3yqq.vulcanoeruptiongame.model.SimulationOutcome;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TickReport {
    private final int tick;
    private final List<Integer> savedCitizens = new ArrayList<>();
    private final List<Integer> lostCitizens = new ArrayList<>();
    private final List<Position> newLavaTiles = new ArrayList<>();
    @Setter
    private SimulationOutcome outcome;

    public TickReport(int tick) {
        this.tick = tick;
    }

}
