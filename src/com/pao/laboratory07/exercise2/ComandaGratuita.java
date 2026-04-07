package com.pao.laboratory07.exercise2;

import com.pao.laboratory07.exercise1.OrderState;

public final class ComandaGratuita extends Comanda {
    public ComandaGratuita(String nume) {
        this(nume, null);
    }

    public ComandaGratuita(String nume, String client) {
        this.nume = nume;
        this.client = client;
    }

    @Override
    public double pretFinal() {
        return 0.0;
    }

    @Override
    public String descriere() {
        return descriereCuClient(descriereFaraStare() + " [" + OrderState.PLACED + "]");
    }

    @Override
    public String descriereFaraStare() {
        return String.format("GIFT: %s, gratuit", nume);
    }

    @Override
    public String getTip() {
        return "GIFT";
    }
}