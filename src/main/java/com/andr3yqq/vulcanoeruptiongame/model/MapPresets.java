package com.andr3yqq.vulcanoeruptiongame.model;

/**
 * Central place for map templates of different difficulties.
 */
public final class MapPresets {
    private MapPresets() {
    }

    public static GameMap easyMap() {
        return GameMap.fromTemplate(
                "WWWWWWWWWWWW",
                "W.S....S...W",
                "W.RRRWWRRR.W",
                "W.R..WW..R.W",
                "W.RH.RH.R..W",
                "W.R..V..R..W",
                "W.RH.RH.R..W",
                "W.R..WW..R.W",
                "W.RRRWWRRR.W",
                "W...S..S...W",
                "WWWWWWWWWWWW"
        );
    }

    public static GameMap normalMap() {
        return GameMap.fromTemplate(
                "WWWWWWWWWWWWWWW",
                "W..H....R...S.W",
                "W.WWW.RRR.WWW.W",
                "W.R..RRRR..R..W",
                "W.R.WWWWWW.R..W",
                "W.R.W..V.W.R..W",
                "W.R.WWWWWW.R..W",
                "W.R..RRRR..R..W",
                "W.WWW.RRR.WWW.W",
                "W.S...R..H....W",
                "WWWWWWWWWWWWWWW"
        );
    }

    public static GameMap hardMap() {
        return GameMap.fromTemplate(
                "WWWWWWWWWWWWWWWW",
                "W.H..R...S..R..W",
                "W.RWWRWWWWWRW.WW",
                "W.R..R....RRR..W",
                "W.R..R.WWWW.R..W",
                "W.WWWR.W..R.R..W",
                "W.R..R.WV.R.R..W",
                "W.R..R.W..R.R..W",
                "W.RWWR.WWWW.R..W",
                "W.R..RRRR..R.R.W",
                "W.S..R....RH...W",
                "WWWWWWWWWWWWWWWW"
        );
    }

    public static GameMap proceduralMap(long seed) {
        return RandomMapGenerator.generate(29, 23, 18, 3, seed);
    }
}
