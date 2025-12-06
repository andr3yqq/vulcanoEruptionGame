package com.andr3yqq.vulcanoeruptiongame.factory;

import com.andr3yqq.vulcanoeruptiongame.model.GameMap;

public interface MapFactory {
    GameMap create(long seed);
}
