package com.pao.laboratory05.angajati;

public class AngajatService {
    private Angajat[] angajati;

    private AngajatService() {
        this.angajati = new Angajat[0];
    }

    private static class AngajatServiceHolder {
        private static final AngajatService INSTANCE = new AngajatService();
    }

    public static AngajatService getInstance() {
        return AngajatServiceHolder.INSTANCE;
    }

    public void addAngajat(Angajat angajat) {
        if (angajat == null) {
            System.out.println("Nu poti adauga un angajat nul.");
            return;
        }

        Angajat[] angajatiResize = new Angajat[angajati.length + 1];
        System.arraycopy(angajati, 0, angajatiResize, 0, angajati.length);
        angajatiResize[angajati.length] = angajat;
        angajati = angajatiResize;

        System.out.println("Angajat adaugat: " + angajat);
    }

    public void printAll() {
        System.out.println("Lista angajati:");
        for (Angajat angajat : angajati) {
            System.out.println(angajat);
        }
    }

    public void listBySalary(){
        Angajat[] copy = angajati.clone();
        java.util.Arrays.sort(copy);

        System.out.println("Angajati sortati dupa salariu (descrescator):");
        for (Angajat angajat : copy) {
            System.out.println(angajat);
        }
    }

    public void findByDepartament(String numeDep) {
        boolean found = false;
        for (Angajat angajat : angajati) {
            if (angajat.getDepartament().nume().equalsIgnoreCase(numeDep)) {
                System.out.println(angajat);
                found = true;
            }
        }
        if (!found) {
            System.out.println("Niciun angajat in departamentul: " + numeDep);
        }
    }



}