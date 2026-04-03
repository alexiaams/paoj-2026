package com.pao.laboratory06.exercise2;

public class PFAColaborator extends PersoanaFizica implements IOperatiiCitireScriere {
    private static final double SALARIU_MINIM_BRUT_ANUAL_2026 = 4050.0 * 12.0;

    private final String firstName;
    private final String lastName;
    private double cheltuieliLunare;

    public PFAColaborator(String firstName, String lastName, double salary) {
        this(firstName, lastName, salary, 0.0);
    }

    public PFAColaborator(String firstName, String lastName, double salary, double cheltuieliLunare) {
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
        System.out.printf("PFA: %s %s, venit net anual: %.2f lei%n", firstName, lastName, calculeazaVenitNetAnual());
    }

    @Override
    public String tipContract() {
        return "PFA";
    }

    @Override
    public double calculeazaVenitNetAnual() {
        double bazaAnuala = (getSalary() - cheltuieliLunare) * 12.0;

        if (bazaAnuala < 24000.0) {
            return 9600.0;
        }
        if (bazaAnuala < 48000.0) {
            return 19200.0;
        }

        double net = bazaAnuala * 0.80;
        return Math.min(net, 86400.0);
    }
}