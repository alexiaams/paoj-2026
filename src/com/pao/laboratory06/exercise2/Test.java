package com.pao.laboratory06.exercise2;

import com.pao.test.IOTest;

import java.nio.file.Files;
import java.nio.file.Path;

public class Test {
    public static void main(String[] args) {
        String testsPath = resolveTestsPath();
        IOTest.runParts(testsPath, Main::main);
    }

    private static String resolveTestsPath() {
        String pathFromProjectRoot = "src/com/pao/laboratory06/exercise2/tests";
        if (Files.exists(Path.of(pathFromProjectRoot))) {
            return pathFromProjectRoot;
        }

        String pathFromWorkspaceRoot = "paoj-2026/src/com/pao/laboratory06/exercise2/tests";
        if (Files.exists(Path.of(pathFromWorkspaceRoot))) {
            return pathFromWorkspaceRoot;
        }

        return pathFromProjectRoot;
    }
}
