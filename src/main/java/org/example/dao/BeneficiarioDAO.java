package org.example.dao;

import org.example.db.Database;
import org.example.model.Beneficiario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BeneficiarioDAO {
    public Beneficiario inserir(Beneficiario b) throws SQLException {
        String sql = "INSERT INTO beneficiarios (nome, necessidade, ativo) VALUES (?, ?, TRUE)";
        try (Connection c = Database.getConexao();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, b.getNome());
            ps.setString(2, b.getNecessidade());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) b.setId(rs.getInt(1));
            }
            // fetch data_cadastro
            return buscarPorId(b.getId()).orElse(b);
        }
    }

    public Optional<Beneficiario> buscarPorId(int id) throws SQLException {
    String sql = "SELECT id, nome, necessidade, data_cadastro FROM beneficiarios WHERE id = ? AND ativo=TRUE";
        try (Connection c = Database.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Beneficiario> listarTodosPorOrdemCadastro() throws SQLException {
        List<Beneficiario> lista = new ArrayList<>();
    String sql = "SELECT id, nome, necessidade, data_cadastro FROM beneficiarios WHERE ativo=TRUE ORDER BY data_cadastro, id";
        try (Connection c = Database.getConexao();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public void atualizar(Beneficiario b) throws SQLException {
        String sql = "UPDATE beneficiarios SET nome=?, necessidade=? WHERE id=?";
        try (Connection c = Database.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, b.getNome());
            ps.setString(2, b.getNecessidade());
            ps.setInt(3, b.getId());
            ps.executeUpdate();
        }
    }

    public void remover(int id) throws SQLException {
    String sql = "UPDATE beneficiarios SET ativo=FALSE WHERE id=?";
        try (Connection c = Database.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Beneficiario mapear(ResultSet rs) throws SQLException {
        return new Beneficiario(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("necessidade"),
                rs.getTimestamp("data_cadastro").toLocalDateTime()
        );
    }
}
