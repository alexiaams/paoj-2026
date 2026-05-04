package com.pao.laboratory09.exercise3;

import com.pao.laboratory09.exercise1.Tranzactie;
import java.util.LinkedList;
import java.util.Queue;

public class CoadaTranzactii {
    private static final int CAPACITY = 5;
    private Queue<Tranzactie> coada = new LinkedList<>();
    public volatile boolean shutdown = false;

    public synchronized void adauga(Tranzactie t) {
        while (coada.size() >= CAPACITY) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        coada.add(t);
        notifyAll();
    }

    public synchronized Tranzactie extrage() {
        while (coada.isEmpty() && !shutdown) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (coada.isEmpty()) {
            return null;
        }
        Tranzactie t = coada.poll();
        notifyAll();
        return t;
    }

    public synchronized boolean esteGoala() {
        return coada.isEmpty();
    }

    public synchronized int dimensiune() {
        return coada.size();
    }
}
