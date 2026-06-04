package com.pao.laboratory14.exercise2.model;

import com.pao.laboratory14.exercise1.TipBilet;

public class Eveniment {
    private int id;
    private String nume;
    private String data;
    private int capacitate;
    private TipBilet tip;

    public Eveniment() {
    }

    public Eveniment(String nume, String data, int capacitate, TipBilet tip) {
        this.nume = nume;
        this.data = data;
        this.capacitate = capacitate;
        this.tip = tip;
    }

    public Eveniment(int id, String nume, String data, int capacitate, TipBilet tip) {
        this.id = id;
        this.nume = nume;
        this.data = data;
        this.capacitate = capacitate;
        this.tip = tip;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public String getData() {
        return data;
    }

    public int getCapacitate() {
        return capacitate;
    }

    public TipBilet getTip() {
        return tip;
    }
}


