package com.andr3yqq.vulcanoeruptiongame.simulation;

import com.andr3yqq.vulcanoeruptiongame.model.GameMap;
import com.andr3yqq.vulcanoeruptiongame.model.Position;
import com.andr3yqq.vulcanoeruptiongame.model.SimulationState;
import com.andr3yqq.vulcanoeruptiongame.model.Tile;

import java.util.HashSet;
import java.util.Set;

/**
 * Floods outward from every lava tile, melting barricades in one tick.
 */
public class DefaultLavaSpreadStrategy implements LavaSpreadStrategy {
    @Override
    public Set<Position> spread(GameMap map, SimulationState state) {
        Set<Position> newlyLava = new HashSet<>();
        for (Position source : state.getLavaCells()) {
            for (Position neighbor : map.neighbors(source)) {
                if (state.getLavaCells().contains(neighbor) || newlyLava.contains(neighbor)) {
                    continue;
                }
                Tile tile = map.getTile(neighbor);
                if (tile.isBarricaded()) {
                    tile.setBarricaded(false);
                    continue;
                }
                tile.setLava(true);
                newlyLava.add(neighbor);
            }
        }
        state.getLavaCells().addAll(newlyLava);
        return newlyLava;
    }
}
