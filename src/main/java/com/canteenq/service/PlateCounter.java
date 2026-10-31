package com.canteenq.service;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class PlateCounter {

    private int platesLeft;
    private final List<String> servedTo = new ArrayList<>();
    private final TreeMap<Integer, Integer> line = new TreeMap<>();

    public PlateCounter(int plates) {
        this.platesLeft = plates;
    }

    public synchronized void standInLine(int priority) {
        line.merge(priority, 1, Integer::sum);
    }

    public synchronized boolean grab(String person, int priority) {
        try {
            while (line.lastKey() > priority) {
                wait();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            leaveLine(priority);
            return false;
        }
        leaveLine(priority);
        if (platesLeft == 0) {
            return false;
        }
        platesLeft--;
        servedTo.add(person);
        return true;
    }

    private void leaveLine(int priority) {
        line.computeIfPresent(priority, (p, count) -> count == 1 ? null : count - 1);
        notifyAll();
    }

    public synchronized List<String> servedTo() {
        return new ArrayList<>(servedTo);
    }

    public synchronized int platesLeft() {
        return platesLeft;
    }
}
