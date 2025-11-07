package com.andr3yqq.vulcanoeruptiongame.ui;

import com.andr3yqq.vulcanoeruptiongame.model.Citizen;
import com.andr3yqq.vulcanoeruptiongame.model.GameMap;
import com.andr3yqq.vulcanoeruptiongame.model.Position;
import com.andr3yqq.vulcanoeruptiongame.model.Tile;
import com.andr3yqq.vulcanoeruptiongame.model.TileType;
import com.andr3yqq.vulcanoeruptiongame.simulation.SimulationEngine;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Draws the grid-based map, lava spread, and citizens onto a provided canvas.
 */
public class MapRenderer {
    private final Canvas canvas;
    private final SimulationEngine engine;
    private double cellSize;

    public MapRenderer(Canvas canvas, SimulationEngine engine) {
        this.canvas = canvas;
        this.engine = engine;
    }

    public void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        GameMap map = engine.getState().getConfig().getMap();
        cellSize = Math.min(
                canvas.getWidth() / map.getWidth(),
                canvas.getHeight() / map.getHeight());

        drawTiles(gc, map);
        drawCitizens(gc);
        drawGrid(gc, map);
    }

    private void drawTiles(GraphicsContext gc, GameMap map) {
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                Position position = new Position(x, y);
                Tile tile = map.getTile(position);
                Color baseColor = switch (tile.getType()) {
                    case ROAD -> Color.DARKGRAY;
                    case HOUSE -> Color.BEIGE;
                    case SAFE_ZONE -> Color.DARKSEAGREEN;
                    case VOLCANO -> Color.DARKRED;
                    case WALL -> Color.DIMGRAY;
                };
                double px = x * cellSize;
                double py = y * cellSize;
                gc.setFill(baseColor);
                gc.fillRect(px, py, cellSize, cellSize);

                if (tile.isBarricaded()) {
                    gc.setStroke(Color.GOLDENROD);
                    gc.setLineWidth(2);
                    gc.strokeLine(px, py, px + cellSize, py + cellSize);
                    gc.strokeLine(px, py + cellSize, px + cellSize, py);
                }
                if (tile.hasLava()) {
                    gc.setFill(Color.color(0.85, 0.25, 0.1, 0.8));
                    gc.fillRect(px, py, cellSize, cellSize);
                }
            }
        }
    }

    private void drawCitizens(GraphicsContext gc) {
        gc.setFont(Font.font(cellSize * 0.4));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        for (Citizen citizen : engine.getState().getCitizens()) {
            if (!citizen.isAlive() || citizen.isSafe()) {
                continue;
            }
            Position position = citizen.getPosition();
            double px = position.x() * cellSize;
            double py = position.y() * cellSize;
            double diameter = cellSize * 0.35;
            double cx = px + cellSize / 2;
            double cy = py + cellSize / 2;
            gc.setFill(citizen.getType().name().equals("FAST") ? Color.CORNFLOWERBLUE : Color.LIGHTBLUE);
            gc.fillOval(cx - diameter / 2, cy - diameter / 2, diameter, diameter);
            Color priorityColor = switch (citizen.getPriority()) {
                case 3 -> Color.GOLD;
                case 2 -> Color.ORANGE;
                default -> Color.SLATEGRAY;
            };
            gc.setStroke(priorityColor);
            gc.setLineWidth(2);
            gc.strokeOval(cx - diameter / 2, cy - diameter / 2, diameter, diameter);
            gc.setFill(Color.BLACK);
            gc.fillText(String.valueOf(citizen.getId()), cx, cy);
        }
    }

    private void drawGrid(GraphicsContext gc, GameMap map) {
        gc.setStroke(Color.color(0, 0, 0, 0.2));
        gc.setLineWidth(1);
        for (int x = 0; x <= map.getWidth(); x++) {
            double px = x * cellSize;
            gc.strokeLine(px, 0, px, map.getHeight() * cellSize);
        }
        for (int y = 0; y <= map.getHeight(); y++) {
            double py = y * cellSize;
            gc.strokeLine(0, py, map.getWidth() * cellSize, py);
        }
    }

    public Position pickCell(double canvasX, double canvasY) {
        if (cellSize <= 0) {
            return new Position(0, 0);
        }
        int gridX = (int) Math.floor(canvasX / cellSize);
        int gridY = (int) Math.floor(canvasY / cellSize);
        GameMap map = engine.getState().getConfig().getMap();
        gridX = Math.max(0, Math.min(map.getWidth() - 1, gridX));
        gridY = Math.max(0, Math.min(map.getHeight() - 1, gridY));
        return new Position(gridX, gridY);
    }
}
