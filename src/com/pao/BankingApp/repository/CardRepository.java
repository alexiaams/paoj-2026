package com.pao.BankingApp.repository;

import com.pao.BankingApp.model.Card;
import com.pao.BankingApp.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.YearMonth;

public class CardRepository implements Repository<Card, Long> {

    @Override
    public void save(Card card) {
        // Persist model id to keep DB ids in sync with in-memory ids
        String sql = "INSERT INTO cards(id, account_id, card_number, holder_name, expiration_date, cvv, active) VALUES(?,?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, card.getId());
            ps.setLong(2, card.getAccountId());
            ps.setString(3, card.getCardNumber());
            ps.setString(4, card.getHolderName());
            ps.setString(5, card.getExpirationDate().toString());
            ps.setString(6, card.getCvv());
            ps.setInt(7, card.isActive() ? 1 : 0);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save card: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Card> findById(Long id) {
        String sql = "SELECT * FROM cards WHERE id = ?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapRowToCard(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find card: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Card> findAll() {
        String sql = "SELECT * FROM cards";
        List<Card> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToCard(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list cards: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Card card) {
        String sql = "UPDATE cards SET active = ? WHERE card_number = ?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, card.isActive() ? 1 : 0);
            ps.setString(2, card.getCardNumber());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update card: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM cards WHERE id = ?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete card: " + e.getMessage(), e);
        }
    }

    private Card mapRowToCard(ResultSet rs) throws SQLException {
        return new Card(
                rs.getLong("id"),
                rs.getString("holder_name"),
                rs.getLong("account_id"),
                rs.getString("card_number"),
                YearMonth.parse(rs.getString("expiration_date")),
                rs.getString("cvv"),
                rs.getInt("active") != 0
        );
    }
}
