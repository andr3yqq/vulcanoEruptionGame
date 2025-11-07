package com.andr3yqq.vulcanoeruptiongame.model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

public class Citizen {
    private final int id;
    private final CitizenType type;
    private final int priority;
    private Position position;
    private boolean alive = true;
    private boolean safe = false;
    private Deque<Position> plannedPath = new ArrayDeque<>();

    public Citizen(int id, CitizenType type, int priority, Position start) {
        this.id = id;
        this.type = type;
        this.priority = priority;
        this.position = start;
    }

    public int getId() {
        return id;
    }

    public CitizenType getType() {
        return type;
    }

    public int getPriority() {
        return priority;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        this.alive = false;
    }

    public boolean isSafe() {
        return safe;
    }

    public void markSafe() {
        this.safe = true;
    }

    public void setPath(Deque<Position> path) {
        this.plannedPath = path;
    }

    public Optional<Position> nextStep() {
        return Optional.ofNullable(plannedPath.pollFirst());
    }

    public boolean hasPath() {
        return plannedPath != null && !plannedPath.isEmpty();
    }
}
