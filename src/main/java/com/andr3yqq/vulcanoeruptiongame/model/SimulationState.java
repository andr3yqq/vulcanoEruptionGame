package com.andr3yqq.vulcanoeruptiongame.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class SimulationState {
    private final SimulationConfig config;
    private final List<Citizen> citizens;
    private final Set<Position> lavaCells = new HashSet<>();

    private int tick;
    private int barricadeActionsLeft;
    private int openRoadActionsLeft;
    private int savedCount;
    private int lostCount;
    @Setter
    private SimulationOutcome outcome = SimulationOutcome.RUNNING;

    private SimulationState(SimulationConfig config, List<Citizen> citizens) {
        this.config = config;
        this.citizens = citizens;
        this.barricadeActionsLeft = config.getBarricadeActions();
        this.openRoadActionsLeft = config.getOpenRoadActions();
        Position volcano = config.getMap().getVolcanoSource();
        lavaCells.add(volcano);
        config.getMap().getTile(volcano).setLava(true);
    }

    public static SimulationState bootstrap(SimulationConfig config) {
        List<Position> housePositions = config.getMap().getHouses();
        List<Citizen> citizens = new ArrayList<>();
        int count = 0;
        Position volcano = config.getMap().getVolcanoSource();
        for (Position house : housePositions) {
            CitizenType type = (count % 2 == 0) ? CitizenType.SLOW : CitizenType.FAST;
            int manhattan = Math.abs(house.x() - volcano.x()) + Math.abs(house.y() - volcano.y());
            int priority = manhattan <= 3 ? 3 : manhattan <= 6 ? 2 : 1;
            citizens.add(new Citizen(count, type, priority, house));
            count++;
        }
        return new SimulationState(config, citizens);
    }

    public void incrementTick() {
        this.tick++;
    }

    public void decrementBarricade() {
        if (barricadeActionsLeft > 0) {
            barricadeActionsLeft--;
        }
    }

    public void decrementOpenRoad() {
        if (openRoadActionsLeft > 0) {
            openRoadActionsLeft--;
        }
    }

    public void markCitizenSafe(Citizen citizen) {
        if (!citizen.isSafe()) {
            citizen.markSafe();
            savedCount++;
        }
    }

    public void markCitizenDead(Citizen citizen) {
        if (citizen.isAlive()) {
            citizen.kill();
            lostCount++;
        }
    }

    public boolean everyoneResolved() {
        return savedCount + lostCount == citizens.size();
    }
}
