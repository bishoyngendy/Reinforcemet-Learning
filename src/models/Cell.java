package models;

import java.util.Arrays;
import java.util.Random;
import java.util.Set;

public class Cell implements Cloneable {
    private double oldValue;
    private double newValue;
    private boolean barrier;
    private double probabilities[];

    public Cell() {
        this.oldValue = 0.0;
        this.newValue = 0.0;
        this.barrier = false;
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
        probabilities[3] = 1 - sumTillNow;
    }

    public boolean isBarrier() {
        return barrier;
    }

    public void setBarrier(boolean barrier) {
        this.barrier = barrier;
    }

    public double getOldValue() {
        return oldValue;
    }

    public void setOldValue(double oldValue) {
        this.oldValue = oldValue;
    }

    public double getNewValue() {
        return newValue;
    }

    public void setNewValue(double newValue) {
        this.newValue = newValue;
    }

    public double[] getProbabilities() {
        return probabilities;
    }

    public void setProbabilities(double[] probabilities) {
        this.probabilities = probabilities;
    }

    public void updateProbabilities(Set<Integer> newActions) {
        int numberOfNewActions = newActions.size();
        for (int i = 0; i < probabilities.length; i++) {
            if (newActions.contains(i)) {
                probabilities[i] = 1.0 / numberOfNewActions;
            } else {
                probabilities[i] = 0.0;
            }
        }
    }


    public void updatePolicy(int newAction) {
        for (int i = 0; i < probabilities.length; i++) {
            if (i == newAction) {
                probabilities[i] = 1.0;
            } else {
                probabilities[i] = 0.0;
            }
        }
    }

    public Cell clone() {
        Cell cell = new Cell();
        cell.setOldValue(this.oldValue);
        cell.setNewValue(this.newValue);
        cell.setBarrier(this.barrier);
        cell.setProbabilities(this.probabilities.clone());
        return cell;
    }

    public int getPolicy() {
        int policy = -1;
        double max = -Double.MAX_VALUE;
        for (int i = 0; i < 4; i++) {
            if (probabilities[i] > max) {
                max = probabilities[i];
                policy = i;
            }
        }
        return policy;
    }
}
