package com.andr3yqq.vulcanoeruptiongame.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

/**
 * Immutable layout describing roads/houses/safe zones. Dynamic state (lava, barricades)
 * lives inside {@link Tile} instances.
 */
public class GameMap {

    private final Tile[][] grid;
    private final List<Position> houses;
    private final List<Position> safeZones;
    private final Position volcanoSource;

    public GameMap(Tile[][] grid, List<Position> houses, List<Position> safeZones, Position volcanoSource) {
        this.grid = grid;
        this.houses = houses;
        this.safeZones = safeZones;
        this.volcanoSource = volcanoSource;
    }

    public int getWidth() {
        return grid[0].length;
    }

    public int getHeight() {
        return grid.length;
    }

    public Tile getTile(Position p) {
        return grid[p.y()][p.x()];
    }

    public List<Position> getHouses() {
        return houses;
    }

    public List<Position> getSafeZones() {
        return safeZones;
    }

    public Position getVolcanoSource() {
        return volcanoSource;
    }

    public boolean isInside(Position p) {
        return p.x() >= 0 && p.y() >= 0 && p.x() < getWidth() && p.y() < getHeight();
    }

    public boolean isTraversable(Position p) {
        return isInside(p) && getTile(p).isTraversable();
    }

    public boolean isSafeZone(Position p) {
        return isInside(p) && getTile(p).getType() == TileType.SAFE_ZONE;
    }

    public List<Position> neighbors(Position p) {
        List<Position> filtered = new ArrayList<>(4);
        for (Position neighbor : p.neighbors4()) {
            if (!isInside(neighbor)) {
                continue;
            }
            TileType type = getTile(neighbor).getType();
            if (type == TileType.WALL || type == TileType.VOLCANO) {
                continue;
            }
            filtered.add(neighbor);
        }
        return filtered;
    }

    /**
     * BFS for the current traversable layout (lava/barricades considered).
     */
    public Optional<List<Position>> shortestPathToSafeZone(Position from) {
        Queue<Position> queue = new ArrayDeque<>();
        Map<Position, Position> parent = new HashMap<>();
        queue.add(from);
        parent.put(from, null);

        while (!queue.isEmpty()) {
            Position current = queue.poll();
            if (!from.equals(current) && isSafeZone(current)) {
                return Optional.of(reconstructPath(parent, current));
            }
            for (Position neighbor : neighbors(current)) {
                if (parent.containsKey(neighbor)) {
                    continue;
                }
                if (!getTile(neighbor).isTraversable() && !isSafeZone(neighbor)) {
                    continue;
                }
                parent.put(neighbor, current);
                queue.add(neighbor);
            }
        }
        return Optional.empty();
    }

    private List<Position> reconstructPath(Map<Position, Position> parent, Position target) {
        List<Position> path = new ArrayList<>();
        Position cursor = target;
        while (cursor != null) {
            path.add(0, cursor);
            cursor = parent.get(cursor);
        }
        return path;
    }

    public static GameMap demoMap() {
        String[] template = new String[]{
                "WWWWWWWWWWWW",
                "W..H....S..W",
                "W..RRRRRR..W",
                "W..RWWWWR..W",
                "W..R....R..W",
                "W..R.V..R..W",
                "W..R....R..W",
                "W..RRRRRR..W",
                "W..H....S..W",
                "WWWWWWWWWWWW"
        };
        int height = template.length;
        int width = template[0].length();
        Tile[][] grid = new Tile[height][width];
        List<Position> houses = new ArrayList<>();
        List<Position> safeZones = new ArrayList<>();
        Position volcano = null;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                char symbol = template[y].charAt(x);
                Tile tile;
                switch (symbol) {
                    case 'H' -> {
                        tile = new Tile(TileType.HOUSE);
                        houses.add(new Position(x, y));
                    }
                    case 'S' -> {
                        tile = new Tile(TileType.SAFE_ZONE);
                        safeZones.add(new Position(x, y));
                    }
                    case 'V' -> {
                        tile = new Tile(TileType.VOLCANO);
                        volcano = new Position(x, y);
                    }
                    case 'W' -> tile = new Tile(TileType.WALL);
                    case 'R', '.' -> tile = new Tile(TileType.ROAD);
                    default -> throw new IllegalStateException("Unknown symbol: " + symbol);
                }
                grid[y][x] = tile;
            }
        }
        if (volcano == null) {
            throw new IllegalStateException("Map template missing volcano vent");
        }
        return new GameMap(grid, houses, safeZones, volcano);
    }
}
