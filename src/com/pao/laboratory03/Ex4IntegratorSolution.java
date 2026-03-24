package com.pao.laboratory03;

import java.util.*;

/**
 * Soluție Exercițiul 4 (Integrator) — Gestiune studenți + note
 * Rulează direct acest main. Toate clasele sunt definite ca inner types.
 */
public class Ex4IntegratorSolution {

    // ═══════════════════════════════════════════
    //  MODEL
    // ═══════════════════════════════════════════

    private enum Subject {
        PAOJ("Programare Avansată pe Obiecte", 6),
        BD("Baze de Date", 5),
        SO("Sisteme de Operare", 5),
        RC("Rețele de Calculatoare", 4);

        private final String fullName;
        private final int credits;

        Subject(String fullName, int credits) {
            this.fullName = fullName;
            this.credits = credits;
        }

        public String getFullName() { return fullName; }
        public int getCredits() { return credits; }

        @Override
        public String toString() {
            return name() + " (" + fullName + ", " + credits + " credite)";
        }
    }

    private static class Student {
        private final String name;
        private final int age;
        private final Map<Subject, Double> grades;

        public Student(String name, int age) {
            if (age < 18 || age > 60) {
                throw new InvalidStudentException("Vârsta " + age + " nu este validă (18-60)");
            }
            this.name = name;
            this.age = age;
            this.grades = new HashMap<>();
        }

        public String getName() { return name; }
        public int getAge() { return age; }
        public Map<Subject, Double> getGrades() { return grades; }

        public void addGrade(Subject subject, double grade) {
            if (grade < 1 || grade > 10) {
                throw new InvalidGradeException("Nota " + grade + " nu este validă (1-10)");
            }
            grades.put(subject, grade);
        }

        public double getAverage() {
            if (grades.isEmpty()) return 0;
            double sum = 0;
            for (double g : grades.values()) sum += g;
            return sum / grades.size();
        }

        @Override
        public String toString() {
            return String.format("Student{name='%s', age=%d, avg=%.2f}", name, age, getAverage());
        }
    }

    // ═══════════════════════════════════════════
    //  EXCEPȚII
    // ═══════════════════════════════════════════

    private static class InvalidStudentException extends RuntimeException {
        public InvalidStudentException(String message) { super(message); }
    }

    private static class InvalidGradeException extends RuntimeException {
        public InvalidGradeException(String message) { super(message); }
    }

    private static class StudentNotFoundException extends RuntimeException {
        public StudentNotFoundException(String message) { super(message); }
    }

    // ═══════════════════════════════════════════
    //  SERVICIU (Singleton)
    // ═══════════════════════════════════════════

    private static class StudentService {
        private static StudentService instance;
        private final List<Student> students = new ArrayList<>();

        private StudentService() {}

        public static StudentService getInstance() {
            if (instance == null) {
                instance = new StudentService();
            }
            return instance;
        }

        public void addStudent(String name, int age) {
            for (Student s : students) {
                if (s.getName().equalsIgnoreCase(name)) {
                    throw new RuntimeException("Studentul '" + name + "' există deja");
                }
            }
            students.add(new Student(name, age));
        }

        public Student findByName(String name) {
            for (Student s : students) {
                if (s.getName().equalsIgnoreCase(name)) {
                    return s;
                }
            }
            throw new StudentNotFoundException("Studentul '" + name + "' nu a fost găsit");
        }

        public void addGrade(String studentName, Subject subject, double grade) {
            Student student = findByName(studentName);
            student.addGrade(subject, grade);
        }

        public void printAllStudents() {
            if (students.isEmpty()) {
                System.out.println("Nu există studenți.");
                return;
            }
            int i = 1;
            for (Student s : students) {
                System.out.println(i + ". " + s);
                for (Map.Entry<Subject, Double> entry : s.getGrades().entrySet()) {
                    System.out.println("   " + entry.getKey().name() + " = " + entry.getValue());
                }
                i++;
            }
        }

        public void printTopStudents() {
            List<Student> sorted = new ArrayList<>(students);
            sorted.sort((a, b) -> Double.compare(b.getAverage(), a.getAverage()));
            System.out.println("=== Top studenți ===");
            int i = 1;
            for (Student s : sorted) {
                System.out.printf("%d. %s — media: %.2f%n", i, s.getName(), s.getAverage());
                i++;
            }
        }

        public Map<Subject, Double> getAveragePerSubject() {
            Map<Subject, Double> result = new HashMap<>();
            for (Subject subject : Subject.values()) {
                double sum = 0;
                int count = 0;
                for (Student s : students) {
                    Double grade = s.getGrades().get(subject);
                    if (grade != null) {
                        sum += grade;
                        count++;
                    }
                }
                if (count > 0) {
                    result.put(subject, sum / count);
                }
            }
            return result;
        }
    }

    // ═══════════════════════════════════════════
    //  MAIN
    // ═══════════════════════════════════════════

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StudentService service = StudentService.getInstance();

        System.out.println("=== Sistem Gestiune Studenți ===");

        boolean running = true;
        while (running) {
            System.out.println("\n--- Meniu ---");
            System.out.println("1. Adaugă student");
            System.out.println("2. Adaugă notă");
            System.out.println("3. Afișează toți studenții");
            System.out.println("4. Top studenți (după medie)");
            System.out.println("5. Media pe materie");
            System.out.println("0. Ieșire");
            System.out.print("Opțiune: ");

            String option = scanner.nextLine().trim();

            try {
                switch (option) {
                    case "1":
                        System.out.print("Nume: ");
                        String name = scanner.nextLine().trim();
                        System.out.print("Vârsta: ");
                        int age = Integer.parseInt(scanner.nextLine().trim());
                        service.addStudent(name, age);
                        System.out.println("Student adăugat cu succes!");
                        break;

                    case "2":
                        System.out.print("Nume student: ");
                        String studentName = scanner.nextLine().trim();
                        System.out.print("Materie (" + Arrays.toString(Subject.values()) + "): ");
                        String subjectStr = scanner.nextLine().trim().toUpperCase();
                        System.out.print("Nota (1-10): ");
                        double grade = Double.parseDouble(scanner.nextLine().trim());
                        Subject subject = Subject.valueOf(subjectStr);
                        service.addGrade(studentName, subject, grade);
                        System.out.println("Notă adăugată!");
                        break;

                    case "3":
                        service.printAllStudents();
                        break;

                    case "4":
                        service.printTopStudents();
                        break;

                    case "5":
                        System.out.println("=== Media pe materie ===");
                        Map<Subject, Double> avgs = service.getAveragePerSubject();
                        for (Map.Entry<Subject, Double> entry : avgs.entrySet()) {
                            System.out.printf("%s: %.2f%n", entry.getKey().name(), entry.getValue());
                        }
                        break;

                    case "0":
                        running = false;
                        System.out.println("La revedere!");
                        break;

                    default:
                        System.out.println("Opțiune invalidă.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Eroare: Introdu un număr valid.");
            } catch (IllegalArgumentException e) {
                System.out.println("Eroare: " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Eroare: " + e.getMessage());
            }
        }

        scanner.close();
    }
}

