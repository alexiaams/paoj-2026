package com.pao.laboratory03;

import java.util.ArrayList;
import java.util.List;

/**
 * Soluție Exercițiul 3 — Excepții custom, try-catch-finally, multi-catch
 * Rulează direct acest main. Excepțiile sunt definite ca inner classes.
 */
public class Ex3ExceptionsSolution {

    // --- Excepții custom (în proiect ar fi fișiere separate) ---
    private static class InvalidAgeException extends RuntimeException {
        public InvalidAgeException(String message) { super(message); }
    }

    private static class DuplicateEntryException extends RuntimeException {
        public DuplicateEntryException(String message) { super(message); }
    }

    // --- Metode helper ---
    private static void validateAge(int age) {
        if (age < 0 || age > 150) {
            throw new InvalidAgeException("Vârsta " + age + " nu este validă (0-150)");
        }
    }

    private static void addToList(List<String> list, String name) {
        if (list.contains(name)) {
            throw new DuplicateEntryException("'" + name + "' există deja în listă");
        }
        list.add(name);
    }

    private static void process(int age) throws InvalidAgeException {
        validateAge(age);
        System.out.println("Procesare OK pentru vârsta: " + age);
    }

    public static void main(String[] args) {
        // a) Unchecked — NullPointerException + finally
        System.out.println("=== a) Unchecked — NullPointerException ===");
        try {
            String s = null;
            s.length();
        } catch (NullPointerException e) {
            System.out.println("Prins: " + e.getMessage());
        } finally {
            System.out.println("Finally se execută mereu!");
        }

        // b) Custom exceptions
        System.out.println("\n=== b) Custom exceptions ===");
        try {
            validateAge(-5);
        } catch (InvalidAgeException e) {
            System.out.println("InvalidAgeException: " + e.getMessage());
        }

        try {
            List<String> names = new ArrayList<>();
            names.add("Ana");
            addToList(names, "Ana");
        } catch (DuplicateEntryException e) {
            System.out.println("DuplicateEntryException: " + e.getMessage());
        }

        // c) Multi-catch
        System.out.println("\n=== c) Multi-catch ===");
        try {
            validateAge(200);
        } catch (InvalidAgeException | DuplicateEntryException e) {
            System.out.println("Excepție prinsă: " + e.getMessage());
        }

        // d) Catch ordering (specific → general)
        System.out.println("\n=== d) Catch ordering (specific → general) ===");
        try {
            validateAge(-1);
        } catch (InvalidAgeException e) {
            System.out.println("InvalidAgeException prinsă specific: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("RuntimeException generală: " + e.getMessage());
        }

        // e) Throw vs throws
        System.out.println("\n=== e) Throw vs throws ===");
        try {
            process(999);
        } catch (InvalidAgeException e) {
            System.out.println("Metoda process() a aruncat: " + e.getMessage());
        }
    }
}

