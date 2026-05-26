package com.pao.BankingApp.model;

import com.pao.BankingApp.exception.InsufficientFundsException;
import com.pao.BankingApp.service.AuditService;
import com.pao.BankingApp.repository.AccountRepository;

public abstract class BankAccount implements TransferPaymentOperations {
	private static long nextId = 1;

	private final long id;
	private final Iban iban;
	private double balance;

	protected BankAccount(double initialBalance) {
		this(generateId(), null, initialBalance, true);
	}

	protected BankAccount(long id, Iban iban, double initialBalance) {
		this(id, iban, initialBalance, false);
	}

	private BankAccount(long id, Iban iban, double initialBalance, boolean generateIban) {
		if (initialBalance < 0) {
			throw new IllegalArgumentException("Initial balance cannot be negative");
		}
		this.id = id;
		updateNextId(id);
		if (generateIban) {
			this.iban = Iban.generateForAccount(this.id);
		} else {
			if (iban == null) {
				throw new IllegalArgumentException("IBAN cannot be null");
			}
			this.iban = iban;
		}
		this.balance = initialBalance;
	}

	private static synchronized long generateId() {
		return nextId++;
	}

	private static synchronized void updateNextId(long id) {
		if (id >= nextId) {
			nextId = id + 1;
		}
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
		AuditService.getInstance().log("deposit_withdraw");
		try {
			new AccountRepository().update(this);
		} catch (RuntimeException e) {
			System.err.println("Warning: failed to persist deposit to DB: " + e.getMessage());
		}
	}

	@Override
	public void withdraw(double amount) {
		validateAmount(amount);
		if (amount > balance) {
			throw new InsufficientFundsException("Insufficient funds for account " + iban);
		}
		balance -= amount;
		AuditService.getInstance().log("deposit_withdraw");
		try {
			new AccountRepository().update(this);
		} catch (RuntimeException e) {
			System.err.println("Warning: failed to persist withdraw to DB: " + e.getMessage());
		}
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
