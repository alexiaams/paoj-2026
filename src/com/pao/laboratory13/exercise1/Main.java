package com.pao.laboratory13.exercise1;

public class Main {
    public static void main(String[] args) {
        try {
            new ProtocolEngine().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ProtocolEngine {
    enum State { INIT, AUTH, OPEN, CLOSED }

    private State state = State.INIT;
    private int historyCount = 0;

    public void run() throws Exception {
        java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
        String line;
        // read first non-empty line as Q
        int Q = 0;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;
            try { Q = Integer.parseInt(line); } catch (NumberFormatException e) { Q = 0; }
            break;
        }
        int processed = 0;
        while (processed < Q && (line = br.readLine()) != null) {
            if (line.trim().isEmpty()) continue; // ignore blank lines
            processLine(line);
            processed++;
        }
    }

    private void processLine(String raw) {
        String line = raw.trim();
        String[] parts = line.split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty()) {
            System.out.println();
            return;
        }
        String cmd = parts[0];

        // Check parse errors first
        // Unknown command
        if (!isKnownCommand(cmd)) {
            System.out.println("ERR E_PARSE UNKNOWN_COMMAND");
            return;
        }

        // Per-command arity checks
        switch (cmd) {
            case "AUTH":
                if (parts.length < 2) { System.out.println("ERR E_PARSE AUTH"); return; }
                break;
            case "OPEN":
                if (parts.length > 1) { System.out.println("ERR E_PARSE OPEN"); return; }
                break;
            case "SEND":
                if (parts.length < 2) { System.out.println("ERR E_PARSE SEND"); return; }
                break;
            case "BROADCAST":
                if (parts.length < 2) { System.out.println("ERR E_PARSE BROADCAST"); return; }
                break;
            case "HISTORY":
                if (parts.length > 1) { System.out.println("ERR E_PARSE HISTORY"); return; }
                break;
            case "CLOSE":
                if (parts.length > 1) { System.out.println("ERR E_PARSE CLOSE"); return; }
                break;
        }

        // Now state checks and execution
        if (state == State.CLOSED) {
            // After passing parse checks, any command in CLOSED is E_STATE CLOSED
            System.out.println("ERR E_STATE CLOSED");
            return;
        }

        switch (cmd) {
            case "AUTH": {
                String user = parts[1];
                // AUTH allowed in any state except CLOSED
                state = State.AUTH;
                historyCount = 0;
                System.out.println("OK AUTH user=" + user);
                break;
            }
            case "OPEN": {
                if (state == State.AUTH) {
                    state = State.OPEN;
                    System.out.println("OK OPEN");
                } else if (state == State.OPEN) {
                    System.out.println("ERR E_STATE ALREADY_OPEN");
                } else { // INIT
                    System.out.println("ERR E_STATE NOT_OPEN");
                }
                break;
            }
            case "SEND": {
                if (state != State.OPEN) { System.out.println("ERR E_STATE NOT_OPEN"); break; }
                historyCount++;
                System.out.println("OK OPEN sent");
                break;
            }
            case "BROADCAST": {
                if (state != State.OPEN) { System.out.println("ERR E_STATE NOT_OPEN"); break; }
                historyCount++;
                System.out.println("OK OPEN broadcast");
                break;
            }
            case "HISTORY": {
                if (state != State.OPEN) { System.out.println("ERR E_STATE NOT_OPEN"); break; }
                System.out.println("OK OPEN history=" + historyCount);
                break;
            }
            case "CLOSE": {
                if (state != State.OPEN) { System.out.println("ERR E_STATE NOT_OPEN"); break; }
                state = State.CLOSED;
                System.out.println("OK CLOSED");
                break;
            }
            default:
                System.out.println("ERR E_PARSE UNKNOWN_COMMAND");
        }
    }

    private boolean isKnownCommand(String cmd) {
        return cmd.equals("AUTH") || cmd.equals("OPEN") || cmd.equals("SEND") || cmd.equals("BROADCAST") || cmd.equals("HISTORY") || cmd.equals("CLOSE");
    }
}
