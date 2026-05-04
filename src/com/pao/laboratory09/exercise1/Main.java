package com.pao.laboratory09.exercise1;

import java.io.*;
import java.util.*;

public class Main {
    private static final String OUTPUT_FILE = "output/lab09_ex1.ser";

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        List<Tranzactie> tranzactii = new ArrayList<>();

        // 1. Citește N și apoi N tranzacții
        int n = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        for (int i = 0; i < n; i++) {
            int id = scanner.nextInt();
            double suma = scanner.nextDouble();
            String data = scanner.next();
            String contSursa = scanner.next();
            String contDestinatie = scanner.next();
            TipTranzactie tip = TipTranzactie.valueOf(scanner.next());
            scanner.nextLine(); // Consume newline

            Tranzactie tranzactie = new Tranzactie(id, suma, data, contSursa, contDestinatie, tip);
            tranzactii.add(tranzactie);
        }

        // 2. Setează note = "procesat" pe fiecare tranzacție
        for (Tranzactie t : tranzactii) {
            t.setNote("procesat");
        }

        // 3. Serializează lista în OUTPUT_FILE
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(OUTPUT_FILE))) {
            oos.writeObject(tranzactii);
        }

        // 4. Deserializează lista din OUTPUT_FILE
        List<Tranzactie> deserializedTranzactii;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(OUTPUT_FILE))) {
            deserializedTranzactii = (List<Tranzactie>) ois.readObject();
        }

        // 5. Procesează comenzile
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            String command = parts[0];

            if (command.equals("LIST")) {
                for (Tranzactie t : deserializedTranzactii) {
                    System.out.println(t);
                }
            } else if (command.equals("FILTER")) {
                String prefix = parts[1];
                boolean found = false;
                for (Tranzactie t : deserializedTranzactii) {
                    if (t.getData().startsWith(prefix)) {
                        System.out.println(t);
                        found = true;
                    }
                }
                if (!found) {
                    System.out.println("Niciun rezultat.");
                }
            } else if (command.equals("NOTE")) {
                int id = Integer.parseInt(parts[1]);
                boolean found = false;
                for (Tranzactie t : deserializedTranzactii) {
                    if (t.getId() == id) {
                        System.out.println("NOTE[" + id + "]: " + t.getNote());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.out.println("NOTE[" + id + "]: not found");
                }
            }
        }

        scanner.close();
    }
}
