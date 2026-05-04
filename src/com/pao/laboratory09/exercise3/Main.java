package com.pao.laboratory09.exercise3;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Procesator asincron de tranzacții bancare ===\n");

        // Creeaza banda partajata
        CoadaTranzactii coada = new CoadaTranzactii();

        // Creeaza 3 producatori (ATM-uri)
        ATMThread atm1 = new ATMThread(1, coada);
        ATMThread atm2 = new ATMThread(2, coada);
        ATMThread atm3 = new ATMThread(3, coada);

        // Creeaza consumatorul (Processor)
        ProcessorThread processor = new ProcessorThread(coada);
        Thread processorThread = new Thread(processor);

        // Porneste toti producatorii
        atm1.start();
        atm2.start();
        atm3.start();

        // Porneste consumatorul
        processorThread.start();

        // Asteapta terminarea tuturor producatorilor
        atm1.join();
        atm2.join();
        atm3.join();

        // Opreste consumatorul si il trezeste daca e in asteaptare
        processor.activ = false;
        coada.shutdown = true;
        synchronized (coada) {
            coada.notifyAll();
        }

        // Asteapta terminarea consumatorului
        processorThread.join();

        System.out.println("\nToate tranzactiile procesate. Total: 12");
    }
}
