package com.pao.laboratory14.exercise2;

import com.pao.laboratory14.exercise2.model.Eveniment;
import com.pao.laboratory14.exercise2.repository.EvenimentRepository;
import com.pao.laboratory14.exercise1.TipBilet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        EvenimentRepository repo = new EvenimentRepository();
        repo.initSchema();

        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split("\\s+");
            String cmd = parts[0];
            switch (cmd) {
                case "ADD": {
                    String nume = parts[1];
                    String data = parts[2];
                    int cap = Integer.parseInt(parts[3]);
                    TipBilet tip = TipBilet.valueOf(parts[4]);
                    Eveniment e = new Eveniment(nume, data, cap, tip);
                    repo.save(e);
                    System.out.println(String.format("Adaugat: [%d] %s", e.getId(), e.getNume()));
                    break;
                }
                case "LIST": {
                    List<Eveniment> all = repo.findAll();
                    for (Eveniment e : all) {
                        System.out.println(String.format("[%d] %s | %s | cap=%d | %s", e.getId(), e.getNume(), e.getData(), e.getCapacitate(), e.getTip().name()));
                    }
                    break;
                }
                case "DELETE": {
                    int id = Integer.parseInt(parts[1]);
                    int affected = repo.deleteImpl(id);
                    if (affected > 0) System.out.println("Sters: " + id);
                    else System.out.println("Nu exista: " + id);
                    break;
                }
                case "COUNT": {
                    long total = repo.count();
                    System.out.println("Total: " + total);
                    break;
                }
                default:
                    // ignore unknown
            }
        }
    }
}

