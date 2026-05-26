package com.pao.BankingApp.model;

import com.pao.BankingApp.exception.AccountNotFoundException;
import com.pao.BankingApp.repository.ClientRepository;
import com.pao.BankingApp.repository.CardRepository;
import com.pao.BankingApp.service.AuditService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Bank {
	private final String name;
	private final String bic;
	private final Map<Long, Client> clientsById;
	private final Map<Long, Employee> employeesById;
	private final Map<Long, BankAccount> accountsById;
	private final Map<Long, Card> cardsById;
	private final ClientRepository clientRepository;
	private final CardRepository cardRepository;

	public Bank(String name, String bic) {
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("Bank name cannot be empty");
		}
		if (bic == null || bic.isBlank()) {
			throw new IllegalArgumentException("BIC cannot be empty");
		}

		this.name = name.trim();
		this.bic = bic.trim().toUpperCase();
		this.clientsById = new HashMap<>();
		this.employeesById = new HashMap<>();
		this.accountsById = new HashMap<>();
		this.cardsById = new HashMap<>();
		this.clientRepository = new ClientRepository();
		this.cardRepository = new CardRepository();
	}

	public String getName() {
		return name;
	}

	public String getBic() {
		return bic;
	}

	public void addClient(Client client) {
		if (client == null) {
			throw new IllegalArgumentException("Client cannot be null");
		}
		if (hasPersonWithCnp(client.getCNP())) {
			throw new IllegalArgumentException("A person with this CNP is already registered in the bank");
		}
		try {
			clientRepository.save(client);
		} catch (RuntimeException e) {
			System.err.println("Warning: failed to persist client to DB: " + e.getMessage());
		}
		clientsById.put(client.getId(), client);
		AuditService.getInstance().log("create_client");
	}

	public void addEmployee(Employee employee) {
		if (employee == null) {
			throw new IllegalArgumentException("Employee cannot be null");
		}
		if (hasPersonWithCnp(employee.getCNP())) {
			throw new IllegalArgumentException("A person with this CNP is already registered in the bank");
		}
		employeesById.put(employee.getId(), employee);
		AuditService.getInstance().log("create_employee");
	}

	private boolean hasPersonWithCnp(String cnp) {
		for (Client client : clientsById.values()) {
			if (client.getCNP().equals(cnp)) {
				return true;
			}
		}

		for (Employee employee : employeesById.values()) {
			if (employee.getCNP().equals(cnp)) {
				return true;
			}
		}

		return false;
	}

	public void addAccount(BankAccount account) {
		if (account == null) {
			throw new IllegalArgumentException("Account cannot be null");
		}
		accountsById.put(account.getId(), account);
		AuditService.getInstance().log("register_account");
	}

	public void addCard(Card card) {
		if (card == null) {
			throw new IllegalArgumentException("Card cannot be null");
		}
		if (!accountsById.containsKey(card.getAccountId())) {
			throw new AccountNotFoundException("Cannot attach card. Account not found with id: " + card.getAccountId());
		}
		try {
			cardRepository.save(card);
		} catch (RuntimeException e) {
			System.err.println("Warning: failed to persist card to DB: " + e.getMessage());
		}
		cardsById.put(card.getId(), card);
		AuditService.getInstance().log("assign_account_and_issue_card");
	}

	public Optional<Client> findClientById(long clientId) {
		Client c = clientsById.get(clientId);
		if (c != null) return Optional.of(c);
		Optional<Client> fromDb = clientRepository.findById(clientId);
		fromDb.ifPresent(client -> clientsById.put(client.getId(), client));
		if (fromDb.isPresent()) {
			AuditService.getInstance().log("find_by_id");
		}
		return fromDb;
	}

	public Optional<Employee> findEmployeeById(long employeeId) {
		return Optional.ofNullable(employeesById.get(employeeId));
	}

	public Optional<BankAccount> findAccountById(long accountId) {
		return Optional.ofNullable(accountsById.get(accountId));
	}

	public Optional<Card> findCardById(long cardId) {
		return Optional.ofNullable(cardsById.get(cardId));
	}

	public List<Client> getAllClients() {
		if (clientsById.isEmpty()) {
			List<Client> fromDb = clientRepository.findAll();
			for (Client c : fromDb) clientsById.put(c.getId(), c);
		}
		return new ArrayList<>(clientsById.values());
	}

	public List<Employee> getAllEmployees() {
		return new ArrayList<>(employeesById.values());
	}

	public List<BankAccount> getAllAccounts() {
		return new ArrayList<>(accountsById.values());
	}

	public List<Card> getAllCards() {
		return new ArrayList<>(cardsById.values());
	}

	public boolean removeCard(long cardId) {
		AuditService.getInstance().log("remove_entity");
		return cardsById.remove(cardId) != null;
	}

	public List<Client> getClientsSortedByTotalBalanceDesc() {
		List<Client> clients = getAllClients();
		clients.sort(Comparator.comparingDouble(Client::getTotalBalance).reversed());
		return clients;
	}

	public int getTotalPeopleCount() {
		return clientsById.size() + employeesById.size();
	}

	@Override
	public String toString() {
		return "Bank{" +
				"name='" + name + '\'' +
				", bic='" + bic + '\'' +
				", clients=" + clientsById.size() +
				", employees=" + employeesById.size() +
				", accounts=" + accountsById.size() +
				", cards=" + cardsById.size() +
				'}';
	}
}
