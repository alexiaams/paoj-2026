package com.pao.BankingApp.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.pao.BankingApp.repository.AccountRepository;
import com.pao.BankingApp.repository.ClientRepository;

public class Client extends Person {
    private static final double PLATINUM_SPENDING_THRESHOLD = 10_000.0;
    private static final int STUDENT_MAX_AGE = 25;
    private static final Map<Long, Client> ACCOUNT_OWNERS = new HashMap<>();

    private final String clientCode;
    private final LocalDate onboardingDate;
    private boolean kycVerified;
    private ClientType clientType;
    private final List<BankAccount> accounts;
    private double totalSpent;

    public Client(String firstName, String lastName, String CNP, String phoneNumber) {
        this(firstName, lastName, CNP, phoneNumber, ClientType.NORMAL);
    }

    public Client(String firstName, String lastName, String CNP, String phoneNumber, ClientType clientType) {
        super(firstName, lastName, CNP, phoneNumber);
        this.clientCode = "CL-" + getId();
        this.onboardingDate = LocalDate.now();
        this.kycVerified = false;
        this.totalSpent = 0;
        setClientType(clientType);
        this.accounts = new ArrayList<>();
    }

    public Client(long id, String firstName, String lastName, String CNP, String phoneNumber, ClientType clientType, LocalDate onboardingDate, boolean kycVerified, double totalSpent) {
        super(id, firstName, lastName, CNP, phoneNumber);
        this.clientCode = "CL-" + getId();
        this.onboardingDate = onboardingDate == null ? LocalDate.now() : onboardingDate;
        this.kycVerified = kycVerified;
        this.totalSpent = 0;
        setClientType(clientType);
        this.accounts = new ArrayList<>();
        if (totalSpent < 0) {
            throw new IllegalArgumentException("Total spent cannot be negative");
        }
        if (totalSpent > 0) {
            registerSpend(totalSpent);
        }
    }

    @Override
    public String getPersonType() {
        return "Client";
    }

    public String getClientCode() {
        return clientCode;
    }

    public LocalDate getOnboardingDate() {
        return onboardingDate;
    }

    public boolean isKycVerified() {
        return kycVerified;
    }

    public void setKycVerified(boolean kycVerified) {
        this.kycVerified = kycVerified;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        if (isStudentByAge()) {
            this.clientType = ClientType.STUDENT;
            return;
        }

        if (clientType == null) {
            throw new IllegalArgumentException("Client type cannot be null");
        }
        this.clientType = clientType;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void registerSpend(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Spent amount must be positive");
        }

        totalSpent += amount;
        if (clientType == ClientType.NORMAL && totalSpent >= PLATINUM_SPENDING_THRESHOLD && !isStudentByAge()) {
            clientType = ClientType.PLATINUM;
        }

        try {
            new ClientRepository().update(this);
        } catch (RuntimeException e) {
            System.err.println("Warning: failed to persist client spending to DB: " + e.getMessage());
        }
    }

    public List<BankAccount> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    public void addAccount(BankAccount account) {
        addAccountInternal(account, true);
    }

    public void attachAccountFromDatabase(BankAccount account) {
        addAccountInternal(account, false);
    }

    private void addAccountInternal(BankAccount account, boolean persistToDatabase) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        for (BankAccount existing : accounts) {
            if (existing.getId() == account.getId()) {
                throw new IllegalArgumentException("Account is already assigned to this client");
            }
        }
        accounts.add(account);
        ACCOUNT_OWNERS.put(account.getId(), this);
        if (persistToDatabase) {
            try {
                new AccountRepository().updateClientOwner(account.getId(), getId());
            } catch (RuntimeException e) {
                System.err.println("Warning: failed to persist account owner to DB: " + e.getMessage());
            }
        }
    }

    public boolean removeAccountById(long accountId) {
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getId() == accountId) {
                accounts.remove(i);
                ACCOUNT_OWNERS.remove(accountId);
                return true;
            }
        }
        return false;
    }

    public static Optional<Client> findOwnerByAccountId(long accountId) {
        return Optional.ofNullable(ACCOUNT_OWNERS.get(accountId));
    }

    public int getAge() {
        LocalDate birthDate = extractBirthDateFromCnp(getCNP());
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public boolean isStudentByAge() {
        return getAge() <= STUDENT_MAX_AGE;
    }

    private LocalDate extractBirthDateFromCnp(String cnp) {
        int firstDigit = Character.digit(cnp.charAt(0), 10);
        int year = Integer.parseInt(cnp.substring(1, 3));
        int month = Integer.parseInt(cnp.substring(3, 5));
        int day = Integer.parseInt(cnp.substring(5, 7));

        int century;
        if (firstDigit == 1 || firstDigit == 2) {
            century = 1900;
        } else if (firstDigit == 3 || firstDigit == 4) {
            century = 1800;
        } else if (firstDigit == 5 || firstDigit == 6 || firstDigit == 7 || firstDigit == 8) {
            century = 2000;
        } else {
            throw new IllegalArgumentException("Unsupported CNP first digit: " + firstDigit);
        }

        return LocalDate.of(century + year, month, day);
    }

    public boolean hasAccounts() {
        return !accounts.isEmpty();
    }

    public double getTotalBalance() {
        double total = 0;
        for (BankAccount account : accounts) {
            total += account.getBalance();
        }
        return total;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + getId() +
                ", fullName='" + getFullName() + '\'' +
                ", clientCode='" + clientCode + '\'' +
                ", onboardingDate=" + onboardingDate +
                ", kycVerified=" + kycVerified +
                ", clientType=" + clientType +
                ", totalSpent=" + totalSpent +
                ", accountsCount=" + accounts.size() +
                '}';
    }
}
