package com.pao.laboratory06.exercise1;

import com.pao.test.IOTest;

import java.io.File;

public class Test {
    public static void main(String[] args) {
        String testsDir = resolveTestsDir();
        if (testsDir == null) {
            System.out.println("EROARE: directorul de teste nu există.");
            System.out.println("Verifică working directory sau structura proiectului.");
            return;
        }

        IOTest.runParts(testsDir, Main::main);
    }

    private static String resolveTestsDir() {
        String[] candidates = {
                "src/com/pao/laboratory06/exercise1/tests",
                "paoj-2026/src/com/pao/laboratory06/exercise1/tests"
        };

        for (String candidate : candidates) {
            File dir = new File(candidate);
            if (dir.exists() && dir.isDirectory()) {
                return candidate;
            }
        }

        return null;
    }
}
