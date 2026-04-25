# BankingApp

BankingApp is a console-based, in-memory banking demo used in the PAOJ 2026 materials. It is built to show core OOP ideas in Java: abstraction, inheritance, encapsulation, polymorphism, immutable objects, custom exceptions, and singleton services.

## Repository Structure

Only the BankingApp part of the repository is shown here:

```text
src/com/pao/BankingApp/
├── Main.java
├── README.md
├── exception/
│   ├── AccountNotFoundException.java
│   └── InsufficientFundsException.java
├── model/
│   ├── Bank.java
│   ├── BankAccount.java
│   ├── Card.java
│   ├── CheckingAccount.java
│   ├── Client.java
│   ├── ClientType.java
│   ├── Department.java
│   ├── Employee.java
│   ├── Iban.java
│   ├── Person.java
│   ├── SavingsAccount.java
│   ├── Transaction.java
│   └── TransferPaymentOperations.java
└── service/
	├── AccountService.java
	└── TransactionService.java
```

## Architecture Overview

The application is split into three clear parts:

1. `Main` builds the scenario and prints each operation.
2. `model` contains the banking domain objects and their rules.
3. `service` contains singleton services that keep all state in memory.

The entire app is intentionally lightweight. There is no database, no web framework, and no persistence layer. State is stored using `Map` and `List` collections.

## Package Responsibilities

| Package | Role |
|---|---|
| `com.pao.BankingApp` | Console entry point and scenario orchestration |
| `com.pao.BankingApp.model` | Domain entities, value objects, inheritance hierarchy, and business rules |
| `com.pao.BankingApp.service` | Singleton services for accounts and transactions |
| `com.pao.BankingApp.exception` | Custom runtime exceptions |

## OOP Concepts Used

### Encapsulation

Most fields are `private`, and access happens through getters, setters, and behavior methods.

- `Person` encapsulates identity and contact data.
- `BankAccount` encapsulates account identity and balance changes.
- `Card` encapsulates card number, expiration, active state, and linked account id.
- `Transaction` encapsulates transfer data as a read-only record-like object.

### Abstraction

Abstract types define shared behavior and reduce duplication.

- `Person` is the abstract base for `Client` and `Employee`.
- `BankAccount` is the abstract base for `CheckingAccount` and `SavingsAccount`.
- `TransferPaymentOperations` defines the contract for payment and transfer behavior.

### Inheritance

The hierarchy is used in two places:

- `Person -> Client` and `Person -> Employee`
- `BankAccount -> CheckingAccount` and `BankAccount -> SavingsAccount`

This lets the code reuse shared logic while still allowing type-specific behavior.

### Polymorphism

Polymorphism appears when the code works with the base type but executes subclass behavior:

- `BankAccount` references can point to `CheckingAccount` or `SavingsAccount`.
- `Person` behavior is specialized by `Client` and `Employee`.
- The `describeAccount()` method in `Main` checks whether an account is a `SavingsAccount` to show the interest rate.

### Immutable Object Design

`Iban` and `Transaction` are designed as immutable objects:

- their important fields are `final`
- they are fully initialized in the constructor
- they do not expose setters
- they model values rather than mutable state

### Singleton Pattern

Both services use the Singleton pattern:

- `AccountService.getInstance()`
- `TransactionService.getInstance()`

This keeps one in-memory registry for the whole application run.

### Validation and Exceptions

Validation is done close to the data:

- `Person` validates CNP format
- `SavingsAccount` validates interest rate bounds
- `Bank` prevents duplicate CNPs
- `BankAccount` rejects invalid deposits and withdrawals
- `AccountNotFoundException` and `InsufficientFundsException` communicate domain errors clearly

## Main Classes

| Class | Role |
|---|---|
| `Main` | Runs the demonstration scenario |
| `Bank` | Holds clients, employees, accounts, and cards in memory |
| `Person` | Abstract base class for people in the system |
| `Client` / `Employee` | Concrete person types |
| `BankAccount` | Abstract base class for accounts |
| `CheckingAccount` / `SavingsAccount` | Concrete account types |
| `Card` | Bank card linked to an account |
| `Transaction` | Immutable transfer record |
| `Iban` | Immutable IBAN value object |
| `AccountService` | Account registry and lookup service |
| `TransactionService` | Transfer orchestration and transaction history |

## OOP Mapping By Class

| Class | OOP idea used |
|---|---|
| `Person` | abstraction, encapsulation, inheritance root, `equals()` / `hashCode()` by CNP |
| `Client` | inheritance, collection ownership, business rule enforcement, stateful domain object |
| `Employee` | inheritance, encapsulation, typed role and department data |
| `BankAccount` | abstraction, shared balance logic, interface implementation |
| `CheckingAccount` | concrete specialization of `BankAccount` |
| `SavingsAccount` | concrete specialization with extra interest-rate behavior |
| `Card` | encapsulated linked value with identity, activation state, and custom formatting |
| `Transaction` | immutable transfer snapshot, identity-based equality |
| `Iban` | immutable validated value object |
| `Bank` | aggregate-like in-memory coordinator |
| `AccountService` | Singleton service, collection-based registry |
| `TransactionService` | Singleton service, transfer workflow and history |

## Domain Rules

| Area | Rule |
|---|---|
| Person validation | CNP must contain exactly 13 digits |
| Client type | Clients under 26 are forced to `STUDENT` |
| Spending upgrade | A `NORMAL` client becomes `PLATINUM` after reaching the spending threshold |
| Bank integrity | `Bank` rejects duplicate CNP values |
| Account operations | Deposits and withdrawals require positive amounts |
| Withdrawal safety | Withdrawals fail when balance is insufficient |
| Transfer processing | Transfers move money and create a `Transaction` |
| IBAN generation | IBAN values are generated randomly and validated with mod-97 checksum |
| Savings policy | Savings interest rate must stay within the allowed range |
| Card creation | Cards can only be issued for known accounts |
| Card number format | Card numbers are random 16-digit values with a Luhn check digit |

## Exceptions

| Exception | When it is used |
|---|---|
| `AccountNotFoundException` | Missing account lookup or invalid card-to-account reference |
| `InsufficientFundsException` | Withdrawal or transfer cannot be completed because of low balance |

## Main Actions

`Main` demonstrates the application through these actions:

1. Create and register clients, including a student-type client.
2. Create and register an employee, including duplicate CNP validation.
3. Create checking and savings accounts, including interest-rate validation.
4. Register accounts in both `Bank` and `AccountService`.
5. Attach accounts to clients and issue cards.
6. Perform deposits and withdrawals.
7. Perform transfers and record transactions.
8. Search clients, employees, accounts, cards, and IBANs.
9. List all stored data and sort accounts/clients by balance.
10. Remove a card and an account, then handle custom exceptions.
11. Print the final transaction and balance summary.

## More About The Services

### `AccountService`

`AccountService` stores accounts in a `Map<Long, BankAccount>` keyed by account id. It supports:

- add account
- find account by id
- find account by IBAN
- remove account
- list all accounts
- list accounts sorted by balance descending

### `TransactionService`

`TransactionService` stores transactions in a `List<Transaction>`. It supports:

- transfer money between two accounts
- keep a transfer history
- list all transactions
- list transactions for one account
- compute the total transferred amount

## Collection Usage

| Class | Collections used |
|---|---|
| `Bank` | `Map` for clients, employees, accounts, and cards |
| `Client` | `List` for owned accounts |
| `AccountService` | `Map` for accounts by id |
| `TransactionService` | `List` for transaction history |

## Project Shape In One View

```text
src/com/pao/BankingApp/
├── Main.java
├── exception/
│   ├── AccountNotFoundException.java
│   └── InsufficientFundsException.java
├── model/
│   ├── Bank.java
│   ├── BankAccount.java
│   ├── Card.java
│   ├── CheckingAccount.java
│   ├── Client.java
│   ├── ClientType.java
│   ├── Department.java
│   ├── Employee.java
│   ├── Iban.java
│   ├── Person.java
│   ├── SavingsAccount.java
│   ├── Transaction.java
│   └── TransferPaymentOperations.java
└── service/
	├── AccountService.java
	└── TransactionService.java
```
