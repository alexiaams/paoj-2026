# BankingApp - Proiect Individual PAOJ (2026)

**BankingApp** este o aplicatie consola Java conceputa ca sistem de gestiune bancara. Aceasta acopera modelarea orientata pe obiecte (Etapa I) si persistenta intr-o baza de date relationala SQLite utilizand JDBC, tranzactii si servicii de audit (Etapa II).

---

## 1. Definirea Sistemului (Etapa I)

### 1.1 - Lista de actiuni si interogari in sistem (Minim 10)
Aplicatia implementeaza urmatoarele actiuni descrise si demonstrate in clasa `Main`:
1.  **Adaugare / Inregistrare client nou** (cu validare CNP si determinare automata a tipului de client).
2.  **Adaugare angajat nou** (cu verificare la nivelul bancii pentru a evita duplicarea CNP-urilor).
3.  **Deschidere cont curent** (`CheckingAccount`) pentru un client.
4.  **Deschidere cont de economii** (`SavingsAccount`) cu validarea ratei dobanzii (intre 0% si 20%).
5.  **Atasare conturi bancare** clientilor existenti si inregistrarea acestora in gestiunea centralizata a serviciilor.
6.  **Emitere card de debit** asociat unui cont bancar, generand automat numar de card valid (algoritmul Luhn) si cod CVV.
7.  **Depunere numerar** in cont (actualizeaza soldul in memorie si in baza de date SQLite).
8.  **Retragere numerar** din cont (cu validare de fonduri suficiente, aruncand exceptii specifice).
9.  **Transfer de bani intre conturi** executat tranzactional in baza de date (debitare cont sursa, creditare cont destinatie si inregistrare tranzactie ca o singura unitate de lucru).
10. **Cautare entitati** (clienti, angajati, conturi, carduri) dupa ID, CNP sau IBAN.
11. **Sortare si clasificare**:
    *   Sortarea conturilor descrescator dupa sold.
    *   Sortarea clientilor descrescator dupa suma soldurilor cumulate.
12. **Dezactivare card si Stergere tranzactie** (operatiuni CRUD de tip update/delete din DB).

### 1.2 - Tipuri de obiecte din domeniu (Minim 8)
Sistemul utilizeaza urmatoarele obiecte de domeniu, aflate in pachetul `model`:
1.  `Person` (Clasa abstracta) - Modelul de baza pentru entitatile umane din sistem.
2.  `Client` - Reprezinta clientul bancii, asociat cu conturi, avand logica de upgrade de tip (ex: *Platinum* la cheltuieli mari).
3.  `Employee` - Reprezinta angajatii bancii cu department, functie, sucursala si salariu.
4.  `BankAccount` (Clasa abstracta) - Baza pentru conturi (gestioneaza IBAN, sold, depunere, retragere).
5.  `CheckingAccount` - Cont curent fara dobanda.
6.  `SavingsAccount` - Cont de economii cu dobanda aplicabila.
7.  `Card` - Reprezinta cardul fizic atasat unui cont, cu stari de activare/expirare.
8.  `Transaction` - Inregistrarea imutabila a unui transfer intre conturi.
9.  `Iban` (Value Object) - Incapsuleaza formatul specific unui cod IBAN.

---

## 2. Concepte OOP Aplicate (Etapa I)

*   **Mostenire pe 2 niveluri**: Ierarhia `Person` (abstracta) -> `Client` si `Employee`.
*   **Clasa abstracta in ierarhie**: `Person` declara metoda abstracta `getPersonType()`, implementata specific de subclase. `BankAccount` este de asemenea abstracta si implementeaza interfata `TransferPaymentOperations`.
*   **Clasa imutabila**: `Transaction` este imutabila. Toate atributele sunt `final`, nu are setteri si se initializeaza complet la constructie.
*   **Exceptii custom (minim 2)**: 
    *   `AccountNotFoundException` (aruncata la cautari de conturi inexistente).
    *   `InsufficientFundsException` (aruncata la retrageri care depasesc soldul disponibil).
*   **Colectii utilizate**:
    *   `List` (`ArrayList`) - Folosita in servicii si repozitorii pentru liste de conturi, clienti si tranzactii.
    *   `Set` (`HashSet`) - Folosit in `Card` pentru a asigura unicitatea numerelor de card generate global.
    *   `Map` (`HashMap`) - Folosit pentru indexare rapida in memorie (ex: maparea conturilor la detinatorii lor: `ACCOUNT_OWNERS`).
*   **Sortari**: Implementate cu `Comparator` (de exemplu in `AccountService.getAccountsSortedByBalanceDesc` sau `Bank.getClientsSortedByTotalBalanceDesc`).
*   **Servicii Singleton (Minim 2)**: 
    *   `AccountService` (gestioneaza conturile in memorie si DB).
    *   `TransactionService` (gestioneaza transferurile si tranzactiile).
    *   `AuditService` (serviciul central de jurnalizare).

---

## 3. Persistenta JDBC si Tranzactii (Etapa II)

### 3.1 - Schema Bazei de Date (`schema.sql` si `db.properties`)
Schema este definita in [schema.sql](file:///c:/Users/alexi/Desktop/PAOJ/paoj-2026/resources/schema.sql) si contine:
*   Tabelele: `clients`, `accounts`, `cards`, `transactions`.
*   Campuri de tip `PRIMARY KEY AUTOINCREMENT` pe toate tabelele.
*   **4 relatii de tip `FOREIGN KEY`** cu reguli de integritate:
    *   `accounts.client_id` -> `clients.id` (cu `ON DELETE CASCADE`)
    *   `cards.account_id` -> `accounts.id` (cu `ON DELETE CASCADE`)
    *   `transactions.source_account_id` -> `accounts.id`
    *   `transactions.destination_account_id` -> `accounts.id`
*   `DROP TABLE IF EXISTS` la inceputul scriptului pentru a garanta o re-rulare curata.

Conexiunea este configurata in mod extern prin [db.properties](file:///c:/Users/alexi/Desktop/PAOJ/paoj-2026/resources/db.properties), fara a hardcoda credentialele in cod. Clasa singleton `DatabaseConnection` incarca dinamic aceste proprietati pe baza classpath-ului.

### 3.2 - Repozitorii si CRUD Complet (>=4 entitati)
Interfata generica `Repository<T, ID>` este declarata in [Repository.java](file:///c:/Users/alexi/Desktop/PAOJ/paoj-2026/src/com/pao/BankingApp/repository/Repository.java). 

Sunt implementate 4 clase repozitorii concrete care realizeaza CRUD complet prin JDBC:
1.  **ClientRepository** - Gestioneaza clientii in tabela `clients`.
2.  **AccountRepository** - Gestioneaza conturile in tabela `accounts` (sincronizata cu owner-ul din memorie in caz de salvare fara ID specificat).
3.  **CardRepository** - Gestioneaza cardurile in tabela `cards`.
4.  **TransactionRepository** - Gestioneaza inregistrarile de tranzactie in tabela `transactions`.

*Nota:* Toate interogarile SQL din repozitorii folosesc **`PreparedStatement`** (pentru securitate impotriva SQL Injection) si inchid resursele JDBC in mod automat prin structura **`try-with-resources`**.

### 3.3 - Tranzactii JDBC Explicite
In clasa [TransactionService.java](file:///c:/Users/alexi/Desktop/PAOJ/paoj-2026/src/com/pao/BankingApp/service/TransactionService.java) la metoda `transfer()`, debitarea contului sursa, creditarea contului destinatie si scrierea istoricului in tabela `transactions` sunt grupate intr-o **tranzactie explicita**:
*   Dezactivare auto-commit (`conn.setAutoCommit(false)`).
*   Executare actualizari.
*   Commit la succes (`conn.commit()`).
*   Rollback automat in blocul `catch` in caz de `SQLException` (`conn.rollback()`).

### 3.4 - Interogari Avansate cu JOIN (Minim 3)
Clasa [ReportingRepository.java](file:///c:/Users/alexi/Desktop/PAOJ/paoj-2026/src/com/pao/BankingApp/repository/ReportingRepository.java) implementeaza 3 rapoarte complexe bazate pe JOIN-uri SQL:
1.  `listClientsWithAccountCountAndTotalBalance()` - Combina `clients` si `accounts` printr-un `LEFT JOIN` cu functii de agregare (`COUNT`, `SUM`, `COALESCE`) pentru a obtine o privire de ansamblu asupra portofoliului fiecarui client.
2.  `listTransactionsWithIbans()` - Realizeaza un `JOIN` multiplu intre `transactions` si `accounts` (de doua ori) pentru a afisa sumele transferate direct impreuna cu IBAN-ul sursa si cel destinatie.
3.  `listCardsWithAccountAndClientDetails()` - Face `JOIN` intre `cards`, `accounts` si `clients` pentru a genera detalii despre carduri, conturile asociate si numele titularului.

---

## 4. Serviciul de Audit (Etapa II)

Clasa [AuditService.java](file:///c:/Users/alexi/Desktop/PAOJ/paoj-2026/src/com/pao/BankingApp/service/AuditService.java) inregistreaza actiunile rulate in fisierul `audit.csv`.
*   **Format**: `nume_actiune,timestamp_ISO_8601`.
*   **Mod deschidere**: *Append* (pentru a nu pierde intrarile anterioare).
*   **Thread-safety**: Asigurat prin utilizarea unui `ReentrantLock` exclusiv pe zona critica de scriere in fisier.
*   Peste 10 actiuni distincte logheaza activitatea in fisier (ex: `create_client`, `create_account`, `transfer_money`, `deposit_withdraw`, `remove_entity`, `list_and_sort` etc.).

---

## 5. Structura Proiectului (Tree)

Proiectul este structurat respectand recomandarile oficiale din laboratoare:

```text
paoj-2026/
├── resources/
│   ├── db.properties               - Proprietati conexiune SQLite / MySQL
│   └── schema.sql                  - Schema bazei de date (DDL)
└── src/
    └── com/pao/BankingApp/
        ├── Main.java               - Orchestrator scenariu demo
        ├── README.md               - Acest document de documentare
        ├── audit.csv               - Fisierul de audit generat la rulare
        ├── paoj_proiect.db         - Fisierul bazei de date SQLite generat la rulare
        ├── exception/              - Exceptii custom
        │   ├── AccountNotFoundException.java
        │   └── InsufficientFundsException.java
        ├── lib/                    - Biblioteci externe (driver JDBC)
        │   └── sqlite-jdbc-3.42.0.0.jar
        ├── model/                  - Modele de domeniu si reguli de business
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
        ├── repository/             - Logica JDBC (CRUD & JOIN)
        │   ├── AccountRepository.java
        │   ├── CardRepository.java
        │   ├── ClientRepository.java
        │   ├── ReportingRepository.java
        │   ├── Repository.java      - Interfata generica Repository
        │   └── TransactionRepository.java
        ├── service/                - Servicii Singleton (Tranzactii, conturi, audit)
        │   ├── AccountService.java
        │   ├── AuditService.java
        │   └── TransactionService.java
        └── util/                   - Utilitare (conexiune DB, SchemaRunner)
            ├── DatabaseConnection.java
            └── SchemaRunner.java
```

---

## 6. Instructiuni de Compilare si Rulare

Pentru a curata baza de date anterioara, a compila codul si a rula scenariul demonstrativ:

### Windows PowerShell:
```powershell
# Curatare compilari anterioare si DB
Remove-Item -Path "src/com/pao/BankingApp/out" -Recurse -ErrorAction SilentlyContinue

# Creare director output
New-Item -ItemType Directory -Path "src/com/pao/BankingApp/out" -Force | Out-Null

# Compilare resurse Java utilizand classpath-ul JDBC
javac -cp "src/com/pao/BankingApp/lib/sqlite-jdbc-3.42.0.0.jar" -d "src/com/pao/BankingApp/out" src/com/pao/BankingApp/*.java src/com/pao/BankingApp/exception/*.java src/com/pao/BankingApp/model/*.java src/com/pao/BankingApp/repository/*.java src/com/pao/BankingApp/service/*.java src/com/pao/BankingApp/util/*.java

# Copiere fisiere proprietati si schema in classpath
Copy-Item -Path "resources/*" -Destination "src/com/pao/BankingApp/out" -Force

# Rulare scenariu demonstrativ
java -cp "src/com/pao/BankingApp/out;src/com/pao/BankingApp/lib/sqlite-jdbc-3.42.0.0.jar" com.pao.BankingApp.Main
```

### Linux / WSL:
Din radacina proiectului `paoj-2026`:
```bash
./src/com/pao/BankingApp/run-wsl.sh
```
