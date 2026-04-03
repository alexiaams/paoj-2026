package com.pao.laboratory06.exercise3;

public abstract class Persoana {
    private final String nume;
    private final String prenume;
    private final String telefon;

    protected Persoana(String nume, String prenume, String telefon) {
        if (isBlank(nume) || isBlank(prenume)) {
            throw new IllegalArgumentException("Numele si prenumele sunt obligatorii.");
        }
        this.nume = nume;
        this.prenume = prenume;
        this.telefon = telefon;
    }

    public String getNume() {
        return nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public String getTelefon() {
        return telefon;
    }

    protected static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
