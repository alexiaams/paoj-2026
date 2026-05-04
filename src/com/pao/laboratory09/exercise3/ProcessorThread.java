package com.pao.laboratory09.exercise3;

import com.pao.laboratory09.exercise1.Tranzactie;

public class ProcessorThread implements Runnable {
    private CoadaTranzactii coada;
    public volatile boolean activ = true;

    public ProcessorThread(CoadaTranzactii coada) {
        this.coada = coada;
    }

    @Override
    public void run() {
            while (true) {
            Tranzactie t = coada.extrage();
                if (t == null) break;  // Queue is empty and shutdown flag is set
            
                System.out.println("[Processor] Factura #" + t.getId() + " - " + 
                    String.format("%.2f", t.getSuma()) + " RON | " + t.getData());

                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
        }
    }
}
