package com.pao.BankingApp.model;

public class CheckingAccount extends BankAccount {
	public CheckingAccount(double initialBalance) {
		super(initialBalance);
	}

	public CheckingAccount(long id, Iban iban, double initialBalance) {
		super(id, iban, initialBalance);
	}
}
