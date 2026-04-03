package com.pao.laboratory06.exercise2;

import java.util.Scanner;

class CIMColaborator extends PersoanaFizica implements IOperatiiCitireScriere {
    private final String firstName;
    private final String lastName;
    private boolean bonus;

    public CIMColaborator(String firstName, String lastName, double salary) {
        this(firstName, lastName, salary, false);
    }

    public CIMColaborator(String firstName, String lastName, double salary, boolean bonus) {
        super(firstName, lastName, salary);
        this.firstName = firstName;
        this.lastName = lastName;
        this.bonus = bonus;
    }

    @Override
    public double calculeazaVenitNetAnual() {
        double venitNetAnual = getSalary() * 12 * 0.55;
        if (bonus) {
            venitNetAnual *= 1.10;
        }
        return venitNetAnual;
    }

    @Override
    public void citeste(Scanner in) {
    }

    @Override
    public void afiseaza() {
        System.out.printf("CIM: %s %s, venit net anual: %.2f lei%n", firstName, lastName, calculeazaVenitNetAnual());
    }

    @Override
    public String tipContract() {
        return "CIM";
    }

    @Override
    public boolean areBonus() {
        return bonus;
    }
}