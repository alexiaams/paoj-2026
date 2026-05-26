# BankingApp

BankingApp is a console-based banking demo used in the PAOJ 2026 materials. It combines the original in-memory OOP model with SQLite-backed persistence, JDBC repositories, reporting queries, and audit logging.

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
├── repository/
│   ├── AccountRepository.java
│   ├── CardRepository.java
│   ├── ClientRepository.java
│   ├── ReportingRepository.java
│   ├── Repository.java
│   └── TransactionRepository.java
└── service/
	├── AccountService.java
	├── AuditService.java
	└── TransactionService.java

resources/
├── db.properties
└── schema.sql
```

## Architecture Overview

The application is split into three clear parts:

1. `Main` builds the scenario and prints each operation.
2. `model` contains the banking domain objects and their rules.
3. `service` contains singleton services that keep all state in memory.

The domain objects still live in memory during the demo, but the part II implementation also persists clients, accounts, cards, and transactions in SQLite through JDBC repositories.

## Package Responsibilities

| Package | Role |
|---|---|
| `com.pao.BankingApp` | Console entry point and scenario orchestration |
| `com.pao.BankingApp.model` | Domain entities, value objects, inheritance hierarchy, and business rules |
| `com.pao.BankingApp.repository` | JDBC repositories and reporting queries |
| `com.pao.BankingApp.service` | Singleton services for accounts, transactions, and auditing |
| `com.pao.BankingApp.util` | Database connection and schema bootstrap |
| `com.pao.BankingApp.exception` | Custom runtime exceptions |

## OOP Concepts Used

### Encapsulation
# BankingApp

BankingApp is a console-based banking demo used in the PAOJ 2026 materials. It started as an in-memory OOP exercise and in Etapa II was extended with SQLite persistence (JDBC), repository classes, reporting queries, and audit logging.

## Correct Project Tree (relevant parts)

The README previously showed only the local package layout. Below is a repository-level view (paths are workspace-relative):

```text
paoj-2026/
├── src/
│   └── com/pao/BankingApp/
│       ├── Main.java
│       ├── exception/
│       ├── model/
│       ├── repository/
│       ├── service/
│       └── util/
├── resources/
│   ├── db.properties
│   └── schema.sql
├── lib/
│   └── sqlite-jdbc-3.42.0.0.jar  (required at runtime; keep under BankingApp/lib for demo)
└── run-wsl.sh
```

Inside `src/com/pao/BankingApp` the package layout is:

```text
src/com/pao/BankingApp/
├── Main.java
├── exception/
├── model/
├── repository/
├── service/
└── util/
```

See the actual files in the workspace for full listing.

## Etapa II — What I implemented and where

This section describes exactly what was added/changed for Etapa II (JDBC persistence and reporting):

- Persistence layer (JDBC repositories):
	- `src/com/pao/BankingApp/repository/` contains `ClientRepository`, `AccountRepository`, `CardRepository`, `TransactionRepository`, and `ReportingRepository` implementing CRUD and reporting queries.

- Database bootstrap and configuration:
	- `resources/schema.sql` contains the SQL DDL used by `SchemaRunner` to create tables.
	- `resources/db.properties` contains DB settings used by `DatabaseConnection`.
	- `src/com/pao/BankingApp/util/SchemaRunner.java` runs the schema when `db.init=true`.

- Connection management:
	- `src/com/pao/BankingApp/util/DatabaseConnection.java` centralizes JDBC `Connection` creation (uses sqlite-jdbc).

- Transactional operations:
	- `TransactionService.transfer()` now performs transfers inside an explicit JDBC transaction and uses `TransactionRepository.saveWithConnection()` to persist the `Transaction` atomically with account updates.

- Data consistency fixes (important):
	- Model classes now support constructing objects with the explicit DB id/IBAN/timestamp when loading from the database; repository `mapRowToX` methods reconstruct objects while preserving DB identifiers (so in-memory IDs match the DB and reporting queries are consistent).
	- `Client.registerSpend()` upgrades client type in memory and the change is persisted via `ClientRepository.update()` so reports reflect the upgrade.

- Audit logging:
	- `AuditService` appends demo actions to `audit.csv` in a thread-safe way (uses `ReentrantLock`).

- Reporting queries:
	- `ReportingRepository` contains JOIN queries that produce the demo reports shown by `Main` (clients + accounts + transactions, aggregates, counts).

## How Etapa II maps to the demo (Main)

- The demo (`Main`) now demonstrates the same scenario but with persistent storage: clients/accounts/cards/transactions written to SQLite and read back on demand. Key observable effects:
	- Transaction rows have stable IDs and timestamps after persistence.
	- Client spending upgrades are reflected in DB-backed reports.
	- Reporting queries show joined data from `clients`, `accounts`, `cards`, and `transactions` tables.

## Run the demo

From WSL, inside the project root or `src/com/pao/BankingApp`:

```bash
./run-wsl.sh
```

Requirements:

- Place `sqlite-jdbc-3.42.0.0.jar` under `src/com/pao/BankingApp/lib/` (or `paoj-2026/lib/` and update classpath in the run script).
- `resources/schema.sql` and `resources/db.properties` must be present (they are in the repo).

Generated files you can remove between runs:

- `out/` (compiled classes)
- `audit.csv` (audit log)
- `paoj_proiect.db` (SQLite database file)

## Notes and known limitations

- The repository preserves in-memory demo behavior while adding persistence — the project still uses plain `javac`/`java` (no Maven/Gradle).
- A full workspace compile may fail due to unrelated labs and missing third-party jars; the BankingApp demo compiles and runs in isolation.

If you want the README formatted differently, or a shorter student-facing summary for submission, tell me the exact layout you prefer and I will adjust it.
| `SavingsAccount` | concrete specialization with extra interest-rate behavior |
