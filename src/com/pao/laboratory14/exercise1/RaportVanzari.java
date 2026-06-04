package com.pao.laboratory14.exercise1;

import java.util.Map;

public final class RaportVanzari {
    private final Map<TipBilet, Long> numarPerTip;
    private final Map<TipBilet, Double> incasariPerTip;
    private final double totalGlobal;
    private final double medieGlobala;
    private final TipBilet tipCelMaiPopular;

    public RaportVanzari(Map<TipBilet, Long> numarPerTip,
                         Map<TipBilet, Double> incasariPerTip,
                         double totalGlobal,
                         double medieGlobala,
                         TipBilet tipCelMaiPopular) {
        this.numarPerTip = numarPerTip;
        this.incasariPerTip = incasariPerTip;
        this.totalGlobal = totalGlobal;
        this.medieGlobala = medieGlobala;
        this.tipCelMaiPopular = tipCelMaiPopular;
    }

    public Map<TipBilet, Long> getNumarPerTip() {
        return numarPerTip;
    }

    public Map<TipBilet, Double> getIncasariPerTip() {
        return incasariPerTip;
    }

    public double getTotalGlobal() {
        return totalGlobal;
    }

    public double getMedieGlobala() {
        return medieGlobala;
    }

    public TipBilet getTipCelMaiPopular() {
        return tipCelMaiPopular;
    }
}
