package com.pao.laboratory13.exercise2;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        DemoServer.runDemo();
    }
}

class DemoServer {
    static void runDemo() {
        final int port = 9000;
        final int clients = 2;
        ExecutorService serverPool = Executors.newSingleThreadExecutor();
        ExecutorService clientPool = Executors.newCachedThreadPool();
        CountDownLatch clientsDone = new CountDownLatch(clients);

        serverPool.submit(() -> {
            try (ServerSocket server = new ServerSocket(port)) {
                System.out.println("[SERVER] Listening on port " + port);
                // accept clients asynchronously
                int accepted = 0;
                while (accepted < clients) {
                    Socket s = server.accept();
                    accepted++;
                    int id = accepted;
                    System.out.println("[SERVER] Accepted client " + id + " from " + s.getRemoteSocketAddress());
                    // handle each client in its own thread
                    new Thread(new SessionHandler(s, id, clientsDone)).start();
                }
                // wait for all clients to finish
                clientsDone.await();
                System.out.println("[SERVER] All clients done. Shutting down.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Start client threads (demo clients)
        clientPool.submit(() -> demoClient(1, port, new String[]{
                "AUTH alexia",
                "OPEN",
                "SEND hello",
                "AUTH alex",
                "OPEN",
                "HISTORY",
                "CLOSE"
        }));
        clientPool.submit(() -> demoClient(2, port, new String[]{
                "AUTH maria",
                "OPEN",
                "BROADCAST ping",
                "HISTORY",
                "CLOSE"
        }));

        clientPool.shutdown();
        try {
            clientPool.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        serverPool.shutdown();
    }

    private static void demoClient(int id, int port, String[] cmds) {
        try (Socket socket = new Socket("127.0.0.1", port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            System.out.println("[CLIENT-" + id + "] Connected");
            for (String c : cmds) {
                out.println(c);
                String resp = in.readLine();
                System.out.printf("[CLIENT-%d] >> %s  =>  %s%n", id, c, resp);
                // small pause to mix concurrency
                try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        } catch (IOException e) {
            System.out.println("[CLIENT-" + id + "] Error: " + e.getMessage());
        }
    }
}

class SessionHandler implements Runnable {
    private final Socket socket;
    private final int id;
    private final CountDownLatch doneLatch;

    enum State { INIT, AUTH, OPEN, CLOSED }

    SessionHandler(Socket s, int id, CountDownLatch doneLatch) {
        this.socket = s;
        this.id = id;
        this.doneLatch = doneLatch;
    }

    @Override
    public void run() {
        try (Socket s = this.socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
             PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {

            State state = State.INIT;
            int history = 0;
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\s+");
                String cmd = parts[0];
                String resp;

                // parse errors first
                if (!isKnown(cmd)) {
                    resp = "ERR E_PARSE UNKNOWN_COMMAND";
                    out.println(resp);
                    log(line, resp);
                    continue;
                }
                switch (cmd) {
                    case "AUTH":
                        if (parts.length < 2) { resp = "ERR E_PARSE AUTH"; out.println(resp); log(line, resp); continue; }
                        break;
                    case "OPEN":
                        if (parts.length > 1) { resp = "ERR E_PARSE OPEN"; out.println(resp); log(line, resp); continue; }
                        break;
                    case "SEND":
                        if (parts.length < 2) { resp = "ERR E_PARSE SEND"; out.println(resp); log(line, resp); continue; }
                        break;
                    case "BROADCAST":
                        if (parts.length < 2) { resp = "ERR E_PARSE BROADCAST"; out.println(resp); log(line, resp); continue; }
                        break;
                    case "HISTORY":
                        if (parts.length > 1) { resp = "ERR E_PARSE HISTORY"; out.println(resp); log(line, resp); continue; }
                        break;
                    case "CLOSE":
                        if (parts.length > 1) { resp = "ERR E_PARSE CLOSE"; out.println(resp); log(line, resp); continue; }
                        break;
                }

                if (state == State.CLOSED) {
                    resp = "ERR E_STATE CLOSED";
                    out.println(resp);
                    log(line, resp);
                    continue;
                }

                switch (cmd) {
                    case "AUTH": {
                        String user = parts[1];
                        state = State.AUTH;
                        history = 0;
                        resp = "OK AUTH user=" + user;
                        out.println(resp);
                        log(line, resp);
                        break;
                    }
                    case "OPEN": {
                        if (state == State.AUTH) {
                            state = State.OPEN;
                            resp = "OK OPEN";
                        } else if (state == State.OPEN) {
                            resp = "ERR E_STATE ALREADY_OPEN";
                        } else {
                            resp = "ERR E_STATE NOT_OPEN";
                        }
                        out.println(resp);
                        log(line, resp);
                        break;
                    }
                    case "SEND": {
                        if (state != State.OPEN) { resp = "ERR E_STATE NOT_OPEN"; out.println(resp); log(line, resp); break; }
                        history++;
                        resp = "OK OPEN sent";
                        out.println(resp);
                        log(line, resp);
                        break;
                    }
                    case "BROADCAST": {
                        if (state != State.OPEN) { resp = "ERR E_STATE NOT_OPEN"; out.println(resp); log(line, resp); break; }
                        history++;
                        resp = "OK OPEN broadcast";
                        out.println(resp);
                        log(line, resp);
                        break;
                    }
                    case "HISTORY": {
                        if (state != State.OPEN) { resp = "ERR E_STATE NOT_OPEN"; out.println(resp); log(line, resp); break; }
                        resp = "OK OPEN history=" + history;
                        out.println(resp);
                        log(line, resp);
                        break;
                    }
                    case "CLOSE": {
                        if (state != State.OPEN) { resp = "ERR E_STATE NOT_OPEN"; out.println(resp); log(line, resp); break; }
                        state = State.CLOSED;
                        resp = "OK CLOSED";
                        out.println(resp);
                        log(line, resp);
                        // after close, end loop
                        break;
                    }
                }

                if ("CLOSE".equals(cmd)) break;
            }
        } catch (IOException e) {
            System.out.println("[SERVER] Client-" + id + " IO error: " + e.getMessage());
        } finally {
            System.out.println("[SERVER] Client-" + id + " disconnected");
            doneLatch.countDown();
        }
    }

    private boolean isKnown(String c) {
        return c.equals("AUTH") || c.equals("OPEN") || c.equals("SEND") || c.equals("BROADCAST") || c.equals("HISTORY") || c.equals("CLOSE");
    }

    private void log(String cmdLine, String resp) {
        System.out.printf("[SERVER][CLIENT-%d] >> %s  =>  %s%n", id, cmdLine, resp);
    }
}
