package com.andr3yqq.vulcanoeruptiongame.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

/**
 * Procedurally carves roads and places volcano/houses/safe zones.
 */
public final class RandomMapGenerator {

    private RandomMapGenerator() {
    }

    public static GameMap generate(int width, int height, int houseCount, int safeZoneCount, long seed) {
        int w = Math.max(15, width | 1); // force odd dimensions for maze carving
        int h = Math.max(15, height | 1);
        char[][] layout = new char[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                layout[y][x] = 'W';
            }
        }

        Random random = new Random(seed);
        carveMaze(layout, random);
        openExtraPassages(layout, random, 0.12);

        List<Position> roadTiles = collectRoads(layout);
        if (roadTiles.isEmpty()) {
            throw new IllegalStateException("No roads generated");
        }

        Position volcano = selectVolcano(layout, roadTiles, random);
        layout[volcano.y()][volcano.x()] = 'V';

        Map<Position, Integer> distance = computeDistances(layout, volcano);
        List<Position> safeZones = selectSafeZones(layout, distance, safeZoneCount);
        for (Position pos : safeZones) {
            layout[pos.y()][pos.x()] = 'S';
        }

        List<Position> houses = selectHouses(layout, distance, houseCount, random);
        for (Position pos : houses) {
            layout[pos.y()][pos.x()] = 'H';
        }

        String[] template = new String[h];
        for (int y = 0; y < h; y++) {
            template[y] = new String(layout[y]);
        }
        return GameMap.fromTemplate(template);
    }

    private static void carveMaze(char[][] grid, Random random) {
        int h = grid.length;
        int w = grid[0].length;
        boolean[][] visited = new boolean[h][w];
        Deque<Position> stack = new ArrayDeque<>();
        Position start = new Position(1, 1);
        visited[start.y()][start.x()] = true;
        grid[start.y()][start.x()] = '.';
        stack.push(start);

        int[][] directions = {{2, 0}, {-2, 0}, {0, 2}, {0, -2}};

        while (!stack.isEmpty()) {
            Position current = stack.peek();
            List<int[]> neighbors = new ArrayList<>();
            for (int[] dir : directions) {
                int nx = current.x() + dir[0];
                int ny = current.y() + dir[1];
                if (nx <= 0 || ny <= 0 || nx >= w - 1 || ny >= h - 1) {
                    continue;
                }
                if (!visited[ny][nx]) {
                    neighbors.add(new int[]{nx, ny, dir[0], dir[1]});
                }
            }
            if (neighbors.isEmpty()) {
                stack.pop();
                continue;
            }
            int[] choice = neighbors.get(random.nextInt(neighbors.size()));
            int nx = choice[0];
            int ny = choice[1];
            int betweenX = current.x() + choice[2] / 2;
            int betweenY = current.y() + choice[3] / 2;
            grid[betweenY][betweenX] = '.';
            grid[ny][nx] = '.';
            visited[ny][nx] = true;
            stack.push(new Position(nx, ny));
        }
    }

    private static void openExtraPassages(char[][] grid, Random random, double chance) {
        int h = grid.length;
        int w = grid[0].length;
        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                if (grid[y][x] == 'W' && random.nextDouble() < chance) {
                    grid[y][x] = '.';
                }
            }
        }
    }

    private static List<Position> collectRoads(char[][] grid) {
        List<Position> roads = new ArrayList<>();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                if (grid[y][x] == '.') {
                    roads.add(new Position(x, y));
                }
            }
        }
        return roads;
    }

    private static Position selectVolcano(char[][] grid, List<Position> roads, Random random) {
        Position center = new Position(grid[0].length / 2, grid.length / 2);
        return roads.stream()
                .min(Comparator.comparingInt(pos -> manhattan(pos, center) + random.nextInt(4)))
                .orElse(roads.get(0));
    }

    private static Map<Position, Integer> computeDistances(char[][] grid, Position start) {
        Map<Position, Integer> dist = new HashMap<>();
        Queue<Position> queue = new ArrayDeque<>();
        queue.add(start);
        dist.put(start, 0);
        while (!queue.isEmpty()) {
            Position current = queue.poll();
            int currentDist = dist.get(current);
            for (Position neighbor : current.neighbors4()) {
                if (!isRoadLike(grid, neighbor)) {
                    continue;
                }
                if (dist.containsKey(neighbor)) {
                    continue;
                }
                dist.put(neighbor, currentDist + 1);
                queue.add(neighbor);
            }
        }
        return dist;
    }

    private static boolean isRoadLike(char[][] grid, Position p) {
        return p.isInside(grid[0].length, grid.length) && grid[p.y()][p.x()] != 'W';
    }

    private static List<Position> selectSafeZones(char[][] grid, Map<Position, Integer> distances, int safeCount) {
        List<Position> candidates = new ArrayList<>();
        for (Map.Entry<Position, Integer> entry : distances.entrySet()) {
            Position pos = entry.getKey();
            if (grid[pos.y()][pos.x()] == '.') {
                candidates.add(pos);
            }
        }
        candidates.sort(Comparator.comparingInt(distances::get).reversed());
        List<Position> safeZones = new ArrayList<>();
        for (Position candidate : candidates) {
            if (safeZones.size() >= safeCount) {
                break;
            }
            if (isEdgeTile(candidate, grid)) {
                safeZones.add(candidate);
            }
        }
        int idx = 0;
        while (safeZones.size() < safeCount && idx < candidates.size()) {
            Position fallback = candidates.get(idx++);
            if (!safeZones.contains(fallback)) {
                safeZones.add(fallback);
            }
        }
        return safeZones;
    }

    private static boolean isEdgeTile(Position pos, char[][] grid) {
        return pos.x() <= 1 || pos.y() <= 1 || pos.x() >= grid[0].length - 2 || pos.y() >= grid.length - 2;
    }

    private static List<Position> selectHouses(char[][] grid, Map<Position, Integer> distances, int count, Random random) {
        List<Position> candidates = new ArrayList<>();
        for (Map.Entry<Position, Integer> entry : distances.entrySet()) {
            Position pos = entry.getKey();
            char symbol = grid[pos.y()][pos.x()];
            if (symbol == '.' || symbol == 'R') {
                candidates.add(pos);
            }
        }
        candidates.sort(Comparator.comparingInt(distances::get));
        List<Position> houses = new ArrayList<>();
        for (Position candidate : candidates) {
            if (houses.size() >= count) {
                break;
            }
            if (grid[candidate.y()][candidate.x()] == '.' && random.nextDouble() > 0.25) {
                houses.add(candidate);
            }
        }
        int tries = 0;
        while (houses.size() < count && tries < candidates.size()) {
            Position backup = candidates.get(random.nextInt(candidates.size()));
            if (grid[backup.y()][backup.x()] == '.') {
                houses.add(backup);
            }
            tries++;
        }
        return houses;
    }

    private static int manhattan(Position a, Position b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }
}
