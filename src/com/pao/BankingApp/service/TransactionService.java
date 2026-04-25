package com.pao.BankingApp.service;

import com.pao.BankingApp.model.BankAccount;
import com.pao.BankingApp.model.Client;
import com.pao.BankingApp.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public final class TransactionService {
	private static final TransactionService INSTANCE = new TransactionService();

	private final List<Transaction> transactions;
	private final AccountService accountService;

	private TransactionService() {
		this.transactions = new ArrayList<>();
		this.accountService = AccountService.getInstance();
	}

	public static TransactionService getInstance() {
		return INSTANCE;
	}

	public Transaction transfer(long sourceAccountId, long destinationAccountId, double amount, String description) {
		BankAccount source = accountService.getAccountById(sourceAccountId);
		BankAccount destination = accountService.getAccountById(destinationAccountId);

		source.transferTo(destination, amount);

		Transaction transaction = new Transaction(sourceAccountId, destinationAccountId, amount, description);
		transactions.add(transaction);
		Client.findOwnerByAccountId(sourceAccountId).ifPresent(client -> client.registerSpend(amount));
		return transaction;
	}

	public List<Transaction> getAllTransactions() {
		return new ArrayList<>(transactions);
	}

	public List<Transaction> getTransactionsForAccount(long accountId) {
		List<Transaction> result = new ArrayList<>();
		for (Transaction transaction : transactions) {
			if (transaction.getSourceAccountId() == accountId || transaction.getDestinationAccountId() == accountId) {
				result.add(transaction);
			}
		}
		return result;
	}

	public double getTotalTransferredAmount() {
		double total = 0;
		for (Transaction transaction : transactions) {
			total += transaction.getAmount();
		}
		return total;
	}

	public int getNumberOfTransactions() {
		return transactions.size();
	}
}
