package com.andr3yqq.vulcanoeruptiongame.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

@Getter
@Setter
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

    public void kill() {
        this.alive = false;
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
