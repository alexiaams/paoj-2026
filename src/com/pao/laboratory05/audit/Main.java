package com.pao.laboratory05.audit;

import java.util.Scanner;

/**
 * Exercise 4 (Bonus) — Audit Log
 *
 * Cerințele complete se află în:
 *   src/com/pao/laboratory05/Readme.md  →  secțiunea "Exercise 4 (Bonus) — Audit"
 *
 * Extinde soluția de la Exercise 3 cu un sistem de audit bazat pe record.
 * Creează fișierele de la zero în acest pachet, apoi rulează Main.java
 * pentru a verifica output-ul așteptat din Readme.
 */
public class Main {
    public static void main(String[] args) {
        AngajatService service = AngajatService.getInstance();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Gestionare Angajați (cu Audit) =====");
            System.out.println("1. Adaugă angajat");
            System.out.println("2. Listare după salariu");
            System.out.println("3. Caută după departament");
            System.out.println("4. Afișează audit log");
            System.out.println("0. Ieșire");
            System.out.print("Opțiune: ");

            String input = scanner.nextLine().trim();
            int optiune;
            try {
                optiune = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Opțiune invalidă. Introdu un număr.");
                continue;
            }

            switch (optiune) {
                case 1:
                    System.out.print("Nume: ");
                    String nume = scanner.nextLine().trim();

                    System.out.print("Departament (nume): ");
                    String numeDepartament = scanner.nextLine().trim();

                    System.out.print("Departament (locatie): ");
                    String locatieDepartament = scanner.nextLine().trim();

                    System.out.print("Salariu: ");
                    String salariuInput = scanner.nextLine().trim();
                    double salariu;
                    try {
                        salariu = Double.parseDouble(salariuInput);
                    } catch (NumberFormatException e) {
                        System.out.println("Salariu invalid. Introdu o valoare numerică.");
                        break;
                    }

                    Departament departament = new Departament(numeDepartament, locatieDepartament);
                    Angajat angajat = new Angajat(nume, departament, salariu);
                    service.addAngajat(angajat);
                    break;
                case 2:
                    service.listBySalary();
                    break;
                case 3:
                    System.out.print("Departament: ");
                    String dept = scanner.nextLine().trim();
                    service.findByDepartament(dept);
                    break;
                case 4:
                    service.printAuditLog();
                    break;
                case 0:
                    System.out.println("La revedere!");
                    return;
                default:
                    System.out.println("Opțiune inexistentă.");
                    break;
            }
        }
    }
}
