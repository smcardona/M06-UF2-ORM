package com.accesadades.jdbc;

import java.time.LocalDate;

public class Employees {

    private int employeeId; // EMPLOYEE_ID
    private String firstName; // FIRST_NAME
    private String lastName; // LAST_NAME
    private String email; // EMAIL
    private String phoneInt; // PHONE_INT
    private LocalDate hireDate; // HIRE_DATE
    private String jobId; // JOB_ID
    private Float salary; // SALARY
    private Float commissionPct; // COMMISSION_PCT
    private Integer managerId; // MANAGER_ID
    private Integer departmentId; // DEPARTMENT_ID
    private String bonus; // BONUS

    // Constructor completo
    public Employees(int employeeId, String firstName, String lastName, String email, String phoneInt,
                  LocalDate hireDate, String jobId, Float salary, Float commissionPct,
                  Integer managerId, Integer departmentId, String bonus) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneInt = phoneInt;
        this.hireDate = hireDate;
        this.jobId = jobId;
        this.salary = salary;
        this.commissionPct = commissionPct;
        this.managerId = managerId;
        this.departmentId = departmentId;
        this.bonus = bonus;
    }

    // Getters y Setters
    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneInt() {
        return phoneInt;
    }

    public void setPhoneInt(String phoneInt) {
        this.phoneInt = phoneInt;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Float getSalary() {
        return salary;
    }

    public void setSalary(Float salary) {
        this.salary = salary;
    }

    public Float getCommissionPct() {
        return commissionPct;
    }

    public void setCommissionPct(Float commissionPct) {
        this.commissionPct = commissionPct;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getBonus() {
        return bonus;
    }

    public void setBonus(String bonus) {
        this.bonus = bonus;
    }

    // toString para depuración y visualización
    @Override
    public String toString() {
        return "Person{" +
                "employeeId=" + employeeId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneInt='" + phoneInt + '\'' +
                ", hireDate=" + hireDate +
                ", jobId='" + jobId + '\'' +
                ", salary=" + salary +
                ", commissionPct=" + commissionPct +
                ", managerId=" + managerId +
                ", departmentId=" + departmentId +
                ", bonus='" + bonus + '\'' +
                '}';
    }
}