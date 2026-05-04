package com.pao.laboratory09.exercise3;

import com.pao.laboratory09.exercise1.Tranzactie;
import com.pao.laboratory09.exercise1.TipTranzactie;

public class ATMThread extends Thread {
    private int atmId;
    private CoadaTranzactii coada;
    private static int transactionCounter = 1;
    
    private static synchronized int getNextId() {
        return transactionCounter++;
    }

    public ATMThread(int atmId, CoadaTranzactii coada) {
        this.atmId = atmId;
        this.coada = coada;
    }

    @Override
    public void run() {
        for (int i = 0; i < 4; i++) {
            int id = getNextId();
            double suma = 100 + Math.random() * 900;
            String data = "2024-01-15";
            TipTranzactie tip = Math.random() < 0.5 ? TipTranzactie.CREDIT : TipTranzactie.DEBIT;

            Tranzactie t = new Tranzactie(id, suma, data, "RO0" + atmId, "RO99", tip);

            System.out.println("[ATM-" + atmId + "] trimite: Tranzactie #" + id + " " + String.format("%.2f", suma) + " RON");

            coada.adauga(t);

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
