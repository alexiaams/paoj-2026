package com.pao.BankingApp.service;

import com.pao.BankingApp.model.BankAccount;
import com.pao.BankingApp.model.Client;
import com.pao.BankingApp.model.Transaction;
import com.pao.BankingApp.util.DatabaseConnection;
import com.pao.BankingApp.repository.TransactionRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
		if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");

		BankAccount source = accountService.getAccountById(sourceAccountId);
		BankAccount destination = accountService.getAccountById(destinationAccountId);

		if (source.getBalance() < amount) {
			throw new IllegalArgumentException("Insufficient funds in source account");
		}

		TransactionRepository txRepo = new TransactionRepository();

		// Perform DB transaction: debit source, credit destination, insert transaction record (use repository to insert within same connection)
		boolean dbSucceeded = false;
		try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
			conn.setAutoCommit(false);
			try (PreparedStatement debit = conn.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE id = ?");
				 PreparedStatement credit = conn.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE id = ?");
				 ) {

				debit.setDouble(1, amount);
				debit.setLong(2, sourceAccountId);
				debit.executeUpdate();

				credit.setDouble(1, amount);
				credit.setLong(2, destinationAccountId);
				credit.executeUpdate();

				// create transaction object and persist using repository within same connection
				Transaction transaction = new Transaction(sourceAccountId, destinationAccountId, amount, description);
				txRepo.saveWithConnection(conn, transaction);

				conn.commit();
				dbSucceeded = true;
			} catch (SQLException e) {
				conn.rollback();
				System.err.println("DB transaction error, rolled back: " + e.getMessage());
			} finally {
				conn.setAutoCommit(true);
			}
		} catch (SQLException e) {
			System.err.println("Warning: DB unavailable during transfer: " + e.getMessage());
		}

		if (!dbSucceeded) {
			// Fallback to in-memory operations when DB is not available
			source.withdraw(amount);
			destination.deposit(amount);
			Transaction transaction = new Transaction(sourceAccountId, destinationAccountId, amount, description);
			transactions.add(transaction);
			Client.findOwnerByAccountId(sourceAccountId).ifPresent(client -> client.registerSpend(amount));
			AuditService.getInstance().log("transfer_money");
			return transaction;
		}

		// Update in-memory model to reflect DB changes
		source.withdraw(amount);
		destination.deposit(amount);

		Transaction transaction = new Transaction(sourceAccountId, destinationAccountId, amount, description);
		transactions.add(transaction);
		Client.findOwnerByAccountId(sourceAccountId).ifPresent(client -> client.registerSpend(amount));
		AuditService.getInstance().log("transfer_money");
		return transaction;
	}

	public List<Transaction> getAllTransactions() {
		TransactionRepository repo = new TransactionRepository();
		return repo.findAll();
	}

	public List<Transaction> getTransactionsForAccount(long accountId) {
		TransactionRepository repo = new TransactionRepository();
		return repo.findByAccountId(accountId);
	}

	public double getTotalTransferredAmount() {
		TransactionRepository repo = new TransactionRepository();
		return repo.sumAmounts();
	}

	public int getNumberOfTransactions() {
		TransactionRepository repo = new TransactionRepository();
		return repo.countAll();
	}
}
