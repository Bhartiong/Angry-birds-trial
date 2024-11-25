package com.project.angrybird;

import java.util.List;

public class Level {
    private List<Pig> pigs;
    private List<Obstacle> obstacles;
    private List<Bird> birds;

    public Level(List<Pig> pigs, List<Obstacle> obstacles, List<Bird> birds) {
        this.pigs = pigs;
        this.obstacles = obstacles;
        this.birds = birds;
    }

    public List<Pig> getPigs() {
        return pigs;
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public List<Bird> getBirds() {
        return birds;
    }
}
