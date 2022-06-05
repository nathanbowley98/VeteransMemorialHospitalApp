package com.example.myapplication;

public class PaymentRecord {
    String employer;
    String employee;
    double amount;
    String date;

    public PaymentRecord(String employer, String employee, double amount, String date) {
        this.employer = employer;
        this.employee = employee;
        this.amount = amount;
        this.date = date;
    }

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
