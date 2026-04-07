package com.pao.laboratory07.exercise3;

import com.pao.laboratory07.exercise2.Comanda;
import com.pao.laboratory07.exercise2.ComandaGratuita;
import com.pao.laboratory07.exercise2.ComandaRedusa;
import com.pao.laboratory07.exercise2.ComandaStandard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = createScanner();
        if (!scanner.hasNextLine()) {
            return;
        }

        int numberOfCommands = Integer.parseInt(scanner.nextLine().trim());
        List<Comanda> comenzi = new ArrayList<>();

        for (int i = 0; i < numberOfCommands; i++) {
            String[] tokens = scanner.nextLine().trim().split("\\s+");
            String type = tokens[0];

            switch (type) {
                case "STANDARD" -> {
                    String nume = tokens[1];
                    double pret = Double.parseDouble(tokens[2]);
                    String client = tokens[3];
                    comenzi.add(new ComandaStandard(nume, pret, client));
                }
                case "DISCOUNTED" -> {
                    String nume = tokens[1];
                    double pret = Double.parseDouble(tokens[2]);
                    int discount = Integer.parseInt(tokens[3]);
                    String client = tokens[4];
                    comenzi.add(new ComandaRedusa(nume, pret, discount, client));
                }
                case "GIFT" -> {
                    String nume = tokens[1];
                    String client = tokens[2];
                    comenzi.add(new ComandaGratuita(nume, client));
                }
                default -> throw new InvalidExercise3InputException("Tip de comanda invalid: " + type);
            }
        }

        for (Comanda comanda : comenzi) {
            System.out.println(comanda.descriere());
        }

        System.out.println();

        while (scanner.hasNextLine()) {
            String commandLine = scanner.nextLine().trim();
            if (commandLine.isEmpty()) {
                continue;
            }

            String[] tokens = commandLine.split("\\s+");
            String command = tokens[0];

            switch (command) {
                case "STATS" -> printStats(comenzi);
                case "FILTER" -> printFilter(comenzi, Double.parseDouble(tokens[1]));
                case "SORT" -> printSort(comenzi);
                case "SPECIAL" -> printSpecial(comenzi);
                case "QUIT" -> {
                    return;
                }
                default -> throw new InvalidExercise3InputException("Comanda necunoscuta: " + commandLine);
            }
        }
    }

    private static void printStats(List<Comanda> comenzi) {
        System.out.println("--- STATS ---");

        Map<String, Double> averages = comenzi.stream()
                .collect(Collectors.groupingBy(
                        Comanda::getTip,
                        LinkedHashMap::new,
                        Collectors.averagingDouble(Comanda::pretFinal)
                ));

        for (String type : List.of("STANDARD", "DISCOUNTED", "GIFT")) {
            if (averages.containsKey(type)) {
                System.out.printf("%s: medie = %.2f lei%n", type, averages.get(type));
            }
        }

        System.out.println();
    }

    private static void printFilter(List<Comanda> comenzi, double threshold) {
        System.out.printf("--- FILTER (>= %.2f) ---%n", threshold);

        List<Comanda> filtered = comenzi.stream()
                .filter(comanda -> comanda.getPretFinal() >= threshold)
            .toList();

        filtered.forEach(comanda -> System.out.println(formatWithoutState(comanda)));

        System.out.println();
    }

    private static void printSort(List<Comanda> comenzi) {
        System.out.println("--- SORT (by client, then by pret) ---");

        comenzi.stream()
                .sorted(Comparator.comparing(Comanda::getClient)
                        .thenComparingDouble(Comanda::getPretFinal))
                .forEach(comanda -> System.out.println(formatWithoutState(comanda)));

        System.out.println();
    }

    private static void printSpecial(List<Comanda> comenzi) {
        System.out.println("--- SPECIAL (discount > 15%) ---");

        comenzi.stream()
                .filter(comanda -> comanda instanceof ComandaRedusa redusa && redusa.getDiscountProcent() > 15)
                .forEach(comanda -> System.out.println(formatWithoutState(comanda)));

        System.out.println();
    }

    private static String formatWithoutState(Comanda comanda) {
        return comanda.descriereFaraStare() + " - client: " + comanda.getClient();
    }

    private static Scanner createScanner() {
        try {
            Path defaultInputPath = Paths.get("paoj-2026", "src", "com", "pao", "laboratory07", "exercise3", "sample.in");
            return new Scanner(defaultInputPath);
        } catch (Exception e) {
            throw new IllegalStateException("Nu pot deschide sample.in", e);
        }
    }
}
