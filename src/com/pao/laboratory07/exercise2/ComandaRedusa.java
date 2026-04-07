package com.pao.laboratory07.exercise2;
import com.pao.laboratory07.exercise1.OrderState;

public final class ComandaRedusa extends Comanda {
    private int discountProcent;

    public ComandaRedusa(String nume, int discountProcent) {
        this(nume, 0.0, discountProcent, null);
    }

    public ComandaRedusa(String nume, double pret, int discountProcent) {
        this(nume, pret, discountProcent, null);
    }

    public ComandaRedusa(String nume, double pret, int discountProcent, String client) {
        this.nume = nume;
        this.pret = pret;
        this.discountProcent = discountProcent;
        this.client = client;
    }

    @Override
    public double pretFinal() {
        return this.pret* (1 - discountProcent / 100.0);
    }

    @Override
    public String descriere() {
        return descriereCuClient(descriereFaraStare() + " [" + OrderState.PLACED + "]");
    }

    @Override
    public String descriereFaraStare() {
        return String.format("DISCOUNTED: %s, pret: %.2f lei (-%d%%)", nume, pretFinal(), discountProcent);
    }

    @Override
    public String getTip() {
        return "DISCOUNTED";
    }

    public int getDiscountProcent() {
        return discountProcent;
    }

}