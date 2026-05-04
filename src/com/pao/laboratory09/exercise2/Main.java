package com.pao.laboratory09.exercise2;

import com.pao.laboratory09.exercise1.TipTranzactie;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public class Main {
    private static final String OUTPUT_FILE = "output/lab09_ex2.bin";
    private static final int RECORD_SIZE = 32;

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        
        // 1. Citește N și apoi N tranzacții
        int n = scanner.nextInt();
        scanner.nextLine();

        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int id = scanner.nextInt();
            double suma = scanner.nextDouble();
            String data = scanner.next();
            TipTranzactie tip = TipTranzactie.valueOf(scanner.next());
            scanner.nextLine();
            
            transactions.add(new Transaction(id, suma, data, tip));
        }

        // 2. Scrie înregistrările în OUTPUT_FILE
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(OUTPUT_FILE))) {
            for (Transaction t : transactions) {
                writeTransaction(dos, t);
            }
        }

        // 3. Procesează comenzile cu RandomAccessFile
        try (RandomAccessFile raf = new RandomAccessFile(OUTPUT_FILE, "rw")) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");
                String command = parts[0];

                if (command.equals("READ")) {
                    int idx = Integer.parseInt(parts[1]);
                    Transaction t = readTransaction(raf, idx);
                    if (t != null) {
                        System.out.println(formatTransaction(idx, t));
                    }
                } else if (command.equals("UPDATE")) {
                    int idx = Integer.parseInt(parts[1]);
                    String statusStr = parts[2];
                    Status status = Status.fromString(statusStr);
                    updateStatus(raf, idx, status);
                    System.out.println("Updated [" + idx + "]: " + statusStr);
                } else if (command.equals("PRINT_ALL")) {
                    int count = (int) (raf.length() / RECORD_SIZE);
                    for (int i = 0; i < count; i++) {
                        Transaction t = readTransaction(raf, i);
                        if (t != null) {
                            System.out.println(formatTransaction(i, t));
                        }
                    }
                }
            }
        }

        scanner.close();
    }

    private static void writeTransaction(DataOutputStream dos, Transaction t) throws IOException {
        // Write id (4 bytes, little-endian)
        byte[] id = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(t.id).array();
        dos.write(id);

        // Write suma (8 bytes, little-endian)
        byte[] suma = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(t.suma).array();
        dos.write(suma);

        // Write data (10 bytes, padded with spaces)
        byte[] dataBytes = t.data.getBytes();
        dos.write(dataBytes);
        for (int i = dataBytes.length; i < 10; i++) {
            dos.write(' ');
        }

        // Write tip (1 byte)
        int tipValue = t.tip == TipTranzactie.CREDIT ? 0 : 1;
        dos.write(tipValue);

        // Write status (1 byte) - initially PENDING
        dos.write(Status.PENDING.getValue());

        // Write padding (8 bytes)
        for (int i = 0; i < 8; i++) {
            dos.write(0);
        }
    }

    private static Transaction readTransaction(RandomAccessFile raf, int idx) throws IOException {
        long offset = (long) idx * RECORD_SIZE;
        if (offset < 0 || offset >= raf.length()) {
            return null;
        }

        raf.seek(offset);
        byte[] record = new byte[RECORD_SIZE];
        raf.readFully(record);

        ByteBuffer bb = ByteBuffer.wrap(record).order(ByteOrder.LITTLE_ENDIAN);

        int id = bb.getInt();
        double suma = bb.getDouble();
        
        byte[] dataBytes = new byte[10];
        bb.get(dataBytes);
        String data = new String(dataBytes).trim();
        
        int tipValue = bb.get() & 0xFF;
        TipTranzactie tip = tipValue == 0 ? TipTranzactie.CREDIT : TipTranzactie.DEBIT;
        
        int statusValue = bb.get() & 0xFF;
        Status status = Status.fromValue(statusValue);

        Transaction t = new Transaction(id, suma, data, tip);
        t.status = status;
        return t;
    }

    private static void updateStatus(RandomAccessFile raf, int idx, Status status) throws IOException {
        long offset = (long) idx * RECORD_SIZE + 23;
        raf.seek(offset);
        raf.write(status.getValue());
    }

    private static String formatTransaction(int idx, Transaction t) {
        return String.format("[%d] id=%d data=%s tip=%s suma=%.2f RON status=%s",
                idx, t.id, t.data, t.tip, t.suma, t.status);
    }

    static class Transaction {
        int id;
        double suma;
        String data;
        TipTranzactie tip;
        Status status = Status.PENDING;

        Transaction(int id, double suma, String data, TipTranzactie tip) {
            this.id = id;
            this.suma = suma;
            this.data = data;
            this.tip = tip;
        }
    }
}
