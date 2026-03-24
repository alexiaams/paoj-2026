package com.pao.laboratory05.audit;

import java.time.LocalDateTime;

public class AngajatService {
    private Angajat[] angajati;
    private AuditEntry[] auditLog;

    private AngajatService() {
        this.angajati = new Angajat[0];
        this.auditLog = new AuditEntry[0];
    }

    private static class AngajatServiceHolder {
        private static final AngajatService INSTANCE = new AngajatService();
    }

    public static AngajatService getInstance() {
        return AngajatServiceHolder.INSTANCE;
    }

    private void logAction(String action, String target) {
        String timestamp = LocalDateTime.now().toString();
        AuditEntry entry = new AuditEntry(action, target, timestamp);
        
        AuditEntry[] auditResize = new AuditEntry[auditLog.length + 1];
        System.arraycopy(auditLog, 0, auditResize, 0, auditLog.length);
        auditResize[auditLog.length] = entry;
        auditLog = auditResize;
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
        logAction("ADD", angajat.getNume());
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
        logAction("FIND_BY_DEPT", numeDep);
        
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

    public void printAuditLog() {
        System.out.println("--- Audit Log ---");
        for (AuditEntry entry : auditLog) {
            System.out.println(entry);
        }
    }
}
