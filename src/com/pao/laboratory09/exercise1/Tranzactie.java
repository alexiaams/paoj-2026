package com.pao.laboratory09.exercise1;

import java.io.Serializable;

public class Tranzactie implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private double suma;
    private String data;
    private String contSursa;
    private String contDestinatie;
    private TipTranzactie tip;
    private transient String note;

    public Tranzactie() {
    }

    public Tranzactie(int id, double suma, String data, String contSursa, String contDestinatie, TipTranzactie tip) {
        this.id = id;
        this.suma = suma;
        this.data = data;
        this.contSursa = contSursa;
        this.contDestinatie = contDestinatie;
        this.tip = tip;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getSuma() {
        return suma;
    }

    public void setSuma(double suma) {
        this.suma = suma;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getContSursa() {
        return contSursa;
    }

    public void setContSursa(String contSursa) {
        this.contSursa = contSursa;
    }

    public String getContDestinatie() {
        return contDestinatie;
    }

    public void setContDestinatie(String contDestinatie) {
        this.contDestinatie = contDestinatie;
    }

    public TipTranzactie getTip() {
        return tip;
    }

    public void setTip(TipTranzactie tip) {
        this.tip = tip;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s %s: %.2f RON | %s -> %s", 
            id, data, tip, suma, contSursa, contDestinatie);
    }
}
