# PAOJ 2026

Repository for the 2026 edition of **Programare Avansată pe Obiecte în Java**.
It contains the laboratory exercises, the individual project brief, and the support code used during the course.

## What is in this repository

- `src/com/pao/laboratory01` to `src/com/pao/laboratory08` - laboratory exercises and explanations.
- `src/com/pao/project/README.md` - requirements for the individual project.
- `src/com/pao/BankingApp` - a complete example application used in the course materials.
- `src/com/pao/test` - helper classes used by some laboratory tests.

## Course structure

| Laboratory | Main topic |
|---|---|
| [laboratory01](src/com/pao/laboratory01/Readme.md) | First program, arrays, `Scanner` |
| [laboratory02](src/com/pao/laboratory02/Readme.md) | Classes, encapsulation, Singleton, `Comparator` |
| [laboratory03](src/com/pao/laboratory03/Readme.md) | Inheritance, abstract classes, interfaces, `equals()` / `hashCode()`, collections |
| [laboratory04](src/com/pao/laboratory04/Readme.md) | `Map`, enums, custom exceptions |
| [laboratory05](src/com/pao/laboratory05/Readme.md) | Records, advanced `Comparable`, multi-criteria sorting |
| [laboratory06](src/com/pao/laboratory06/Readme.md) | Interfaces, callbacks, interface hierarchies |
| [laboratory07](src/com/pao/laboratory07/Readme.md) | Sealed classes and advanced enums |
| [laboratory08](src/com/pao/laboratory08/Readme.md) | Marker interfaces, cloning, and I/O streams |

## Individual project

The project brief is in [src/com/pao/project/README.md](src/com/pao/project/README.md).
It describes the two-stage individual project, deadlines, and submission rules.

## Repository layout

```text
src/
└── com/pao/
    ├── laboratory01..08/   laboratory exercises
    ├── BankingApp/         banking domain example
    ├── project/            individual project brief
    └── test/               shared test utilities
```

## Requirements

- Java Development Kit 17 or newer.
- A Java-capable IDE such as IntelliJ IDEA or VS Code.
- Git if you want to follow the fork-and-push workflow used in the course.

## How to run code

The repository does not use Maven or Gradle. Compile and run Java sources directly.

### Windows PowerShell

```powershell
# Compile everything under src/
$files = Get-ChildItem -Recurse -Filter "*.java" -Path src | ForEach-Object { $_.FullName }
javac -d out @files

# Run a class with a main method
java -cp out com.pao.laboratory01.Main
```

### macOS / Linux / WSL

```bash
javac -d out $(find src -name "*.java" -type f)
java -cp out com.pao.laboratory01.Main
```

If you want to compile only one laboratory, point the compiler to that folder instead of `src/`.

## Submission workflow

From laboratory 04 onward, solutions are typically submitted to a personal GitHub fork of this repository.
The usual workflow is:

1. Fetch the latest branch from the course repository.
2. Create or update your local lab branch.
3. Commit your work regularly.
4. Push to your fork before the deadline.

The project brief contains the exact branch names and deadline information.

## Notes

- Some laboratories include small test runners or helper classes under `src/com/pao/test`.
- The `BankingApp` package is an illustrative domain model used in examples and demonstrations.
- If you are using VS Code, make sure the workspace is opened at the repository root so package paths resolve correctly.

