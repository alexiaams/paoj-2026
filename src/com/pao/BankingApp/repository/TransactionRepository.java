package com.pao.BankingApp.repository;

import com.pao.BankingApp.model.Transaction;
import com.pao.BankingApp.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionRepository implements Repository<Transaction, Long> {

    @Override
    public void save(Transaction transaction) {
        // Persist model id and timestamp to keep DB ids/time in sync
        String sql = "INSERT INTO transactions(id, source_account_id,destination_account_id,amount,timestamp,description) VALUES(?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, transaction.getId());
            ps.setLong(2, transaction.getSourceAccountId());
            ps.setLong(3, transaction.getDestinationAccountId());
            ps.setDouble(4, transaction.getAmount());
            ps.setString(5, transaction.getTimestamp().toString());
            ps.setString(6, transaction.getDescription());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save transaction: " + e.getMessage(), e);
        }
    }

    public double sumAmounts() {
        String sql = "SELECT COALESCE(SUM(amount),0) FROM transactions";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0.0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM transactions";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Save using an existing connection (used to participate in an external transaction).
     */
    public void saveWithConnection(Connection conn, Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions(id, source_account_id,destination_account_id,amount,timestamp,description) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, transaction.getId());
            ps.setLong(2, transaction.getSourceAccountId());
            ps.setLong(3, transaction.getDestinationAccountId());
            ps.setDouble(4, transaction.getAmount());
            ps.setString(5, transaction.getTimestamp().toString());
            ps.setString(6, transaction.getDescription());
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        String sql = "SELECT * FROM transactions WHERE id = ?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapRowToTransaction(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find transaction: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Transaction> findAll() {
        String sql = "SELECT * FROM transactions ORDER BY timestamp DESC";
        List<Transaction> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToTransaction(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list transactions: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Transaction transaction) {
        String sql = "UPDATE transactions SET description = ? WHERE id = ?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, transaction.getDescription());
            ps.setLong(2, transaction.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update transaction: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete transaction: " + e.getMessage(), e);
        }
    }

    public List<Transaction> findByAccountId(long accountId) {
        String sql = "SELECT * FROM transactions WHERE source_account_id = ? OR destination_account_id = ? ORDER BY timestamp DESC";
        List<Transaction> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            ps.setLong(2, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToTransaction(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list transactions for account: " + e.getMessage(), e);
        }
    }

    private Transaction mapRowToTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getLong("id"),
                rs.getLong("source_account_id"),
                rs.getLong("destination_account_id"),
                rs.getDouble("amount"),
                LocalDateTime.parse(rs.getString("timestamp")),
                rs.getString("description")
        );
    }
}
