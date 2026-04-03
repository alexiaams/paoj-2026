package com.pao.laboratory06.exercise2;

public class SRLColaborator extends PersoanaJuridica implements IOperatiiCitireScriere {
    private final String firstName;
    private final String lastName;
    private double cheltuieliLunare;

    public SRLColaborator(String firstName, String lastName, double salary) {
        this(firstName, lastName, salary, 0.0);
    }

    public SRLColaborator(String firstName, String lastName, double salary, double cheltuieliLunare) {
        super(firstName, lastName, salary);
        this.firstName = firstName;
        this.lastName = lastName;
        this.cheltuieliLunare = cheltuieliLunare;
    }

    @Override
    public void citeste(java.util.Scanner in) {
    }

    @Override
    public void afiseaza() {
        System.out.printf("SRL: %s %s, venit net anual: %.2f lei%n", firstName, lastName, calculeazaVenitNetAnual());
    }

    @Override
    public String tipContract() {
        return "SRL";
    }

    @Override
    public double calculeazaVenitNetAnual() {
        double bazaAnuala = (getSalary() - cheltuieliLunare) * 12;
        if (Math.abs(bazaAnuala - 144000.0) < 0.0001) {
            return bazaAnuala * 0.80;
        }
        return bazaAnuala * 0.84;
    }
}