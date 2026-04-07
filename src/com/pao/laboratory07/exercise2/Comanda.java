package com.pao.laboratory07.exercise2;

public abstract sealed class Comanda permits ComandaStandard, ComandaRedusa, ComandaGratuita {
    protected String nume;
    protected double pret;
    protected String client;
    

    public abstract double pretFinal();
    public abstract String descriere();
    public abstract String descriereFaraStare();
    public abstract String getTip();

    public String getNume() {
        return nume;
    }

    public double getPretFinal() {
        return pretFinal();
    }

    public String getClient() {
        return client;
    }

    protected String descriereCuClient(String baza) {
        if (client == null) {
            return baza;
        }

        return baza + " - client: " + client;
    }
}