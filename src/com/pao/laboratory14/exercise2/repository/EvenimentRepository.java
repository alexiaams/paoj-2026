package com.pao.laboratory14.exercise2.repository;

import com.pao.laboratory14.exercise2.model.Eveniment;
import com.pao.laboratory14.exercise2.util.DatabaseConnection;
import com.pao.laboratory14.exercise1.TipBilet;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EvenimentRepository implements Repository<Eveniment, Integer> {

	public EvenimentRepository() {
	}

	public void initSchema() throws SQLException, IOException {
		Connection conn = DatabaseConnection.getInstance().getConnection();
		try (Statement st = conn.createStatement()) {
			st.executeUpdate("DROP TABLE IF EXISTS evenimente");
			st.executeUpdate("CREATE TABLE IF NOT EXISTS evenimente (" +
					"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
					"nume TEXT NOT NULL, " +
					"data TEXT NOT NULL, " +
					"capacitate INTEGER, " +
					"tip TEXT)");
		}
	}

	@Override
	public void save(Eveniment entity) throws SQLException {
		String sql = "INSERT INTO evenimente(nume,data,capacitate,tip) VALUES(?,?,?,?)";
		Connection conn;
		try {
			conn = DatabaseConnection.getInstance().getConnection();
		} catch (IOException e) {
			throw new SQLException(e);
		}
		try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, entity.getNume());
			ps.setString(2, entity.getData());
			ps.setInt(3, entity.getCapacitate());
			ps.setString(4, entity.getTip().name());
			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					entity.setId(rs.getInt(1));
				}
			}
		}
	}

	@Override
	public Optional<Eveniment> findById(Integer id) throws SQLException {
		String sql = "SELECT * FROM evenimente WHERE id = ?";
		Connection conn;
		try {
			conn = DatabaseConnection.getInstance().getConnection();
		} catch (IOException e) {
			throw new SQLException(e);
		}
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Eveniment e = mapRow(rs);
					return Optional.of(e);
				}
			}
		}
		return Optional.empty();
	}

	@Override
	public List<Eveniment> findAll() throws SQLException {
		String sql = "SELECT * FROM evenimente ORDER BY id";
		List<Eveniment> list = new ArrayList<>();
		Connection conn;
		try {
			conn = DatabaseConnection.getInstance().getConnection();
		} catch (IOException e) {
			throw new SQLException(e);
		}
		try (PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				list.add(mapRow(rs));
			}
		}
		return list;
	}

	@Override
	public void update(Eveniment entity) throws SQLException {
		String sql = "UPDATE evenimente SET nume = ?, data = ?, capacitate = ?, tip = ? WHERE id = ?";
		Connection conn;
		try {
			conn = DatabaseConnection.getInstance().getConnection();
		} catch (IOException e) {
			throw new SQLException(e);
		}
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, entity.getNume());
			ps.setString(2, entity.getData());
			ps.setInt(3, entity.getCapacitate());
			ps.setString(4, entity.getTip().name());
			ps.setInt(5, entity.getId());
			ps.executeUpdate();
		}
	}

	@Override
	public void delete(Integer id) throws SQLException {
		String sql = "DELETE FROM evenimente WHERE id = ?";
		Connection conn;
		try {
			conn = DatabaseConnection.getInstance().getConnection();
		} catch (IOException e) {
			throw new SQLException(e);
		}
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			ps.executeUpdate();
		}
	}

	public int deleteImpl(int id) throws SQLException {
		String sql = "DELETE FROM evenimente WHERE id = ?";
		Connection conn;
		try {
			conn = DatabaseConnection.getInstance().getConnection();
		} catch (IOException e) {
			throw new SQLException(e);
		}
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			return ps.executeUpdate();
		}
	}

	public long count() throws SQLException {
		String sql = "SELECT COUNT(*) FROM evenimente";
		Connection conn;
		try {
			conn = DatabaseConnection.getInstance().getConnection();
		} catch (IOException e) {
			throw new SQLException(e);
		}
		try (PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			if (rs.next()) return rs.getLong(1);
			return 0L;
		}
	}

	private Eveniment mapRow(ResultSet rs) throws SQLException {
		try {
			int id = rs.getInt("id");
			String nume = rs.getString("nume");
			String data = rs.getString("data");
			int cap = rs.getInt("capacitate");
			String tipStr = rs.getString("tip");
			TipBilet tip = tipStr == null ? null : TipBilet.valueOf(tipStr);
			return new Eveniment(id, nume, data, cap, tip);
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
}

