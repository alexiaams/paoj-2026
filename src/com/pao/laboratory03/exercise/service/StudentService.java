package com.pao.laboratory03.exercise.service;

import com.pao.laboratory03.exercise.exception.StudentNotFoundException;
import com.pao.laboratory03.exercise.model.Student;
import com.pao.laboratory03.exercise.model.Subject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentService {

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
                throw new RuntimeException("Există deja un student cu numele: " + name);
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
        throw new StudentNotFoundException("Studentul nu a fost găsit: " + name);
    }

    public void addGrade(String studentName, Subject subject, double grade) {
        findByName(studentName).addGrade(subject, grade);
    }

    public void printAllStudents() {
        if (students.isEmpty()) {
            System.out.println("Nu există studenți înregistrați.");
            return;
        }
        for (Student s : students) {
            System.out.println(s);
            for (Map.Entry<Subject, Double> entry : s.getGrades().entrySet()) {
                System.out.printf("  %-6s → %.2f%n", entry.getKey().name(), entry.getValue());
            }
        }
    }

    public void printTopStudents() {
        List<Student> sorted = new ArrayList<>(students);
        sorted.sort(Comparator.comparingDouble(Student::getAverage).reversed());
        for (Student s : sorted) {
            System.out.println(s);
        }
    }

    public Map<Subject, Double> getAveragePerSubject() {
        Map<Subject, Double> sumMap = new HashMap<>();
        Map<Subject, Integer> countMap = new HashMap<>();

        for (Student s : students) {
            for (Map.Entry<Subject, Double> entry : s.getGrades().entrySet()) {
                Subject subj = entry.getKey();
                sumMap.merge(subj, entry.getValue(), Double::sum);
                countMap.merge(subj, 1, Integer::sum);
            }
        }

        Map<Subject, Double> averages = new HashMap<>();
        for (Subject subj : sumMap.keySet()) {
            averages.put(subj, sumMap.get(subj) / countMap.get(subj));
        }
        return averages;
    }
}

