package com.pao.laboratory07.exercise2;

import com.pao.laboratory07.exercise1.OrderState;

public final class ComandaStandard extends Comanda {
    public ComandaStandard(String nume, double pret) {
        this(nume, pret, null);
    }

    public ComandaStandard(String nume, double pret, String client) {
        this.nume = nume;
        this.pret = pret;
        this.client = client;
    }

    @Override
    public double pretFinal() {
        return pret;
    }

    @Override
    public String descriere() {
        return descriereCuClient(descriereFaraStare() + " [" + OrderState.PLACED + "]");
    }

    @Override
    public String descriereFaraStare() {
        return String.format("STANDARD: %s, pret: %.2f lei", nume, pretFinal());
    }

    @Override
    public String getTip() {
        return "STANDARD";
    }
}

