package com.pao.BankingApp.model;

import java.time.LocalDate;

public class Employee extends Person {
    private final String employeeCode;
    private Department department;
    private String role;
    private String branch;
    private final LocalDate startDate;
    private double salary;
    private boolean isActive;

    public Employee(String firstName, String lastName, String CNP, String phoneNumber, 
                    Department department, String role, String branch, double salary) {
        super(firstName, lastName, CNP, phoneNumber);
        this.employeeCode = "EMP-" + getId();
        setDepartment(department);
        setRole(role);
        setBranch(branch);
        this.startDate = LocalDate.now();
        setSalary(salary);
        this.isActive = true;
    }

    @Override
    public String getPersonType() {
        return "Employee";
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        if (department == null) {
            throw new IllegalArgumentException("Department cannot be null");
        }
        this.department = department;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Role cannot be empty");
        }
        this.role = role;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        if (branch == null || branch.isBlank()) {
            throw new IllegalArgumentException("Branch cannot be empty");
        }
        this.branch = branch;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        if (salary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
        this.salary = salary;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + getId() +
                ", fullName='" + getFullName() + '\'' +
                ", employeeCode='" + employeeCode + '\'' +
                ", department=" + department +
                ", role='" + role + '\'' +
                ", branch='" + branch + '\'' +
                ", startDate=" + startDate +
                ", salary=" + salary +
                ", isActive=" + isActive +
                '}';
    }
}
