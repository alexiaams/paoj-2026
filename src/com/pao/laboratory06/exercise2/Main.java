package com.pao.laboratory06.exercise2;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        if (!in.hasNextInt()) {
            return;
        }

        int n = in.nextInt();
        List<Colaborator> colaboratori = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            String tipRaw = in.next();
            TipColaborator tip = TipColaborator.valueOf(tipRaw);

            String firstName = in.next();
            String lastName = in.next();
            double venitLunar = in.nextDouble();

            switch (tip) {
                case CIM:
                    boolean bonus = false;
                    if (in.hasNext("DA|NU")) {
                        bonus = "DA".equalsIgnoreCase(in.next());
                    }
                    colaboratori.add(new CIMColaborator(firstName, lastName, venitLunar, bonus));
                    break;
                case PFA:
                    double cheltuieliPfa = in.nextDouble();
                    colaboratori.add(new PFAColaborator(firstName, lastName, venitLunar, cheltuieliPfa));
                    break;
                case SRL:
                    double cheltuieliSrl = in.nextDouble();
                    colaboratori.add(new SRLColaborator(firstName, lastName, venitLunar, cheltuieliSrl));
                    break;
                default:
                    break;
            }
        }

        for (Colaborator colaborator : colaboratori) {
            System.out.println(formatColaborator(colaborator));
        }

        System.out.println();

        Colaborator maxColaborator = null;
        for (Colaborator colaborator : colaboratori) {
            if (maxColaborator == null || colaborator.calculeazaVenitNetAnual() > maxColaborator.calculeazaVenitNetAnual()) {
                maxColaborator = colaborator;
            }
        }

        if (maxColaborator != null) {
            System.out.println("Colaborator cu venit net maxim: " + formatColaborator(maxColaborator));
        }

        System.out.println();
        System.out.println("Colaboratori persoane juridice:");
        for (Colaborator colaborator : colaboratori) {
            if (colaborator instanceof PersoanaJuridica) {
                System.out.println(formatColaborator(colaborator));
            }
        }

        System.out.println();
        System.out.println("Sume și număr colaboratori pe tip:");

        Map<TipColaborator, Double> sume = new EnumMap<>(TipColaborator.class);
        Map<TipColaborator, Integer> numar = new EnumMap<>(TipColaborator.class);

        for (Colaborator colaborator : colaboratori) {
            TipColaborator tip = TipColaborator.valueOf(((IOperatiiCitireScriere) colaborator).tipContract());
            sume.put(tip, sume.getOrDefault(tip, 0.0) + colaborator.calculeazaVenitNetAnual());
            numar.put(tip, numar.getOrDefault(tip, 0) + 1);
        }

        TipColaborator[] ordineAfisare = {TipColaborator.CIM, TipColaborator.PFA, TipColaborator.SRL};
        for (TipColaborator tip : ordineAfisare) {
            if (numar.containsKey(tip)) {
                System.out.printf(Locale.US, "%s: suma = %.2f lei, număr = %d%n", tip.name(), sume.get(tip), numar.get(tip));
            }
        }
    }

    private static String formatColaborator(Colaborator colaborator) {
        String tip = ((IOperatiiCitireScriere) colaborator).tipContract();
        return String.format(
                Locale.US,
                "%s: %s %s, venit net anual: %.2f lei",
                tip,
                colaborator.getFirstName(),
                colaborator.getLastName(),
                colaborator.calculeazaVenitNetAnual()
        );
    }
}