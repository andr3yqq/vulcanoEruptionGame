package com.andr3yqq.vulcanoeruptiongame.model;

import java.util.Locale;
import java.util.function.Supplier;

public enum DifficultyLevel {
    EASY("Lengva", () -> new SimulationConfig(MapPresets.easyMap(), 3, 3, 3)),
    NORMAL("Vidutinė", () -> new SimulationConfig(MapPresets.normalMap(), 2, 2, 2)),
    HARD("Sunki", () -> new SimulationConfig(MapPresets.hardMap(), 1, 1, 1));

    private final String displayName;
    private final Supplier<SimulationConfig> supplier;

    DifficultyLevel(String displayName, Supplier<SimulationConfig> supplier) {
        this.displayName = displayName;
        this.supplier = supplier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public SimulationConfig createConfig() {
        return supplier.get();
    }

    @Override
    public String toString() {
        return displayName;
    }
}
