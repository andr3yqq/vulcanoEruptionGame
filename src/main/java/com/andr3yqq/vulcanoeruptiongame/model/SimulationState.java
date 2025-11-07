package com.andr3yqq.vulcanoeruptiongame.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimulationState {
    private final SimulationConfig config;
    private final List<Citizen> citizens;
    private final Set<Position> lavaCells = new HashSet<>();
    private final Deque<Position> lavaFront = new ArrayDeque<>();

    private int tick;
    private int barricadeActionsLeft;
    private int openRoadActionsLeft;
    private int savedCount;
    private int lostCount;
    private SimulationOutcome outcome = SimulationOutcome.RUNNING;

    private SimulationState(SimulationConfig config, List<Citizen> citizens) {
        this.config = config;
        this.citizens = citizens;
        this.barricadeActionsLeft = config.getBarricadeActions();
        this.openRoadActionsLeft = config.getOpenRoadActions();
        Position volcano = config.getMap().getVolcanoSource();
        lavaCells.add(volcano);
        lavaFront.add(volcano);
        config.getMap().getTile(volcano).setLava(true);
    }

    public static SimulationState bootstrap(SimulationConfig config) {
        List<Position> housePositions = config.getMap().getHouses();
        List<Citizen> citizens = new ArrayList<>();
        int count = 0;
        for (Position house : housePositions) {
            CitizenType type = (count % 2 == 0) ? CitizenType.SLOW : CitizenType.FAST;
            citizens.add(new Citizen(count, type, house));
            count++;
        }
        return new SimulationState(config, citizens);
    }

    public SimulationConfig getConfig() {
        return config;
    }

    public List<Citizen> getCitizens() {
        return citizens;
    }

    public int getTick() {
        return tick;
    }

    public void incrementTick() {
        this.tick++;
    }

    public int getBarricadeActionsLeft() {
        return barricadeActionsLeft;
    }

    public void decrementBarricade() {
        if (barricadeActionsLeft > 0) {
            barricadeActionsLeft--;
        }
    }

    public int getOpenRoadActionsLeft() {
        return openRoadActionsLeft;
    }

    public void decrementOpenRoad() {
        if (openRoadActionsLeft > 0) {
            openRoadActionsLeft--;
        }
    }

    public int getSavedCount() {
        return savedCount;
    }

    public int getLostCount() {
        return lostCount;
    }

    public Set<Position> getLavaCells() {
        return lavaCells;
    }

    public Deque<Position> getLavaFront() {
        return lavaFront;
    }

    public SimulationOutcome getOutcome() {
        return outcome;
    }

    public void setOutcome(SimulationOutcome outcome) {
        this.outcome = outcome;
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
