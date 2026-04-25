package com.pao.BankingApp;

import com.pao.BankingApp.exception.AccountNotFoundException;
import com.pao.BankingApp.exception.InsufficientFundsException;
import com.pao.BankingApp.model.Bank;
import com.pao.BankingApp.model.BankAccount;
import com.pao.BankingApp.model.Card;
import com.pao.BankingApp.model.CheckingAccount;
import com.pao.BankingApp.model.Client;
import com.pao.BankingApp.model.ClientType;
import com.pao.BankingApp.model.Department;
import com.pao.BankingApp.model.Employee;
import com.pao.BankingApp.model.SavingsAccount;
import com.pao.BankingApp.model.Transaction;
import com.pao.BankingApp.service.AccountService;
import com.pao.BankingApp.service.TransactionService;

import java.util.List;

public class Main {
	public static void main(String[] args) {
		AccountService accountService = AccountService.getInstance();
		TransactionService transactionService = TransactionService.getInstance();
		Bank bank = new Bank("PAOJ Bank", "PAOJROBU");

		printStep(1, "Create clients and demonstrate age-based type rules");
		Client client1 = new Client("Andrei", "Popescu", "1980101223344", "0712345678", ClientType.NORMAL);
		Client client2 = new Client("Ioana", "Marin", "2991209456789", "0723456789", ClientType.NORMAL);
		Client studentClient = new Client("Mara", "Ionescu", "5060101223344", "0745678901", ClientType.NORMAL);
		System.out.println(client1);
		System.out.println(client2);
		System.out.println(studentClient);
		bank.addClient(client1);
		bank.addClient(client2);
		bank.addClient(studentClient);
		System.out.println("Student client resolved type: " + studentClient.getClientType());

		printStep(2, "Create employee and demonstrate duplicate CNP rejection");
		Employee employee = new Employee(
				"Mihai",
				"Dumitru",
				"1870523123456",
				"0734567890",
				Department.RETAIL,
				"Account Manager",
				"Bucharest Central",
				6500
		);
		System.out.println(employee);
		bank.addEmployee(employee);
		try {
			bank.addEmployee(new Employee(
					"Fake",
					"Duplicate",
					client1.getCNP(),
					"0700000000",
					Department.IT,
					"Engineer",
					"Remote",
					7200
			));
		} catch (IllegalArgumentException e) {
			System.out.println("Handled duplicate CNP rule: " + e.getMessage());
		}

		printStep(3, "Create accounts and demonstrate savings interest validation");
		CheckingAccount checking1 = new CheckingAccount(1000);
		SavingsAccount savings1 = new SavingsAccount(2500, 0.04);
		CheckingAccount checking2 = new CheckingAccount(500);
		CheckingAccount checking3 = new CheckingAccount(750);
		try {
			new SavingsAccount(1000, 0.35);
		} catch (IllegalArgumentException e) {
			System.out.println("Handled invalid savings interest rate: " + e.getMessage());
		}
		System.out.println("Account " + checking1.getId() + " IBAN: " + checking1.getIban());
		System.out.println("Account " + savings1.getId() + " IBAN: " + savings1.getIban());
		System.out.println("Account " + checking2.getId() + " IBAN: " + checking2.getIban());
		System.out.println("Account " + checking3.getId() + " IBAN: " + checking3.getIban());

		printStep(4, "Register accounts in AccountService and Bank");
		accountService.addAccount(checking1);
		accountService.addAccount(savings1);
		accountService.addAccount(checking2);
		accountService.addAccount(checking3);
		bank.addAccount(checking1);
		bank.addAccount(savings1);
		bank.addAccount(checking2);
		bank.addAccount(checking3);
		System.out.println("Total registered accounts: " + accountService.getNumberOfAccounts());
		System.out.println("Bank total people count: " + bank.getTotalPeopleCount());

		printStep(5, "Assign accounts to clients and issue cards");
		client1.addAccount(checking1);
		client1.addAccount(savings1);
		client2.addAccount(checking2);
		studentClient.addAccount(checking3);
		Card card1 = new Card(client1.getFullName(), checking1.getId());
		Card card2 = new Card(client2.getFullName(), checking2.getId());
		Card card3 = new Card(studentClient.getFullName(), checking3.getId());
		bank.addCard(card1);
		bank.addCard(card2);
		bank.addCard(card3);
		System.out.println(client1.getFullName() + " total balance: " + client1.getTotalBalance());
		System.out.println(client2.getFullName() + " total balance: " + client2.getTotalBalance());
		System.out.println(studentClient.getFullName() + " total balance: " + studentClient.getTotalBalance());
		System.out.println("Issued card: " + card1);
		System.out.println("Issued card: " + card2);
		System.out.println("Issued card: " + card3);

		printStep(6, "Deposit and withdraw operations");
		checking1.deposit(200);
		savings1.withdraw(300);
		checking2.deposit(150);
		checking3.deposit(100);
		System.out.println("Checking1 balance: " + checking1.getBalance());
		System.out.println("Savings1 balance: " + savings1.getBalance());
		System.out.println("Checking2 balance: " + checking2.getBalance());
		System.out.println("Checking3 balance: " + checking3.getBalance());

		printStep(7, "Transfers, transaction recording, and platinum upgrade threshold");
		checking1.deposit(11_000);
		System.out.println("Checking1 balance prepared for large transfers: " + checking1.getBalance());
		Transaction tx1 = transactionService.transfer(checking1.getId(), checking2.getId(), 150, "Rent split");
		Transaction tx2 = transactionService.transfer(checking1.getId(), checking2.getId(), 5000, "Large transfer 1");
		Transaction tx3 = transactionService.transfer(checking1.getId(), checking2.getId(), 5000, "Large transfer 2");
		System.out.println(tx1);
		System.out.println(tx2);
		System.out.println(tx3);
		System.out.println("Checking1 new balance: " + checking1.getBalance());
		System.out.println("Checking2 new balance: " + checking2.getBalance());
		System.out.println("Client1 type after spending: " + client1.getClientType());
		System.out.println("Client1 total spent: " + client1.getTotalSpent());

		printStep(8, "Find objects by id and IBAN");
		System.out.println("Found client by id: " + bank.findClientById(client1.getId()).orElseThrow());
		System.out.println("Found employee by id: " + bank.findEmployeeById(employee.getId()).orElseThrow());
		System.out.println("Found account by id: " + describeAccount(bank.findAccountById(checking1.getId()).orElseThrow()));
		System.out.println("Found card by id: " + bank.findCardById(card1.getId()).orElseThrow());
		BankAccount foundByIban = accountService.findAccountByIban(savings1.getIban())
				.orElseThrow(() -> new RuntimeException("Expected account to exist by IBAN"));
		System.out.println("Found account by IBAN: " + describeAccount(foundByIban));

		printStep(9, "List all entities and sort accounts/clients");
		System.out.println("All clients: " + bank.getAllClients());
		System.out.println("All employees: " + bank.getAllEmployees());
		printAccounts("All bank accounts", bank.getAllAccounts());
		System.out.println("All cards: " + bank.getAllCards());
		printAccounts("All accounts in AccountService", accountService.getAllAccounts());
		System.out.println("All transactions: " + transactionService.getAllTransactions());
		System.out.println("Transactions for checking1: " + transactionService.getTransactionsForAccount(checking1.getId()));

		List<BankAccount> sorted = accountService.getAccountsSortedByBalanceDesc();
		for (BankAccount account : sorted) {
			System.out.println("Account " + account.getId() + " -> " + account.getBalance());
		}
		System.out.println("Bank client ranking by total balance: " + bank.getClientsSortedByTotalBalanceDesc());

		printStep(10, "Remove entities and trigger handled exceptions");
		boolean removedCard = bank.removeCard(card3.getId());
		boolean removedAccount = accountService.removeAccount(checking2.getId());
		System.out.println("Removed student card from bank: " + removedCard);
		System.out.println("Removed checking2 from AccountService: " + removedAccount);
		System.out.println("Bank cards after removal: " + bank.getAllCards());
		printAccounts("Accounts after removal from service", accountService.getAllAccounts());

		try {
			accountService.getAccountById(99999);
		} catch (AccountNotFoundException e) {
			System.out.println("Handled AccountNotFoundException: " + e.getMessage());
		}

		try {
			checking2.withdraw(10_000);
		} catch (InsufficientFundsException e) {
			System.out.println("Handled InsufficientFundsException: " + e.getMessage());
		}

		System.out.println("\nTotal transactions recorded: " + transactionService.getNumberOfTransactions());
		System.out.println("Total transferred amount: " + transactionService.getTotalTransferredAmount());
		System.out.println("Bank still has clients: " + bank.getAllClients().size());
		System.out.println("Bank still has employees: " + bank.getAllEmployees().size());
		System.out.println("Bank snapshot: " + bank);
	}

	private static void printStep(int index, String label) {
		System.out.println("\n=== Step " + index + ": " + label + " ===");
	}

	private static void printAccounts(String label, List<BankAccount> accounts) {
		System.out.println(label + ":");
		for (BankAccount account : accounts) {
			System.out.println("  - " + describeAccount(account));
		}
	}

	private static String describeAccount(BankAccount account) {
		StringBuilder description = new StringBuilder();
		description.append("Account{")
				.append("id=").append(account.getId())
				.append(", type=").append(account.getClass().getSimpleName())
				.append(", balance=").append(account.getBalance())
				.append(", iban=").append(account.getIban());
		if (account instanceof SavingsAccount savingsAccount) {
			description.append(", interestRate=").append(savingsAccount.getInterestRate());
		}
		description.append('}');
		return description.toString();
	}
}
