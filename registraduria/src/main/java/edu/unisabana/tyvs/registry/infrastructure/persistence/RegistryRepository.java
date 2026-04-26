package edu.unisabana.tyvs.registry.infrastructure.persistence;

import edu.unisabana.tyvs.registry.application.port.out.RegistryRepositoryPort;
import java.sql.*;

/** Adaptador de salida: implementa el puerto usando JDBC (compatible con H2 y BD reales). */
public class RegistryRepository implements RegistryRepositoryPort {

    private final String jdbcUrl;

    public RegistryRepository(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, "sa", "");
    }

    @Override
    public void initSchema() throws Exception {
        try (Connection c = connect(); Statement s = c.createStatement()) {
            s.execute(
                "CREATE TABLE IF NOT EXISTS voters (" +
                "  id    INT PRIMARY KEY," +
                "  name  VARCHAR(100)," +
                "  age   INT," +
                "  alive BOOLEAN)"
            );
        }
    }

    @Override
    public void deleteAll() throws Exception {
        try (Connection c = connect(); Statement s = c.createStatement()) {
            s.execute("DELETE FROM voters");
        }
    }

    @Override
    public boolean existsById(int id) throws Exception {
        String sql = "SELECT COUNT(*) FROM voters WHERE id = ?";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    @Override
    public void save(int id, String name, int age, boolean alive) throws Exception {
        String sql = "INSERT INTO voters (id, name, age, alive) VALUES (?, ?, ?, ?)";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id); ps.setString(2, name);
            ps.setInt(3, age); ps.setBoolean(4, alive);
            ps.executeUpdate();
        }
    }
}