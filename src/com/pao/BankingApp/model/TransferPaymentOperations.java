package com.pao.BankingApp.model;

public interface TransferPaymentOperations {
    void deposit(double amount);
    void withdraw(double amount);
    void transferTo(BankAccount destination, double amount);
    double getBalance();
}
