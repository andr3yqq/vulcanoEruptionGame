package com.andr3yqq.vulcanoeruptiongame.factory;

import com.andr3yqq.vulcanoeruptiongame.model.GameMap;

public class PresetMapFactory implements MapFactory {
    private final GameMap template;

    public PresetMapFactory(GameMap template) {
        this.template = template;
    }

    @Override
    public GameMap create(long seed) {
        // Map is immutable in layout; return as-is
        return template;
    }
}
