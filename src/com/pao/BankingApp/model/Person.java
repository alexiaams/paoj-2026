package com.pao.BankingApp.model;

import java.util.Objects;

public abstract class Person {
    private static long nextId = 1;

    private final long id;
    private String firstName;
    private String lastName;
    private final String CNP;
    private String phoneNumber;

    protected Person(String firstName, String lastName, String CNP, String phoneNumber) {
        this.id = generateId();
        setFirstName(firstName);
        setLastName(lastName);
        validateCNP(CNP);
        this.CNP = CNP;
        setPhoneNumber(phoneNumber);
    }

    private static synchronized long generateId() {
        return nextId++;
    }
    

    public abstract String getPersonType();
    
      private void validateCNP(String CNP) {
        if (CNP == null || !CNP.matches("\\d{13}")) {
            throw new IllegalArgumentException("CNP must contain exactly 13 digits");
        }
    
    }

    // Getters
    public long getId() {
        return id;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getCNP() {
        return CNP;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getFullName() {
        return firstName + " " + lastName;
    }
    //setters
    public void setFirstName(String firstName) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        this.lastName = lastName;
    }


    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", CNP='" + CNP + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", personType='" + getPersonType() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Person person)) {
            return false;
        }
        return Objects.equals(CNP, person.CNP);
    }

    @Override
    public int hashCode() {
        return Objects.hash(CNP);
    }

}
