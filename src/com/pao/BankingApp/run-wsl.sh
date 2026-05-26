#!/usr/bin/env bash
set -euo pipefail

# Rulează din folderul BankingApp:
# /mnt/c/Users/alexi/Desktop/PAOJ/paoj-2026/src/com/pao/BankingApp

rm -rf out paoj_proiect.db audit.csv
mkdir -p out

javac -cp "lib/sqlite-jdbc-3.42.0.0.jar" -d out $(find . -name "*.java" -not -path "./out/*")

cp ../../../../resources/* out/

java -cp "out:lib/sqlite-jdbc-3.42.0.0.jar" com.pao.BankingApp.Main