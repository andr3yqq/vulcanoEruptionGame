package com.andr3yqq.vulcanoeruptiongame.model.agent;

import com.andr3yqq.vulcanoeruptiongame.model.Position;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Agent {
    private final int id;
    private Position position;
    private boolean alive = true;

    protected Agent(int id, Position start) {
        this.id = id;
        this.position = start;
    }

    public void kill() {
        this.alive = false;
    }
}
