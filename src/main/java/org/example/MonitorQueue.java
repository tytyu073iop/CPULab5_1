package org.example;

import java.util.LinkedList;

public class MonitorQueue<T> {
    LinkedList<T> queue = new LinkedList<>();
    int size;
    int empty;

    public MonitorQueue(int size) {
        this.size = size;
        empty = size;
    }

    public synchronized void put(T item) {
        while (empty == 0) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }

        queue.add(item);
        empty--;
        if (empty == size - 1) {
            notifyAll();
        }
    }

    public synchronized T take() {
        while (empty == size) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }

        T item = queue.poll();
        empty++;
        if (empty == 1) {
            notifyAll();
        }
        return item;
    }
}
