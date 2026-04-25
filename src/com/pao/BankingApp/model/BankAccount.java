package com.pao.BankingApp.model;

import com.pao.BankingApp.exception.InsufficientFundsException;

public abstract class BankAccount implements TransferPaymentOperations {
	private static long nextId = 1;

	private final long id;
	private final Iban iban;
	private double balance;

	protected BankAccount(double initialBalance) {
		if (initialBalance < 0) {
			throw new IllegalArgumentException("Initial balance cannot be negative");
		}
		this.id = nextId++;
		this.iban = Iban.generateForAccount(this.id);
		this.balance = initialBalance;
	}

	public long getId() {
		return id;
	}

	public Iban getIban() {
		return iban;
	}

	@Override
	public double getBalance() {
		return balance;
	}

	@Override
	public void deposit(double amount) {
		validateAmount(amount);
		balance += amount;
	}

	@Override
	public void withdraw(double amount) {
		validateAmount(amount);
		if (amount > balance) {
			throw new InsufficientFundsException("Insufficient funds for account " + iban);
		}
		balance -= amount;
	}

	@Override
	public void transferTo(BankAccount destination, double amount) {
		if (destination == null) {
			throw new IllegalArgumentException("Destination account cannot be null");
		}
		withdraw(amount);
		destination.deposit(amount);
	}

	protected void validateAmount(double amount) {
		if (amount <= 0) {
			throw new IllegalArgumentException("Amount must be positive");
		}
	}
}
