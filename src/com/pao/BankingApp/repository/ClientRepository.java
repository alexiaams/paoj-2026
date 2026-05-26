package com.pao.BankingApp.repository;

import com.pao.BankingApp.model.Client;
import com.pao.BankingApp.model.ClientType;
import com.pao.BankingApp.model.BankAccount;
import com.pao.BankingApp.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientRepository implements Repository<Client, Long> {
    private final AccountRepository accountRepository = new AccountRepository();

    @Override
    public void save(Client client) {
        // Persist model id into DB so in-memory IDs and DB IDs stay in sync
        String sql = "INSERT INTO clients(id, first_name,last_name,cnp,phone_number,client_code,onboarding_date,kyc_verified,client_type,total_spent) VALUES(?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, client.getId());
            ps.setString(2, client.getFirstName());
            ps.setString(3, client.getLastName());
            ps.setString(4, client.getCNP());
            ps.setString(5, client.getPhoneNumber());
            ps.setString(6, client.getClientCode());
            ps.setString(7, client.getOnboardingDate().toString());
            ps.setInt(8, client.isKycVerified() ? 1 : 0);
            ps.setString(9, client.getClientType().name());
            ps.setDouble(10, client.getTotalSpent());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save client: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Client> findById(Long id) {
        String sql = "SELECT * FROM clients WHERE id = ?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapRowToClient(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find client: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Client> findAll() {
        String sql = "SELECT * FROM clients";
        List<Client> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToClient(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list clients: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Client client) {
        String sql = "UPDATE clients SET first_name=?, last_name=?, phone_number=?, kyc_verified=?, client_type=?, total_spent=? WHERE cnp = ?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, client.getFirstName());
            ps.setString(2, client.getLastName());
            ps.setString(3, client.getPhoneNumber());
            ps.setInt(4, client.isKycVerified() ? 1 : 0);
            ps.setString(5, client.getClientType().name());
            ps.setDouble(6, client.getTotalSpent());
            ps.setString(7, client.getCNP());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update client: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM clients WHERE id = ?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete client: " + e.getMessage(), e);
        }
    }

    private Client mapRowToClient(ResultSet rs) throws SQLException {
        Client client = new Client(
                rs.getLong("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("cnp"),
                rs.getString("phone_number"),
                ClientType.valueOf(rs.getString("client_type")),
                LocalDate.parse(rs.getString("onboarding_date")),
                rs.getInt("kyc_verified") == 1,
                rs.getDouble("total_spent")
        );

        for (BankAccount account : accountRepository.findByClientId(client.getId())) {
            client.attachAccountFromDatabase(account);
        }

        return client;
    }
}
