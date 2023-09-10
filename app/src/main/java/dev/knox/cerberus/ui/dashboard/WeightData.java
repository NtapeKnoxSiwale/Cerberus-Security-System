package dev.knox.cerberus.ui.dashboard;

import java.util.ArrayList;
import java.util.List;

public class WeightData {
    private static WeightData instance;
    private List<Double> room1Weights;
    private List<Double> room2Weights;
    private List<WeightDataObserver> observers;

    private WeightData() {
        // Private constructor to prevent instantiation
        room1Weights = new ArrayList<>();
        room2Weights = new ArrayList<>();
        observers = new ArrayList<>();
    }

    public static WeightData getInstance() {
        if (instance == null) {
            instance = new WeightData();
        }
        return instance;
    }

    public List<Double> getRoom1Weights() {
        return room1Weights;
    }

    public List<Double> getRoom2Weights() {
        return room2Weights;
    }

    public void addRoom1Weight(Double room1Weight) {
        room1Weights.add(room1Weight);
        notifyObservers();
    }

    public void addRoom2Weight(Double room2Weight) {
        room2Weights.add(room2Weight);
        notifyObservers();
    }

    public void addObserver(WeightDataObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(WeightDataObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (WeightDataObserver observer : observers) {
            observer.onWeightChanged();
        }
    }
}
