package com.pao.laboratory06.exercise2;

abstract class Colaborator {
    private String firstName;
    private String lastName;
    private double salary;

    public Colaborator(String firstName, String lastName, double salary) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
    }

    public abstract double calculeazaVenitNetAnual();

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public double getSalary() {
        return salary;
    }

}