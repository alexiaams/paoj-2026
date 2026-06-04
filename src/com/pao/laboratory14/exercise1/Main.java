package com.pao.laboratory14.exercise1;

import java.util.*;
import java.util.stream.Collector;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = Integer.parseInt(sc.nextLine().trim());
        List<Bilet> bilete = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) { i--; continue; }
            String[] parts = line.split("\\s+");
            int id = Integer.parseInt(parts[0]);
            String eveniment = parts[1];
            TipBilet tip = TipBilet.valueOf(parts[2]);
            double pret = Double.parseDouble(parts[3]);
            bilete.add(new Bilet(id, eveniment, tip, pret));
        }

        String command = sc.nextLine().trim();

        Collector<Bilet, Map<TipBilet, double[]>, RaportVanzari> collector = Collector.of(
                () -> new EnumMap<>(TipBilet.class),
                (map, b) -> {
                    double[] arr = map.computeIfAbsent(b.getTip(), t -> new double[2]);
                    arr[0] += 1; // count
                    arr[1] += b.getPret(); // sum
                },
                (m1, m2) -> {
                    for (Map.Entry<TipBilet, double[]> e : m2.entrySet()) {
                        double[] a = m1.computeIfAbsent(e.getKey(), t -> new double[2]);
                        a[0] += e.getValue()[0];
                        a[1] += e.getValue()[1];
                    }
                    return m1;
                },
                map -> {
                    Map<TipBilet, Long> counts = new LinkedHashMap<>();
                    Map<TipBilet, Double> sums = new LinkedHashMap<>();
                    double total = 0.0;
                    long totalCount = 0;
                    for (TipBilet t : TipBilet.values()) {
                        double[] a = map.get(t);
                        if (a != null) {
                            long c = (long) a[0];
                            double s = a[1];
                            counts.put(t, c);
                            sums.put(t, s);
                            total += s;
                            totalCount += c;
                        }
                    }
                    double medie = totalCount == 0 ? 0.0 : total / totalCount;
                    TipBilet mostPopular = null;
                    long max = Long.MIN_VALUE;
                    for (TipBilet t : TipBilet.values()) {
                        Long c = counts.get(t);
                        if (c != null) {
                            if (c > max) {
                                max = c;
                                mostPopular = t;
                            }
                        }
                    }
                    return new RaportVanzari(Collections.unmodifiableMap(counts), Collections.unmodifiableMap(sums), total, medie, mostPopular);
                }
        );

        RaportVanzari raport = bilete.stream().collect(collector);

        // Print report lines in alphabetical enum order for present types
        for (TipBilet t : TipBilet.values()) {
            Long c = raport.getNumarPerTip().get(t);
            if (c != null) {
                double s = raport.getIncasariPerTip().get(t);
                System.out.println(String.format(Locale.US, "%s: count=%d incasari=%.2f RON", t.name(), c, s));
            }
        }

        if ("RAPORT_COMPLET".equals(command)) {
            System.out.println("---");
            System.out.println(String.format(Locale.US, "Total: %.2f RON", raport.getTotalGlobal()));
            System.out.println(String.format(Locale.US, "Medie: %.2f RON", raport.getMedieGlobala()));
            TipBilet top = raport.getTipCelMaiPopular();
            System.out.println("Cel mai popular: " + (top == null ? "" : top.name()));
        }
    }
}

