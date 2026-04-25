package com.pao.laboratory08.exercise2;

import com.pao.laboratory08.exercise1.Adresa;
import com.pao.laboratory08.exercise1.Student;

import java.io.*;
import java.util.*;

public class Main {
    private static final String FILE_PATH = "paoj-2026/src/com/pao/laboratory08/tests/studenti.txt";
    private static final String OUTPUT_FILE = "rezultate.txt";
    private static final String OUTPUT_FILE_PATH = "paoj-2026/src/com/pao/laboratory08/exercise2/" + OUTPUT_FILE;

    public static void main(String[] args) throws Exception {
        List<Student> studenti = readStudentsFromFile();

        Scanner scanner = new Scanner(System.in);
        if (!scanner.hasNextInt()) {
            return;
        }

        int prag = scanner.nextInt();
        List<Student> filtrati = filterByMinAge(studenti, prag);

        writeStudentsToFile(filtrati, OUTPUT_FILE_PATH);
        printSummary(prag, filtrati);
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

    private static List<Student> filterByMinAge(List<Student> studenti, int prag) {
        List<Student> filtrati = new ArrayList<>();
        for (Student student : studenti) {
            if (student.getVarsta() >= prag) {
                filtrati.add(student);
            }
        }
        return filtrati;
    }

    private static void writeStudentsToFile(List<Student> studenti, String outputPath) throws IOException {
        try (BufferedWriter fout = new BufferedWriter(new FileWriter(outputPath))) {
            for (Student student : studenti) {
                fout.write(student.toString());
                fout.newLine();
            }
        }
    }

    private static void printSummary(int prag, List<Student> filtrati) {
        System.out.println("Filtru: varsta >= " + prag);
        System.out.println("Rezultate: " + filtrati.size() + " studenti");
        System.out.println();

        for (Student student : filtrati) {
            System.out.println(student);
        }

        System.out.println();
        System.out.println("Scris in: " + OUTPUT_FILE);
    }
}

