package com.andr3yqq.vulcanoeruptiongame.simulation;

import com.andr3yqq.vulcanoeruptiongame.model.Citizen;
import com.andr3yqq.vulcanoeruptiongame.model.GameMap;
import com.andr3yqq.vulcanoeruptiongame.model.Position;
import com.andr3yqq.vulcanoeruptiongame.model.SimulationConfig;
import com.andr3yqq.vulcanoeruptiongame.model.SimulationOutcome;
import com.andr3yqq.vulcanoeruptiongame.model.SimulationState;
import com.andr3yqq.vulcanoeruptiongame.model.Tile;
import com.andr3yqq.vulcanoeruptiongame.model.TileType;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Pure simulation logic; the UI layer will call {@link #tick()} on a schedule.
 */
public class SimulationEngine {

    private final SimulationState state;
    private final GameMap map;

    public SimulationEngine(SimulationConfig config) {
        this.state = SimulationState.bootstrap(config);
        this.map = config.getMap();
    }

    public SimulationState getState() {
        return state;
    }

    public TickReport tick() {
        if (state.getOutcome() != SimulationOutcome.RUNNING) {
            TickReport idleReport = new TickReport(state.getTick());
            idleReport.setOutcome(state.getOutcome());
            return idleReport;
        }

        state.incrementTick();
        TickReport report = new TickReport(state.getTick());

        moveCitizens(report);
        if (state.getTick() % state.getConfig().getLavaSpreadInterval() == 0) {
            spreadLava(report);
        }
        evaluateOutcome(report);
        return report;
    }

    private void moveCitizens(TickReport report) {
        for (Citizen citizen : state.getCitizens()) {
            if (!citizen.isAlive() || citizen.isSafe()) {
                continue;
            }
            ensurePath(citizen);
            for (int step = 0; step < citizen.getType().getTilesPerTick(); step++) {
                if (!citizen.hasPath()) {
                    ensurePath(citizen);
                    if (!citizen.hasPath()) {
                        break; // stuck for now
                    }
                }
                Position next = citizen.nextStep().orElse(null);
                if (next == null) {
                    break;
                }
                citizen.setPosition(next);
                Tile tile = map.getTile(next);
                if (tile.hasLava()) {
                    state.markCitizenDead(citizen);
                    report.getLostCitizens().add(citizen.getId());
                    break;
                }
                if (map.isSafeZone(next)) {
                    state.markCitizenSafe(citizen);
                    report.getSavedCitizens().add(citizen.getId());
                    break;
                }
            }
        }
    }

    private void ensurePath(Citizen citizen) {
        if (citizen.hasPath()) {
            return;
        }
        map.shortestPathToSafeZone(citizen.getPosition()).ifPresent(path -> {
            Deque<Position> deque = new ArrayDeque<>(path);
            if (!deque.isEmpty()) {
                deque.pollFirst(); // remove current tile
            }
            citizen.setPath(deque);
        });
    }

    private void spreadLava(TickReport report) {
        Deque<Position> front = state.getLavaFront();
        int expansions = front.size();
        if (expansions == 0) {
            return;
        }
        Deque<Position> newFront = new ArrayDeque<>();
        for (int i = 0; i < expansions; i++) {
            Position source = front.pollFirst();
            for (Position neighbor : map.neighbors(source)) {
                if (state.getLavaCells().contains(neighbor)) {
                    continue;
                }
                Tile tile = map.getTile(neighbor);
                if (tile.isBarricaded()) {
                    tile.setBarricaded(false); // barricade buys time but melts here
                    continue;
                }
                tile.setLava(true);
                state.getLavaCells().add(neighbor);
                newFront.addLast(neighbor);
                report.getNewLavaTiles().add(neighbor);
                eliminateCitizensOn(neighbor, report);
            }
        }
        front.addAll(newFront);
    }

    private void eliminateCitizensOn(Position tilePos, TickReport report) {
        for (Citizen citizen : state.getCitizens()) {
            if (citizen.isAlive() && !citizen.isSafe() && citizen.getPosition().equals(tilePos)) {
                state.markCitizenDead(citizen);
                report.getLostCitizens().add(citizen.getId());
            }
        }
    }

    private void evaluateOutcome(TickReport report) {
        if (state.getOutcome() != SimulationOutcome.RUNNING) {
            report.setOutcome(state.getOutcome());
            return;
        }
        if (state.everyoneResolved()) {
            state.setOutcome(state.getSavedCount() > 0 ? SimulationOutcome.VICTORY : SimulationOutcome.FAILURE);
        } else if (state.getLostCount() == state.getCitizens().size()) {
            state.setOutcome(SimulationOutcome.FAILURE);
        }
        report.setOutcome(state.getOutcome());
    }

    public boolean buildBarricade(Position position) {
        if (state.getBarricadeActionsLeft() <= 0) {
            return false;
        }
        if (!map.isInside(position)) {
            return false;
        }
        Tile tile = map.getTile(position);
        if (tile.getType() != TileType.ROAD || tile.hasLava()) {
            return false;
        }
        tile.setBarricaded(true);
        state.decrementBarricade();
        return true;
    }

    public boolean openRoad(Position position) {
        if (state.getOpenRoadActionsLeft() <= 0) {
            return false;
        }
        if (!map.isInside(position)) {
            return false;
        }
        Tile tile = map.getTile(position);
        if (tile.getType() != TileType.WALL) {
            return false;
        }
        tile.setType(TileType.ROAD);
        state.decrementOpenRoad();
        return true;
    }
}
