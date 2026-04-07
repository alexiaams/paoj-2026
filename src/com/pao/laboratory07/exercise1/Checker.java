package com.pao.laboratory07.exercise1;

import com.pao.test.IOTest;

import java.io.File;
import java.io.IOException;

public class Checker {
    private static final String TESTS_DIR = "paoj-2026/src/com/pao/laboratory07/exercise1/tests";

    public static void main(String[] args) {
        IOTest.runParts(resolveTestsDir(), Main::main);

        // Sau ruleaza doar testele pentru o parte specifica:
//        IOTest.runPart(resolveTestsDir(), "partA", Main::main);
//        IOTest.runPart(resolveTestsDir(), "partB", Main::main);
//        IOTest.runPart(resolveTestsDir(), "partC", Main::main);
    }

    private static String resolveTestsDir() {
        if (new File(TESTS_DIR).isDirectory()) {
            return TESTS_DIR;
        }

        return TESTS_DIR;
    }
}
