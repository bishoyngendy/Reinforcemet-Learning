package models;

import java.util.Random;
import java.util.Set;

public class Cell {
    private double oldValue;
    private double newValue;
    private boolean isBarrier;
    private double probabilities[];

    public Cell() {
        this.oldValue = 0.0;
        this.newValue = 0.0;
        this.isBarrier = false;
        this.probabilities = new double[4];
    }

    public void initializeProbabilities() {
        double total = 1;
        double sumTillNow = 0;
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            probabilities[i] = random.nextDouble() * total;
            total -= probabilities[i];
            sumTillNow += probabilities[i];
        }
        probabilities[4] = 1 - sumTillNow;
    }

    public boolean getBarrier() {
        return isBarrier;
    }

    public void setBarrier(boolean barrier) {
        isBarrier = barrier;
    }

    public void setOldValue(double oldValue) {
        this.oldValue = oldValue;
    }

    public void setNewValue(double newValue) {
        this.newValue = newValue;
    }

    public double[] getProbabilities() {
        return probabilities;
    }

    public double getOldValue() {
        return oldValue;
    }

    public void updateProbabilities(Set<Integer> newActions) {
        int numberOfNewActions = newActions.size();
        for (int i = 0; i < probabilities.length; i++) {
            if (newActions.contains(i)) {
                probabilities[i] = 1.0 / numberOfNewActions;
            }
        }
    }
}
