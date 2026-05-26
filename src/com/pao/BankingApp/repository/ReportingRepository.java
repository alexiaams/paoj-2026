package com.pao.BankingApp.repository;

import com.pao.BankingApp.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportingRepository {

    /**
     * JOIN 1 — list clients with account count and total balance
     */
    public List<Map<String, Object>> listClientsWithAccountCountAndTotalBalance() {
        String sql = "SELECT c.id, c.first_name || ' ' || c.last_name AS full_name, c.client_type, "
                + "COUNT(a.id) AS account_count, COALESCE(SUM(a.balance),0) AS total_balance "
                + "FROM clients c LEFT JOIN accounts a ON c.id = a.client_id "
                + "GROUP BY c.id, c.first_name, c.last_name, c.client_type";
        List<Map<String, Object>> out = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getLong("id"));
                row.put("full_name", rs.getString("full_name"));
                row.put("client_type", rs.getString("client_type"));
                row.put("account_count", rs.getInt("account_count"));
                row.put("total_balance", rs.getDouble("total_balance"));
                out.add(row);
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to run client summary JOIN: " + e.getMessage(), e);
        }
    }

    /**
     * JOIN 2 — list transactions with source and destination IBANs
     */
    public List<Map<String, Object>> listTransactionsWithIbans() {
        String sql = "SELECT t.id, t.amount, t.timestamp, t.description, src.iban AS source_iban, dst.iban AS destination_iban "
                + "FROM transactions t JOIN accounts src ON t.source_account_id = src.id "
                + "JOIN accounts dst ON t.destination_account_id = dst.id ORDER BY t.timestamp DESC";
        List<Map<String, Object>> out = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getLong("id"));
                row.put("amount", rs.getDouble("amount"));
                row.put("timestamp", rs.getString("timestamp"));
                row.put("description", rs.getString("description"));
                row.put("source_iban", rs.getString("source_iban"));
                row.put("destination_iban", rs.getString("destination_iban"));
                out.add(row);
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to run transactions JOIN: " + e.getMessage(), e);
        }
    }

    /**
     * JOIN 3 — list cards with account and client details
     */
    public List<Map<String, Object>> listCardsWithAccountAndClientDetails() {
        String sql = "SELECT card.id, card.card_number, card.holder_name, card.active, a.iban, a.balance, a.account_type, c.first_name, c.last_name, c.client_type "
                + "FROM cards card JOIN accounts a ON card.account_id = a.id JOIN clients c ON a.client_id = c.id";
        List<Map<String, Object>> out = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("card_id", rs.getLong("id"));
                row.put("card_number", rs.getString("card_number"));
                row.put("holder_name", rs.getString("holder_name"));
                row.put("active", rs.getInt("active") != 0);
                row.put("iban", rs.getString("iban"));
                row.put("balance", rs.getDouble("balance"));
                row.put("account_type", rs.getString("account_type"));
                row.put("client_first_name", rs.getString("first_name"));
                row.put("client_last_name", rs.getString("last_name"));
                row.put("client_type", rs.getString("client_type"));
                out.add(row);
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to run cards JOIN: " + e.getMessage(), e);
        }
    }
}
