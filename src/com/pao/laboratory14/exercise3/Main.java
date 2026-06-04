package com.pao.laboratory14.exercise3;

/**
 * Bonus — Alocare Automata de Sali pentru Evenimente
 * <p>
 * Problema clasica de interviu: date N evenimente cu intervale [start, end],
 * gaseste numarul minim de sali necesare si atribuie fiecare eveniment la o sala.
 * <p>
 * Doua variante demonstrate:
 * Varianta 1 — greedy simplu O(N^2): prima sala disponibila
 * Varianta 2 — PriorityQueue O(N log N): min-heap de ore de final
 */
public class Main {

    record Eveniment(String nume, int startMin, int endMin) {
    }

    /**
     * Converteste "HH:MM" in minute intregi de la miezul noptii.
     */
    private static int toMin(String hhmm) {
        String[] p = hhmm.split(":");
        return Integer.parseInt(p[0]) * 60 + Integer.parseInt(p[1]);
    }

    /**
     * Converteste minute intregi inapoi in "HH:MM".
     */
    private static String toHHMM(int min) {
        return String.format("%02d:%02d", min / 60, min % 60);
    }

//    public static void main(String[] args) {}
    public static void main(String[] args) {
        java.util.List<Eveniment> events = java.util.Arrays.asList(
                new Eveniment("A_Concert", toMin("09:00"), toMin("10:30")),
                new Eveniment("B_Show", toMin("09:15"), toMin("11:00")),
                new Eveniment("C_Festival", toMin("10:00"), toMin("11:30")),
                new Eveniment("D_Play", toMin("11:00"), toMin("12:00")),
                new Eveniment("E_Recital", toMin("09:00"), toMin("09:30")),
                new Eveniment("F_Opera", toMin("12:00"), toMin("13:00")),
                new Eveniment("G_Dance", toMin("11:15"), toMin("12:30")),
                new Eveniment("H_Comedy", toMin("10:30"), toMin("11:00"))
        );

        events.sort(java.util.Comparator.comparingInt(Eveniment::startMin));

        System.out.println("Varianta 1 - Greedy O(N^2) (prima sala disponibila)");
        java.util.List<Integer> roomEnds = new java.util.ArrayList<>();
        java.util.Map<Eveniment, Integer> assignment = new java.util.HashMap<>();
        for (Eveniment ev : events) {
            int assigned = -1;
            for (int i = 0; i < roomEnds.size(); i++) {
                if (roomEnds.get(i) <= ev.startMin) {
                    assigned = i;
                    roomEnds.set(i, ev.endMin);
                    break;
                }
            }
            if (assigned == -1) {
                roomEnds.add(ev.endMin);
                assigned = roomEnds.size() - 1;
            }
                assignment.put(ev, assigned + 1); // room numbers start at 1
                System.out.println(String.format("%-15s (%s - %s)  ->  Sala #%d",
                    ev.nume, toHHMM(ev.startMin), toHHMM(ev.endMin), assigned + 1));
        }

        int roomsGreedy = roomEnds.size();
        System.out.println("Numar sali utilizate (greedy): " + roomsGreedy);

        // Varianta 2 — PriorityQueue O(N log N)
        System.out.println();
        System.out.println("Varianta 2 - PriorityQueue O(N log N) (confirmare numar minim)");
        java.util.PriorityQueue<Integer> pq = new java.util.PriorityQueue<>();
        for (Eveniment ev : events) {
            if (!pq.isEmpty() && pq.peek() <= ev.startMin) {
                pq.poll();
            }
            pq.offer(ev.endMin);
        }
        int roomsPQ = pq.size();
        System.out.println("Numar sali minim (PriorityQueue): " + roomsPQ);

        System.out.println();
        if (roomsGreedy == roomsPQ) {
            System.out.println("Rezultat: numarul minim confirmat: " + roomsPQ + " sali.");
        } else {
            System.out.println("Atentie: variantele au returnat valori diferite: greedy=" + roomsGreedy + " pq=" + roomsPQ);
        }
    }
}

