package com.pao.BankingApp.repository;

import com.pao.BankingApp.model.BankAccount;
import com.pao.BankingApp.model.CheckingAccount;
import com.pao.BankingApp.model.Client;
import com.pao.BankingApp.model.Iban;
import com.pao.BankingApp.model.SavingsAccount;
import com.pao.BankingApp.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountRepository implements Repository<BankAccount, Long> {

    public Optional<BankAccount> findByIban(String iban) {
        String sql = "SELECT * FROM accounts WHERE iban = ?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, iban);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapRowToAccount(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find account by IBAN: " + e.getMessage(), e);
        }
    }

    @Override
    public void save(BankAccount account) {
        Long clientId = Client.findOwnerByAccountId(account.getId())
                .map(Client::getId)
                .orElse(null);
        save(account, clientId);
    }

    public void save(BankAccount account, Long clientId) {
        // Insert the model id explicitly to keep DB ids in sync with in-memory ids
        String sql = "INSERT INTO accounts(id, client_id, iban, balance, account_type, interest_rate) VALUES(?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, account.getId());
            if (clientId == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setLong(2, clientId);
            }
            ps.setString(3, account.getIban().getValue());
            ps.setDouble(4, account.getBalance());
            if (account instanceof SavingsAccount) {
                ps.setString(5, "SAVINGS");
                ps.setDouble(6, ((SavingsAccount) account).getInterestRate());
            } else {
                ps.setString(5, "CHECKING");
                ps.setNull(6, Types.REAL);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save account: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<BankAccount> findById(Long id) {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapRowToAccount(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find account: " + e.getMessage(), e);
        }
    }

    @Override
    public List<BankAccount> findAll() {
        String sql = "SELECT * FROM accounts";
        List<BankAccount> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToAccount(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list accounts: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(BankAccount account) {
        String sql = "UPDATE accounts SET balance = ?, interest_rate = ? WHERE iban = ?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, account.getBalance());
            if (account instanceof SavingsAccount) {
                ps.setDouble(2, ((SavingsAccount) account).getInterestRate());
            } else {
                ps.setNull(2, Types.REAL);
            }
            ps.setString(3, account.getIban().getValue());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update account: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM accounts WHERE id = ?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete account: " + e.getMessage(), e);
        }
    }

    public void updateClientOwner(long accountId, long clientId) {
        String sql = "UPDATE accounts SET client_id = ? WHERE id = ?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, clientId);
            ps.setLong(2, accountId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update account owner: " + e.getMessage(), e);
        }
    }

    public List<BankAccount> findByClientId(long clientId) {
        String sql = "SELECT * FROM accounts WHERE client_id = ? ORDER BY id";
        List<BankAccount> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToAccount(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list accounts for client: " + e.getMessage(), e);
        }
    }

    private BankAccount mapRowToAccount(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String type = rs.getString("account_type");
        Iban iban = new Iban(rs.getString("iban"));
        double balance = rs.getDouble("balance");
        if ("SAVINGS".equalsIgnoreCase(type)) {
            double rate = rs.getDouble("interest_rate");
            return new SavingsAccount(id, iban, balance, rate);
        }
        return new CheckingAccount(id, iban, balance);
    }
}
