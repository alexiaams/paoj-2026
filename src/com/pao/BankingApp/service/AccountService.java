package com.pao.BankingApp.service;

import com.pao.BankingApp.exception.AccountNotFoundException;
import com.pao.BankingApp.model.BankAccount;
import com.pao.BankingApp.model.Iban;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class AccountService {
	private static final AccountService INSTANCE = new AccountService();

	private final Map<Long, BankAccount> accountsById;

	private AccountService() {
		this.accountsById = new HashMap<>();
	}

	public static AccountService getInstance() {
		return INSTANCE;
	}

	public void addAccount(BankAccount account) {
		if (account == null) {
			throw new IllegalArgumentException("Account cannot be null");
		}
		accountsById.put(account.getId(), account);
	}

	public BankAccount getAccountById(long accountId) {
		BankAccount account = accountsById.get(accountId);
		if (account == null) {
			throw new AccountNotFoundException("Account not found with id: " + accountId);
		}
		return account;
	}

	public Optional<BankAccount> findAccountByIban(Iban iban) {
		if (iban == null) {
			throw new IllegalArgumentException("IBAN cannot be null");
		}

		for (BankAccount account : accountsById.values()) {
			if (account.getIban().equals(iban)) {
				return Optional.of(account);
			}
		}
		return Optional.empty();
	}

	public boolean removeAccount(long accountId) {
		return accountsById.remove(accountId) != null;
	}

	public List<BankAccount> getAllAccounts() {
		return new ArrayList<>(accountsById.values());
	}

	public List<BankAccount> getAccountsSortedByBalanceDesc() {
		List<BankAccount> sorted = getAllAccounts();
		sorted.sort(Comparator.comparingDouble(BankAccount::getBalance).reversed());
		return sorted;
	}

	public int getNumberOfAccounts() {
		return accountsById.size();
	}
}
