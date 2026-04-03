package com.pao.laboratory06.exercise3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PersoanaJuridica extends Persoana implements PlataOnlineSMS {
    private final List<String> smsTrimise = new ArrayList<>();
    private double sold;

    public PersoanaJuridica(String nume, String prenume, String telefon, double soldInitial) {
        super(nume, prenume, telefon);
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
    public boolean trimiteSMS(String mesaj) {
        if (isBlank(mesaj) || isBlank(getTelefon())) {
            return false;
        }
        smsTrimise.add(mesaj);
        return true;
    }

    public List<String> getSmsTrimise() {
        return Collections.unmodifiableList(smsTrimise);
    }
}
