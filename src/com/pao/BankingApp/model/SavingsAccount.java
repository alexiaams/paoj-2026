package com.pao.BankingApp.model;

public class SavingsAccount extends BankAccount {
	private static final double MIN_INTEREST_RATE = 0.0;
	private static final double MAX_INTEREST_RATE = 0.20;

	private double interestRate;

	public SavingsAccount(double initialBalance, double interestRate) {
		super(initialBalance);
		setInterestRate(interestRate);
	}

	public double getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(double interestRate) {
		if (interestRate < MIN_INTEREST_RATE || interestRate > MAX_INTEREST_RATE) {
			throw new IllegalArgumentException("Interest rate must be between 0.0 and 0.20");
		}
		this.interestRate = interestRate;
	}
}
