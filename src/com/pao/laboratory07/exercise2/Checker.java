package com.pao.laboratory07.exercise2;

import com.pao.test.IOTest;

import java.io.File;

public class Checker {
    private static final String TESTS_DIR = "paoj-2026/src/com/pao/laboratory07/exercise2/tests";

    public static void main(String[] args) {
        IOTest.runFlat(resolveTestsDir(), Main::main);
    }

    private static String resolveTestsDir() {
        if (new File(TESTS_DIR).isDirectory()) {
            return TESTS_DIR;
        }

        return TESTS_DIR;
    }
}

