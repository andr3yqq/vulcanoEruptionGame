package com.andr3yqq.vulcanoeruptiongame.model;

import com.andr3yqq.vulcanoeruptiongame.model.agent.Agent;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

@Getter
public class Citizen extends Agent {
    private final CitizenType type;
    private final int priority;
    private boolean safe = false;
    @Setter
    private Deque<Position> plannedPath = new ArrayDeque<>();

    public Citizen(int id, CitizenType type, int priority, Position start) {
        super(id, start);
        this.type = type;
        this.priority = priority;
    }

    public void markSafe() {
        this.safe = true;
    }

    public Optional<Position> nextStep() {
        return Optional.ofNullable(plannedPath.pollFirst());
    }

    public boolean hasPath() {
        return plannedPath != null && !plannedPath.isEmpty();
    }
}
