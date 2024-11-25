package com.project.angrybird;

import java.util.ArrayList;
import java.util.List;

public class GameStateManager {
    private GameState currentState;
    private final List<GameStateObserver> observers;

    public GameStateManager() {
        this.currentState = GameState.AIMING;
        this.observers = new ArrayList<>();
    }

    public void addObserver(GameStateObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(GameStateObserver observer) {
        observers.remove(observer);
    }

    public void setState(GameState newState) {
        currentState = newState;
        notifyObservers();
    }

    public GameState getState() {
        return currentState;
    }

    private void notifyObservers() {
        for (GameStateObserver observer : observers) {
            observer.onStateChange(currentState);
        }
    }
}

interface GameStateObserver {
    void onStateChange(GameState newState);
}
