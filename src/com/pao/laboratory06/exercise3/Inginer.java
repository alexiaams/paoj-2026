package com.pao.laboratory06.exercise3;

public class Inginer extends Angajat implements PlataOnline, Comparable<Inginer> {
    private double sold;
    private boolean autentificat;

    public Inginer(String nume, String prenume, String telefon, double salariu, double soldInitial) {
        super(nume, prenume, telefon, salariu);
        if (soldInitial < 0) {
            throw new IllegalArgumentException("Soldul initial nu poate fi negativ.");
        }
        this.sold = soldInitial;
    }

    @Override
    public void autentificare(String user, String parola) {
        if (isBlank(user) || isBlank(parola)) {
            throw new IllegalArgumentException("User si parola sunt obligatorii.");
        }
        autentificat = true;
    }

    @Override
    public double consultareSold() {
        return sold;
    }

    @Override
    public boolean efectuarePlata(double suma) {
        if (suma <= 0 || suma > sold) {
            return false;
        }
        sold -= suma;
        return true;
    }

    @Override
    public int compareTo(Inginer other) {
        int byNume = getNume().compareToIgnoreCase(other.getNume());
        if (byNume != 0) {
            return byNume;
        }
        return getPrenume().compareToIgnoreCase(other.getPrenume());
    }

    public boolean isAutentificat() {
        return autentificat;
    }

    @Override
    public String toString() {
        return "Inginer{" +
                "nume='" + getNume() + '\'' +
                ", prenume='" + getPrenume() + '\'' +
                ", salariu=" + getSalariu() +
                ", sold=" + sold +
                '}';
    }
}
