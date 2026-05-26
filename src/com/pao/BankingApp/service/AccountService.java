package com.pao.BankingApp.service;

import com.pao.BankingApp.exception.AccountNotFoundException;
import com.pao.BankingApp.model.BankAccount;
import com.pao.BankingApp.model.Iban;
import com.pao.BankingApp.repository.AccountRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.pao.BankingApp.service.AuditService;

public final class AccountService {
	private static final AccountService INSTANCE = new AccountService();

	private final Map<Long, BankAccount> accountsById;
	private final AccountRepository accountRepository;

	private AccountService() {
		this.accountsById = new HashMap<>();
		this.accountRepository = new AccountRepository();
	}

	public static AccountService getInstance() {
		return INSTANCE;
	}

	public void addAccount(BankAccount account) {
		if (account == null) {
			throw new IllegalArgumentException("Account cannot be null");
		}
		// try to persist to DB; if driver unavailable, continue in-memory
		try {
			accountRepository.save(account, null);
		} catch (RuntimeException e) {
			System.err.println("Warning: failed to persist account to DB: " + e.getMessage());
		}
		accountsById.put(account.getId(), account);
		AuditService.getInstance().log("create_account");
	}

	public BankAccount getAccountById(long accountId) {
		BankAccount account = accountsById.get(accountId);
		if (account == null) {
			Optional<BankAccount> fromDb = accountRepository.findById(accountId);
			if (fromDb.isPresent()) {
				account = fromDb.get();
				accountsById.put(account.getId(), account);
			} else {
				throw new AccountNotFoundException("Account not found with id: " + accountId);
			}
		}
		return account;
	}


	public Optional<BankAccount> findAccountByIban(Iban iban) {
		if (iban == null) {
			throw new IllegalArgumentException("IBAN cannot be null");
		}

		Optional<BankAccount> fromDb = accountRepository.findByIban(iban.getValue());
		if (fromDb.isPresent()) {
			BankAccount acc = fromDb.get();
			accountsById.put(acc.getId(), acc);
			return Optional.of(acc);
		}

		for (BankAccount account : accountsById.values()) {
			if (account.getIban().equals(iban)) {
				return Optional.of(account);
			}
		}
		return Optional.empty();
	}

	public boolean removeAccount(long accountId) {
		accountRepository.delete(accountId);
		AuditService.getInstance().log("remove_entity");
		return accountsById.remove(accountId) != null;
	}

	public List<BankAccount> getAllAccounts() {
		if (accountsById.isEmpty()) {
			List<BankAccount> fromDb = accountRepository.findAll();
			for (BankAccount a : fromDb) {
				accountsById.put(a.getId(), a);
			}
		}
		return new ArrayList<>(accountsById.values());
	}

	public List<BankAccount> getAccountsSortedByBalanceDesc() {
		List<BankAccount> sorted = getAllAccounts();
		sorted.sort(Comparator.comparingDouble(BankAccount::getBalance).reversed());
		AuditService.getInstance().log("list_and_sort");
		return sorted;
	}

	public int getNumberOfAccounts() {
		if (!accountsById.isEmpty()) return accountsById.size();
		return accountRepository.findAll().size();
	}
}
