package com.pao.laboratory08.exercise1;

import java.io.*;
import java.util.*;

public class Main {
    // Calea către fișierul cu date — relativă la rădăcina workspace-ului PAOJ
    private static final String FILE_PATH = "paoj-2026/src/com/pao/laboratory08/tests/studenti.txt";

    public static void main(String[] args) throws Exception {
        List<Student> studenti = readStudentsFromFile();

        BufferedReader stdinReader = new BufferedReader(new InputStreamReader(System.in));
        String commandLine = stdinReader.readLine();

        if (commandLine == null) {
            return;
        }

        String command = commandLine.trim();
        if ("PRINT".equals(command)) {
            for (Student student : studenti) {
                System.out.println(student);
            }
            return;
        }

        if (command.startsWith("SHALLOW ")) {
            String nume = command.substring("SHALLOW ".length()).trim();
            Student original = findStudentByName(studenti, nume);
            if (original == null) {
                return;
            }

            Student clona = original.shallowClone();
            clona.getAdresa().setOras("MODIFICAT");

            System.out.println("Original: " + original);
            System.out.println("Clona: " + clona);
            return;
        }

        if (command.startsWith("DEEP ")) {
            String nume = command.substring("DEEP ".length()).trim();
            Student original = findStudentByName(studenti, nume);
            if (original == null) {
                return;
            }

            Student clona = original.deepClone();
            clona.getAdresa().setOras("MODIFICAT");

            System.out.println("Original: " + original);
            System.out.println("Clona: " + clona);
        }
    }

    private static Student findStudentByName(List<Student> studenti, String nume) {
        for (Student student : studenti) {
            if (student.getNume().equals(nume)) {
                return student;
            }
        }
        return null;
    }

    private static List<Student> readStudentsFromFile() throws IOException {
        List<Student> studenti = new ArrayList<>();

        try (BufferedReader fileReader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");
                String nume = parts[0].trim();
                int varsta = Integer.parseInt(parts[1].trim());
                String oras = parts[2].trim();
                String strada = parts[3].trim();

                studenti.add(new Student(nume, varsta, new Adresa(oras, strada)));
            }
        }

        return studenti;
    }
}
