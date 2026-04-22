package com.pao.laboratory08.exercise1;

public class Adresa implements Cloneable 
{
    private String oras;
    private String strada;

    public Adresa(String oras, String strada) {
        this.oras = oras;
        this.strada = strada;
    }
    public String getOras() {
        return oras;
    }
    public String getStrada() {
        return strada;
    }
    public void setOras(String oras) {
        this.oras = oras;
    }
    public void setStrada(String strada) {
        this.strada = strada;
    }
    public String toString() {
        return "Adresa{oras='" + oras + "', strada='" + strada + "'}";
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}